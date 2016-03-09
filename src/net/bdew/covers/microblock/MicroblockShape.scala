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

import java.util

import mcmultipart.multipart.PartSlot
import net.minecraft.util.{AxisAlignedBB, EnumFacing, Vec3}

abstract class MicroblockShape(val name: String) {
  /**
    * @return Size of a full block, in terms of sizes used by this shape
    */
  def blockSize: Int

  /**
    * @return Set of valid sizes, shouldn't include blockSize
    */
  def validSizes: Set[Int]

  /**
    * @return Set of valid slots that parts of this shape should occupy
    */
  def validSlots: Set[PartSlot]

  /**
    * @return default slot for this shape, used for item form
    */
  def defaultSlot: PartSlot

  /**
    * Check if a side is fully solid
    *
    * @param slot slot of the part being checked
    * @param side side being checked
    * @return true if the given part makes the side fully solid
    */
  def isSolid(slot: PartSlot, size: Int, side: EnumFacing): Boolean = false

  /**
    * @return Bounding box for the whole part of the given size in the given slot
    */
  def getBoundingBox(slot: PartSlot, size: Int): AxisAlignedBB

  /**
    * @return List of bounding boxes that form the part of the given size in the given slot
    */
  def getPartBoxes(slot: PartSlot, size: Int): List[AxisAlignedBB] = List(getBoundingBox(slot, size))

  /**
    * Determine slot for a new part
    *
    * @param vec  hit vector of the click that would place the part
    * @param side the side that was clicked
    * @return slot that the new part should take or None if the click shouldn't place a new part
    */
  def getSlotFromHit(vec: Vec3, side: EnumFacing): Option[PartSlot]

  /**
    * @param slot main slot of the part
    * @param size size of the part
    * @return EnumSet of the slots this part actually occupies
    */
  def getSlotMask(slot: PartSlot, size: Int): util.EnumSet[PartSlot] = util.EnumSet.of(slot)

  /**
    * Describe how this shape converts to smaller shapes (e.g. Face -> Edge)
    *
    * @param size size of current part
    * @return Shape and size of new part, or None if not valid
    */
  def reduce(size: Int): Option[(MicroblockShape, Int)] = None

  /**
    * Describe how this shape converts to bigger shapes (e.g. Edge -> Face)
    *
    * @param size size of current part
    * @return Shape and size of new part, or None if not valid
    */
  def combine(size: Int): Option[(MicroblockShape, Int)] = None

  /**
    * Describe how this shape "transforms" to another shape (e.g. Edge <-> Center)
    *
    * @param size size of current part
    * @return Shape and size of new part, or None if not valid
    */
  def transform(size: Int): Option[(MicroblockShape, Int)] = None

  /**
    * Describe how this shape changes to a hollow form (e.g. Face <-> Hollow Face)
    *
    * @param size size of current part
    * @return Shape and size of new part, or None if not valid
    */
  def hollow(size: Int): Option[(MicroblockShape, Int)] = None
}
