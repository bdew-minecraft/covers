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

package net.bdew.covers.config

import java.io.File

import net.minecraftforge.common.config.Configuration

object Config {

  object ShowMode extends Enumeration {
    val ALL, MINIMAL, NONE = Value
  }

  var jeiShowMode = ShowMode.ALL

  def load(cfg: File) {
    val c = new Configuration(cfg)
    c.load()
    try {
      val showModeProp = c.get(Configuration.CATEGORY_GENERAL, "JEI Display Mode", "ALL", "ALL will show everything, MINIMAL will show each shape once, NONE will hide all microblocks from JEI", Array("ALL, MINIMAL, NONE"))
      jeiShowMode = ShowMode.withName(showModeProp.getString)
    } finally {
      c.save()
    }
  }
}