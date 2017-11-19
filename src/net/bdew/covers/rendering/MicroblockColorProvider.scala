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

package net.bdew.covers.rendering

import net.bdew.covers.block.{BlockCover, CoverInfoProperty, ItemCover}
import net.bdew.lib.Client
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.color.{IBlockColor, IItemColor}
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.IExtendedBlockState

object MicroblockColorProvider extends IBlockColor with IItemColor {
  override def colorMultiplier(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos, tintIndex: Int): Int = {
    if (state.getBlock == BlockCover && state.isInstanceOf[IExtendedBlockState]) {
      val data = state.asInstanceOf[IExtendedBlockState].getValue(CoverInfoProperty)
      if (data != null)
        Client.blockColors.colorMultiplier(data.material.getDefaultState, worldIn, pos, tintIndex)
      else
        -1
    } else -1
  }

  override def colorMultiplier(itemStack: ItemStack, index: Int) = {
    ItemCover.getData(itemStack) map { data =>
      Client.blockColors.colorMultiplier(data.material.getDefaultState, null, null, index)
    } getOrElse -1
  }

  def register(): Unit = {
    Client.blockColors.registerBlockColorHandler(this, BlockCover)
    Client.itemColors.registerItemColorHandler(this, ItemCover)
  }
}
