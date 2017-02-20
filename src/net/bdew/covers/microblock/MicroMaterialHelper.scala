package net.bdew.covers.microblock

import mcmultipart.api.microblock.{MicroMaterial, MicroMaterialBlock}
import net.minecraft.item.ItemStack

object MicroMaterialHelper {
  def hasItemStack(m: MicroMaterial) = m match {
    case b: MicroMaterialBlock => !b.getStack.isEmpty
    case _ => false
  }

  def getItemStack(m: MicroMaterial, sz: Int = 1) = {
    val s = m match {
      case b: MicroMaterialBlock => b.getStack.copy()
      case _ => ItemStack.EMPTY
    }
    if (!s.isEmpty) s.setCount(sz)
    s
  }

}
