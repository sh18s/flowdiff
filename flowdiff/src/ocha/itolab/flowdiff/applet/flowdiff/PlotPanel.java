package ocha.itolab.flowdiff.applet.flowdiff;

import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ocha.itolab.flowdiff.core.seedselect.BestSetSelector;
import ocha.itolab.flowdiff.core.seedselect.Seed;
import ocha.itolab.flowdiff.core.seedselect.SingleEvaluator;
import ocha.itolab.flowdiff.core.seedselect.ViewDependentEvaluator;

public class PlotPanel extends JPanel{
	
	public ChartPanel cpanel;
	public JSlider eSlider, dSlider;
	public JLabel eSliderText, dSliderText;
	int ivSize;
	double e, d;
	public static double emax = 1, dmax = 0;
	public static double elim = 0, dlim = 0.5; // グラフの閾値(初期値を入れる)
	
	SingleEvaluator se = new SingleEvaluator();
	
	public SliderListener sl = new SliderListener();
	
	public PlotPanel(){
		super();
		setPreferredSize(new Dimension(300, 800));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JFreeChart chart = ChartFactory.createScatterPlot("Score",
				"shape entropy",
				"difference",
				createData(),
				PlotOrientation.VERTICAL, 
				true, 
				false, 
				false);

		cpanel = new ChartPanel(chart);
		cpanel.setMaximumSize(new Dimension(300, 300));
		cpanel.setMinimumSize(new Dimension(300, 300));
		XYPlot plot = chart.getXYPlot();
		NumberAxis domain = (NumberAxis)plot.getDomainAxis();
		domain.setTickLabelsVisible(false);
		NumberAxis range = (NumberAxis)plot.getRangeAxis();
		range.setTickLabelsVisible(false);
//		cpanel.setPreferredSize(new Dimension(50, 50));
		this.add(cpanel);
		
		// Slider
		// Slider for entropy
		eSlider = new JSlider(0, 100, 1);
		eSlider.setMajorTickSpacing(25);//描画するめもりの幅
		eSlider.setMinorTickSpacing(5);
		eSlider.setPaintTicks(true);
		// ラベルを小数に
	    Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
	    labelTable.put(new Integer(100), new JLabel("1.0"));
	    labelTable.put(new Integer(75), new JLabel("0.75"));
	    labelTable.put(new Integer(50), new JLabel("0.50"));
	    labelTable.put(new Integer(25), new JLabel("0.25"));
	    labelTable.put(new Integer(0), new JLabel("0.0"));
		eSlider.setLabelTable(labelTable);
		eSlider.setPaintLabels(true);
		eSliderText = new JLabel(" entropy: " + eSlider.getValue());
		this.add(eSliderText);
		this.add(eSlider);
		
		// Slider for diff
		dSlider = new JSlider(0, 100, 1);
		dSlider.setMajorTickSpacing(25); // 描画するめもりの幅
		dSlider.setMinorTickSpacing(5);
		dSlider.setPaintTicks(true);
		dSlider.setLabelTable(labelTable); // ラベルを小数に
		dSlider.setPaintLabels(true);
		dSliderText = new JLabel(" difference: " + dSlider.getValue());
		this.add(dSliderText);
		this.add(dSlider);
		
		addSliderListener(sl);
		
	}
	
	public XYSeriesCollection createData(){
	    XYSeriesCollection data = new XYSeriesCollection();

//	    int xdata[] = {0};
//	    int ydata[] = {0};
	    
	    // Get seed size
	    if(BestSetSelector.selectCounter == 0){
	    	ivSize = 0;
	    }else{
		    ivSize = ViewDependentEvaluator.bestSeedList.size();
	    }
//	    ivSize = SingleEvaluator.graphIVList.size();
	    
	    XYSeries series = new XYSeries("pair of streamlines");

//	    if(xdata.length > 0 || ydata.length > 0){
//	    	for (int i = 0 ; i < 1 ; i++){
//	  	      series.add(xdata[i], ydata[i]);
//	  	    }
	    //}
	    
	    if(0 < ivSize){
	    	series = new XYSeries("streamline pairs");
	    	for(int i= 0; i< ivSize; i++){
	    		Seed seed = ViewDependentEvaluator.bestSeedList.get(i);
	    		
	    		int id = seed.getId();
	    		e = BestSetSelector.infoList.get(id).getEntropy();
	    		d = BestSetSelector.infoList.get(id).getDiff();
//	    		System.out.println("entropy in graph  = " + e);
//	    		System.out.println("diff in graph  = " + d);
//	    		e = SingleEvaluator.graphIVList.get(i).outputEntropy();
//	    		d = SingleEvaluator.graphIVList.get(i).outputDiff();
//	    		series.add(e / emax, d / dmax);
	    		series.add(e, d); // Add data to series
	    	}
	    }

	    data.addSeries(series);

	    return data;
	}
	
	public void addSliderListener(ChangeListener changeListener) {
		eSlider.addChangeListener(changeListener);
		dSlider.addChangeListener(changeListener);
	}
	
	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider changedSlider = (JSlider) e.getSource();
			
			if (changedSlider == eSlider) {
				eSliderText.setText(" entropy:" + eSlider.getValue() / 100.0);
				elim = eSlider.getValue();
			}else if (changedSlider == dSlider) {
				dSliderText.setText(" difference:" + dSlider.getValue() / 100.0);
				dlim = dSlider.getValue();
			}
		}
	}

}