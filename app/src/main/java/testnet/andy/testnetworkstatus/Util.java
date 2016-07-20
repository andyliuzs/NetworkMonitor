package testnet.andy.testnetworkstatus;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andyliu on 16-7-11.
 */
@Deprecated
public class Util {
    public static final String PERMISSION_INTERNET = "android.permission.INTERNET";

    /**
     * 获取具有相应权限的app
     *
     * @param context
     * @param p
     * @return
     */
    public static List<PackageInfo> getHavePermissionPkg(Context context, String p) {
        List<PackageInfo> packageInfos = new ArrayList<PackageInfo>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : packinfos) {
            String[] premissions = info.requestedPermissions;
            if (premissions != null && premissions.length > 0) {
                for (String premission : premissions) {
                    if (premission.equals(p)) {
                        packageInfos.add(info);
                    }
                }
            }
        }
        return packageInfos;
    }


    /***
     * 获取应用相关数据
     *
     * @param context
     * @return
     */
//    public static List<TrafficModel> getTrafficModels(Context context) {
//        List<TrafficModel> trafficModels = new ArrayList<TrafficModel>();
//        PackageManager pm = context.getPackageManager();
//        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
//        for (PackageInfo info : packinfos) {
//            String[] premissions = info.requestedPermissions;
//            if (premissions != null && premissions.length > 0) {
//                for (String premission : premissions) {
//                    if (PERMISSION_INTERNET.equals(premission)) {
//                        TrafficModel tm = new TrafficModel();
//                        int uid = info.applicationInfo.uid;
//                        long rx = TrafficStats.getUidRxBytes(uid);
//                        long tx = TrafficStats.getUidTxBytes(uid);
//                        tm.setUid(uid);
//                        tm.setRx(rx);
//                        tm.setTx(tx);
//                        tm.setTotal(rx + tx);
//                        trafficModels.add(tm);
//                    }
//                }
//            }
//        }
//        return trafficModels;
//    }
    public static long getTotalRx() {
        long rx = TrafficStats.getTotalRxBytes();
        return rx < 0 ? 0 : rx;
    }

    public static long getTotalTx() {
        long tx = TrafficStats.getTotalTxBytes();
        return tx < 0 ? 0 : tx;
    }

    public static long getTotalRT() {
        long rx = TrafficStats.getTotalRxBytes();
        long tx = TrafficStats.getTotalTxBytes();
        return (rx + tx) < 0 ? 0 : rx + tx;
    }

//    static long  getMobileRxBytes()  //获取通过Mobile连接收到的字节总数，不包含WiFi
//    static long  getMobileRxPackets()  //获取Mobile连接收到的数据包总数
//    static long  getMobileTxBytes()  //Mobile发送的总字节数
//    static long  getMobileTxPackets()  //Mobile发送的总数据包数
//    static long  getTotalRxBytes()  //获取总的接受字节数，包含Mobile和WiFi等
//    static long  getTotalRxPackets()  //总的接受数据包数，包含Mobile和WiFi等
//    static long  getTotalTxBytes()  //总的发送字节数，包含Mobile和WiFi等
//    static long  getTotalTxPackets()  //发送的总数据包数，包含Mobile和WiFi等
//    static long  getUidRxBytes(int uid)  //获取某个网络UID的接受字节数
//    static long  getUidTxBytes(int uid) //获取某个网络UID的发送字节数
//    总接受流量TrafficStats.getTotalRxBytes()，
//            总发送流量TrafficStats.getTotalTxBytes());
//    不包含WIFI的手机GPRS接收量TrafficStats.getMobileRxBytes());
//    不包含Wifi的手机GPRS发送量TrafficStats.getMobileTxBytes());
//    某一个进程的总接收量TrafficStats.getUidRxBytes(Uid));
//    某一个进程的总发送量TrafficStats.getUidTxBytes(Uid));

//    这些都是从第一次启动程序到最后一次启动的统计量。并不是这篇文章里所说的“从本次开机到本次关机的统计量”！
}
