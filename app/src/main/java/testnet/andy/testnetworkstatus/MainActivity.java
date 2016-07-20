package testnet.andy.testnetworkstatus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    AppService appService;
    Intent intent;
    ViewPager mPager;
    SlidingTabLayout mSlidingTabLayout;
    List<Fragment> fragments;
    /****
     * VIew
     *****/
    Toolbar mToolbar;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        intent = new Intent(MainActivity.this, AppService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setTitle("网络监控");// 标题的文字需在setSupportActionBar之前，不然会无效
// toolbar.setSubtitle("副标题");
        setSupportActionBar(mToolbar);
/* 这些通过ActionBar来设置也是一样的，注意要在setSupportActionBar(toolbar);之后，不然就报错了 */
// getSupportActionBar().setTitle("标题");
// getSupportActionBar().setSubtitle("副标题");
// getSupportActionBar().setLogo(R.drawable.ic_launcher);

/* 菜单的监听可以在toolbar里设置，也可以像ActionBar那样，通过Activity的onOptionsItemSelected回调方法来处理 */
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        Toast.makeText(MainActivity.this, "action_settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_share:
                        Toast.makeText(MainActivity.this, "action_share", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });


        mPager = (ViewPager) findViewById(R.id.viewpager);
        fragments = new ArrayList<Fragment>();
        Fragment detailFragment = new DetailFragment();
        Fragment reportFragment = new ReportFragment();
        fragments.add(detailFragment);
        fragments.add(reportFragment);

        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), fragments));
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingtab);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_item, R.id.tabText);
        mSlidingTabLayout.setViewPager(mPager);
        mSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorPrimary));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        /* ShareActionProvider配置 */
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu
                .findItem(R.id.action_share));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        mShareActionProvider.setShareIntent(intent);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "onServiceConnected");
            appService = ((AppService.AppBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "onServiceDisconnected");
        }
    };


    private class MyPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (fragmentList.get(position) instanceof DetailFragment) {
                return DetailFragment.ITEM_NAME;
            } else if (fragmentList.get(position) instanceof ReportFragment) {
                return ReportFragment.ITEM_NAME;
            }
            return "";
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

//    private class MyPagerAdapter extends PagerAdapter {
//
//        List<Fragment> fragmentList;
//
//        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
//            this.fragmentList = fragmentList;
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object o) {
//            return o == view;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int pos) {
//            if (fragmentList.get(pos) instanceof DetailFragment) {
//                return DetailFragment.ITEM_NAME;
//            } else if (fragmentList.get(pos) instanceof ReportFragment) {
//                return ReportFragment.ITEM_NAME;
//            }
//            return "null";
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int pos) {
//           return fragmentList.get(pos);
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView((View) object);
//        }
//
//        @Override
//        public int getCount() {
//            return fragmentList.size();
//        }
//    }

    class MainAdapter extends RecyclerView.Adapter {
        PackageManager pm = MainActivity.this.getPackageManager();
        List<TrafficModel> listApps = new ArrayList<TrafficModel>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_main_item, parent, false);
            return new MainHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TrafficModel trafficModel = listApps.get(position);
            MainHolder mainHolder = (MainHolder) holder;
            mainHolder.ivLauncher.setImageDrawable(trafficModel.getAppInfo().loadIcon(pm));
            mainHolder.tvName.setText((String) pm.getApplicationLabel(trafficModel.getAppInfo()));
            mainHolder.tvDownload.setText("下载：" + Formatter.formatFileSize(MainActivity.this, trafficModel.getRx()));
            mainHolder.tvUpload.setText("上传：" + Formatter.formatFileSize(MainActivity.this, trafficModel.getTx()));
        }

        private void setList(List<TrafficModel> list) {
            this.listApps = list;
        }

        @Override
        public int getItemCount() {
            return listApps.size();
        }

        class MainHolder extends RecyclerView.ViewHolder {

            ImageView ivLauncher;
            TextView tvName;
            TextView tvDownload;
            TextView tvUpload;

            public MainHolder(View itemView) {
                super(itemView);
                ivLauncher = (ImageView) itemView.findViewById(R.id.iv_main_item_laucher);
                tvName = (TextView) itemView.findViewById(R.id.tv_main_item_name);
                tvDownload = (TextView) itemView.findViewById(R.id.tv_main_item_download);
                tvUpload = (TextView) itemView.findViewById(R.id.tv_main_item_upload);
            }
        }
    }

    ActivityInterface activityInterface = null;

    public void setActivityInterface(ActivityInterface activityInterface) {
        this.activityInterface = activityInterface;
    }

    interface ActivityInterface {
        public void onServiceCreated(AppService service);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        stopService(intent);
    }

}
