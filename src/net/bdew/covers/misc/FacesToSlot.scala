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

import mcmultipart.api.slot.{EnumCornerSlot, EnumEdgeSlot, EnumFaceSlot, IPartSlot}
import net.minecraft.util.EnumFacing

object FacesToSlot {
  val map: Map[Set[EnumFacing], IPartSlot] = (
    EnumFaceSlot.values().toList.map(x => Set(x.getFacing) -> x) ++
      EnumEdgeSlot.values().toList.map(x => Set(x.getFace1, x.getFace2) -> x) ++
      EnumCornerSlot.values().toList.map(x => Set(x.getFace1, x.getFace2, x.getFace3) -> x)
    ).toMap

  val inverted = map.map(_.swap)

  def from(faces: EnumFacing*) = map(faces.toSet)

  def find(faces: EnumFacing*) = {
    import scala.collection.JavaConverters._
    val faceSet = faces.toSet
    map.filterKeys(_.intersect(faceSet) == faceSet).values.toSet.asJava
  }
}
