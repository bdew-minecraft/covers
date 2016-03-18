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

import mcmultipart.microblock.{Microblock, MicroblockClass}
import mcmultipart.multipart.{ISolidPart, PartSlot}
import mcmultipart.raytrace.RayTraceUtils.{RayTraceResult, RayTraceResultPart}
import mcmultipart.raytrace.{PartMOP, RayTraceUtils}
import net.bdew.covers.microblock.MicroblockShapeProperty
import net.bdew.covers.microblock.shape.MicroblockShape
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{AxisAlignedBB, EnumFacing, EnumWorldBlockLayer, Vec3}
import net.minecraftforge.common.property.{ExtendedBlockState, IExtendedBlockState}

trait PartImplementation extends Microblock with ISolidPart {
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

  override def collisionRayTrace(start: Vec3, end: Vec3): RayTraceUtils.RayTraceResultPart = {
    val res = super.collisionRayTrace(start, end)
    if (res == null)
      null
    else
      new RayTraceResultPart(new RayTraceResult(res.hit, getBounds), this)
  }

  override def canRenderInLayer(layer: EnumWorldBlockLayer): Boolean = getMicroMaterial.canRenderInLayer(layer)

  override def getStrength(player: EntityPlayer, hit: PartMOP): Float = 0.1f

  override def getSlotMask: util.EnumSet[PartSlot] = shape.getSlotMask(getSlot, getSize)

  override def isSideSolid(side: EnumFacing): Boolean = shape.isSolid(getSlot, getSize, side)

  override def createBlockState(): ExtendedBlockState =
    new ExtendedBlockState(null, Array.empty, (Microblock.PROPERTIES.toList :+ MicroblockShapeProperty).toArray)

  override def getExtendedState(state: IBlockState): IExtendedBlockState =
    super.getExtendedState(state).withProperty(MicroblockShapeProperty, shape)

  override def getModelPath: String = "covers:microblock"
}
