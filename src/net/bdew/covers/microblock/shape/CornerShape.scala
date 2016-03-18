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
import net.bdew.covers.microblock.parts.PartCorner
import net.bdew.covers.misc.{AABBHiddenFaces, CoverUtils, FacesToSlot}
import net.bdew.lib.block.BlockFace
import net.minecraft.util.EnumFacing.{Axis, AxisDirection}
import net.minecraft.util.{AxisAlignedBB, EnumFacing, Vec3}

object CornerShape extends MicroblockShape("corner") {
  override val validSlots = PartSlot.CORNERS.toSet
  override val defaultSlot = PartSlot.CORNER_NNN

  override def createPart(slot: PartSlot, size: Int, material: IMicroMaterial, client: Boolean) = new PartCorner(material, slot, size, client)

  override def isSolid(slot: PartSlot, size: Int, side: EnumFacing): Boolean = false

  private def interval(size: Double, positive: Boolean): (Double, Double) =
    if (positive)
      (1 - size, 1)
    else
      (0, size)

  override def getBoundingBox(slot: PartSlot, size: Int): AxisAlignedBB = {
    require(validSlots.contains(slot))
    require(validSizes.contains(size))
    val doubleSize = size / 8D

    val directions = Map(
      slot.f1.getAxis -> (slot.f1.getAxisDirection == AxisDirection.POSITIVE),
      slot.f2.getAxis -> (slot.f2.getAxisDirection == AxisDirection.POSITIVE),
      slot.f3.getAxis -> (slot.f3.getAxisDirection == AxisDirection.POSITIVE)
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

  override def exclusionBox(slot: PartSlot, size: Int, box: AxisAlignedBB, sides: Set[EnumFacing]): AxisAlignedBB = box

  override def getShadowedSlots(slot: PartSlot, size: Int): util.EnumSet[PartSlot] = {
    val faces = util.EnumSet.of(
      FacesToSlot.from(slot.f1),
      FacesToSlot.from(slot.f2),
      FacesToSlot.from(slot.f3),
      FacesToSlot.from(slot.f1, slot.f2),
      FacesToSlot.from(slot.f2, slot.f3),
      FacesToSlot.from(slot.f3, slot.f1)
    )
    if (size >= 4) faces.add(PartSlot.CENTER)
    faces
  }

  override def getSlotFromHit(vec: Vec3, side: EnumFacing): Option[PartSlot] = {
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
