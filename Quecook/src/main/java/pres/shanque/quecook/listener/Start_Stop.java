
package pres.shanque.quecook.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Start_Stop implements Listener {
    private HashMap<Player, Long> cookingTimers = new HashMap();
    private final int MAX_COOKING_TIME_DIFFERENCE = 5;
    boolean stop = false;
    ClickMenuListener clickMenuListener = new ClickMenuListener();

    @EventHandler
    public boolean onClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack currentItem = event.getCurrentItem();
        String inv = inventory.getName();
        String inv2 = inv.substring(inv.length() - 4);
        final Player player = (Player) ((Object) event.getWhoClicked());
        Inventory playerGui = player.getInventory();
        if (!inv2.equals("烹饪界面")) {
            return false;
        }
        ItemStack item = inventory.getItem(45);
        net.minecraft.server.v1_12_R1.ItemStack cookTimeNMS = CraftItemStack.asNMSCopy(item);
        assert cookTimeNMS.getTag() != null;
        final int[] cookTime = {cookTimeNMS.getTag().getInt("cookTime")};
        final int[] range = {cookTimeNMS.getTag().getInt("range")};
        final String success = cookTimeNMS.getTag().getString("success");
        final String fail = cookTimeNMS.getTag().getString("fail");
        final String recipe = cookTimeNMS.getTag().getString("recipe");
        ConfigurationSection recipeData = clickMenuListener.getRecipeData(recipe);



        event.setCancelled(true);
        if (cookTime[0] == 0) {
            player.sendMessage("当前配方未配置完全 请联系管理员反馈");
            return false;
        }
        if (!inv2.equals("烹饪界面")) {
            return false;
        }
        net.minecraft.server.v1_12_R1.ItemStack itemNMS = CraftItemStack.asNMSCopy(currentItem);
        if (currentItem == null || currentItem.getType() == Material.AIR) {
            return false;
        }

        String successName = success.contains("-") ? success.split("-")[0] : success;
        int successAmount = success.contains("-") ? Integer.parseInt(success.split("-")[1]) : 1;

        String failName = fail.contains("-") ? fail.split("-")[0] : success;
        int failAmount = fail.contains("-") ? Integer.parseInt(fail.split("-")[1]) : 1;




        ItemStack failItem = io.lumine.xikage.mythicmobs.MythicMobs.inst().getItemManager().getItemStack(failName);
        failItem.setAmount(failAmount);
        ItemStack successItem = io.lumine.xikage.mythicmobs.MythicMobs.inst().getItemManager().getItemStack(successName);
        successItem.setAmount(successAmount);
        if (Objects.equals(currentItem.getItemMeta().getDisplayName(), "开始烹饪")) {
            if (this.cookingTimers.containsKey(player)) {
                player.sendMessage(ChatColor.RED + "您已经在烹饪了，请勿重复操作！");
                return false;
            }

            if (materialAmount(inventory, player)) {
                player.sendMessage("材料充足,开始烹饪");
                this.cookingTimers.put(player, System.currentTimeMillis());
                removeItemFromInventory(inventory, player);
                getPlayerItem(inventory,player,recipeData);
                new BukkitRunnable() {
                    private long startCookingTimeMillis = System.currentTimeMillis();
                    int num = 0;

                    public void run() {
                        num++;
                        if (num == cookTime[0]) {
                            player.sendMessage("请及时结束烹饪");
                        }
                        if (stop) {
                            stop = false;
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("Quecook"), 0L, 20L);
            } else {
                player.sendMessage("材料不足");
            }

        }

        if (Objects.equals(currentItem.getItemMeta().getDisplayName(), "结束烹饪")) {

            if (!this.cookingTimers.containsKey(player)) {
                player.sendMessage(ChatColor.RED + "您还没有开始烹饪！");
                return false;
            }
            long startCookingTimeMillis = this.cookingTimers.get(player);
            int elapsedSeconds = (int) ((System.currentTimeMillis() - startCookingTimeMillis) / 1000L);


            this.cookingTimers.remove(player);
            if (elapsedSeconds < cookTime[0]-range[0]) {
                player.sendMessage(ChatColor.RED + "烹饪时间误差过小，烹饪失败！");
                playerGui.addItem(failItem);
            }
            else if(elapsedSeconds > cookTime[0]+range[0]){
                player.sendMessage(ChatColor.RED + "烹饪时间误差过大，烹饪失败！");
                playerGui.addItem(failItem);
            }
            else{
                player.sendMessage(ChatColor.GREEN + "烹饪成功！");
                playerGui.addItem(successItem);
            }
            return true;
        }
        if (Objects.equals(currentItem.getItemMeta().getDisplayName(), "详细配方")) {

            event.setCancelled(true);
        }
        return false;
    }


    public boolean materialAmount(Inventory inventory, Player player){
        for (int i = 0; i < 30; i++) {
            ItemStack mat =  inventory.getItem(i);
            if (mat == null)
            {
                break;
            }
            int matAmount = mat.getAmount();
            int realAmount = countItem(player,mat);
            if (matAmount > realAmount)
            {
                return false;
            }
        }
        return true;
    }

    public void removeItemFromInventory(Inventory inventory,Player player){
        Inventory playerGui = player.getInventory();
        for (int i = 0; i < 30; i++) {
            ItemStack mat =  inventory.getItem(i);
            if (mat == null)
            {
                break;
            }
            int matAmount = mat.getAmount();
            for (ItemStack item : player.getInventory().getContents()) {

                if (item.isSimilar(mat)) {
                    int amount = item.getAmount();
                    if (amount > matAmount) {
                        item.setAmount(amount - matAmount);
                        break;
                    } else {
                        matAmount -= amount;
                        player.getInventory().remove(item);
                        if (matAmount == 0) break;
                    }
                }
            }
        }
    }

    public int countItem(Player player, ItemStack targetItem) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(targetItem)) {
                count += item.getAmount();
            }
        }
        return count;
    }



    public void getPlayerItem(Inventory inventory,Player player,ConfigurationSection recipeData){
        List<String> ingredients = recipeData.getStringList("ingredients");
        for (int i = 0; i < ingredients.size(); i++) {
            String ingredient = ingredients.get(i);
            String ingredientName = null;
            int ingredientAmount = 1;
            if (ingredient.contains("-")) {
                ingredientName = ingredients.get(i).split("-")[0];
                ingredientAmount = Integer.parseInt(ingredients.get(i).split("-")[1]);
            }else{
                ingredientName = ingredients.get(i);
            }

            if (ingredientName != null) {
                ItemStack MMitem = io.lumine.xikage.mythicmobs.MythicMobs.inst().getItemManager().getItemStack(ingredientName);
                int count = countItem(player,MMitem);
                MMitem.setAmount(ingredientAmount);
                inventory.setItem(i, MMitem);
                if (count==0){
                    ItemStack nullItem = new ItemStack(Material.PAPER);
                    ItemMeta nullMeta = nullItem.getItemMeta();
                    nullMeta.setDisplayName("你背包没有"+MMitem.getItemMeta().getDisplayName());
                    nullItem.setItemMeta(nullMeta);
                    inventory.setItem(i+19,nullItem);
                }else {
                    MMitem.setAmount(count);
                    inventory.setItem(i+19,MMitem);
                }

            }
        }
    }
}
