package testnet.andy.testnetworkstatus;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;

import testnet.andy.testnetworkstatus.db.DBManager;

/**
 * Created by andyliu on 16-7-11.
 */
public class TrafficModel {
    public static final String TRAFFIC_MODEL_TABLE = "traffic_model_table";
    public static final String _ID = "_id";
    //    public static final String UID = "uid";
    public static final String DATE = "date";
    public static final String GROUP_BY = "groupby";
    public static final String RX = "rx";
    public static final String TX = "tx";
    public static final String PACKAGE = "package";
    //0 true,1 false
    public static final String IS_BACKUPS = "isbackups";

    public static final String TRAFFIC_ITEMS_TYPE = DBManager.BASE_DIR_TYPE + ".traffic";
    public static final String TRAFFIC_ITEM_TYPE = DBManager.BASE_ITEM_TYPE + ".traffic";
    public static final String[] PROJECTION = new String[]{_ID, DATE, GROUP_BY, RX, TX, PACKAGE, IS_BACKUPS};
    public static final Uri ITEMS_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + DBManager.AUTHORITY + "/"
            + TRAFFIC_MODEL_TABLE);
    public static final Uri ITEM_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + DBManager.AUTHORITY + "/"
            + TRAFFIC_MODEL_TABLE + "/");


    private ApplicationInfo appInfo;


    private int id;
    //    private int uid;
    private long date;
    private String gropby;
    private long rx;
    private long tx;
    private String packageName;
//    private long foreMobileRx;
//    private long foreMobileTx;
//
//    private long backMobileRx;
//    private long backMobileTx;
//
//    private long foreWIFIRx;
//    private long foreWIFITx;
//
//    private long backWIFIRx;
//    private long backWIFITx;
//    //总流量= all Rx + all Tx;


    public ApplicationInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(ApplicationInfo appInfo) {
        this.appInfo = appInfo;
    }

//    public int getUid() {
//        return uid;
//    }
//
//    public void setUid(int uid) {
//        this.uid = uid;
//    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getRx() {
        return rx;
    }

    public void setRx(long rx) {
        this.rx = rx;
    }

    public long getTx() {
        return tx;
    }

    public void setTx(long tx) {
        this.tx = tx;
    }

    public String getGropby() {
        return gropby;
    }

    public void setGropby(String gropby) {
        this.gropby = gropby;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public static ArrayList<TrafficModel> getBeans(Context context,Cursor cursor) {
        ArrayList<TrafficModel> list = new ArrayList<TrafficModel>();
        cursor.moveToPosition(-1);
        long current = 0;
        long total = current;
        while (cursor.moveToNext()) {

            TrafficModel bean = new TrafficModel();
            bean.setId(cursor.getInt(cursor.getColumnIndex(TrafficModel._ID)));
            bean.setDate(cursor.getLong(cursor.getColumnIndex(TrafficModel.DATE)));

            bean.setRx(cursor.getLong(cursor.getColumnIndex(TrafficModel.RX)));
            bean.setTx(cursor.getLong(cursor.getColumnIndex(TrafficModel.TX)));
            bean.setGropby(cursor.getString(cursor.getColumnIndex(TrafficModel.GROUP_BY)));
            bean.setPackageName(cursor.getString(cursor.getColumnIndex(TrafficModel.PACKAGE)));
            current = System.currentTimeMillis();
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = Utils.getApplicationInfoByPkgName(bean.getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                context.getContentResolver().delete(TrafficModel.ITEMS_URI, TrafficModel.PACKAGE + "=?", new String[]{bean.getPackageName()});
                continue;
            }
            bean.setAppInfo(applicationInfo);
            current = System.currentTimeMillis() - current;
            list.add(bean);

//            Log.v("TrafficModel", "创建当前bean耗费" + current);
            total += current;
        }
//        Log.v("TrafficModel", "创建所有bean耗费" + total);
        return list;
    }

    @Override
    public String toString() {
        return "id" + id + ",packageName->" + packageName + ",date" + date + ",group by" + gropby + ",rx->" + Formatter.formatFileSize(App.getInstance(), rx) + ",tx->" + Formatter.formatFileSize(App.getInstance(), tx);
    }

}
