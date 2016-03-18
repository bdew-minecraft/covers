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

package net.bdew.covers.items

import java.util

import mcmultipart.item.IItemMultipartFactory
import mcmultipart.microblock.{IMicroMaterial, MicroblockRegistry}
import mcmultipart.multipart.{IMultipart, MultipartHelper}
import net.bdew.covers.microblock._
import net.bdew.covers.microblock.shape.MicroblockShape
import net.bdew.covers.microblock.transition.OldPartConverter
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.items.BaseItem
import net.bdew.lib.nbt.NBT
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.{BlockPos, EnumFacing, Vec3}
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
object ItemMicroblock extends BaseItem("Part") with IItemMultipartFactory {
  setHasSubtypes(true)

  case class Data(shape: MicroblockShape, material: IMicroMaterial, size: Int)

  def getData(stack: ItemStack) = {
    if (stack.hasTagCompound) OldPartConverter.convertItemData(stack.getTagCompound)
    for {
      tag <- Option(stack.getTagCompound) if stack.hasTagCompound
      shapeId <- tag.get[String]("shape")
      materialId <- tag.get[String]("material")
      size <- tag.get[Int]("size")
      shape <- InternalRegistry.shapes.get(shapeId) if shape.validSizes.contains(size)
      material <- Option(MicroblockRegistry.getMaterial(materialId))
    } yield {
      Data(shape, material, size)
    }
  }

  def getMaterial(stack: ItemStack) = getData(stack).getOrElse(sys.error("Missing part data")).material
  def getShape(stack: ItemStack) = getData(stack).getOrElse(sys.error("Missing part data")).shape
  def getSize(stack: ItemStack) = getData(stack).getOrElse(sys.error("Missing part data")).size

  def makeStack(material: IMicroMaterial, shape: MicroblockShape, partSize: Int, stackSize: Int = 1) = {
    val stack = new ItemStack(this, stackSize, 0)
    stack.setTagCompound(NBT("shape" -> shape.name, "material" -> material.getName, "size" -> partSize, "v" -> 2))
    stack
  }

  override def getItemStackDisplayName(stack: ItemStack): String =
    getData(stack) map (data => data.shape.getLocalizedName(data.material, data.size)) getOrElse super.getItemStackDisplayName(stack)

  override def getSubItems(item: Item, tab: CreativeTabs, list: util.List[ItemStack]): Unit =
    for (material <- InternalRegistry.materials.values; shape <- InternalRegistry.shapes.values; size <- shape.validSizes)
      list.add(makeStack(material, shape, size))

  def createPart(world: World, pos: BlockPos, side: EnumFacing, hit: Vec3, stack: ItemStack, player: EntityPlayer): IMultipart =
    getData(stack) map (data => data.shape.createPart(data.shape.defaultSlot, data.size, data.material, world.isRemote)) getOrElse sys.error("Creating part from invalid stack")

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (player.canPlayerEdit(pos, side, stack)) {
      for (data <- getData(stack); place <- MicroblockLocation.calculate(world, pos, new Vec3(hitX, hitY, hitZ), side, data.shape, data.size, data.material, world.isRemote)) {
        if (!world.isRemote) MultipartHelper.addPart(world, place.pos, place.part)
        stack.stackSize -= 1
        val sound = place.part.getMicroMaterial.getSound
        if (sound != null)
          world.playSoundEffect(place.pos.getX + 0.5, place.pos.getY + 0.5, place.pos.getZ + 0.5, sound.getPlaceSound, sound.getVolume, sound.getFrequency)
        return true
      }
    }
    false
  }

  override def canItemEditBlocks = true

  override def registerItemModels(): Unit = {
    ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("covers:microblock", "inventory"))
  }
}
