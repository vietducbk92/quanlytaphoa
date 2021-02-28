/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.depot;

import com.vdbk.apps.quanlybanhang.database.Item;

/**
 *
 * @author vietd
 */
public class ItemDialogResult {

    static int RESULT_CANCEL = 0;
    static int RESULT_ENTER = 1;
    static int RESULT_DELETE = -1;
    static int RESULT_NEW_ITEM = 2;
    static int RESULT_UPDATE = 3;
    static int RESULT_INSERT = 4;
    public Item item;
    private int result = 0;

    public ItemDialogResult(int result, Item item) {
        this.result = result;
        this.item = item;
    }

    public boolean isCancel() {
        return result == RESULT_CANCEL;
    }

    public boolean isEnter() {
        return result == RESULT_ENTER;
    }

    public boolean isDelete() {
        return result == RESULT_DELETE;
    }

    public boolean isAddNewItem() {
        return result == RESULT_NEW_ITEM;
    }
    
    public boolean isUpdateItem(){
        return result == RESULT_UPDATE;
    }
    
    public boolean isInsertNewItem(){
        return result == RESULT_INSERT;
    }
}
