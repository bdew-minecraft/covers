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

import mcmultipart.microblock.{BlockMicroMaterial, IMicroMaterial, MicroblockRegistry}
import net.bdew.covers.Covers
import net.bdew.covers.microblock.shape._
import net.minecraft.block.Block

object InternalRegistry {
  case class Material(block: Block, meta: Int)

  var shapes = Map.empty[String, MicroblockShape]
  var materials = Map.empty[Material, IMicroMaterial]

  def registerShape(p: MicroblockShape): Unit = {
    MicroblockRegistry.registerMicroClass(p)
    shapes += p.name -> p
  }

  def registerMaterial(block: Block, meta: Int): Unit = {
    val material = new BlockMicroMaterial(block.getStateFromMeta(meta))
    if (MicroblockRegistry.getMaterial(material.getName) == null) {
      MicroblockRegistry.registerMaterial(material)
      materials += Material(block, meta) -> material
    } else {
      Covers.logInfo("Material already registered - skipping: %s".format(material.getName))
      materials += Material(block, meta) -> MicroblockRegistry.getMaterial(material.getName)
    }
  }

  def getMaterial(block: Block, meta: Int) = materials.get(Material(block, meta))

  registerShape(FaceShape)
  registerShape(EdgeShape)
  registerShape(CornerShape)
  registerShape(CenterShape)
  registerShape(HollowFaceShape)
}
