package net.bdew.covers.block

import mcmultipart.api.microblock.MicroMaterial
import mcmultipart.api.slot.IPartSlot
import net.bdew.covers.microblock.shape.MicroblockShape
import net.bdew.lib.property.SimpleUnlistedProperty

case class CoverInfo(shape: MicroblockShape, slot: IPartSlot, material: MicroMaterial, size: Int)

object CoverInfoProperty extends SimpleUnlistedProperty("COVER", classOf[CoverInfo])

