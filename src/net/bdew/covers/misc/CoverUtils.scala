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

import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.{AxisAlignedBB, EnumFacing, Vec3}

object CoverUtils {
  def getAxis(vec: Vec3, axis: Axis, neg: Boolean = false) = {
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
}
