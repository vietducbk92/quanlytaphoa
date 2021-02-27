/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.bill;

import com.vdbk.apps.quanlybanhang.database.Item;

/**
 *
 * @author vietd
 */
public class NewBillItem implements BillItem {

    private Item item = null;
    private float number = 1;
    private double totalPrice = 0;
    private double totalInterest = 0;
    private double unitPrice = 0;

    public NewBillItem(Item item) {
        this.item = item;
        this.number = 1;
        unitPrice = item.getRetailPrice();
        totalPrice = calculateTotalPrice();
        totalInterest = calculateTotalInterest();
    }

    @Override
    public String getId() {
        return item.getId();
    }
    
    //ten
    @Override
    public String getName() {
        return item.getName();
    }

    // đơn giá
    @Override
    public double getUnitPrice() {
        return unitPrice;
    }

    // số lượng
    @Override
    public float getNumber() {
        return number;
    }

    //tổng giá tiền
    @Override
    public double getTotalPrice() {
        return totalPrice;
    }

    // unit
    @Override
    public String getUnit() {
        return item.getUnit();
    }

    //tiền lãi
    @Override
    public double getInterest() {
        return totalInterest;
    }
    public String getNote(){
        return item.getNote();
    }
    
    //cap nhat lại khi giá bán của item bị thay đổi
    public void updateItem(Item newItem) {
        this.item = newItem;
        totalPrice = calculateTotalPrice();
        totalInterest = calculateTotalInterest();
    }

    //thay đổi số lượng hàng trong hóa đơn
    public void updateNumber(float value) {
        this.number = value;
        totalPrice = calculateTotalPrice();
        totalInterest = calculateTotalInterest();
    }

    //thay đổi tổng tiền của hàng trong hóa đơn
    public void updateTotalPrice(double value) {
        this.totalPrice = value;
        float tmpAmount;
        double rnumber = totalPrice / item.getRetailPrice();
        if (value >= item.getWholeScalePrice() && item.getWholeScalePrice() > 0) {
            int a = (int) (value / item.getWholeScalePrice());
            float b = (float) ((totalPrice - a * item.getWholeScalePrice()) / (item.getWholeScalePrice() / item.getRetailMaxNumber()));
            tmpAmount = (a * item.getRetailMaxNumber() + b);
           // this.amount = (float) (Math.ceil((a * item.getRetailMaxNumber() + b) * 100) / 100);
        } else {
            tmpAmount = (float) rnumber;
           // this.amount = (float) (Math.ceil(rnumber * 100) / 100);
        }
        this.unitPrice = Math.ceil(totalPrice / tmpAmount);
        this.number = (float) (Math.ceil(tmpAmount * 100) / 100);
    }

    //tinh tong so tien khach hang phai tra
    private double calculateTotalPrice() {
        double ret;
        if (number >= item.getRetailMaxNumber() && item.getRetailMaxNumber() > 0) {
            int a = (int) (number / item.getRetailMaxNumber());
            float b = number % item.getRetailMaxNumber();
            ret = a * item.getWholeScalePrice() + b * item.getWholeScalePrice() / item.getRetailMaxNumber();
        } else {
            ret = number * item.getRetailPrice();
        }
        this.unitPrice = Math.ceil(ret / number);
        return (Math.ceil(ret));
    }

    //tinh tong so tien lai
    private double calculateTotalInterest() {
        double ret = totalPrice - number * item.getOriginPrice();
        return (Math.ceil(ret));
    }

}
