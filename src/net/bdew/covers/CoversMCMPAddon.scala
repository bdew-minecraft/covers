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
