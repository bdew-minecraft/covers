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

package net.bdew.covers.microblock.shape

import java.util
import java.util.Collections

import mcmultipart.api.slot.{EnumCenterSlot, EnumFaceSlot, IPartSlot}
import net.bdew.covers.misc.{AABBHiddenFaces, CoverUtils}
import net.bdew.lib.block.BlockFace
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.{Axis, AxisDirection}
import net.minecraft.util.math.{AxisAlignedBB, Vec3d}

object CenterShape extends MicroblockShapeImpl("center", classOf[EnumFaceSlot], Set(EnumFaceSlot.NORTH, EnumFaceSlot.UP, EnumFaceSlot.EAST), EnumFaceSlot.NORTH) {
  override def getBoundingBox(aSlot: IPartSlot, size: Int): AxisAlignedBB = {
    val slot = validateSlot(aSlot)
    require(validSizes.contains(size))

    val offset = size / 16D

    slot match {
      case EnumFaceSlot.EAST => new AxisAlignedBB(0, 0.5 - offset, 0.5 - offset, 1, 0.5 + offset, 0.5 + offset)
      case EnumFaceSlot.UP => new AxisAlignedBB(0.5 - offset, 0, 0.5 - offset, 0.5 + offset, 1, 0.5 + offset)
      case EnumFaceSlot.NORTH => new AxisAlignedBB(0.5 - offset, 0.5 - offset, 0, 0.5 + offset, 0.5 + offset, 1)
      case _ => sys.error("This should be unreachable")
    }
  }

  override def getItemBoxes(size: Int): List[AABBHiddenFaces] = {
    require(validSizes.contains(size))
    val offset = size / 16D
    List(new AABBHiddenFaces(0.5 - offset, 0, 0.5 - offset, 0.5 + offset, 1, 0.5 + offset, AABBHiddenFaces.noFaces))
  }

  override def exclusionBox(slot: IPartSlot, size: Int, box: AxisAlignedBB, sides: Set[EnumFacing]): AxisAlignedBB = box

  override def getShadowedSlots(slot: IPartSlot, size: Int): util.Set[IPartSlot] = Collections.emptySet()

  override def getSlotFromHit(vec: Vec3d, side: EnumFacing): Option[IPartSlot] = {
    val neighbours = BlockFace.neighbourFaces(side)

    val x = CoverUtils.getAxis(vec, neighbours.right.getAxis, neighbours.right.getAxisDirection == AxisDirection.POSITIVE)
    val y = CoverUtils.getAxis(vec, neighbours.top.getAxis, neighbours.top.getAxisDirection == AxisDirection.POSITIVE)

    if (x > 0.25 && x < 0.75 && y > 0.25 && y < 0.75) {
      side.getAxis match {
        case Axis.X => Some(EnumFaceSlot.EAST)
        case Axis.Y => Some(EnumFaceSlot.UP)
        case Axis.Z => Some(EnumFaceSlot.NORTH)
      }
    } else None
  }

  override def getSlotMask(slot: IPartSlot, size: Int): util.Set[IPartSlot] = Collections.singleton(EnumCenterSlot.CENTER)

  override def transform(size: Int): Option[(MicroblockShape, Int)] = Some(EdgeShape, size)
}
