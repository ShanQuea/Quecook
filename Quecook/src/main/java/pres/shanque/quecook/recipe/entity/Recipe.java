package pres.shanque.quecook.recipe.entity;

public interface Recipe {

    String getId();

    String getName();

    String[] getLores();

    String[] getMaterials();

    int getCastTime();
}
