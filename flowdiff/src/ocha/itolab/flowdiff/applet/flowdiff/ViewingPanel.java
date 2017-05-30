
package ocha.itolab.flowdiff.applet.flowdiff;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import ocha.itolab.flowdiff.core.data.Grid;
import ocha.itolab.flowdiff.core.data.TecPlotFileReader;
import ocha.itolab.flowdiff.core.streamline.Streamline;
import ocha.itolab.flowdiff.core.streamline.StreamlineArray;
import ocha.itolab.flowdiff.core.streamline.StreamlineGenerator;
import ocha.itolab.flowdiff.core.seedselect.*;


public class ViewingPanel extends JPanel {
	// new data
	// file path for Mac
	String path = "../data/DeltaWing/";
//	String path = "C:/DeltaWing/";
	
	String filename1 = "DeltaWing_AoA20.dat";
	String filename2 = "DeltaWing_AoA27.dat";
	String filename3 = "DeltaWing_AoA30.dat";
	String filename4 = "DeltaWing_AoA33.dat";
	
	String filenamePink = filename1;
	String filenameCyan = filename2;
	

	// ベクタ場のファイルを読み込む（相対パス）
	//110度の時
	//static String url1 = "file:../data/kassoro/ari/";
	//static String url2 = "file:../data/kassoro/nashi/";
	//90度の時
	//static String url1 = "file:../data/kassoro/ari90/";
	//static String url2 = "file:../data/kassoro/nashi90/";
	//80度の時
	static String url1 = "file:../data/kassoro/ari80/";
	static String url2 = "file:../data/kassoro/nashi80/";


	public JButton  openDataButton, viewResetButton, viewBuildingButton,generateButton, viewVectorButton, viewCriticalPoint, viewVorticity,
	resetAllStreamlineButton,removeStreamlineButton,highlightStreamline, autoStreamlineButton;
	public JRadioButton viewRotateButton, viewScaleButton, viewShiftButton, noneGridView, grid1View, grid2View, bothGridView,
	noneRotView, grid1RotView, grid2RotView, bothRotView,viewRotate0,viewRotate1,viewRotate2,viewRotate3,viewRotate4,viewRotate5,
	showDiffAngView,showDiffLenView,noneDiffView,showDiffVectorView,showDiffVectorViewLength, aoa1g20, aoa1g27, aoa1g30, aoa1g33,
	aoa2g20, aoa2g27, aoa2g30, aoa2g33,aoa20, aoa27, aoa30, aoa33;
	public JLabel xText, yText, zText, vtext, vhText, vecviewText, diffText,distText,counterText;
	public JSlider sliderX, sliderY, sliderZ,sliderVH,vheight,sliderDiff,sliderDist,sliderCounter;
	public JList list;
	public DefaultListModel model;
	public Container container;
	File currentDirectory;

	/* Selective canvas */
	Canvas canvas;

	/* Cursor Sensor */
	boolean cursorSensorFlag = false;

	/* Action listener */
	ButtonListener bl = null;
	RadioButtonListener rbl = null;
	CheckBoxListener cbl = null;
	SliderListener sl = null;

	/* Data */
	Grid grid1 = null;
	Grid grid2 = null;

	/*button toggle flag*/
	boolean viewVorticity_flag = false;//trueの時オン
	boolean viewBuildingButton_flag = false;
	
	/**
	 * Settings for GridBagLayout
	 */
	public GridBagConstraints setConstraints(int x, int y, int w, int h, double wy){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weighty = wy;
		gbc.anchor = GridBagConstraints.WEST;
		return gbc;
	}
	
	public void setRadioButton(GridBagLayout gbl, GridBagConstraints gbc, JRadioButton button, int x, int y, int w, int h, double wy){
//		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weighty = wy;
		gbc.anchor = GridBagConstraints.WEST;
		gbl.setConstraints(button, gbc);
	}
	public void setButton(GridBagLayout gbl, GridBagConstraints gbc, JButton button, int x, int y, int w, int h, double wy){
//		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weighty = wy;
		gbc.anchor = GridBagConstraints.WEST;
		gbl.setConstraints(button, gbc);
	}
	public void setLabel(GridBagLayout gbl, GridBagConstraints gbc, JLabel label, int x, int y, int w, int h, double wy){
//		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weighty = wy;
		gbc.anchor = GridBagConstraints.WEST;
		gbl.setConstraints(label, gbc);
	}

	public ViewingPanel() {
		// super class init
		super();
		setSize(150, 800);
		
		JTabbedPane tabbedpane = new JTabbedPane();
		// PANEL1: Display and read files
		JPanel p1 = new JPanel();
//		p1.setLayout(new GridLayout(14,1));
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		p1.setLayout(gbl);
		
		// Select data label
		JLabel dataLabel = new JLabel("Select two data you want to compare.");
		setLabel(gbl, gbc, dataLabel, 0,0,1,1,1.0d);
		p1.add(dataLabel);
		// Panel for data selection
		JPanel aoaPanel = new JPanel();
		LineBorder border = new LineBorder(Color.GRAY);
		aoaPanel.setBorder(border);
		GridBagLayout subGbl = new GridBagLayout();
		GridBagConstraints subGbc = new GridBagConstraints();
		aoaPanel.setLayout(subGbl);
		// Data group1
		// Label 1
		JLabel pinkLabel = new JLabel("color: pink");
		subGbc.insets = new Insets(0,0,0,10); // padding
		setLabel(subGbl, subGbc, pinkLabel, 0,0,1,1,0.0d);
		aoaPanel.add(pinkLabel);
		// Button group 1
		ButtonGroup aoa1Group = new ButtonGroup();
		aoa1g20 = new JRadioButton ("20°", true);
		setRadioButton(subGbl, subGbc, aoa1g20, 0,1,1,1,0.0d);
		aoa1Group.add(aoa1g20);
		aoaPanel.add(aoa1g20);
		aoa1g27 = new JRadioButton("27°");
		setRadioButton(subGbl, subGbc, aoa1g27, 0,2,1,1,0.0d);
		aoa1Group.add(aoa1g27);
		aoaPanel.add(aoa1g27);
		aoa1g30 = new JRadioButton("30°");
		setRadioButton(subGbl, subGbc, aoa1g30, 0,3,1,1,0.0d);
		aoa1Group.add(aoa1g30);
		aoaPanel.add(aoa1g30);
		aoa1g33 = new JRadioButton("33°");
		setRadioButton(subGbl, subGbc, aoa1g33, 0,4,1,1,0.0d);
		aoa1Group.add(aoa1g33);
		aoaPanel.add(aoa1g33);
		// Label 2
		JLabel cyanLabel = new JLabel("color: cyan");
		subGbc.insets = new Insets(0,10,0,0); // padding
		setLabel(subGbl, subGbc, cyanLabel, 1,0,1,1,0.0d);
		aoaPanel.add(cyanLabel);
		// Button group 2
		ButtonGroup aoa2Group = new ButtonGroup();
		aoa2g20 = new JRadioButton ("20°");
		setRadioButton(subGbl, subGbc, aoa2g20, 1,1,1,1,0.0d);
		aoa2Group.add(aoa2g20);
		aoaPanel.add(aoa2g20);
		aoa2g27 = new JRadioButton("27°", true);
		setRadioButton(subGbl, subGbc, aoa2g27, 1,2,1,1,0.0d);
		aoa2Group.add(aoa2g27);
		aoaPanel.add(aoa2g27);
		aoa2g30 = new JRadioButton("30°");
		setRadioButton(subGbl, subGbc, aoa2g30, 1,3,1,1,0.0d);
		aoa2Group.add(aoa2g30);
		aoaPanel.add(aoa2g30);
		aoa2g33 = new JRadioButton("33°");
		setRadioButton(subGbl, subGbc, aoa2g33, 1,4,1,1,0.0d);
		aoa2Group.add(aoa2g33);
		aoaPanel.add(aoa2g33);
		// Add aoa panel to p1
		gbc = setConstraints(0,1,1,1,1.0d);
		gbl.setConstraints(aoaPanel, gbc);
		p1.add(aoaPanel);
		
		// Open data button
		openDataButton = new JButton("Read file");
		setButton(gbl, gbc, openDataButton, 0,2,1,1,2.0d);
		p1.add(openDataButton);
		// Reset button
		viewResetButton = new JButton("Restore");
		setButton(gbl, gbc, viewResetButton, 0,3,1,1,2.0d);
		p1.add(viewResetButton);
		
		// Operation Panel
		JPanel opePanel = new JPanel();
		subGbl = new GridBagLayout();
		subGbc = new GridBagConstraints();
		opePanel.setLayout(subGbl);
		// Operation label
		JLabel opeLabel = new JLabel("Operation");
		setLabel(subGbl, subGbc, opeLabel, 0,0,1,1,0.0d);
		opePanel.add(opeLabel);
		// Operation button
		ButtonGroup opeGroup = new ButtonGroup();
		viewRotateButton = new JRadioButton("rotation",true);
		setRadioButton(subGbl, subGbc, viewRotateButton, 0,1,1,1,0.0d);
		opeGroup.add(viewRotateButton);
		opePanel.add(viewRotateButton);
		viewScaleButton = new JRadioButton("Scale");
		setRadioButton(subGbl, subGbc, viewScaleButton, 0,2,1,1,0.0d);
		opeGroup.add(viewScaleButton);
		opePanel.add(viewScaleButton);
		viewShiftButton = new JRadioButton("Shift");
		setRadioButton(subGbl, subGbc, viewShiftButton, 0,3,1,1,0.0d);
		opeGroup.add(viewShiftButton);
		opePanel.add(viewShiftButton);
		// Viewpoint label
		JLabel viewLabel = new JLabel("Viewpoint");
		setLabel(subGbl, subGbc, viewLabel, 1,0,1,1,0.0d);
		opePanel.add(viewLabel);
		// View button group
		ButtonGroup viewGroup = new ButtonGroup();
		viewRotate0 = new JRadioButton("Angle", true);
		setRadioButton(subGbl, subGbc, viewRotate0, 1,1,1,1,0.0d);
		viewGroup.add(viewRotate0);
		opePanel.add(viewRotate0);
		viewRotate1 = new JRadioButton("Right above");
		setRadioButton(subGbl, subGbc, viewRotate1, 1,2,1,1,0.0d);
		viewGroup.add(viewRotate1);
		opePanel.add(viewRotate1);
		viewRotate2 = new JRadioButton("Front");
		setRadioButton(subGbl, subGbc, viewRotate2, 1,3,1,1,0.0d);
		viewGroup.add(viewRotate2);
		opePanel.add(viewRotate2);
		viewRotate3 = new JRadioButton("Back");
		setRadioButton(subGbl, subGbc, viewRotate3, 1,4,1,1,0.0d);
		viewGroup.add(viewRotate3);
		opePanel.add(viewRotate3);
		viewRotate4 = new JRadioButton("Right side");
		setRadioButton(subGbl, subGbc, viewRotate4, 1,5,1,1,0.0d);
		viewGroup.add(viewRotate4);
		opePanel.add(viewRotate4);
		viewRotate5 = new JRadioButton("Left side");
		setRadioButton(subGbl, subGbc, viewRotate5, 1,6,1,1,0.0d);
		viewGroup.add(viewRotate5);
		opePanel.add(viewRotate5);
		// Add operation panel to p1
		gbc = setConstraints(0,4,1,1,1.0d);
		gbl.setConstraints(opePanel, gbc);
		p1.add(opePanel);
		// Building button
		viewBuildingButton = new JButton("Display buildings");
		setButton(gbl, gbc, viewBuildingButton, 0,5,1,1,2.0d);
		p1.add(viewBuildingButton);

		// パネル2
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(7,1));
		vecviewText = new JLabel("Display vector");
		p2.add(vecviewText);
		ButtonGroup group2 = new ButtonGroup();
		noneGridView = new JRadioButton("なし",true);//最初にチェックが入っている
		group2.add(noneGridView);
		p2.add(noneGridView);
		bothGridView = new JRadioButton("両方");
		group2.add(bothGridView);
		p2.add(bothGridView);
		grid1View = new JRadioButton("建物有(ベクトル白)");
		group2.add(grid1View);
		p2.add(grid1View);
		grid2View = new JRadioButton("建物無(ベクトル赤)");
		group2.add(grid2View);
		p2.add(grid2View);

		vheight = new JSlider(0, 85, 10);
		vtext = new JLabel(" ベクトル面地上から: " + vheight.getValue());
		vheight.setMajorTickSpacing(10);
		vheight.setMinorTickSpacing(5);
		vheight.setPaintTicks(true);
		vheight.setLabelTable(vheight.createStandardLabels(20));
		vheight.setPaintLabels(true);
		p2.add(vheight);
		p2.add(vtext);

		// パネル3
		JPanel p3 = new JPanel();
		p3.setLayout(new GridLayout(8,1));
		viewCriticalPoint = new JButton("渦中心表示");
		p3.add(viewCriticalPoint);
		viewVorticity = new JButton("渦度表示");
		p3.add(viewVorticity);
		sliderVH = new JSlider(0, 85, 10);
		vhText = new JLabel(" Height(vorticity): " + sliderVH.getValue());
		sliderVH.setMajorTickSpacing(10);
		sliderVH.setMinorTickSpacing(5);
		sliderVH.setPaintTicks(true);
		sliderVH.setLabelTable(sliderVH.createStandardLabels(20));
		sliderVH.setPaintLabels(true);
		p3.add(vhText);
		p3.add(sliderVH);
		ButtonGroup group3 = new ButtonGroup();
		noneRotView = new JRadioButton("なし", true);//最初にチェックが入っている
		group3.add(noneRotView);
		p3.add(noneRotView);
		grid1RotView = new JRadioButton("両方");//最初にチェックが入っている
		group3.add(grid1RotView);
		p3.add(grid1RotView);
		grid2RotView = new JRadioButton("建物有(ベクトル白)");
		group3.add(grid2RotView);
		p3.add(grid2RotView);
		bothRotView = new JRadioButton("建物無(ベクトル赤)");
		group3.add(bothRotView);
		p3.add(bothRotView);

		// PANEL4: Streamlines panel
		JPanel p4 = new JPanel();
		gbl = new GridBagLayout();
		p4.setLayout(gbl);
		// Panel title
		JLabel slsLabel = new JLabel("Select streamlines automatically");
		gbc = setConstraints(0,0,1,1,2.0d);
		gbl.setConstraints(slsLabel, gbc);
		p4.add(slsLabel);
		
		
		// Threshold of distance Panel
		JPanel distPanel = new JPanel();
		GridBagLayout distGbl = new GridBagLayout();
		distPanel.setLayout(distGbl);
		// Slider
		sliderDist = new JSlider(0, 10, 2);
		sliderDist.setMajorTickSpacing(2); //描画するめもりの幅
		sliderDist.setMinorTickSpacing(1);
		sliderDist.setPaintTicks(true);
		sliderDist.setLabelTable(sliderDist.createStandardLabels(2));
	    sliderDist.setPaintLabels(true);
	    // Text
	 	distText = new JLabel(" Threshold of distance: " + sliderDist.getValue());
	    // Layout (text)
	    GridBagConstraints distGbc = setConstraints(0,0,1,1,0.0d);
	    distGbc.insets = new Insets(1,1,1,1);
	    distGbl.setConstraints(distText, distGbc);
	    distPanel.add(distText);
	    // Layout (slider)
	    distGbc = setConstraints(0,1,1,1,0.0d);
	    distGbl.setConstraints(sliderDist, distGbc);
	    distPanel.add(sliderDist);
	    // Layout (distPanel)
	    gbc = setConstraints(0,3,1,1,2.0d);
	    gbl.setConstraints(distPanel, gbc);
	    p4.add(distPanel);
	    
		// Threshold of number of vertex Panel
	    JPanel numPanel = new JPanel();
	    GridBagLayout numGbl = new GridBagLayout();
	    numPanel.setLayout(numGbl);
	    // Slider
		sliderCounter = new JSlider(0, 3000, 2000);
		sliderCounter.setMajorTickSpacing(200);//描画するめもりの幅
		sliderCounter.setMinorTickSpacing(100);
		sliderCounter.setPaintTicks(true);
		sliderCounter.setLabelTable(sliderCounter.createStandardLabels(500));
		sliderCounter.setPaintLabels(true);
	    // Text
	    counterText = new JLabel(" Threshold of number of vertex: " + sliderCounter.getValue());
		// Layout (text)
		GridBagConstraints numGbc = setConstraints(0,0,1,1,0.0d);
		numGbc.insets = new Insets(1,1,1,1);
		numGbl.setConstraints(counterText, numGbc);
		numPanel.add(counterText);
		// Layout (slider)
		numGbc = setConstraints(0,1,1,1,0.0d);
		numGbl.setConstraints(sliderCounter, numGbc);
		numPanel.add(sliderCounter);
		// Layout (numPanel)
		gbc = setConstraints(0,4,1,1,2.0d);
	    gbl.setConstraints(numPanel, gbc);
		p4.add(numPanel);
		
		autoStreamlineButton = new JButton("Automatically selection");
		gbc = setConstraints(0,5,1,1,1.0d);
		gbl.setConstraints(autoStreamlineButton, gbc);
		p4.add(autoStreamlineButton);
		
		// パネル5
		JPanel p5 = new JPanel();
		p5.setLayout(new GridLayout(8,1));
		p5.add(new JLabel("差分表示"));
		ButtonGroup group5 = new ButtonGroup();
		noneDiffView = new JRadioButton("表示しない", true);//最初にチェックが入っている
		group5.add(noneDiffView);
		p5.add(noneDiffView);
		showDiffAngView = new JRadioButton("角度差分表示");
		group5.add(showDiffAngView);
		p5.add(showDiffAngView);
		showDiffLenView = new JRadioButton("長さ差分表示");
		group5.add(showDiffLenView);
		p5.add(showDiffLenView);
		showDiffVectorView = new JRadioButton("ベクトル差分表示");
		group5.add(showDiffVectorView);
		p5.add(showDiffVectorView);
		showDiffVectorViewLength = new JRadioButton("ベクトル差分表示");
		group5.add(showDiffVectorViewLength);
		p5.add(showDiffVectorViewLength);
		sliderDiff = new JSlider(0, 85, 10);
		sliderDiff.setMajorTickSpacing(10);
		sliderDiff.setMinorTickSpacing(5);
		sliderDiff.setPaintTicks(true);
		sliderDiff.setLabelTable(sliderDiff.createStandardLabels(10));
		sliderDiff.setPaintLabels(true);
	    diffText = new JLabel(" Height: " + sliderDiff.getValue());
		p5.add(sliderDiff);
		p5.add(diffText);

        // Pannel6: 流線(手動)選択
		JPanel p6 = new JPanel();
		//p6.setLayout(new GridLayout(8,1));
		JPanel pXYZ = new JPanel();
		pXYZ.setLayout(new GridLayout(7,1));
		p6.setLayout(new BoxLayout(p6, BoxLayout.Y_AXIS));
	    model = new DefaultListModel();
	    list = new JList(model);

	    sliderX = new JSlider(0, 100, 10);
		sliderX.setMajorTickSpacing(10);
		sliderX.setMinorTickSpacing(5);
		sliderX.setPaintTicks(true);
		sliderX.setLabelTable(sliderX.createStandardLabels(20));
	    sliderX.setPaintLabels(true);
	    xText = new JLabel(" Width: " + sliderX.getValue());
	    pXYZ.add(xText);
		pXYZ.add(sliderX);
		sliderY = new JSlider(0, 100, 10);
		sliderY.setMajorTickSpacing(10);
		sliderY.setMinorTickSpacing(5);
		sliderY.setPaintTicks(true);
		sliderY.setLabelTable(sliderY.createStandardLabels(20));
	    sliderY.setPaintLabels(true);
	    yText = new JLabel(" Height: " + sliderY.getValue());
	    pXYZ.add(yText);
	    pXYZ.add(sliderY);
		sliderZ = new JSlider(0, 100, 10);
		sliderZ.setMajorTickSpacing(10);
		sliderZ.setMinorTickSpacing(5);
		sliderZ.setPaintTicks(true);
		sliderZ.setLabelTable(sliderZ.createStandardLabels(20));
	    sliderZ.setPaintLabels(true);
	    zText = new JLabel(" Depth: " + sliderZ.getValue());
	    generateButton = new JButton("Deciede streamline");
	    pXYZ.add(zText);
	    pXYZ.add(sliderZ);
	    pXYZ.add(generateButton);
		p6.add(pXYZ);
		
	    
	    JScrollPane sp = new JScrollPane();
	    sp.getViewport().setView(list);
	    sp.setPreferredSize(new Dimension(200, 100));

	   // JLabel label = new JLabel();
	    highlightStreamline = new JButton("ハイライト");
	    removeStreamlineButton = new JButton("削除");
	    resetAllStreamlineButton = new JButton("全て削除");
	    JPanel pb = new JPanel();
	    //pb.add(label);
	    pb.add(highlightStreamline);
	    pb.add(removeStreamlineButton);
		pb.add(resetAllStreamlineButton);
	    p6.add(sp);
	    p6.add(pb);
	    
	    //
		// パネル群のレイアウト
		tabbedpane.addTab("Display", p1);
		tabbedpane.addTab("Vector", p2);
		tabbedpane.addTab("Vorticity", p3);
		tabbedpane.addTab("Streamlines", p4);
		tabbedpane.addTab("Streamline", p6);
		tabbedpane.addTab("Different", p5);
		this.add(tabbedpane);

		//
		// リスナーの追加
		//
		if (bl == null)
			bl = new ButtonListener();
		addButtonListener(bl);

		if (rbl == null)
			rbl = new RadioButtonListener();
		addRadioButtonListener(rbl);

		if (cbl == null)
			cbl = new CheckBoxListener();
		addCheckBoxListener(cbl);

		if (sl == null)
			sl = new SliderListener();
		addSliderListener(sl);
	}

	/**
	 * Canvasをセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c) {
		canvas = (Canvas) c;
	}


	/**
	 * Cursor Sensor の ON/OFF を指定するフラグを返す
	 * @return cursorSensorFlag
	 */
	public boolean getCursorSensorFlag() {
		return cursorSensorFlag;
	}


	/**
	 * ラジオボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addRadioButtonListener(ActionListener actionListener) {
		aoa1g20.addActionListener(actionListener);
		aoa1g27.addActionListener(actionListener);
		aoa1g30.addActionListener(actionListener);
		aoa1g33.addActionListener(actionListener);
		aoa2g20.addActionListener(actionListener);
		aoa2g27.addActionListener(actionListener);
		aoa2g30.addActionListener(actionListener);
		aoa2g33.addActionListener(actionListener);
		viewRotateButton.addActionListener(actionListener);
		viewScaleButton.addActionListener(actionListener);
		viewShiftButton.addActionListener(actionListener);
		noneGridView.addActionListener(actionListener);
		bothGridView.addActionListener(actionListener);
		grid1View.addActionListener(actionListener);
		grid2View.addActionListener(actionListener);
		noneRotView.addActionListener(actionListener);
		grid1RotView.addActionListener(actionListener);
		grid2RotView.addActionListener(actionListener);
		bothRotView.addActionListener(actionListener);
		viewRotate0.addActionListener(actionListener);
		viewRotate1.addActionListener(actionListener);
		viewRotate2.addActionListener(actionListener);
		viewRotate3.addActionListener(actionListener);
		viewRotate4.addActionListener(actionListener);
		viewRotate5.addActionListener(actionListener);
		noneDiffView.addActionListener(actionListener);
		showDiffAngView.addActionListener(actionListener);
		showDiffLenView.addActionListener(actionListener);
		showDiffVectorView.addActionListener(actionListener);
		showDiffVectorViewLength.addActionListener(actionListener);
	}

	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		openDataButton.addActionListener(actionListener);
		viewResetButton.addActionListener(actionListener);
		generateButton.addActionListener(actionListener);
		viewCriticalPoint.addActionListener(actionListener);
		viewVorticity.addActionListener(actionListener);
		viewBuildingButton.addActionListener(actionListener);
		resetAllStreamlineButton.addActionListener(actionListener);
		removeStreamlineButton.addActionListener(actionListener);
		highlightStreamline.addActionListener(actionListener);
		autoStreamlineButton.addActionListener(actionListener);
	}

	/**
	 * チェックボックスのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addCheckBoxListener(CheckBoxListener checkBoxListener) {
	}

	/**
	 * スライダのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addSliderListener(ChangeListener changeListener) {
		vheight.addChangeListener(changeListener);
		sliderX.addChangeListener(changeListener);
		sliderY.addChangeListener(changeListener);
		sliderZ.addChangeListener(changeListener);
		sliderDist.addChangeListener(changeListener);
		sliderCounter.addChangeListener(changeListener);
		sliderVH.addChangeListener(changeListener);
		sliderDiff.addChangeListener(changeListener);
	}

	/**
	 * ボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton buttonPushed = (JButton) e.getSource();

			if (buttonPushed == openDataButton) {
//				grid1 = FileReader.getGrid(url1);
//				grid2 = FileReader.getGrid(url2);
				grid1 = TecPlotFileReader.getGrid(path + filenamePink);
				grid2 = TecPlotFileReader.getGrid(path + filenameCyan);
				sliderX.setValue(10);
				sliderY.setValue(10);
				sliderZ.setValue(10);
				canvas.setGrid1(grid1);
				canvas.setGrid2(grid2);
				canvas.setDiffVector2(grid1, grid2);
				canvas.setStreamline1(null);
				canvas.setStreamline2(null);
			}

			if (buttonPushed == viewResetButton) {
				grid1.setStartPoint(10, 10, 10);
				grid2.setStartPoint(10, 10, 10);
				sliderX.setValue(10);
				sliderY.setValue(10);
				sliderZ.setValue(10);
				canvas.viewReset();
			}

			if (buttonPushed == generateButton) {
				Streamline sl1 = new Streamline();
				Streamline sl2 = new Streamline();
				int eIjk[] = new int[3];
				int numg[] = grid1.getNumGridPoint();
				eIjk[0] = sliderX.getValue() * numg[0] / 100;
				eIjk[1] = sliderY.getValue() * numg[1] / 100;
				eIjk[2] = sliderZ.getValue() * numg[2] / 100;
				StreamlineGenerator.generate(grid1, sl1, eIjk, null);
				StreamlineGenerator.generate(grid2, sl2, eIjk, null);
				Drawer.slarray.addList(sl1, sl2, eIjk);
				canvas.setStreamline(Drawer.slarray.deperture, Drawer.slarray.streamlines1, Drawer.slarray.streamlines2, Drawer.slarray.color);
				//canvas.setStreamlineHighColor(StreamlineArray.color);
				//canvas.setStreamlineArray(Streamlinearray);
				model.addElement(" (横："+eIjk[0]+", 高さ："+eIjk[1]+2+", 縦："+eIjk[2]+")");
			}

			StreamlineArray slset = new StreamlineArray();
			if (buttonPushed == autoStreamlineButton) {
				try {
//					slset = BestSeedSetSelector.selectRandomly(grid1, grid2);
					slset = BestSetSelector.selectRandomly(grid1, grid2);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JsonParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JsonMappingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Drawer.slarray = slset;
				canvas.setStreamline(Drawer.slarray.deperture, Drawer.slarray.streamlines1, Drawer.slarray.streamlines2, Drawer.slarray.color);
			}
			
			
			if(buttonPushed == resetAllStreamlineButton){
				Drawer.slarray.clearAllList();
				model.clear();
			}
			if(buttonPushed == removeStreamlineButton){

				int[] index = list.getSelectedIndices();

				if (!list.isSelectionEmpty()){
					if (index.length > 0){
						for (int i = index.length-1 ; i > -1 ; i--){
							Drawer.slarray.clearList(index[i]);
							model.remove(index[i]);
						}
					}
				}
			}

			if(buttonPushed == highlightStreamline){
				//ハイライトボタンをおした際、流線の色が変わる
				int index[] = list.getSelectedIndices();

				if (!list.isSelectionEmpty()){
					//StreamlineArray.setStreamlineColor(index, !(StreamlineArray.color.get(index)));
					if (index.length > 0){
						for (int i = index.length-1 ; i > -1 ; i--){
							Drawer.slarray.setStreamlineColor(index[i], !(Drawer.slarray.color.get(index[i])));
						}
					}
				}
			}

			if (buttonPushed == viewVectorButton) {

			}

			if (buttonPushed == viewCriticalPoint) {
				canvas.setCriticalPoint(true);
			}
			if (buttonPushed == viewVorticity) {
				viewVorticity_flag = !viewVorticity_flag;
				canvas.setVorticity(viewVorticity_flag);
			}
			if(buttonPushed == viewBuildingButton){
				viewBuildingButton_flag =! viewBuildingButton_flag;
				canvas.setIsBuilding(viewBuildingButton_flag);
			}
			canvas.display();
		}
	}


	/**
	 * ファイルダイアログにイベントがあったときに、対応するディレクトリを特定する
	 * @return ファイル
	 */
	String getDirectory() {
		JFileChooser dirChooser = new JFileChooser();
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int selected = dirChooser.showOpenDialog(container);
		if (selected == JFileChooser.APPROVE_OPTION) { // open selected
			return dirChooser.getSelectedFile().getAbsolutePath();
		} else if (selected == JFileChooser.CANCEL_OPTION) { // cancel selected
			return null;
		}

		return null;
	}


	/**
	 * 拡張子がJPGであるファイルの名前一式を配列に確保して返す
	 */
	String[] getJpegFilenames(String dirname) {

		File directory = new File(dirname);
		String[] filelist = directory.list();
		int num = 0;
		for(int i = 0; i < filelist.length; i++) {
			if(filelist[i].endsWith("JPG") || filelist[i].endsWith("jpg"))
				num++;
		}

		String jpeglist[] = new String[num];
		num = 0;
		for(int i = 0; i < filelist.length; i++) {
			if(filelist[i].endsWith("JPG") || filelist[i].endsWith("jpg"))
				jpeglist[num++] = filelist[i];
		}

		return jpeglist;
	}


	/**
	 * ラジオボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class RadioButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JRadioButton buttonPushed = (JRadioButton) e.getSource();
			if(buttonPushed == aoa1g20){
				filenamePink = filename1;
				BestSetSelector.data1 = 20;
			}
			if(buttonPushed == aoa1g27){
				filenamePink = filename2;
				BestSetSelector.data1 = 27;
			}
			if(buttonPushed == aoa1g30){
				filenamePink = filename3;
				BestSetSelector.data1 = 30;
			}
			if(buttonPushed == aoa1g33){
				filenamePink = filename4;
				BestSetSelector.data1 = 33;
			}
			if(buttonPushed == aoa2g20){
				filenameCyan = filename1;
				BestSetSelector.data2 = 20;
			}
			if(buttonPushed == aoa2g27){
				filenameCyan = filename2;
				BestSetSelector.data2 = 22;
			}
			if(buttonPushed == aoa2g30){
				filenameCyan = filename3;
				BestSetSelector.data2 = 30;
			}
			if(buttonPushed == aoa2g33){
				filenameCyan = filename4;
				BestSetSelector.data2 = 33;
			}
			if (buttonPushed == viewRotateButton) {
				canvas.setDragMode(3);
			}
			if (buttonPushed == viewScaleButton) {
				canvas.setDragMode(1);
			}
			if (buttonPushed == viewShiftButton) {
				canvas.setDragMode(2);
			}
			if(buttonPushed == noneGridView){
				canvas.setVectorView(0);
			}
			if(buttonPushed == bothGridView){
				canvas.setVectorView(1);
			}
			if(buttonPushed == grid1View){
				canvas.setVectorView(2);
			}
			if(buttonPushed == grid2View){
				canvas.setVectorView(3);
			}

			if(buttonPushed == noneRotView){
				canvas.setRotView(0);
			}
			if(buttonPushed == grid1RotView){
				canvas.setRotView(1);
			}
			if(buttonPushed == grid2RotView){
				canvas.setRotView(2);
			}
			if(buttonPushed == bothRotView){
				canvas.setRotView(3);
			}
			if (buttonPushed == viewRotate0) {
				canvas.setLookAt(0);
			}
			if (buttonPushed == viewRotate1) {
				canvas.setLookAt(1);
			}
			if (buttonPushed == viewRotate2) {
				canvas.setLookAt(2);
			}
			if (buttonPushed == viewRotate3) {
				canvas.setLookAt(3);
			}
			if (buttonPushed == viewRotate4) {
				canvas.setLookAt(4);
			}
			if (buttonPushed == viewRotate5) {
				canvas.setLookAt(5);
			}
			if(buttonPushed == noneDiffView){
				canvas.setDiffVector(0);
			}
			if(buttonPushed == showDiffAngView){
				canvas.setDiffVector(1);
			}
			if(buttonPushed == showDiffLenView){
				canvas.setDiffVector(2);
			}
			if(buttonPushed == showDiffVectorView){
				canvas.setDiffVector(3);
			}
			if(buttonPushed == showDiffVectorViewLength){
				canvas.setDiffVector(4);
			}
			canvas.display();
		}
	}

	/**
	 * チェックボックスのアクションを検知するItemListener
	 * @author itot
	 */
	class CheckBoxListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			JCheckBox stateChanged = (JCheckBox) e.getSource();

			// 再描画
			canvas.display();
		}
	}

	/**
	 * スライダのアクションを検知するActionListener
	 * @author itot
	 */
	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider changedSlider = (JSlider) e.getSource();
			
			int numg[] = grid1.getNumGridPoint();
			
			if (changedSlider == sliderX) {
				xText.setText(" Width:" + sliderX.getValue());
				grid1.startPoint[0] = sliderX.getValue() * numg[0] / 100;
				grid2.startPoint[0] = sliderX.getValue() * numg[0] / 100;
			}
			else if (changedSlider == sliderY) {
				yText.setText(" Height:" + sliderY.getValue());
				grid1.startPoint[1] = sliderY.getValue() * numg[1] / 100;
				grid2.startPoint[1] = sliderY.getValue() * numg[1] / 100;
			}
			else if (changedSlider == sliderZ) {
				zText.setText(" Depth:" + sliderZ.getValue());
				grid1.startPoint[2] = sliderZ.getValue() * numg[2] / 100;
				grid2.startPoint[2] = sliderZ.getValue() * numg[2] / 100;
			}
			if (changedSlider == sliderDist) {//New Slider
				distText.setText(" Threshold of distance:" + sliderDist.getValue());
				ViewDependentEvaluator.DIST_TH = sliderDist.getValue();
			}
			else if (changedSlider == sliderCounter) {//New Slider
				counterText.setText(" Threshold of number of vertex:" + sliderCounter.getValue());
				ViewDependentEvaluator.COUNTER_TH = sliderCounter.getValue();
			}
			else if(changedSlider == vheight){
				vtext.setText(" ベクトル面地上から: " + vheight.getValue());
				canvas.setVheight(vheight.getValue());
			}
			else if(changedSlider == sliderVH){
				vhText.setText(" 高さ(渦度): " + sliderVH.getValue());
				canvas.setVortheight(sliderVH.getValue());
			}
			else if(changedSlider == sliderDiff){
				diffText.setText("　高さ：　"+ sliderDiff.getValue());
				canvas.setDiffheight(sliderDiff.getValue());
			}
			canvas.display();
		}
	}
}
