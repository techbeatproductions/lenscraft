package com.example.lenscraft.Recycler_view;

public class ImageButtonModel {
    private int imageResourceId;
    private String labelText;

    public ImageButtonModel(int imageResourceId, String labelText) {
        this.imageResourceId = imageResourceId;
        this.labelText = labelText;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getLabelText() {
        return labelText;
    }
}
