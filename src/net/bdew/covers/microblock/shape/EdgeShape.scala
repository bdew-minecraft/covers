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

import mcmultipart.api.slot.{EnumCenterSlot, EnumEdgeSlot, IPartSlot}
import net.bdew.covers.misc.{AABBHiddenFaces, CoverUtils, FacesToSlot}
import net.bdew.lib.block.BlockFace
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.{Axis, AxisDirection}
import net.minecraft.util.math.{AxisAlignedBB, Vec3d}

object EdgeShape extends MicroblockShapeImpl("edge", classOf[EnumEdgeSlot], EnumEdgeSlot.values().toSet, EnumEdgeSlot.EDGE_NNZ) {
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
      slot.getFace2.getAxis -> (slot.getFace2.getAxisDirection == AxisDirection.POSITIVE)
    )

    val (minX, maxX) = directions.get(Axis.X).map(d => interval(doubleSize, d)).getOrElse(0D, 1D)
    val (minY, maxY) = directions.get(Axis.Y).map(d => interval(doubleSize, d)).getOrElse(0D, 1D)
    val (minZ, maxZ) = directions.get(Axis.Z).map(d => interval(doubleSize, d)).getOrElse(0D, 1D)

    new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
  }

  override def getItemBoxes(size: Int): List[AABBHiddenFaces] = {
    require(validSizes.contains(size))
    val doubleSize = size / 16D
    val (min, max) = (0.5 - doubleSize, 0.5 + doubleSize)
    List(new AABBHiddenFaces(min, 0, min, max, 1, max, AABBHiddenFaces.noFaces))
  }

  override def exclusionBox(aSlot: IPartSlot, size: Int, box: AxisAlignedBB, sides: Set[EnumFacing]): AxisAlignedBB = {
    val slot = validateSlot(aSlot)
    if (sides.contains(slot.getFace1)) {
      val (min, max) = if (slot.getFace2.getAxisDirection == AxisDirection.POSITIVE) (0D, 1 - size / 8D) else (size / 8D, 1D)
      CoverUtils.clampBBOnAxis(box, slot.getFace2.getAxis, min, max)
    } else if (sides.contains(slot.getFace2)) {
      val (min, max) = if (slot.getFace1.getAxisDirection == AxisDirection.POSITIVE) (0D, 1 - size / 8D) else (size / 8D, 1D)
      CoverUtils.clampBBOnAxis(box, slot.getFace1.getAxis, min, max)
    } else box
  }

  override def getShadowedSlots(aSlot: IPartSlot, size: Int): util.Set[IPartSlot] = {
    val slot = validateSlot(aSlot)
    val faces = FacesToSlot.find(slot.getFace1, slot.getFace2)
    faces.add(FacesToSlot.from(slot.getFace1))
    faces.add(FacesToSlot.from(slot.getFace2))
    if (size >= 4) faces.add(EnumCenterSlot.CENTER)
    faces
  }

  override def getSlotFromHit(vec: Vec3d, side: EnumFacing): Option[IPartSlot] = {
    val neighbours = BlockFace.neighbourFaces(side)
    val x = CoverUtils.getAxis(vec, neighbours.right.getAxis, neighbours.right.getAxisDirection == AxisDirection.POSITIVE)
    val y = CoverUtils.getAxis(vec, neighbours.top.getAxis, neighbours.top.getAxisDirection == AxisDirection.POSITIVE)

    if (y > 0.7) {
      if (x > 0.7) {
        Some(FacesToSlot.from(neighbours.top, neighbours.right))
      } else if (x < 0.3) {
        Some(FacesToSlot.from(neighbours.top, neighbours.left))
      } else {
        Some(FacesToSlot.from(side, neighbours.top))
      }
    } else if (y < 0.3) {
      if (x > 0.7) {
        Some(FacesToSlot.from(neighbours.bottom, neighbours.right))
      } else if (x < 0.3) {
        Some(FacesToSlot.from(neighbours.bottom, neighbours.left))
      } else {
        Some(FacesToSlot.from(side, neighbours.bottom))
      }
    } else {
      if (x > 0.7) {
        Some(FacesToSlot.from(side, neighbours.right))
      } else if (x < 0.3) {
        Some(FacesToSlot.from(side, neighbours.left))
      } else {
        None
      }
    }
  }

  override def reduce(size: Int): Option[(MicroblockShape, Int)] = Some(CornerShape, size)
  override def combine(size: Int): Option[(MicroblockShape, Int)] = Some(FaceShape, size)
  override def transform(size: Int): Option[(MicroblockShape, Int)] = Some(CenterShape, size)
}
