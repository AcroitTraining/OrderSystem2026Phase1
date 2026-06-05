package model;

import java.util.List;

public class OrderHistoryLogic {
    public int calcTotalOrderPrice(List<OrderHistoryInfo> list) {
        int total = 0;
        if(list == null) return 0;
        for (OrderHistoryInfo item : list) {
            total += item.getSubTotal();
        }
        return total;
    }
    
    public int calcTotalOrderQuantity(List<OrderHistoryInfo> list) {
        int total = 0;
        if(list == null) return 0;
        for (OrderHistoryInfo item : list) {
            total += item.getOrderQuantity();
        }
        return total;
    }
    
    public int showPopUp(List<OrderHistoryInfo> list, String action) {
        // お会計ボタン押下時のみ判定
        if (list == null || list.isEmpty() || !"checkOut".equals(action)) {
            return 0;
        }
        
        // 1つでも提供フラグが0（未提供）なら未提供ポップアップ
        for (OrderHistoryInfo item : list) {
            if (item.getOrderFlag() == 0) {
                return 1;
            }
        }
        // 全て提供済みならお会計確認ポップアップ
        return 2;
    }
}