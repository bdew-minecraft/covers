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

import mcmultipart.microblock.IMicroMaterial
import mcmultipart.multipart.PartSlot
import net.bdew.covers.microblock.parts.PartFace
import net.bdew.covers.misc.{CoverUtils, FacesToSlot}
import net.bdew.lib.block.BlockFace
import net.minecraft.util.EnumFacing.AxisDirection
import net.minecraft.util.{AxisAlignedBB, EnumFacing, Vec3}

object FaceShape extends MicroblockShape("face") {
  override val validSlots = PartSlot.FACES.toSet
  override def defaultSlot = PartSlot.NORTH

  override def createPart(slot: PartSlot, size: Int, material: IMicroMaterial, client: Boolean) = new PartFace(material, slot, size, client)

  override def isSolid(slot: PartSlot, size: Int, side: EnumFacing): Boolean =
    slot == PartSlot.getFaceSlot(side)

  override def getBoundingBox(slot: PartSlot, size: Int): AxisAlignedBB = {
    require(validSlots.contains(slot))
    require(validSizes.contains(size))
    val doubleSize = size / 8D

    val (min, max) = if (slot.f1.getAxisDirection == EnumFacing.AxisDirection.POSITIVE) (1 - doubleSize, 1D) else (0D, doubleSize)

    CoverUtils.clampBBOnAxis(new AxisAlignedBB(0, 0, 0, 1, 1, 1), slot.f1.getAxis, min, max)
  }

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

  override def reduce(size: Int): Option[(MicroblockShape, Int)] = Some(EdgeShape, size)
  override def hollow(size: Int): Option[(MicroblockShape, Int)] = Some(HollowFaceShape, size)
}
