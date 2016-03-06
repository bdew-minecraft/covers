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

import net.bdew.covers.items.{ItemMicroblock, ItemSaw}
import net.bdew.covers.microblock.shape.FaceShape
import net.bdew.covers.microblock.{MicroblockMaterial, MicroblockRegistry}
import net.minecraft.block.Block
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.{ItemBlock, ItemStack}
import net.minecraft.world.World

object RecipeSplitBlock extends IRecipe {
  def verifyAndGetMaterial(inv: InventoryCrafting): Option[MicroblockMaterial] = {
    var saw = Option.empty[RecipeMatch]
    var block = Option.empty[RecipeMatch]

    for (i <- 0 until inv.getWidth; j <- 0 until inv.getHeight; stack <- Option(inv.getStackInRowAndColumn(i, j))) {
      if (stack.getItem == ItemSaw && saw.isEmpty)
        saw = Some(RecipeMatch(i, j, stack))
      else if (stack.getItem.isInstanceOf[ItemBlock] && block.isEmpty)
        block = Some(RecipeMatch(i, j, stack))
      else return None // Extra item - abort
    }

    (saw, block) match {
      case (Some(RecipeMatch(xSaw, ySaw, stackSaw)), Some(RecipeMatch(xBlock, yBlock, stackBlock))) if xSaw == xBlock && yBlock == ySaw + 1 =>
        MicroblockRegistry.getMaterial(Block.getBlockFromItem(stackBlock.getItem), stackBlock.getItemDamage)
      case _ => None
    }
  }

  override def matches(inv: InventoryCrafting, worldIn: World): Boolean = verifyAndGetMaterial(inv).isDefined

  override def getCraftingResult(inv: InventoryCrafting): ItemStack =
    verifyAndGetMaterial(inv).map(material => ItemMicroblock.makeStack(material, FaceShape, FaceShape.blockSize / 2, 2)).getOrElse(getRecipeOutput)

  override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] = {
    val res = new Array[ItemStack](inv.getSizeInventory)
    for (i <- res.indices; stack <- Option(inv.getStackInSlot(i))) {
      if (stack.getItem == ItemSaw) res(i) = stack
    }
    res
  }

  override def getRecipeSize: Int = 9

  override def getRecipeOutput: ItemStack = ItemMicroblock.makeStack(MicroblockRegistry.defaultMaterial, FaceShape, 1)
}
