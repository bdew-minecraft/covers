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

package net.bdew.covers.rendering

import mcmultipart.multipart.PartSlot
import net.bdew.covers.microblock.MicroblockShape
import net.bdew.lib.Client
import net.bdew.lib.render.Unpacker
import net.bdew.lib.render.models.SimpleBakedModelBuilder
import net.bdew.lib.render.primitive.TVertex
import net.minecraft.block.Block
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.resources.model.IBakedModel
import net.minecraft.util.{AxisAlignedBB, EnumFacing}
import net.minecraftforge.client.model.IModelState

import scala.collection.JavaConversions._

object MicroblockModelProvider {
  def getModel(block: Block, meta: Int, shape: MicroblockShape, slot: PartSlot, size: Int, state: IModelState): IBakedModel = {
    val base = Client.minecraft.getBlockRendererDispatcher.getBlockModelShapes.getModelForState(block.getStateFromMeta(meta))
    val box = shape.getBoundingBox(slot, size)
    val unpacker = new Unpacker(DefaultVertexFormats.ITEM)
    val builder = new SimpleBakedModelBuilder(DefaultVertexFormats.ITEM)

    builder.texture = base.getParticleTexture
    builder.isGui3d = true
    builder.setTransformsFromState(state)

    for (packed <- List.empty ++ base.getGeneralQuads ++ EnumFacing.values().flatMap(base.getFaceQuads)) {
      packed.pipe(unpacker)
      val quad = unpacker.buildAndReset().getQuad()
      val sf = scaleFactors(quad.vertexes.vector)
      builder.addQuadGeneral(quad.transform(x => clampVertex(x, box, sf)))
    }

    builder.build()
  }

  def scaleFactors(v: Vector[TVertex]) = {
    var xf = (0f, 0f)
    var yf = (0f, 0f)
    var zf = (0f, 0f)
    var found = 0
    for (i <- 0 until 3 if found < 2) {
      val v1 = v(i)
      val v2 = v(i + 1)
      val dx = v1.x - v2.x
      val dy = v1.y - v2.y
      val dz = v1.z - v2.z
      val du = v1.u - v2.u
      val dv = v1.v - v2.v
      if (dx != 0 && dy == 0 && dz == 0) {
        xf = (du / dx, dv / dx)
        found += 1
      } else if (dx == 0 && dy != 0 && dz == 0) {
        yf = (du / dy, dv / dy)
        found += 1
      } else if (dx == 0 && dy == 0 && dz != 0) {
        zf = (du / dz, dv / dz)
        found += 1
      }
    }
    (xf, yf, zf)
  }

  def clampVertex(vertex: TVertex, bb: AxisAlignedBB, sf: ((Float, Float), (Float, Float), (Float, Float))): TVertex = {
    if (vertex.x >= bb.minX && vertex.x <= bb.maxX && vertex.y >= bb.minY && vertex.y <= bb.maxY && vertex.z >= bb.minZ && vertex.z <= bb.maxZ) return vertex
    val ((xu, xv), (yu, yv), (zu, zv)) = sf
    var x = vertex.x
    var y = vertex.y
    var z = vertex.z
    var u = vertex.u
    var v = vertex.v

    if (x < bb.minX) {
      u = u + xu * (bb.minX.toFloat - x)
      v = v + xv * (bb.minX.toFloat - x)
      x = bb.minX.toFloat
    } else if (x > bb.maxX) {
      u = u + xu * (bb.maxX.toFloat - x)
      v = v + xv * (bb.maxX.toFloat - x)
      x = bb.maxX.toFloat
    }

    if (y < bb.minY) {
      u = u + yu * (bb.minY.toFloat - y)
      v = v + yv * (bb.minY.toFloat - y)
      y = bb.minY.toFloat
    } else if (y > bb.maxY) {
      u = u + yu * (bb.maxY.toFloat - y)
      v = v + yv * (bb.maxY.toFloat - y)
      y = bb.maxY.toFloat
    }

    if (z < bb.minZ) {
      u = u + zu * (bb.minZ.toFloat - z)
      v = v + zv * (bb.minZ.toFloat - z)
      z = bb.minZ.toFloat
    } else if (z > bb.maxZ) {
      u = u + zu * (bb.maxZ.toFloat - z)
      v = v + zv * (bb.maxZ.toFloat - z)
      z = bb.maxZ.toFloat
    }

    TVertex(x, y, z, u, v)
  }
}




