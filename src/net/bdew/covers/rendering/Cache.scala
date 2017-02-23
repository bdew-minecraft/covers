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

package net.bdew.covers.rendering

import java.util

class Cache[K, V](cacheSize: Int, f: K => V) {
  var hits = 0
  var misses = 0

  val map = new util.LinkedHashMap[K, V](16, 0.75f, true) {
    override protected def removeEldestEntry(eldest: util.Map.Entry[K, V]): Boolean = size >= cacheSize
  }

  def apply(k: K): V = {
    this.synchronized {
      if (map.containsKey(k)) {
        hits += 1
        map.get(k)
      } else {
        misses += 1
        val v = f(k)
        map.put(k, v)
        v
      }
    }
  }

  def resetStats(): Unit = {
    this.synchronized {
      hits = 0
      misses = 0
    }
  }

  def stats = this.synchronized {
    (hits, misses)
  }
}

