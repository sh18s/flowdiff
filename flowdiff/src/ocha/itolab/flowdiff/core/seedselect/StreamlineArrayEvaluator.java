package ocha.itolab.flowdiff.core.seedselect;

import ocha.itolab.flowdiff.core.data.*;
import ocha.itolab.flowdiff.core.streamline.*;
import java.util.*;

public class StreamlineArrayEvaluator {

	
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
	 * Calculate the similarity of the common-seed streamlines
	 */
	static double evaluateSimilarity(Streamline sl1, Streamline sl2) {
		double length = 0.0;
		
		int num1 = sl1.getNumVertex();
		int num2 = sl2.getNumVertex();
		if(num1 > num2)
			length += calcVertexGap(sl2, sl1);
		else
			length += calcVertexGap(sl1, sl2);
	
		return length;
	}

	
	/**
	 * Calculate the average gap between two streamlines
	 */
	static double calcVertexGap(Streamline sla, Streamline slb) {
		double gap = 0.0;
		if(sla.getNumVertex() <= 0) return 0.0;
		
		for(int i = 0; i < sla.getNumVertex(); i++) {
			double posa[] = sla.getPosition(i);
			double minlen = 1.0e+30;
			for(int j = 0; j < slb.getNumVertex(); j++) {
				double posb[] = slb.getPosition(j);
				double d = (posa[0] - posb[0]) * (posa[0] - posb[0])
						 + (posa[1] - posb[1]) * (posa[1] - posb[1])
						 + (posa[2] - posb[2]) * (posa[2] - posb[2]);
				if(minlen > d)
					minlen = d;
			}
			gap += Math.sqrt(minlen);
		}
		
		gap /= (double)sla.getNumVertex();
		return gap;
	}

	
	
	/**
	 * Evaluate distances to any other streamlines
	 */
	static double evaluateDistance(StreamlineArray slset) {
		double distance = 0.0;
		
		ArrayList<Streamline> list1 = slset.getAllList1();
		ArrayList<Streamline> list2 = slset.getAllList2();
		
		for(Streamline sl : list1) 
			distance += calcStreamlineDistance(sl, slset);
		for(Streamline sl : list2) 
			distance += calcStreamlineDistance(sl, slset);
		
		distance /= (double)(list1.size() + list2.size());
		return distance;
	}

	
	
	/**
	 * Calculate average minimum distance to any other streamlines
	 */
	static double calcStreamlineDistance(Streamline sl, StreamlineArray slset) {
		double distance = 0.0;
		if(sl.getNumVertex() < 2) return 0.0;
		
		// for each point of the streamline
		for(int i = 0; i < sl.getNumVertex(); i++) {
			double pos[] = sl.getPosition(i);
			
			// for each of any other streamlines
			ArrayList<Streamline> list1 = slset.getAllList1();
			ArrayList<Streamline> list2 = slset.getAllList2();
			
			double dmin = 1.0e+30;
			for(Streamline sl2 : list1) {
				if(sl == sl2) continue;
				double d = calcOnePosDistance(pos, sl2);
				if(dmin > d) dmin = d;
			}
			for(Streamline sl2 : list2) {
				if(sl == sl2) continue;
				double d = calcOnePosDistance(pos, sl2);
				if(dmin > d) dmin = d;
			}
			distance += dmin;
		}
		
		
		distance /= (double)sl.getNumVertex();
		return distance;
	}
	
	
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

