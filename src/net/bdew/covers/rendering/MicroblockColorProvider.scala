/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/bdlib
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.covers.rendering

import mcmultipart.client.multipart.{IMultipartColor, MultipartRegistryClient}
import mcmultipart.microblock.Microblock
import net.bdew.covers.Covers
import net.bdew.covers.items.ItemMicroblock
import net.bdew.covers.microblock.{InternalRegistry, PosProperty}
import net.bdew.lib.Client
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.property.IExtendedBlockState

object MicroblockColorProvider extends IMultipartColor with IItemColor {
  override def colorMultiplier(state: IBlockState, tintIndex: Int): Int = {
    if (state.isInstanceOf[IExtendedBlockState]) {
      val ex = state.asInstanceOf[IExtendedBlockState]
      val material = ex.getValue(Microblock.PROPERTY_MATERIAL)
      val pos = ex.getValue(PosProperty)
      if (material != null && pos != null)
        Client.blockColors.colorMultiplier(material.getDefaultMaterialState, Client.world, pos, tintIndex)
      else
        -1
    } else -1
  }

  override def getColorFromItemstack(stack: ItemStack, tintIndex: Int): Int = {
    ItemMicroblock.getData(stack) map { data =>
      Client.blockColors.colorMultiplier(data.material.getDefaultMaterialState, null, null, tintIndex)
    } getOrElse -1
  }

  def register(): Unit = {
    for (name <- InternalRegistry.shapes.keys) {
      MultipartRegistryClient.registerColorProvider(new ResourceLocation(Covers.modId, name), this)
    }
    Client.itemColors.registerItemColorHandler(this, ItemMicroblock)
  }
}
