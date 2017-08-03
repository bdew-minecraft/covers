/*
 * Copyright (c) bdew, 2016 - 2017
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
import net.bdew.covers.microblock.InternalRegistry
import net.bdew.lib.PimpVanilla._
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation

class TileCover extends TileEntity {
  var data: CoverInfo = _

  lazy val multipart = new TileCoverMultipart(this)

  override def readFromNBT(tag: NBTTagCompound): Unit = {
    super[TileEntity].readFromNBT(tag)
    for {
      shapeId <- tag.get[String]("shape")
      materialId <- tag.get[String]("material")
      slotId <- tag.get[String]("slot")
      size <- tag.get[Int]("size")
      shape <- InternalRegistry.shapes.get(shapeId) if shape.validSizes.contains(size)
      material <- Option(MCMultiPart.microMaterialRegistry.getValue(new ResourceLocation(materialId)))
      slot <- Option(MCMultiPart.slotRegistry.getValue(new ResourceLocation(slotId)))
    } {
      data = CoverInfo(shape, slot, material, size)
    }
  }

  override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
    if (data != null) {
      tag.set("shape", data.shape.name)
      tag.set("material", data.material.getRegistryName.toString)
      tag.set("slot", data.slot.getRegistryName.toString)
      tag.set("size", data.size)
    }
    super[TileEntity].writeToNBT(tag)
  }
  override def getUpdateTag: NBTTagCompound = writeToNBT(new NBTTagCompound)

  override def handleUpdateTag(tag: NBTTagCompound): Unit = readFromNBT(tag)

  override def getUpdatePacket: SPacketUpdateTileEntity =
    new SPacketUpdateTileEntity(pos, 0, getUpdateTag)

  override def onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity): Unit =
    if (world.isRemote && pkt.getTileEntityType == 0)
      handleUpdateTag(pkt.getNbtCompound)
}
