package ocha.itolab.flowdiff.applet.flowdiff;


import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.gl2.GLUgl2;

import ocha.itolab.flowdiff.applet.flowdiff.Transformer;
import ocha.itolab.flowdiff.core.data.Building;
import ocha.itolab.flowdiff.core.data.Element;
import ocha.itolab.flowdiff.core.data.Grid;
import ocha.itolab.flowdiff.core.data.GridPoint;
import ocha.itolab.flowdiff.core.seedselect.ViewDependentEvaluator;
import ocha.itolab.flowdiff.core.streamline.Streamline;
import ocha.itolab.flowdiff.core.streamline.StreamlineArray;
import ocha.itolab.flowdiff.core.streamline.StreamlineGenerator;
import ocha.itolab.flowdiff.util.CriticalPoint;
import ocha.itolab.flowdiff.util.CriticalPointFinder;
import ocha.itolab.flowdiff.util.DiffVectorCal;
import ocha.itolab.flowdiff.util.VorticityCalculate;

import com.jogamp.opengl.util.gl2.GLUT;
//import com.sun.opengl.util.gl2.GLUT;



/**
 * 描画処理のクラス
 *
 * @author itot
 */
public class Drawer implements GLEventListener {

	private GL gl;
	private GL2 gl2;
	private GLU glu;
	private GLUgl2 glu2;
	private GLUT glut;
	GLAutoDrawable glAD;
	GLCanvas glcanvas;

	Transformer trans = null;

	DoubleBuffer modelview, projection, p1, p2, p3, p4;
	IntBuffer viewport;
	int windowWidth, windowHeight;
	int isVectorView = 0;//(0:なし 1:両方)
	int isRotView = 0;//(0:なし 1:両方)

	boolean isMousePressed = false, isAnnotation = true;
	boolean isImage = true, isWireframe = true;
	boolean isCriticalPoint = false;
	boolean isVorticity = false;
	boolean isBuilding = false;


	double linewidth = 1.0;
	long datemin, datemax;
	int authmax;

	int dragMode = 1;

	private double angleX = 0.0;
	private double angleY = 0.0;
	private double shiftX = 0.0;
	private double shiftY = 0.0;
	private double scale = 1.0;
	private double centerX = 0.5;
	private double centerY = 0.5;
	private double centerZ = 0.0;
	private double size = 0.25;

	Grid grid1 = null, grid2 = null;
	Streamline sl1 = null, sl2 = null;
	ArrayList<Streamline> arrsl1 = null, arrsl2 = null;
	ArrayList<int[]> deplist = null ;
	ArrayList<Boolean> scolor = null;
	int vheight = 10;
	int vort = 10;
	Building b;
	VorticityCalculate vc1,vc2;
	DiffVectorCal dv;
	int hdiff = 50;
	int numDiff = 0;
	
	public static StreamlineArray slarray = new StreamlineArray();

	/**
	 * Constructor
	 *
	 * @param width
	 *            描画領域の幅
	 * @param height
	 *            描画領域の高さ
	 */
	public Drawer(int width, int height, GLCanvas c) {
		glcanvas = c;
		windowWidth = width;
		windowHeight = height;

		viewport = IntBuffer.allocate(4);
		modelview = DoubleBuffer.allocate(16);
		projection = DoubleBuffer.allocate(16);

		p1 = DoubleBuffer.allocate(3);
		p2 = DoubleBuffer.allocate(3);
		p3 = DoubleBuffer.allocate(3);
		p4 = DoubleBuffer.allocate(3);

		glcanvas.addGLEventListener((javax.media.opengl.GLEventListener) this);
		b = new Building();
		vc1 = new VorticityCalculate();
		vc2 = new VorticityCalculate();
		dv = new DiffVectorCal();
	}

	public GLAutoDrawable getGLAutoDrawable() {
		return glAD;
	}

	/**
	 * ダミーメソッド
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	/**
	 * Transformerをセットする
	 *
	 * @param transformer
	 */
	public void setTransformer(Transformer view) {
		this.trans = view;
	}

	/**
	 * 高さをセットする
	 * @return
	 */
	public int getVheight() {
		return vheight;
	}

	public void setVheight(int vheight) {
		this.vheight = vheight;
		//System.out.println(vheight);
	}
	public int getVortheight() {
		return vort;
	}
	public void setVortheight(int vort) {
		this.vort = vort;
		//System.out.println(vheight);
	}
	public void setVectorView(int v){
		this.isVectorView = v;
	}
	public void setRotView(int r){
		this.isRotView = r;
	}

	public void setCriticalPoint(boolean c){
		this.isCriticalPoint = c;
	}
	public void setVorticity(boolean c){
		this.isVorticity = c;
	}
	public void setIsBuilding(boolean b){
		this.isBuilding = b;
	}
	public void setDiffVector(int c){ //差分の種類を得る
		this.numDiff = c;
	}
	public void setDiffheight(int h){
		this.hdiff = h;
	}

	/**
	 * Gridをセットする
	 */
	public void setGrid1(Grid g) {
		grid1 = g;
		double minmax[] = grid1.getMinmaxPos();
		centerX = (minmax[0] + minmax[1]) * 0.5;
		centerY = (minmax[2] + minmax[3]) * 0.5;
		centerZ = (minmax[4] + minmax[5]) * 0.5;
		setBuildingLabel(grid1);
		//setVorticity1(grid1);
	}

	/**
	 * Gridをセットする
	 */
	public void setGrid2(Grid g) {
		grid2 = g;
		double minmax[] = grid2.getMinmaxPos();
		centerX = (minmax[0] + minmax[1]) * 0.5;
		centerY = (minmax[2] + minmax[3]) * 0.5;
		centerZ = (minmax[4] + minmax[5]) * 0.5;
		//setVorticity2(grid2);
	}

	/*
	 * 建物のラベリングを行う（コンストラクタでインスタンス作成）
	 * gridを取得した場所(setGrid1)で使用
	 */
	void setBuildingLabel(Grid grid){
		b.labeling(grid);
	}
	/**
	 * 渦度計算を行う（コンストラクタでインスタンス作成）
	 * gridを取得した場所(setGrid1)で使用
	 * @param grid
	 */
	void setVorticity1(Grid grid){
		vc1.calculatevorticity(grid);
		//vc1.minmax(grid);//最大最少を確認（表示するのみ）
	}
	void setVorticity2(Grid grid){
		vc2.calculatevorticity(grid);
		//vc2.minmax(grid);//最大最少を確認（表示するのみ）
	}
	/**
	 * 差分計算を行う（コンストラクタでインスタンス作成）
	 * gridを取得した場所(setGrid1)で使用
	 * @param grid
	 */
	void setDiffVector2(Grid grid1,Grid grid2){
		dv.calDiffAngle(grid1,grid2);
		dv.calDiffLen(grid1,grid2);
	}

	/**
	void setDiffAngVector(Grid grid1,Grid grid2){
		dv.calDiffAngle(grid1,grid2);
	}
	void setDiffLngVector(Grid grid1,Grid grid2){
		dv.calDiffLen(grid1,grid2);
	}
	**/
	/**
	 * Streamlineをセットする
	 */
	public void setStreamline1(ArrayList<Streamline> streamline) {
		arrsl1 = streamline;
	}

	/**
	 * Streamlineをセットする
	 */
	public void setStreamline2(ArrayList<Streamline> streamline) {
		arrsl2 = streamline;
	}
	/**
	 * 流線始点のリストをセットする
	 * @param allDeperture
	 */
	public void setStreamlineDepertures(ArrayList<int[]> allDeperture) {
		// TODO 自動生成されたメソッド・スタブ
		deplist = allDeperture;
	}

	public void setStreamlineHighColor(ArrayList<Boolean> color) {
		// TODO 自動生成されたメソッド・スタブ
		scolor = color;
	}
	public void setStreamline(ArrayList<int[]> depl, ArrayList<Streamline> sl1, ArrayList<Streamline> sl2, ArrayList<Boolean> c) {
		// TODO 自動生成されたメソッド・スタブ
		deplist = depl;
		arrsl1 = sl1;
		arrsl2 = sl2;
		scolor = c;
	}
	public void setStreamlineArray() {
		arrsl1 = slarray.streamlines1;
		arrsl2 = slarray.streamlines2;
		deplist = slarray.deperture;
		scolor = slarray.color;
		// TODO 自動生成されたメソッド・スタブ
//		arrsl1 = StreamlineArray.streamlines1;
//		arrsl2 = StreamlineArray.streamlines2;
//		deplist = StreamlineArray.deperture;
//		scolor = StreamlineArray.color;
	}

	/**
	 * 描画領域のサイズを設定する
	 *
	 * @param width
	 *            描画領域の幅
	 * @param height
	 *            描画領域の高さ
	 */
	public void setWindowSize(int width, int height) {
		windowWidth = width;
		windowHeight = height;
	}

	/**
	 * マウスボタンのON/OFFを設定する
	 *
	 * @param isMousePressed
	 *            マウスボタンが押されていればtrue
	 */
	public void setMousePressSwitch(boolean isMousePressed) {
		this.isMousePressed = isMousePressed;
	}

	/**
	 * 線の太さをセットする
	 *
	 * @param lw
	 *            線の太さ（画素数）
	 */
	public void setLinewidth(double lw) {
		linewidth = lw;
	}

	/**
	 * Imageの可否をセットする
	 */
	public void isImage(boolean is) {
		isImage = is;
	}

	/**
	 * Wireframeの可否をセットする
	 */
	public void isWireframe(boolean is) {
		isWireframe = is;
	}


	/**
	 * マウスドラッグのモードを設定する
	 *
	 * @param dragMode
	 *            (1:ZOOM 2:SHIFT 3:ROTATE)
	 */
	public void setDragMode(int newMode) {
		dragMode = newMode;
	}

	/**
	 * 初期化
	 */
	public void init(GLAutoDrawable drawable) {

		gl = drawable.getGL();
		gl2= drawable.getGL().getGL2();
		glu = new GLU();
		glu2 = new GLUgl2();
		glut = new GLUT();
		this.glAD = drawable;

		gl.glEnable(GL.GL_RGBA);
		gl.glEnable(GL2.GL_DEPTH);
		gl.glEnable(GL2.GL_DOUBLE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_NORMALIZE);
		gl2.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
		//gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

	}

	/**
	 * 再描画
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {

		windowWidth = width;
		windowHeight = height;

		// ビューポートの定義
		gl.glViewport(0, 0, width, height);

		// 投影変換行列の定義
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(-width / 200.0, width / 200.0, -height / 200.0,
				height / 200.0, -1000.0, 1000.0);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);

	}

	/**
	 * 描画を実行する
	 */
	public void display(GLAutoDrawable drawable) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		// 視点位置を決定
		gl2.glLoadIdentity();
		glu.gluLookAt(centerX, centerY, centerZ + 20.0, centerX, centerY,
				centerZ, 0.0, 0.1, 0.0);

		shiftX = trans.getViewShift(0);
		shiftY = trans.getViewShift(1);
		scale = trans.getViewScaleY() * windowHeight / (size * 300.0);
		angleX = trans.getViewRotateY() * 45.0;
		angleY = trans.getViewRotateX() * 45.0;

		// 行列をプッシュ
		gl2.glPushMatrix();

		// いったん原点方向に物体を動かす
		gl2.glTranslated(centerX, centerY, centerZ);

		// マウスの移動量に応じて回転
		gl2.glRotated(angleX, 1.0, 0.0, 0.0);
		gl2.glRotated(angleY, 0.0, 1.0, 0.0);

		// マウスの移動量に応じて移動
		gl2.glTranslated(shiftX, shiftY, 0.0);

		// マウスの移動量に応じて拡大縮小
		gl2.glScaled(scale, scale, scale);

		// 物体をもとの位置に戻す
		gl2.glTranslated(-centerX, -centerY, -centerZ);

		// 変換行列とビューポートの値を保存する
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
		gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview);
		gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection);
		
		ViewDependentEvaluator.setViewConfiguration(modelview, projection, viewport, glu2);

		drawBox();
		//建物の描画
		//drawBuilding2(grid1,isBuilding);


		//両方ベクトル表示
		if(isVectorView == 1){
			drawVectorPart(grid1,1,vheight,20);
			drawVectorPart(grid2,2,vheight,20);
		}

		//grid1ベクトル表示
		if(isVectorView == 2){
			drawVectorPart(grid1,1,vheight,20);
		}
		//grid2ベクトル表示
		if(isVectorView == 3){//ベクトル表示の有無
			drawVectorPart(grid2,2,vheight,20);
		}

		//渦度表示
		if(isRotView == 1){//両方
			drawVorticity(grid1,vort,1,vc1);
			drawVorticity(grid2,vort,2,vc2);
		}
		if(isRotView == 2){
			drawVorticity(grid1,vort,1,vc1);
		}
		if(isRotView == 3){
			drawVorticity(grid2,vort,2,vc2);
		}
		//drawEdgeElement(grid1);
		//drawVectorPart(grid2,2);
		if(isCriticalPoint){//渦中心表示
			drawCriticalPoint(grid1);
		}
		/*
		if(isVorticity == true){//渦中心表示
			drawVorticity(grid1,vort,1,vc1);
			drawVorticity(grid2,vort,2,vc2);
		}*/
		//差分表示
		if(numDiff != 0){
			drawDiffVector(grid1,grid2,hdiff,numDiff);
		}
		if(grid1 != null && arrsl1 != null) {
			drawStartGrid(grid1);
			drawStreamlineStart(deplist);
			drawStreamline(arrsl1, scolor, 1);
			//drawEndGrid(grid1);
		}
		if(grid2 != null && arrsl2 != null) {
			//drawStartGrid(grid2);
			drawStreamline(arrsl2, scolor, 2);
			//drawEndGrid(grid2);
		}

		// 行列をポップ
		gl2.glPopMatrix();

	}


	/**
	 * 格子領域を箱で描画する
	 */
	void drawBox() {
		if(grid1 == null) return;
		double minmax[] = grid1.getMinmaxPos();

		// 6本のループを描く
		gl2.glColor3d(0.5, 0.5, 0.5);
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glEnd();

		//地面だけ色を変える
		//gl2.glColor3d(0.18, 0.18, 0.18);
		/*
		gl2.glBegin(GL.GL_TRIANGLE_FAN);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		*/
	}

	/**
	 * 建物を描画する
	 * @param grid
	 */
	void drawBuilding(Grid grid){
		if(grid == null) return;

		//建物がある座標に点を描画
		for(int i = 0;i<grid.getNumGridPointAll();i++){

			if(grid.getGridPoint(i).getEnvironment() != 0.0){
				if(grid.getGridPoint(i).getEnvironment() == 0.5){
					gl2.glColor3d(0.0, 0.0, 1.0);
				}
				else{
					gl2.glColor3d(0.0, 1.0, 0.0);
				}
				gl2.glBegin(GL.GL_POINTS);
				gl2.glVertex3d(grid.getEnvironmentPoint(i)[0],grid.getEnvironmentPoint(i)[1],grid.getEnvironmentPoint(i)[2]);
				gl2.glEnd();
			}
		}
	}
	/**
	 * 建物を描画する
	 * @param grid
	 */
	void drawBuilding1(Grid grid){
		if(grid == null) return;
		int num = grid.getBuildingPoint1().length;
		//GridPoint[] bgp = new GridPoint[num];
		//System.out.println(grid.getBuildingPoint1().length);
		//bgp = grid.getBuildingPoint1();
		GridPoint[] bgp = grid.getBuildingPoint1();
		//建物がある座標に点を描画
		for(int i = 0;i<num;i++){
				gl2.glColor3d(0.0, 0.0, 1.0);
				gl2.glBegin(GL.GL_POINTS);
				gl2.glVertex3d(bgp[i].getPosition()[0], bgp[i].getPosition()[1], bgp[i].getPosition()[2]);
				gl2.glEnd();
		}
	}

	void drawBuilding2(Grid grid,boolean t){
		if(grid == null) return;
		int type = 0;
		if(t){
			type = 5;
		}else{
			type = 6;
		}
		//海・滑走路を描く
		for(int i =6;i<12;i++){
			GridPoint minmaxXZ[] = b.minmaxPosXZ(grid1,i);
			if(i == 11){
				gl2.glColor3d(0.35, 0.35, 0.35);
			}else{
				gl2.glColor3d(0.0, 0.0, 0.4);
			}
			gl2.glBegin(GL.GL_TRIANGLE_FAN);
			gl2.glVertex3d(minmaxXZ[0].getPosition()[0], minmaxXZ[0].getPosition()[1], minmaxXZ[0].getPosition()[2]);
			gl2.glVertex3d(minmaxXZ[1].getPosition()[0], minmaxXZ[1].getPosition()[1], minmaxXZ[1].getPosition()[2]);
			gl2.glVertex3d(minmaxXZ[2].getPosition()[0], minmaxXZ[2].getPosition()[1], minmaxXZ[2].getPosition()[2]);
			gl2.glVertex3d(minmaxXZ[3].getPosition()[0], minmaxXZ[3].getPosition()[1], minmaxXZ[3].getPosition()[2]);
			gl2.glEnd();
		}

		for(int i=1; i<type;i++){
			GridPoint minmax[] = b.minmaxPos(grid1,i);
			//System.out.println("minmax="+minmax.length);

			// 建物を描く
			gl2.glColor3d(0.0, 0.5, 0.2);
			if(i==5){
				gl2.glColor3d(0.0, 0.2, 0.5);
			}
			gl2.glBegin(GL.GL_TRIANGLE_FAN);
			gl2.glVertex3d(minmax[0].getPosition()[0], minmax[0].getPosition()[1], minmax[0].getPosition()[2]);
			gl2.glVertex3d(minmax[1].getPosition()[0], minmax[1].getPosition()[1], minmax[1].getPosition()[2]);
			gl2.glVertex3d(minmax[3].getPosition()[0], minmax[3].getPosition()[1], minmax[3].getPosition()[2]);
			gl2.glVertex3d(minmax[2].getPosition()[0], minmax[2].getPosition()[1], minmax[2].getPosition()[2]);
			gl2.glEnd();
			gl2.glBegin(GL.GL_TRIANGLE_FAN);
			gl2.glVertex3d(minmax[0].getPosition()[0], minmax[0].getPosition()[1], minmax[0].getPosition()[2]);
			gl2.glVertex3d(minmax[2].getPosition()[0], minmax[2].getPosition()[1], minmax[2].getPosition()[2]);
			gl2.glVertex3d(minmax[6].getPosition()[0], minmax[6].getPosition()[1], minmax[6].getPosition()[2]);
			gl2.glVertex3d(minmax[4].getPosition()[0], minmax[4].getPosition()[1], minmax[4].getPosition()[2]);
			gl2.glEnd();
			gl2.glBegin(GL.GL_TRIANGLE_FAN);
			gl2.glVertex3d(minmax[1].getPosition()[0], minmax[1].getPosition()[1], minmax[1].getPosition()[2]);
			gl2.glVertex3d(minmax[3].getPosition()[0], minmax[3].getPosition()[1], minmax[3].getPosition()[2]);
			gl2.glVertex3d(minmax[7].getPosition()[0], minmax[7].getPosition()[1], minmax[7].getPosition()[2]);
			gl2.glVertex3d(minmax[5].getPosition()[0], minmax[5].getPosition()[1], minmax[5].getPosition()[2]);
			gl2.glEnd();
			gl2.glBegin(GL.GL_TRIANGLE_FAN);
			gl2.glVertex3d(minmax[4].getPosition()[0], minmax[4].getPosition()[1], minmax[4].getPosition()[2]);
			gl2.glVertex3d(minmax[5].getPosition()[0], minmax[5].getPosition()[1], minmax[5].getPosition()[2]);
			gl2.glVertex3d(minmax[7].getPosition()[0], minmax[7].getPosition()[1], minmax[7].getPosition()[2]);
			gl2.glVertex3d(minmax[6].getPosition()[0], minmax[6].getPosition()[1], minmax[6].getPosition()[2]);
			gl2.glEnd();
			gl2.glBegin(GL.GL_TRIANGLE_FAN);
			gl2.glVertex3d(minmax[2].getPosition()[0], minmax[2].getPosition()[1], minmax[2].getPosition()[2]);
			gl2.glVertex3d(minmax[3].getPosition()[0], minmax[3].getPosition()[1], minmax[3].getPosition()[2]);
			gl2.glVertex3d(minmax[7].getPosition()[0], minmax[7].getPosition()[1], minmax[7].getPosition()[2]);
			gl2.glVertex3d(minmax[6].getPosition()[0], minmax[6].getPosition()[1], minmax[6].getPosition()[2]);
			gl2.glEnd();
		}
	}

	/**
	 * ベクトルの描画
	 */
	void drawVector(Grid grid){
		if(grid == null) return;

		for(int i = 0; i < grid.getNumGridPointAll();i++){
			gl2.glColor3d(1.0, 0.0, 0.0);
			gl2.glBegin(GL.GL_POINTS);
			gl2.glVertex3d(grid.getGridPoint(i).getPosition()[0], grid.getGridPoint(i).getPosition()[1], grid.getGridPoint(i).getPosition()[2]);
			gl2.glEnd();
		}
	}

	/**
	 * 一平面ベクトルの描画(高さ)
	 * @param grid
	 * @param type グリッドの種類
	 * @param h　高さ
	 * @param l　ベクトルの長さ(大きいと短くなる)
	 */
	void drawVectorPart(Grid grid, int type,int h,int l){
		if(grid == null) return;
		if(h == 85) return;//ぬるぽ対策

		int height = h;//高さ80
		int test = grid.getNumGridPoint()[0]*height;//描画する最初の要素
		int next = grid.getNumGridPoint()[0]*grid.getNumGridPoint()[1];//描画する次の列
		int num = 0;
		double[] gpos = new double[3];
		double[] vpos = new double[3];
		int vlen = l; //ベクトルの長さを調節する値(大きいと短くなる)
		double[] maxvec = new double[3];
		double[] minvec = new double[3];
		double[] maxdiff = new double[3];
		maxvec = grid.getMaxVector();
		minvec = grid.getMinVector();
		maxdiff[0] = maxvec[0] - minvec[0];
		maxdiff[1] = maxvec[1] - minvec[1];
		maxdiff[2] = maxvec[2] - minvec[2];
		//System.out.println(next);

		for(int i = 0; i < grid.getNumGridPoint()[2];i++){
			for (int j = 0; j < grid.getNumGridPoint()[0]; j++) {
				if(i == 0){
					//最初に描画する要素の決定
					num = test;
				}else{
					//二列目以降に描画する要素の決定
					num = test+next*i;
				}
				if(grid.getEnvironment(num+j)==0.0 && j%2==0 && i%2==0){
					//座標・ベクトルを取得
					gpos[0] = grid.getGridPoint(num+j).getPosition()[0];
					gpos[1] = grid.getGridPoint(num+j).getPosition()[1];
					gpos[2] = grid.getGridPoint(num+j).getPosition()[2];
					vpos[0] = grid.getGridPoint(num+j).getVector()[0];
					vpos[1] = grid.getGridPoint(num+j).getVector()[1];
					vpos[2] = grid.getGridPoint(num+j).getVector()[2];

					//色の調整(type 1:建物あり　2:建物なし)
					if(type==1){
						//gl2.glEnable(GL.GL_BLEND);
						//gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
						//gl2.glColor4d(vpos[0]/maxvec[0], vpos[1]/maxvec[1], vpos[2]/maxvec[2],1.0-vpos[2]/maxvec[2]);
						gl2.glColor3d(vpos[0]/maxdiff[0], vpos[1]/maxdiff[1], vpos[2]/maxdiff[2]);
						//gl2.glColor3d(0.8, 0.8, 0.8);
					}else{
						gl2.glColor3d(1.0, 0.2, 0.2);
					}
					//ベクトルの描画

					//
					//gl2.glLineWidth(0.001f);
					//gl2.glBegin(GL.GL_LINE_LOOP);
					gl2.glBegin(GL.GL_LINES);
					//gl2.glBegin(GL2.GL_LINE_STRIP);
					gl2.glVertex3d(gpos[0], gpos[1], gpos[2]);
					gl2.glVertex3d(gpos[0]+vpos[0]/vlen, gpos[1]+vpos[1]/vlen, gpos[2]+vpos[2]/vlen);
					//glut.glutSolidCone(1.0, 1.0, 1, 1);
					//glut.glutSolidCube(1.0f);
					//glut.glutWireTeapot(1.0, true);
					gl2.glEnd();
					/*
					gl2.glPointSize(2.25f);
					gl2.glBegin(GL.GL_POINTS);
					gl2.glVertex3d(gpos[0]+vpos[0]/vlen, gpos[1]+vpos[1]/vlen, gpos[2]+vpos[2]/vlen);
					gl2.glEnd();
					*/
				}
			}
		}
	}

	/**
	 * エレメント群の描写（端っこの）
	 * @param grid
	 */
	void drawEdgeElement(Grid grid){
		if(grid == null) return;
		GridPoint egp[] = new GridPoint[8];
		for(int i =0 ;i<grid.getNumElementAll();i++){
			if(grid.isEdgeElement(i)==true){
				for(int j = 0; j < 8; j ++){
					egp[j] = grid.getElement(i).gp[j];
					gl2.glBegin(GL.GL_POINTS);
					gl2.glVertex3d(egp[j].getPosition()[0],egp[j].getPosition()[1],egp[j].getPosition()[2]);
					gl2.glEnd();
				}
			}
		}

	}
	/**
	 * 渦中心の表示
	 * @param grid
	 */
	void drawCriticalPoint(Grid grid){
		if(grid == null) return;
		ArrayList<CriticalPoint> array = new ArrayList<CriticalPoint>();
		CriticalPointFinder cpf = new CriticalPointFinder();
		array = cpf.find(grid);

		gl2.glColor3d(0.0, 1.0, 1.0);
		gl2.glPointSize(2.f);
		//ベクトルの描画
		for(int i = 0; i < array.size();i++){
			gl2.glBegin(GL.GL_POINTS);
			gl2.glVertex3d(array.get(i).getPosition()[0], array.get(i).getPosition()[1], array.get(i).getPosition()[2]);
			gl2.glEnd();
		}
	}
	/**
	 * 渦度の表示
	 */
	void drawVorticity(Grid grid,VorticityCalculate vc){
		if(grid == null) return;
		//VorticityCalculate vc = new VorticityCalculate();
		//vc.calculatevorticity(grid);
		//vc.minmax(grid);
		//gl2.glPointSize(1.0f);
		//渦度の描画
		for(int i = 0; i < grid.getNumElementAll();i++){
			if(vc.vorticity[i].getVorticity()>1.0){
				gl2.glEnable(GL.GL_BLEND);
				gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				//gl2.glColor3d(1.0*vc.vorticity[i].getVorticity()/2,0.0, 0.0);
				gl2.glColor4d(1.0*vc.vorticity[i].getVorticity()/2,0.0, 0.0, 0.3);
			}else if(vc.vorticity[i].getVorticity()<-1.0){
				gl2.glEnable(GL.GL_BLEND);
				gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				gl2.glColor3d(0.0,0.0, -1.0*vc.vorticity[i].getVorticity()/2);
				//gl2.glColor3d(1.0, 1.0, 0.0);
			}else{
				continue;
			}

			gl2.glBegin(GL.GL_POINTS);
			gl2.glVertex3d(vc.vorticity[i].getPosition()[0], vc.vorticity[i].getPosition()[1], vc.vorticity[i].getPosition()[2]);
			gl2.glEnd();
		}
		//gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
	}

	void drawVorticity(Grid grid, int height,int type,VorticityCalculate vc){
		if(grid == null) return;
		int posnum = grid.getNumElement()[0]*height + 1;
		gl2.glPointSize(2.0f);
		//渦度の描画
		for(int i = 0; i < grid.getNumElement()[2];i++){

			for(int j = 0;j <grid.getNumElement()[0];j++){
				if(vc.vorticity[posnum+j].getVorticity()>2.0){
					gl2.glEnable(GL.GL_BLEND);
					gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
					//gl2.glColor3d(1.0*vc.vorticity[posnum+j].getVorticity()/2,0.0, 0.0);
					if(type==1){
						//gl2.glColor3d(1.0,1.0, 0.0);
						gl2.glColor4d(1.0,0.0, -1.0*vc.vorticity[posnum+j].getVorticity()/10,0.8);
					}else{
						//gl2.glColor3d(0.0,1.0, 1.0);
						gl2.glColor4d(0.0,1.0, -1.0*vc.vorticity[posnum+j].getVorticity()/10,0.8);
					}
					//gl2.glColor4d(1.0*vc.vorticity[posnum+j].getVorticity()/2,0.0, 0.0, 0.3);
				}else if(vc.vorticity[posnum+j].getVorticity()<-2.0){
					gl2.glEnable(GL.GL_BLEND);
					gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
					if(type==1){
						//gl2.glColor3d(1.0,1.0, 0.0);
						//gl2.glColor3d(0.0,0.0, -1.0*vc.vorticity[posnum+j].getVorticity()/2);
						gl2.glColor4d(1.0,0.0, -1.0*vc.vorticity[posnum+j].getVorticity()/10,0.8);
					}else{
						//gl2.glColor3d(0.0,1.0, 1.0);
						gl2.glColor4d(0.0,1.0, -1.0*vc.vorticity[posnum+j].getVorticity()/10,0.8);
						//gl2.glColor3d(0.0,1.0, 0.0);
						//gl2.glColor3d(1.0, 1.0, 0.0);
					}
				}else{
					continue;
				}

				gl2.glBegin(GL.GL_POINTS);
				gl2.glVertex3d(vc.vorticity[posnum+j].getPosition()[0], vc.vorticity[posnum+j].getPosition()[1], vc.vorticity[posnum+j].getPosition()[2]);
				gl2.glEnd();
			}
			posnum += grid.getNumElement()[0]*grid.getNumElement()[1];
		}
		//gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
	}

	/**
	 * 差分の表示
	 */
	void drawDiffVector(Grid grid1, Grid grid2,int hdiff,int type){
		if(grid1 == null) return;
		if(grid2 == null) return;

		//描画回数
		int num = grid1.getNumGridPoint()[0]* grid1.getNumGridPoint()[2];
		//色の設定

		//差分の描画
		GridPoint[] gp = grid1.getPlanePoints(hdiff);
		GridPoint[] gp2 = grid2.getPlanePoints(hdiff);

		if(type == 1){
			for(int i = 0; i < num;i++){
				if(gp[i].getAngDiff() != Double.NaN){
					double angle = gp[i].getAngDiff();
					double color = Math.log((Math.E-1)*(angle-1)/(Math.PI-1)+1);
					//double color = Math.exp(Math.log(2)*angle/Math.PI)-1;
					//System.out.println("angle =" +angle);
					gl2.glEnable(GL.GL_BLEND);
					gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
					gl2.glColor4d(color, 0.0, 0.0, color);
					gl2.glBegin(GL.GL_POINTS);
					gl2.glVertex3d(gp[i].getPosition()[0], gp[i].getPosition()[1], gp[i].getPosition()[2]);
					gl2.glEnd();
				}
			}
		}
		else if(type == 2){
			for(int i = 0; i < num;i++){
				if(gp[i].getLenDiff() != Double.NaN){
					double length = gp[i].getLenDiff();
					double color = Math.exp(Math.log(2)*length/dv.max)-1;
					//System.out.println("angle =" +angle);
					gl2.glEnable(GL.GL_BLEND);
					gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
					gl2.glColor4d(color, 0.0, 0.0, color);
					gl2.glBegin(GL.GL_POINTS);
					gl2.glVertex3d(gp[i].getPosition()[0], gp[i].getPosition()[1], gp[i].getPosition()[2]);
					gl2.glEnd();
				}
			}
		}
		else if(type == 3){
			int vlen = 20; //ベクトルの長さを調節する値
			for (int i = 0; i < num; i++) {
				double vpos1[] = new double[3];
				double vpos2[] = new double[3];
				//grid1のベクトル位置
				vpos1[0] = gp[i].getPosition()[0] + gp[i].getVector()[0]/vlen;
				vpos1[1] = gp[i].getPosition()[1] + gp[i].getVector()[1]/vlen;
				vpos1[2] = gp[i].getPosition()[2] + gp[i].getVector()[2]/vlen;
				//grid2のベクトル位置
				vpos2[0] = gp2[i].getPosition()[0] + gp2[i].getVector()[0]/vlen;
				vpos2[1] = gp2[i].getPosition()[1] + gp2[i].getVector()[1]/vlen;
				vpos2[2] = gp2[i].getPosition()[2] + gp2[i].getVector()[2]/vlen;
				gl2.glEnable(GL.GL_BLEND);
				gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				gl2.glColor3d(1.0, 1.0, 0.0);
				gl2.glBegin(GL.GL_LINES);
				gl2.glVertex3d(vpos1[0], vpos1[1], vpos1[2]);
				gl2.glVertex3d(vpos2[0], vpos2[1], vpos2[2]);
				gl2.glEnd();
			}
		}
		else if(type == 4){
			int vlen = 20; //ベクトルの長さを調節する値
			for (int i = 0; i < num; i++) {
				double vpos1[] = new double[3];
				double vpos2[] = new double[3];
				//grid1のベクトル位置
				vpos1[0] = gp[i].getPosition()[0] + gp[i].getVector()[0];
				vpos1[1] = gp[i].getPosition()[1] + gp[i].getVector()[1];
				vpos1[2] = gp[i].getPosition()[2] + gp[i].getVector()[2];
				//grid2のベクトル位置
				vpos2[0] = gp2[i].getPosition()[0] + gp2[i].getVector()[0];
				vpos2[1] = gp2[i].getPosition()[1] + gp2[i].getVector()[1];
				vpos2[2] = gp2[i].getPosition()[2] + gp2[i].getVector()[2];
				gl2.glEnable(GL.GL_BLEND);
				gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				gl2.glColor3d(1.0, 1.0, 0.0);
				gl2.glBegin(GL.GL_LINES);
				gl2.glVertex3d(vpos1[0], vpos1[1], vpos1[2]);
				gl2.glVertex3d(vpos2[0], vpos2[1], vpos2[2]);
				gl2.glEnd();
			}
		}
	}

	/**
	 * 始点を描画する(エレメントを描画)
	 */
	void drawStartGrid(Grid grid){
		int i, j, k;
		if(grid == null) return;
		i = grid.startPoint[0];
		j = grid.startPoint[1];
		k = grid.startPoint[2];

		double minmax[] = new double[6];
		minmax[0] = minmax[2] = minmax[4] = 1.0e+30;
		minmax[1] = minmax[3] = minmax[5] = -1.0e+30;

		Element element = grid.getElement(grid.calcElementId(i, j, k));
		for (int d = 0; d < 8; d++){
			double pos[] = element.gp[d].getPosition();
			for (int loop = 0; loop < 3; loop++){
				minmax[loop*2] = (minmax[loop*2] > pos[loop] ? pos[loop] : minmax[loop*2]);
				minmax[loop*2 + 1] = (minmax[loop*2 + 1] < pos[loop] ? pos[loop] : minmax[loop*2 + 1]);
			}
		}

		// 6本のループを描く
		gl2.glColor3d(1.0, 1.0, 1.0);

		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glEnd();
	}

	/**
	 * 始点を描画する（エレメントの中心を描画）
	 */
	void drawStartGrid(Grid grid, int[] dep){
		int i, j, k;
		double[] pos = new double[3];

		if(grid == null) return;
		i = dep[0];
		j = dep[1];
		k = dep[2];
		//エレメントの中心座標を描画する
		pos = grid.calcElementCenter(i, j, k);

		gl2.glColor3d(1.0, 1.0, 1.0);
		gl2.glBegin(GL.GL_POINTS);
		gl2.glVertex3d(pos[0], pos[1], pos[2]);
		gl2.glEnd();
	}


	/**
	 * 流線の行き着いた先の格子を描く
	 */
	public void drawEndGrid(Grid grid){
		if(grid == null) return;

		double minmax[] = new double[6];
		minmax[0] = minmax[2] = minmax[4] = 1.0e+30;
		minmax[1] = minmax[3] = minmax[5] = -1.0e+30;

		Element element;
		int lastId = StreamlineGenerator.lastElementId();
		if (lastId > 0) {
			element = grid.getElement(lastId);
		}
		else {
			return;
		}
		// System.out.println("    lastElementId=" + StreamlineGenerator.lastElementId());
		for (int d = 0; d < 8; d++){
			double pos[] = element.gp[d].getPosition();
			for (int loop = 0; loop < 3; loop++){
				minmax[loop*2] = (minmax[loop*2] > pos[loop] ? pos[loop] : minmax[loop*2]);
				minmax[loop*2 + 1] = (minmax[loop*2 + 1] < pos[loop] ? pos[loop] : minmax[loop*2 + 1]);
			}
		}

		// 6本のループを描く

		gl2.glColor3d(0.0, 1.0, 0.0);

		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[0], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[0], minmax[2], minmax[5]);
		gl2.glEnd();
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[4]);
		gl2.glVertex3d(minmax[1], minmax[3], minmax[5]);
		gl2.glVertex3d(minmax[1], minmax[2], minmax[5]);
		gl2.glEnd();

	}



	/**
	 * 流線を描く
	 */
	void drawStreamline(ArrayList<Streamline> arrsl, ArrayList<Boolean> color,int id) {

		// 折れ線を描く
		for(int i=0;i<arrsl.size();i++){
			Streamline sl = arrsl.get(i);
			int numvertex = sl.getNumVertex();
			gl2.glLineWidth(3.5f);

			//色のハイライト
			if(color.get(i)){
				if(id == 1){
					//grid1白
					gl2.glColor3d(1.0, 1.0, 1.0);
//					gl2.glLineWidth(2.0f);
				}
				if(id == 2){
					//grid2黄色
					gl2.glColor3d(0.5, 1.0, 0.5);
//					gl2.glLineWidth(2.5f);
				}
			}else{
				if(id == 1){
					//grid1ピンク
					gl2.glColor3d(1.0, 0.0, 1.0);
//					gl2.glLineWidth(2.5f);
				}
				if(id == 2){
					//grid2シアン
					gl2.glColor3d(0.0, 1.0, 1.0);
//					gl2.glLineWidth(2.5f);
				}
			}

			gl2.glBegin(GL2.GL_LINE_STRIP);
			//流線描画
//			if(id == 2){ // only draw streamlines generated under one condition
				for(int j = 0; j < numvertex-1; j++) {
//					if(j == 0) gl2.glColor3d(1.0, 1.0, 1.0);
//					else if(j > 0){
//						if(id == 1) gl2.glColor3d(1.0, 0.0, 1.0);
//						else gl2.glColor3d(0.0, 1.0, 1.0);
//					}
					double pos[] = sl.getPosition(j);
					gl2.glVertex3d(pos[0], pos[1], pos[2]);
				}
//			}
			gl2.glEnd();
		}
	}
	/**
	 * 流線の始点リストを描画する
	 * @param deplist
	 */
	void drawStreamlineStart(ArrayList<int[]> deplist){
		if(grid1 == null) return;
		//始点を描画
		for(int i=0; i<deplist.size(); i++){
			int[] dep = deplist.get(i);
			drawStartGrid(grid1,dep);
		}
	}

	/**
	 * エレメントを表示するテスト
	 */
	void drawElement1(Grid grid,int id,int n1,int n2){
		if(grid == null) return;
		GridPoint egp[] = new GridPoint[2];
		egp = grid.getElement(id).getElement2(n1, n2);
		//gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glColor3d(1.0, 0.0, 0.0);
		gl2.glPointSize(4.0f);
		gl2.glBegin(GL.GL_POINTS);
		gl2.glVertex3d(egp[0].getPosition()[0], egp[0].getPosition()[1], egp[0].getPosition()[2]);
		gl2.glColor3d(0.0, 1.0, 1.0);
		gl2.glVertex3d(egp[1].getPosition()[0], egp[1].getPosition()[1], egp[1].getPosition()[2]);
		gl2.glEnd();
	}
	void drawElement2(Grid grid,int id,int n1,int n2){
		if(grid == null) return;
		GridPoint egp[] = new GridPoint[2];
		egp = grid.getElement(id).getElement2(n1, n2);
		if(egp[0].getPosition()[1]==egp[1].getPosition()[1]){
			System.out.println("same height y!");
		}
		//gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glColor3d(1.0, 0.0, 0.0);
		gl2.glPointSize(4.0f);
		gl2.glBegin(GL.GL_POINTS);
		gl2.glVertex3d(egp[0].getPosition()[0], egp[0].getPosition()[1], egp[0].getPosition()[2]);
		gl2.glColor3d(0.0, 0.0, 1.0);
		gl2.glVertex3d(egp[1].getPosition()[0], egp[1].getPosition()[1], egp[1].getPosition()[2]);
		gl2.glEnd();
	}

	//平面にエレメントが表示されるかのチェック
	void drawElement3(Grid grid,int height){
		if(grid == null) return;
		Element egp[] = grid.getPartElement(height);
		//gl2.glBegin(GL.GL_LINE_LOOP);
		for(int i = 0; i< egp.length;i++){
			gl2.glColor3d(1.0, 0.0, 0.0);
			gl2.glPointSize(4.0f);
			gl2.glBegin(GL.GL_POINTS);
			for(int j=0; j<8; j++){
				gl2.glVertex3d(egp[i].gp[j].getPosition()[0], egp[i].gp[j].getPosition()[1], egp[i].gp[j].getPosition()[2]);
			}
			gl2.glEnd();
		}
	}
	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

}
