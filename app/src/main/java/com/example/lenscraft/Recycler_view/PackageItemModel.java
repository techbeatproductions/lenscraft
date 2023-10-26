package com.example.lenscraft.Recycler_view;

public class PackageItemModel {
    public static final int CAMERA = 0;
    public static final int LENS = 1;

    private int id;
    private String brand;
    private double amount;
    private int itemType;

    public PackageItemModel(int id, String brand, double amount, int itemType) {
        this.id = id;
        this.brand = brand;
        this.amount = amount;
        this.itemType = itemType;
    }

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
