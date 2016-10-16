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

import java.io.{File, FileWriter}

import net.bdew.covers.Covers
import net.bdew.covers.microblock.InternalRegistry
import net.bdew.lib.recipes._
import net.bdew.lib.recipes.gencfg._
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraftforge.oredict.OreDictionary

object Tuning extends ConfigSection

object TuningLoader {
  case class MaterialRegisterBlock(spec: StackRef) extends ConfigStatement

  var materialBlocks = List.empty[StackRef]

  class Parser extends RecipeParser with GenericConfigParser {
    def stAddMaterial = "AddMaterial" ~> spec ^^ MaterialRegisterBlock
    override def configStatement: Parser[ConfigStatement] = super.configStatement | stAddMaterial
  }

  class Loader extends RecipeLoader with GenericConfigLoader {
    val cfgStore = Tuning

    override def newParser() = new Parser()
    override def processConfigStatement(s: ConfigStatement): Unit = s match {
      case MaterialRegisterBlock(sp) =>
        materialBlocks :+= sp
      case _ => super.processConfigStatement(s)
    }
  }

  val loader = new Loader

  def loadDelayed() = loader.processRecipeStatements()

  def loadConfigFiles() {
    if (!Covers.configDir.exists()) {
      Covers.configDir.mkdir()
      val nl = System.getProperty("line.separator")
      val f = new FileWriter(new File(Covers.configDir, "readme.txt"))
      f.write("Any .cfg files in this directory will be loaded after the internal configuration, in alphabetic order" + nl)
      f.write("Files in 'overrides' directory with matching names cab be used to override internal configuration" + nl)
      f.close()
    }

    RecipesHelper.loadConfigs(
      modName = "Simple Covers",
      listResource = "/assets/covers/config/files.lst",
      configDir = Covers.configDir,
      resBaseName = "/assets/covers/config/",
      loader = loader)
  }

  def processMaterials(): Unit = {
    for (sp <- materialBlocks) {
      val stacks = loader.getAllConcreteStacks(sp)
      if (stacks.isEmpty) Covers.logWarn("Material block not found - %s", sp)
      for (stack <- stacks) {
        if (stack.getItem.isInstanceOf[ItemBlock]) {
          Covers.logDebug("Registering multiblock material from %s", stack)
          if (stack.getItemDamage == OreDictionary.WILDCARD_VALUE) {
            Covers.logDebug("Result meta is unset, defaulting to 0")
            stack.setItemDamage(0)
          }
          val block = Block.getBlockFromItem(stack.getItem)
          InternalRegistry.registerMaterial(block, stack.getItemDamage)
        } else Covers.logWarn("Item %s is not a block - skipping material registration", stack)
      }
    }
    materialBlocks = List.empty
  }
}

