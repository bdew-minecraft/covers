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

import mcmultipart.client.microblock.{IMicroModelState, MicroblockRegistryClient}
import net.bdew.covers.items.ItemMicroblock
import net.bdew.covers.microblock.MicroblockLocation
import net.bdew.lib.Client
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.client.model.pipeline.WorldRendererConsumer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

object PartPlacementRender {
  val noFaces = util.EnumSet.noneOf(classOf[EnumFacing])

  def init(): Unit = {
    MinecraftForge.EVENT_BUS.register(this)
  }

  @SubscribeEvent
  def onRender(ev: RenderWorldLastEvent): Unit = {
    for {
      player <- Option(Client.player)
      world <- Option(Client.world)
      stack <- Option(player.inventory.getCurrentItem) if stack.getItem == ItemMicroblock
      data <- ItemMicroblock.getData(stack)
      place <- MicroblockLocation.calculate(world, player.rayTrace(Client.minecraft.playerController.getBlockReachDistance, ev.partialTicks), data.shape, data.size, data.material, true)
    } {
      val px = player.lastTickPosX + (player.posX - player.lastTickPosX) * ev.partialTicks
      val py = player.lastTickPosY + (player.posY - player.lastTickPosY) * ev.partialTicks
      val pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * ev.partialTicks

      GlStateManager.pushMatrix()
      GL11.glTranslated(-px, -py, -pz)
      GL11.glTranslatef(place.pos.getX, place.pos.getY, place.pos.getZ)
      GL11.glColor4f(1, 1, 1, 0.8f)
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      GL11.glEnable(GL11.GL_BLEND)

      val provider = MicroblockRegistryClient.getModelProviderFor(data.material)

      import scala.collection.JavaConversions._

      val quads = place.part.shape.getPartBoxes(place.part.getSlot, place.part.getSize) flatMap { bb =>
        val model = provider.provideMicroModel(new IMicroModelState.Impl(place.part.getMicroMaterial, bb, bb.hidden))
        model.getGeneralQuads ++ EnumFacing.values().flatMap(model.getFaceQuads)
      }

      val T = Tessellator.getInstance()
      val W = T.getWorldRenderer
      W.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
      val consumer = new WorldRendererConsumer(W)

      quads foreach (_.pipe(consumer))

      T.draw()

      GL11.glColor4f(1, 1, 1, 1)
      GL11.glDisable(GL11.GL_BLEND)
      GL11.glPopMatrix()
    }
  }
}
