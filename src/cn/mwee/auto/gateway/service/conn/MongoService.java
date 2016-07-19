package cn.mwee.auto.gateway.service.conn;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.mwee.auto.gateway.util.GatewayConst.*;

@Service
public class MongoService {

    private Logger logger = LoggerFactory.getLogger(MongoService.class);

    private static Map<String, MongoCollection<Document>> mongo_collections = new HashMap<String, MongoCollection<Document>>();

    private static Map<String, MongoDatabase> mongo_databases = new HashMap<String, MongoDatabase>();

    @Value("${mongo.servers}")
    private String mongoServers;

    @Value("${mongo.username}")
    private String user;

    @Value("${mongo.password}")
    private String pwd;

    private MongoClient mongoClient = null;


//    @PostConstruct
    private void initMongoClient() {
        mongoClient = null;
        List<ServerAddress> nodes = new ArrayList<ServerAddress>();
        try {
            String[] servers = mongoServers.split(",");

            for (String server : servers) {
                String[] host_port = server.split(":");
                nodes.add(new ServerAddress(host_port[0], Integer.parseInt(host_port[1])));
            }

            if (StringUtils.isNotEmpty(user)) {
                MongoCredential cred1 = MongoCredential.createMongoCRCredential(user, MongoConst.DB_MSG, pwd.toCharArray());
                mongoClient = new MongoClient(nodes, Lists.newArrayList(cred1));
            } else {
                mongoClient = new MongoClient(nodes);
            }

        } catch (Exception e) {
            logger.warn("--- 加载mongodb失败 ---{}", e);
        }

    }

    public MongoCollection<Document> getMongoCollection(Integer userId, String dbName, String tableName) {
        MongoDatabase db = mongo_databases.get(dbName);

        if (db == null) {
            db = mongoClient.getDatabase(dbName);
            mongo_databases.put(dbName, db);
        }

        if (userId != null && userId > 0) {
            tableName = tableName.concat(getTableIndex(userId));
        }

        MongoCollection<Document> collection = mongo_collections.get(tableName);

        if (collection == null) {
            collection = db.getCollection(tableName);
            mongo_collections.put(tableName, collection);
        }

        return collection;
    }

    public MongoCollection<Document> getMongoCollection(String dbName, String tableName) {
        MongoDatabase db = mongo_databases.get(dbName);

        if (db == null) {
            db = mongoClient.getDatabase(dbName);
            mongo_databases.put(dbName, db);
        }

        MongoCollection<Document> collection = mongo_collections.get(tableName);

        if (collection == null) {
            collection = db.getCollection(tableName);
            mongo_collections.put(tableName, collection);
        }

        return collection;
    }

    //TODO FIX SPLIT TABLE INDEX CODE
    private String getTableIndex(Integer userId) {
        return "1";
    }

    public Document touchRequestDocument(Long requestId, Integer shopId, String data, long reqTimeout) {
        Document document = new Document();
        document.append(MongoConst.COLUMN_REQUEST_ID, requestId);
        document.append(MongoConst.COLUMN_SHOP_ID, shopId);
        document.append(MongoConst.COLUMN_DATA, data);
        document.append(MongoConst.COLUMN_REQ_TIMEOUT, reqTimeout);
        document.append(MongoConst.COLUMN_CREATE_TIME, System.currentTimeMillis());
        return document;
    }

    public Document touchResponseDocument(Long requestId, Integer shopId, String data) {
        Document document = new Document();
        document.append(MongoConst.COLUMN_REQUEST_ID, requestId);
        document.append(MongoConst.COLUMN_SHOP_ID, shopId);
        document.append(MongoConst.COLUMN_DATA, data);
        document.append(MongoConst.COLUMN_CREATE_TIME, System.currentTimeMillis());
        return document;
    }

    public MongoCollection<Document> getShopRequestCollection(Integer shopId) {
        return getMongoCollection(shopId, MongoConst.DB_MSG, MongoConst.TB_REQUEST);
    }

    public MongoCollection<Document> getShopResponseCollection(Integer shopId) {
        return getMongoCollection(shopId, MongoConst.DB_MSG, MongoConst.TB_RESPONSE);
    }
}
