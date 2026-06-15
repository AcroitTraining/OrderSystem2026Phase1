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
    	System.out.println("orderhistory"+list.size());
    	// リストが空ならポップアップは出さない
        if (list == null || list.isEmpty()) {
            return 0;
        }
        
        // 【最優先チェック】1つでも提供フラグが0（未提供）なら、
        // アクションに関係なく（checkOutでもyesでも）絶対に未提供ポップアップを表示する
        for (OrderHistoryInfo item : list) {
            if (item.getOrderFlag() == 0) {
                return 1;
            }
        }
        
        // 全て提供済みで、かつお会計ボタン（最初のボタン）が押されたなら確認ポップアップ
        if ("checkOut".equals(action)) {
            return 2;
        }
        
        // それ以外（通常表示や、全て提供済みで「はい」が進むときなど）
        return 0;
    }
}