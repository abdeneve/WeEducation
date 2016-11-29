package com.itup.weeducation.model;

/**
 * Created by Alex-Dell on 10/19/2016.
 */

public class ImageMessageBody extends FileMessageBody{
    private String thumbnailUrl;
    private int width;
    private int height;

    public ImageMessageBody() {
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
