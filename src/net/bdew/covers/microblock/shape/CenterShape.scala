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
import net.bdew.covers.microblock.parts.PartCenter
import net.bdew.covers.misc.{AABBHiddenFaces, CoverUtils}
import net.bdew.lib.block.BlockFace
import net.minecraft.util.EnumFacing.{Axis, AxisDirection}
import net.minecraft.util.{AxisAlignedBB, EnumFacing, Vec3}

object CenterShape extends MicroblockShape("center") {
  override val validSlots = Set(PartSlot.NORTH, PartSlot.UP, PartSlot.EAST)
  override val defaultSlot = PartSlot.NORTH

  override def createPart(slot: PartSlot, size: Int, material: IMicroMaterial, client: Boolean) = new PartCenter(material, slot, size, client)

  override def getBoundingBox(slot: PartSlot, size: Int): AxisAlignedBB = {
    require(validSlots.contains(slot))
    require(validSizes.contains(size))

    val offset = size / 16D

    slot match {
      case PartSlot.EAST => new AxisAlignedBB(0, 0.5 - offset, 0.5 - offset, 1, 0.5 + offset, 0.5 + offset)
      case PartSlot.UP => new AxisAlignedBB(0.5 - offset, 0, 0.5 - offset, 0.5 + offset, 1, 0.5 + offset)
      case PartSlot.NORTH => new AxisAlignedBB(0.5 - offset, 0.5 - offset, 0, 0.5 + offset, 0.5 + offset, 1)
      case _ => sys.error("This should be unreachable")
    }
  }

  override def getItemBoxes(size: Int): List[AABBHiddenFaces] = {
    require(validSizes.contains(size))
    val offset = size / 16D
    List(new AABBHiddenFaces(0.5 - offset, 0, 0.5 - offset, 0.5 + offset, 1, 0.5 + offset, AABBHiddenFaces.noFaces))
  }

  override def exclusionBox(slot: PartSlot, size: Int, box: AxisAlignedBB, sides: Set[EnumFacing]): AxisAlignedBB = box

  override def getShadowedSlots(slot: PartSlot, size: Int): util.EnumSet[PartSlot] = util.EnumSet.noneOf(classOf[PartSlot])

  override def getSlotFromHit(vec: Vec3, side: EnumFacing): Option[PartSlot] = {
    val neighbours = BlockFace.neighbourFaces(side)

    val x = CoverUtils.getAxis(vec, neighbours.right.getAxis, neighbours.right.getAxisDirection == AxisDirection.POSITIVE)
    val y = CoverUtils.getAxis(vec, neighbours.top.getAxis, neighbours.top.getAxisDirection == AxisDirection.POSITIVE)

    if (x > 0.25 && x < 0.75 && y > 0.25 && y < 0.75) {
      side.getAxis match {
        case Axis.X => Some(PartSlot.EAST)
        case Axis.Y => Some(PartSlot.UP)
        case Axis.Z => Some(PartSlot.NORTH)
      }
    } else None
  }

  override def getSlotMask(slot: PartSlot, size: Int): util.EnumSet[PartSlot] = util.EnumSet.of(PartSlot.CENTER)

  override def transform(size: Int): Option[(MicroblockShape, Int)] = Some(EdgeShape, size)
}
