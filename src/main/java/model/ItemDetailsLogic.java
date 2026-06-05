package model;

import java.util.List;
public class ItemDetailsLogic {
    public void calcToppingQuantity(List<ItemDetailsInfo> toppingList, int index, String action) {
        if (toppingList == null || index < 0 || index >= toppingList.size()) {
            return;
        }
        ItemDetailsInfo target = toppingList.get(index);
        int currentQty = target.getToppingQuantity();
        if ("plus".equals(action)) {
            if (currentQty < 20 && currentQty < target.getToppingStock()) {
                target.setToppingQuantity(currentQty + 1);
            }
        } else if ("minus".equals(action)) {
            if (currentQty > 0) {
                target.setToppingQuantity(currentQty - 1);
            }
        }
    }
    // 小計
    public int calcSubTotal(int productPrice, List<ItemDetailsInfo> toppingList) {
        int total = productPrice;
        if (toppingList != null) {
            for (ItemDetailsInfo t : toppingList) {
                total += t.getToppingPrice() * t.getToppingQuantity();
            }
        }
        return total;
    }
    public boolean isToppingRequired(String category) {
        return false;
    }
}