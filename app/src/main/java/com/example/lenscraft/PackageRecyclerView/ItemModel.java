package com.example.lenscraft.PackageRecyclerView;

public class ItemModel {
    private int id; // The item's ID (general)
    private String brand;
    private String model;
    private String description;
    private double amount;
    private int lightId; // Additional field for light ID
    private int printableId; // Additional field for printable ID
    private int lensId; // Additional field for lens ID
    private int audioId; // Additional field for audio ID
    private int cameraId; // Additional field for camera ID

    public ItemModel(int id, String brand, String model, String description, double amount,
                     int lightId, int printableId, int lensId, int audioId, int cameraId) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.amount = amount;
        this.lightId = lightId;
        this.printableId = printableId;
        this.lensId = lensId;
        this.audioId = audioId;
        this.cameraId = cameraId;
    }

    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public int getLightId() {
        return lightId;
    }

    public int getPrintableId() {
        return printableId;
    }

    public int getLensId() {
        return lensId;
    }

    public int getAudioId() {
        return audioId;
    }

    public int getCameraId() {
        return cameraId;
    }
}
