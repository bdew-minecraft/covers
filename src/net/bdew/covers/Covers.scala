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

package net.bdew.covers

import java.io.File

import net.bdew.covers.config.{Config, Items, TuningLoader}
import net.bdew.covers.recipes.{RecipeSplitBlock, Recipes}
import net.bdew.covers.transition.OldPartConverter
import net.minecraft.item.crafting.CraftingManager
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event._
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.Logger

@Mod(modid = Covers.modId, version = "COVERS_VER", name = "Simple Covers", dependencies = "required-after:mcmultipart;required-after:bdlib", modLanguage = "scala")
object Covers {
  var log: Logger = null
  var instance = this

  final val modId = "covers"

  var configDir: File = null

  def logDebug(msg: String, args: Any*) = log.debug(msg.format(args: _*))
  def logInfo(msg: String, args: Any*) = log.info(msg.format(args: _*))
  def logWarn(msg: String, args: Any*) = log.warn(msg.format(args: _*))
  def logError(msg: String, args: Any*) = log.error(msg.format(args: _*))
  def logWarnException(msg: String, t: Throwable, args: Any*) = log.warn(msg.format(args: _*), t)
  def logErrorException(msg: String, t: Throwable, args: Any*) = log.error(msg.format(args: _*), t)

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    log = event.getModLog
    configDir = new File(event.getModConfigurationDirectory, "SimpleCovers")
    TuningLoader.loadConfigFiles()
    Items.load()
    OldPartConverter.register()
    if (event.getSide.isClient) Config.load(new File(configDir, "client.config"))
    if (event.getSide == Side.CLIENT) CoversClient.preInit()
  }

  @EventHandler
  def init(event: FMLInitializationEvent) {
    TuningLoader.loadDelayed()
    Creative.init()
    CraftingManager.getInstance().addRecipe(RecipeSplitBlock)
    Recipes.register()
    if (event.getSide == Side.CLIENT) CoversClient.init()
  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) {
  }
}