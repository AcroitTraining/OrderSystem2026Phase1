package model;

import java.util.List;

public class ItemDetailsChangeLogic {
    
    /**
     * トッピングの現在の合計選択数を計算します
     */
    public int getTotalToppingQuantity(List<ItemDetailsInfo> toppingList) {
        int total = 0;
        if (toppingList != null) {
            for (ItemDetailsInfo t : toppingList) {
                total += t.getToppingQuantity();
            }
        }
        return total;
    }

    public void calcToppingQuantity(List<ItemDetailsInfo> toppingList, int index, String action) {
        if (toppingList == null) return;
        if (index < 0 || index >= toppingList.size()) return;
        ItemDetailsInfo t = toppingList.get(index);
        int qty = t.getToppingQuantity();
        
        // 全体の合計選択数を取得
        int totalQty = getTotalToppingQuantity(toppingList);
        
        // ＋
        if ("plus".equals(action)) {
            // 【変更】個別上限20個ではなく、全体の合計が4個未満、かつ個別在庫の範囲内のみプラス可能
            if (totalQty < 4 && qty < t.getToppingStock()) {
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