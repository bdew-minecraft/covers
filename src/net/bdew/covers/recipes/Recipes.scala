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

import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.RecipeSorter

object Recipes {
  def register(): Unit = {
    GameRegistry.addRecipe(RecipeSplitBlock)
    GameRegistry.addRecipe(RecipeSplitPart)
    GameRegistry.addRecipe(RecipeCombineParts)
    RecipeSorter.register("bdew.covers:splitblock", RecipeSplitBlock.getClass, RecipeSorter.Category.SHAPED, "")
    RecipeSorter.register("bdew.covers:splitpart", RecipeSplitPart.getClass, RecipeSorter.Category.SHAPED, "")
    RecipeSorter.register("bdew.covers:combine", RecipeCombineParts.getClass, RecipeSorter.Category.SHAPELESS, "")
  }
}
