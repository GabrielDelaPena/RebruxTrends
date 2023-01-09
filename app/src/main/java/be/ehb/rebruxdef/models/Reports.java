package be.ehb.rebruxdef.models;

public class Reports {
    String id,creator,image,description,street,city,zip, createdAt;
    Boolean cleaned;
    Double lat,lng;

    public Reports() {
    }

    public Reports(String id, String creator, String image, String description, String street, String city, String zip, String createdAt, Boolean cleaned, Double lat, Double lng) {
        this.id = id;
        this.creator = creator;
        this.image = image;
        this.description = description;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.createdAt = createdAt;
        this.cleaned = cleaned;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getCleaned() {
        return cleaned;
    }

    public void setCleaned(Boolean cleaned) {
        this.cleaned = cleaned;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
