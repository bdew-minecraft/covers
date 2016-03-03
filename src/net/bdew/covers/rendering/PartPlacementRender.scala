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

import net.bdew.covers.items.ItemMicroblock
import net.bdew.covers.microblock.MicroblockPlacement
import net.bdew.lib.Client
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.client.resources.model.ModelRotation
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.client.model.pipeline.WorldRendererConsumer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

object PartPlacementRender {
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
      place <- MicroblockPlacement.calculate(world, player.rayTrace(Client.minecraft.playerController.getBlockReachDistance, ev.partialTicks), data)
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

      val m = place.part.data.material.getModel(place.part.data, ModelRotation.X0_Y0)
      import scala.collection.JavaConversions._

      val T = Tessellator.getInstance()
      val W = T.getWorldRenderer
      W.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
      val consumer = new WorldRendererConsumer(W)

      for (quad <- m.getGeneralQuads ++ EnumFacing.values().flatMap(m.getFaceQuads)) {
        quad.pipe(consumer)
      }

      T.draw()

      GL11.glColor4f(1, 1, 1, 1)
      GL11.glDisable(GL11.GL_BLEND)
      GL11.glPopMatrix()
    }
  }
}
