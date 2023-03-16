package pres.shanque.quecook.recipe.factory;

import pres.shanque.quecook.recipe.entity.Recipe;

public interface RecipeFilter<T extends Recipe> {

    boolean filter(T recipe);
}
