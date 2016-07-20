package testnet.andy.testnetworkstatus;

import android.app.Application;

import testnet.andy.testnetworkstatus.db.greendao.DaoMaster;
import testnet.andy.testnetworkstatus.db.greendao.DaoSession;

/**
 * Created by andyliu on 16-6-28.
 */
public class App extends Application {
    private static App app;

    private DaoSession daoSession;
    private final String DB_NAME = "testdb";
    private final String DB_NAME_ENCRYPTED = "testdb_encrypted";
    //是否走加密
    private final boolean ENCRYPTED = false;
    private final String PASSWORD = "dbpassword";

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public DaoSession getDaoSession() {

//        “notes-db”是我们自定的数据库名字，应为我们之前创建了一个Entity叫做User，所以greenDAO自定帮我们生成的UserDao，拿到了这个UserDao，我们就可以操作User这张表了。
//        一个DaoMaster就代表着一个数据库的连接；DaoSession可以让我们使用一些Entity的基本操作和获取Dao操作类，DaoSession可以创建多个，每一个都是属于同一个数据库连接的。
        if (daoSession == null) {
            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? DB_NAME_ENCRYPTED : DB_NAME, null);
            DaoMaster daoMaster = new DaoMaster(ENCRYPTED ? devOpenHelper.getEncryptedWritableDb(PASSWORD) : devOpenHelper.getWritableDb());
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }


    public static App getInstance() {
        return app;
    }


}
