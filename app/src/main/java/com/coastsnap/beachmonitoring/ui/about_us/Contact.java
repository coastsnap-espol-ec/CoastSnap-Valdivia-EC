package com.coastsnap.beachmonitoring.ui.about_us;

public class Contact {
    private int id;
    private final int img;
    private final String socialMediaName;
    private final String description;
    private final String uriSocialMedia;

    public Contact(int id, int img, String socialMediaName, String description, String uriSocial) {
        this.id = id;
        this.img = img;
        this.socialMediaName = socialMediaName;
        this.description = description;
        this.uriSocialMedia = uriSocial;
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

    /*public void setImg(int img) {
        this.img = img;
    }*/

    public String getSocialMediaName() {
        return socialMediaName;
    }

    /*public void setSocialMediaName(String socialMediaName) {
        this.socialMediaName = socialMediaName;
    }*/

    public String getDescription() {
        return description;
    }

    /*public void setDescription(String description) {
        this.description = description;
    }*/

    public String getUriSocialMedia() {
        return uriSocialMedia;
    }

    /*public void setUriSocialMedia(String uriSocialMedia) {
        this.uriSocialMedia = uriSocialMedia;
    }*/
}
