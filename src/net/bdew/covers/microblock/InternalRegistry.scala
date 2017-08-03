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

package net.bdew.covers.microblock

import mcmultipart.MCMultiPart
import mcmultipart.api.microblock.{MicroMaterial, MicroMaterialBlock}
import net.bdew.covers.Covers
import net.bdew.covers.microblock.shape._
import net.minecraft.block.Block
import net.minecraft.init.Blocks

object InternalRegistry {

  case class Material(block: Block, meta: Int)

  var shapes = Map.empty[String, MicroblockShape]
  var materials = Map.empty[Material, MicroMaterial]

  val defaultMaterial = registerMaterial(Blocks.STONE, 0)

  def registerShape(p: MicroblockShape): Unit = {
    MCMultiPart.microblockTypeRegistry.register(p)
    shapes += p.name -> p
  }

  def registerMaterial(block: Block, meta: Int): MicroMaterial = {
    val material = new MicroMaterialBlock(block.getStateFromMeta(meta))
    val registered = MCMultiPart.microMaterialRegistry.getValue(material.getRegistryName)
    val actualMaterial = if (registered == null) {
      MCMultiPart.microMaterialRegistry.register(material)
      material
    } else {
      Covers.logDebug("Material already registered - skipping: %s".format(material.getRegistryName))
      registered
    }
    materials += Material(block, meta) -> actualMaterial
    actualMaterial
  }

  def isValidMaterial(block: Block, meta: Int) = materials.isDefinedAt(Material(block, meta))
  def getMaterial(block: Block, meta: Int) = materials.get(Material(block, meta))

  registerShape(FaceShape)
  registerShape(EdgeShape)
  registerShape(CornerShape)
  registerShape(CenterShape)
  registerShape(HollowFaceShape)
  registerShape(GhostFaceShape)
}
