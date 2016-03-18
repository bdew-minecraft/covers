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

package net.bdew.covers.transition

import net.bdew.covers.items.ItemMicroblock
import net.bdew.covers.microblock.InternalRegistry
import net.bdew.covers.recipes.MicroblockRecipe
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.crafting.RecipeMatcher
import net.minecraft.item.ItemStack

object RecipeConvert extends MicroblockRecipe {
  override def verifyAndCreateResult(inv: RecipeMatcher): Option[ItemStack] = {
    for {
      first <- inv.matchItem(OldItemMicroblock).first() if inv.allMatched && first.stack.hasTagCompound
      shapeId <- first.stack.getTagCompound.get[String]("shape")
      materialId <- first.stack.getTagCompound.get[String]("material")
      size <- first.stack.getTagCompound.get[Int]("size")
      shape <- InternalRegistry.shapes.get(shapeId)
      material <- OldPartConverter.materialMap.get(materialId)
    } yield {
      ItemMicroblock.makeStack(material, shape, Math.min(if (size > 1) size >> 1 else 1, 4))
    }
  }
}
