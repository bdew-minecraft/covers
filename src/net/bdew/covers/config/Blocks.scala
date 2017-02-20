package net.bdew.covers.config

import net.bdew.covers.Creative
import net.bdew.covers.block.BlockCover
import net.bdew.lib.config.BlockManager

object Blocks  extends BlockManager(Creative.main) {
  regBlock(BlockCover)
}
