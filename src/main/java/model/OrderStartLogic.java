package model;

public class OrderStartLogic {
    public int updateGuestCount(int currentCount, String action) {
        int newCount = currentCount;
        
        if ("plus".equals(action)) {
            if (newCount < 9) {
                newCount++;
            }
        } else if ("minus".equals(action)) {
            if (newCount > 1) {
                newCount--;
            }
        }
        return newCount;
    }
}