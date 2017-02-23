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

package net.bdew.covers.microblock.shape

import java.util
import java.util.Collections

import mcmultipart.api.microblock.{MicroMaterial, MicroblockType}
import mcmultipart.api.slot.IPartSlot
import net.bdew.covers.block.{BlockCover, CoverInfo, CoverInfoProperty, ItemCover}
import net.bdew.covers.misc.AABBHiddenFaces
import net.bdew.lib.Misc
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.util.math.{AxisAlignedBB, RayTraceResult, Vec3d}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.property.IExtendedBlockState

abstract class MicroblockShape(val name: String) extends MicroblockType {
  setRegistryName("covers", name)

  /**
    * @return Set of valid sizes, shouldn't include blockSize
    */
  def validSizes: Set[Int] = Set(1, 2, 4)

  /**
    * @return Set of valid slots that parts of this shape should occupy
    */
  def validSlots: Set[IPartSlot]

  /**
    * @return default slot for this shape, used for item form
    */
  def defaultSlot: IPartSlot

  /**
    * Check if a side is fully solid
    *
    * @param slot slot of the part being checked
    * @param side side being checked
    * @return true if the given part makes the side fully solid
    */
  def isSolid(slot: IPartSlot, size: Int, side: EnumFacing): Boolean = false

  /**
    * @return Bounding box for the whole part of the given size in the given slot
    */
  def getBoundingBox(slot: IPartSlot, size: Int): AxisAlignedBB

  /**
    * @return List of bounding boxes that form the part of the given size in the given slot
    */
  def getPartBoxes(slot: IPartSlot, size: Int): List[AABBHiddenFaces] = List(AABBHiddenFaces.withHiddenFaces(getBoundingBox(slot, size)))

  /**
    * @return List of bounding boxes that form the part of the given size in the given slot
    */
  def getItemBoxes(size: Int): List[AABBHiddenFaces]

  /**
    * Determine slot for a new part
    *
    * @param vec  hit vector of the click that would place the part
    * @param side the side that was clicked
    * @return slot that the new part should take or None if the click shouldn't place a new part
    */
  def getSlotFromHit(vec: Vec3d, side: EnumFacing): Option[IPartSlot]

  /**
    * @param slot main slot of the part
    * @param size size of the part
    * @return Set of the slots this part actually occupies
    */
  def getSlotMask(slot: IPartSlot, size: Int): util.Set[IPartSlot] = Collections.singleton(slot)

  /**
    * @param slot main slot of the part
    * @param size size of the part
    * @return Set of the slots this part prevents other parts from taking
    */
  def getShadowedSlots(slot: IPartSlot, size: Int): util.Set[IPartSlot]

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

  /**
    * Describe how this shape changes to a ghost form (e.g. Face <-> Ghost Face)
    *
    * @param size size of current part
    * @return Shape and size of new part, or None if not valid
    */
  def ghost(size: Int): Option[(MicroblockShape, Int)] = None

  def createBlockState(slot: IPartSlot, material: MicroMaterial, size: Int) =
    BlockCover.getDefaultState.asInstanceOf[IExtendedBlockState].withProperty(CoverInfoProperty, CoverInfo(this, slot, material, size))

  /**
    * Remove the area covered by this part from a bounding box
    *
    * @return new bounding box, that doesn't include this part
    */
  def exclusionBox(slot: IPartSlot, size: Int, box: AxisAlignedBB, sides: Set[EnumFacing]): AxisAlignedBB

  //   ==== MicroblockType ====

  override val getMaxSize = validSizes.max

  override def getSize(stack: ItemStack): Int = {
    if (stack.getItem == ItemCover)
      ItemCover.getSize(stack)
    else -1
  }

  override def getMaterial(stack: ItemStack): MicroMaterial =
    if (stack.getItem == ItemCover)
      ItemCover.getMaterial(stack)
    else null

  override def getLocalizedName(material: MicroMaterial, size: Int): String =
    Misc.toLocalF("bdew.covers." + name + "." + size, material.getLocalizedName)

  override def createDrops(material: MicroMaterial, size: Int): util.List[ItemStack] =
    Collections.singletonList(ItemCover.makeStack(material, this, size))

  override def createStack(material: MicroMaterial, size: Int): ItemStack =
    ItemCover.makeStack(material, this, size)

  override def drawPlacement(world: IBlockAccess, player: EntityPlayer, stack: ItemStack, hit: RayTraceResult): Unit = ???

  override def place(world: World, player: EntityPlayer, stack: ItemStack, hit: RayTraceResult): Boolean = ???
}

abstract class MicroblockShapeImpl[T <: IPartSlot](name: String, slotClass: Class[T], validSlots1: Set[T], val defaultSlot1: T) extends MicroblockShape(name) {
  override def validSlots: Set[IPartSlot] = validSlots1.toSet[IPartSlot]
  // why the fuck are scala sets invariant?..
  override def defaultSlot: IPartSlot = defaultSlot1

  def validateSlot(s: IPartSlot): T = {
    require(validSlots.contains(s))
    if (slotClass.isInstance(s))
      slotClass.cast(s)
    else
      sys.error(s"Invalid slot for shape $name: $s (${ s.getClass.getName })")
  }
}
