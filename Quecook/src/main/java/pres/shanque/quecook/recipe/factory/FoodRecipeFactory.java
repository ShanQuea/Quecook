package pres.shanque.quecook.recipe.factory;

import pres.shanque.quecook.Quecook;
import pres.shanque.quecook.recipe.entity.FoodRecipe;
import pres.shanque.quecook.util.YamlUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FoodRecipeFactory extends AbstractRecipeFactory<FoodRecipe> {

    public FoodRecipeFactory(String location) {
        super(new File(Quecook.getInstance().getDataFolder(), location), FoodRecipe.class);
    }

    @Override
    protected void load() {
        final File configFile = getConfigFile();
        if (!configFile.exists() || configFile.isDirectory()) {
            return;
        }

        Map<String, FoodRecipe> newFoodRecipeMap;
        try {
            newFoodRecipeMap = YamlUtils.toBeanMap(configFile.toURI(), getRecipeType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (newFoodRecipeMap.isEmpty()) {
            return;
        }

        newFoodRecipeMap.forEach((k, v) -> v.setId(k));
        newFoodRecipeMap = check(newFoodRecipeMap);

        final Map<String, FoodRecipe> recipeMap = getRecipeMap();
        recipeMap.putAll(newFoodRecipeMap);
    }

    private Map<String, FoodRecipe> check(Map<String, FoodRecipe> foodRecipeMap) {
        return foodRecipeMap;
    }
}
