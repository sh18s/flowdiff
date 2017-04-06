package ocha.itolab.flowdiff.core.seedselect;

import org.json.JSONException;
import org.json.JSONArray;

import ocha.itolab.flowdiff.core.streamline.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import ocha.itolab.flowdiff.core.data.*;

/**
 * Make JSON File to keeps scores of pair of streamlines
 */
public class MakeEvaluationFile {
	static int ALLPAIR = 10000; // 計算対象の流線の本数(ほんとは全部)
	static String ENTROPY = "entropy";
	static String DIFF = "diff";
	static double alpha = 0.5d;
	static double beta = 1.0d - alpha;
	
	// Keep normalized evaluations and scores here
	public static JSONArray nEvArray = new JSONArray();
	public static JSONArray scoreArray = new JSONArray();
	// Keep entropy and diff here
	ArrayList<HashMap<String, Double>> evArray = new ArrayList<HashMap<String, Double>>();
	// Keep range of entropy and diff here
	static HashMap<String, Double> eRange = new HashMap<String, Double>(){
		{put("max", 0d);}
		{put("min", 0d);}
	};;
	static HashMap<String, Double> dRange = eRange;
	
	/**
	 * Make file to keep normalized evaluation values by using row entropy and diff
	 */
	public void makeEvaluationFile(Grid grid1, Grid grid2) throws JSONException{
		// Get evaluations of all pairs of streamlines
		for(int i = 0;i < ALLPAIR; i++){
			// Generate seed
			Seed seed = new Seed();
			seed.id = i;
			seed.eid = setSeedRandomly(grid1);
			// Generate Streamline
			seed.sl1 = new Streamline();
			seed.sl2 = new Streamline();
			StreamlineGenerator.generate(grid1, seed.sl1, seed.eid, null);
			StreamlineGenerator.generate(grid2, seed.sl2, seed.eid, null);
			
			getEvaluation(seed.sl1, seed.sl2); // Get evaluation
		}
		// Normalize evaluations
		for(HashMap<String, Double> evaluation: evArray){
			double nEntropy = normalize(evaluation.get(ENTROPY), eRange);
			double nDiff = normalize(evaluation.get(DIFF), dRange);
			HashMap<String, Double> nEvMap = new HashMap<String, Double>();
			nEvMap.put(ENTROPY, nEntropy);
			nEvMap.put(DIFF, nDiff);
			nEvArray.put(nEvMap);
			scoreArray.put(nEntropy * alpha + nDiff * beta);
		}
		
		// Make File
		try{
			FileWriter fileWriter= new FileWriter("evaluation.txt", false);
			fileWriter.write(nEvArray.toString());
			fileWriter.close();
			System.out.println("Evaluation File is done.");
		}catch(IOException e){
			System.out.println("Evaluation file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter("score.txt", false);
			fileWriter.write(scoreArray.toString());
			fileWriter.close();
			System.out.println("Score file is done.");
		}catch(IOException e){
			System.out.println("Score file failed...");
		}
	}
	
	
	/**
	 * Calculate Score
	 */
	public double calcScore(double entropy, double diff){ 
		double score = 0;
		return score;
	}
	
	/**
	 * Get evaluations of a pair of streamlines
	 */
	public void getEvaluation(Streamline streamline1, Streamline streamline2){
		// Get evaluation
		HashMap<String, Double> evaluation = CalculateEvaluation.calcEvaluation(streamline1, streamline2);
		evArray.add(evaluation);
		
		// Keep max and min value
		compareMinMax(evaluation, eRange);
		compareMinMax(evaluation, dRange);
	}
	
	/**
	 * Normalize a param
	 */
	public double normalize(double value, HashMap<String, Double> range){
		double max = range.get("max"), min = range.get("min");
		double nv = (value - min) / (max - min);
		return nv;
	}
	
	/**
	 * Compare value to keep max and min
	 */
	void compareMinMax(HashMap<String, Double> evaluation, HashMap<String, Double> extreme){
		for(Double value: evaluation.values()){
			if(extreme.get("max") < value) extreme.put("max", value);
			if(extreme.get("min") > value) extreme.put("min", value);
		}
	}
	
	/**
	 * Set seed randomly
	 */
	static int[] setSeedRandomly(Grid grid) {
		int seedid[] = new int[3];
		int nume[] = grid.getNumElement();
		
		// for each axis
		for(int j = 0; j < 3; j++) {
			int id = (int)(Math.random() * (double)nume[j]);
			if(id < 0) id = 0;
			if(id >= nume[j]) id = nume[j] - 1;
			seedid[j] = id;
		}
		return seedid;
	}
}