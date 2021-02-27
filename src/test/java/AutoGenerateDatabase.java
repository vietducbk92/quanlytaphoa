
import com.vdbk.apps.quanlybanhang.database.DatabaseManager;
import com.vdbk.apps.quanlybanhang.database.Item;
import java.net.UnknownHostException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vietd
 */
public class AutoGenerateDatabase {

    private static Item autoGenerateItem(int i) {
        Item item = new Item();
        item.setCategory("category"+i);
        item.setId(System.currentTimeMillis()+""+i);
        item.setName("Mặt hàng"+i);
        item.setNote("Ghi chú  "+i);
        item.setOriginPrice((i+1)*10000);
        item.setWholeScalePrice((i+1)*10000+2000);
        item.setRetailMaxNumber(i);
        item.setRetailPrice((i+1)*10000+5000);
        item.setUnit("cái");
        item.setHasBarcode(i%2);
        return item;
    }
    int MAX_ITEM = 500;
    public static void main(String args[]) throws UnknownHostException {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        for(int i = 0;i<300;i++){
            Item item = autoGenerateItem(i);
            databaseManager.insertItem(item);
        }
    }
}
