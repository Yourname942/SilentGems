package net.silentchaos512.gems.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.silentchaos512.gems.SilentGems;
import net.silentchaos512.gems.core.util.InventoryHelper;
import net.silentchaos512.gems.core.util.LogHelper;
import net.silentchaos512.gems.item.CraftingMaterial;
import net.silentchaos512.gems.item.Gem;
import net.silentchaos512.gems.item.GemRod;
import net.silentchaos512.gems.item.tool.GemAxe;
import net.silentchaos512.gems.item.tool.GemHoe;
import net.silentchaos512.gems.item.tool.GemPickaxe;
import net.silentchaos512.gems.item.tool.GemShovel;
import net.silentchaos512.gems.item.tool.GemSickle;
import net.silentchaos512.gems.item.tool.GemSword;
import net.silentchaos512.gems.lib.EnumGem;
import net.silentchaos512.gems.lib.Names;
import net.silentchaos512.gems.lib.Strings;
import net.silentchaos512.gems.material.ModMaterials;

public class DecorateToolRecipe implements IRecipe {
  
//  private ItemStack getResult(InventoryCrafting inv) { // incomplete
//    
//    final int gridWidth = inv.getSizeInventory() == 4 ? 2 : 3;
//    
//    int i;
//    int toolRow;
//    int toolColumn;
//    ItemStack stack;
//    
//    // Find tool, count gems
//    ItemStack tool;
//    int gemCount;
//
//    for (i = 0; i < inv.getSizeInventory(); ++i) {
//      stack = inv.getStackInSlot(i);
//      if (InventoryHelper.isGemTool(stack)) {
//        tool = stack;
//      } // TODO
//    }
//  }

  public ItemStack getStackInRowAndColumn(InventoryCrafting inventorycrafting, int row, int column,
      int gridWidth) {

    if (row >= 0 && row < gridWidth && column >= 0 && column < gridWidth) {
      int slot = row + column * gridWidth;
      if (slot >= 0 && slot < inventorycrafting.getSizeInventory()) {
        return inventorycrafting.getStackInSlot(slot);
      }
    }

    return null;
  }

  @Override
  public boolean matches(InventoryCrafting inventorycrafting, World world) {

    int numTools = 0;
    int numGems = 0;
    int numRods = 0;
    int numWool = 0;
    int toolSlot = -1;
    int i, row, column;

    final int gridWidth = inventorycrafting.getSizeInventory() == 4 ? 2 : 3;

    ItemStack stack, tool = null;

    // Count valid ingredients and look for invalid
    for (i = 0; i < inventorycrafting.getSizeInventory(); ++i) {
      stack = inventorycrafting.getStackInSlot(i);
      if (stack != null) {
        if (stack.getItem() instanceof Gem) {
          // Only regular gems, not supercharged.
          if (stack.getItemDamage() > 15) {
            return false;
          }
          ++numGems;
        } else if (InventoryHelper.isStackBlock(stack, Blocks.wool)) {
          ++numWool;
        } else if (InventoryHelper.isGemTool(stack)) {
          ++numTools;
          toolSlot = i;
          tool = stack;
        } else if (InventoryHelper.matchesOreDict(stack, Strings.ORE_DICT_STICK_FANCY)) {
          ++numRods;
        } else {
          // Invalid item
          return false;
        }
      }
    }

    // Need exactly one tool.
    if (numTools != 1) {
      return false;
    }
    // No more than one wool.
    if (numWool > 1) {
      return false;
    }
    // No more than one rod.
    if (numRods > 1) {
      return false;
    }
    // At least one gem, wool, or rod
    if (numWool == 0 && numGems == 0 && numRods == 0) {
      return false;
    }

    // Get tool row/column
    row = toolSlot % gridWidth;
    column = toolSlot / gridWidth;

    // Slots directly adjacent to tool may be a gem, wool, rod, or null.
    ItemStack[] stacks = new ItemStack[4];
    stacks[0] = getStackInRowAndColumn(inventorycrafting, row - 1, column, gridWidth);
    stacks[1] = getStackInRowAndColumn(inventorycrafting, row + 1, column, gridWidth);
    stacks[2] = getStackInRowAndColumn(inventorycrafting, row, column - 1, gridWidth);
    stacks[3] = getStackInRowAndColumn(inventorycrafting, row, column + 1, gridWidth);

    // Count gems adjacent to tool.
    int adjGems = 0;
    for (i = 0; i < stacks.length; ++i) {
      if (stacks[i] != null) {
        if (stacks[i].getItem() instanceof Gem) {
          ++adjGems;
        } else if (!InventoryHelper.isStackBlock(stacks[i], Blocks.wool)
            && !InventoryHelper.matchesOreDict(stacks[i], Strings.ORE_DICT_STICK_FANCY)) {
          return false;
        }
      }
    }

    // Make sure gems are only adjacent to tool.
    if (adjGems != numGems) {
      return false;
    }

    return true;
  }

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {

    ItemStack stack, tool = null;

    int i, row, column, toolSlot = 0, gemCount = 0;

    final int gridWidth = inventorycrafting.getSizeInventory() == 4 ? 2 : 3;

    // Find tool and count gems.
    for (i = 0; i < inventorycrafting.getSizeInventory(); ++i) {
      stack = inventorycrafting.getStackInSlot(i);
      if (stack != null) {
        if (InventoryHelper.isGemTool(stack)) {
          tool = stack;
          toolSlot = i;
        } else if (stack.getItem() instanceof Gem) {
          ++gemCount;
        }
      }
    }

    if (tool == null) {
      return null;
    }

    byte baseGem = getToolBaseGem(tool);

    // Get tool row/column
    row = toolSlot % gridWidth;
    column = toolSlot / gridWidth;

    // Copy tool, we can't modify the original!
    ItemStack result = tool.copy();
    if (result.stackTagCompound == null) {
      result.stackTagCompound = new NBTTagCompound();
    }

    // Deco
    stack = getStackInRowAndColumn(inventorycrafting, row, column + 1, gridWidth);
    if (stack != null && stack.getItem() instanceof Gem) {
      result.stackTagCompound.setByte(Strings.TOOL_ICON_DECO, (byte) stack.getItemDamage());
    }
    // HeadL
    stack = getStackInRowAndColumn(inventorycrafting, row - 1, column, gridWidth);
    if (stack != null && stack.getItem() instanceof Gem) {
      result.stackTagCompound.setByte(Strings.TOOL_ICON_HEAD_LEFT, (byte) stack.getItemDamage());
    } else if (!result.stackTagCompound.hasKey(Strings.TOOL_ICON_HEAD_LEFT)
        || result.stackTagCompound.getByte(Strings.TOOL_ICON_HEAD_LEFT) == -1) {
      result.stackTagCompound.setByte(Strings.TOOL_ICON_HEAD_LEFT, baseGem);
    }
    // HeadM
    stack = getStackInRowAndColumn(inventorycrafting, row, column - 1, gridWidth);
    if (stack != null && stack.getItem() instanceof Gem) {
      result.stackTagCompound.setByte(Strings.TOOL_ICON_HEAD_MIDDLE, (byte) stack.getItemDamage());
    } else if (!result.stackTagCompound.hasKey(Strings.TOOL_ICON_HEAD_MIDDLE)
        || result.stackTagCompound.getByte(Strings.TOOL_ICON_HEAD_MIDDLE) == -1) {
      result.stackTagCompound.setByte(Strings.TOOL_ICON_HEAD_MIDDLE, baseGem);
    }
    // HeadR
    stack = getStackInRowAndColumn(inventorycrafting, row + 1, column, gridWidth);
    if (stack != null && stack.getItem() instanceof Gem) {
      result.stackTagCompound.setByte(Strings.TOOL_ICON_HEAD_RIGHT, (byte) stack.getItemDamage());
    } else if (!result.stackTagCompound.hasKey(Strings.TOOL_ICON_HEAD_RIGHT)
        || result.stackTagCompound.getByte(Strings.TOOL_ICON_HEAD_RIGHT) == -1) {
      result.stackTagCompound.setByte(Strings.TOOL_ICON_HEAD_RIGHT, baseGem);
    }

    for (i = 0; i < inventorycrafting.getSizeInventory(); ++i) {
      stack = inventorycrafting.getStackInSlot(i);
      if (stack != null) {
        // Wool ("Rod")
        if (InventoryHelper.isStackBlock(stack, Blocks.wool)) {
          result.stackTagCompound.setByte(Strings.TOOL_ICON_ROD, (byte) stack.getItemDamage());
        }
        // Rod ("Handle")
        else if (InventoryHelper.matchesOreDict(stack, Strings.ORE_DICT_STICK_FANCY)) {
          result.stackTagCompound.setByte(Strings.TOOL_ICON_HANDLE, (byte) getToolRodId(stack));
        }
      }
    }

    // Repair with gems.
    result.attemptDamageItem(-gemCount * result.getMaxDamage() / 8, SilentGems.instance.random);

    return result;
  }

  public static int getToolRodId(ItemStack rod) {

    if (!InventoryHelper.matchesOreDict(rod, Strings.ORE_DICT_STICK_FANCY)) {
      LogHelper.warning("DecorateToolRecipe.getToolRodId was passed an invalid item: "
          + rod.getItem().getUnlocalizedName());
      return -1;
    }

//    if (rod.getItem() instanceof GemRod) {
//      return rod.getItemDamage();
//    } else if (rod.getItem() instanceof CraftingMaterial) {
//      int d = rod.getItemDamage();
//      if (d == CraftingMaterial.getMetaFor(Names.ORNATE_STICK)) {
//        return -1;
//      }else if (d == CraftingMaterial.getMetaFor(Names.FANCY_STICK_IRON)) {
//        return EnumGem.all().length;
//      } else if (d == CraftingMaterial.getMetaFor(Names.FANCY_STICK_COPPER)) {
//        return EnumGem.all().length + 1;
//      } else if (d == CraftingMaterial.getMetaFor(Names.FANCY_STICK_TIN)) {
//        return EnumGem.all().length + 2;
//      } else if (d == CraftingMaterial.getMetaFor(Names.FANCY_STICK_SILVER)) {
//        return EnumGem.all().length + 3;
//      }
//    }

    return -1;
  }

  @Override
  public int getRecipeSize() {

    // TODO What's this?
    return 9;
  }

  @Override
  public ItemStack getRecipeOutput() {

    // TODO What's this?
    return null;
  }

  private byte getToolBaseGem(ItemStack stack) {

    Item item = stack.getItem();
    Byte b = -1;

    if (item instanceof GemSword) {
      b = (byte) ((GemSword) item).getGemId();
    } else if (item instanceof GemPickaxe) {
      b = (byte) ((GemPickaxe) item).getGemId();
    } else if (item instanceof GemShovel) {
      b = (byte) ((GemShovel) item).getGemId();
    } else if (item instanceof GemAxe) {
      b = (byte) ((GemAxe) item).getGemId();
    } else if (item instanceof GemHoe) {
      b = (byte) ((GemHoe) item).getGemId();
    } else if (item instanceof GemSickle) {
      b = (byte) ((GemSickle) item).getGemId();
    }

    // Eliminate "supercharged" damage difference.
    if (b != -1 && b != ModMaterials.FISH_GEM_ID) {
      b = (byte) (b & 15);
    }

    return b;
  }
}
