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

package net.bdew.covers.microblock

import net.bdew.covers.rendering.MicroblockModelProvider
import net.minecraft.block.Block
import net.minecraft.client.resources.model.IBakedModel
import net.minecraft.item.ItemStack
import net.minecraftforge.client.model.IModelState
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class MicroblockMaterial(block: Block, meta: Int) {
  val stack = new ItemStack(block, 0, meta)
  val id = block.getRegistryName + "@" + meta

  def sound = block.stepSound
  def displayName = stack.getDisplayName

  @SideOnly(Side.CLIENT)
  def getModel(data: MicroblockData, state: IModelState): IBakedModel =
    MicroblockModelProvider.getModel(block, meta, data.shape, data.slot, data.size, state)
}

