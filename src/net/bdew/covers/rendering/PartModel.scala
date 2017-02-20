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

package net.bdew.covers.rendering

import java.util
import java.util.Collections
import javax.vecmath.Matrix4f

import com.google.common.base.{Function, Optional}
import com.google.common.collect.ImmutableList
import mcmultipart.api.microblock.MicroMaterial
import net.bdew.covers.block.{CoverInfoProperty, ItemCover}
import net.bdew.covers.misc.AABBHiddenFaces
import net.bdew.lib.Client
import net.bdew.lib.render.models.{SimpleBakedModelBuilder, SmartItemModel}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.client.renderer.block.model.{BakedQuad, IBakedModel, ItemCameraTransforms}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.{DefaultVertexFormats, VertexFormat}
import net.minecraft.item.ItemStack
import net.minecraft.util.{BlockRenderLayer, EnumFacing, ResourceLocation}
import net.minecraftforge.client.{ForgeHooksClient, MinecraftForgeClient}
import net.minecraftforge.client.model._
import net.minecraftforge.common.model.{IModelState, TRSRTransformation}
import net.minecraftforge.common.property.IExtendedBlockState
import org.apache.commons.lang3.tuple.Pair

object PartModel extends IModel {
  override def getTextures: util.Collection[ResourceLocation] = ImmutableList.of()
  override def bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function[ResourceLocation, TextureAtlasSprite]): IBakedModel =
    new PartBakedModel(format, state)
  override def getDefaultState: IModelState = TRSRTransformation.identity()
  override def getDependencies: util.Collection[ResourceLocation] = ImmutableList.of()
}

class PartBakedModel(vertexFormat: VertexFormat, state: IModelState) extends IBakedModel with SmartItemModel with IPerspectiveAwareModel {
  lazy val missing = {
    val builder = new SimpleBakedModelBuilder(DefaultVertexFormats.ITEM)
    builder.texture = Client.missingIcon
    builder.setTransformsFromState(state)
    builder.build()
  }

  override def getQuads(state: IBlockState, face: EnumFacing, rand: Long): util.List[BakedQuad] = {
    val ex = state.asInstanceOf[IExtendedBlockState]
    val data = ex.getValue(CoverInfoProperty)
    // fixme
    //    val bounds = ex.getValue(BoundsProperty)
    //    val boxes = CoverUtils.limitBoxes(shape.getPartBoxes(slot, size), bounds)
    if (data != null) {
      if (data.material.canRenderInLayer(data.material.getDefaultState, MinecraftForgeClient.getRenderLayer)) {
        val boxes = data.shape.getPartBoxes(data.slot, data.size)
        generateQuads(data.material, boxes, face, rand)
      } else Collections.emptyList()
    } else {
      missing.getQuads(state, face, rand)
    }
  }

  override def getItemQuads(item: ItemStack, face: EnumFacing, mode: TransformType, rand: Long): util.List[BakedQuad] = {
    ItemCover.getData(item) map { data =>
      val list = new util.ArrayList[BakedQuad]()
      for (layer <- BlockRenderLayer.values().toList.filter(l => data.material.canRenderInLayer(data.material.getDefaultState, l))) {
        ForgeHooksClient.setRenderLayer(layer)
        list.addAll(generateQuads(data.material, data.shape.getItemBoxes(data.size), face, rand))
      }
      ForgeHooksClient.setRenderLayer(null)
      list
    } getOrElse missing.getQuads(null, face, rand)
  }

  def generateQuads(material: MicroMaterial, boxes: List[AABBHiddenFaces], face: EnumFacing, rand: Long): util.List[BakedQuad] = {
    MicroblockModelHelper.getModel(material.getDefaultState, boxes, TRSRTransformation.identity()).getQuads(null, face, rand)
  }

  override def handlePerspective(cameraTransformType: TransformType): Pair[_ <: IBakedModel, Matrix4f] = {
    val tr = state.apply(Optional.of(cameraTransformType)).or(TRSRTransformation.identity)
    if (tr != TRSRTransformation.identity)
      Pair.of(this, TRSRTransformation.blockCornerToCenter(tr).getMatrix)
    else
      Pair.of(this, null)
  }

  override def getParticleTexture = Client.missingIcon
  override def isBuiltInRenderer = false
  override def isAmbientOcclusion = true
  override def isGui3d: Boolean = true
  override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT
}
