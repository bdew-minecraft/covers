package net.bdew.covers.block

import mcmultipart.api.multipart.IMultipartTile
import net.minecraft.tileentity.TileEntity

class TileCoverMultipart(base: TileCover) extends IMultipartTile {
  override def getTileEntity: TileEntity = base
}
