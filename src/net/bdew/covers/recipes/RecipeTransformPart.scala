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

package net.bdew.covers.recipes

import net.bdew.covers.block.ItemCover
import net.bdew.lib.crafting.RecipeMatcher
import net.minecraft.item.ItemStack

object RecipeTransformPart extends MicroblockRecipe("transform", 1, 1) {
  override def verifyAndCreateResult(inv: RecipeMatcher): Option[ItemStack] = {
    for {
      part <- inv.matchItem(ItemCover).first() if inv.allMatched
      data <- ItemCover.getData(part.stack)
      (newShape, newSize) <- data.shape.transform(data.size)
    } yield ItemCover.makeStack(data.material, newShape, newSize)
  }
}
