package com.greedy_cars_institucional.institucional.model;

import java.util.Collections;
import java.util.List;

public class VehicleCatalogData {

    private final List<VehicleCardView> vehicles;
    private final CatalogStats stats;

    public VehicleCatalogData(List<VehicleCardView> vehicles, CatalogStats stats) {
        this.vehicles = vehicles != null ? vehicles : Collections.emptyList();
        this.stats = stats != null ? stats : new CatalogStats(0, 0, 0);
    }

    public List<VehicleCardView> getVehicles() {
        return vehicles;
    }

    public CatalogStats getStats() {
        return stats;
    }

    public static VehicleCatalogData empty() {
        return new VehicleCatalogData(Collections.emptyList(), new CatalogStats(0, 0, 0));
    }
}
