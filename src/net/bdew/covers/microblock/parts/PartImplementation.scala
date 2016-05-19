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

package net.bdew.covers.microblock.parts

import java.util

import mcmultipart.MCMultiPartMod
import mcmultipart.client.microblock.MicroblockRegistryClient
import mcmultipart.client.multipart.AdvancedEffectRenderer
import mcmultipart.microblock.{Microblock, MicroblockClass}
import mcmultipart.multipart._
import mcmultipart.raytrace.PartMOP
import mcmultipart.raytrace.RayTraceUtils.{AdvancedRayTraceResult, AdvancedRayTraceResultPart}
import net.bdew.covers.microblock.shape.MicroblockShape
import net.bdew.covers.microblock.{BoundsProperty, MicroblockShapeProperty}
import net.bdew.covers.misc.{CoverUtils, FacesToSlot}
import net.bdew.lib.Misc
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.{AxisAlignedBB, Vec3d}
import net.minecraft.util.{BlockRenderLayer, EnumFacing, ResourceLocation}
import net.minecraftforge.common.property.{ExtendedBlockState, IExtendedBlockState}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

trait PartImplementation extends Microblock with ISolidPart with INormallyOccludingPart {
  def shape: MicroblockShape

  override def getMicroClass: MicroblockClass = shape

  override def getBounds: AxisAlignedBB = shape.getBoundingBox(getSlot, getSize)

  override def getRenderBoundingBox: AxisAlignedBB = shape.getBoundingBox(getSlot, getSize)

  override def addCollisionBoxes(mask: AxisAlignedBB, list: util.List[AxisAlignedBB], collidingEntity: Entity): Unit = {
    for (bb <- shape.getPartBoxes(getSlot, getSize) if mask.intersectsWith(bb))
      list.add(bb)
  }

  override def addSelectionBoxes(list: util.List[AxisAlignedBB]): Unit = {
    for (bb <- shape.getPartBoxes(getSlot, getSize))
      list.add(bb)
  }

  override def collisionRayTrace(start: Vec3d, end: Vec3d): AdvancedRayTraceResultPart = {
    val res = super.collisionRayTrace(start, end)
    if (res == null)
      null
    else
      new AdvancedRayTraceResultPart(new AdvancedRayTraceResult(res.hit, getBounds), this)
  }

  override def canRenderInLayer(layer: BlockRenderLayer): Boolean = getMicroMaterial.canRenderInLayer(layer)

  override def getStrength(player: EntityPlayer, hit: PartMOP): Float = 0.1f

  override def getSlotMask: util.EnumSet[PartSlot] = shape.getSlotMask(getSlot, getSize)

  override def isSideSolid(side: EnumFacing): Boolean = shape.isSolid(getSlot, getSize, side)

  override def createBlockState(): ExtendedBlockState =
    new ExtendedBlockState(MCMultiPartMod.multipart, Array.empty, (Microblock.PROPERTIES.toList :+ MicroblockShapeProperty :+ BoundsProperty).toArray)

  override def getExtendedState(state: IBlockState): IExtendedBlockState =
    super.getExtendedState(state).withProperty(MicroblockShapeProperty, shape).withProperty(BoundsProperty, calcBounds())

  override def occlusionTest(part: IMultipart): Boolean = {
    super.occlusionTest(part) && (
      if (part.isInstanceOf[ISlottedPart]) {
        val slots = part.asInstanceOf[ISlottedPart].getSlotMask
        slots.retainAll(shape.getShadowedSlots(getSlot, getSize))
        slots.isEmpty
      } else true)
  }

  def calcBounds(): AxisAlignedBB = {
    var box = new AxisAlignedBB(0, 0, 0, 1, 1, 1)
    if (getContainer != null && getContainer.getParts.size() >= 1) {
      import scala.collection.JavaConversions._
      for (part <- Misc.filterType(getContainer.getParts, classOf[PartImplementation]) if part != this && CoverUtils.shouldPartAffectBounds(this, part)) {
        box = part.shape.exclusionBox(part.getSlot, part.getSize, box, FacesToSlot.inverted(this.getSlot))
      }
    }
    box
  }

  @SideOnly(Side.CLIENT)
  override def addHitEffects(hit: PartMOP, effectRenderer: AdvancedEffectRenderer): Boolean = {
    val provider = MicroblockRegistryClient.getModelProviderFor(getMicroMaterial)
    val model = provider.provideMicroModel(getMicroMaterial, getBounds, util.EnumSet.noneOf(classOf[EnumFacing]))
    effectRenderer.addBlockHitEffects(getPos, hit, getBounds, model.getParticleTexture)
    true
  }

  @SideOnly(Side.CLIENT)
  override def addDestroyEffects(effectRenderer: AdvancedEffectRenderer): Boolean = {
    val provider = MicroblockRegistryClient.getModelProviderFor(getMicroMaterial)
    val model = provider.provideMicroModel(getMicroMaterial, getBounds, util.EnumSet.noneOf(classOf[EnumFacing]))
    effectRenderer.addBlockDestroyEffects(getPos, model.getParticleTexture)
    true
  }

  override def getModelPath: ResourceLocation = new ResourceLocation("covers", "microblock")

  override def addOcclusionBoxes(list: util.List[AxisAlignedBB]): Unit = {
    for (bb <- shape.getPartBoxes(getSlot, getSize))
      list.add(bb)
  }
}
