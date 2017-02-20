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

import net.bdew.covers.block.ItemCover
import net.bdew.covers.microblock.MicroMaterialHelper
import net.bdew.covers.microblock.shape.FaceShape
import net.bdew.lib.crafting.RecipeMatcher
import net.minecraft.item.ItemStack

object RecipeCombineParts extends MicroblockRecipe {
  override def verifyAndCreateResult(inv: RecipeMatcher): Option[ItemStack] = {
    for {
      first <- inv.matchItem(ItemCover).first()
      second <- inv.matchItem(ItemCover).and(first.matchRight).first() if inv.allMatched
      firstData <- ItemCover.getData(first.stack)
      secondData <- ItemCover.getData(second.stack)
      if firstData == secondData && (firstData.shape.validSizes.contains(firstData.size * 2) || (firstData.size == 4 && firstData.shape == FaceShape))
    } yield {
      if (firstData.size == 4 && firstData.shape == FaceShape)
        MicroMaterialHelper.getItemStack(firstData.material)
      else
        ItemCover.makeStack(firstData.material, firstData.shape, firstData.size * 2)
    }
  }
}
