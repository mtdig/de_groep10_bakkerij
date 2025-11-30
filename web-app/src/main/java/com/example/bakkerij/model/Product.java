package com.example.bakkerij.model;

import java.util.Objects;

public class Product {
    private final int id;
    private final String nameNl;
    private final String nameFr;
    private final String nameEn;
    private final String nameDe;
    private final String nameEs;
    private final String nameZh;
    private final String descriptionNl;
    private final String descriptionFr;
    private final String descriptionEn;
    private final String descriptionDe;
    private final String descriptionEs;
    private final String descriptionZh;
    private final double price;
    private final String image;
    private final String category;

    public Product(int id, String nameNl, String nameFr, String nameEn, String nameDe, String nameEs, String nameZh,
                  String descriptionNl, String descriptionFr, String descriptionEn, String descriptionDe, 
                  String descriptionEs, String descriptionZh, double price, String image, String category) {
        this.id = id;
        this.nameNl = nameNl;
        this.nameFr = nameFr;
        this.nameEn = nameEn;
        this.nameDe = nameDe;
        this.nameEs = nameEs;
        this.nameZh = nameZh;
        this.descriptionNl = descriptionNl;
        this.descriptionFr = descriptionFr;
        this.descriptionEn = descriptionEn;
        this.descriptionDe = descriptionDe;
        this.descriptionEs = descriptionEs;
        this.descriptionZh = descriptionZh;
        this.price = price;
        this.image = image;
        this.category = category;
    }

    public int getId() { return id; }
    public String getNameNl() { return nameNl; }
    public String getNameFr() { return nameFr; }
    public String getNameEn() { return nameEn; }
    public String getNameDe() { return nameDe; }
    public String getNameEs() { return nameEs; }
    public String getNameZh() { return nameZh; }
    public String getDescriptionNl() { return descriptionNl; }
    public String getDescriptionFr() { return descriptionFr; }
    public String getDescriptionEn() { return descriptionEn; }
    public String getDescriptionDe() { return descriptionDe; }
    public String getDescriptionEs() { return descriptionEs; }
    public String getDescriptionZh() { return descriptionZh; }
    public double getPrice() { return price; }
    public String getImage() { return image; }
    public String getCategory() { return category; }
    
    public String getNameForLanguage(String lang) {
        return switch (lang) {
            case "fr" -> nameFr;
            case "en" -> nameEn;
            case "de" -> nameDe;
            case "es" -> nameEs;
            case "zh" -> nameZh;
            default -> nameNl;
        };
    }
    
    public String getDescriptionForLanguage(String lang) {
        return switch (lang) {
            case "fr" -> descriptionFr;
            case "en" -> descriptionEn;
            case "de" -> descriptionDe;
            case "es" -> descriptionEs;
            case "zh" -> descriptionZh;
            default -> descriptionNl;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", nameNl='" + nameNl + "', price=" + price + ", category='" + category + "'}";
    }
}
