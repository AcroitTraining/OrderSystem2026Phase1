package model;

import java.util.List;

public class ItemDetailsLogic {
    
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
        if (toppingList == null || index < 0 || index >= toppingList.size()) {
            return;
        }
        ItemDetailsInfo target = toppingList.get(index);
        int currentQty = target.getToppingQuantity();
        
        // 全体の合計選択数を取得
        int totalQty = getTotalToppingQuantity(toppingList);

        if ("plus".equals(action)) {
            // 【変更】個別の上限20個ではなく、全体の合計が4個未満、かつ個別の在庫がある場合のみプラス
            if (totalQty < 4 && currentQty < target.getToppingStock()) {
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