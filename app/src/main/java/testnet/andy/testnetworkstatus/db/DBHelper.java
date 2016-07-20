package testnet.andy.testnetworkstatus.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import testnet.andy.testnetworkstatus.TrafficModel;


/**
 * Created by dongjunkun on 2016/1/12.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 2;


    private final static String TABLE_TRAFFIC = "CREATE TABLE IF NOT EXISTS " +
            TrafficModel.TRAFFIC_MODEL_TABLE + " (" +
            TrafficModel._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrafficModel.PACKAGE + " TEXT," +
            TrafficModel.RX + " INTEGER," +
            TrafficModel.TX + " INTEGER," +
            TrafficModel.DATE + " INTEGER," +
            TrafficModel.IS_BACKUPS + " INTEGER," +
            TrafficModel.GROUP_BY + " TEXT"
            + ");";

    public DBHelper(Context context) {
        super(context, DBManager.AUTHORITY, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_TRAFFIC);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    private static void addColumn(SQLiteDatabase db, String table, String field, String type) {
        db.execSQL("ALTER TABLE " + table + " ADD " + field + " " + type);
    }

    @Override
    public synchronized void close() {
        super.close();
    }
}
