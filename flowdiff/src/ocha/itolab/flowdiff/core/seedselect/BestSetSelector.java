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
	public static int selectCounter = 0;
	
	static String SCORE = "score", ENTROPY = "entropy", DIFF = "diff";	
	
	static final String path = "../bin/";
	public static int data1 = 20;
	public static int data2 = 27;
	static final String seedFilename = "_seeds.json";
	static final String scoreFilename = "_score.json";

	// Keep data from file here 
	public static List<SeedInfo> infoList = new ArrayList<SeedInfo>();
	public static List<ScoreRank> sRankList = new ArrayList<ScoreRank>();
	// Keep meaning seeds here
	public static ArrayList<Seed> meaningList = new ArrayList<Seed>();
	
	/**
	 * Select the best set of streamlines using the file
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException
	 */
	public static StreamlineArray selectRandomly(Grid grid1, Grid grid2) throws JSONException, JsonParseException, JsonMappingException, IOException {
		StreamlineArray bestset = null;
		
		if(selectCounter == 0){
			String usedData = null;
			if(data1 > data2) usedData = Integer.toString(data2) + Integer.toString(data1);
			else if(data2 > data1) usedData = Integer.toString(data1) + Integer.toString(data2);
			//TODO: data1 == data2ならエラーに
			
			File seedFile = new File(path + usedData + seedFilename);
			File scoreFile = new File(path + usedData + scoreFilename);
			System.out.println("filename = " + path + usedData + seedFilename);

			// if there is no file, make it.
			if(! seedFile.exists() || ! scoreFile.exists()){
				//			MakeRandomEvaluationFile mef = new MakeRandomEvaluationFile();
				//			mef.makeEvaluationFile(grid1, grid2);
				MakeAllEvaluationFile maef = new MakeAllEvaluationFile();
				maef.makeEvaluationFile(grid1, grid2);
			}else{
				System.out.println("Files exist.");
			}

			// Read File
			// Parse JSON files
			System.out.print("Parsing files...");
			infoList = new ObjectMapper().readValue(seedFile, new TypeReference<List<SeedInfo>>(){});
			sRankList = new ObjectMapper().readValue(scoreFile, new TypeReference<List<ScoreRank>>(){});
			System.out.println("Done.");
			
			System.out.print("Making meaning list...");
			for(int i = 0; i < sRankList.size(); i++){
				// Set information of the "i"th place seed by using read files
				ScoreRank scoreRank = sRankList.get(i);
				Seed seed = new Seed();
				seed.id = scoreRank.getId();
				SeedInfo seedInfo = infoList.get(seed.id); // Get seed information from infoList

				seedInfo.getSeedInfo(seed); // Put information in seed

				// Generate a pair of streamlines
				seed.sl1 = new Streamline();
				seed.sl2 = new Streamline();
				StreamlineGenerator.generate(grid1, seed.sl1, seed.eid, null);
				StreamlineGenerator.generate(grid2, seed.sl2, seed.eid, null);
				meaningList.add(seed); // add this seed to meaningList
			}
			System.out.println("Done.");

		}
		
		// Decide best set using view dependent evaluation
		System.out.println("Calculating view-independent evaluation...");
		bestset = ViewDependentEvaluator.select(meaningList);
			
		// Ignore view-dependent evaluation
//		ignoreViewDependentEvaluation(meaningList, bestset);
			
		// ランダムにNUMCANDIDATE本選んで可視化する
//		bestset = randomSelect(seedlist);
		
		// TODO: 散布図描画
		PlotPanel plotPanel = new PlotPanel();
		plotPanel.createData();
		
		// Make JSON file to observe this result in VR
		MakeJsonFile mjf = new MakeJsonFile();
		mjf.makeJsonFile(bestset);
		
		selectCounter++;
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
}
