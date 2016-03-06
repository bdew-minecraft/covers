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

import mcmultipart.multipart.PartSlot
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.nbt.NBT
import net.bdew.lib.property.SimpleUnlistedProperty
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer

case class MicroblockData(shape: MicroblockShape, material: MicroblockMaterial, size: Int, slot: PartSlot) {
  def toNBT = NBT("shape" -> shape.name, "material" -> material.id, "size" -> size, "slot" -> slot.name())
  def writeNBT(tag: NBTTagCompound) = tag.merge(toNBT)
  def writePacket(buf: PacketBuffer): Unit = {
    buf.writeString(shape.name)
    buf.writeString(material.id)
    buf.writeInt(size)
    buf.writeString(slot.name())
  }
}

object MicroblockData {
  def fromNBT(tag: NBTTagCompound) = {
    for {
      shapeId <- tag.get[String]("shape")
      materialId <- tag.get[String]("material")
      size <- tag.get[Int]("size")
      slotName <- tag.get[String]("slot")
    } yield {
      val shape = MicroblockRegistry.getShape(shapeId)
      val material = MicroblockRegistry.getMaterial(materialId)
      if (shape.validSizes.contains(size))
        MicroblockData(shape, material, size, PartSlot.valueOf(slotName))
      else // Temporary - to prevent people that played with 0.0.1 from breaking their worlds
        MicroblockData(shape, material, shape.validSizes.toList.sorted.head, PartSlot.valueOf(slotName))
    }
  }

  def readPacket(buf: PacketBuffer) = {
    MicroblockData(
      MicroblockRegistry.getShape(buf.readStringFromBuffer(255)),
      MicroblockRegistry.getMaterial(buf.readStringFromBuffer(255)),
      buf.readInt(),
      PartSlot.valueOf(buf.readStringFromBuffer(255))
    )
  }

  object Property extends SimpleUnlistedProperty("microblock-data", classOf[MicroblockData])
}
