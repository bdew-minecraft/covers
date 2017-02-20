/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/bdlib
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
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

  override def getColorFromItemstack(stack: ItemStack, tintIndex: Int): Int = {
    ItemCover.getData(stack) map { data =>
      Client.blockColors.colorMultiplier(data.material.getDefaultState, null, null, tintIndex)
    } getOrElse -1
  }

  def register(): Unit = {
    Client.blockColors.registerBlockColorHandler(this, BlockCover)
    Client.itemColors.registerItemColorHandler(this, ItemCover)
  }
}
