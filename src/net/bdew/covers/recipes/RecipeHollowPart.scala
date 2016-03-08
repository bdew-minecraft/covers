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

object RecipeHollowPart extends MicroblockRecipe {
  override def verifyAndCreateResult(inv: RecipeMatcher): Option[ItemStack] = {
    for {
      first <- inv.matchItem(ItemMicroblock).first()
      others = inv.matchItem(ItemMicroblock).all() if inv.at(1, 1).first().isEmpty && inv.allMatched && others.size == 7
      data <- ItemMicroblock.getData(first.stack) if others.forall(x => ItemMicroblock.getData(x.stack).contains(data))
      (newShape, newSize) <- data.shape.hollow(data.size)
    } yield ItemMicroblock.makeStack(data.material, newShape, newSize, 8)
  }
}
