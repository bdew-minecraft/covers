package net.bdew.covers

import mcmultipart.api.addon.{IMCMPAddon, MCMPAddon}
import mcmultipart.api.multipart.IMultipartRegistry
import net.bdew.covers.block.{BlockCover, MultipartCover}
import net.bdew.lib.Misc

@MCMPAddon
class CoversMCMPAddon extends IMCMPAddon {
  override def registerParts(registry: IMultipartRegistry): Unit = {
    Misc.withModId(Covers.modId) {
      registry.registerPartWrapper(BlockCover, MultipartCover)
    }
  }
}
