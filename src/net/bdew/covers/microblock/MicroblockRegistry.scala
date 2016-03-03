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

package net.bdew.covers.microblock

import net.bdew.covers.microblock.shape.FaceShape
import net.minecraft.block.Block
import net.minecraft.init.Blocks

object MicroblockRegistry {
  var shapes = Map.empty[String, MicroblockShape]
  var materials = Map.empty[String, MicroblockMaterial]
  var blocks = Map.empty[(Block, Int), MicroblockMaterial]

  def registerShape(p: MicroblockShape) = shapes += p.name -> p
  def getShape(n: String) = shapes(n)

  def registerMaterial(m: MicroblockMaterial) = materials += (m.id -> m)
  def getMaterial(n: String) = materials.getOrElse(n, materials("minecraft:stone@0"))

  registerMaterial(MicroblockMaterial(Blocks.stone, 0))
  registerShape(FaceShape)
}
