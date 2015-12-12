package net.silentchaos512.gems.client.renderers.tool;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.silentchaos512.gems.core.util.ToolHelper;
import net.silentchaos512.gems.item.ModItems;
import net.silentchaos512.gems.item.tool.GemAxe;
import net.silentchaos512.gems.item.tool.GemBow;
import net.silentchaos512.gems.item.tool.GemHoe;
import net.silentchaos512.gems.item.tool.GemPickaxe;
import net.silentchaos512.gems.item.tool.GemShovel;
import net.silentchaos512.gems.item.tool.GemSickle;
import net.silentchaos512.gems.item.tool.GemSword;
import net.silentchaos512.gems.lib.Strings;

/**
 * This must be an Item in order to contain item icons. Why Minecraft won't let you register icons elsewhere, I don't
 * know, but this works!
 */
public class ToolRenderHelper extends Item {

  public static ToolRenderHelper instance;

  public static void init() {

    if (instance == null && ModItems.toolRenderHelper != null) {
      instance = ModItems.toolRenderHelper;
    }
  }

  /*
   * Constants
   */

  // The number of head types
  public static final int HEAD_TYPE_COUNT = 15;
  // The number of rod types
  public static final int ROD_TYPE_COUNT = 2;
  // The number of bow stages
  public static final int BOW_STAGE_COUNT = 4;
  // The number of rod gem decorations
  public static final int ROD_DECO_TYPE_COUNT = 15;
  // The number of wool grip types (shouldn't change)
  public static final int ROD_WOOL_TYPE_COUNT = 16;
  // The number of mining tool tips.
  public static final int TIP_TYPE_COUNT = 3;

  // Render pass IDs and count
  public static final int PASS_ROD = 0;
  public static final int PASS_HEAD_M = 1;
  public static final int PASS_HEAD_L = 2;
  public static final int PASS_HEAD_R = 3;
  public static final int PASS_ROD_DECO = 4;
  public static final int PASS_ROD_WOOL = 5;
  public static final int PASS_TIP = 6;
  public static final int RENDER_PASS_COUNT = 7;

  /*
   * Icons
   */

  // Shared
  public IIcon iconBlank;           // A completely transparent texture.
  public IIcon iconError;           // Shows up if I screw up.
  public IIcon[] iconMainRodDeco;   // Rod decoration used by most tools.
  public IIcon[] iconMainRodWool;   // Rod wool grip used by most tools.

  // Specific tool icon collections.
  public final ToolIconCollection swordIcons = new ToolIconCollection();
  public final ToolIconCollection pickaxeIcons = new ToolIconCollection();
  public final ToolIconCollection shovelIcons = new ToolIconCollection();
  public final ToolIconCollection axeIcons = new ToolIconCollection();
  public final ToolIconCollection hoeIcons = new ToolIconCollection();
  public final ToolIconCollection sickleIcons = new ToolIconCollection();
  public final ToolIconCollection bow0Icons = new ToolIconCollection();
  public final ToolIconCollection bow1Icons = new ToolIconCollection();
  public final ToolIconCollection bow2Icons = new ToolIconCollection();
  public final ToolIconCollection bow3Icons = new ToolIconCollection();

  @Override
  public void registerIcons(IIconRegister reg) {

    String domain = Strings.RESOURCE_PREFIX;
    String item;
    int i;

    /*
     * Shared
     */

    iconBlank = reg.registerIcon(domain + "Blank");
    iconError = reg.registerIcon(domain + "Error");

    item = domain + "ToolDeco";
    iconMainRodDeco = new IIcon[ROD_DECO_TYPE_COUNT];
    for (i = 0; i < ROD_DECO_TYPE_COUNT; ++i) {
      iconMainRodDeco[i] = reg.registerIcon(item + i);
    }

    item = domain + "RodWool";
    iconMainRodWool = new IIcon[ROD_WOOL_TYPE_COUNT];
    for (i = 0; i < ROD_WOOL_TYPE_COUNT; ++i) {
      iconMainRodWool[i] = reg.registerIcon(item + i);
    }

    /*
     * Swords
     */

    item = domain + "Sword";
    swordIcons.rod[0] = reg.registerIcon(item + "_RodNormal");
    swordIcons.rod[1] = reg.registerIcon(item + "_RodOrnate");

    for (i = 0; i < HEAD_TYPE_COUNT; ++i) {
      swordIcons.headL[i] = reg.registerIcon(item + i + "L");
    }
    for (i = 0; i < HEAD_TYPE_COUNT; ++i) {
      swordIcons.headM[i] = reg.registerIcon(item + i);
    }
    for (i = 0; i < HEAD_TYPE_COUNT; ++i) {
      swordIcons.headR[i] = reg.registerIcon(item + i + "R");
    }
    for (i = 0; i < TIP_TYPE_COUNT; ++i) {
      swordIcons.tip[i] = reg.registerIcon(item + "Tip" + i);
    }

    item = domain + "SwordDeco";
    for (i = 0; i < ROD_DECO_TYPE_COUNT; ++i) {
      swordIcons.rodDeco[i] = reg.registerIcon(item + i);
    }
    item = domain + "SwordWool";
    for (i = 0; i < ROD_WOOL_TYPE_COUNT; ++i) {
      swordIcons.rodWool[i] = reg.registerIcon(item + i);
    }

    /*
     * Pickaxes
     */

    item = domain + "Pickaxe";
    pickaxeIcons.rod[0] = reg.registerIcon(item + "_RodNormal");
    pickaxeIcons.rod[1] = reg.registerIcon(item + "_RodOrnate");

    for (i = 0; i < HEAD_TYPE_COUNT; ++i) {
      pickaxeIcons.headL[i] = reg.registerIcon(item + i + "L");
      pickaxeIcons.headM[i] = reg.registerIcon(item + i);
      pickaxeIcons.headR[i] = reg.registerIcon(item + i + "R");
    }
    for (i = 0; i < TIP_TYPE_COUNT; ++i) {
      pickaxeIcons.tip[i] = reg.registerIcon(item + "Tip" + i);
    }

    pickaxeIcons.rodDeco = iconMainRodDeco;
    pickaxeIcons.rodWool = iconMainRodWool;

    /*
     * Shovels
     */

    item = domain + "Shovel";
    shovelIcons.rod[0] = reg.registerIcon(item + "_RodNormal");
    shovelIcons.rod[1] = reg.registerIcon(item + "_RodOrnate");

    for (i = 0; i < HEAD_TYPE_COUNT; ++i) {
      shovelIcons.headL[i] = reg.registerIcon(item + i + "L");
      shovelIcons.headM[i] = reg.registerIcon(item + i);
      shovelIcons.headR[i] = reg.registerIcon(item + i + "R");
    }
    for (i = 0; i < TIP_TYPE_COUNT; ++i) {
      shovelIcons.tip[i] = reg.registerIcon(item + "Tip" + i);
    }

    shovelIcons.rodDeco = iconMainRodDeco;
    shovelIcons.rodWool = iconMainRodWool;

    /*
     * Axes
     */

    item = domain + "Axe";
    axeIcons.rod[0] = reg.registerIcon(item + "_RodNormal");
    axeIcons.rod[1] = reg.registerIcon(item + "_RodOrnate");

    for (i = 0; i < HEAD_TYPE_COUNT; ++i) {
      axeIcons.headL[i] = reg.registerIcon(item + i + "L");
      axeIcons.headM[i] = reg.registerIcon(item + i);
      axeIcons.headR[i] = reg.registerIcon(item + i + "R");
    }
    for (i = 0; i < TIP_TYPE_COUNT; ++i) {
      axeIcons.tip[i] = reg.registerIcon(item + "Tip" + i);
    }

    axeIcons.rodDeco = iconMainRodDeco;
    axeIcons.rodWool = iconMainRodWool;

    /*
     * Hoe
     */

    item = domain + "Hoe";
    hoeIcons.rod[0] = reg.registerIcon(item + "_RodNormal");
    hoeIcons.rod[1] = reg.registerIcon(item + "_RodOrnate");

    for (i = 0; i < HEAD_TYPE_COUNT; ++i) {
      hoeIcons.headL[i] = reg.registerIcon(item + i + "L");
      hoeIcons.headM[i] = reg.registerIcon(item + i);
      hoeIcons.headR[i] = reg.registerIcon(item + i + "R");
    }
    for (i = 0; i < TIP_TYPE_COUNT; ++i) {
      hoeIcons.tip[i] = reg.registerIcon(item + "Tip" + i);
    }

    hoeIcons.rodDeco = iconMainRodDeco;
    hoeIcons.rodWool = iconMainRodWool;

    /*
     * Sickles
     */

    item = domain + "Sickle";
    sickleIcons.rod[0] = reg.registerIcon(item + "_RodNormal");
    sickleIcons.rod[1] = reg.registerIcon(item + "_RodOrnate");

    for (i = 0; i < HEAD_TYPE_COUNT; ++i) {
      sickleIcons.headL[i] = reg.registerIcon(item + i + "L");
      sickleIcons.headM[i] = reg.registerIcon(item + i);
      sickleIcons.headR[i] = reg.registerIcon(item + i + "R");
    }
    for (i = 0; i < TIP_TYPE_COUNT; ++i) {
      sickleIcons.tip[i] = reg.registerIcon(item + "Tip" + i);
    }

    sickleIcons.rodDeco = iconMainRodDeco;
    item += "Wool";
    for (i = 0; i < ROD_WOOL_TYPE_COUNT; ++i) {
      sickleIcons.rodWool[i] = reg.registerIcon(item + i);
    }

    /*
     * Bows
     */

    item = domain + "Bow";
    for (i = 0; i < ROD_TYPE_COUNT; ++i) {
      String str = item + (i == 0 ? "_MainNormal" : "_MainOrnate");
      bow0Icons.rod[i] = reg.registerIcon(str + 0);
      bow1Icons.rod[i] = reg.registerIcon(str + 1);
      bow2Icons.rod[i] = reg.registerIcon(str + 2);
      bow3Icons.rod[i] = reg.registerIcon(str + 3);
    }

    for (i = 0; i < HEAD_TYPE_COUNT; ++i) {
      bow0Icons.headL[i] = reg.registerIcon(item + i + "L");
      bow0Icons.headM[i] = reg.registerIcon(item + i);
      bow0Icons.headR[i] = reg.registerIcon(item + i + "R");
      bow1Icons.headL[i] = bow0Icons.headL[i];
      bow1Icons.headM[i] = bow0Icons.headM[i];
      bow1Icons.headR[i] = bow0Icons.headR[i];
      bow2Icons.headL[i] = bow0Icons.headL[i];
      bow2Icons.headM[i] = bow0Icons.headM[i];
      bow2Icons.headR[i] = bow0Icons.headR[i];
      bow3Icons.headL[i] = reg.registerIcon(item + i + "L_3");
      bow3Icons.headM[i] = reg.registerIcon(item + i + "_3");
      bow3Icons.headR[i] = reg.registerIcon(item + i + "R_3");
    }
    for (i = 0; i < TIP_TYPE_COUNT; ++i) {
      bow0Icons.tip[i] = reg.registerIcon(item + "Tip" + i);
      bow1Icons.tip[i] = bow0Icons.tip[i];
      bow2Icons.tip[i] = bow0Icons.tip[i];
      bow3Icons.tip[i] = reg.registerIcon(item + "Tip" + i + "_3");
    }

    item = domain + "BowDeco";
    for (i = 0; i < ROD_DECO_TYPE_COUNT; ++i) {
      bow0Icons.rodDeco[i] = reg.registerIcon(item + i);
      bow1Icons.rodDeco[i] = bow0Icons.rodDeco[i];
      bow2Icons.rodDeco[i] = bow0Icons.rodDeco[i];
      bow3Icons.rodDeco[i] = bow0Icons.rodDeco[i];
    }
    item = domain + "Bow_Arrow";
    for (i = 0; i < ROD_WOOL_TYPE_COUNT; ++i) {
      // Using the wool arrays for arrow textures, to avoid adding a render pass and because I'm not sure what to
      // do for wool on bows... A bit of a hack, but this whole class is, in a way.
      bow0Icons.rodWool[i] = iconBlank;
      bow1Icons.rodWool[i] = i == 0 ? reg.registerIcon(item + 1) : iconBlank;
      bow2Icons.rodWool[i] = i == 0 ? reg.registerIcon(item + 2) : iconBlank;
      bow3Icons.rodWool[i] = i == 0 ? reg.registerIcon(item + 3) : iconBlank;
      // bow0Icons.rodWool[i] = reg.registerIcon(item + i);
      // bow1Icons.rodWool[i] = bow0Icons.rodWool[i];
      // bow2Icons.rodWool[i] = bow0Icons.rodWool[i];
      // bow3Icons.rodWool[i] = reg.registerIcon(item + i + "_3");
    }
  }

  public IIcon getIcon(ItemStack stack, int pass, int gemId, boolean supercharged) {

    return getIcon(stack, pass, gemId, supercharged, stack, stack.getMaxItemUseDuration());
  }

  public IIcon getIcon(ItemStack stack, int pass, int gemId, boolean supercharged,
      ItemStack usingItem, int useRemaining) {

    ToolIconCollection icons;
    Item item = stack.getItem();

    if (item instanceof GemSword) {
      icons = swordIcons;
    } else if (item instanceof GemPickaxe) {
      icons = pickaxeIcons;
    } else if (item instanceof GemShovel) {
      icons = shovelIcons;
    } else if (item instanceof GemAxe) {
      icons = axeIcons;
    } else if (item instanceof GemHoe) {
      icons = hoeIcons;
    } else if (item instanceof GemSickle) {
      icons = sickleIcons;
    } else if (item instanceof GemBow) {
      int index = ((GemBow) item).getUsingIndex(stack, useRemaining);
      switch (index) {
        case 0:
          icons = bow0Icons;
          break;
        case 1:
          icons = bow1Icons;
          break;
        case 2:
          icons = bow2Icons;
          break;
        case 3:
          icons = bow3Icons;
          break;
        default:
          icons = bow0Icons;
      }
    } else {
      return iconError;
    }

    switch (pass) {
      case PASS_ROD:
        return getRodIcon(icons, stack, supercharged);
      case PASS_ROD_DECO:
        return getRodDecoIcon(icons, stack, supercharged);
      case PASS_ROD_WOOL:
        return getRodWoolIcon(icons, stack, supercharged);
      case PASS_HEAD_M:
        return getHeadMiddleIcon(icons, stack, gemId);
      case PASS_HEAD_L:
        return getHeadLeftIcon(icons, stack, gemId);
      case PASS_HEAD_R:
        return getHeadRightIcon(icons, stack, gemId);
      case PASS_TIP:
        return getTipIcon(icons, stack, gemId);
      default:
        return iconBlank;
    }
  }

  public boolean hasKey(ItemStack stack, String key) {

    return stack.stackTagCompound != null && stack.stackTagCompound.hasKey(key);
  }

  public IIcon getRodIcon(ToolIconCollection icons, ItemStack stack, boolean supercharged) {

    return supercharged ? icons.rod[1] : icons.rod[0];
  }

  public IIcon getRodDecoIcon(ToolIconCollection icons, ItemStack stack, boolean supercharged) {

    // Regular tools have no rod decoration!
    if (!supercharged) {
      return iconBlank;
    }

    int k = ToolHelper.getToolRodDeco(stack);
    if (k > -1) {
      k = MathHelper.clamp_int(k, 0, ROD_DECO_TYPE_COUNT - 1);
      return icons.rodDeco[k];
    }
    return icons.rodDeco[12];
  }

  public IIcon getRodWoolIcon(ToolIconCollection icons, ItemStack stack, boolean supercharged) {

    if (stack.getItem() instanceof GemBow) {
      // Bow arrow texture.
      return icons.rodWool[0];
    }

    int k = ToolHelper.getToolRodWool(stack);
    if (k > -1) {
      k = MathHelper.clamp_int(k, 0, ROD_WOOL_TYPE_COUNT - 1);
      return icons.rodWool[k];
    }

    return iconBlank;
  }

  public IIcon getHeadMiddleIcon(ToolIconCollection icons, ItemStack stack, int gemId) {

    int k = ToolHelper.getToolHeadMiddle(stack);
    if (k > -1) {
      k = MathHelper.clamp_int(k, 0, HEAD_TYPE_COUNT - 1);
      return icons.headM[k];
    }

    if (gemId >= 0 && gemId < HEAD_TYPE_COUNT) {
      return icons.headM[gemId];
    } else {
      return iconError;
    }
  }

  public IIcon getHeadLeftIcon(ToolIconCollection icons, ItemStack stack, int gemId) {

    int k = ToolHelper.getToolHeadLeft(stack);
    if (k > -1) {
      k = MathHelper.clamp_int(k, 0, HEAD_TYPE_COUNT - 1);
      return icons.headL[k];
    }

    if (gemId >= 0 && gemId < HEAD_TYPE_COUNT) {
      return icons.headL[gemId];
    } else {
      return iconError;
    }
  }

  public IIcon getHeadRightIcon(ToolIconCollection icons, ItemStack stack, int gemId) {

    int k = ToolHelper.getToolHeadRight(stack);
    if (k > -1) {
      k = MathHelper.clamp_int(k, 0, HEAD_TYPE_COUNT - 1);
      return icons.headR[k];
    }

    if (gemId >= 0 && gemId < HEAD_TYPE_COUNT) {
      return icons.headR[gemId];
    } else {
      return iconError;
    }
  }

  public IIcon getTipIcon(ToolIconCollection icons, ItemStack stack, int gemId) {

    int k = ToolHelper.getToolHeadTip(stack);
    if (k > -1) {
      k -= 1;
      if (k < 0) {
        return iconBlank;
      } else if (k > TIP_TYPE_COUNT - 1) {
        return iconError;
      }
      return icons.tip[k];
    } else {
      return iconBlank;
    }
  }

  /**
   * This is actually for the tools themselves.
   */
  public boolean hasEffect(ItemStack tool, int pass) {

    return tool.isItemEnchanted() && pass == RENDER_PASS_COUNT - 1
        && !ToolHelper.getToolNoGlint(tool);
  }

  public int getPossibleToolCombinations() {

    return HEAD_TYPE_COUNT * HEAD_TYPE_COUNT * HEAD_TYPE_COUNT * ROD_TYPE_COUNT
        * ROD_DECO_TYPE_COUNT * (ROD_WOOL_TYPE_COUNT + 1) * (TIP_TYPE_COUNT + 1);

  }
}
