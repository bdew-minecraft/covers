/*
 * Copyright (c) bdew 2016.
 *
 * This file is part of Simple Covers.
 *
 * Simple Covers is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Simple Covers is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Simple Covers.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.bdew.covers.block

import mcmultipart.MCMultiPart
import mcmultipart.api.microblock.MicroMaterial
import mcmultipart.api.multipart.MultipartHelper
import net.bdew.covers.microblock._
import net.bdew.covers.microblock.shape.MicroblockShape
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.items.BaseItemBlock
import net.bdew.lib.nbt.NBT
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util._
import net.minecraft.util.math.{BlockPos, Vec3d}
import net.minecraft.world.World

object ItemCover extends BaseItemBlock("cover", BlockCover) {
  setHasSubtypes(true)

  case class Data(shape: MicroblockShape, material: MicroMaterial, size: Int)

  def getData(stack: ItemStack) = {
    for {
      tag <- Option(stack.getTagCompound) if stack.hasTagCompound
      shapeId <- tag.get[String]("shape")
      materialId <- tag.get[String]("material")
      size <- tag.get[Int]("size")
      shape <- InternalRegistry.shapes.get(shapeId) if shape.validSizes.contains(size)
      material <- Option(MCMultiPart.microMaterialRegistry.getObject(new ResourceLocation(materialId)))
    } yield {
      Data(shape, material, size)
    }
  }

  def getMaterial(stack: ItemStack) = getData(stack).getOrElse(sys.error("Missing part data")).material
  def getShape(stack: ItemStack) = getData(stack).getOrElse(sys.error("Missing part data")).shape
  def getSize(stack: ItemStack) = getData(stack).getOrElse(sys.error("Missing part data")).size

  def makeStack(material: MicroMaterial, shape: MicroblockShape, partSize: Int, stackSize: Int = 1) = {
    val stack = new ItemStack(this, stackSize, 0)
    stack.setTagCompound(NBT("shape" -> shape.name, "material" -> material.getRegistryName.toString, "size" -> partSize))
    stack
  }

  override def getItemStackDisplayName(stack: ItemStack): String =
    getData(stack) map (data => data.shape.getLocalizedName(data.material, data.size)) getOrElse super.getItemStackDisplayName(stack)

  override def getSubItems(itemIn: Item, tab: CreativeTabs, list: NonNullList[ItemStack]): Unit =
    for (material <- InternalRegistry.materials.values; shape <- InternalRegistry.shapes.values; size <- shape.validSizes)
      list.add(makeStack(material, shape, size))

  override def onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    val stack = player.getHeldItem(hand)
    if (player.canPlayerEdit(pos, facing, stack) && !stack.isEmpty && stack.getItem == this) {
      for (data <- getData(stack); place <- MicroblockLocation.calculate(world, pos, new Vec3d(hitX, hitY, hitZ), facing, data.shape, data.size, data.material, world.isRemote)) {
        if (!world.isRemote) {
          if (place.intoContainer)
            MultipartHelper.addPart(world, place.pos, place.slot, place.state, false)
          else
            world.setBlockState(place.pos, place.state, 3)
        }
        stack.shrink(1)
        val sound = data.material.getSound(data.material.getDefaultState, world, place.pos, player)
        if (sound != null)
          world.playSound(player, pos, sound.getPlaceSound, SoundCategory.BLOCKS, sound.getVolume, sound.getPitch)
        return EnumActionResult.SUCCESS
      }
    }
    EnumActionResult.PASS
  }

  override def canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing, player: EntityPlayer, stack: ItemStack): Boolean = true

  override def canItemEditBlocks = true
}
