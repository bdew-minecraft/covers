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

import mcmultipart.microblock.IMicroMaterial
import mcmultipart.multipart.PartSlot
import net.bdew.covers.microblock.parts.PartHollowFace
import net.bdew.covers.misc.{AABBHiddenFaces, CoverUtils, FacesToSlot}
import net.bdew.lib.block.BlockFace
import net.minecraft.util.EnumFacing.AxisDirection
import net.minecraft.util.{AxisAlignedBB, EnumFacing, Vec3}

object HollowFaceShape extends MicroblockShape("hollowface") {
  override val validSlots = PartSlot.FACES.toSet
  override def defaultSlot = PartSlot.NORTH

  override def createPart(slot: PartSlot, size: Int, material: IMicroMaterial, client: Boolean) = new PartHollowFace(material, slot, size, client)

  override def getBoundingBox(slot: PartSlot, size: Int): AxisAlignedBB = {
    require(validSlots.contains(slot))
    require(validSizes.contains(size))
    val doubleSize = size / 8D

    val (min, max) = if (slot.f1.getAxisDirection == EnumFacing.AxisDirection.POSITIVE) (1 - doubleSize, 1D) else (0D, doubleSize)

    CoverUtils.clampBBOnAxis(new AxisAlignedBB(0, 0, 0, 1, 1, 1), slot.f1.getAxis, min, max)
  }

  override def getPartBoxes(slot: PartSlot, size: Int): List[AABBHiddenFaces] = {
    val bb = getBoundingBox(slot, size)
    val (secondary, third) = CoverUtils.otherAxes(slot.f1.getAxis)
    val top = CoverUtils.clampBBOnAxis(bb, secondary, 0, 0.25)
    val side = CoverUtils.clampBBOnAxis(bb, secondary, 0.25, 0.75)
    val bottom = CoverUtils.clampBBOnAxis(bb, secondary, 0.75, 1)

    val sp = CoverUtils.axisToFace(secondary, true)
    val sn = CoverUtils.axisToFace(secondary, false)
    val tp = CoverUtils.axisToFace(third, true)
    val tn = CoverUtils.axisToFace(third, false)

    List(
      AABBHiddenFaces.withHiddenFaces(CoverUtils.clampBBOnAxis(top, third, 0, 0.25), tp, sp),
      AABBHiddenFaces.withHiddenFaces(CoverUtils.clampBBOnAxis(top, third, 0.25, 0.75), tp, tn),
      AABBHiddenFaces.withHiddenFaces(CoverUtils.clampBBOnAxis(top, third, 0.75, 1), tn, sp),
      AABBHiddenFaces.withHiddenFaces(CoverUtils.clampBBOnAxis(side, third, 0, 0.25), sp, sn),
      AABBHiddenFaces.withHiddenFaces(CoverUtils.clampBBOnAxis(side, third, 0.75, 1), sp, sn),
      AABBHiddenFaces.withHiddenFaces(CoverUtils.clampBBOnAxis(bottom, third, 0, 0.25), tp, sn),
      AABBHiddenFaces.withHiddenFaces(CoverUtils.clampBBOnAxis(bottom, third, 0.25, 0.75), tp, tn),
      AABBHiddenFaces.withHiddenFaces(CoverUtils.clampBBOnAxis(bottom, third, 0.75, 1), tn, sn)
    )
  }

  override def getShadowedSlots(slot: PartSlot, size: Int): util.EnumSet[PartSlot] = FacesToSlot.find(slot.f1)

  override def getSlotFromHit(vec: Vec3, side: EnumFacing): Option[PartSlot] = {
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

  override def hollow(size: Int): Option[(MicroblockShape, Int)] = Some(FaceShape, size)
}
