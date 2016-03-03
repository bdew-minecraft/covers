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
import mcmultipart.multipart.{IMultipart, MultipartHelper, PartSlot}
import net.bdew.covers.microblock._
import net.bdew.covers.microblock.shape.FaceShape
import net.bdew.lib.Misc
import net.bdew.lib.items.BaseItem
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.{BlockPos, EnumFacing, Vec3}
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader

object ItemMicroblock extends BaseItem("Part") with IItemMultipartFactory {
  setHasSubtypes(true)

  def getData(stack: ItemStack) =
    if (stack.hasTagCompound) MicroblockData.fromNBT(stack.getTagCompound) else None

  def getMaterial(stack: ItemStack) = getData(stack).getOrElse(sys.error("Missing part data")).material
  def getShape(stack: ItemStack) = getData(stack).getOrElse(sys.error("Missing part data")).shape
  def getSize(stack: ItemStack) = getData(stack).getOrElse(sys.error("Missing part data")).size

  def makeStack(material: MicroblockMaterial, shape: MicroblockShape, partSize: Int, stackSize: Int = 1) = {
    val stack = new ItemStack(this, stackSize, 0)
    stack.setTagCompound(MicroblockData(shape, material, partSize, PartSlot.NORTH).toNBT)
    stack
  }

  override def getItemStackDisplayName(stack: ItemStack): String =
    getData(stack) map (data => Misc.toLocalF("bdew.covers." + data.shape.name + "." + data.size, data.material.displayName)) getOrElse super.getItemStackDisplayName(stack)

  override def getSubItems(item: Item, tab: CreativeTabs, list: util.List[ItemStack]): Unit =
    for (material <- MicroblockRegistry.materials.values; shape <- MicroblockRegistry.shapes.values; size <- shape.validSizes)
      list.add(makeStack(material, FaceShape, size))

  def createPart(world: World, pos: BlockPos, side: EnumFacing, hit: Vec3, stack: ItemStack, player: EntityPlayer): IMultipart =
    new PartMicroblock(getData(stack).getOrElse(sys.error("Creating part from invalid stack")))

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (player.canPlayerEdit(pos, side, stack)) {
      for (data <- getData(stack); place <- MicroblockPlacement.calculate(world, pos, new Vec3(hitX, hitY, hitZ), side, data)) {
        if (!world.isRemote) MultipartHelper.addPart(world, place.pos, place.part)
        stack.stackSize -= 1
        val sound = place.part.data.material.sound
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
