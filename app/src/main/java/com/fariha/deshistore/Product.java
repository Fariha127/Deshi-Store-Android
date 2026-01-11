package com.fariha.deshistore;

public class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private String unit;
    private String imageUrl;
    private String manufacturer;
    private String description;
    private int recommendCount;
    private boolean isFavorite;
    private boolean isApproved;
    private String vendorId;
    private String productId;
    private String status;
    private String rejectionReason;

    // Default constructor
    public Product() {
    }

    // Constructor for basic product
    public Product(String id, String name, String category, double price, String unit, 
                   String imageUrl, String manufacturer, int recommendCount, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.unit = unit;
        this.imageUrl = imageUrl;
        this.manufacturer = manufacturer;
        this.recommendCount = recommendCount;
        this.isFavorite = isFavorite;
        this.isApproved = false;
        this.vendorId = "";
    }

    // Full constructor
    public Product(String id, String name, String category, double price, String unit, 
                   String imageUrl, String manufacturer, int recommendCount, boolean isFavorite,
                   boolean isApproved, String vendorId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.unit = unit;
        this.imageUrl = imageUrl;
        this.manufacturer = manufacturer;
        this.recommendCount = recommendCount;
        this.isFavorite = isFavorite;
        this.isApproved = isApproved;
        this.vendorId = vendorId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getRecommendCount() {
        return recommendCount;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getProductId() {
        return productId;
    }

    public String getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setRecommendCount(int recommendCount) {
        this.recommendCount = recommendCount;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void incrementRecommendCount() {
        this.recommendCount++;
    }
}
