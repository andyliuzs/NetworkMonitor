package testnet.andy.testnetworkstatus;

import android.app.Application;

/**
 * Created by andyliu on 16-6-28.
 */
public class App extends Application {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

    }


    public static App getInstance() {
        return app;
    }

}
