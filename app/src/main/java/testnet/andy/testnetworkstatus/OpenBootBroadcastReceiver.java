package testnet.andy.testnetworkstatus;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

/**
 * Created by andyliu on 16-7-12.
 */
public class OpenBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        //安装应用后，关机开机后一直保留true
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.v("OpenBoot", "手机刚开启。。。。或者刚刚关机");
            context.startService(new Intent(context, AppService.class));
        } else if (intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")) {
            Log.v("OpenBoot", "手机刚刚关机");
            //备份上次关机前存储的数据
            backUp(context);
        } else {
            Log.v("OpenBoot", "手机刚开启。。。。或者刚刚关机");
        }


    }

    private void backUp(Context context) {
        Log.v("OpenBoot", "开始备份数据");
//        PackageManager pm = context.getPackageManager();
//        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
////        List<TrafficModel> listApps = new ArrayList<TrafficModel>();
//        for (PackageInfo info : packinfos) {
//            String[] premissions = info.requestedPermissions;
//            if (premissions != null && premissions.length > 0) {
//                String pr = Arrays.toString(premissions);
//                if (pr.contains("android.permission.INTERNET"))
//                    number++;
//            }
//        }
        int number = PreUtils.getInt(context, "app_number", 100);
        String order = TrafficModel.DATE + " DESC limit 0," + number;
        Cursor c = context.getContentResolver().query(TrafficModel.ITEMS_URI, TrafficModel.PROJECTION, TrafficModel.IS_BACKUPS + "=?", new String[]{"1"}, order);
        List<TrafficModel> trafficModels = TrafficModel.getBeans(context, c);

        for (TrafficModel trafficModel : trafficModels) {
            ContentValues values = new ContentValues();
            values.put(TrafficModel.RX, trafficModel.getRx());
            values.put(TrafficModel.TX, trafficModel.getTx());
            String where = TrafficModel.DATE + "=? and " + TrafficModel.PACKAGE + "= ? and " + TrafficModel.IS_BACKUPS + "=? and " + TrafficModel.GROUP_BY + "=?";
            String[] args = new String[]{"0", trafficModel.getPackageName(), "0", ""};
            Log.v("OpenBoot", "PACKAGE=" + trafficModel.getPackageName() + ",rx=" + trafficModel.getRx() + ",tx=" + trafficModel.getTx());
            context.getContentResolver().update(TrafficModel.ITEMS_URI, values, where, args);

        }
        Log.v("OpenBoot", "数据备份完成 NUMBER = " + number + "c.getCount=" + c.getCount());

    }
}
