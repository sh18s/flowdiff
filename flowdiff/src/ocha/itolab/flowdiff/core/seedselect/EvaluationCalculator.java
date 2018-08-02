package ocha.itolab.flowdiff.core.seedselect;

import ocha.itolab.flowdiff.core.streamline.*;

import java.util.*;



public class EvaluationCalculator {

	public static final int xLength = 2;
	public static final int xDirection = 14;
	
	/**
	 * Calculate each evaluation and keep them
	 */
	public static HashMap<String, Double> calcEvaluation(Streamline sl1, Streamline sl2){
		HashMap<String, Double> evaluation = new HashMap<String, Double>();

		evaluation.put("entropy", calcShapeEntropy(sl1, sl2));
		evaluation.put("diff", calcPairDistance(sl1, sl2));
		
		return evaluation;
	}	
	

	/**
	 * ある流線ペアの形状エントロピーを算出する
	 */
	static double calcShapeEntropy(Streamline list1, Streamline list2){		
		double e = 0.0;

		Streamline sl1 = list1;
		Streamline sl2 = list2;
		ArrayList<ArrayList<Double>> segments1 = getSegment(sl1);
		ArrayList<ArrayList<Double>> segments2 = getSegment(sl2);
		e = calcEntropy(segments1) + calcEntropy(segments2);
		return e;
	}
	
	/**
	 * 流線の形状エントロピーを算出する
	 */
	static double calcEntropy(ArrayList<ArrayList<Double>> segments){
		double e = 0.0;
		double[][] p = calcPx(segments);
		
		if(0 < segments.size()){
			for(int i = 0; i < xLength; i++){
				for(int j = 0; j < xDirection; j++){
					if(p[i][j] == 0){
						continue;
					}else{
						e -= p[i][j] * Math.log10(p[i][j]);
					}
				}
			}
		}
		return e;
	}
	
	/**
	 * すべてのxについて、p(x)を算出する
	 */
	static double[][] calcPx(ArrayList<ArrayList<Double>> segments){
		double p[][] = new double[xLength][xDirection];
		int[][] x = calcSmallx(segments);
		int num = segments.size();
		if(num == 0) num = 1;		
		for(int i = 0; i < xLength; i++){
			for(int j = 0; j < xDirection; j++){
				p[i][j] = (double)x[i][j] / (double)num;
			}
		}
		return p;
	}
	
	
	/**
	 * Calculate "x" of a streamline
	 * calcPxLengthを利用
	 */
	static int[][] calcSmallx(ArrayList<ArrayList<Double>> segments){
		// initialize Array
		int x[][] = new int[xLength][xDirection];
		for(int i = 0; i < xLength; i++){
			for(int j = 0; j < xDirection; j++){
				x[i][j] = 0;
			}
		}
		// Get flags of length
		ArrayList<Integer> lFlagArray = calcPxLength(segments);
		// Get "x"
		for(int i = 0;i < segments.size();i ++){
			int lFlag = lFlagArray.get(i);
			int dFlag = calcOnePxDirection(segments.get(i));
			x[lFlag][dFlag] ++;
		}
		return x;
	}
	
	/**
	 * ある流線のすべてのセグメントについて、長さを評価する
	 */
	static ArrayList<Integer> calcPxLength(ArrayList<ArrayList<Double>> segments){
		ArrayList<Integer> flagArray = new ArrayList<Integer>();
		
		// TODO: add steps
		// Velocity: 2 steps
		
		// Calculate length of all segments
		ArrayList<Double> lengthArray = new ArrayList<Double>();
		double aveLength = 0; // Keep longest length
		
		for(ArrayList<Double> segment: segments){
			double length = 0;
			for(int j = 0;j < 3; j++){
				length += Math.pow(segment.get(j), 2.0d);
			}
			aveLength += length;
			lengthArray.add(Math.sqrt(length));
		}
		// Calculate average of length of segments
		if(segments.size() != 0){
			aveLength /= (double)segments.size();
			
			// Decide Flag
			for(Double length: lengthArray){
				if(length < aveLength){
					flagArray.add(0);
				}
				else{
					flagArray.add(1);
				}
			}
		}
		else flagArray.add(0);
		
		return flagArray;
	}
	
	/**
	 * あるセグメントについて、方向を評価する
	 * dFlag = 0~5: 6 steps evaluation
	 * - x+:0, y+:1, z+:2, x-:3, y-:4, z-:5
	 * dFlag = 6~13: 8 steps evaluation
	 */
	static int calcOnePxDirection(ArrayList<Double> segment){
		// Direction：14 steps evaluation
		int dFlag = 0;
		
		// Get and keep absolute value and axis of segment
		ArrayList<Double> absSeg = new ArrayList<Double>();
		for(int i = 0; i < 3; i++){
			// 8桁で切る
			String seg = String.format("%.8f", segment.get(i));
			segment.set(i, Double.parseDouble(seg));
			absSeg.add(Math.abs(segment.get(i)));
		}
		
		// Sort absolute values to decide max value
		ArrayList<Double> absRank = new ArrayList<Double>();
		
		// Sort values of segment
		absRank.add(absSeg.get(0));
		if(absSeg.get(0) > absSeg.get(1)){
			absRank.add(absSeg.get(1));
		}else{
			absRank.add(0, absSeg.get(0));
		}
		if(absRank.get(1) > absSeg.get(2)){
			absRank.add(absSeg.get(2));
		}else if(absRank.get(0) > absSeg.get(2)){
			absRank.add(1, absSeg.get(2));
		}else absRank.add(0, absSeg.get(2));
		
		// 6 steps evaluation
		// Decide axis of max absolute value
		int fFlag = segment.indexOf(absRank.get(0));
		if(fFlag == -1)
			fFlag = segment.indexOf(-1.0d * absRank.get(0)) + 3;

		// Decide 6 or 8 steps evaluation
		if(absRank.get(0) > 10.0d * absRank.get(1)){
			// 6 steps evaluation
			dFlag = fFlag;
		}else{
			// 8 steps evaluation
			if(segment.get(0) >= 0){
				if(segment.get(1) >= 0){
					if(segment.get(2) >= 0) dFlag = 6;
					else dFlag = 7;
				}else{
					if(segment.get(2) >= 0) dFlag = 8;
					else dFlag = 9;
				}
			}else{
				if(segment.get(1) >= 0){
					if(segment.get(2) >= 0) dFlag = 10;
					else dFlag = 11;
				}else{
					if(segment.get(2) >= 0) dFlag = 12;
					else dFlag = 13;
				}
			}
		}
//		System.out.println("dFlag = " + dFlag);
		return dFlag;
	}
	
	/**
	 * ある流線を有向線分に分割する
	 */
	static ArrayList<ArrayList<Double>> getSegment(Streamline sl){
		ArrayList<ArrayList<Double>> segments = new ArrayList<ArrayList<Double>>();
		int nums = sl.getNumVertex() -1;
		// 頂点が1つ以上存在すればセグメントを計算
		if(0 < nums){
			for(int i = 0; i < nums - 1; i++){
				double p1[] = sl.getPosition(i);
				double p2[] = sl.getPosition(i + 1);
				ArrayList<Double> seg = new ArrayList<Double>();
				for(int j = 0; j < 3; j++){
					seg.add(j, p2[j] - p1[j]);
				}
				segments.add(seg);
			}
		}
		return segments;
	}


	/**
	 * ある流線ペア間の距離を計算する
	 */
	static double calcPairDistance(Streamline sl1, Streamline sl2){
		double distance = 0;
		boolean useAllVer = true;
		Streamline longSl = new Streamline();
		Streamline shortSl = new Streamline();
		
		int ver1 = sl1.getNumVertex();
		int ver2 = sl2.getNumVertex();
		
		// Cut off too short streamlines
		if(ver1 <= 10 || ver2 <= 10) return distance;
		
		// Compare of number of vertexes and rename
		if(ver1 < ver2){
			longSl = sl2; shortSl = sl1;
		}else{
			longSl = sl1; shortSl = sl2;
		}
		
		DistPos distPos = new DistPos();
		double dist = 0;
		int nearPos = 0;
		int prePos = 0; // 1つ前の格子点の、最も近い格子点
		
		// 最後の頂点どうしの距離が遠すぎる流線ペアは，すべての頂点を使用しない
		// Get Last vertexes
		double[] pos1 = sl1.getPosition(ver1 -1);
		double[] pos2 = sl2.getPosition(ver2 -1);
		double lastDist = Math.sqrt((pos1[0] - pos2[0]) * (pos1[0] - pos2[0])
				 + (pos1[1] - pos2[1]) * (pos1[1] - pos2[1])
				 + (pos1[2] - pos2[2]) * (pos1[2] - pos2[2]));
		
		int vertex = 0;
		
		for(int i = 0; i < longSl.getNumVertex(); i++){
			if(i < shortSl.getNumVertex()){
				pos1 = shortSl.getPosition(i);
				distPos = calcOnePosDistance(pos1, longSl);
				distance += distPos.getDist();
				vertex++;
				if(i == shortSl.getNumVertex() -1){
					if(lastDist > (distance / (double)vertex) * 10){ 
						useAllVer = false;
					}
				}
			}else{
				if(!useAllVer) break;
			}
			pos2 = longSl.getPosition(i);
			distPos = calcOnePosDistance(pos2, shortSl);
			distance += distPos.getDist();
			vertex++;
		}
//		System.out.println("distance = " + distance/vertex);
		if(vertex == 0) return distance;
		else return distance / vertex;
		
//		distance = calcDistance(useAllVer, longSl, shortSl);
		
//		for(int i = 0; i < shortSl.getNumVertex(); i++){
//			if(i > 0) prePos = nearPos;
//			double pos[] = shortSl.getPosition(i);
//			distPos = calcOnePosDistance(pos, longSl);
//			nearPos = distPos.getPos();
////			System.out.println("i = " + i + ", nearPos = " + nearPos);
//			// 逆流判定
//			if(prePos > nearPos){
//				pos2 = longSl.getPosition(i);
//				dist = (pos[0] - pos2[0]) * (pos[0] - pos2[0])
//					 + (pos[1] - pos2[1]) * (pos[1] - pos2[1])
//					 + (pos[2] - pos2[2]) * (pos[2] - pos2[2]);
//			}else dist = distPos.getDist();
//			distance += dist;
////			distance += calcOnePosDistance(pos, slb);
//		}
//		distance /= shortSl.getNumVertex();
//		return distance;
	}
	
	/**
	 * Calculate Distance of a streamline pair.
	 * @param useAllVer
	 * @param longSl
	 * @param shortSl
	 * @return
	 */
	static double calcDistance(boolean useAllVer, Streamline longSl, Streamline shortSl){
		double distance = 0 ;
		int vertex = 0;
		double[] pos = new double[3];
		double[] pos2 = new double[3];
		DistPos distPos = new DistPos();
		
		for(int i = 0; i < longSl.getNumVertex() -1; i++){
			if(i < shortSl.getNumVertex() - 1){
				pos = shortSl.getPosition(i);
				distPos = calcOnePosDistance(pos, longSl);
				distance += distPos.getDist();
				vertex++;
			}else{
				if(!useAllVer) break;
			}
			pos2 = longSl.getPosition(i);
			distPos = calcOnePosDistance(pos2, shortSl);
			distance += distPos.getDist();
			vertex++;
		}
		if(vertex == 0) return distance;
		else return distance / vertex;
	}
	
	/**
	 * ある格子点から、ペアとなる流線の最も近い格子点までの距離を計算する
	 */
	static DistPos calcOnePosDistance(double pos[], Streamline sl) {
		double distance = 1.0e+30;
		DistPos distPos = new DistPos();
		
		if(sl.getNumVertex() <= 0){
			distPos.setDist(distance);
			distPos.setPos(0);
			return distPos;
		}
		
		// for each point of the streamline
		int nearPos = 0;
		for(int i = 0; i < sl.getNumVertex(); i++) {
			double pos2[] = sl.getPosition(i);
			double d = (pos[0] - pos2[0]) * (pos[0] - pos2[0])
					 + (pos[1] - pos2[1]) * (pos[1] - pos2[1])
					 + (pos[2] - pos2[2]) * (pos[2] - pos2[2]);
			if(distance > d)
				distance = d;
				nearPos = i;
		}
		distPos.setDist(Math.sqrt(distance));
		distPos.setPos(nearPos);
		return distPos;
	}
	
	// 最も近い格子点とその距離
	static class DistPos{
		double distance;
		int pos;
		
		void setDist(double distance){
			this.distance = distance;
		}
		void setPos(int pos){
			this.pos = pos;
		}
		double getDist(){
			return this.distance;
		}
		int getPos(){
			return this.pos;
		}
	}

}