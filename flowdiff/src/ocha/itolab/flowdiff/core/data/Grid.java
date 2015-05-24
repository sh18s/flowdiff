package ocha.itolab.flowdiff.core.data;

import ocha.itolab.flowdiff.core.streamline.Streamline;
import ocha.itolab.flowdiff.util.DiffVectorCal;

public class Grid {
	int num[] = new int[3];
	int elnum[] = new int[3];
	int gtotal, etotal;
	GridPoint garray[];
	Element earray[];
	double minmaxPos[] = new double[6]; // xmin, xmax, ymin, ymax, zmin, zmax
	public int startPoint[] = new int [3]; // 始点となる格子の座標値を格納する
	public int target[] = new int [3]; // 的となる格子の座標値を格納する
	public int levelMode = 1; // 初期設定は「ちょいむず」レベル（「かなりむず」は0になる）
	public double environment[] = new double[4]; // 建物の座標値・種別を格納する(1:建物・滑走路　0.5:海 0:何もなし)
	int bnum = 0;
	int count = 0;
	DiffVectorCal dv[];
	double maxvec[] = new double[3];
	double minvec[] = new double[3];
	/**
	 * 格子の頂点数を設定する
	 */
	public void setNumGridPoint(int nx, int ny, int nz) {
		num[0] = nx;
		num[1] = ny;
		num[2] = nz;
		elnum[0] = nx -1;
		elnum[1] = ny -1;
		elnum[2] = nz -1;

		gtotal = nx * ny * nz;
		etotal = (nx - 1) * (ny - 1) * (nz - 1);
		//System.out.println("nx,ny,nz="+nx+","+ny+","+nz);

		// 格子点の配列を確保する
		garray = new GridPoint[gtotal];
		for(int i = 0; i < gtotal; i++)
			garray[i] = new GridPoint();

		// 要素の配列を確保する
		earray = new Element[etotal];
		for(int i = 0; i < etotal; i++)
			earray[i] = new Element();
	}

	//public int[] getNum(){

	//}

	/**
	 * 格子の頂点数を返す
	 */
	public int[] getNumGridPoint() {
		return num;
	}
	public int getNumGridPointAll() {
		return gtotal;
	}

	/**
	 * エレメント数を返す
	 * @return
	 */
	public int[] getNumElement() {
		return elnum;
	}
	public int getNumElementAll() {
		return etotal;
	}

	/**
	 * 建物のある座標値の数を返す
	 * @return
	 */
	public int getNumBuilding(){

		for(int i = 0; i<gtotal;i++){
			if(this.getEnvironment(i)!=0.0){
				bnum++;
			}
		}
		return bnum;
	}

	/**
	 * 端に位置している要素ならtrueを返す
	 */
	public boolean isEdgeElement(int id){
		boolean ans = false;
		//側面の要素
		if(id >=0 && id <= elnum[0]*elnum[1]){
			ans = true;
		}else if(id % elnum[0] == 0){
			ans = true;
		}else if(id % elnum[0] == elnum[0] -1){
			ans = true;
		}else if(id < elnum[0]*elnum[1]*elnum[2] && id >= elnum[0]*elnum[1]*(elnum[2]-1)){
			ans = true;
		}else if(id % (elnum[0] * elnum[1]) < elnum[0] * elnum[1] && id % (elnum[0] * elnum[1])>=elnum[0] * elnum[1]-elnum[0]){
			//上面の要素
			ans = true;
		}else if(id % (elnum[0] * elnum[1]) >=0 &&  id % (elnum[0] * elnum[1]) <= elnum[0]){
			//下面の要素
			ans = true;
		}
		return ans;
	}

	/**
	 * 同じ平面の要素を返す
	 */
	public GridPoint[] getPlanePoints(int height){
		GridPoint[] gp = new GridPoint[num[0]*num[2]];
		int count = 0;
		for(int i = num[0]*(height-1); i < getNumGridPointAll() ;i++){
			for(int j = 0; j<num[0]; j++){
				gp[count] = getGridPoint(i+j);
				count++;
			}
			i += num[0]*num[1]-1;
		}
		return gp;
	}

	/**
	 * 同じ平面の頂点を返す
	 */
	public Element[] getPartElement(int height){
		Element[] egp = new Element[elnum[0]*elnum[2]];
		int count = 0;
		for(int i = elnum[0]*(height-1); i < getNumElementAll() ;i++){
			for(int j = 0; j<elnum[0]; j++){
				egp[count] = getElement(i+j);
				count++;
			}
			i += elnum[0]*elnum[1]-1;
		}
		return egp;
	}

	/**
	 * 所定の格子点を返す
	 */
	public GridPoint getGridPoint(int id) {
		return garray[id];
	}
	/**
	 * getGridPointにエラー処理を加えたもの
	 * @param id
	 * @return
	 */
	public GridPoint getGridPoint2(int id){

		return garray[id];
	}
	/**
	 * 所定の要素を返す
	 */
	public Element getElement(int id) {
		return earray[id];
	}

	/**
	 * 座標値の最小値・最大値を返す
	 */
	public double[] getMinmaxPos() {
		return minmaxPos;
	}

	/**
	 * 格子点の通し番号を求める
	 */
	public int calcGridPointId(int i, int j, int k) {
		return (k * num[0] * num[1] + j * num[0] + i);
	}


	/**
	 * 要素の通し番号を求める
	 */
	public int calcElementId(int i, int j, int k) {
		return (k * (num[0] - 1) * (num[1] - 1) + j * (num[0] - 1) + i);
	}

	/**
	 * 要素の中心座標を求める
	 */
	public double[] calcElementCenter(int i, int j, int k) {
		double[][] pos = new double[8][3];
		double[] cenpos = new double[3];

		Element element = this.getElement(this.calcElementId(i, j, k));
		for (int d = 0; d < 8; d++){
			pos[d] = element.gp[d].getPosition();
		}
		for (int d = 0; d < 8; d++){
			cenpos[0] += pos[d][0];
			cenpos[1] += pos[d][1];
			cenpos[2] += pos[d][2];
		}
		cenpos[0] = cenpos[0]/8;
		cenpos[1] = cenpos[1]/8;
		cenpos[2] = cenpos[2]/8;

		return cenpos;
	}

	/**
	 * 始点の座標値を定める
	 */
	public void setStartPoint(int i, int j, int k) {
		startPoint[0] = i;
		startPoint[1] = j;
		startPoint[2] = k;
	}

	/**
	 * 的の座標値を定める
	 */
	public void setTarget(int i, int j, int k) {
		target[0] = i;
		target[1] = j;
		target[2] = k;
	}


	/**
	 * データを読み終えたあとのまとめ作業
	 */
	public void finalize() {

		// 座標値の最小値・最大値の初期化
		minmaxPos[0] = minmaxPos[2] = minmaxPos[4] = 1.0e+30;
		minmaxPos[1] = minmaxPos[3] = minmaxPos[5] = -1.0e+30;

		// 各格子点について：
		//   座標値の最小・最大を更新する
		for(int i = 0; i < gtotal; i++) {
			double pos[] = garray[i].getPosition();
			minmaxPos[0] = (minmaxPos[0] > pos[0]) ? pos[0] : minmaxPos[0];
			minmaxPos[1] = (minmaxPos[1] < pos[0]) ? pos[0] : minmaxPos[1];
			minmaxPos[2] = (minmaxPos[2] > pos[1]) ? pos[1] : minmaxPos[2];
			minmaxPos[3] = (minmaxPos[3] < pos[1]) ? pos[1] : minmaxPos[3];
			minmaxPos[4] = (minmaxPos[4] > pos[2]) ? pos[2] : minmaxPos[4];
			minmaxPos[5] = (minmaxPos[5] < pos[2]) ? pos[2] : minmaxPos[5];
		}

		// 各要素について：
		//   8個の頂点をセットする
		int count = 0;
		for(int k = 0; k < (num[2] - 1); k++) {
			for(int j = 0; j < (num[1] - 1); j++) {
				for(int i = 0; i < (num[0] - 1); i++, count++) {
					Element e = earray[count];
					e.gp[0] = garray[calcGridPointId(i, j, k)];
					e.gp[1] = garray[calcGridPointId((i + 1), j, k)];
					e.gp[2] = garray[calcGridPointId(i, (j + 1), k)];
					e.gp[3] = garray[calcGridPointId((i + 1), (j + 1), k)];
					e.gp[4] = garray[calcGridPointId(i, j, (k + 1))];
					e.gp[5] = garray[calcGridPointId((i + 1), j, (k + 1))];
					e.gp[6] = garray[calcGridPointId(i, (j + 1), (k + 1))];
					e.gp[7] = garray[calcGridPointId((i + 1), (j + 1), (k + 1))];
				}
			}
		}
	}

	/**
	 * 建物のある座標値・種別を返す
	 */
	public  GridPoint[] getBuildingPoint1(){
		int num = getNumBuilding();
		GridPoint barray[] = new GridPoint[bnum];
		//System.out.println("bnum=" +bnum);
		for(int i = 0; i<gtotal;i++){
			if(this.getEnvironment(i)!=0.0){
				barray[count] = this.getGridPoint(i);
				count++;
			}
		}
		return barray;
	}

	/**
	 * 建物のある座標値・種別を返す
	 */
	public  GridPoint[] getBuildingPoint2(){
		GridPoint barray[] = new GridPoint[bnum];
		int count = 0;//建物の種類の数
		int n = 0;
		//建物のラベリング
		for(int i = 0; i<gtotal;i++){
			if(getGridPoint(i).getBuildingLabel() ==0){
				if(this.getEnvironment(i) == 1.0){
					count++;
					while(n>=0){
						getGridPoint(i+n).setBuildingLabel(count);
						barray[n] = this.getGridPoint(i+n);
						if(this.getEnvironment(i+n) != 1.0){break;}
						n++;
					}
				}
				if(this.getEnvironment(i) == 0.5){

				}
			}
		}
		return barray;
	}

	/**
	 * 一点の建物の位置を返す
	 */
	double[] test = new double[3];
	public double[] getEnvironmentPoint(int id){
		test[0] = this.getGridPoint(id).getPosition()[0];
		test[1] = this.getGridPoint(id).getPosition()[1];
		test[2] = this.getGridPoint(id).getPosition()[2];
		return test;
	}
	/**
	 * 一点の建物の種別を返す
	 */
	public double getEnvironment(int id){
		return this.getGridPoint(id).environment;
	}

	/**
	 * 角度差分を返す
	 */
	public void setAngDiff(int id, double diff){
		this.getGridPoint(id).setAngDiff(diff);
	}

	public double getAngDiff(int id){
		return this.getGridPoint(id).getAngDiff();
	}


	/**
	 * 長さ差分を返す
	 */
	public void setLenDiff(int id, double diff){
		this.getGridPoint(id).setLenDiff(diff);
	}

	public double getLenDiff(int id){
		return this.getGridPoint(id).getLenDiff();
	}

	/**
	 * 一番大きいベクトルを返す
	 * @return
	 */
	public double[] getMaxVector(){

		maxvec[0] = 0.0;
		maxvec[1] = 0.0;
		maxvec[2] = 0.0;

		for(int i=0; i<this.getNumGridPointAll(); i++){
			if(maxvec[0] < this.getGridPoint(i).getVector()[0]){
				maxvec[0] = this.getGridPoint(i).getVector()[0];
			}
			if(maxvec[1] < this.getGridPoint(i).getVector()[1]){
				maxvec[1] = this.getGridPoint(i).getVector()[1];
			}
			if(maxvec[2] < this.getGridPoint(i).getVector()[2]){
				maxvec[2] = this.getGridPoint(i).getVector()[2];
			}
		}
		return maxvec;
	}

	/**
	 * 一番小さいベクトルを返す
	 * @return
	 */
	public double[] getMinVector(){

		minvec[0] = 100.0;
		minvec[1] = 100.0;
		minvec[2] = 100.0;

		for(int i=0; i<this.getNumGridPointAll(); i++){
			if(minvec[0] > this.getGridPoint(i).getVector()[0]){
				minvec[0] = this.getGridPoint(i).getVector()[0];
			}
			if(minvec[1] > this.getGridPoint(i).getVector()[1]){
				minvec[1] = this.getGridPoint(i).getVector()[1];
			}
			if(minvec[2] > this.getGridPoint(i).getVector()[2]){
				minvec[2] = this.getGridPoint(i).getVector()[2];
			}
		}
		return minvec;
	}


	// targetとなっているelementとstreamlineの交差判定
	public boolean intersectWithTarget(Streamline sl) {
		int targetId = calcElementId(target[0],target[1], target[2]);
		return getElement(targetId).intersect(targetId, sl, levelMode);
	}
}
