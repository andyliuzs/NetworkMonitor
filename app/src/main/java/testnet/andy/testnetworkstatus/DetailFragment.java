package testnet.andy.testnetworkstatus;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by andyliu on 16-7-13.
 */
public class DetailFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<TrafficModel>>, MainActivity.ActivityInterface {
    private final String TAG = DetailFragment.class.getSimpleName();
    public static final String ITEM_NAME="流量详情";
    private MainAdapter mainAdapter = null;
    private RecyclerView recyclerView;
    AppService appService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        Bundle b = new Bundle();
        b.putInt("number", PreUtils.getInt(getActivity(), "app_number", 30));
        getLoaderManager().initLoader(0, b, this);
        ((MainActivity) getActivity()).setActivityInterface(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (appService != null) {
            appService.setServiceInterface(null);
        }
    }

    private void initView() {

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_main);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mainAdapter = new MainAdapter();
        recyclerView.setAdapter(mainAdapter);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.content_main;
    }

    /**
     * Activity中service创建之后
     *
     * @param service
     */
    @Override
    public void onServiceCreated(AppService service) {

        appService = service;
        if (appService != null) {
            appService.setServiceInterface(serviceInterface);
        }
    }

    AppService.ServiceInterface serviceInterface = new AppService.ServiceInterface() {

        @Override
        public void onListChange(int number) {
//            mainAdapter.setList(list);
//            mainAdapter.notifyDataSetChanged();

            Bundle b = new Bundle();
            b.putInt("number", number);
            getLoaderManager().restartLoader(0, b, DetailFragment.this);

            Log.v(TAG, "ACTIVITY刷新数据");

        }
    };


    @Override
    public Loader<List<TrafficModel>> onCreateLoader(int id, Bundle args) {
        DataAsyncTaskLoader dataAsyncTaskLoader = new DataAsyncTaskLoader(getActivity(), args.getInt("number"));
        return dataAsyncTaskLoader;
    }


    @Override
    public void onLoadFinished(Loader<List<TrafficModel>> loader, List<TrafficModel> data) {
        mainAdapter.setList(data);
        mainAdapter.notifyDataSetChanged();

        Log.v(TAG, "开始刷新界面" + new Date().toString());
    }

    @Override
    public void onLoaderReset(Loader<List<TrafficModel>> loader) {
        loader = null;
        mainAdapter.notifyDataSetChanged();
    }


    private static class DataAsyncTaskLoader extends AsyncTaskLoader<List<TrafficModel>> {

        private Context context;
        int number = 0;

        public DataAsyncTaskLoader(Context context, int number) {
            super(context);
            this.context = context;
            this.number = number;
        }

        @Override
        public List<TrafficModel> loadInBackground() {
            String order = TrafficModel.DATE + " DESC limit 0," + number;
            Cursor c = context.getContentResolver().query(TrafficModel.ITEMS_URI, TrafficModel.PROJECTION, TrafficModel.IS_BACKUPS + "=?", new String[]{"1"}, order);
            List<TrafficModel> models = TrafficModel.getBeans(getContext(), c);
            return models;
        }

        @Override
        protected void onStartLoading() {
            Log.v("MainActivity", "DataAsyncTaskLoader--》onStartLoading");
            forceLoad();    //强制加载
        }

        @Override
        protected void onStopLoading() {
            Log.v("MainActivity", "DataAsyncTaskLoader--》onStopLoading");
            super.onStopLoading();
        }
    }


    class MainAdapter extends RecyclerView.Adapter {
        PackageManager pm = getActivity().getPackageManager();
        List<TrafficModel> listApps = new ArrayList<TrafficModel>();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_main_item, parent, false);
            return new MainHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TrafficModel trafficModel = listApps.get(position);
            MainHolder mainHolder = (MainHolder) holder;
            mainHolder.ivLauncher.setImageDrawable(trafficModel.getAppInfo().loadIcon(pm));
            mainHolder.tvName.setText((String) pm.getApplicationLabel(trafficModel.getAppInfo()));
            mainHolder.tvDownload.setText("下载：" + Formatter.formatFileSize(getActivity(), trafficModel.getRx()));
            mainHolder.tvUpload.setText("上传：" + Formatter.formatFileSize(getActivity(), trafficModel.getTx()));
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
}


