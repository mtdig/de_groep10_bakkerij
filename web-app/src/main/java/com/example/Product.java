package com.example;

public class Product {
    public int id;
    public String nameNl;
    public String nameFr;
    public String nameEn;
    public String nameDe;
    public String nameEs;
    public String nameZh;
    public String descriptionNl;
    public String descriptionFr;
    public String descriptionEn;
    public String descriptionDe;
    public String descriptionEs;
    public String descriptionZh;
    public double price;
    public String image;
    public String category;

    public Product(int id, String nameNl, String nameFr, String nameEn, String nameDe, String nameEs, String nameZh,
                  String descriptionNl, String descriptionFr, String descriptionEn, String descriptionDe, String descriptionEs, String descriptionZh,
                  double price, String image, String category) {
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
}
