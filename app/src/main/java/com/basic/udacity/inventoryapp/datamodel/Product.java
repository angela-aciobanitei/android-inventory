package com.basic.udacity.inventoryapp.datamodel;

import android.graphics.Bitmap;

import com.basic.udacity.inventoryapp.utils.ImageUtils;

public class Product {

    private String mName;
    private Double mPrice;
    private Integer mQuantity;
    private String mSupplierName;
    private String mSupplierEmail;
    private String mSupplierPhone;
    private Bitmap mImage;

    public Product(String name) {
        mName = name;
        mPrice = 0.0;
        mQuantity = 0;
        mSupplierName = "";
        mSupplierEmail = "";
        mSupplierPhone ="";
    }

    public Product (String name, Double price, Integer quantity, String supplier,
                    String email, String phone, Bitmap image) {
        mName = name;
        mPrice = price;
        mQuantity = quantity;
        mSupplierName = supplier;
        mSupplierEmail = email;
        mSupplierPhone = phone;
        mImage = image;
    }

    public String getName() {
        return mName;
    }

    public Double getPrice() {
        return mPrice;
    }

    public Integer getQuantity() {
        return mQuantity;
    }

    public String getSupplierName() {
        return mSupplierName;
    }

    public String getSupplierEmail() {
        return mSupplierEmail;
    }

    public String getSupplierPhone() {
        return mSupplierPhone;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public byte[] getImageInBytes() {
        return ImageUtils.getByteArrayFromBitmap(mImage);
    }

    @Override
    public String toString() {
        return "Product {" +
                "productName='" + mName + '\'' +
                ", price='" + mPrice + '\'' +
                ", quantity=" + mQuantity +
                ", supplierName='" + mSupplierName + '\'' +
                ", supplierEmail='" + mSupplierEmail + '\'' +
                ", supplierPhone='" + mSupplierPhone + '\'' +
                '}';
    }
}


