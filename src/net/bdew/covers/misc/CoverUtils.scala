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

package net.bdew.covers.misc

import net.bdew.covers.microblock.parts.PartImplementation
import net.bdew.covers.microblock.shape.{EdgeShape, FaceShape, GhostFaceShape, HollowFaceShape}
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.math.{AxisAlignedBB, Vec3d}

object CoverUtils {
  def getAxis(vec: Vec3d, axis: Axis, neg: Boolean = false) = {
    if (neg) {
      axis match {
        case Axis.X => vec.xCoord
        case Axis.Y => vec.yCoord
        case Axis.Z => vec.zCoord
      }
    } else {
      axis match {
        case Axis.X => 1 - vec.xCoord
        case Axis.Y => 1 - vec.yCoord
        case Axis.Z => 1 - vec.zCoord
      }
    }
  }

  def otherAxes(axis: Axis) = axis match {
    case Axis.X => (Axis.Y, Axis.Z)
    case Axis.Y => (Axis.Z, Axis.X)
    case Axis.Z => (Axis.X, Axis.Y)
  }

  def axisToFace(axis: Axis, positive: Boolean) = (axis, positive) match {
    case (Axis.X, true) => EnumFacing.EAST
    case (Axis.X, false) => EnumFacing.WEST
    case (Axis.Y, true) => EnumFacing.UP
    case (Axis.Y, false) => EnumFacing.DOWN
    case (Axis.Z, true) => EnumFacing.SOUTH
    case (Axis.Z, false) => EnumFacing.NORTH
  }

  def clampBBOnAxis(box: AxisAlignedBB, axis: Axis, min: Double, max: Double) =
    new AxisAlignedBB(
      if (axis == Axis.X && box.minX < min) min else box.minX,
      if (axis == Axis.Y && box.minY < min) min else box.minY,
      if (axis == Axis.Z && box.minZ < min) min else box.minZ,
      if (axis == Axis.X && box.maxX > max) max else box.maxX,
      if (axis == Axis.Y && box.maxY > max) max else box.maxY,
      if (axis == Axis.Z && box.maxZ > max) max else box.maxZ
    )

  val boundsPriorities = Map(
    FaceShape -> 2,
    HollowFaceShape -> 2,
    GhostFaceShape -> 2,
    EdgeShape -> 1
  ).withDefaultValue(-1)

  def shouldPartAffectBounds(part: PartImplementation, otherPart: PartImplementation): Boolean = {
    if (!part.getBounds.intersectsWith(otherPart.getBounds)) return false
    val p1 = boundsPriorities(part.shape)
    val p2 = boundsPriorities(otherPart.shape)
    if (p1 == -1 || p2 == -1 || p2 < p1)
      false
    else if (p2 > p1)
      true
    else if (otherPart.getSize > part.getSize) // Same priority - compare size
      true
    else if (otherPart.getSize < part.getSize)
      false
    else otherPart.getSlot.ordinal() > part.getSlot.ordinal() // Same priority and size
  }

  def limitBoxes(boxes: List[AABBHiddenFaces], bounds: AxisAlignedBB): List[AABBHiddenFaces] = {
    val minX = bounds.minX
    val minY = bounds.minY
    val minZ = bounds.minZ
    val maxX = bounds.maxX
    val maxY = bounds.maxY
    val maxZ = bounds.maxZ
    boxes.filter(_.intersectsWith(bounds)).map(box =>
      new AABBHiddenFaces(
        if (box.minX < minX) minX else box.minX,
        if (box.minY < minY) minY else box.minY,
        if (box.minZ < minZ) minZ else box.minZ,
        if (box.maxX > maxX) maxX else box.maxX,
        if (box.maxY > maxY) maxY else box.maxY,
        if (box.maxZ > maxZ) maxZ else box.maxZ,
        box.hidden
      )
    )
  }
}
