package net.silentchaos512.gems.core.util;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public class PlayerHelper {

  public static void addChatMessage(EntityPlayer player, String message) {

    player.addChatMessage(new ChatComponentText(message));
  }

  public static void addChatMessage(EntityPlayer player, String key, boolean fromLocalizationFile) {

    addChatMessage(player, fromLocalizationFile ? LocalizationHelper.getLocalizedString(key) : key);
  }

  public static void addItemToInventoryOrDrop(EntityPlayer player, ItemStack stack) {

    if (!player.inventory.addItemStackToInventory(stack)) {
      // Spawn item entity
      EntityItem entityItem = new EntityItem(player.worldObj, player.posX, player.posY + 1.5,
          player.posZ, stack);
      player.worldObj.spawnEntityInWorld(entityItem);
    }
  }

  public static boolean isPlayerHoldingItem(EntityPlayer player, Object object) {

    if (player.inventory.getCurrentItem() != null && object != null) {
      if (object instanceof Item) {
        return player.inventory.getCurrentItem().getItem().equals((Item) object);
      } else if (object instanceof ItemStack) {
        return player.inventory.getCurrentItem().getItem().equals(((ItemStack) object).getItem());
      } else if (object instanceof Block) {
        return InventoryHelper.isStackBlock(player.inventory.getCurrentItem(), (Block) object);
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public static void removeExperience(EntityPlayer player, int amount) {

    if (amount <= 0) {
      return;
    }

    player.addScore((-1) * amount);
    int j = Integer.MIN_VALUE + player.experienceTotal;

    if (amount < j) {
      amount = j;
    }

    player.experience -= (float) amount / (float) player.xpBarCap();

    for (player.experienceTotal -= amount; player.experience <= 0.0F; player.experience /= (float) player
        .xpBarCap()) {
      if (player.experience == 0 && player.experienceTotal == 0 && player.experienceLevel == 0) {
        return;
      }
      player.experience = (player.experience + 1.0F) * (float) player.xpBarCap();
      player.addExperienceLevel(-1);
    }
  }
}