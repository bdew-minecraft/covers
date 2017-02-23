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
import net.bdew.covers.items.ItemSaw
import net.bdew.covers.microblock.InternalRegistry
import net.bdew.covers.microblock.shape.FaceShape
import net.bdew.lib.crafting.RecipeMatcher
import net.minecraft.block.Block
import net.minecraft.item.{ItemBlock, ItemStack}

object RecipeSplitBlock extends MicroblockRecipe {
  override def verifyAndCreateResult(inv: RecipeMatcher): Option[ItemStack] = {
    for {
      saw <- inv.matchItem(ItemSaw).first()
      block <- inv.matchItem(_.isInstanceOf[ItemBlock]).and(saw.matchBelow).first() if inv.allMatched
      blockObj <- Option(Block.getBlockFromItem(block.stack.getItem))
      material <- InternalRegistry.getMaterial(blockObj, block.stack.getItemDamage)
    } yield ItemCover.makeStack(material, FaceShape, 4, 2)
  }
}
