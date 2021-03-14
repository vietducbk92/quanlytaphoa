/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vdbk.apps.quanlybanhang.database;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.vdbk.apps.quanlybanhang.bill.Bill;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

/**
 *
 * @author vietd
 */
public class DatabaseManager {

    private static DatabaseManager instance = null;
    private ArrayList<DatabaseListener> listeners = new ArrayList<DatabaseListener>();

    synchronized public static DatabaseManager getInstance() throws UnknownHostException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public interface ItemAvailableListener {

        void onItemAvailable(Item item);
    }
    
    public interface BillItemAvailableListener {

        void onItemAvailable(Bill item);
    }

    public interface DatabaseListener {

        void onNewItemInserted(Item newItem);

        void onItemUpdated(Item updatedItem);

        void onItemDeleted(String id);

        void onNewBillInserted(Bill bill);

        void onBillDeleted(String id);
    }

    private MongoClient mongoClient;
    private MongoDatabase sellManagerDB;
    private MongoCollection<Document> depotCollection;
    private MongoCollection<Document> billCollection;
    private static String SELL_MANAGER_DB = "SellManagerDB";
    private static String DEPOT_COLLECTION = "depot";
    private static String BILL_COLLECTION = "bill";

    private DatabaseManager() throws UnknownHostException {
        mongoClient = MongoUtils.getMongoClient();
        sellManagerDB = mongoClient.getDatabase(SELL_MANAGER_DB);
        depotCollection = sellManagerDB.getCollection(DEPOT_COLLECTION);
        billCollection = sellManagerDB.getCollection(BILL_COLLECTION);
    }

    public void addDatabaseListener(DatabaseListener listener) {
        listeners.add(listener);
    }

    public void removeDatabaseListener(DatabaseListener listener) {
        listeners.remove(listener);
    }

    public void clearAllDatabaseListener() {
        listeners.clear();
    }

    public void getAllItems(ItemAvailableListener listener) {
        FindIterable<Document> iterDoc = depotCollection.find();
        Iterator<Document> it = iterDoc.iterator();
        while (it.hasNext()) {
            Document obj = it.next();
            listener.onItemAvailable(new Item(obj));
        }
    }

    public void getAllNonBarcodeItems(ItemAvailableListener listener) {
        FindIterable<Document> iterDoc = depotCollection.find(Filters.eq(Item.KEY_HAS_BARCODE, 0));
        Iterator<Document> it = iterDoc.iterator();
        while (it.hasNext()) {
            Document obj = it.next();
            listener.onItemAvailable(new Item(obj));
        }
    }

    public void getAllItemsInCategory(String category, ItemAvailableListener listener) {
        FindIterable<Document> iterDoc = depotCollection.find(Filters.eq(Item.KEY_CATEGORY, category));
        Iterator<Document> it = iterDoc.iterator();
        while (it.hasNext()) {
            Document obj = it.next();
            listener.onItemAvailable(new Item(obj));
        }
    }

    public ArrayList<Item> getItems(String code) {
        ArrayList items = new ArrayList<Item>();
        FindIterable<org.bson.Document> iterDoc = depotCollection.find(Filters.regex("_id", code));
        Iterator<org.bson.Document> it = iterDoc.iterator();

        while (it.hasNext()) {
            items.add(new Item(it.next()));
        }
        return items;
    }

    public Item getItem(String code) {
        FindIterable<org.bson.Document> iterDoc = depotCollection.find(Filters.regex("_id", code));
        Iterator<org.bson.Document> it = iterDoc.iterator();

        while (it.hasNext()) {
            return new Item(it.next());
        }
        return null;
    }

    public void insertItem(Item item) {
        Document obj = item.convertToDocument();
        depotCollection.insertOne(obj);
        for (DatabaseListener listener : listeners) {
            listener.onNewItemInserted(item);
        }
    }

    public void update(Item newItem) {
        Bson updateOperationDocument = new Document("$set", newItem.convertToDocumentWithoutId());
        depotCollection.updateOne(Filters.eq(Item.KEY_ID, newItem.getId()), updateOperationDocument);
        for (DatabaseListener listener : listeners) {
            listener.onItemUpdated(newItem);
        }
    }

    public void deleteItem(String code) {
        depotCollection.deleteOne(Filters.eq(Item.KEY_ID, code));
        for (DatabaseListener listener : listeners) {
            listener.onItemDeleted(code);
        }
    }

    //bill
    public void getAllBills(BillItemAvailableListener listener) {
        FindIterable<Document> iterDoc = billCollection.find().sort(Sorts.descending("_id")).limit(100);
        Iterator<Document> it = iterDoc.iterator();
        while (it.hasNext()) {
            Document obj = it.next();
            listener.onItemAvailable(new Bill(obj));
        }
    }
    
    public Bill getBill(String id) {
        FindIterable<org.bson.Document> iterDoc = billCollection.find(Filters.regex("_id", id));
        Iterator<org.bson.Document> it = iterDoc.iterator();

        while (it.hasNext()) {
            return new Bill(it.next());
        }
        return null;
    }
    
    public void insertNewBill(Bill bill) {
        Document obj = bill.convertToDocument();
        billCollection.insertOne(obj);
        for (DatabaseListener listener : listeners) {
            listener.onNewBillInserted(bill);
        }
    }

    public void deleteBill(String id) {
        billCollection.deleteOne(Filters.eq(Bill.KEY_ID, id));
        for (DatabaseListener listener : listeners) {
            listener.onItemDeleted(id);
        }
    }
}
