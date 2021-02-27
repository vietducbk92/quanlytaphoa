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
public interface BillItem {
    public String getId();
    public String getName();//ten
    public double getUnitPrice();//don gia
    public float getNumber();// so luong
    public double getTotalPrice();// tong tien
    public double getInterest();// tien lai
    public String getUnit();
}
