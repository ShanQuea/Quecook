package pres.shanque.quecook.recipemanage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class RecipeManager {
    private static FileConfiguration recipeConfig;
    private static FileConfiguration potConfig;
    public Object getRecipeConfig;


    public static FileConfiguration loadRecipeConfig(File configFile) {
        recipeConfig = YamlConfiguration.loadConfiguration(configFile);
        return recipeConfig;
    }

    public static FileConfiguration loadPotConfig(File configFile) {
        potConfig = YamlConfiguration.loadConfiguration(configFile);
        return potConfig;
    }

    public static void saveRecipeConfig(File configFile) {
        try {
            recipeConfig.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getRecipeConfig() {
        return recipeConfig;
    }
    public static FileConfiguration getPotConfig(){ return  potConfig;}
}
