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
public class MakeAllEvaluationFile {
	static int REPEAT= 100; // 計算対象の流線の本数(ほんとは全部)
	static String SCORE = "score";
	static String ENTROPY = "entropy";
	static String DIFF = "diff";
	static double alpha = 0.5d;
	static double beta = 1.0d - alpha;
	
	// Keep seeds here
	ArrayList<Seed> seedlist = new ArrayList<Seed>();
	public static JSONArray seedArray= new JSONArray();
	// Keep rank of score, entropy and diff here
	public static JSONArray scoreRank  = new JSONArray();
	public static JSONArray entropyRank  = new JSONArray();
	public static JSONArray diffRank  = new JSONArray();
	
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
		for(int i = 0; i < REPEAT; i++){
			for(int j = 0; j < REPEAT; j++){
				for(int k = 0; k < REPEAT; k++){
					if(i%10 == 0 && j%10 == 0 && k%10 == 0) System.out.println("i = " + i + ", j = " + j + ", k = " + k);
					// Generate seed
					Seed seed = new Seed();
					seed.id = i;
					seed.eid = setSeedRandomly(grid1);
					// Generate Streamline
					seed.sl1 = new Streamline();
					seed.sl2 = new Streamline();
					StreamlineGenerator.generate(grid1, seed.sl1, seed.eid, null);
					StreamlineGenerator.generate(grid2, seed.sl2, seed.eid, null);
					
					getEvaluation(seed); // Get evaluation
					seedlist.add(seed);
				}
			}
		}
		// Normalize evaluations and calculate score
		for(Seed seed: seedlist){
			double nEntropy = normalize(seed.entropy, eRange);
			seed.entropy = nEntropy;
			double nDiff = normalize(seed.diff, dRange);
			seed.diff = nDiff;
			
			seed.score = nEntropy * alpha + nDiff * beta;
			
			// Make Array to keep all information of the seeds
			HashMap<String, Object> seedInfo = makeSeedInfoHash(seed);
			seedArray.put(seedInfo);
		}
		
		// Rank seeds by score, entropy and diff
		LinkedList<Seed> sRankList = new LinkedList<Seed>();
		LinkedList<Seed> eRankList = new LinkedList<Seed>();
		LinkedList<Seed> dRankList = new LinkedList<Seed>();
		for(Seed seed: seedlist){
			BinarySearch.binarySearch(sRankList, seed, SCORE);
			BinarySearch.binarySearch(eRankList, seed, ENTROPY);
			BinarySearch.binarySearch(dRankList, seed, DIFF);
		}
		// Make file
		for(int i = 0; i < sRankList.size(); i++){
			makeRankArray(scoreRank, sRankList, i);
			makeRankArray(entropyRank, eRankList, i);
			makeRankArray(diffRank, dRankList, i);
		}
		try{
			FileWriter fileWriter= new FileWriter("seeds.json", false);
			fileWriter.write(seedArray.toString());
			fileWriter.close();
			System.out.println("Seeds File is done.");
		}catch(IOException e){
			System.out.println("Seeds file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter("score.json", false);
			fileWriter.write(scoreRank.toString());
			fileWriter.close();
			System.out.println("Score File is done.");
		}catch(IOException e){
			System.out.println("Score file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter("entropy.json", false);
			fileWriter.write(entropyRank.toString());
			fileWriter.close();
			System.out.println("Entropy File is done.");
		}catch(IOException e){
			System.out.println("Entropy file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter("diff.json", false);
			fileWriter.write(diffRank.toString());
			fileWriter.close();
			System.out.println("Diff File is done.");
		}catch(IOException e){
			System.out.println("Diff file failed...");
		}
	}
	
	
	/**
	 * Make HashMap of the seed
	 */
	public HashMap<String, Object> makeSeedInfoHash(Seed seed){
		HashMap<String, Object> seedInfo = new HashMap<String, Object>();
		seedInfo.put(SCORE, (Object)seed.score);
		seedInfo.put("eid", (Object)seed.eid);
		seedInfo.put("entropy", seed.entropy);
		seedInfo.put("diff", seed.diff);
		
		return seedInfo;
	}
	
	/** 
	 * Make rank hash of score and so on
	 */
	public void makeRankArray(JSONArray rankArray, LinkedList<Seed> rankList, int index){
		HashMap<String, Double> rankHash = new HashMap<String, Double>();
		Seed seed = rankList.get(index);
		rankHash.put("id", (double)seed.id);
		rankHash.put(SCORE, seed.score);
		rankArray.put(rankHash);
	}
	
	/**
	 * Get evaluations of a pair of streamlines
	 */
	public void getEvaluation(Seed seed){
		// Get evaluation
		Streamline streamline1 = seed.sl1;
		Streamline streamline2 = seed.sl2;
		HashMap<String, Double> evaluation = EvaluationCalculator.calcEvaluation(streamline1, streamline2);
		seed.entropy = evaluation.get(ENTROPY);
		seed.diff = evaluation.get(DIFF);
		
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