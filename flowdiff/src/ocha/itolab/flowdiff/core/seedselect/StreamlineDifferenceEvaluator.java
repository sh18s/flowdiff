package ocha.itolab.flowdiff.core.seedselect;

import ocha.itolab.flowdiff.core.data.*;
import ocha.itolab.flowdiff.core.streamline.*;

import java.util.*;

import ocha.itolab.flowdiff.core.seedselect.BinarySearch;

public class StreamlineDifferenceEvaluator {

	public static final int xLength = 1;
	public static final int xDirection = 6;
	
	/**
	 * 視点に依存しない評価値を算出し、ランキングする
	 */
	static LinkedList<RankValue> independentValue(StreamlineArray slset){
		LinkedList<RankValue> rankList = new LinkedList<RankValue>();
		
		ArrayList<Streamline> list1 = slset.getAllList1();
		ArrayList<Streamline> list2 = slset.getAllList2();
		
		for(int i = 0; i < list1.size(); i ++){
			Streamline sl1 = list1.get(i);
			Streamline sl2 = list1.get(i);
			double distance = calcPairDistance(sl1, sl2);
			PutRankValue pValue = new PutRankValue();
			RankValue value = new RankValue();
			value = pValue.putRankValue(i, distance);
//			RankValue value = pValue.putRankValue(i, distance);
//			RankValue value = PutRankValue.putRankValue(i, distance);
			rankList = BinarySearch.binarySearch(rankList, value);
		}
		
		
		return rankList;
	}

	/**
	 * 流線ペアの形状エントロピーを算出する
	 */
	
	/**
	 * 流線の形状エントロピーを算出する
	 */
	static double calcEntropy(double length, double[] p){
		double e = 0.0;
		for(double px: p){
			e -= px * Math.log10(px);
		}
		return e;
	}
	
	/**
	 * p(x)を算出する
	 */
	
	/**
	 * ある流線について、xを算出する
	 */
	static int[][] calcPx(ArrayList<Double> segments, int[] lx, int[] dx){
		double px = 0.0;
		int x[][];
		x = new int[1][6];
		for(int i = 0; i < 1; i++){
			for(int j = 0; j < 6; j++){
				x[i][j] = 0;
			}
		}
		for(double[] seg[] : segments){
			int counter = 0;
			x[lx[counter]][dx[counter]] ++ ;
		}
		return x;
	}
	
	/**
	 * ある流線について、長さを評価する
	 */
	static int[] calcPxLength(ArrayList<Double[]> segments){
		int x[] = {0, 0, 0, 0, 0, 0};
		return x;
	}
	
	/**
	 * ある流線について、	方向を評価する
	 */
	static int[] calcPxDirection(ArrayList<Double[]> segments){
		// 方向：6段階評価
		int x[] = {0, 0, 0, 0, 0, 0};
		
		for(double[] seg[]: segments){
			int counter = 0;
			double[] absSeg = new double[3];
			for(int i = 0; i < 3; i++){
				absSeg[i] = Math.abs(seg[i]);
			}
			double max = Math.max(absSeg[0], absSeg[1]);
			max = Math.max(max, absSeg[2]);
			if(max == absSeg[0]) x[0]++;
			else if(max == absSeg[1]) x[1]++;
			else if(max == absSeg[2]) x[2]++;
			else if(max == -absSeg[0]) x[3]++;
			else if(max == -absSeg[1]) x[4]++;
			else if(max == -absSeg[2]) x[5]++;
		}
		return x;
	}
	
	/**
	 * ある流線を有向線分に分割する
	 */
	static ArrayList<Double[]> getSegment(Streamline sl){
		ArrayList<Double[]> segments = new ArrayList<Double[]>();
		int nums = sl.getNumVertex() -1;
		for(int i = 0; i < nums - 1; i++){
			double p1[] = sl.getPosition(i);
			double p2[] = sl.getPosition(i + 1);
			double[] seg = new double[3];
//			Double[] seg = null;
			for(int j = 0; j < 3; j++){
				seg[j] = p2[j] - p1[j];
			}
			segments.add(seg);
		}
		return segments;
	}
	
	/**
	 * Evaluate the lengths of streamlines
	 */
	static double evaluateLength(Streamline sl1, Streamline sl2) {
		double length = 0.0;
		length += calcStreamlineLength(sl1);
		length += calcStreamlineLength(sl2);
		return length;
	}
	
	
	/**
	 * Calculate the length of streamline
	 */
	static double calcStreamlineLength(Streamline sl) {
		double length = 0.0;
		int nums = sl.getNumVertex() - 1;
		for(int i = 0; i < nums; i++) {
			double p1[] = sl.getPosition(i);
			double p2[] = sl.getPosition(i + 1);
			double d = (p1[0] - p2[0]) * (p1[0] - p2[0])
					 + (p1[1] - p2[1]) * (p1[1] - p2[1])
					 + (p1[2] - p2[2]) * (p1[2] - p2[2]);
			length += Math.sqrt(d);
		}
		return length;
	}
	
	/**
	 * 流線ペア番号と距離をまとめるクラス
	 */
	public class RankValue{
		public int num;
		public double distance;
	}
	
	public class PutRankValue{
		public RankValue putRankValue(int i, double dist){
			RankValue value = new RankValue();
			value.num = i;
			value.distance = dist;
			return value;
		}
	}

	/**
	 * すべての流線ペアについて差分を計算する
	 */
	static ArrayList<Double> calcStreamlineDifference(StreamlineArray slset){
		ArrayList<Double> diff = new ArrayList<Double>();
//		LinkedList<RankValue> rankList = new LinkedList<RankValue>();
		
		ArrayList<Streamline> list1 = slset.getAllList1();
		ArrayList<Streamline> list2 = slset.getAllList2();
		
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
		return distance /=numver;
		
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