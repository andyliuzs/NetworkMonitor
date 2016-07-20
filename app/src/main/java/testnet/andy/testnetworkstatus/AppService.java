package testnet.andy.testnetworkstatus;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import testnet.andy.testnetworkstatus.db.DBManager;

/**
 * Created by andyliu on 16-7-11.
 */
public class AppService extends Service {
    public static final String TAG = AppService.class.getSimpleName();
    public static final String APP_SERVICE_ACTION = "com.itzs.testtrafficmonitor.APP_SERVICE";
    private Timer timer;
    DBManager dbManager = new DBManager();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AppBinder();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, 0, 1000 * 60);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            trafficMonitor();
        }
    };


    /**
     * 遍历有联网权限的应用程序的流量记录
     */
    private void trafficMonitor() {
        int number = 0;
        PackageManager pm = this.getPackageManager();
        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
//        List<TrafficModel> listApps = new ArrayList<TrafficModel>();
        for (PackageInfo info : packinfos) {
            String[] premissions = info.requestedPermissions;
            if (premissions != null && premissions.length > 0) {
                String pr = Arrays.toString(premissions);
                if (pr.contains("android.permission.INTERNET")) {
                    // System.out.println(info.packageName+"访问网络");
                    int uid = info.applicationInfo.uid;
                    long rx = TrafficStats.getUidRxBytes(uid);
                    long tx = TrafficStats.getUidTxBytes(uid);
                    long date = new Date().getTime();
                    String groupBy = Utils.getDate(date);
                    String packageName = info.applicationInfo.packageName;
//                        TrafficModel trafficModel = new TrafficModel();
//                        trafficModel.setAppInfo(info.applicationInfo);
//                        trafficModel.setPackageName(info.applicationInfo.packageName);
//                        trafficModel.setDate(new Date().getTime());
//                        trafficModel.setRx(rx);
//                        trafficModel.setTx(tx);
//                        listApps.add(trafficModel);
                    String order = TrafficModel.DATE + " DESC";
                    String where = TrafficModel.DATE + "=? and " + TrafficModel.PACKAGE + "= ? and " + TrafficModel.IS_BACKUPS + "=? and " + TrafficModel.GROUP_BY + "=?";
                    String[] args = new String[]{"0", packageName, "0", ""};
                    Cursor c = getContentResolver().query(TrafficModel.ITEMS_URI, TrafficModel.PROJECTION, where, args, order);
                    if (c.getCount() > 0) {
//                        Log.v(TAG, "有备份数据 同步");
                        c.moveToPosition(-1);
                        c.moveToNext();
                        long backUpRx = c.getLong(c.getColumnIndex(TrafficModel.RX));
                        long backUpTx = c.getLong(c.getColumnIndex(TrafficModel.TX));
//                        Log.v(TAG, "BACKUP PACKAGE=" + packageName + ", RX=" + backUpRx + ",TX=" + backUpTx);
                        rx += backUpRx;
                        tx += backUpTx;

                    } else {
//                        Log.v(TAG, "没有有备份数据 不同步");
                        dbManager.insertTraffic(0, "", 0, 0, packageName, 0);
                    }
                    dbManager.insertTraffic(date, groupBy, rx, tx, packageName, 1);

                    number++;

                }
            }
        }

//        showAllData();
        PreUtils.putInt(this, "app_number", number);

        Log.v(TAG, "更新数据库数据");


        if (serviceInterface != null) {
            serviceInterface.onListChange(number);
            Log.v(TAG, "刷新数据");
        }
    }

//    private void showAllData() {
//        String order = TrafficModel.DATE + " DESC";
//        Cursor c = getContentResolver().query(TrafficModel.ITEMS_URI, TrafficModel.PROJECTION, null, null, order);
//        List<TrafficModel> trafficModels = TrafficModel.getBeans(this,c);
//        for (TrafficModel t : trafficModels) {
//            Log.v(TAG, t.toString());
//        }
//
//    }


    public class AppBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public AppService getService() {
            return AppService.this;
        }
    }

    ServiceInterface serviceInterface = null;

    public void setServiceInterface(ServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public interface ServiceInterface {
        public void onListChange(int number);
    }

}
