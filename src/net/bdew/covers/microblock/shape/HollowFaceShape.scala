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

package net.bdew.covers.microblock.shape

import mcmultipart.multipart.PartSlot
import net.bdew.covers.microblock.{MicroblockShape, PartSlotMapper}
import net.bdew.covers.misc.AxisHelper
import net.bdew.lib.block.BlockFace
import net.minecraft.util.EnumFacing.AxisDirection
import net.minecraft.util.{AxisAlignedBB, EnumFacing, Vec3}

object HollowFaceShape extends MicroblockShape("hollowface") {
  override val blockSize = 16
  override val validSizes = Set(2, 4, 8)
  override val validSlots = PartSlot.FACES.toSet
  override def defaultSlot = PartSlot.NORTH

  override def getBoundingBox(slot: PartSlot, size: Int): AxisAlignedBB = {
    require(validSlots.contains(slot))
    require(validSizes.contains(size))
    val doubleSize = size / 16D

    val (min, max) = if (slot.f1.getAxisDirection == EnumFacing.AxisDirection.POSITIVE) (1 - doubleSize, 1D) else (0D, doubleSize)

    AxisHelper.clampBBOnAxis(new AxisAlignedBB(0, 0, 0, 1, 1, 1), slot.f1.getAxis, min, max)
  }

  override def getPartBoxes(slot: PartSlot, size: Int): List[AxisAlignedBB] = {
    val bb = getBoundingBox(slot, size)
    val (secondary, third) = AxisHelper.otherAxes(slot.f1.getAxis)
    val side = AxisHelper.clampBBOnAxis(bb, secondary, 0.25, 0.75)
    List(
      AxisHelper.clampBBOnAxis(bb, secondary, 0, 0.25),
      AxisHelper.clampBBOnAxis(bb, secondary, 0.75, 1),
      AxisHelper.clampBBOnAxis(side, third, 0, 0.25),
      AxisHelper.clampBBOnAxis(side, third, 0.75, 1)
    )
  }

  override def getSlotFromHit(vec: Vec3, side: EnumFacing): Option[PartSlot] = {
    val neighbours = BlockFace.neighbourFaces(side)
    val x = AxisHelper.getAxis(vec, neighbours.right.getAxis, neighbours.right.getAxisDirection == AxisDirection.POSITIVE)
    val y = AxisHelper.getAxis(vec, neighbours.top.getAxis, neighbours.top.getAxisDirection == AxisDirection.POSITIVE)

    if (y > 0.7)
      Some(PartSlotMapper.from(neighbours.top))
    else if (y < 0.3)
      Some(PartSlotMapper.from(neighbours.bottom))
    else if (x > 0.7)
      Some(PartSlotMapper.from(neighbours.right))
    else if (x < 0.3)
      Some(PartSlotMapper.from(neighbours.left))
    else
      Some(PartSlotMapper.from(side))
  }

  override def hollow(size: Int): Option[(MicroblockShape, Int)] = Some(FaceShape, size)
}
