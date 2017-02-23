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

package net.bdew.covers.block

import mcmultipart.api.microblock.MicroMaterial
import mcmultipart.api.slot.IPartSlot
import net.bdew.covers.microblock.shape.MicroblockShape
import net.bdew.lib.property.SimpleUnlistedProperty

case class CoverInfo(shape: MicroblockShape, slot: IPartSlot, material: MicroMaterial, size: Int)

object CoverInfoProperty extends SimpleUnlistedProperty("COVER", classOf[CoverInfo])

