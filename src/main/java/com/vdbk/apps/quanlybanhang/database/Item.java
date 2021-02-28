/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.Document;

/**
 *
 * @author vietd
 */
public class Item {
    //barcode
    private String id = "";
    //ten
    private String name = "";
    //gia nhap
    private double originPrice = 0;
    //gia si
    private double wholeScalePrice = 0;
    // gia le
    private double retailPrice = 0;
    //so luong nho nhat de lay gia si
    private int retailMaxNumber = 0;
    //ghi chu
    private String note = "";
    //category
    private String category = "";
    //unit
    private String unit = "";
    //has barcode
    private int hasBarCode = 0;
    
    public static String KEY_ID = "_id";
    public static String KEY_NAME = "_name";
    public static String KEY_ORIGIN_PRICE = "_oiigin_price";
    public static String KEY_WHOLESCALE_PRICE = "_ws_price";
    public static String KEY_RETAIL_PRICE = "_rt_price";
    public static String KEY_RETAIL_MAX_NUMBER = "_rt_max_number";
    public static String KEY_NOTE = "_note";
    public static String KEY_CATEGORY = "_category";
    public static String KEY_UNIT = "_unit";
    public static String KEY_HAS_BARCODE = "_has_barcode";
    
    //public static String [] UNITS = {"cái","chai", "chiếc", "thùng", "hộp", "lon","lít","lạng","cân","gói"};

    public Item(Document obj) {
        setId((String) obj.get(KEY_ID));
        setName((String) obj.get(KEY_NAME));
        setOriginPrice((double) obj.get(KEY_ORIGIN_PRICE));
        setWholeScalePrice((double) obj.get(KEY_WHOLESCALE_PRICE));
        setRetailPrice((double) obj.get(KEY_RETAIL_PRICE));
        setRetailMaxNumber((int) obj.get(KEY_RETAIL_MAX_NUMBER));
        setNote((String) obj.get(KEY_NOTE));
        setCategory((String) obj.get(KEY_CATEGORY));
        setUnit((String) obj.get(KEY_UNIT));
        if(obj.containsKey(KEY_HAS_BARCODE))
            setHasBarcode((int) obj.get(KEY_HAS_BARCODE));
    }

    public Item() {
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name.toUpperCase();
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public void setOriginPrice(double price){
        if(price < 1000)
            price = price *1000;
        this.originPrice = price;
    }
    
    public double getOriginPrice(){
        return this.originPrice;
    }

    /**
     * @return the wholeScalePrice
     */
    public double getWholeScalePrice() {
        if(wholeScalePrice <= 0)
            return 0;
        return wholeScalePrice;
    }

    /**
     * @param wholeScalePrice the wholeScalePrice to set
     */
    public void setWholeScalePrice(double wholeScalePrice) {
        if(wholeScalePrice < 1000)
            wholeScalePrice = wholeScalePrice *1000;
        this.wholeScalePrice = wholeScalePrice;
    }

    /**
     * @return the retailPrice
     */
    public double getRetailPrice() {
        return retailPrice;
    }

    /**
     * @param retailPrice the retailPrice to set
     */
    public void setRetailPrice(double retailPrice) {
        if(retailPrice < 1000)
            retailPrice = retailPrice *1000;
        this.retailPrice = retailPrice;
    }

    /**
     * @return the retailMaxNumber
     */
    public int getRetailMaxNumber() {
        return retailMaxNumber;
    }

    /**
     * @param retailMaxNumber the retailMaxNumber to set
     */
    public void setRetailMaxNumber(int retailMaxNumber) {
        this.retailMaxNumber = retailMaxNumber;
    }
   
    public void setNote(String note){
        this.note = note;
    }
    
    public String getNote(){
        return this.note;
    }
    
    public void setCategory(String category){
        this.category = category;
    }
    
    public String getCategory(){
        return this.category;
    }
    
    public void  setUnit(String unit){
        this.unit = unit;
    }
    
    public void setHasBarcode(int value){
        this.hasBarCode = value;
    }
    
    public boolean hasBarCode(){
        return this.hasBarCode != 0;
    }
    
    public String getUnit(){
        return this.unit;
    }
    
    
    public Document convertToDocument(){
        Document obj = new Document();
        obj.append(KEY_ID, id);
        obj.append(KEY_NAME, name);
        obj.append(KEY_ORIGIN_PRICE, originPrice);
        obj.append(KEY_CATEGORY, category);
        obj.append(KEY_NOTE,note);
        obj.append(KEY_RETAIL_MAX_NUMBER,retailMaxNumber);
        obj.append(KEY_RETAIL_PRICE, retailPrice);
        obj.append(KEY_WHOLESCALE_PRICE,wholeScalePrice);
        obj.append(KEY_UNIT, unit);
        obj.append(KEY_HAS_BARCODE,hasBarCode);
        return obj;
    }
    public Document convertToDocumentWithoutId(){
        Document obj = new Document();
        obj.append(KEY_NAME, name);
        obj.append(KEY_ORIGIN_PRICE, originPrice);
        obj.append(KEY_CATEGORY, category);
        obj.append(KEY_NOTE,note);
        obj.append(KEY_RETAIL_MAX_NUMBER,retailMaxNumber);
        obj.append(KEY_RETAIL_PRICE, retailPrice);
        obj.append(KEY_WHOLESCALE_PRICE,wholeScalePrice);
        obj.append(KEY_UNIT, unit);
        obj.append(KEY_HAS_BARCODE,hasBarCode);
        return obj;
    }

//    public int getUnitIndex() {
//        int index = 0;
//        for(String unit: UNITS){
//            if(unit.equals (this.unit))
//                break;
//            index ++;
//        }
//        return index;
//    }
}
