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
public class MakeRandomEvaluationFile {
	public static int FILESIZE = 10000;
	
	static int REPEAT= 10000; // 計算対象の流線の本数(ほんとは全部)
	static String SCORE = "score";
	static String ENTROPY = "entropy";
	static String DIFF = "diff";
	static double alpha = 0.5d;
	static double beta = 1.0d - alpha;
	
	// Keep range of entropy and diff here
	double emin = 0d;
	double emax = 0d;
	double dmin = 0d;
	double dmax = 0d;
//	HashMap<String, Double> eRange = new HashMap<String, Double>(){
//		{put("max", 0d);}
//		{put("min", 0d);}
//	};;
//	HashMap<String, Double> dRange = eRange;
	
	static MakeAllEvaluationFile mef = new MakeAllEvaluationFile();
	
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
		
		// Get evaluations of pairs of streamlines which selected randomly
		for(int i = 0;i < REPEAT; i++){
			if(i % 100 == 0) System.out.println("i = " + i);
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
			
			// Make ArrayList to calculate score
			SeedInfo seedInfo = new SeedInfo();
			seedInfo.setSeedInfo(seed);
			infoList.add(seedInfo);
		}
		
		// Keep LinkedList rank of score, entropy, and diff here
		LinkedList<ScoreRank> sRankList = new LinkedList<ScoreRank>();
		LinkedList<ScoreRank> eRankList = new LinkedList<ScoreRank>();
		LinkedList<ScoreRank> dRankList = new LinkedList<ScoreRank>();
		int counter = 0;
		
		for(SeedInfo seedInfo: infoList){
			if(counter%1000 == 0) System.out.println("Normalizing counter is " + counter);
			// Normalize evaluations
			double nEntropy = (seedInfo.getEntropy() - emin) / (emax - emin);
			seedInfo.setEntropy(nEntropy);
			double nDiff = (seedInfo.getDiff() - dmin) / (dmax - dmin);
			seedInfo.setDiff(nDiff);
			// Calculate score
			double score = nEntropy * alpha + nDiff * beta;
			seedInfo.setScore(score);
			
			makeSeedInfoArray(seedInfoArray, seedInfo); // Make JSONArray
			
			// Rank seeds by score, entropy and diff
			int id = seedInfo.getId();
			BinarySearch.binarySearch(sRankList, score, id);
			BinarySearch.binarySearch(eRankList, nEntropy, id);
			BinarySearch.binarySearch(dRankList, nDiff, id);
			counter++;
		}
		// Make file
		counter = 0;
		for(int i = 0; i < sRankList.size(); i++){
			if(counter%1000 == 0) System.out.println("Making files counter is " + counter);
			makeRankArray(scoreRankArray, sRankList.get(i));
			makeRankArray(entropyRankArray, eRankList.get(i));
			makeRankArray(diffRankArray, dRankList.get(i));
			counter++;
		}
		
		String filename = Integer.toString(BestSetSelector.data1) + Integer.toString(BestSetSelector.data2);
		try{
			FileWriter fileWriter= new FileWriter(filename + "random_seeds.json", false);
			fileWriter.write(seedInfoArray.toString());
			fileWriter.close();
			System.out.println("Seeds File is done.");
		}catch(IOException e){
			System.out.println("Seeds file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter(filename + "random_score.json", false);
			fileWriter.write(scoreRankArray.toString());
			fileWriter.close();
			System.out.println("Score File is done.");
		}catch(IOException e){
			System.out.println("Score file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter(filename + "random_entropy.json", false);
			fileWriter.write(entropyRankArray.toString());
			fileWriter.close();
			System.out.println("Entropy File is done.");
		}catch(IOException e){
			System.out.println("Entropy file failed...");
		}
		try{
			FileWriter fileWriter= new FileWriter(filename + "random_diff.json", false);
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
	 * Get evaluations of a pair of streamlines
	 */
	public void getEvaluation(Seed seed){
		// Get evaluation
		Streamline streamline1 = seed.sl1;
		Streamline streamline2 = seed.sl2;
		
		HashMap<String, Double> evaluation = EvaluationCalculator.calcEvaluation(streamline1, streamline2);
		double e = evaluation.get(ENTROPY);
		double d = evaluation.get(DIFF);
		seed.entropy = e;
		seed.diff = d;
		// Keep max and min value
		if(e < emin) emin = e;
		else if(emax < e) emax = e;
		if(d < dmin) dmin = d;
		else if(dmax < d) dmax = d;
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