package pres.shanque.quecook.recipe.entity;

import java.io.Serializable;
import java.util.Arrays;

public abstract class AbstractRecipe implements Recipe, Serializable {

    private static final long serialVersionUID = -1790340550334778307L;
    private String id;
    private String name;
    private String[] lores;
    private String[] materials;
    private int castTime;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String[] getLores() {
        return lores;
    }

    public void setLores(String[] lores) {
        this.lores = lores;
    }

    @Override
    public String[] getMaterials() {
        return materials;
    }

    public void setMaterials(String[] materials) {
        this.materials = materials;
    }

    @Override
    public int getCastTime() {
        return castTime;
    }

    public void setCastTime(int castTime) {
        this.castTime = castTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractRecipe)) {
            return false;
        }

        AbstractRecipe that = (AbstractRecipe) o;

        if (getCastTime() != that.getCastTime()) {
            return false;
        }
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
            return false;
        }
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getLores(), that.getLores())) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getMaterials(), that.getMaterials());
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + Arrays.hashCode(getLores());
        result = 31 * result + Arrays.hashCode(getMaterials());
        result = 31 * result + getCastTime();
        return result;
    }
}
