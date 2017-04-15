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
	static String SCORE = "score";
	static String ENTROPY = "entropy";
	static String DIFF = "diff";
	static double alpha = 0.5d;
	static double beta = 1.0d - alpha;
	
	// Keep range of entropy and diff here
	HashMap<String, Double> eRange = new HashMap<String, Double>(){
		{put("max", 0d);}
		{put("min", 0d);}
	};;
	HashMap<String, Double> dRange = eRange;
	
	/**
	 * Make file to keep normalized evaluation values by using row entropy and diff
	 */
	public void makeEvaluationFile(Grid grid1, Grid grid2) throws JSONException{
		// Keep seedInfo here to make JSON file
		JSONArray seedInfoArray = new JSONArray();
		// Keep rank of score, entropy and diff here
		JSONArray scoreRank = new JSONArray();
		JSONArray entropyRank = new JSONArray();
		JSONArray diffRank = new JSONArray();
		
		// Keep all SeedInfo here (sorted by id number)
		ArrayList<SeedInfo> infoList = new ArrayList<SeedInfo>();
		
		int[] total = grid1.getNumElement();
		int[] eid = new int[3];
		int seedId = 0;
		
		// Get evaluations of all pairs of streamlines
		for(int i = 0; i < total[0]; i++){
			eid[0] = i;
			System.out.println("i = " + i);
			for(int j = 0; j < total[1]; j++){
				eid[1] = j;
				for(int k = 0; k < total[2]; k++){
					eid[2] = k;
					// Generate seed
					Seed seed = new Seed();
					seed.id = seedId;
					seed.eid = eid;
					// Generate Streamline
					seed.sl1 = new Streamline();
					seed.sl2 = new Streamline();
					StreamlineGenerator.generate(grid1, seed.sl1, seed.eid, null);
					StreamlineGenerator.generate(grid2, seed.sl2, seed.eid, null);
					
					getEvaluation(seed); // Get evaluation
					
					// Make ArrayList to calculate score
					SeedInfo seedInfo = new SeedInfo();
					seedInfo.setSeedInfo(seed);
					infoList.add(seedInfo);
					
					seedId++;
				}
			}
		}
		// Keep LinkedList rank of score, entropy, and diff here
		LinkedList<SeedInfo> sRankList = new LinkedList<SeedInfo>();
		LinkedList<SeedInfo> eRankList = new LinkedList<SeedInfo>();
		LinkedList<SeedInfo> dRankList = new LinkedList<SeedInfo>();
		int counter = 0;
		for(SeedInfo seedInfo: infoList){
			if(counter%100 == 0) System.out.println("Normalizing counter is " + counter);
			// Normalize evaluations
			double nEntropy = normalize(seedInfo.getEntropy(), eRange);
			seedInfo.setEntropy(nEntropy);
			double nDiff = normalize(seedInfo.getDiff(), dRange);
			seedInfo.setDiff(nDiff);

			seedInfo.setScore(nEntropy * alpha + nDiff * beta); // Calculate score
			
			makeSeedInfoArray(seedInfoArray, seedInfo); // Make JSONArray of SeedInfo
			
			// Rank seeds by score, entropy and diff
			BinarySearch.keepSizeBinarySearch(sRankList, seedInfo, seedInfo.getScore());
			BinarySearch.keepSizeBinarySearch(eRankList, seedInfo, seedInfo.getEntropy());
			BinarySearch.keepSizeBinarySearch(dRankList, seedInfo, seedInfo.getDiff());
			counter++;
		}
		// Make file
		counter = 0;
		for(int i = 0; i < sRankList.size(); i++){
			if(counter%100 == 0) System.out.println("Making files counter is " + counter);
			SeedInfo seedInfo = sRankList.get(i);
			makeRankArray(scoreRank, seedInfo, seedInfo.getScore());
			seedInfo = eRankList.get(i);
			makeRankArray(entropyRank, seedInfo, seedInfo.getEntropy());
			seedInfo = dRankList.get(i);
			makeRankArray(diffRank, seedInfo, seedInfo.getDiff());
			counter++;
		}
		try{
			FileWriter fileWriter= new FileWriter("all_seeds.json", false);
			fileWriter.write(seedInfoArray.toString());
			fileWriter.close();
			System.out.println("Seeds File is done.");
		}catch(IOException e){
			System.out.println("Seeds file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter("all_score.json", false);
			fileWriter.write(scoreRank.toString());
			fileWriter.close();
			System.out.println("Score File is done.");
		}catch(IOException e){
			System.out.println("Score file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter("all_entropy.json", false);
			fileWriter.write(entropyRank.toString());
			fileWriter.close();
			System.out.println("Entropy File is done.");
		}catch(IOException e){
			System.out.println("Entropy file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter("all_diff.json", false);
			fileWriter.write(diffRank.toString());
			fileWriter.close();
			System.out.println("Diff File is done.");
		}catch(IOException e){
			System.out.println("Diff file failed...");
		}
	}
	
	/** 
	 * Make rank JSONArray of score, entropy ,diff
	 */
	public void makeRankArray(JSONArray rankArray, SeedInfo seedInfo, double score){	
		HashMap<String, Object> rankHash = new HashMap<String, Object>();
		rankHash.put("eid", seedInfo.getEid());
		rankHash.put(SCORE, score);
		rankArray.put(rankHash);
	}
	
	/**
	 * Make seedInfo JSONArray
	 */
	public void makeSeedInfoArray(JSONArray seedInfoArray, SeedInfo seedInfo){
		HashMap<String, Object> infoHash = new HashMap<String, Object>();
		infoHash.put("eid", seedInfo.getEid());
		infoHash.put(SCORE, seedInfo.getScore());
		infoHash.put(ENTROPY, seedInfo.getEntropy());
		infoHash.put(DIFF, seedInfo.getDiff());
		
		seedInfoArray.put(infoHash);
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
	 * Compare value to keep max and min
	 */
	void compareMinMax(HashMap<String, Double> evaluation, HashMap<String, Double> extreme){
		for(Double value: evaluation.values()){
			if(extreme.get("max") < value) extreme.put("max", value);
			if(extreme.get("min") > value) extreme.put("min", value);
		}
	}
}