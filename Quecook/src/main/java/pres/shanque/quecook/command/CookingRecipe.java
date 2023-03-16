package pres.shanque.quecook.command;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import pres.shanque.quecook.recipemanage.RecipeManager;



import java.io.File;
import java.util.List;
import java.util.Objects;


public class CookingRecipe implements CommandExecutor {
    private final JavaPlugin plugin;
    private FileConfiguration config;

    private Inventory gui;

    public CookingRecipe(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void onEnable() {
        this.loadConfig();
        this.initGui();
        System.out.println("gui部分加载成功");
        this.plugin.getCommand("Qgui").setExecutor(this);
    }

    public void onDisable() {
        this.gui = null;
        this.config = null;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Objects.equals(args[0], "reload")) {
            Player player = (Player)((Object)sender);
            loadConfig();

            initGui();
            player.sendMessage("重载成功");
            return true;
        }
        if (Objects.equals(args[0], "open") || sender instanceof Player) {
            Player player = (Player)((Object)sender);
            player.openInventory(this.gui);
            return true;
        }
        return false;
    }

    public void loadConfig() {
        File configFile = new File(this.plugin.getDataFolder(), "recipe.yml");
        File potFile = new File(this.plugin.getDataFolder(),"pot.yml");
        if (!configFile.exists()) {
            this.plugin.saveResource("recipe.yml", false);
        }
        if(!potFile.exists()){
            this.plugin.saveResource("pot.yml",false);
        }
        RecipeManager.loadPotConfig(potFile);
        this.config = RecipeManager.loadRecipeConfig(configFile);
    }

    private void initGui() {
        this.gui = this.plugin.getServer().createInventory(null, 45, "鹊之烹饪");
        long concurrentTime1 = System.nanoTime();

        ConfigurationSection itemsSection = this.config.getConfigurationSection("");
        int Num = 0;
        for (String itemName : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemName);
            ItemStack itemStack = this.createItemStackFromConfigSection(itemSection);
            this.gui.addItem(itemStack);
            ++Num;
        }
        long concurrentTime2 = System.nanoTime();
        String time = String.valueOf((double)(concurrentTime2 - concurrentTime1)/1000000);
        System.out.println("初始化GUI执行时间为：" + time.substring(0,time.equals("0.0") ?  1 : (time.indexOf(".")+3)) + " ms");
        System.out.println("已加载" + Num + "个菜谱");
    }

    private ItemStack createItemStackFromConfigSection(ConfigurationSection section) {
        Material material = Material.valueOf(section.getString("id"));
        short data = (short)section.getInt("data");
        String displayName = ChatColor.translateAlternateColorCodes('&', section.getString("display"));
        List<String> lore = section.getStringList("lore");

        ItemStack itemStack = new ItemStack(material, 1, data);

        //nms设置物品NBT
        net.minecraft.server.v1_12_R1.ItemStack itemNMS = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbt = itemNMS.getTag();
        if (nbt == null) {
            nbt = new NBTTagCompound();
        }
        String path = section.toString().split("path='")[1].split("'")[0];
        nbt.set("recipe", new NBTTagString(path));
        itemNMS.setTag(nbt);
        itemStack = CraftItemStack.asBukkitCopy(itemNMS);


        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
