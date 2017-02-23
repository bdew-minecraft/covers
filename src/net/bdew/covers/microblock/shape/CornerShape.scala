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

import mcmultipart.api.slot.{EnumCenterSlot, EnumCornerSlot, IPartSlot}
import net.bdew.covers.misc.{AABBHiddenFaces, CoverUtils, FacesToSlot}
import net.bdew.lib.block.BlockFace
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.{Axis, AxisDirection}
import net.minecraft.util.math.{AxisAlignedBB, Vec3d}

object CornerShape extends MicroblockShapeImpl("corner", classOf[EnumCornerSlot], EnumCornerSlot.values().toSet, EnumCornerSlot.CORNER_NNN) {
  override def isSolid(slot: IPartSlot, size: Int, side: EnumFacing): Boolean = false

  private def interval(size: Double, positive: Boolean): (Double, Double) =
    if (positive)
      (1 - size, 1)
    else
      (0, size)

  override def getBoundingBox(aSlot: IPartSlot, size: Int): AxisAlignedBB = {
    val slot = validateSlot(aSlot)
    require(validSizes.contains(size))
    val doubleSize = size / 8D

    val directions = Map(
      slot.getFace1.getAxis -> (slot.getFace1.getAxisDirection == AxisDirection.POSITIVE),
      slot.getFace2.getAxis -> (slot.getFace2.getAxisDirection == AxisDirection.POSITIVE),
      slot.getFace3.getAxis -> (slot.getFace3.getAxisDirection == AxisDirection.POSITIVE)
    )

    val (minX, maxX) = interval(doubleSize, directions(Axis.X))
    val (minY, maxY) = interval(doubleSize, directions(Axis.Y))
    val (minZ, maxZ) = interval(doubleSize, directions(Axis.Z))

    new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
  }

  override def getItemBoxes(size: Int): List[AABBHiddenFaces] = {
    require(validSizes.contains(size))
    val doubleSize = size / 16D
    val (min, max) = (0.5 - doubleSize, 0.5 + doubleSize)
    List(new AABBHiddenFaces(min, min, min, max, max, max, AABBHiddenFaces.noFaces))
  }

  override def exclusionBox(slot: IPartSlot, size: Int, box: AxisAlignedBB, sides: Set[EnumFacing]): AxisAlignedBB = box

  override def getShadowedSlots(aSlot: IPartSlot, size: Int): util.Set[IPartSlot] = {
    import scala.collection.JavaConverters._
    val slot = validateSlot(aSlot)
    val faces = Set(
      FacesToSlot.from(slot.getFace1),
      FacesToSlot.from(slot.getFace2),
      FacesToSlot.from(slot.getFace3),
      FacesToSlot.from(slot.getFace1, slot.getFace2),
      FacesToSlot.from(slot.getFace2, slot.getFace3),
      FacesToSlot.from(slot.getFace3, slot.getFace1)
    ).asJava
    if (size >= 4) faces.add(EnumCenterSlot.CENTER)
    faces
  }

  override def getSlotFromHit(vec: Vec3d, side: EnumFacing): Option[IPartSlot] = {
    val neighbours = BlockFace.neighbourFaces(side)
    val x = CoverUtils.getAxis(vec, neighbours.right.getAxis, neighbours.right.getAxisDirection == AxisDirection.POSITIVE)
    val y = CoverUtils.getAxis(vec, neighbours.top.getAxis, neighbours.top.getAxisDirection == AxisDirection.POSITIVE)

    if (y > 0.5) {
      if (x > 0.5) {
        Some(FacesToSlot.from(side, neighbours.top, neighbours.right))
      } else {
        Some(FacesToSlot.from(side, neighbours.top, neighbours.left))
      }
    } else {
      if (x > 0.5) {
        Some(FacesToSlot.from(side, neighbours.bottom, neighbours.right))
      } else {
        Some(FacesToSlot.from(side, neighbours.bottom, neighbours.left))
      }
    }
  }

  override def combine(size: Int): Option[(MicroblockShape, Int)] = Some(EdgeShape, size)
}
