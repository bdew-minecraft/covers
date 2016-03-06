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

import java.util

import mcmultipart.multipart.PartSlot
import net.bdew.covers.microblock.{MicroblockShape, PartSlotMapper}
import net.bdew.covers.misc.FaceHelper
import net.bdew.lib.block.BlockFace
import net.minecraft.util.EnumFacing.AxisDirection
import net.minecraft.util.{AxisAlignedBB, EnumFacing, Vec3}

object FaceShape extends MicroblockShape("face") {
  override val blockSize = 16
  override val validSizes = Set(2, 4, 8)
  override val validSlots = PartSlot.FACES.toSet
  override def defaultSlot = PartSlot.NORTH

  override def isSolid(slot: PartSlot, size: Int, side: EnumFacing): Boolean =
    slot == PartSlot.getFaceSlot(side)

  private def interval(size: Double, positive: Boolean): (Double, Double) =
    if (positive)
      (1 - size, 1)
    else
      (0, size)

  override def getBoundingBox(slot: PartSlot, size: Int): AxisAlignedBB = {
    require(validSlots.contains(slot))
    require(validSizes.contains(size))
    val doubleSize = size / 16D
    val axis = slot.f1.getAxis
    val direction = slot.f1.getAxisDirection

    val (minX, maxX) = if (axis == EnumFacing.Axis.X) interval(doubleSize, direction == EnumFacing.AxisDirection.POSITIVE) else (0D, 1D)
    val (minY, maxY) = if (axis == EnumFacing.Axis.Y) interval(doubleSize, direction == EnumFacing.AxisDirection.POSITIVE) else (0D, 1D)
    val (minZ, maxZ) = if (axis == EnumFacing.Axis.Z) interval(doubleSize, direction == EnumFacing.AxisDirection.POSITIVE) else (0D, 1D)

    new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
  }

  override def getSlotFromHit(vec: Vec3, side: EnumFacing): Option[PartSlot] = {
    val neighbours = BlockFace.neighbourFaces(side)
    val x = FaceHelper.getAxis(vec, neighbours.right.getAxis, neighbours.right.getAxisDirection == AxisDirection.POSITIVE)
    val y = FaceHelper.getAxis(vec, neighbours.top.getAxis, neighbours.top.getAxisDirection == AxisDirection.POSITIVE)

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

  override def getSlotMask(slot: PartSlot, size: Int): util.EnumSet[PartSlot] = util.EnumSet.of(slot)
}
