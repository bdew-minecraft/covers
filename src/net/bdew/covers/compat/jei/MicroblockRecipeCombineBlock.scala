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

import com.google.common.collect.ImmutableList
import net.bdew.covers.items.ItemMicroblock
import net.bdew.covers.microblock.InternalRegistry
import net.bdew.covers.microblock.shape.FaceShape
import net.minecraft.item.ItemStack

object MicroblockRecipeCombineBlock extends MicroblockRecipe {
  override val getInputs: util.List[_] = {
    val partList = new util.ArrayList[ItemStack]()

    for (x <- InternalRegistry.materials.values)
      partList.add(ItemMicroblock.makeStack(x, FaceShape, 4, 2))

    ImmutableList.of(partList, partList)
  }

  override val getOutputs: util.List[ItemStack] = {
    val blockList = new util.ArrayList[ItemStack]()

    for (x <- InternalRegistry.materials.values)
      blockList.add(x.getItem)

    blockList
  }
}