package model;

import java.util.List;

public class ItemDetailsChangeLogic {
    public void calcToppingQuantity(List<ItemDetailsInfo> toppingList, int index, String action) {
        if (toppingList == null) return;
        if (index < 0 || index >= toppingList.size()) return;
        ItemDetailsInfo t = toppingList.get(index);
        int qty = t.getToppingQuantity();
        // ＋
        if ("plus".equals(action)) {
            if (qty < 20 && qty < t.getToppingStock()) {
                t.setToppingQuantity(qty + 1);
            }
        }
        // －
        if ("minus".equals(action)) {
            if (qty > 0) {
                t.setToppingQuantity(qty - 1);
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
}