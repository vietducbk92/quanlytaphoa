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
import java.net.UnknownHostException;
import java.util.ArrayList;

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

    public interface DatabaseListener {

        void onNewItemInserted(Item newItem);

        void onItemUpdated(Item updatedItem);

        void onItemDeleted(String id);
    }

    private MongoClient mongoClient;
    private DB sellManagerDB;
    private DBCollection depotCollection;
    private static String SELL_MANAGER_DB = "SellManagerDB";
    private static String DEPOT_COLLECTION = "depot";

    private DatabaseManager() throws UnknownHostException {
        mongoClient = MongoUtils.getMongoClient();
        sellManagerDB = mongoClient.getDB(SELL_MANAGER_DB);
        depotCollection = sellManagerDB.getCollection(DEPOT_COLLECTION);
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
        DBCursor cursor = depotCollection.find();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            listener.onItemAvailable(new Item(obj));
        }
    }

    public void getAllNonBarcodeItems(ItemAvailableListener listener) {
        BasicDBObjectBuilder whereBuilder = BasicDBObjectBuilder.start();
        whereBuilder.append(Item.KEY_HAS_BARCODE, 0);
        DBObject where = whereBuilder.get();
        DBCursor cursor = depotCollection.find(where);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            listener.onItemAvailable(new Item(obj));
        }
    }

    public void getAllItemsInCategory(String category, ItemAvailableListener listener) {
        BasicDBObjectBuilder whereBuilder = BasicDBObjectBuilder.start();
        whereBuilder.append(Item.KEY_CATEGORY, category);
        DBObject where = whereBuilder.get();
        DBCursor cursor = depotCollection.find(where);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            listener.onItemAvailable(new Item(obj));
        }
    }

    public Item getItem(String code) {
        BasicDBObjectBuilder whereBuilder = BasicDBObjectBuilder.start();
        whereBuilder.append(Item.KEY_ID, code);
        DBObject where = whereBuilder.get();
        DBCursor cursor = depotCollection.find(where);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            return new Item(obj);
        }
        return null;
    }

    public void insertItem(Item item) {
        BasicDBObject obj = item.convertToDBOject();
        depotCollection.insert(obj);
        for (DatabaseListener listener : listeners) {
            listener.onNewItemInserted(item);
        }
    }

    public void update(Item newItem) {
        BasicDBObjectBuilder whereBuilder = BasicDBObjectBuilder.start();
        whereBuilder.append(Item.KEY_ID, newItem.getId());
        DBObject where = whereBuilder.get();
        WriteResult result = depotCollection.update(where, newItem.convertToDBOject());
        for (DatabaseListener listener : listeners) {
            listener.onItemUpdated(newItem);
        }
    }

    public void deleteItem(String code) {
        BasicDBObjectBuilder whereBuilder = BasicDBObjectBuilder.start();
        whereBuilder.append(Item.KEY_ID, code);
        DBObject where = whereBuilder.get();
        WriteResult result = depotCollection.remove(where);
        for (DatabaseListener listener : listeners) {
            listener.onItemDeleted(code);
        }
    }
}
