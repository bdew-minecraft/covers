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

package net.bdew.covers.transition

import mcmultipart.multipart.IPartFactory.IAdvancedPartFactory
import mcmultipart.multipart.{IMultipart, MultipartRegistry, PartSlot}
import net.bdew.covers.Covers
import net.bdew.covers.microblock.InternalRegistry
import net.bdew.lib.PimpVanilla._
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation

object OldPartConverter extends IAdvancedPartFactory {
  var registered = false

  lazy val materialMap = for ((ref, material) <- InternalRegistry.materials)
    yield (ref.block.getRegistryName + "@" + ref.meta) -> material

  override def createPart(kind: ResourceLocation, tag: NBTTagCompound): IMultipart =
    if (kind.getResourceDomain == "covers" && kind.getResourcePath == "microblock") {
      (for {
        shapeId <- tag.get[String]("shape")
        materialId <- tag.get[String]("material")
        size <- tag.get[Int]("size")
        slotName <- tag.get[String]("slot")
        shape <- InternalRegistry.shapes.get(shapeId)
        material <- materialMap.get(materialId)
        slot <- Option(PartSlot.valueOf(slotName))
      } yield {
        val sizeModified = Math.min(if (size > 1) size >> 1 else 1, 4)
        shape.createPart(slot, sizeModified, material, false)
      }) getOrElse {
        if (registered)
          Covers.logWarn("Failed to convert part to new format " + tag)
        new PartSelfRemove
      }
    } else null

  override def createPart(kind: ResourceLocation, buf: PacketBuffer): IMultipart = null // Should never be called

  def register(): Unit = {
    MultipartRegistry.registerPartFactory(OldPartConverter, "covers:microblock")
  }
}
