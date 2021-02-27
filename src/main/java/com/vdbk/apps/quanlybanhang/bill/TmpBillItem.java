/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.bill;

/**
 *
 * @author vietd
 */
public class TmpBillItem implements BillItem{
    private String name = "";
    private double totalPrice = 0;
    
    public TmpBillItem(String name, double price){
        this.name = name;
        this.totalPrice = price;
    }
    
    public void updateName(String name){
        this.name = name;
    }
    
    public void updateTotalPrice(double price){
        this.totalPrice = price;
    }
    
    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public double getUnitPrice() {
        return 0;
    }

    @Override
    public float getNumber() {
        return 1;
    }

    @Override
    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public double getInterest() {
        return 0;
    }

    @Override
    public String getUnit() {
        return "";
    }
    
}
