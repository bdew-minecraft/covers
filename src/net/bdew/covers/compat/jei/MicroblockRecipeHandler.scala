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

package net.bdew.covers.compat.jei

import mezz.jei.api.recipe.{IRecipeHandler, IRecipeWrapper, VanillaRecipeCategoryUid}

object MicroblockRecipeHandler extends IRecipeHandler[MicroblockRecipe] {
  override def getRecipeWrapper(recipe: MicroblockRecipe): IRecipeWrapper = recipe
  override def isRecipeValid(recipe: MicroblockRecipe): Boolean = true
  override def getRecipeClass: Class[MicroblockRecipe] = classOf[MicroblockRecipe]
  override def getRecipeCategoryUid(recipe: MicroblockRecipe): String = VanillaRecipeCategoryUid.CRAFTING
}
