package ocha.itolab.flowdiff.core.seedselect;

//import ocha.itolab.flowdiff.core.data.*;
import ocha.itolab.flowdiff.core.streamline.*;

import java.util.*;

import ocha.itolab.flowdiff.core.seedselect.BinarySearch;

public class ViewIndependentEvaluator {

	public static final int xLength = 1;
	public static final int xDirection = 6;
	public static final double alpha = 0.3;
	public static final double beta = 1.0 - alpha;
	
	
	/**
	 * 評価値と流線番号をまとめるクラス
	 */
	public static class RankValue{
		int num;
		double value;
	}
	
	public static class PutRankValue{
		public static RankValue putRankValue(int i, double value){
			RankValue rankValue = new RankValue();
			rankValue.num = i;
			rankValue.value = value;
			return rankValue;
		}
	}
	
	/**
	 * 視点に依存しない評価値を算出し、ランキングする
	 */
	static LinkedList<RankValue> rankIndependentValue(StreamlineArray slset){
		LinkedList<RankValue> rankList = new LinkedList<RankValue>();
		
		ArrayList<Streamline> list1 = slset.getAllList1();
		ArrayList<Streamline> list2 = slset.getAllList2();
		
		ArrayList<Double> diff = calcStreamlineDifference(list1, list2);
		ArrayList<Double> entropy = calcShapeEntropy(list1, list2);
		
		for(int i = 0; i < list1.size(); i ++){ // i : 流線番号
			Streamline sl1 = list1.get(i);
			Streamline sl2 = list1.get(i);
			double iValue = calcIndependentValue(diff.get(i), entropy.get(i));
			rankList = BinarySearch.binarySearch(rankList, PutRankValue.putRankValue(i, iValue));
		}
		return rankList;
	}
	
	/**
	 * すべての流線の視点に依存しない評価値を算出する
	 */
	public StreamlineArray calcAllIndependentValue(StreamlineArray slset){
		StreamlineArray selected = new StreamlineArray();
		
		ArrayList<Streamline> list1 = slset.getAllList1();
		ArrayList<Streamline> list2 = slset.getAllList2();
		
		ArrayList<Double> diff = calcStreamlineDifference(list1, list2);
		ArrayList<Double> entropy = calcShapeEntropy(list1, list2);
		
		for(int i = 0; i < list1.size(); i ++){
			Streamline sl1 = list1.get(i);
			Streamline sl2 = list1.get(i);
			double iValue = calcIndependentValue(diff.get(i), entropy.get(i));
//			rankList = BinarySearch.binarySearch(rankList, iValue);
			
		}
		return selected;
	}
	
	
	/**
	 * ある流線ペアの視点に依存しない評価値を計算する
	 */
	static double calcIndependentValue(double difference, double shapeEntropy){
		double iValue = 0.0;
//		TODO: 正規化
		iValue = alpha * difference + beta + shapeEntropy;
		return iValue;
	}

	/**
	 * すべての流線ペアの形状エントロピーを算出する
	 */
	static ArrayList<Double> calcShapeEntropy(ArrayList<Streamline> list1, ArrayList<Streamline> list2){		
		ArrayList<Double> e = new ArrayList<Double>();
		
		for(int i = 0; i < list1.size(); i++){
			Streamline sl1 = list1.get(i);
			Streamline sl2 = list2.get(i);
			ArrayList<ArrayList<Double>> segments1 = getSegment(sl1);
			ArrayList<ArrayList<Double>> segments2 = getSegment(sl2);
			e.add(calcEntropy(segments1) + calcEntropy(segments2));
		}
		return e;
	}
	
	/**
	 * 流線の形状エントロピーを算出する
	 */
	static double calcEntropy(ArrayList<ArrayList<Double>> segments){
		double e = 0.0;
		double[][] p = calcPx(segments);
		for(int i = 0; i < xLength; i++){
			for(int j = 0; j < xDirection; j++){
				e -= p[i][j] * Math.log10(p[i][j]);
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
		
		for(int i = 0; i < xLength; i++){
			for(int j = 0; j < xDirection; j++){
				p[i][j] = x[i][j] / num;
			}
		}
		return p;
	}
	
	/**
	 * ある流線について、xを算出する
	 */
	static int[][] calcSmallx(ArrayList<ArrayList<Double>> segments){
		int x[][] = new int[xLength][xDirection];
		for(int i = 0; i < xLength; i++){
			for(int j = 0; j < xDirection; j++){
				x[i][j] = 0;
			}
		}
		int[] lx = calcPxLength(segments);
		int[] dx = calcPxDirection(segments);
		for(int i = 0; i < segments.size(); i ++){
			x[lx[i]][dx[i]] ++ ;
		}
		return x;
	}
	
	/**
	 * ある流線について、長さを評価する
	 */
	static int[] calcPxLength(ArrayList<ArrayList<Double>> segments){
		// 速度: 1段階評価
		int x[] = {0, 0, 0, 0, 0, 0};
		return x;
	}
	
	/**
	 * ある流線について、方向を評価する
	 */
	static int[] calcPxDirection(ArrayList<ArrayList<Double>> segments){
		// 方向：6段階評価
		int x[] = {0, 0, 0, 0, 0, 0};
		
		for(ArrayList<Double> seg: segments){
			ArrayList<Double> absSeg = new ArrayList<Double>();
			for(double element: seg){
				absSeg.add(Math.abs(element));
			}
			double max = Math.max(absSeg.indexOf(0), absSeg.indexOf(1));
			max = Math.max(max, absSeg.indexOf(2));
			if(max == absSeg.indexOf(0)) x[0]++;
			else if(max == absSeg.indexOf(1)) x[1]++;
			else if(max == absSeg.indexOf(2)) x[2]++;
			else if(max == -absSeg.indexOf(0)) x[3]++;
			else if(max == -absSeg.indexOf(1)) x[4]++;
			else if(max == -absSeg.indexOf(2)) x[5]++;
		}
		return x;
	}
	
	/**
	 * ある流線を有向線分に分割する
	 */
	static ArrayList<ArrayList<Double>> getSegment(Streamline sl){
		ArrayList<ArrayList<Double>> segments = new ArrayList<ArrayList<Double>>();
		int nums = sl.getNumVertex() -1;
		for(int i = 0; i < nums - 1; i++){
			double p1[] = sl.getPosition(i);
			double p2[] = sl.getPosition(i + 1);
			ArrayList<Double> seg = new ArrayList<Double>();
			for(int j = 0; j < 3; j++){
				seg.add(j, p2[j] - p1[j]);
			}
			segments.add(seg);
		}
		return segments;
	}

	/**
	 * すべての流線ペアについて差分を計算する
	 */
	static ArrayList<Double> calcStreamlineDifference(ArrayList<Streamline> list1, ArrayList<Streamline> list2){
		ArrayList<Double> diff = new ArrayList<Double>();

		for(int i = 0; i < list1.size(); i ++){
			Streamline sl1 = list1.get(i);
			Streamline sl2 = list1.get(i);
			double distance = calcPairDistance(sl1, sl2);
			diff.add(distance);
		}
		return diff;
	}
	
	
	/**
	 * ある流線ペア間の距離を計算する
	 */
	static double calcPairDistance(Streamline sl1, Streamline sl2){
		int numver = 0;
		double distance = 1.0e+30;
		int ver1 = sl1.getNumVertex();
		int ver2 = sl2.getNumVertex();
		if(ver1 <= 0 || ver2 <= 0) return distance;
		
		Streamline sla, slb;
		if(ver1 < ver2){
			sla = sl2; slb = sl1; numver = ver2;
		}else{
			sla = sl1; slb = sl2; numver = ver1;
		}
		for(int i = 0; i < numver; i++){
			double pos[] = sla.getPosition(i);
			distance += calcOnePosDistance(pos, slb);
		}
		distance /= numver;
		return distance;
		
	}
	
	/**
	 * ある格子点から、ペアとなる流線の最も近い格子点までの距離を計算する
	 */
	static double calcOnePosDistance(double pos[], Streamline sl) {
		double distance = 1.0e+30;
		if(sl.getNumVertex() <= 0) return distance;
		
		// for each point of the streamline
		for(int i = 0; i < sl.getNumVertex(); i++) {
			double pos2[] = sl.getPosition(i);
			double d = (pos[0] - pos2[0]) * (pos[0] - pos2[0])
					 + (pos[1] - pos2[1]) * (pos[1] - pos2[1])
					 + (pos[2] - pos2[2]) * (pos[2] - pos2[2]);
			if(distance > d)
				distance = d;
		}
		
		return Math.sqrt(distance);
	}

}