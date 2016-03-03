/*
 * Copyright (c) bdew 2016.
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

package net.bdew.covers.misc

import net.bdew.lib.render.primitive.{Texture, UV, Vertex}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.{EnumFacing, Vec3}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object FaceHelper {
  def getAxis(vec: Vec3, axis: Axis, neg: Boolean = false) = {
    if (neg) {
      axis match {
        case Axis.X => vec.xCoord
        case Axis.Y => vec.yCoord
        case Axis.Z => vec.zCoord
      }
    } else {
      axis match {
        case Axis.X => 1 - vec.xCoord
        case Axis.Y => 1 - vec.yCoord
        case Axis.Z => 1 - vec.zCoord
      }
    }
  }

  @SideOnly(Side.CLIENT)
  def cuboidTexture(v1: Vertex, v2: Vertex, face: EnumFacing, texture: TextureAtlasSprite): Texture = {
    face.getAxis match {
      case Axis.X => Texture(texture, UV(v1.y * 16f, v1.z * 16f), UV(v2.y * 16f, v2.z * 16f))
      case Axis.Z => Texture(texture, UV(v1.x * 16f, v1.y * 16f), UV(v2.x * 16f, v2.y * 16f))
      case Axis.Y => Texture(texture, UV(v1.z * 16f, v1.x * 16f), UV(v2.z * 16f, v2.x * 16f))
    }
  }
}
