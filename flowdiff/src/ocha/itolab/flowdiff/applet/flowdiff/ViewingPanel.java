
package ocha.itolab.flowdiff.applet.flowdiff;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import ocha.itolab.flowdiff.core.data.FileReader;
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
	showDiffAngView,showDiffLenView,noneDiffView,showDiffVectorView,showDiffVectorViewLength;
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

	public ViewingPanel() {
		// super class init
		super();
		setSize(150, 800);

		JTabbedPane tabbedpane = new JTabbedPane();
		// パネル1
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(14,1));
		openDataButton = new JButton("Read file");
		viewResetButton = new JButton("Restore");
		p1.add(openDataButton);
		p1.add(viewResetButton);
		p1.add(new JLabel("Operation"));
		ButtonGroup group1 = new ButtonGroup();
		viewRotateButton = new JRadioButton("rotation",true);//最初にチェックが入っている
		group1.add(viewRotateButton);
		p1.add(viewRotateButton);
		viewScaleButton = new JRadioButton("Scale");
		group1.add(viewScaleButton);
		p1.add(viewScaleButton);
		viewShiftButton = new JRadioButton("Shift");
		group1.add(viewShiftButton);
		p1.add(viewShiftButton);
		p1.add(new JLabel("Vewpoint"));
		ButtonGroup group4 = new ButtonGroup();
		viewRotate0 = new JRadioButton("Angle", true);//最初にチェックが入っている
		group4.add(viewRotate0);
		p1.add(viewRotate0);
		viewRotate1 = new JRadioButton("Right above",true);
		group4.add(viewRotate1);
		p1.add(viewRotate1);
		viewRotate2 = new JRadioButton("Front");
		group4.add(viewRotate2);
		p1.add(viewRotate2);
		viewRotate3 = new JRadioButton("Back");
		group4.add(viewRotate3);
		p1.add(viewRotate3);
		viewRotate4 = new JRadioButton("Right side");
		group4.add(viewRotate4);
		p1.add(viewRotate4);
		viewRotate5 = new JRadioButton("Left side");
		group4.add(viewRotate5);
		p1.add(viewRotate5);
		viewBuildingButton = new JButton("建物表示");
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
		vhText = new JLabel(" 高さ(渦度): " + sliderVH.getValue());
		sliderVH.setMajorTickSpacing(10);
		sliderVH.setMinorTickSpacing(5);
		sliderVH.setPaintTicks(true);
		sliderVH.setLabelTable(sliderVH.createStandardLabels(20));
		sliderVH.setPaintLabels(true);
		p3.add(sliderVH);
		p3.add(vhText);
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

		// パネル4
		JPanel p4 = new JPanel();
		p4.setLayout(new GridLayout(11,1));
		p4.add(new JLabel("Display streamlines"));
		p4.add(new JLabel("pink：Angle of attack is 20 degrees."));
		p4.add(new JLabel("cyan：Angle of attack is 27 degrees."));
		/*sliderX = new JSlider(0, 100, 10);
		sliderX.setMajorTickSpacing(10);
		sliderX.setMinorTickSpacing(5);
		sliderX.setPaintTicks(true);
		sliderX.setLabelTable(sliderX.createStandardLabels(20));
	    sliderX.setPaintLabels(true);
	    xText = new JLabel(" よこ: " + sliderX.getValue());
		//p4.add(sliderX);
		//p4.add(xText);
		sliderY = new JSlider(0, 100, 10);
		sliderY.setMajorTickSpacing(10);
		sliderY.setMinorTickSpacing(5);
		sliderY.setPaintTicks(true);
		sliderY.setLabelTable(sliderY.createStandardLabels(20));
	    sliderY.setPaintLabels(true);
	    yText = new JLabel(" 高さ: " + sliderY.getValue());
		//p4.add(sliderY);
		//p4.add(yText);
		sliderZ = new JSlider(0, 100, 10);
		sliderZ.setMajorTickSpacing(10);
		sliderZ.setMinorTickSpacing(5);
		sliderZ.setPaintTicks(true);
		sliderZ.setLabelTable(sliderZ.createStandardLabels(20));
	    sliderZ.setPaintLabels(true);
	    zText = new JLabel(" たて: " + sliderZ.getValue());
		//p4.add(sliderZ);
		//p4.add(zText);*/
		
		//New Slider
		sliderDist = new JSlider(0, 10, 1);
		sliderDist.setMajorTickSpacing(2);//描画するめもりの幅
		sliderDist.setMinorTickSpacing(1);
		sliderDist.setPaintTicks(true);
		sliderDist.setLabelTable(sliderDist.createStandardLabels(2));
	    sliderDist.setPaintLabels(true);
	    distText = new JLabel(" Threshold of distance: " + sliderDist.getValue());
	    p4.add(distText);
	    p4.add(sliderDist);
		
		//New Slider
		sliderCounter = new JSlider(0, 3000, 100);
		sliderCounter.setMajorTickSpacing(200);//描画するめもりの幅
		sliderCounter.setMinorTickSpacing(100);
		sliderCounter.setPaintTicks(true);
		sliderCounter.setLabelTable(sliderCounter.createStandardLabels(500));
		sliderCounter.setPaintLabels(true);
		counterText = new JLabel(" Threshold of number of vertex: " + sliderCounter.getValue());
		p4.add(counterText);
		p4.add(sliderCounter);
		
		autoStreamlineButton = new JButton("Automatically selection");
//		p4.add(generateButton);
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

        // 流線(手動)選択
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
		//
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
				grid1 = TecPlotFileReader.getGrid(path + filename1);
				grid2 = TecPlotFileReader.getGrid(path + filename2);
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
