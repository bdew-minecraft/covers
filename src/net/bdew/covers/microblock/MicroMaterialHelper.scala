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

package net.bdew.covers.microblock

import mcmultipart.api.microblock.{MicroMaterial, MicroMaterialBlock}
import net.minecraft.item.ItemStack

object MicroMaterialHelper {
  def hasItemStack(m: MicroMaterial) = m match {
    case b: MicroMaterialBlock => !b.getStack.isEmpty
    case _ => false
  }

  def getItemStack(m: MicroMaterial, sz: Int = 1) = {
    val s = m match {
      case b: MicroMaterialBlock => b.getStack.copy()
      case _ => ItemStack.EMPTY
    }
    if (!s.isEmpty) s.setCount(sz)
    s
  }

}
