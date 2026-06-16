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
    
    public int showPopUp(List<OrderListInfo> list, List<OrderHistoryInfo> list2,  String action) {
        System.out.println("orderhistory size: " + list.size() + ", action: " + action);
        
        // リストが空、または「お会計」ボタンが押されていない通常表示時はポップアップを出さない
        if (list2 == null || !"checkOut".equals(action)) {
            return 0;
        }
        
        // ======= ここからは「お会計ボタン」が押された（action が "checkOut" の）ときだけの処理 =======
        
        // 1件でも未提供（0）があれば、未提供ポップアップ（1）を表示
        for (OrderListInfo item : list) {
            if (item.getOrderFlag() == 0) {
                return 1; 
            }
        }
        
        // 全て提供済み（1）であれば、お会計確認ポップアップ（2）を表示
        return 2;
    }
}