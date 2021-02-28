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
import com.mongodb.client.model.Updates;
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

    public interface DatabaseListener {

        void onNewItemInserted(Item newItem);

        void onItemUpdated(Item updatedItem);

        void onItemDeleted(String id);
    }

    private MongoClient mongoClient;
    private MongoDatabase sellManagerDB;
    private MongoCollection<Document> depotCollection;
    private static String SELL_MANAGER_DB = "SellManagerDB";
    private static String DEPOT_COLLECTION = "depot";

    private DatabaseManager() throws UnknownHostException {
        mongoClient = MongoUtils.getMongoClient();
        sellManagerDB = mongoClient.getDatabase(SELL_MANAGER_DB);
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

    public ArrayList<Item> getItems(String code){
        ArrayList items = new ArrayList<Item>();
        FindIterable<org.bson.Document> iterDoc = depotCollection.find(Filters.regex("_id", code));
        Iterator<org.bson.Document> it = iterDoc.iterator();
        
        while (it.hasNext()) {
            items.add(new Item(it.next()));
        }
        return items;
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
}
