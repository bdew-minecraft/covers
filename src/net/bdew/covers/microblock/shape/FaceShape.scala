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

import mcmultipart.api.slot.{EnumCenterSlot, EnumFaceSlot, IPartSlot}
import net.bdew.covers.misc.{AABBHiddenFaces, CoverUtils, FacesToSlot}
import net.bdew.lib.block.BlockFace
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.AxisDirection
import net.minecraft.util.math.{AxisAlignedBB, Vec3d}

object FaceShape extends MicroblockShapeImpl("face", classOf[EnumFaceSlot], EnumFaceSlot.values().toSet, EnumFaceSlot.NORTH) {
  override def isSolid(slot: IPartSlot, size: Int, side: EnumFacing): Boolean =
    slot == EnumFaceSlot.fromFace(side)

  override def getBoundingBox(aSlot: IPartSlot, size: Int): AxisAlignedBB = {
    val slot = validateSlot(aSlot)
    require(validSizes.contains(size))
    val doubleSize = size / 8D

    val (min, max) = if (slot.getFacing.getAxisDirection == AxisDirection.POSITIVE) (1 - doubleSize, 1D) else (0D, doubleSize)

    CoverUtils.clampBBOnAxis(new AxisAlignedBB(0, 0, 0, 1, 1, 1), slot.getFacing.getAxis, min, max)
  }

  override def getItemBoxes(size: Int): List[AABBHiddenFaces] = {
    require(validSizes.contains(size))
    val doubleSize = size / 16D
    val (min, max) = (0.5 - doubleSize, 0.5 + doubleSize)
    List(new AABBHiddenFaces(0, 0, min, 1, 1, max, AABBHiddenFaces.noFaces))
  }

  override def exclusionBox(aSlot: IPartSlot, size: Int, box: AxisAlignedBB, sides: Set[EnumFacing]): AxisAlignedBB = {
    val slot = validateSlot(aSlot)
    val (min, max) = if (slot.getFacing.getAxisDirection == AxisDirection.POSITIVE) (0D, 1 - size / 8D) else (size / 8D, 1D)
    CoverUtils.clampBBOnAxis(box, slot.getFacing.getAxis, min, max)
  }

  override def getShadowedSlots(slot: IPartSlot, size: Int): util.Set[IPartSlot] = {
    val faces = FacesToSlot.find(validateSlot(slot).getFacing)
    if (size >= 4) faces.add(EnumCenterSlot.CENTER)
    faces
  }

  override def getSlotFromHit(vec: Vec3d, side: EnumFacing): Option[IPartSlot] = {
    val neighbours = BlockFace.neighbourFaces(side)
    val x = CoverUtils.getAxis(vec, neighbours.right.getAxis, neighbours.right.getAxisDirection == AxisDirection.POSITIVE)
    val y = CoverUtils.getAxis(vec, neighbours.top.getAxis, neighbours.top.getAxisDirection == AxisDirection.POSITIVE)

    if (y > 0.7)
      Some(FacesToSlot.from(neighbours.top))
    else if (y < 0.3)
      Some(FacesToSlot.from(neighbours.bottom))
    else if (x > 0.7)
      Some(FacesToSlot.from(neighbours.right))
    else if (x < 0.3)
      Some(FacesToSlot.from(neighbours.left))
    else
      Some(FacesToSlot.from(side))
  }

  override def reduce(size: Int): Option[(MicroblockShape, Int)] = Some(EdgeShape, size)
  override def hollow(size: Int): Option[(MicroblockShape, Int)] = Some(HollowFaceShape, size)
  override def ghost(size: Int): Option[(MicroblockShape, Int)] =
    if (GhostFaceShape.validSizes.contains(size))
      Some(GhostFaceShape, size)
    else
      None
}
