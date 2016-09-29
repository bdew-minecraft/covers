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

package net.bdew.covers.compat.jei

import mcmultipart.microblock.IMicroMaterial
import mezz.jei.api.ingredients.IIngredients
import net.bdew.covers.items.{ItemMicroblock, ItemSaw}
import net.bdew.covers.microblock.shape.{FaceShape, MicroblockShape}
import net.minecraft.item.ItemStack

import scala.collection.JavaConversions._

// 2 slabs => block
class RecipeCombineBlock(material: IMicroMaterial) extends MicroblockRecipe {
  override def getIngredients(ingredients: IIngredients): Unit = {
    val part = ItemMicroblock.makeStack(material, FaceShape, 4)
    ingredients.setInputs(classOf[ItemStack], List(part, part))
    ingredients.setOutput(classOf[ItemStack], material.getItem)
  }
}

// 2 parts => bigger part
class RecipeCombinePart(material: IMicroMaterial, shape: MicroblockShape, size: Int) extends MicroblockRecipe {
  override def getIngredients(ingredients: IIngredients): Unit = {
    val input = ItemMicroblock.makeStack(material, shape, size)
    ingredients.setInputs(classOf[ItemStack], List(input, input))
    ingredients.setOutput(classOf[ItemStack], ItemMicroblock.makeStack(material, shape, size * 2, 1))
  }
}

// saw + block => 2 slabs
class RecipeCutBlock(material: IMicroMaterial) extends MicroblockRecipe {
  override def getIngredients(ingredients: IIngredients): Unit = {
    ingredients.setInputs(classOf[ItemStack], List(new ItemStack(ItemSaw), null, null, material.getItem))
    ingredients.setOutput(classOf[ItemStack], ItemMicroblock.makeStack(material, FaceShape, 4, 2))
  }
}

// saw + part => smaller part
class RecipeCutPart(material: IMicroMaterial, shape: MicroblockShape, size: Int) extends MicroblockRecipe {
  override def getIngredients(ingredients: IIngredients): Unit = {
    ingredients.setInputs(classOf[ItemStack], List[ItemStack](
      new ItemStack(ItemSaw), null, null, ItemMicroblock.makeStack(material, shape, size)
    ))
    ingredients.setOutput(classOf[ItemStack], ItemMicroblock.makeStack(material, shape, size / 2, 2))
  }
}

// 8 parts in hollow pattern => hollow version
class RecipeHollowPart(material: IMicroMaterial, shape: MicroblockShape, size: Int) extends MicroblockRecipe {
  override def getIngredients(ingredients: IIngredients): Unit = {
    val (newShape, newSize) = shape.hollow(size).getOrElse(sys.error("Invalid recipe (%s,%s)".format(shape, size)))
    val input = ItemMicroblock.makeStack(material, shape, size)

    ingredients.setInputs(classOf[ItemStack], List(
      input, input, input,
      input, null, input,
      input, input, input
    ))

    ingredients.setOutput(classOf[ItemStack], ItemMicroblock.makeStack(material, newShape, newSize, 8))
  }
}

// saw above part => smaller shape
class RecipeReducePart(material: IMicroMaterial, shape: MicroblockShape, size: Int) extends MicroblockRecipe {
  override def getIngredients(ingredients: IIngredients): Unit = {
    val (newShape, newSize) = shape.reduce(size).getOrElse(sys.error("Invalid recipe (%s,%s)".format(shape, size)))
    ingredients.setInputs(classOf[ItemStack], List(
      new ItemStack(ItemSaw), ItemMicroblock.makeStack(material, shape, size)
    ))
    ingredients.setOutput(classOf[ItemStack], ItemMicroblock.makeStack(material, newShape, newSize, 2))
  }
}

// part above part => bigger shape
class RecipeUnreducePart(material: IMicroMaterial, shape: MicroblockShape, size: Int) extends MicroblockRecipe {
  override def getIngredients(ingredients: IIngredients): Unit = {
    val (newShape, newSize) = shape.combine(size).getOrElse(sys.error("Invalid recipe (%s,%s)".format(shape, size)))
    val input = ItemMicroblock.makeStack(material, shape, size)
    ingredients.setInputs(classOf[ItemStack], List(input, null, null, input))
    ingredients.setOutput(classOf[ItemStack], ItemMicroblock.makeStack(material, newShape, newSize, 1))
  }
}

// Part alone => different shape
class RecipeTransform(material: IMicroMaterial, shape: MicroblockShape, size: Int) extends MicroblockRecipe {
  override def getIngredients(ingredients: IIngredients): Unit = {
    val (newShape, newSize) = shape.transform(size).getOrElse(sys.error("Invalid recipe (%s,%s)".format(shape, size)))
    ingredients.setInput(classOf[ItemStack], ItemMicroblock.makeStack(material, shape, size))
    ingredients.setOutput(classOf[ItemStack], ItemMicroblock.makeStack(material, newShape, newSize))
  }
}
