package net.silentchaos512.gems.skills;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.api.ITool;
import net.silentchaos512.gems.api.lib.EnumMaterialTier;
import net.silentchaos512.gems.handler.PlayerDataHandler;
import net.silentchaos512.gems.handler.PlayerDataHandler.PlayerData;
import net.silentchaos512.gems.item.ModItems;
import net.silentchaos512.gems.item.tool.ItemGemAxe;
import net.silentchaos512.gems.util.ToolHelper;

public class SkillLumberjack extends ToolSkill {

  public static final float DIG_SPEED_MULTIPLIER = 0.25f;
  public static final int CHAOS_COST = 250;

  public static final SkillLumberjack INSTANCE = new SkillLumberjack();

  public void onGetBreakSpeed(PlayerEvent.BreakSpeed event) {

    if (!event.getState().getBlock().isWood(event.getEntityPlayer().worldObj, event.getPos())) {
      return;
    }

    PlayerData data = PlayerDataHandler.get(event.getEntityPlayer());
    int cost = event.getEntityPlayer().capabilities.isCreativeMode ? 0 : CHAOS_COST;
    if (data.chaos >= cost) {
      event.setNewSpeed(event.getNewSpeed() * DIG_SPEED_MULTIPLIER);
    }
  }

  @Override
  public boolean activate(ItemStack tool, EntityPlayer player, BlockPos pos) {

    World world = player.worldObj;
    IBlockState state = world.getBlockState(pos);

    // Must be an axe
    if (tool.getItem() != ModItems.axe || state == null || state.getBlock() == null) {
      return false;
    }

    // Must be super tool with special enabled.
    if (ToolHelper.getToolTier(tool) != EnumMaterialTier.SUPER
        || !ToolHelper.isSpecialAbilityEnabled(tool)) {
      return false;
    }

    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    if (state.getBlock().isWood(world, pos) || state.getMaterial() == Material.SPONGE) {
      if (detectTree(world, x, y, z, state.getBlock())) {
        // Don't allow in creative mode.
        if (player.capabilities.isCreativeMode) {
          return false;
        }

        // Does player have enough chaos?
        PlayerData data = PlayerDataHandler.get(player);
        int cost = CHAOS_COST;
        if (data.chaos >= cost) {
          data.drainChaos(cost);
        } else {
          return false;
        }

        int blocksBroken = breakTree(world, x, y, z, x, y, z, tool, state, player);
        ToolHelper.incrementStatBlocksMined(tool, blocksBroken);
        return true;
      }
    }

    return false;
  }

  public static boolean detectTree(World world, int x, int y, int z, Block wood) {

    int height = y;
    boolean foundTop = false;
    do {
      ++height;
      Block block = world.getBlockState(new BlockPos(x, height, z)).getBlock();
      if (block != wood) {
        --height;
        foundTop = true;
      }
    } while (!foundTop);

    int numLeaves = 0;
    if (height - y < 50) {
      for (int xPos = x - 1; xPos <= x + 1; xPos++) {
        for (int yPos = height - 1; yPos <= height + 1; yPos++) {
          for (int zPos = z - 1; zPos <= z + 1; zPos++) {
            BlockPos pos = new BlockPos(xPos, yPos, zPos);
            IBlockState leaves = world.getBlockState(pos);
            if (leaves != null && leaves.getBlock().isLeaves(world.getBlockState(pos), world, pos))
              ++numLeaves;
          }
        }
      }
    }

    return numLeaves > 3;
  }

  private static int breakTree(World world, int x, int y, int z, int xStart, int yStart, int zStart,
      ItemStack tool, IBlockState state, EntityPlayer player) {

    int blocksBroken = 0;

    ItemGemAxe axe = (ItemGemAxe) tool.getItem();
    Block block = state.getBlock();
    BlockPos pos = new BlockPos(x, y, z);

    Block localBlock;
    IBlockState localState;
    BlockPos localPos;

    int meta = block.getMetaFromState(state);
    int localMeta;

    for (int xPos = x - 1; xPos <= x + 1; ++xPos) {
      for (int yPos = y; yPos <= y + 1; ++yPos) {
        for (int zPos = z - 1; zPos <= z + 1; ++zPos) {
          localPos = new BlockPos(xPos, yPos, zPos);
          localState = world.getBlockState(localPos);
          localBlock = localState.getBlock();
          if (block == localBlock) {
            int harvestLevel = localBlock.getHarvestLevel(localState);
            float localHardness = localBlock == null ? Float.MAX_VALUE
                : localState.getBlockHardness(world, localPos);

            if (harvestLevel <= axe.getHarvestLevel(tool, "axe") && localHardness >= 0) {
              boolean cancel = false;

              // Block break event
              BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, localPos, localState,
                  player);
              // event.setCanceled(cancel);
              MinecraftForge.EVENT_BUS.post(event);
              cancel = event.isCanceled();

              int xDist = xPos - xStart;
              int yDist = yPos - yStart;
              int zDist = zPos - zStart;

              if (9 * xDist * xDist + yDist * yDist + 9 * zDist * zDist < 2500) {
                if (cancel) {
                  blocksBroken += breakTree(world, xPos, yPos, zPos, xStart, yStart, zStart, tool,
                      state, player);
                } else {
                  localMeta = localBlock.getMetaFromState(localState);
                  if (localBlock == block && localMeta % 4 == meta % 4) {
                    if (!player.capabilities.isCreativeMode) {
                      localBlock.harvestBlock(world, player, pos, state, world.getTileEntity(pos),
                          tool);
                      axe.onBlockDestroyed(tool, world, localState, localPos, player);
                      ++blocksBroken;
                    }

                    world.setBlockToAir(localPos);
                    if (!world.isRemote) {
                      blocksBroken += breakTree(world, xPos, yPos, zPos, xStart, yStart, zStart,
                          tool, state, player);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    return blocksBroken;
  }

  @Override
  public String getTranslatedName() {

    return SilentGems.instance.localizationHelper.getLocalizedString("skill", "Lumberjack");
  }
}