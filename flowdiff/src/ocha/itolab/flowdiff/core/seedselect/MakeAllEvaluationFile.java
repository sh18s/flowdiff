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
		JSONArray scoreRankArray = new JSONArray();
		JSONArray entropyRankArray = new JSONArray();
		JSONArray diffRankArray = new JSONArray();
		
		// Keep all SeedInfo here (sorted by id number)
		ArrayList<SeedInfo> infoList = new ArrayList<SeedInfo>();
		
		int[] total = grid1.getNumElement();
		int seedId = 0;
		
		// Get evaluations of all pairs of streamlines
		for(int i = 0; i < total[0]; i++){
			System.out.println("i = " + i);
			for(int j = 0; j < total[1]; j++){
				for(int k = 0; k < total[2]; k++){
					int[] eid = new int[3];
					eid[0] = i;
					eid[1] = j;
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
		LinkedList<ScoreRank> sRankList = new LinkedList<ScoreRank>();
		LinkedList<ScoreRank> eRankList = new LinkedList<ScoreRank>();
		LinkedList<ScoreRank> dRankList = new LinkedList<ScoreRank>();
		BinarySearch bs = new BinarySearch();
		
		int counter = 0;
		for(SeedInfo seedInfo: infoList){
			if(counter%1000 == 0) System.out.println("Normalizing counter is " + counter);
			// Normalize evaluations
			double nEntropy = normalize(seedInfo.getEntropy(), eRange);
			seedInfo.setEntropy(nEntropy);
			double nDiff = normalize(seedInfo.getDiff(), dRange);
			seedInfo.setDiff(nDiff);

			seedInfo.setScore(nEntropy * alpha + nDiff * beta); // Calculate score
			
			makeSeedInfoArray(seedInfoArray, seedInfo); // Make JSONArray of SeedInfo
//			System.out.println("eid = " + seedInfo.getEid()[0] + "," + seedInfo.getEid()[1] + ", " + seedInfo.getEid()[2]);
			
			// Rank seeds by score, entropy and diff
			int id = seedInfo.getId();
			bs.scoreRankBinarySearch(sRankList, seedInfo.getScore(), id);
			bs.scoreRankBinarySearch(eRankList, seedInfo.getEntropy(), id);
			bs.scoreRankBinarySearch(dRankList, seedInfo.getDiff(), id);
			counter++;
		}
		// Make file
		counter = 0;
		for(int i = 0; i < sRankList.size(); i++){
			if(counter%1000 == 0) System.out.println("Making files counter is " + counter);
			ScoreRank scoreRank = sRankList.get(i);
			makeRankArray(scoreRankArray, scoreRank);
			scoreRank = eRankList.get(i);
			makeRankArray(entropyRankArray, scoreRank);
			scoreRank = dRankList.get(i);
			makeRankArray(diffRankArray, scoreRank);
			counter++;
		}
		
		String filename = Integer.toString(BestSetSelector.data1) + Integer.toString(BestSetSelector.data2);
		try{
			FileWriter fileWriter= new FileWriter(filename + "_seeds.json", false);
			fileWriter.write(seedInfoArray.toString());
			fileWriter.close();
			System.out.println("Seeds File is done.");
		}catch(IOException e){
			System.out.println("Seeds file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter(filename + "_score.json", false);
			fileWriter.write(scoreRankArray.toString());
			fileWriter.close();
			System.out.println("Score File is done.");
		}catch(IOException e){
			System.out.println("Score file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter(filename + "entropy.json", false);
			fileWriter.write(entropyRankArray.toString());
			fileWriter.close();
			System.out.println("Entropy File is done.");
		}catch(IOException e){
			System.out.println("Entropy file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter(filename + "diff.json", false);
			fileWriter.write(diffRankArray.toString());
			fileWriter.close();
			System.out.println("Diff File is done.");
		}catch(IOException e){
			System.out.println("Diff file failed...");
		}
	}
	
	/** 
	 * Make rank JSONArray of score, entropy ,diff
	 */
	public void makeRankArray(JSONArray rankArray, ScoreRank scoreRank){	
		HashMap<String, Object> rankHash = new HashMap<String, Object>();
		rankHash.put("id", scoreRank.getId());
		rankHash.put(SCORE, scoreRank.getScore());
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
		infoHash.put("id", seedInfo.getId());
		
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