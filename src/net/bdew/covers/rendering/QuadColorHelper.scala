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

import java.awt.Color

import net.bdew.covers.config.Config
import net.bdew.lib.Client
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class QuadColorHelper(state: IBlockState, world: IBlockAccess, pos: BlockPos) {
  // Concept shamelessly stolen from MCMP ModelMultipartContainer
  def colorizeQuads(quads: List[BakedQuad]) = {
    quads map { quad =>
      val tintColor =
        if (quad.hasTintIndex)
          new Color(Client.blockColors.colorMultiplier(state, world, pos, quad.getTintIndex), false)
        else
          new Color(1f, 1f, 1f)
      val data = quad.getVertexData.clone
      for (i <- 0 until 4) {
        val color = new Color(data(i * 7 + 3))
        data(i * 7 + 3) = ((Config.placementPreviewTransparency * 255).toInt << 24) | (((color.getRed * tintColor.getBlue) / 255) << 16) | (((color.getGreen * tintColor.getGreen) / 255) << 8) | (((color.getBlue * tintColor.getRed) / 255) << 0)
      }
      new BakedQuad(data, 0, quad.getFace, quad.getSprite, quad.shouldApplyDiffuseLighting, quad.getFormat)
    }
  }
}