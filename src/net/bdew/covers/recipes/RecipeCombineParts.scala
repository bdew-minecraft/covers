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
import net.bdew.covers.microblock.shape.FaceShape
import net.bdew.covers.microblock.{MicroblockData, MicroblockRegistry}
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.world.World

object RecipeCombineParts extends IRecipe {
  def samePart(parts: List[MicroblockData]): Option[MicroblockData] =
    if (parts.tail.forall(parts.head.eq)) parts.headOption else None

  def verifyAndGetData(inv: InventoryCrafting): Option[MicroblockData] = {
    var parts = List.empty[MicroblockData]
    for (i <- 0 until inv.getWidth; j <- 0 until inv.getHeight; stack <- Option(inv.getStackInRowAndColumn(i, j))) {
      if (stack.getItem == ItemMicroblock && parts.length < 2)
        parts :+= ItemMicroblock.getData(stack).getOrElse(return None)
      else return None
    }
    if (parts.length == 2 && parts(0) == parts(1)) {
      val data = parts(0)
      if (data.shape.validSizes.contains(data.size * 2) || data.shape.blockSize == data.size * 2)
        Some(data)
      else
        None
    } else None
  }

  override def matches(inv: InventoryCrafting, worldIn: World): Boolean = verifyAndGetData(inv).isDefined

  override def getCraftingResult(inv: InventoryCrafting): ItemStack =
    verifyAndGetData(inv) map { data =>
      if (data.size * 2 == data.shape.blockSize)
        new ItemStack(data.material.block, 1, data.material.meta)
      else
        ItemMicroblock.makeStack(data.material, data.shape, data.size * 2)
    } getOrElse getRecipeOutput

  override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] = new Array[ItemStack](inv.getSizeInventory)

  override def getRecipeSize: Int = 9

  override def getRecipeOutput: ItemStack = ItemMicroblock.makeStack(MicroblockRegistry.defaultMaterial, FaceShape, 1)
}
