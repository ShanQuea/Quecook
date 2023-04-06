package pres.shanque.quecook.recipe.factory;

import pres.shanque.quecook.recipe.entity.Recipe;

public interface RecipeFactory<T extends Recipe> {

    T getRecipe(String id);

    T[] findRecipe(RecipeFilter<T> filter);

    T[] getAllRecipes();
}
