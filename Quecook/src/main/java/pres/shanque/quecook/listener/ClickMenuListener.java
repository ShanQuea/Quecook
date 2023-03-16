
package pres.shanque.quecook.listener;

import io.lumine.xikage.mythicmobs.MythicMobs;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pres.shanque.quecook.Quecook;
import pres.shanque.quecook.recipemanage.RecipeManager;

public class ClickMenuListener
        implements Listener {
    private static final String MENU_TITLE = "鹊之烹饪";
    final Quecook quecook = Quecook.getInstance();
    File file = new File(this.quecook.getDataFolder(), "recipe.yml");
    File recipess = new File(Quecook.getPlugin(Quecook.class).getDataFolder(), "recipe.yml");
    FileConfiguration recipes = YamlConfiguration.loadConfiguration(this.recipess);

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack currentItem = event.getCurrentItem();
        if (inventory == null || !MENU_TITLE.equals(inventory.getName()) || currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        String itemName = ChatColor.stripColor(currentItem.getItemMeta().getDisplayName());
        net.minecraft.server.v1_12_R1.ItemStack itemNMS = CraftItemStack.asNMSCopy(currentItem);
        assert (itemNMS.getTag() != null);
        String path = itemNMS.getTag().getString("recipe");
        ConfigurationSection recipeData = this.recipes.getConfigurationSection(path);
        ConfigurationSection itemsSection = RecipeManager.getRecipeConfig().getConfigurationSection(path);
        int cooktime = itemsSection.getInt("cookTime");
        int range = itemsSection.getInt("range");
        String success = itemsSection.getString("success");
        String fail = itemsSection.getString("fail");
        if (recipeData != null) {
            String guiTitle = itemName + "烹饪界面";
            Inventory recipeGUI = Bukkit.createInventory(null, 54, guiTitle);
            ItemStack startBook = new ItemStack(Material.BOOK);
            ItemMeta bookMeta = startBook.getItemMeta();
            ItemStack pot = new ItemStack(Material.PAPER);
            ItemMeta potMeat = pot.getItemMeta();
            bookMeta.setDisplayName("开始烹饪");
            startBook.setItemMeta(bookMeta);
            recipeGUI.setItem(18, startBook);
            bookMeta.setDisplayName("结束烹饪");
            startBook.setItemMeta(bookMeta);
            recipeGUI.setItem(27, startBook);
            potMeat.setDisplayName("请点击背包中的烹饪锅");
            pot.setItemMeta(potMeat);
            recipeGUI.setItem(48, pot);
            net.minecraft.server.v1_12_R1.ItemStack bookNMS = CraftItemStack.asNMSCopy(startBook);
            NBTTagCompound bookNBT = bookNMS.getTag();
            assert bookNBT != null;
            bookNBT.set("cookTime", new NBTTagInt(cooktime));
            bookNMS.setTag(bookNBT);
            bookNBT.set("range", new NBTTagInt(range));
            bookNMS.setTag(bookNBT);
            bookNBT.set("success", new NBTTagString(success));
            bookNMS.setTag(bookNBT);
            bookNBT.set("fail", new NBTTagString(fail));
            bookNMS.setTag(bookNBT);
            bookNBT.set("recipe", new NBTTagString(path));
            startBook = CraftItemStack.asBukkitCopy(bookNMS);
            ItemMeta NewMeta = startBook.getItemMeta();
            NewMeta.setDisplayName("详细配方");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("需要锻造时间" + cooktime);
            lore.add("大概火力范围");
            NewMeta.setLore(lore);
            startBook.setItemMeta(NewMeta);
            recipeGUI.setItem(45, startBook);
            List<String> ingredients = recipeData.getStringList("ingredients");
            for (int i = 0; i < ingredients.size(); ++i) {
                String ingredient = ingredients.get(i);
                String ingredientName;
                int ingredientAmount = 1;
                if (ingredient.contains("-")) {
                    ingredientName = ingredients.get(i).split("-")[0];
                    ingredientAmount = Integer.parseInt(ingredients.get(i).split("-")[1]);
                } else {
                    ingredientName = ingredients.get(i);
                }
                if (ingredientName == null) continue;
                ItemStack MMitem = MythicMobs.inst().getItemManager().getItemStack(ingredientName);
                int count = this.countItem(player, MMitem);
                MMitem.setAmount(ingredientAmount);
                recipeGUI.setItem(i, MMitem);
                if (count == 0) {
                    ItemStack nullItem = new ItemStack(Material.PAPER);
                    ItemMeta nullMeta = nullItem.getItemMeta();
                    nullMeta.setDisplayName("你背包没有" + MMitem.getItemMeta().getDisplayName());
                    nullItem.setItemMeta(nullMeta);
                    recipeGUI.setItem(i + 19, nullItem);
                    continue;
                }
                MMitem.setAmount(count);
                recipeGUI.setItem(i + 19, MMitem);
            }
            player.openInventory(recipeGUI);
        }
    }

    public int countItem(Player player, ItemStack targetItem) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || !item.isSimilar(targetItem) || (count += item.getAmount()) <= 64) continue;
            count = 64;
        }
        return count;
    }


    public ConfigurationSection getRecipeData(String path) {
        return recipes.getConfigurationSection(path);
    }
}
