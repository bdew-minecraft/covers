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

import com.google.common.base.Function
import com.google.common.collect.ImmutableList
import mcmultipart.client.microblock.{IMicroModelState, MicroblockRegistryClient}
import mcmultipart.client.multipart.ISmartMultipartModel
import mcmultipart.microblock.{IMicroMaterial, Microblock}
import net.bdew.covers.items.ItemMicroblock
import net.bdew.covers.microblock.MicroblockShapeProperty
import net.bdew.covers.misc.AABBHiddenFaces
import net.bdew.lib.Client
import net.bdew.lib.render.models.{ModelUtils, SimpleBakedModelBuilder}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.{BakedQuad, ItemCameraTransforms}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.{DefaultVertexFormats, VertexFormat}
import net.minecraft.client.resources.model.IBakedModel
import net.minecraft.item.ItemStack
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.client.model._
import net.minecraftforge.common.property.IExtendedBlockState

object PartModel extends IModel {
  override def getTextures: util.Collection[ResourceLocation] = ImmutableList.of()
  override def bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function[ResourceLocation, TextureAtlasSprite]): IFlexibleBakedModel =
    new PartBakedModel(format, state)
  override def getDefaultState: IModelState = TRSRTransformation.identity()
  override def getDependencies: util.Collection[ResourceLocation] = ImmutableList.of()
}

class PartBakedModel(vertexFormat: VertexFormat, state: IModelState) extends IFlexibleBakedModel with ISmartItemModel with ISmartMultipartModel {
  lazy val missing = Client.minecraft.getBlockRendererDispatcher.getBlockModelShapes.getModelManager.getMissingModel
  val noFaces = util.EnumSet.noneOf(classOf[EnumFacing])

  def addStateToModel(model: IBakedModel): IPerspectiveAwareModel =
    new IPerspectiveAwareModel.MapWrapper(ModelUtils.makeFlexible(model), state)

  def buildModel(material: IMicroMaterial, boxes: List[AABBHiddenFaces]) = {
    val provider = MicroblockRegistryClient.getModelProviderFor(material)
    if (boxes.isEmpty || provider == null) {
      missing
    } else if (boxes.size == 1) {
      addStateToModel(provider.provideMicroModel(new IMicroModelState.Impl(material, boxes.head, boxes.head.hidden)))
    } else if (provider.isInstanceOf[MicroblockModelProvider]) {
      addStateToModel(provider.asInstanceOf[MicroblockModelProvider].provideMicroModelAdvanced(boxes))
    } else {
      val builder = new SimpleBakedModelBuilder(DefaultVertexFormats.ITEM)
      val models = boxes map (box => provider.provideMicroModel(new IMicroModelState.Impl(material, box, box.hidden)))

      builder.texture = models.head.getParticleTexture
      builder.isGui3d = true
      builder.setTransformsFromState(state)

      import scala.collection.JavaConversions._

      for (model <- models) {
        builder.addBakedQuadsGeneral(model.getGeneralQuads.toList)
        for (face <- EnumFacing.values())
          builder.addBakedQuads(face, model.getFaceQuads(face).toList)
      }

      builder.build()
    }
  }

  override def handleItemState(stack: ItemStack): IBakedModel =
    ItemMicroblock.getData(stack) map (data => buildModel(data.material, data.shape.getPartBoxes(data.shape.defaultSlot, data.size))) getOrElse missing

  override def handlePartState(state: IBlockState): IBakedModel = {
    val ex = state.asInstanceOf[IExtendedBlockState]
    val material = ex.getValue(Microblock.PROPERTY_MATERIAL)
    val shape = ex.getValue(MicroblockShapeProperty)
    val size = ex.getValue(Microblock.PROPERTY_SIZE)
    val slot = ex.getValue(Microblock.PROPERTY_SLOT)
    buildModel(material, shape.getPartBoxes(slot, size))
  }

  override def getFormat: VertexFormat = vertexFormat
  override def getParticleTexture = Client.missingIcon
  override def isBuiltInRenderer = false
  override def isAmbientOcclusion = true
  override def isGui3d: Boolean = true
  override def getItemCameraTransforms: ItemCameraTransforms = ItemCameraTransforms.DEFAULT
  override def getGeneralQuads: util.List[BakedQuad] = ImmutableList.of()
  override def getFaceQuads(p_177551_1_ : EnumFacing): util.List[BakedQuad] = ImmutableList.of()
}
