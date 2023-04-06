
package pres.shanque.quecook.listener;

import java.util.*;

import com.google.common.collect.ImmutableMap;
import eos.moe.dragoncore.api.gui.event.CustomPacketEvent;
import eos.moe.dragoncore.network.PacketSender;
import eos.moe.dragoncore.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class Start_Stop implements Listener {
    private HashMap<Player, Long> cookingTimers = new HashMap();
    private final int MAX_COOKING_TIME_DIFFERENCE = 5;
    boolean stop = false;

    List<Integer> slots = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,47,48);
    ClickMenuListener clickMenuListener = new ClickMenuListener();


    @EventHandler
    public boolean onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {

        } else {
            int clickedSlot = event.getRawSlot();
            if(slots.contains(clickedSlot)) event.setCancelled(true);

        }
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


        String failName = fail.contains("-") ? fail.split("-")[0] : fail;
        int failAmount = fail.contains("-") ? Integer.parseInt(fail.split("-")[1]) : 1;
        ItemStack failItem = io.lumine.xikage.mythicmobs.MythicMobs.inst().getItemManager().getItemStack(failName);
        failItem.setAmount(failAmount);

        String successName = success.contains("-") ? success.split("-")[0] : success;
        int successAmount = success.contains("-") ? Integer.parseInt(success.split("-")[1]) : 1;
        ItemStack successItem = io.lumine.xikage.mythicmobs.MythicMobs.inst().getItemManager().getItemStack(successName);
        successItem.setAmount(successAmount);

        if (Objects.equals(currentItem.getItemMeta().getDisplayName(), "开始烹饪")) {
            if (this.cookingTimers.containsKey(player)) {
                player.sendMessage(ChatColor.RED + "您已经在烹饪了，请勿重复操作！");
                return false;
            }
            HashMap<ItemStack, Integer> items = getItemsFromChest(inventory);
            boolean MaterialJudgment = hasAllItems(inventory, items);
            if (MaterialJudgment) {
                player.sendMessage("材料充足,开始烹饪");
                removeItemsFromChest(player, inventory, items);
                this.cookingTimers.put(player, System.currentTimeMillis());
                new BukkitRunnable() {
                    int num = 0;
                    long startCookingTimeMillis = System.currentTimeMillis();
                    public void run() {
                        num++;
                        if (num == cookTime[0]) {
                            player.sendMessage("请及时结束烹饪");
                        }
                        if (stop || num >= cookTime[0] + 10) { // 自动结束烹饪
                            stop = false;
                            this.cancel();
                            if (cookingTimers.containsKey(player)) {
                                player.sendMessage(ChatColor.RED + "烹饪时间已达到上限，自动结束烹饪！");
                                cookingTimers.remove(player);
                                playerGui.addItem(failItem);
                            }
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
            if (elapsedSeconds < cookTime[0] - range[0]) {
                player.sendMessage(ChatColor.RED + "烹饪时间误差过小，烹饪失败！");
                playerGui.addItem(failItem);
            } else if (elapsedSeconds > cookTime[0] + range[0]) {
                player.sendMessage(ChatColor.RED + "烹饪时间误差过大，烹饪失败！");
                playerGui.addItem(failItem);
            } else {
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


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        String inv = inventory.getName();
        String inv2 = inv.substring(inv.length() - 4);
        if (inv2.equals("烹饪界面")) {
            Player player = (Player) event.getPlayer();
            for (int i = 20; i <= 33; i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null) {
                    Map<Integer, ItemStack> excess = player.getInventory().addItem(item);
                    if (!excess.isEmpty()) {
                        excess.values().forEach(drop -> player.getWorld().dropItem(player.getLocation(), drop));
                    }
                }
            }
        }
    }


    public static HashMap<ItemStack, Integer> getItemsFromChest(Inventory inventory) {
        HashMap<ItemStack, Integer> itemMap = new HashMap<>();
        for (int i = 0; i < 13; i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                if (itemMap.containsKey(item)) {
                    int amount = itemMap.get(item);
                    itemMap.put(item, amount + item.getAmount());

                } else {
                    itemMap.put(item, item.getAmount());
                }
            }
        }
        return itemMap;
    }

    public static boolean hasAllItems(Inventory chest, HashMap<ItemStack, Integer> items) {
        // 遍历所有需要的物品
        for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
            ItemStack requiredItem = entry.getKey();
            int requiredAmount = entry.getValue();
            int foundAmount = 0;
            // 遍历箱子中10-20格内的所有物品
            for (int i = 20; i <= 33; i++) {
                ItemStack chestItem = chest.getItem(i);

                if (chestItem != null && chestItem.getItemMeta().equals(requiredItem.getItemMeta())) {
                    foundAmount += chestItem.getAmount();
                    System.out.println(foundAmount);
                }

            }
            // 如果找到的数量不够则返回false
            if (foundAmount < requiredAmount) {
                return false;
            }
        }
        // 所有物品都符合要求则返回true
        return true;
    }

    public static void removeItemsFromChest(Player player,Inventory chest, HashMap<ItemStack, Integer> items) {
        for (ItemStack item : items.keySet()) {
            int requiredAmount = items.get(item);
            for (int i = 20; i <= 33; i++) {
                ItemStack chestItem = chest.getItem(i);
                if (chestItem != null && chestItem.isSimilar(item)) {
                    int amountToRemove = Math.min(chestItem.getAmount(), requiredAmount);
                    chestItem.setAmount(chestItem.getAmount() - amountToRemove);
                    requiredAmount -= amountToRemove;
                    if (requiredAmount <= 0) {
                        break;
                    }
                }
            }
            player.updateInventory();
        }
    }



}
