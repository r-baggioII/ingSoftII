package com.greedy_cars_institucional.institucional.model;

public class VehicleCardView {

    private final String id;
    private final String brand;
    private final String model;
    private final long year;
    private final int seats;
    private final int doors;
    private final int totalUnits;
    private final int availableUnits;
    private final Double dailyCost;
    private final String imageUrl;
    private final String categoryClass;

    public VehicleCardView(String id, String brand, String model, long year, int seats, int doors,
            int totalUnits, int availableUnits, Double dailyCost, String imageUrl, String categoryClass) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.seats = seats;
        this.doors = doors;
        this.totalUnits = totalUnits;
        this.availableUnits = availableUnits;
        this.dailyCost = dailyCost;
        this.imageUrl = imageUrl;
        this.categoryClass = categoryClass;
    }

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public long getYear() {
        return year;
    }

    public int getSeats() {
        return seats;
    }

    public int getDoors() {
        return doors;
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

    public Double getDailyCost() {
        return dailyCost;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategoryClass() {
        return categoryClass;
    }

    public String getDisplayName() {
        StringBuilder builder = new StringBuilder();
        if (brand != null && !brand.isBlank()) {
            builder.append(brand.trim());
        }
        if (model != null && !model.isBlank()) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(model.trim());
        }
        if (year > 0) {
            builder.append(" ").append(year);
        }
        return builder.length() == 0 ? "Vehículo" : builder.toString();
    }
}
