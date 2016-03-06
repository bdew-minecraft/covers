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

package net.bdew.covers.microblock

import java.util

import com.google.common.collect.ImmutableList
import mcmultipart.MCMultiPartMod
import mcmultipart.multipart.{ISlottedPart, ISolidPart, Multipart, PartSlot}
import mcmultipart.raytrace.PartMOP
import net.bdew.covers.items.ItemMicroblock
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.{AxisAlignedBB, EnumFacing, EnumWorldBlockLayer}
import net.minecraftforge.common.property.{ExtendedBlockState, IExtendedBlockState}

class PartMicroblock(var data: MicroblockData) extends Multipart with ISlottedPart with ISolidPart {
  def this() = this(null)

  override def writeToNBT(tag: NBTTagCompound): Unit =
    if (data == null) sys.error("Attempting to save uninitialized microblock") else data.writeNBT(tag)

  override def readFromNBT(tag: NBTTagCompound): Unit =
    MicroblockData.fromNBT(tag) foreach (data = _)

  override def writeUpdatePacket(buf: PacketBuffer): Unit =
    if (data == null) sys.error("Attempting to send uninitialized microblock") else data.writePacket(buf)

  override def readUpdatePacket(buf: PacketBuffer): Unit =
    data = MicroblockData.readPacket(buf)

  override def createBlockState(): BlockState =
    new ExtendedBlockState(MCMultiPartMod.multipart, Array.empty, Array(MicroblockData.Property))

  override def getExtendedState(state: IBlockState): IBlockState =
    super.getExtendedState(state).asInstanceOf[IExtendedBlockState].withProperty(MicroblockData.Property, data)

  override def getSlotMask: util.EnumSet[PartSlot] = util.EnumSet.of(data.slot)

  def getBoundingBox = data.shape.getBoundingBox(data.slot, data.size)

  override def getRenderBoundingBox: AxisAlignedBB = getBoundingBox

  override def addCollisionBoxes(mask: AxisAlignedBB, list: util.List[AxisAlignedBB], collidingEntity: Entity): Unit = {
    val bb = getBoundingBox
    if (mask.intersectsWith(bb))
      list.add(bb)
  }

  override def addSelectionBoxes(list: util.List[AxisAlignedBB]): Unit = {
    list.add(getBoundingBox)
  }

  override def getPickBlock(player: EntityPlayer, hit: PartMOP): ItemStack =
    ItemMicroblock.makeStack(data.material, data.shape, data.size)

  override def getDrops: util.List[ItemStack] =
    ImmutableList.of(ItemMicroblock.makeStack(data.material, data.shape, data.size))

  override def getLightValue: Int =
    data.material.block.getLightValue

  override def canRenderInLayer(layer: EnumWorldBlockLayer): Boolean =
    data.material.block.canRenderInLayer(layer)

  override def getHardness(hit: PartMOP): Float = 0.5f

  override def isSideSolid(side: EnumFacing): Boolean =
    data.shape.isSolid(data.slot, data.size, side)

  override def getModelPath: String = "covers:microblock"
}

