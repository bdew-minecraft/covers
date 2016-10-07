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

package net.bdew.covers.recipes

import net.bdew.covers.items.ItemMicroblock
import net.bdew.lib.crafting.RecipeMatcher
import net.minecraft.item.ItemStack

object RecipeGhostPart extends MicroblockRecipe {
  override def verifyAndCreateResult(inv: RecipeMatcher): Option[ItemStack] = {
    for {
      top <- inv.at(1, 0).and(inv.matchItem(ItemMicroblock)).first()
      data <- ItemMicroblock.getData(top.stack)
      (newShape, newSize) <- data.shape.ghost(data.size)
      if (inv.at(0, 1).and(top.same).verify()
        && inv.at(2, 1).and(top.same).verify()
        && inv.at(1, 2).and(top.same).verify()
        && inv.allMatched)
    } yield ItemMicroblock.makeStack(data.material, newShape, newSize, 4)
  }
}
