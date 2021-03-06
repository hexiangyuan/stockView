package com.wk.chart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.wk.chart.adapter.AbsAdapter;
import com.wk.chart.adapter.CandleAdapter;
import com.wk.chart.compat.Utils;
import com.wk.chart.drawing.AxisDrawing;
import com.wk.chart.drawing.BorderDrawing;
import com.wk.chart.drawing.BreathingLampDrawing;
import com.wk.chart.drawing.CursorDrawing;
import com.wk.chart.drawing.ExtremumLabelDrawing;
import com.wk.chart.drawing.ExtremumTagDrawing;
import com.wk.chart.drawing.GridDrawing;
import com.wk.chart.drawing.HighlightDrawing;
import com.wk.chart.drawing.IndexLabelDrawing;
import com.wk.chart.drawing.IndexLineDrawing;
import com.wk.chart.drawing.MACDDrawing;
import com.wk.chart.drawing.MarkerPointDrawing;
import com.wk.chart.drawing.VolumeDrawing;
import com.wk.chart.drawing.WaterMarkingDrawing;
import com.wk.chart.drawing.candle.CandleDrawing;
import com.wk.chart.drawing.candle.CandleSelectorDrawing;
import com.wk.chart.drawing.depth.DepthDrawing;
import com.wk.chart.drawing.depth.DepthGridDrawing;
import com.wk.chart.drawing.depth.DepthHighlightDrawing;
import com.wk.chart.drawing.depth.DepthSelectorDrawing;
import com.wk.chart.drawing.timeLine.TimeLineDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.ChartCache;
import com.wk.chart.enumeration.DataType;
import com.wk.chart.enumeration.ExtremumVisible;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.LoadingType;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.enumeration.RenderModel;
import com.wk.chart.interfaces.ICacheLoadListener;
import com.wk.chart.marker.AxisTextMarker;
import com.wk.chart.marker.GridTextMarker;
import com.wk.chart.module.CandleIndexModule;
import com.wk.chart.module.CandleModule;
import com.wk.chart.module.DepthModule;
import com.wk.chart.module.FloatModule;
import com.wk.chart.module.TimeLineModule;
import com.wk.chart.module.VolumeModule;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ChartLayout extends ConstraintLayout {
    private static final String TAG = "ChartLayout";
    private final ConstraintSet constraintSet;
    private DataType dataDisplayType;
    private AbsRender<?, ?> candleRender;
    private ChartView candleChartView;
    private ICacheLoadListener iCacheLoadListener;

    public ChartLayout(Context context) {
        this(context, null);
    }

    public ChartLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.constraintSet = new ConstraintSet();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.constraintSet.clone(this);
        initChart();
    }

    /**
     * ?????????????????????
     */
    private void initCandleChartModules(AbsRender<?, ?> render) {
        render.resetChartModules();
        CandleModule candleModule = new CandleModule();
        candleModule.addDrawing(new WaterMarkingDrawing());//????????????
        candleModule.addDrawing(new CandleDrawing());//???????????????
        candleModule.addDrawing(new IndexLineDrawing(IndexType.CANDLE_MA));//MA??????
        candleModule.addDrawing(new IndexLabelDrawing(IndexType.CANDLE_MA));//MA????????????????????????
        candleModule.addDrawing(new IndexLineDrawing(IndexType.BOLL));//BOLL???????????????
        candleModule.addDrawing(new IndexLabelDrawing(IndexType.BOLL));//BOLL????????????????????????
        candleModule.addDrawing(new MarkerPointDrawing());//?????????????????????
        candleModule.addDrawing(new AxisDrawing(5, false));//x?????????
        candleModule.addDrawing(new ExtremumTagDrawing());//??????????????????
        candleModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//????????????
        candleModule.setAttachIndexType(IndexType.CANDLE_MA);
        candleModule.setEnable(true);
        render.addModule(candleModule);

        TimeLineModule timeLineModule = new TimeLineModule();
        timeLineModule.addDrawing(new WaterMarkingDrawing());//????????????
        timeLineModule.addDrawing(new AxisDrawing(5, false));//x?????????
        timeLineModule.addDrawing(new TimeLineDrawing());//???????????????
        timeLineModule.addDrawing(new MarkerPointDrawing());//?????????????????????
        timeLineModule.addDrawing(new BreathingLampDrawing());//???????????????
        timeLineModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//????????????
        render.addModule(timeLineModule);

        VolumeModule volumeModule = new VolumeModule();
        volumeModule.addDrawing(new VolumeDrawing());//???????????????
        volumeModule.addDrawing(new IndexLineDrawing(IndexType.VOLUME_MA));//MA??????
        volumeModule.addDrawing(new IndexLabelDrawing(IndexType.VOLUME_MA));//MA????????????????????????
        volumeModule.addDrawing(new ExtremumLabelDrawing(ExtremumVisible.MAX_VISIBLE, true, false));//x???????????????
        volumeModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//????????????
        volumeModule.setAttachIndexType(IndexType.VOLUME_MA);
        volumeModule.setEnable(true);
        render.addModule(volumeModule);

        CandleIndexModule indexModule = new CandleIndexModule();
        indexModule.addDrawing(new MACDDrawing());//MACD ????????????
        indexModule.addDrawing(new IndexLabelDrawing(IndexType.MACD));//MACD ????????????????????????
        indexModule.addDrawing(new IndexLineDrawing(IndexType.KDJ));//KDJ ???????????????
        indexModule.addDrawing(new IndexLabelDrawing(IndexType.KDJ));//KDJ ????????????????????????
        indexModule.addDrawing(new IndexLineDrawing(IndexType.RSI));//RSI ???????????????
        indexModule.addDrawing(new IndexLabelDrawing(IndexType.RSI));//RSI ????????????????????????
        indexModule.addDrawing(new IndexLineDrawing(IndexType.WR));//WR ???????????????
        indexModule.addDrawing(new IndexLabelDrawing(IndexType.WR));//WR ????????????????????????
        indexModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));//????????????
        indexModule.setEnable(true);
        render.addModule(indexModule);

        FloatModule floatModule = new FloatModule();
        HighlightDrawing candleHighlight = new HighlightDrawing();
        candleHighlight.addMarkerView(new AxisTextMarker());
        candleHighlight.addMarkerView(new GridTextMarker());
        floatModule.addDrawing(new GridDrawing());//Y?????????
        floatModule.addDrawing(candleHighlight);
        floatModule.addDrawing(new CandleSelectorDrawing());
//        floatModule.addDrawing(new BorderDrawing(BorderStyle.TOP));//????????????
        render.addModule(floatModule);
    }

    /**
     * ?????????????????????
     */
    private void initDepthChartModules(AbsRender<?, ?> render) {
        DepthModule depthModule = new DepthModule();
        depthModule.addDrawing(new AxisDrawing(5, true));//x?????????
        depthModule.addDrawing(new DepthDrawing());//???????????????
        depthModule.addDrawing(new BorderDrawing(PositionType.BOTTOM));
        depthModule.setEnable(true);
        render.addModule(depthModule);

        FloatModule floatModule = new FloatModule();
        DepthHighlightDrawing depthHighlight = new DepthHighlightDrawing();
        depthHighlight.addMarkerView(new AxisTextMarker());
        depthHighlight.addMarkerView(new GridTextMarker());
        floatModule.addDrawing(depthHighlight);
        floatModule.addDrawing(new DepthGridDrawing());//Y?????????
        floatModule.addDrawing(new DepthSelectorDrawing());
        floatModule.addDrawing(new BorderDrawing(PositionType.START | PositionType.END | PositionType.BOTTOM));
        render.addModule(floatModule);
    }

    /**
     * ????????? ??????
     */
    private void initChart() {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            View view = getChildAt(i);
            if (!(view instanceof ChartView)) {
                continue;
            }
            ChartView chartView = (ChartView) view;
            AbsRender<?, ?> render = chartView.getRender();
            switch (chartView.getRenderModel()) {
                case CANDLE://?????????
                    this.candleChartView = chartView;
                    this.candleRender = render;
                    initCandleChartModules(render);
                    break;
                case DEPTH://?????????
                    initDepthChartModules(render);
                    chartView.setEnableRightRefresh(false);
                    chartView.setEnableLeftRefresh(false);
                    break;
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param moduleType      ????????????
     * @param moduleGroupType ????????????
     */
    public boolean switchModuleType(@ModuleType int moduleType, @ModuleGroupType int moduleGroupType) {
        if (null == candleRender) {
            return false;
        }
        List<AbsModule<AbsEntry>> modules = candleRender.getModules().get(moduleGroupType);
        if (null == modules || modules.isEmpty()) {
            return false;
        }
        boolean state = false;
        for (AbsModule<AbsEntry> item : modules) {
            if (moduleType == item.getModuleType()) {
                if (item.isEnable()) {
                    state = false;
                } else {
                    item.setEnable(true);
                    state = true;
                }
            } else {
                item.setEnable(false);
            }
        }
        return state;
    }

    /**
     * ??????????????????
     *
     * @param indexType       ????????????
     * @param moduleGroupType ????????????
     */
    public boolean switchIndexType(@IndexType int indexType, @ModuleGroupType int moduleGroupType) {
        if (null == candleRender) {
            return false;
        }
        List<AbsModule<AbsEntry>> modules = candleRender.getModules().get(moduleGroupType);
        if (null == modules || modules.isEmpty()) {
            return false;
        }
        for (AbsModule<AbsEntry> item : modules) {
            if (item.isEnable()) {
                if (item.getAttachIndexType() == indexType) {
                    return false;
                }
                item.setAttachIndexType(indexType);
                return true;
            }
        }
        return false;
    }

    public @IndexType
    int getNowIndexType(@ModuleGroupType int moduleGroupType) {
        List<AbsModule<AbsEntry>> modules = candleRender.getModules().get(moduleGroupType);
        if (null == modules || modules.isEmpty()) {
            return IndexType.NONE;
        }
        for (AbsModule<AbsEntry> item : modules) {
            if (item.isEnable()) {
                return item.getAttachIndexType();
            }
        }
        return IndexType.NONE;
    }

    /**
     * ??????????????????
     *
     * @return ????????????
     */
    public @Nullable
    ChartCache chartCache() {
        if (null == candleRender || null == candleChartView) {
            return null;
        }
        ChartCache chartCache = new ChartCache();
        chartCache.scale = candleRender.getAttribute().currentScale;
        chartCache.beginPosition = candleRender.getBegin();
        AbsAdapter<?, ?> adapter = candleRender.getAdapter();
        if (adapter instanceof CandleAdapter) {
            chartCache.timeType = ((CandleAdapter) adapter).getTimeType();
        }
        for (Map.Entry<Integer, List<AbsModule<AbsEntry>>> item : candleRender.getModules().entrySet()) {
            for (AbsModule<AbsEntry> module : item.getValue()) {
                if (module.isEnable()) {
                    chartCache.getTypes().put(module.getModuleGroup(), new ChartCache.TypeEntry(
                            module.getModuleType(), module.getAttachIndexType()));
                    break;
                }
            }
        }
        return chartCache;
    }

    /**
     * ????????????????????????
     */
    public void loadChartCache(@NotNull final ChartCache chartCache) {
        if (null == candleRender || null == candleChartView) {
            return;
        }
        boolean isNeedLoadData = false;
        candleRender.getAttribute().currentScale = chartCache.scale;
        candleRender.setFirstLoadPosition(chartCache.beginPosition);
        AbsAdapter<?, ?> adapter = candleRender.getAdapter();
        if (adapter instanceof CandleAdapter) {
            CandleAdapter candleAdapter = (CandleAdapter) adapter;
            isNeedLoadData = candleAdapter.isNeedLoadData(chartCache.timeType);
        }
        for (Map.Entry<Integer, ChartCache.TypeEntry> types : chartCache.getTypes().entrySet()) {
            switchModuleType(types.getValue().getModuleType(), types.getKey());
            switchIndexType(types.getValue().getIndexType(), types.getKey());
        }
        this.candleChartView.post(() -> candleChartView.onViewInit());
        if (null != iCacheLoadListener) {
            this.iCacheLoadListener.onLoadCacheTypes(chartCache.timeType, isNeedLoadData, chartCache.getTypes());
        }
    }

    /**
     * ?????????????????????????????????1:???????????????2:???????????????
     */
    public void setDataDisplayType(DataType type) {
        if (type == dataDisplayType) {
            return;
        }
        this.dataDisplayType = type;
        for (int i = 0, z = getChildCount(); i < z; i++) {
            View view = getChildAt(i);
            if (view instanceof ChartView) {
                ChartView chart = (ChartView) view;
                if (chart.getRenderModel() == RenderModel.CANDLE) {
                    AbsRender<?, ?> reader = chart.getRender();
                    if (type == DataType.REAL_TIME) {
                        chart.setEnableRightRefresh(false);//????????????
                        //????????????????????????
                        if (reader.getAttribute().rightScrollOffset == 0) {
                            reader.getAttribute().rightScrollOffset = 250;
                        }
                        chart.getRender().getModule(ModuleType.CANDLE, ModuleGroupType.MAIN).addDrawing(new CursorDrawing());
                        chart.getRender().getModule(ModuleType.TIME, ModuleGroupType.MAIN).addDrawing(new CursorDrawing());
                    } else {
                        chart.setEnableRightRefresh(true);//????????????
                        reader.getAttribute().rightScrollOffset = 0;  //????????????????????????
                        chart.getRender().getModule(ModuleType.CANDLE, ModuleGroupType.MAIN).removeDrawing(CursorDrawing.class);
                        chart.getRender().getModule(ModuleType.TIME, ModuleGroupType.MAIN).removeDrawing(CursorDrawing.class);
                    }
                    break;
                }
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param loadingType ?????????????????????
     */
    public void loadBegin(LoadingType loadingType, ProgressBar bar, ChartView chart) {
        this.constraintSet.setVisibility(bar.getId(), VISIBLE);
        this.constraintSet.connect(bar.getId(), ConstraintSet.START, chart.getId(),
                ConstraintSet.START, Utils.dp2px(getContext(), 30));
        this.constraintSet.connect(bar.getId(), ConstraintSet.END, chart.getId(),
                ConstraintSet.END, Utils.dp2px(getContext(), 30));
        switch (loadingType) {
            case LEFT_LOADING:
                this.constraintSet.clear(bar.getId(), ConstraintSet.END);
                break;
            case RIGHT_LOADING:
                this.constraintSet.clear(bar.getId(), ConstraintSet.START);
                break;
        }
        constraintSet.applyTo(this);
    }

    /**
     * ??????????????????
     */
    public void loadComplete(ProgressBar bar) {
        this.constraintSet.setVisibility(bar.getId(), INVISIBLE);
        constraintSet.applyTo(this);
    }

    public void setICacheLoadListener(ICacheLoadListener iCacheLoadListener) {
        this.iCacheLoadListener = iCacheLoadListener;
    }
}
