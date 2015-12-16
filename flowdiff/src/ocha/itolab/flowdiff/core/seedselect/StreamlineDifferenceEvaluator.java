package ocha.itolab.flowdiff.core.seedselect;

import ocha.itolab.flowdiff.core.data.*;
import ocha.itolab.flowdiff.core.streamline.*;

import java.util.*;

import ocha.itolab.flowdiff.core.seedselect.BinarySearch;

public class StreamlineDifferenceEvaluator {

	
	public static double evaluate1(Grid grid1, Grid grid2, 
			Streamline sl1, Streamline sl2) {
		double score = 0.0, score1, score2;
		double c1 = 1.0, c2 = 1000.0;
		
		
		// evaluate the lengths of streamlines
		score1 = evaluateLength(sl1, sl2);
		
		// evaluate the similarity between the common-seed streamlines
		score2 = evaluateSimilarity(sl1, sl2);
		
		
		// calculate the score
		score = c1 * score1 + c2 * score2;
		//System.out.println("     evaluate1: SCORE=" + score + " s1=" + score1 + " s2=" + score2);
		return score;
	}
	
	
	public static double evaluate2(Grid grid1, Grid grid2, StreamlineArray slset) {
		// evaluate the distances among all the streamlines
		double score = evaluateDistance(slset);
		return score;
	
	}

	/**
	 * 流線ペアの形状エントロピーを算出し、ランキングする
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
	 * ある流線について、p(x)を計算する
	 */
	static double calcPx(Streamline sl){
		double px = 0.0;
		return px;
	}
	
	/**
	 * ある流線を線分に分割する
	 */
	static ArrayList<Double[]> getSegment(Streamline sl){
		ArrayList<Double[]> segments = new ArrayList<Double[]>();
		int nums = sl.getNumVertex() -1;
		for(int i = 0; i < nums; i++){
			double p1[] = sl.getPosition(i);
			double p2[] = sl.getPosition(i + 1);
			Double seg[] = null;
			for(int j = 0; j < 3; j++){
				seg[j] = p1[j] - p2[j];
			}
			segments.add(seg[]);
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
	 * すべての流線ペアについて差分を計算し、その上位N本を決定する
	 */
	static LinkedList<RankValue> rankStreamlineDistance2(StreamlineArray slset){
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