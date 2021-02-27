/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.ui;

import com.vdbk.apps.quanlybanhang.database.Item;

/**
 *
 * @author vietd
 */
public class BillItem {

    private Item item;
    //
    private float number = 1;
    private double totalAmount;
    private double totalInterest;
    private double amount;

    public BillItem(Item item) {
        this.item = item;
        this.number = 1;
        totalAmount = calculateTotalAmount();
        totalInterest = calculateTotalInterest();
    }

    public Item getItem() {
        return this.item;
    }

    public double getAmount() {
        if (number >= item.getRetailMaxNumber() && item.getRetailMaxNumber() > 0) {
            return item.getWholeScalePrice()/item.getRetailMaxNumber();
        } else {
            return item.getRetailPrice();
        }
    }

    public void updateItem(Item newItem) {
        this.item = newItem;
        totalAmount = calculateTotalAmount();
        totalInterest = calculateTotalInterest();
    }

    public float getNumber() {
        return this.number;
    }

    public void updateNumber(float number) {
        this.number = number;
        totalAmount = calculateTotalAmount();
        totalInterest = calculateTotalInterest();
    }

    public void updateTotalAmount(double value) {
        this.totalAmount = value;
        double rnumber = totalAmount / item.getRetailPrice();
        if (value >= item.getWholeScalePrice()&& item.getWholeScalePrice() > 0) {
            int a = (int)(value / item.getWholeScalePrice());
            float b = (float) ((totalAmount - a*item.getWholeScalePrice()) / (item.getWholeScalePrice() / item.getRetailMaxNumber()));
            this.number = (float) (Math.ceil((a*item.getRetailMaxNumber()+b )* 100) / 100);
        } else {
            this.number = (float) (Math.ceil(rnumber * 100) / 100);
        }
    }

    public double getTotalAmount() {
        return this.totalAmount;
    }

    public double getTotalInterest() {
        return this.totalInterest;
    }

    //tinh tong so tien khach hang phai tra
    private double calculateTotalAmount() {
        double ret;
        if (number >= item.getRetailMaxNumber() && item.getRetailMaxNumber() > 0) {
            int a = (int) (number / item.getRetailMaxNumber());
            float b = number % item.getRetailMaxNumber();
            ret = a * item.getWholeScalePrice() + b * item.getWholeScalePrice() / item.getRetailMaxNumber();
        } else {
            ret = number * item.getRetailPrice();
        }
        return (Math.ceil(ret));
    }

    //tinh tong so tien lai
    private double calculateTotalInterest() {
        double ret = totalAmount - number * item.getOriginPrice();
        return (Math.ceil(ret));
    }
}
