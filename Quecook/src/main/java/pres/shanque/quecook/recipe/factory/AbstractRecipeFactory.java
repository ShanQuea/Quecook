package pres.shanque.quecook.recipe.factory;

import pres.shanque.quecook.recipe.entity.AbstractRecipe;
import pres.shanque.quecook.util.asserts.Asserts;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRecipeFactory<T extends AbstractRecipe> implements RecipeFactory<T> {

    private final File configFile;
    private final Class<T> recipeType;
    private final Map<String, T> recipeMap = new ConcurrentHashMap<>(16);

    public AbstractRecipeFactory(File configFile, Class<T> recipeType) {
        Asserts.notNull(configFile, "configFile must not be null");
        Asserts.notNull(recipeType, "recipeType must not be null");

        this.configFile = configFile;
        this.recipeType = recipeType;

        load();
    }

    protected abstract void load();

    public void reload() {
        clear();
        load();
    }

    @Override
    public T getRecipe(String id) {
        return recipeMap.get(id);
    }

    @Override
    @SuppressWarnings({"all"})
    public T[] findRecipe(RecipeFilter<T> filter) {
        if (recipeMap.size() == 0) {
            return null;
        }

        final T[] recipes = (T[]) recipeMap.values()
                .stream().filter(filter::filter).toArray();

        return recipes.length == 0 ? null : recipes;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Class<T> getRecipeType() {
        return recipeType;
    }

    @Override
    @SuppressWarnings({"all"})
    public T[] getAllRecipes() {
        if (recipeMap.size() == 0) {
            return null;
        }

        return (T[]) recipeMap.values().toArray();
    }

    protected Map<String, T> getRecipeMap() {
        return this.recipeMap;
    }

    public void clear() {
        recipeMap.clear();
    }
}
