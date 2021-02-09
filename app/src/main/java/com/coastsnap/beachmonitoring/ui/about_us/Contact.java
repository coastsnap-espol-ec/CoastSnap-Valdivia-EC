package com.coastsnap.beachmonitoring.ui.about_us;

public class Contact {
    private int id;
    private int img;
    private String socialMediaName;
    private String description;

    public Contact(int id, int img, String socialMediaName, String description) {
        this.id = id;
        this.img = img;
        this.socialMediaName = socialMediaName;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getSocialMediaName() {
        return socialMediaName;
    }

    public void setSocialMediaName(String socialMediaName) {
        this.socialMediaName = socialMediaName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
