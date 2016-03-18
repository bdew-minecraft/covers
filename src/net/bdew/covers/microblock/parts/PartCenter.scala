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
import mcmultipart.multipart.ISolidPart.ISolidTopPart
import mcmultipart.multipart.{IMultipart, ISolidPart, PartSlot}
import net.bdew.covers.microblock.shape.CenterShape
import net.bdew.covers.misc.CoverUtils
import net.minecraft.util.EnumFacing.Axis

class PartCenter(material: IMicroMaterial, slot: PartSlot, size: Int, isRemote: Boolean) extends BasePart(CenterShape, material, slot, size, isRemote) with ISolidTopPart {
  override def canPlaceTorchOnTop: Boolean = getSlot.f1.getAxis == Axis.Y
  override def occlusionTest(part: IMultipart): Boolean =
    super.occlusionTest(part) && (
      if (part.isInstanceOf[ISolidPart]) {
        val axis = getSlot.f1.getAxis
        val solidPart = part.asInstanceOf[ISolidPart]
        !solidPart.isSideSolid(CoverUtils.axisToFace(axis, true)) && !solidPart.isSideSolid(CoverUtils.axisToFace(axis, false))
      } else true)
}
