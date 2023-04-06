package pres.shanque.quecook.recipe.entity;

public class FoodRecipe extends AbstractRecipe {

    private String itemId;
    private String itemData;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemData() {
        return itemData;
    }

    public void setItemData(String itemData) {
        this.itemData = itemData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FoodRecipe)) {
            return false;
        }

        FoodRecipe that = (FoodRecipe) o;

        if (getItemId() != null ? !getItemId().equals(that.getItemId()) : that.getItemId() != null) {
            return false;
        }
        return getItemData() != null ? getItemData().equals(that.getItemData()) : that.getItemData() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getItemId() != null ? getItemId().hashCode() : 0);
        result = 31 * result + (getItemData() != null ? getItemData().hashCode() : 0);
        return result;
    }
}
