//package ocha.itolab.flowdiff.core.seedselect;
//
//import ocha.itolab.flowdiff.applet.flowdiff.PlotPanel;
//import ocha.itolab.flowdiff.core.data.*;
//import ocha.itolab.flowdiff.core.streamline.*;
//
//import java.util.*;
//
//
//
//
//public class SingleEvaluator {
//
//	public static final int xLength = 2;
//	public static final int xDirection = 14;
//	public static final double alpha = 0.3;
//	public static final double beta = 1.0 - alpha;
//	public static ArrayList<IndependentValue>  graphIVList = new ArrayList<IndependentValue>();
//	
//	/**
//	 * 視点に依存しない評価値をわたすための関数
//	 * Fuction to hand score of pair of streamlines to "BestSeedSetSelector"
//	 */
//	static double calcSingleValue(Streamline sl1, Streamline sl2){
//		double singleValue;
//
//		double diff = calcPairDistance(sl1, sl2);
//		double entropy = calcShapeEntropy(sl1, sl2);
//		//System.out.println("e, diff: " + entropy + diff);
//		
//		//　正規化するために最大値を保存
//		if(PlotPanel.dmax < diff) PlotPanel.dmax = diff;
//		if(PlotPanel.emax < entropy) PlotPanel.emax = entropy;
//		
//		makeGraphIVList(diff, entropy);
////		System.out.println("elim = " + PlotPanel.elim + ", dlim = " + PlotPanel.dlim);
//		
//		BestSeedSetSelector bestSeedSetSelecotr = new BestSeedSetSelector();
//		
//		// グラフで設定された閾値と比較
////		if(BestSeedSetSelector.selectCounter > 1){
////			if(PlotPanel.elim > 0){
////				if(diff > -PlotPanel.dlim / PlotPanel.elim * entropy + PlotPanel.dlim){
////					// 本来は正規化すべきだが、エントロピーを10^29することで調整
////					singleValue = calcIndependentValue(diff, entropy * Math.pow(10, 29));
////				}else singleValue = 0;
////			}else{
////				return singleValue = 0;
////			}
////		}else{
////			singleValue = calcIndependentValue(diff, entropy * Math.pow(10, 29));
////		}
//		
//		singleValue = calcIndependentValue(diff, entropy);
//		return singleValue;
//	}	
//	
//	/**
//	 * ある流線ペアの視点に依存しない評価値を計算する
//	 */
//	static double calcIndependentValue(double difference, double shapeEntropy){
//		double iValue = 0.0;
////		TODO: 正規化
//		iValue = alpha * difference + beta + shapeEntropy;
//		return iValue;
//	}
//	
//	// グラフ用リストをつくる
//	static void makeGraphIVList(double difference, double shapeEntropy){
//		IndependentValue iv = new IndependentValue();
//		iv.inputValue(difference, shapeEntropy);
//		graphIVList.add(iv);
//	}
//
//	/**
//	 * ある流線ペアの形状エントロピーを算出する
//	 */
//	static double calcShapeEntropy(Streamline list1, Streamline list2){		
//		double e = 0.0;
//
//		Streamline sl1 = list1;
//		Streamline sl2 = list2;
//		ArrayList<ArrayList<Double>> segments1 = getSegment(sl1);
//		ArrayList<ArrayList<Double>> segments2 = getSegment(sl2);
//		e = calcEntropy(segments1) + calcEntropy(segments2);
////		double ip = calcMinIP(segments1) + calcMinIP(segments2);
////		System.out.println("e = " + e);
//		return e;
//	}
//	
//	/**
//	 * 流線の形状エントロピーを算出する
//	 */
//	static double calcEntropy(ArrayList<ArrayList<Double>> segments){
//		double e = 0.0;
//		double[][] p = calcPx(segments);
//		for(int i = 0; i < xLength; i++){
//			for(int j = 0; j < xDirection; j++){
//				if(p[i][j] == 0){
//					continue;
//				}else{
//					e -= p[i][j] * Math.log10(p[i][j]);
//				}
//			}
//		}
////		System.out.println("entropy = " + e);
//		return e;
//	}
//	
//	/**
//	 * すべてのxについて、p(x)を算出する
//	 */
//	static double[][] calcPx(ArrayList<ArrayList<Double>> segments){
//		double p[][] = new double[xLength][xDirection];
////		int[][] x = calcSmallx(segments);
//		int[][] x = calcSmallx2(segments);
//		int num = segments.size();
//		if(num == 0) num = 1;		
//		for(int i = 0; i < xLength; i++){
//			for(int j = 0; j < xDirection; j++){
//				p[i][j] = (double)x[i][j] / (double)num;
////				System.out.println("x, num, p = " + x[i][j] + "," + num + "," + p[i][j]);
//			}
//		}
//		return p;
//	}
//	
//	/**
//	 * ある流線について、xを算出する
//	 */
//	static int[][] calcSmallx(ArrayList<ArrayList<Double>> segments){
//		// Initialize Array
//		int x[][] = new int[xLength][xDirection];
//		for(int i = 0; i < xLength; i++){
//			for(int j = 0; j < xDirection; j++){
//				x[i][j] = 0;
//			}
//		}
//		
//		for(ArrayList<Double> seg: segments){
//			int lFlag = calcOnePxLength(seg);
//			int dFlag = calcOnePxDirection2(seg);
//			x[lFlag][dFlag] ++;
//		}
//		return x;
//	}
//	
//	
//	/**
//	 * Calculate "x" of a streamline
//	 * calcPxLengthを利用
//	 */
//	static int[][] calcSmallx2(ArrayList<ArrayList<Double>> segments){
//		// initialize Array
//		int x[][] = new int[xLength][xDirection];
//		for(int i = 0; i < xLength; i++){
//			for(int j = 0; j < xDirection; j++){
//				x[i][j] = 0;
//			}
//		}
//		// Get flags of length
//		ArrayList<Integer> lFlagArray = calcPxLength(segments);
//		// Get "x"
//		for(int i = 0;i < segments.size();i ++){
//			int lFlag = lFlagArray.get(i);
//			int dFlag = calcOnePxDirection2(segments.get(i));
//			x[lFlag][dFlag] ++;
////			System.out.println("lFlag = " + lFlag);
//		}
//		return x;
//	}
//	
//	/**
//	 * あるセグメントについて、長さを評価する
//	 */
//	static int calcOnePxLength(ArrayList<Double> segment){
//		// 速度: 1段階評価
//		return 0;
//	}
//	
//	/**
//	 * ある流線のすべてのセグメントについて、長さを評価する
//	 */
//	static ArrayList<Integer> calcPxLength(ArrayList<ArrayList<Double>> segments){
//		ArrayList<Integer> flagArray = new ArrayList<Integer>();
//		
//		// TODO: add steps
//		// Velocity: 2 steps
//		
//		// Calculate length of all segments
//		ArrayList<Double> lengthArray = new ArrayList<Double>();
//		double aveLength = 0; // Keep longest length
//		
//		for(ArrayList<Double> segment: segments){
//			double length = 0;
//			for(int j = 0;j < 3; j++){					
//				length += Math.pow(segment.get(j), 2.0d);
//			}
//			aveLength += length;
//			lengthArray.add(Math.sqrt(length));
//		}
//		// Calculate average of length of segments
//		if(segments.size() != 0){
//			aveLength /= (double)segments.size();
//			
//			// Decide Flag
//			for(Double length: lengthArray){
//				if(length < aveLength){
//					flagArray.add(0);
//				}
//				else{
//					flagArray.add(1);
//				}
//			}
//		}
//		else flagArray.add(0);
//		
//		return flagArray;
//	}
//	
//	/**
//	 * あるセグメントについて、方向を評価する
//	 */
//	static int calcOnePxDirection(ArrayList<Double> segment){
//		// 方向：6段階評価
//		int dFlag;
//		ArrayList<Double> absSeg = new ArrayList<Double>();
//		for(int i = 0; i < 3; i++){
//			String seg = String.format("%.8f", segment.get(i));
//			segment.set(i, Double.parseDouble(seg));
//			absSeg.add(Math.abs(segment.get(i)));
//		}
//		double max = Math.max(Math.abs(segment.get(0)), Math.abs(segment.get(1)));
//		max = Math.max(max, Math.abs(segment.get(2)));
//
//		if(absSeg.get(0) <= absSeg.get(1)){
//			if(absSeg.get(1) <= absSeg.get(2)){
//				if(0 <= segment.get(2)) dFlag = 0;
//				else dFlag = 1;
//			}else{
//				if(0 <= segment.get(1)) dFlag = 2;
//				else dFlag = 3;
//			}
//		}else{
//			if(0 < segment.get(0)) dFlag = 4;
//			else dFlag = 5;
//		}
//		return dFlag;
//	}
//	
//	/**
//	 * あるセグメントについて、方向を評価する
//	 * dFlag = 0~5: 6 steps evaluation
//	 * - x+:0, y+:1, z+:2, x-:3, y-:4, z-:5
//	 * dFlag = 6~13: 8 steps evaluation
//	 */
//	static int calcOnePxDirection2(ArrayList<Double> segment){
//		// Direction：14 steps evaluation
//		int dFlag = 0;
//		
//		// Get and keep absolute value and axis of segment
//		ArrayList<Double> absSeg = new ArrayList<Double>();
//		for(int i = 0; i < 3; i++){
//			// 8桁で切る
//			String seg = String.format("%.8f", segment.get(i));
//			segment.set(i, Double.parseDouble(seg));
//			absSeg.add(Math.abs(segment.get(i)));
//		}
//		
//		// Sort absolute values to decide max value
//		ArrayList<Double> absRank = new ArrayList<Double>();
//		
//		// Sort values of segment
//		absRank.add(absSeg.get(0));
//		if(absSeg.get(0) > absSeg.get(1)){
//			absRank.add(absSeg.get(1));
//		}else{
//			absRank.add(0, absSeg.get(0));
//		}
//		if(absRank.get(1) > absSeg.get(2)){
//			absRank.add(absSeg.get(2));
//		}else if(absRank.get(0) > absSeg.get(2)){
//			absRank.add(1, absSeg.get(2));
//		}else absRank.add(0, absSeg.get(2));
//		
//		// 6 steps evaluation
//		// Decide axis of max absolute value
//		int fFlag = segment.indexOf(absRank.get(0));
//		if(fFlag == -1)
//			fFlag = segment.indexOf(-1.0d * absRank.get(0)) + 3;
//
//		// Decide 6 or 8 steps evaluation
//		if(absRank.get(0) > 10.0d * absRank.get(1)){
//			// 6 steps evaluation
//			dFlag = fFlag;
//		}else{
//			// 8 steps evaluation
//			if(segment.get(0) >= 0){
//				if(segment.get(1) >= 0){
//					if(segment.get(2) >= 0) dFlag = 6;
//					else dFlag = 7;
//				}else{
//					if(segment.get(2) >= 0) dFlag = 8;
//					else dFlag = 9;
//				}
//			}else{
//				if(segment.get(1) >= 0){
//					if(segment.get(2) >= 0) dFlag = 10;
//					else dFlag = 11;
//				}else{
//					if(segment.get(2) >= 0) dFlag = 12;
//					else dFlag = 13;
//				}
//			}
//		}
////		System.out.println("dFlag = " + dFlag);
//		return dFlag;
//	}
//	
//	/**
//	 * ある流線について、最小の内積を求める
//	 */
//	static double calcMinIP(ArrayList<ArrayList<Double>> segments){
//		double min = 0;
//		
//		for(int i = 0; i < segments.size() - 1; i++){
//			ArrayList<Double> segment1 = new ArrayList<Double>();
//			ArrayList<Double> segment2 = new ArrayList<Double>();
//			segment1 = segments.get(i);
//			// 正規化するためにセグメントの大きさを取得
//			double mag1 = Math.pow(segment1.get(0), 2) + Math.pow(segment1.get(1), 2) + Math.pow(segment1.get(2), 2);
//			segment2 = segments.get(i + 1);
//			// 正規化するためにセグメントの大きさを取得
//			double mag2 = Math.pow(segment2.get(0), 2) + Math.pow(segment2.get(1), 2) + Math.pow(segment2.get(2), 2);
//			double ip = 0;
//			// 正規化したセグメント同士の内積を計算
//			for(int k = 0; k < 3; k++){
//				if(mag1 == 0 && mag2 ==0){
//					ip += (segment1.get(k) / Math.sqrt(mag1)) * (segment2.get(k) / Math.sqrt(mag2));
//				}
//			}
//			if(ip < min) min = ip;
//		}
//		return min;
//	}
//
//	
//	/**
//	 * ある流線を有向線分に分割する
//	 */
//	static ArrayList<ArrayList<Double>> getSegment(Streamline sl){
//		ArrayList<ArrayList<Double>> segments = new ArrayList<ArrayList<Double>>();
//		int nums = sl.getNumVertex() -1;
//		for(int i = 0; i < nums - 1; i++){
//			double p1[] = sl.getPosition(i);
//			double p2[] = sl.getPosition(i + 1);
//			ArrayList<Double> seg = new ArrayList<Double>();
//			for(int j = 0; j < 3; j++){
//				seg.add(j, p2[j] - p1[j]);
//			}
//			segments.add(seg);
//		}
//		return segments;
//	}
//
//	/**
//	 * すべての流線ペアについて差分を計算する
//	 */
//	static ArrayList<Double> calcStreamlineDifference(ArrayList<Streamline> list1, ArrayList<Streamline> list2){
//		ArrayList<Double> diff = new ArrayList<Double>();
//
//		for(int i = 0; i < list1.size(); i ++){
//			Streamline sl1 = list1.get(i);
//			Streamline sl2 = list1.get(i);
//			double distance = calcPairDistance(sl1, sl2);
//			diff.add(distance);
//		}
//		return diff;
//	}
//	
//	
//	/**
//	 * ある流線ペア間の距離を計算する
//	 */
//	static double calcPairDistance(Streamline sl1, Streamline sl2){
//		int numver = 0;
//		double distance = 1.0e+30;
//		int ver1 = sl1.getNumVertex();
//		int ver2 = sl2.getNumVertex();
//		if(ver1 <= 0 || ver2 <= 0) return distance;
//		
//		Streamline sla, slb; // 線分が多い方の流線をslaにする
//		if(ver1 < ver2){
//			sla = sl2; slb = sl1; numver = ver2;
//		}else{
//			sla = sl1; slb = sl2; numver = ver1;
//		}
//		DistPos distPos = new DistPos();
//		double dist = 0;
//		int nearPos = 0;
//		int prePos = 0; // 1つ前の格子点の、最も近い格子点
//		
//		for(int i = 0; i < numver; i++){
//			if(i > 0) prePos = nearPos;
//			double pos[] = sla.getPosition(i);
//			distPos = calcOnePosDistance(pos, slb);
//			nearPos = distPos.getPos();
////			System.out.println("i = " + i + ", nearPos = " + nearPos);
//			// 逆流判定
//			if(prePos > nearPos){
//				double pos2[] = slb.getPosition(i);
//				dist = (pos[0] - pos2[0]) * (pos[0] - pos2[0])
//					 + (pos[1] - pos2[1]) * (pos[1] - pos2[1])
//					 + (pos[2] - pos2[2]) * (pos[2] - pos2[2]);
//			}else dist = distPos.getDist();
//			distance += dist;
////			distance += calcOnePosDistance(pos, slb);
//		}
//		distance /= numver;
//		return distance;
//		
//	}
//	
//	/**
//	 * ある格子点から、ペアとなる流線の最も近い格子点までの距離を計算する
//	 */
//	static DistPos calcOnePosDistance(double pos[], Streamline sl) {
//		double distance = 1.0e+30;
//		DistPos distPos = new DistPos();
//		
//		if(sl.getNumVertex() <= 0){
//			distPos.setDist(distance);
//			distPos.setPos(0);
//			return distPos;
//		}
//		
//		// for each point of the streamline
//		int nearPos = 0;
//		for(int i = 0; i < sl.getNumVertex(); i++) {
//			double pos2[] = sl.getPosition(i);
//			double d = (pos[0] - pos2[0]) * (pos[0] - pos2[0])
//					 + (pos[1] - pos2[1]) * (pos[1] - pos2[1])
//					 + (pos[2] - pos2[2]) * (pos[2] - pos2[2]);
//			if(distance > d)
//				distance = d;
//				nearPos = i;
//		}
//		distPos.setDist(Math.sqrt(distance));
//		distPos.setPos(nearPos);
//		return distPos;
//	}
//	
//	// 最も近い格子点とその距離
//	static class DistPos{
//		double distance;
//		int pos;
//		
//		void setDist(double distance){
//			this.distance = distance;
//		}
//		void setPos(int pos){
//			this.pos = pos;
//		}
//		double getDist(){
//			return this.distance;
//		}
//		int getPos(){
//			return this.pos;
//		}
//	}
//
//}