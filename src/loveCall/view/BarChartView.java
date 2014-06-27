package loveCall.view;


import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.View;

import com.example.loveCall.R;

public class BarChartView {

	private static int margins[] = new int[] { 60, 60, 60, 60 };
	private static String[] titles = new String[] { "呼入时间", "呼出时间" };
	private List<double[]> values = new ArrayList<double[]>();
	private static int[] colors = new int[2];
	private XYMultipleSeriesRenderer renderer;
	private Context mContext;
	private String mTitle;
	private List<String> option;
	private double maxY;
	private int groupCount;

	public BarChartView(Context context) {
		this.mContext = context;
		this.renderer = new XYMultipleSeriesRenderer();
		colors[0]=mContext.getResources().getColor(R.color.blue);
		colors[1]=mContext.getResources().getColor(R.color.pink);

	}

	public void initData(double[] firstAnswerPercent, double[] lastAnswerPercent, List<String> option, String title, double maxY, int groupCount) {
		this.values.add(firstAnswerPercent);
		this.values.add(lastAnswerPercent);
		this.mTitle = title;
		this.option = option;
		this.maxY = maxY;
		this.groupCount = groupCount;

	}

	public View getBarChartView() {
		buildBarRenderer();
		setChartSettings(renderer, mTitle, "特别关注好友", "时间（分钟）", 0, groupCount + 1, 0, maxY+0.5, Color.BLACK, Color.BLACK);
		renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
		renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
		int size = 0;
		if(option != null){
			size =  option.size();
		}
		for (int i = 0; i < size; i++) {
			renderer.addXTextLabel(i, option.get(i));
		}
		renderer.setMargins(margins);
		renderer.setMarginsColor(0x00ffffff);
		renderer.setChartTitleTextSize(60);
		renderer.setAxisTitleTextSize(50);
		renderer.setLabelsTextSize(40);
		renderer.setPanEnabled(false, false);
		renderer.setZoomEnabled(false, false);// 设置x，y方向都不可以放大或缩�?
		renderer.setZoomRate(1.0f);
		renderer.setInScroll(false);
		renderer.setBackgroundColor(0x00ffffff);
		renderer.setApplyBackgroundColor(false);
		renderer.setBarWidth(20);
		View view = ChartFactory.getBarChartView(mContext, buildBarDataset(titles, values), renderer, Type.DEFAULT); // Type.STACKED
		view.setBackgroundColor(0x00ffffff);
		return view;
	}

	private XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			Log.d("length", length+"");
			CategorySeries series = new CategorySeries(titles[i]);
			double[] v = values.get(i);
			int seriesLength = v.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(v[k]);
			}
			dataset.addSeries(series.toXYSeries());
		}
		return dataset;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle,
			double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
		renderer.setXLabels(0);
		renderer.setYLabels(0);
		renderer.setLabelsTextSize(15);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setShowLegend(false);
	}

	protected void buildBarRenderer() {
		if (null == renderer) {
			return;
		}
		renderer.setBarWidth(10);
		renderer.setBarSpacing(5);
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(12);
		renderer.setLegendTextSize(12);

		renderer.setXTitle("特别关注好友名称");
		renderer.setYTitle("分钟数");
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setShowGrid(true);


		int length = colors.length;
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
			ssr.setChartValuesTextAlign(Align.RIGHT);
			ssr.setChartValuesTextSize(15);
			ssr.setDisplayChartValues(true);
			ssr.setColor(colors[i]);
			renderer.addSeriesRenderer(ssr);
		}
	}
}
