package testnet.andy.testnetworkstatus.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import testnet.andy.testnetworkstatus.App;
import testnet.andy.testnetworkstatus.TrafficModel;


/**
 * Created by dongjunkun on 2016/1/13.
 */
public class DBManager {
    public static final String AUTHORITY = "testnet.andy.testnetworkstatus.db";
    public static final String BASE_DIR_TYPE = "vnd.android.cursor.dir/vnd.testnet";
    public static final String BASE_ITEM_TYPE = "vnd.android.cursor.item/vnd.testnets";

    public static final int version = 1;

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public DBManager() {
        dbHelper = new DBHelper(App.getInstance());
    }

    public void insertTraffic(long date, String groupBy, long rx, long tx, String packageName, int is_backups) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TrafficModel.DATE, date);
        values.put(TrafficModel.GROUP_BY, groupBy);
        values.put(TrafficModel.RX, rx);
        values.put(TrafficModel.TX, tx);
        values.put(TrafficModel.PACKAGE, packageName);
        values.put(TrafficModel.IS_BACKUPS, is_backups);
        db.replace(TrafficModel.TRAFFIC_MODEL_TABLE, null, values);
        db.close();
    }

}
