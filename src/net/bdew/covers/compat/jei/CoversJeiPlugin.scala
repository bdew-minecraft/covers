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

import java.util

import mezz.jei.api._
import mezz.jei.api.recipe.IRecipeWrapper
import net.bdew.covers.Covers
import net.bdew.covers.microblock.MicroblockRegistry

@JEIPlugin
class CoversJeiPlugin extends BlankModPlugin {
  override def register(registry: IModRegistry): Unit = {
    Covers.logInfo("Simple Covers JEI Plugin loaded")
    registry.addRecipeHandlers(MicroblockRecipeHandler)

    val toAdd = new util.ArrayList[IRecipeWrapper]()

    toAdd.add(MicroblockRecipeCutBlock)

    for (shape <- MicroblockRegistry.shapes.values; size <- shape.validSizes) {
      if (size % 2 == 0 && shape.validSizes.contains(size / 2))
        toAdd.add(new MicroblockRecipeCutPart(shape, size, shape, size / 2, true))
      if (shape.validSizes.contains(size * 2))
        toAdd.add(new MicroblockRecipeCombinePart(shape, size, shape, size * 2, false))
    }

    registry.addRecipes(toAdd)
  }

  override def onRuntimeAvailable(jeiRuntime: IJeiRuntime): Unit = {

  }
}
