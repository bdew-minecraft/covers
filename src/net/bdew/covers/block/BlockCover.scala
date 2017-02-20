package net.bdew.covers.block

import java.util

import mcmultipart.api.container.IPartInfo
import net.bdew.lib.block.{BaseBlock, HasItemBlock, HasTE}
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.util.{BlockRenderLayer, EnumFacing, EnumHand}
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.{ChunkCache, IBlockAccess, World}
import net.minecraftforge.common.property.{IExtendedBlockState, IUnlistedProperty}

object BlockCover extends BaseBlock("cover", Material.WOOD) with HasTE[TileCover] with HasItemBlock {
  override val TEClass = classOf[TileCover]
  override val itemBlockInstance: ItemBlock = ItemCover
  override def getUnlistedProperties: List[IUnlistedProperty[_]] = List(CoverInfoProperty)

  setHardness(0.1f)

  def getTile(part: IPartInfo) = part.getTile.asInstanceOf[TileCover]

  // Override the default implementation in HasTE to prevent it from recreating the TE,
  // it causes weirdness in the checks right after the TE is converted to a container by MCMP,
  // and the re-created TE will be 100% useless anyway as we'd loose all data about the part
  override def getTE(w: IBlockAccess, pos: BlockPos): Option[TileCover] = {
    val t = w match {
      case ww: ChunkCache =>
        ww.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK)
      case _ =>
        w.getTileEntity(pos)
    }
    if (t != null && t.isInstanceOf[TileCover])
      Some(t.asInstanceOf[TileCover])
    else
      None
  }

  override def getExtendedStateFromTE(state: IExtendedBlockState, world: IBlockAccess, pos: BlockPos, te: TileCover): IExtendedBlockState =
    state.withProperty(CoverInfoProperty, te.data)

  override def createTileEntity(world: World, state: IBlockState): TileEntity = {
    val te = new TileCover()
    CoverInfoProperty.get(state).foreach(te.data = _)
    te
  }

  override def getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand)

  def getData(world: IBlockAccess, pos: BlockPos) = getTE(world, pos).flatMap(x => Option(x.data))

  override def isOpaqueCube(state: IBlockState): Boolean = false
  override def isFullCube(state: IBlockState): Boolean = false

  val EMPTY_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0)

  override def getBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos): AxisAlignedBB =
    getData(world, pos).map(d => d.shape.getBoundingBox(d.slot, d.size)).getOrElse(EMPTY_AABB)

  override def getCollisionBoundingBox(blockState: IBlockState, world: IBlockAccess, pos: BlockPos): AxisAlignedBB =
    getData(world, pos).map(d => d.shape.getBoundingBox(d.slot, d.size)).getOrElse(EMPTY_AABB)

  override def addCollisionBoxToList(state: IBlockState, world: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: util.List[AxisAlignedBB], entityIn: Entity, p_185477_7_ : Boolean): Unit =
    super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entityIn, p_185477_7_)

  override def isSideSolid(base_state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean =
    getData(world, pos).exists(d => d.shape.isSolid(d.slot, d.size, side))

  override def canRenderInLayer(state: IBlockState, layer: BlockRenderLayer): Boolean =
    true // No access to our material data here, so we return true for all layers, and later check in the model
}
