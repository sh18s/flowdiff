package ocha.itolab.flowdiff.applet.flowdiff;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ocha.itolab.flowdiff.applet.flowdiff.ViewingPanel.ButtonListener;
import ocha.itolab.flowdiff.applet.flowdiff.ViewingPanel.SliderListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

public class FlowGameViewer extends JApplet {

	// GUI element
	MenuBar menuBar;
	ViewingPanel viewingPanel = null;
	PlotPanel plotPanel = null;
	CursorListener cl;
	Canvas canvas;
	Container windowContainer;
	
	//***
	ButtonListener bl;
	//SliderListener sl;
	
	/**
	 * applet を初期化し、各種データ構造を初期化する
	 */
	public void init() {
		setSize(new Dimension(1000,800));
		buildGUI();
	}

	/**
	 * applet の各イベントの受付をスタートする
	 */
	public void start() {
	}

	/**
	 * applet の各イベントの受付をストップする
	 */
	public void stop() {
	}

	/**
	 * applet等を初期化する
	 */
	private void buildGUI() {

		
		// Canvas
		canvas = new Canvas(512, 512);
		canvas.requestFocus();
		GLCanvas glc = canvas.getGLCanvas();
		
		
		// ViewingPanel
		viewingPanel = new ViewingPanel();
		viewingPanel.setCanvas(canvas);
		
		// PlotPanel
		plotPanel = new PlotPanel();
		
		// MenuBar
		menuBar = new MenuBar();
		menuBar.setCanvas(canvas);
		
		// CursorListener
		cl = new CursorListener();
		cl.setCanvas(canvas, glc);
		cl.setViewingPanel(viewingPanel);
		canvas.addCursorListener(cl);
		
		// ButtonListener
		bl = new ButtonListener();
		viewingPanel.autoStreamlineButton.addActionListener(bl);
		
		// SliderListener
//		sl = new SliderListener();
		
		// CanvasとViewingPanelとPlotPanelのレイアウト
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(glc, BorderLayout.CENTER);
		mainPanel.add(viewingPanel, BorderLayout.WEST);
		mainPanel.add(plotPanel, BorderLayout.EAST);

		// ウィンドウ上のレイアウト
		windowContainer = this.getContentPane();
		windowContainer.setLayout(new BorderLayout());
		windowContainer.add(mainPanel, BorderLayout.CENTER);
		windowContainer.add(menuBar, BorderLayout.NORTH);
		
	}

	/**
	 * main関数
	 * @param args 実行時の引数
	 */
	public static void main(String[] args) {
		Window window = new Window(
				"Flow Game Viewer", 1000, 800, Color.LIGHT_GRAY);
		FlowGameViewer fgv = new FlowGameViewer();

		fgv.init();
		window.getContentPane().add(fgv);
		window.setVisible(true);
		

		fgv.start(); 
	}
	
	// 再描画する必要がある
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton buttonPushed = (JButton) e.getSource();

			if (buttonPushed == viewingPanel.autoStreamlineButton) {
				plotPanel.remove(plotPanel.cpanel);
				plotPanel.remove(plotPanel.eSlider);
				plotPanel.remove(plotPanel.eSliderText);
				plotPanel.remove(plotPanel.dSlider);
				plotPanel.remove(plotPanel.dSliderText);
				remove(plotPanel);
				
				JFreeChart chart = ChartFactory.createScatterPlot("Score",
						"shape entropy",
						"difference",
						plotPanel.createData(),
						PlotOrientation.VERTICAL, 
						true, 
						false, 
						false);
				
				// 目盛りを全部消す
/*				XYPlot plot = chart.getXYPlot();
				NumberAxis domain = (NumberAxis)plot.getDomainAxis();
				domain.setVerticalTickLabels(false);
				domain.setTickLabelsVisible(false);
				NumberAxis range = (NumberAxis)plot.getRangeAxis();
				range.setVerticalTickLabels(false);
				range.setTickLabelsVisible(false);
*/

				// 散布図の設定とplotpanelへの追加
				plotPanel.cpanel = new ChartPanel(chart);
				plotPanel.cpanel.setMaximumSize(new Dimension(300, 300));
				plotPanel.cpanel.setMinimumSize(new Dimension(300, 300));
				plotPanel.add(plotPanel.cpanel);
				
				// スライダの設定と追加
				// eSlider
				plotPanel.eSlider = new JSlider(0, 100, 1);
				plotPanel.eSlider.setMajorTickSpacing(25);
				plotPanel.eSlider.setMinorTickSpacing(5);
				plotPanel.eSlider.setPaintTicks(true);
				// ラベルを小数に
				Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
			    labelTable.put(new Integer(100), new JLabel("1.0"));
			    labelTable.put(new Integer(75), new JLabel("0.75"));
			    labelTable.put(new Integer(50), new JLabel("0.50"));
			    labelTable.put(new Integer(25), new JLabel("0.25"));
			    labelTable.put(new Integer(0), new JLabel("0.0"));
				plotPanel.eSlider.setLabelTable(labelTable);
				plotPanel.eSlider.setPaintLabels(true);
				plotPanel.eSliderText = new JLabel("entropy: " + plotPanel.eSlider.getValue());
				plotPanel.add(plotPanel.eSliderText);
				plotPanel.add(plotPanel.eSlider);
				
				// dSlider
				plotPanel.dSlider = new JSlider(0, 100, 1);
				plotPanel.dSlider.setMajorTickSpacing(25);
				plotPanel.dSlider.setMinorTickSpacing(5);
				plotPanel.dSlider.setPaintTicks(true);
			    plotPanel.dSlider.setLabelTable(labelTable); // ラベルを小数に
			    plotPanel.dSlider.setPaintLabels(true);
			    plotPanel.add(plotPanel.dSliderText);
			    plotPanel.add(plotPanel.dSlider);
			    
			    plotPanel.addSliderListener(plotPanel.sl);
				
				plotPanel.setPreferredSize(new Dimension(300, 800));
				plotPanel.setLayout(new BoxLayout(plotPanel, BoxLayout.Y_AXIS));
				revalidate();
				repaint();
			}
		}
	}

//	class SliderListener implements ChangeListener {
//		public void stateChanged(ChangeEvent e) {
//			JSlider changedSlider = (JSlider) e.getSource();
//			
//			if (changedSlider == plotPanel.dSlider) {
//				plotPanel.dSliderText.setText(" diff:" + plotPanel.dSlider.getValue());
//				plotPanel.dlim = plotPanel.dSlider.getValue();
//			}
//		}
//	}
	
}

