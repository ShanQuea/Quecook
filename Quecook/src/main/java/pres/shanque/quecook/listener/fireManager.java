package pres.shanque.quecook.listener;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pres.shanque.quecook.recipemanage.RecipeManager;

import java.util.List;

public class fireManager implements Listener {

    @EventHandler
    public boolean selectGuo(InventoryClickEvent event){
        Inventory inventory = event.getInventory();
        ItemStack currentItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        String inv = inventory.getName();
        String inv2 = inv.substring(inv.length() - 4);
        if (!inv2.equals("烹饪界面")) {
            return false;
        }

        if(currentItem == null || currentItem.getType() == Material.AIR){
            return false;
        }

        String potName = event.getCurrentItem().getItemMeta().getDisplayName();
        if (!potName.contains("锅")){
            return false;
        }
        String pot2 = potName.replaceAll("[^\u4e00-\u9fa5]", "");
        if (potName.contains("锅")){
            inventory.setItem(48, currentItem);
            ConfigurationSection potKey = RecipeManager.getPotConfig().getConfigurationSection(pot2);
            if(potKey != null){
                List<?> fireList = potKey.getList("lore");

            }
        }

        return false;
    }

}
