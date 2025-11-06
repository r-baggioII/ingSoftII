package com.greedy_cars_institucional.institucional.model;

public class CatalogStats {
    private final int totalVehicles;
    private final int totalUnits;
    private final int availableUnits;

    public CatalogStats(int totalVehicles, int totalUnits, int availableUnits) {
        this.totalVehicles = totalVehicles;
        this.totalUnits = totalUnits;
        this.availableUnits = availableUnits;
    }

    public int getTotalVehicles() {
        return totalVehicles;
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public int getAvailableUnits() {
        return availableUnits;
    }

    public int getRentedUnits() {
        return Math.max(totalUnits - availableUnits, 0);
    }
}
