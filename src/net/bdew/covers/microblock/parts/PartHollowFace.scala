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

package net.bdew.covers.microblock.parts

import mcmultipart.microblock.IMicroMaterial
import mcmultipart.microblock.IMicroblock.IFaceMicroblock
import mcmultipart.multipart.{IMultipart, PartSlot}
import net.bdew.covers.microblock.shape._
import net.minecraft.util.EnumFacing

class PartHollowFace(material: IMicroMaterial, slot: PartSlot, size: Int, isRemote: Boolean) extends BasePart(HollowFaceShape, material, slot, size, isRemote) with IFaceMicroblock {
  override def getFace: EnumFacing = getSlot.f1
  override def isEdgeHollow: Boolean = false
  override def isFaceHollow: Boolean = true

  override def occlusionTest(part: IMultipart): Boolean = {
    super.occlusionTest(part) && (
      if (getSize >= 4 && part.isInstanceOf[PartCenter]) {
        part.asInstanceOf[PartCenter].getSlot.f1.getAxis == getFace.getAxis
      } else true)
  }
}

