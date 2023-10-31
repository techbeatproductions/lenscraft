package com.example.lenscraft.PackageRecyclerView;

public class ItemModel {
    private int id;
    private String brand;
    private String model;
    private String description;
    private double amount;

    public ItemModel(int id, String brand, String model, String description, double amount) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.amount = amount;
    }

    // Getter and setter methods for the attributes

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
