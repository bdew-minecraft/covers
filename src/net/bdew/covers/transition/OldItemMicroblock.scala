package net.bdew.covers.transition

import java.util

import net.bdew.lib.Misc
import net.bdew.lib.items.BaseItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}

object OldItemMicroblock extends BaseItem("Part") {
  setHasSubtypes(true)
  override def getSubItems(item: Item, tab: CreativeTabs, list: util.List[ItemStack]): Unit = {}
  override def addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: util.List[String], advanced: Boolean): Unit = {
    tooltip.add(Misc.toLocal("item.covers.Part.description1"))
    tooltip.add(Misc.toLocal("item.covers.Part.description2"))
  }
}
