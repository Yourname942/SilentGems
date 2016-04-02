package net.silentchaos512.gems.block;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.client.gui.GuiHandlerSilentGems;
import net.silentchaos512.gems.lib.Names;
import net.silentchaos512.gems.tile.TileMaterialGrader;
import net.silentchaos512.lib.block.BlockContainerSL;
import net.silentchaos512.wit.api.IWitHudInfo;

public class BlockMaterialGrader extends BlockContainerSL implements IWitHudInfo {

  public BlockMaterialGrader() {

    super(1, SilentGems.MOD_ID, Names.MATERIAL_GRADER, Material.iron);
    setHardness(3.0f);
    setResistance(100.0f);
    this.isBlockContainer = true;

    SilentGems.instance.registry.registerTileEntity(TileMaterialGrader.class,
        Names.MATERIAL_GRADER);
  }

  public EnumBlockRenderType getRenderType(IBlockState state) {

    return EnumBlockRenderType.INVISIBLE;
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {

    return new TileMaterialGrader();
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
      EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {

    if (world.isRemote) {
      return true;
    }

    TileEntity tile = world.getTileEntity(pos);
    if (tile instanceof TileMaterialGrader) {
      player.openGui(SilentGems.instance, GuiHandlerSilentGems.ID_MATERIAL_GRADER, world, pos.getX(),
          pos.getY(), pos.getZ());
    }

    return true;
  }

  @Override
  public List<String> getWitLines(IBlockState state, BlockPos pos, EntityPlayer player,
      boolean advanced) {

    TileEntity tileEntity = player.worldObj.getTileEntity(pos);
    if (tileEntity != null && tileEntity instanceof TileMaterialGrader) {
      TileMaterialGrader tile = (TileMaterialGrader) tileEntity;
      return Lists.newArrayList("Charge: " + tile.getField(0));
    }
    return null;
  }
}
