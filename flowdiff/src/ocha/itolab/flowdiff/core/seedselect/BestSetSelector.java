package ocha.itolab.flowdiff.core.seedselect;

import java.io.*;
import java.util.*;

import org.json.JSONException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import ocha.itolab.flowdiff.core.streamline.*;
import ocha.itolab.flowdiff.core.data.*;

// Read on ViewingPanel

public class BestSetSelector {
	static int REPEAT1 = 10000, REPEAT2 = 20;
	static int NUMCANDIDATE = 100;
	static int selectCounter = 0;
	
	static String SCORE = "score", ENTROPY = "entropy", DIFF = "diff";	
	static String seedPATH = "../bin/seeds.json";
	static String scorePATH = "../bin/score.json";
	
	/**
	 * Select the best set of streamlines using the file
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException
	 */
	public static StreamlineArray selectRandomly(Grid grid1, Grid grid2) throws JSONException, JsonParseException, JsonMappingException, IOException {
		StreamlineArray bestset = null;
		ArrayList<Seed> meaningList = new ArrayList<Seed>();
		
		// TODO: Confirm which file exists or not
		File seedFile = new File(seedPATH);
		File scoreFile = new File(scorePATH);
		// if there is no file, make it.
		if(! seedFile.exists() || ! scoreFile.exists()){
			MakeEvaluationFile mef = new MakeEvaluationFile();
			mef.makeEvaluationFile(grid1, grid2);
		}else{
			System.out.println("Files exist.");
			MakeAllEvaluationFile maef = new MakeAllEvaluationFile();
			maef.makeEvaluationFile(grid1, grid2);
		}
		
		// Read File
		// Parse JSON files
		List<SeedInfo> infoList = new ObjectMapper().readValue(seedFile, new TypeReference<List<SeedInfo>>(){});
		List<ScoreRank> sRankList = new ObjectMapper().readValue(scoreFile, new TypeReference<List<ScoreRank>>(){});
//		System.out.println("scoreRank = " + sRankList.get(0).getScore());
		
		// Generate seed and make meaning list
//		for(ScoreRank scoreRank: sRankList){
		for(int i = 0; i < NUMCANDIDATE; i++){
			Seed seed = new Seed();
			// Set information of the "i"th place seed by using read files
			int id = (int)sRankList.get(i).getId();
			seed.id =  id;
			getSeedInfo(infoList.get(seed.id), seed);
			// Generate a pair of streamlines
			seed.sl1 = new Streamline();
			seed.sl2 = new Streamline();
			StreamlineGenerator.generate(grid1, seed.sl1, seed.eid, null);
			StreamlineGenerator.generate(grid2, seed.sl2, seed.eid, null);
			meaningList.add(seed); // add this seed to meaningList
		}
		
		// TODO: 視点に依存しない評価値で足切り
		
		// Decide best set using view dependent evaluation
		bestset = ViewDependentEvaluator.select(meaningList);
			
		// 視点に依存した評価値を無視して結果出力
//		ignoreViewDependentEvaluation(meaningList, bestset);
			
		// ランダムにNUMCANDIDATE本選んで可視化する
//		bestset = randomSelect(seedlist);
		
		// TODO: 散布図描画
			
		MakeJsonFile mjf = new MakeJsonFile();
		mjf.makeJsonFile(bestset);
        return bestset;
	}
	
	/**
	 * 視点に依存した評価値を無視して結果出力
	 */
	static void ignoreViewDependentEvaluation(ArrayList<Seed> meaningList, StreamlineArray bestset){
		for(int i = 0; i < REPEAT2; i++){
			Seed seed = meaningList.get(i);
			bestset.addList(seed.sl1, seed.sl2, seed.eid);
		}
	}
	
	/** 
	 * ランダムにNUMCANDIDATE本選んで可視化する
	 */
	static StreamlineArray randomSelect(ArrayList<Seed> seedlist){
		ArrayList<Seed> randomList = new ArrayList<Seed>();	
		for(int i = 0; i < NUMCANDIDATE; i++){
			int random = (int)(Math.random()*REPEAT1);
			Seed seed = seedlist.get(random);
				randomList.add(seed);
			}
		return ViewDependentEvaluator.select(randomList);
	}
	
	/**
	 * Parse JSON file of seeds to Seed class
	 */
	public static void getSeedInfo(SeedInfo seedInfo, Seed seed) throws JsonParseException, JsonMappingException, IOException{
		seed.score = seedInfo.getScore();
		seed.entropy = seedInfo.getEntropy();
		seed.diff = seedInfo.getDiff();
		seed.eid = seedInfo.getEid();
	}
}
