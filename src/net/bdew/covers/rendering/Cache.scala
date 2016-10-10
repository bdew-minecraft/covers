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

