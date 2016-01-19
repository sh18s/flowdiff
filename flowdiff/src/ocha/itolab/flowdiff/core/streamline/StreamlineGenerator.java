package ocha.itolab.flowdiff.core.streamline;

import java.util.ArrayList;

import jogamp.graph.font.typecast.ot.table.GposTable;
import ocha.itolab.flowdiff.core.data.Element;
import ocha.itolab.flowdiff.core.data.Grid;
import ocha.itolab.flowdiff.core.data.GridPoint;

public class StreamlineGenerator {
	
	static int eface[][] = {
		{0, 2, 4},
		{4, 2, 6},
		{1, 5, 3},
		{3, 5, 7},
		{0, 4, 1},
		{1, 4, 5},
		{2, 3, 6},
		{6, 3, 7},
		{0, 1, 2},
		{2, 1, 3},
		{4, 6, 5},
		{5, 6, 7}
	};

	static int MAX_VERTEX = 1000;
	static double NEARLY_ZERO = 1.0e-20;
	static int preElementId = -1;
	
	
	/**
	 *  流線の線分列を生成する
	 *  grid: 格子データ
	 *  sl: 線分列データ
	 *  elementId: 出発点となる格子のi,j,k値
	 *  sp: 出発点の座標値
	 */
	public static void generate(Grid grid, Streamline sl, int eIjk[], double sp[]) {
		if(grid == null || sl == null || eIjk == null)
			return;
		
		double startpoint[] = new double[3];
		int elementId = grid.calcElementId(eIjk[0], eIjk[1], eIjk[2]);
		preElementId = -1;
		Element element = grid.getElement(elementId);
		int elementIjk[] = new int[3];
		
		// 出発点座標値の特定
		if(sp == null) {
			int i;
			startpoint[0] = startpoint[1] = startpoint[2] = 0.0;
			for(i = 0; i < 8; i++) {
				double gppos[] = element.gp[i].getPosition();
				startpoint[0] += gppos[0];
				startpoint[1] += gppos[1];
				startpoint[2] += gppos[2];
			}
			startpoint[0] /= 8.0;
			startpoint[1] /= 8.0;
			startpoint[2] /= 8.0;
		}
		else {
			startpoint[0] = sp[0];
			startpoint[1] = sp[1];
			startpoint[2] = sp[2];
		}
		
		// 流線の最初の頂点の設定
		sl.resetAllVertex();
		sl.addOneVertex();
		sl.setPosition(0, startpoint[0], startpoint[1], startpoint[2]);
		sl.setElementId(0, eIjk[0], eIjk[1], eIjk[2]);

		// ベクタ場に沿って追跡し、ボリュームの外に出るまで反復する
		elementIjk[0] = eIjk[0];
		elementIjk[1] = eIjk[1];
		elementIjk[2] = eIjk[2];
		while(elementId != preElementId && elementId >= 0 && sl.getNumVertex() < MAX_VERTEX) {
			preElementId = elementId;
			elementId = traverseElements(grid, sl, elementIjk, startpoint);
		}

	}


	/**
	 * ベクタ場に沿って追跡し、線分を1個生成し、隣の格子を特定する
	 */
	static int traverseElements(
		Grid grid, Streamline sl, int eIjk[], double startpoint[]) {

		int elementId = grid.calcElementId(eIjk[0], eIjk[1], eIjk[2]);
		Element element = grid.getElement(elementId);
		
		double vector[] = new double[3]; //直線の方向ベクトル
		double intersect[] = new double[3]; //交点
		int intersectIjk[] = new int[3]; //交差しているElementのijk成分
		double velocity, dist;
		int i;
		
		
		// 格子内部のベクタ値を算出する
		/*
		vector[0] = vector[1] = vector[2] = 0.0;
		for(i = 0; i < 8; i++) {// ここはstreamlineでは終点-始点でよい
			double gpvec[] = element.gp[i].getVector();
			vector[0] += gpvec[0];
			vector[1] += gpvec[1];
			vector[2] += gpvec[2];
		}
		vector[0] /= 8.0;
		vector[1] /= 8.0;
		vector[2] /= 8.0;
		*/
		vector = calcFlowAt(element, startpoint);
		
		// 方向ベクトルの長さ
		velocity = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]); 
		
		// 格子表面を構成する各三角形について
		for(i = 0; i < 12; i++) {
			GridPoint gp1 = element.gp[eface[i][0]];
			GridPoint gp2 = element.gp[eface[i][1]];
			GridPoint gp3 = element.gp[eface[i][2]];
			double gppos1[] = gp1.getPosition();
			double gppos2[] = gp2.getPosition();
			double gppos3[] = gp3.getPosition();
			double k, k1, k2, inner;
			int ne = 0, po = 0;
			
			// 三角形の2辺のベクトルを算出する
			double  edge1[] = new double[3], 
					edge2[] = new double[3],
					trinormal[] = new double[3],
					normal2[] = new double[3],
					e1[] = new double[3], 
					e2[] = new double[3];
		
			edge1[0] = gppos1[0] - gppos3[0];
			edge1[1] = gppos1[1] - gppos3[1];
			edge1[2] = gppos1[2] - gppos3[2];
			edge2[0] = gppos2[0] - gppos3[0];
			edge2[1] = gppos2[1] - gppos3[1];
			edge2[2] = gppos2[2] - gppos3[2];
			
			// 2辺の外積から、三角形の法線ベクトルを求める
			trinormal[0] = edge1[1] * edge2[2] - edge2[1] * edge1[2];
			trinormal[1] = edge1[2] * edge2[0] - edge2[2] * edge1[0];
			trinormal[2] = edge1[0] * edge2[1] - edge2[0] * edge1[1];
			
			// 三角形（を含む平面）と直線との交点におけるパラメータkを求める
			k2 = trinormal[0] * vector[0] + trinormal[1] * vector[1] + trinormal[2] * vector[2];
			if(Math.abs(k2) < 1.0e-10) continue; 
			k1 = trinormal[0] * (gppos1[0] - startpoint[0])
			   + trinormal[1] * (gppos1[1] - startpoint[1])
			   + trinormal[2] * (gppos1[2] - startpoint[2]);
			k = k1 / k2;
			
			// パラメータが負であれば、交点は後退方向にあるので、これ以降の処理を省略する
			if(k < NEARLY_ZERO) continue;
			
			// 三角形（を含む平面）と直線との交点座標を求める
			intersect[0] = vector[0] * k + startpoint[0];
			intersect[1] = vector[1] * k + startpoint[1];
			intersect[2] = vector[2] * k + startpoint[2];
		
			// 交点座標が三角形の内側にあるか判定する
			e1[0] = gppos1[0] - intersect[0];
			e1[1] = gppos1[1] - intersect[1];
			e1[2] = gppos1[2] - intersect[2];
			e2[0] = gppos2[0] - intersect[0];
			e2[1] = gppos2[1] - intersect[1];
			e2[2] = gppos2[2] - intersect[2];
			normal2[0] = e1[1] * e2[2] - e1[2] * e2[1];
			normal2[1] = e1[2] * e2[0] - e1[0] * e2[2];
			normal2[2] = e1[0] * e2[1] - e1[1] * e2[0];
			inner = normal2[0] * trinormal[0] + normal2[1] * trinormal[1] + normal2[2] * trinormal[2];
			if(inner < 0.0) ne = 1;  if(inner > 0.0) po = 1;
			
			e1[0] = gppos2[0] - intersect[0];
			e1[1] = gppos2[1] - intersect[1];
			e1[2] = gppos2[2] - intersect[2];
			e2[0] = gppos3[0] - intersect[0];
			e2[1] = gppos3[1] - intersect[1];
			e2[2] = gppos3[2] - intersect[2];
			normal2[0] = e1[1] * e2[2] - e1[2] * e2[1];
			normal2[1] = e1[2] * e2[0] - e1[0] * e2[2];
			normal2[2] = e1[0] * e2[1] - e1[1] * e2[0];
			inner = normal2[0] * trinormal[0] + normal2[1] * trinormal[1] + normal2[2] * trinormal[2];
			if(inner < 0.0) ne = 1;  if(inner > 0.0) po = 1;
			
			e1[0] = gppos3[0] - intersect[0];
			e1[1] = gppos3[1] - intersect[1];
			e1[2] = gppos3[2] - intersect[2];
			e2[0] = gppos1[0] - intersect[0];
			e2[1] = gppos1[1] - intersect[1];
			e2[2] = gppos1[2] - intersect[2];
			normal2[0] = e1[1] * e2[2] - e1[2] * e2[1];
			normal2[1] = e1[2] * e2[0] - e1[0] * e2[2];
			normal2[2] = e1[0] * e2[1] - e1[1] * e2[0];
			inner = normal2[0] * trinormal[0] + normal2[1] * trinormal[1] + normal2[2] * trinormal[2];
			if(inner < 0.0) ne = 1;  if(inner > 0.0) po = 1; //外積が全て同符号だったら交差している
			// 交差していたらbreak;
			if(ne == 0 || po == 0) break;
		}

		
		// 該当する三角形がある場合、隣の格子に追跡を移す
		if(i < 12) {// 上でbreakしている = 交差している
			boolean ret = specifyNextElement(grid, i, eIjk);
			if(ret == false) {
				//System.out.println("   Out of volume. traverse over");
				return -1; // ボリュームの外に出たらret == falseとなる
			}
		}
		// 該当する三角形がない場合、最も近い格子点上に追跡を移す
		/*
		else {
			boolean ret = specifyNextGridPoint(grid, element, startpoint, intersect, eIjk);
			if(ret == false) return -1;
		}
		*/
		else {
			//System.out.println("   No next element. traverse over");
			return -1;
		}
		
		// 交差位置の前回からの移動量が非常に小さければ、処理を終了する
		dist = (startpoint[0] - intersect[0]) * (startpoint[0] - intersect[0])
		     + (startpoint[1] - intersect[1]) * (startpoint[1] - intersect[1])
		     + (startpoint[2] - intersect[2]) * (startpoint[2] - intersect[2]);
		if(dist < NEARLY_ZERO) {
			//System.out.println("   Same position. traverse over.  dist=" + dist);
			return elementId;
		}

		intersectIjk[0] = eIjk[0];
		intersectIjk[1] = eIjk[1];
		intersectIjk[2] = eIjk[2];
		
		// 線分列に頂点を1個加える
		sl.addOneVertex();
		int vid = sl.getNumVertex() - 1;
		sl.setPosition(vid, intersect[0], intersect[1], intersect[2]);
		sl.setElementId(vid, intersectIjk[0], intersectIjk[1], intersectIjk[2]);
			
		// 格子表面との交差位置を「次回の出発位置」とする
		startpoint[0] = intersect[0];
		startpoint[1] = intersect[1];
		startpoint[2] = intersect[2];
		
		elementId = grid.calcElementId(eIjk[0], eIjk[1], eIjk[2]);
		return elementId;
	}
	 
	 

	/**
	 *  流線の断片と格子面との交点が見つかったら、隣の格子を特定する
	 */
	static boolean specifyNextElement(Grid grid, int i, int eIjk[]) {
		int numgp[] = grid.getNumGridPoint();
		
		// 追跡方向の格子を特定する
		if(i < 2) {
			if(eIjk[0] == 0) {
				//printf("**** streamline goes outside the volume (1)\n");
				eIjk[0] = -1;  return false;
			}
			(eIjk[0])--;
		}
		else if(i < 4) {
			if(eIjk[0] == numgp[0] - 2) {
				//printf("**** streamline goes outside the volume (2)\n");
				eIjk[0] = numgp[0] - 1;  return false;
			}
			(eIjk[0])++;
		}
		else if(i < 6) {
			if(eIjk[1] == 0) {
				//printf("**** streamline goes outside the volume (3)\n");
				eIjk[1] = -1;  return false;
			}
			(eIjk[1])--;
		}
		else if(i < 8) {
			if(eIjk[1] == numgp[1] - 2) {
				//printf("**** streamline goes outside the volume (4)\n");
				eIjk[1] = numgp[1] - 1;  return false;
			}
			(eIjk[1])++;
		}
		else if(i < 10) {
			if(eIjk[2] == 0) {
				//printf("**** streamline goes outside the volume (5)\n");
				eIjk[2] = -1;  return false;
			}
			(eIjk[2])--;
		}
		else if(i < 12) {
			if(eIjk[2] == numgp[2] - 2) {
				//printf("**** streamline goes outside the volume (6)\n");
				eIjk[2] = numgp[2] - 1;  return false;
			}
			(eIjk[2])++;
		}		
		
		return true;
	}
		
		
	static boolean specifyNextGridPoint(
		Grid grid, Element element, double startpoint[], double intersect[], int eIjk[]) {
		int numgp[] = grid.getNumGridPoint();
		GridPoint ngp = null;
		double dist, mindist = 1.0e+30;
		int i, ii = -1, nIjk[] = new int[3], eeIjk[] = new int[3];
		
		// 位置startpointに最も近い格子点を特定する
		GridPoint egp[] = element.gp;
		for(i = 0; i < 8; i++) {
			double gppos[] = egp[i].getPosition();
			dist = (startpoint[0] - gppos[0]) * (startpoint[0] - gppos[0])
			     + (startpoint[1] - gppos[1]) * (startpoint[1] - gppos[1])
			     + (startpoint[2] - gppos[2]) * (startpoint[2] - gppos[2]);
			if(dist < mindist) {
				ii = i;   mindist = dist;
			}
		}
		nIjk[0] = (ii % 2 == 0) ? eIjk[0] : (eIjk[0] + 1);
		nIjk[1] = (ii % 4 <  2) ? eIjk[1] : (eIjk[1] + 1);
		nIjk[2] = (ii < 4)      ? eIjk[2] : (eIjk[2] + 1);
		ngp = grid.getGridPoint(grid.calcElementId(nIjk[0], nIjk[1], nIjk[2]));
		double ngpvec[] = ngp.getVector();
		
		// 次の格子を特定する
		eeIjk[0] = (ngpvec[0] > 0.0) ? nIjk[0] : (nIjk[0] - 1);
		eeIjk[1] = (ngpvec[1] > 0.0) ? nIjk[1] : (nIjk[1] - 1);
		eeIjk[2] = (ngpvec[2] > 0.0) ? nIjk[2] : (nIjk[2] - 1);
		
		// 流れがボリュームの外に出るようであれば、処理を中止する
		if(eeIjk[0] < 0 || eeIjk[0] >= (numgp[0] - 1)) return false;
	    if(eeIjk[1] < 0 || eeIjk[1] >= (numgp[1] - 1)) return false;
	    if(eeIjk[2] < 0 || eeIjk[2] >= (numgp[2] - 1)) return false;

		// 必要な情報を代入し、処理を続行する
	    double ngppos[] = ngp.getPosition();
		eIjk[0] = eeIjk[0];   intersect[0] = ngppos[0];
		eIjk[1] = eeIjk[1];   intersect[1] = ngppos[1];
		eIjk[2] = eeIjk[2];   intersect[2] = ngppos[2];
		
		return true;
	}
	

	/**
	 * その地点における流れ方向を補間により求める
	 */
	static double[] calcFlowAt(Element element, double[] point) {
		double[] vector = new double[3];
		
		double gppos[][] = new double[8][];
		double gpvec[][] = new double[8][];
		for(int i = 0; i < 8; i++) {
			gppos[i] = element.gp[i].getPosition();
			gpvec[i] = element.gp[i].getVector();
		}
		
		// 座標値の内挿比を算出する
		double ratio[] = new double[3];
		ratio[0] = (point[0] - gppos[0][0]) / (gppos[1][0] - gppos[0][0]);
		ratio[1] = (point[1] - gppos[0][1]) / (gppos[2][1] - gppos[0][1]);
		ratio[2] = (point[2] - gppos[0][2]) / (gppos[4][2] - gppos[0][2]);
		
		// 各格子点に乗じる重みを算出する
		double weight[] = new double[8];
		weight[0] = (1.0 - ratio[0]) * (1.0 - ratio[1]) * (1.0 - ratio[2]);
		weight[1] =        ratio[0]  * (1.0 - ratio[1]) * (1.0 - ratio[2]);
		weight[2] = (1.0 - ratio[0]) *        ratio[1]  * (1.0 - ratio[2]);
		weight[3] =        ratio[0]  *        ratio[1]  * (1.0 - ratio[2]);
		weight[4] = (1.0 - ratio[0]) * (1.0 - ratio[1]) * ratio[2];
		weight[5] =        ratio[0]  * (1.0 - ratio[1]) * ratio[2];
		weight[6] = (1.0 - ratio[0]) *        ratio[1]  * ratio[2];
		weight[7] =        ratio[0]  *        ratio[1]  * ratio[2];
		double sum = 0.0;
		for(int i = 0; i < 8; i++)
			sum += Math.abs(weight[i]);
		for(int i = 0; i < 8; i++)
			weight[i] /= sum;
		
		/*
		for(int i = 0; i < 8; i++)
			System.out.print("  " + weight[i]);
		System.out.println("");
		*/
		
		// 重みに応じたベクトル値を加算することでベクトル値を補間する
		for(int i = 0; i < 8; i++) {
			vector[0] += (weight[i] * gpvec[i][0]);
			vector[1] += (weight[i] * gpvec[i][1]);
			vector[2] += (weight[i] * gpvec[i][2]);
		}
		
		return vector;
	}
	
	
	
	
	public static int lastElementId(){
		return preElementId;
	}
	

}
