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

import mcmultipart.api.microblock.MicroMaterial
import mcmultipart.api.multipart.MultipartHelper
import mcmultipart.api.slot.IPartSlot
import net.bdew.covers.microblock.shape.MicroblockShape
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{BlockPos, RayTraceResult, Vec3d}
import net.minecraft.world.{IBlockAccess, World}

case class MicroblockLocation(world: IBlockAccess, pos: BlockPos, slot: IPartSlot, state: IBlockState, intoContainer: Boolean)

object MicroblockLocation {
  def calculate(world: World, mop: RayTraceResult, shape: MicroblockShape, size: Int, material: MicroMaterial, client: Boolean): Option[MicroblockLocation] = {
    if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK)
      calculate(world, mop.getBlockPos, mop.hitVec.subtract(new Vec3d(mop.getBlockPos)), mop.sideHit, shape, size, material, client)
    else
      None
  }

  def calculate(world: World, blockPosOriginal: BlockPos, hitVecOriginal: Vec3d, hitFaceOriginal: EnumFacing, shape: MicroblockShape, size: Int, material: MicroMaterial, client: Boolean): Option[MicroblockLocation] = {
    var blockPos = blockPosOriginal
    var place = hitVecOriginal
    var hitFace = hitFaceOriginal

    // If targeting the side of a block - switch to the adjacent block

    if (place.x == 0) {
      place = place.addVector(1, 0, 0)
      blockPos = blockPos.add(-1, 0, 0)
      hitFace = hitFace.getOpposite
    } else if (place.x == 1) {
      place = place.addVector(-1, 0, 0)
      blockPos = blockPos.add(1, 0, 0)
      hitFace = hitFace.getOpposite
    } else if (place.y == 0) {
      place = place.addVector(0, 1, 0)
      blockPos = blockPos.add(0, -1, 0)
      hitFace = hitFace.getOpposite
    } else if (place.y == 1) {
      place = place.addVector(0, -1, 0)
      blockPos = blockPos.add(0, 1, 0)
      hitFace = hitFace.getOpposite
    } else if (place.z == 0) {
      place = place.addVector(0, 0, 1)
      blockPos = blockPos.add(0, 0, -1)
      hitFace = hitFace.getOpposite
    } else if (place.z == 1) {
      place = place.addVector(0, 0, -1)
      blockPos = blockPos.add(0, 0, 1)
      hitFace = hitFace.getOpposite
    }

    shape.getSlotFromHit(place, hitFace) flatMap { slot =>
      val state = shape.createBlockState(slot, material, size)
      if (world.isAirBlock(blockPos) || world.getBlockState(blockPos).getBlock.isReplaceable(world, blockPos))
        Some(MicroblockLocation(world, blockPos, slot, state, false))
      else if (MultipartHelper.addPart(world, blockPos, slot, state, true))
        Some(MicroblockLocation(world, blockPos, slot, state, true))
      else
        None
    }
  }
}
