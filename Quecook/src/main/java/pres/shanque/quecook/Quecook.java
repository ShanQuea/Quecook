package pres.shanque.quecook;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pres.shanque.quecook.command.CookingRecipe;
import pres.shanque.quecook.dragonGui.GuiListener;
import pres.shanque.quecook.listener.ClickMenuListener;
import pres.shanque.quecook.listener.CoreListener;
import pres.shanque.quecook.listener.Start_Stop;

import java.io.File;


public class Quecook extends JavaPlugin {

    private static Quecook INSTANCE;

    private CookingRecipe cookingRecipe;


    public static Quecook getInstance() {
        return INSTANCE;
    }



    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    public void onEnable() {
        this.cookingRecipe = new CookingRecipe(this);
        this.cookingRecipe.onEnable();



        File guiFolder = new File(getDataFolder(), "Gui");
        if (!guiFolder.exists()) {
            guiFolder.mkdir();
        }
        saveResource("Gui/鹊之烹饪界面.yml", false);
        saveResource("Gui/鹊之烹饪开始界面.yml", false);

        CoreListener coreListener = new CoreListener(this);
        this.getServer().getPluginManager().registerEvents(coreListener, this);
       this.getServer().getPluginManager().registerEvents(new ClickMenuListener(), this);
        this.getServer().getPluginManager().registerEvents(new Start_Stop(),this);
        this.getServer().getPluginManager().registerEvents(new GuiListener(),this);
        this.getLogger().info("鹊之烹饪加载成功!");
    }

    public void onDisable() {
        this.getLogger().info("鹊之烹饪 卸载成功!");
        this.cookingRecipe.onDisable();
        INSTANCE = null;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.cookingRecipe.onCommand(sender, command, label, args);
    }

}

