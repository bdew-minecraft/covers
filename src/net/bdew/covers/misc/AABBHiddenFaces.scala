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

package net.bdew.covers.misc

import java.util

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB

class AABBHiddenFaces(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double, val hidden: util.EnumSet[EnumFacing]) extends AxisAlignedBB(x1, y1, z1, x2, y2, z2)

object AABBHiddenFaces {
  val noFaces = util.EnumSet.noneOf(classOf[EnumFacing])
  val allFaces = util.EnumSet.allOf(classOf[EnumFacing])

  def withAllVisible(bb: AxisAlignedBB) = new AABBHiddenFaces(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, noFaces)

  def withHiddenFaces(bb: AxisAlignedBB, faces: EnumFacing*): AABBHiddenFaces =
    withHiddenFaces(bb, if (faces.isEmpty) noFaces else util.EnumSet.of(faces.head, faces.tail: _*))

  def withVisibleFaces(bb: AxisAlignedBB, faces: EnumFacing*): AABBHiddenFaces =
    withVisibleFaces(bb, if (faces.isEmpty) noFaces else util.EnumSet.of(faces.head, faces.tail: _*))

  def withHiddenFaces(bb: AxisAlignedBB, faces: util.EnumSet[EnumFacing]): AABBHiddenFaces =
    new AABBHiddenFaces(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, faces)

  def withVisibleFaces(bb: AxisAlignedBB, faces: util.EnumSet[EnumFacing]): AABBHiddenFaces =
    new AABBHiddenFaces(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, util.EnumSet.complementOf(faces))
}