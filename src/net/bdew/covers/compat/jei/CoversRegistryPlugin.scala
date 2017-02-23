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

package net.bdew.covers.compat.jei

import java.util
import java.util.Collections

import mezz.jei.api.recipe._
import net.bdew.covers.block.ItemCover
import net.bdew.covers.items.ItemSaw
import net.bdew.covers.microblock.shape.{EdgeShape, FaceShape, MicroblockShape}
import net.bdew.covers.microblock.{InternalRegistry, MicroMaterialHelper}
import net.minecraft.block.Block
import net.minecraft.item.ItemStack

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object CoversRegistryPlugin extends IRecipeRegistryPlugin {
  val defaultMaterial = InternalRegistry.defaultMaterial

  val sawRecipes = List(
    new RecipeCutBlock(defaultMaterial),
    new RecipeCutPart(defaultMaterial, FaceShape, 4),
    new RecipeReducePart(defaultMaterial, FaceShape, 1),
    new RecipeReducePart(defaultMaterial, EdgeShape, 1)
  )

  val allRecipes = sawRecipes ++ List(
    new RecipeCombineBlock(defaultMaterial),
    new RecipeCombinePart(defaultMaterial, FaceShape, 1),
    new RecipeHollowPart(defaultMaterial, FaceShape, 1),
    new RecipeTransform(defaultMaterial, EdgeShape, 1),
    new RecipeUnreducePart(defaultMaterial, EdgeShape, 1),
    new RecipeGhostPart(defaultMaterial, FaceShape, 1)
  )

  val shapeSizes = for (shape <- InternalRegistry.shapes.values; size <- shape.validSizes) yield (shape, size)

  def makeReverse(f: (MicroblockShape, Int) => Option[(MicroblockShape, Int)]): Map[(MicroblockShape, Int), (MicroblockShape, Int)] =
    shapeSizes.flatMap(x => f.tupled(x) map (_ -> x)).toMap

  val transformReverse = makeReverse((shape, size) => shape.transform(size))
  val reduceReverse = makeReverse((shape, size) => shape.reduce(size))
  val combineReverse = makeReverse((shape, size) => shape.combine(size))
  val hollowReverse = makeReverse((shape, size) => shape.hollow(size))
  val ghostReverse = makeReverse((shape, size) => shape.ghost(size))

  override def getRecipeCategoryUids[V](focus: IFocus[V]): util.List[String] = {
    if (focus.getMode == null) return Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING)
    focus.getValue match {
      case x: ItemStack if (
        x.getItem == ItemCover
          || (x.getItem == ItemSaw && focus.getMode == IFocus.Mode.INPUT)
          || Option(Block.getBlockFromItem(x.getItem)).exists(block => InternalRegistry.isValidMaterial(block, x.getItemDamage))
        ) => Collections.singletonList(VanillaRecipeCategoryUid.CRAFTING)
      case _ => Collections.emptyList()
    }
  }

  override def getRecipeWrappers[T <: IRecipeWrapper](recipeCategory: IRecipeCategory[T]): util.List[T] =
    if (recipeCategory.getUid == VanillaRecipeCategoryUid.CRAFTING)
      getRecipes(null).asInstanceOf[util.List[T]]
    else
      Collections.emptyList()

  override def getRecipeWrappers[T <: IRecipeWrapper, V](recipeCategory: IRecipeCategory[T], focus: IFocus[V]): util.List[T] =
    if (recipeCategory.getUid == VanillaRecipeCategoryUid.CRAFTING)
      getRecipes(focus).asInstanceOf[util.List[T]]
    else
      Collections.emptyList()

  def getRecipes(focus: IFocus[_]): util.List[_ <: MicroblockRecipe] = {
    if (focus == null) return allRecipes
    if (!focus.getValue.isInstanceOf[ItemStack]) return Collections.emptyList()

    val stack = focus.getValue.asInstanceOf[ItemStack]

    if (stack.getItem == ItemSaw && focus.getMode == IFocus.Mode.INPUT) return sawRecipes

    if (stack.getItem == ItemCover) {
      ItemCover.getData(stack) filter (x => MicroMaterialHelper.hasItemStack(x.material)) map { data =>
        if (focus.getMode == IFocus.Mode.INPUT) {
          var list = List.empty[MicroblockRecipe]

          if (data.size % 2 == 0 && data.shape.validSizes.contains(data.size / 2))
            list :+= new RecipeCutPart(data.material, data.shape, data.size)

          if (data.shape.validSizes.contains(data.size * 2))
            list :+= new RecipeCombinePart(data.material, data.shape, data.size)
          else if (data.size == 4 && data.shape == FaceShape)
            list :+= new RecipeCombineBlock(data.material)

          if (data.shape.reduce(data.size).isDefined)
            list :+= new RecipeReducePart(data.material, data.shape, data.size)

          if (data.shape.combine(data.size).isDefined)
            list :+= new RecipeUnreducePart(data.material, data.shape, data.size)

          if (data.shape.hollow(data.size).isDefined)
            list :+= new RecipeHollowPart(data.material, data.shape, data.size)

          if (data.shape.transform(data.size).isDefined)
            list :+= new RecipeTransform(data.material, data.shape, data.size)

          if (data.shape.ghost(data.size).isDefined)
            list :+= new RecipeGhostPart(data.material, data.shape, data.size)

          list.asJava
        } else {
          var list = List.empty[MicroblockRecipe]

          if (data.size % 2 == 0 && data.shape.validSizes.contains(data.size / 2))
            list :+= new RecipeCombinePart(data.material, data.shape, data.size / 2)

          if (data.shape.validSizes.contains(data.size * 2))
            list :+= new RecipeCutPart(data.material, data.shape, data.size * 2)
          else if (data.size == 4 && data.shape == FaceShape)
            list :+= new RecipeCutBlock(data.material)

          list ++= reduceReverse.get((data.shape, data.size)) map { n => new RecipeReducePart(data.material, n._1, n._2) }
          list ++= combineReverse.get((data.shape, data.size)) map { n => new RecipeUnreducePart(data.material, n._1, n._2) }
          list ++= transformReverse.get((data.shape, data.size)) map { n => new RecipeTransform(data.material, n._1, n._2) }
          list ++= hollowReverse.get((data.shape, data.size)) map { n => new RecipeHollowPart(data.material, n._1, n._2) }
          list ++= ghostReverse.get((data.shape, data.size)) map { n => new RecipeGhostPart(data.material, n._1, n._2) }

          list.asJava
        }
      } getOrElse Collections.emptyList()
    } else {
      Option(Block.getBlockFromItem(stack.getItem)) flatMap {
        block =>
          InternalRegistry.getMaterial(block, stack.getItemDamage) filter (MicroMaterialHelper.hasItemStack) map { material =>
            if (focus.getMode == IFocus.Mode.INPUT)
              Collections.singletonList(new RecipeCutBlock(material))
            else
              Collections.singletonList(new RecipeCombineBlock(material))
          }
      } getOrElse Collections.emptyList()
    }
  }
}
