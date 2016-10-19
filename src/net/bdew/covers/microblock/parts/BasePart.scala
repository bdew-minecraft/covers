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

package net.bdew.covers.microblock.parts

import mcmultipart.microblock.{IMicroMaterial, Microblock}
import mcmultipart.multipart.PartSlot
import net.bdew.covers.microblock.InternalRegistry
import net.bdew.covers.microblock.shape.MicroblockShape
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer

abstract class BasePart(val shape: MicroblockShape, aMaterial: IMicroMaterial, slot: PartSlot, size: Int, isRemote: Boolean) extends Microblock(aMaterial, slot, size, isRemote) with PartImplementation {
  override def readFromNBT(tag: NBTTagCompound): Unit = {
    super.readFromNBT(tag)
    if (material == null) material = InternalRegistry.defaultMaterial
  }

  override def readUpdatePacket(buf: PacketBuffer): Unit = {
    super.readUpdatePacket(buf)
    if (material == null) {
      material = InternalRegistry.defaultMaterial
    }
  }
}
