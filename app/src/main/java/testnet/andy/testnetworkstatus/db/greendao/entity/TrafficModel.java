package testnet.andy.testnetworkstatus.db.greendao.entity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.text.format.Formatter;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.util.ArrayList;

import testnet.andy.testnetworkstatus.App;
import testnet.andy.testnetworkstatus.Utils;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by andyliu on 16-7-11.
 */
@Entity
public class TrafficModel {

    @Transient//表示不会被写入数据库
    private ApplicationInfo appInfo;

    @Id
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




    @Generated(hash = 1033314545)
    public TrafficModel(int id, long date, String gropby, long rx, long tx, String packageName) {
        this.id = id;
        this.date = date;
        this.gropby = gropby;
        this.rx = rx;
        this.tx = tx;
        this.packageName = packageName;
    }




    @Generated(hash = 2112154890)
    public TrafficModel() {
    }


   

    @Override
    public String toString() {
        return "id" + id + ",packageName->" + packageName + ",date" + date + ",group by" + gropby + ",rx->" + Formatter.formatFileSize(App.getInstance(), rx) + ",tx->" + Formatter.formatFileSize(App.getInstance(), tx);
    }




    public String getPackageName() {
        return this.packageName;
    }




    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }




    public long getTx() {
        return this.tx;
    }




    public void setTx(long tx) {
        this.tx = tx;
    }




    public long getRx() {
        return this.rx;
    }




    public void setRx(long rx) {
        this.rx = rx;
    }




    public String getGropby() {
        return this.gropby;
    }




    public void setGropby(String gropby) {
        this.gropby = gropby;
    }




    public long getDate() {
        return this.date;
    }




    public void setDate(long date) {
        this.date = date;
    }




    public int getId() {
        return this.id;
    }




    public void setId(int id) {
        this.id = id;
    }

}
