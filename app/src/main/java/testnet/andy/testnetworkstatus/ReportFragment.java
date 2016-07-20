package testnet.andy.testnetworkstatus;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import testnet.andy.testnetworkstatus.Chart.MyMarkerView;

/**
 * Created by andyliu on 16-7-13.
 */
public class ReportFragment extends BaseFragment implements
        OnChartGestureListener, OnChartValueSelectedListener, LoaderManager.LoaderCallbacks<List<TrafficModel>>, MainActivity.ActivityInterface {
    private final String TAG = ReportFragment.class.getSimpleName();
    public static final String ITEM_NAME = "流量报表";
    AppService appService;
    private LineChart mChart;

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

        initChart();
    }

    private void initChart() {

        mChart = (LineChart) rootView.findViewById(R.id.line_chart);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);

        // set the marker to the chart
        mChart.setMarkerView(mv);

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line


        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(150f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaxValue(200f);
        leftAxis.setAxisMinValue(-50f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        setData(45, 100);

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(2500);
        //mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        // // dont forget to refresh the drawing
        // mChart.invalidate();
        for (ILineDataSet iset : mChart.getData().getDataSets()) {
            LineDataSet set = (LineDataSet) iset;
            //是否显示数字
            set.setDrawValues(false);
            // 是否显示圆点
            set.setDrawCircles(false);
            //设置显示模式
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        }
        mChart.invalidate();
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.report_main;
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
            getLoaderManager().restartLoader(0, b, ReportFragment.this);

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

        Log.v(TAG, "开始刷新界面" + new Date().toString());
    }

    @Override
    public void onLoaderReset(Loader<List<TrafficModel>> loader) {
        loader = null;
    }


    /***
     * 数据源初始化
     *
     * @param count 点数
     * @param range 数据
     */
    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            //没有圆点
//            set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
//                    ? LineDataSet.Mode.LINEAR
//                    :  LineDataSet.Mode.CUBIC_BEZIER);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);

            if (com.github.mikephil.charting.utils.Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_drawable);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);


        }
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }


    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
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


}


