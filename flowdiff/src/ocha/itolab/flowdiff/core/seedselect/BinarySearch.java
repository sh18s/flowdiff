package ocha.itolab.flowdiff.core.seedselect;

import java.util.*;

import org.json.JSONArray;
//import ocha.itolab.flowdiff.core.seedselect.Seed;

public class BinarySearch{
	static int MSIZE = 10000;

	public void binarySearch(LinkedList<Seed> rankList, Seed key, String string){
		int pLeft = 0;
		int pRight = rankList.size() -1;
//		System.out.println("key.socre = " + keyValue);
		
		double keyValue = 0;
		if(string == "score") keyValue = key.score;
		else if(string == "entropy") keyValue = key.entropy;
		else if(string == "diff") keyValue = key.diff;
		else return;

		if(rankList.size() == 0){
			rankList.add(key);
		}
		else{
			do{
				int center = (pLeft + pRight) / 2;
				// 一致するものがあるか確認
				if(rankList.contains(keyValue)){
					rankList.add(rankList.lastIndexOf(key) + 1, key);
					break;
					// なければ二分探索	
				}else if(rankList.get(center).score < keyValue){
					pLeft = center + 1;
				}else{
					pRight = center - 1;
				}
			}while(pLeft < pRight);
			rankList.add(pLeft, key);
		}
	}
	
	public static void binarySearch(LinkedList<ScoreRank> rankList, double keyValue, int id){
		int size = rankList.size();
		int pLeft = 0;
		int pRight = size -1;
		int findFlag = 0;
		
		// Make Key whose type is ScorerRank
		ScoreRank scoreRank = new ScoreRank();
		scoreRank.setScore(keyValue);
		scoreRank.setId(id);
		
		if(size == 0){
			rankList.add(scoreRank);
		}else{
			do{
				int center = (pLeft + pRight) / 2;
				double centerScore = rankList.get(center).getScore();
				// 中央値と一致するか確認
				if(keyValue == centerScore){
					findFlag = center + 1;
					break;
				}else if(keyValue < centerScore){
					pLeft = center + 1;
				}else{
					pRight = center - 1;
				}
			}while(pLeft <= pRight);
			
			if(findFlag == 0) rankList.add(pLeft, scoreRank);
			else rankList.add(findFlag, scoreRank);
		}
	}

	public static void keepSizeBinarySearch(LinkedList<ScoreRank> rankList, double keyValue, int id){
		int size = rankList.size();
		int pLeft = 0;
		int pRight = size -1;
		int findFlag = 0;
		
		// Make Key whose type is ScorerRank
		ScoreRank scoreRank = new ScoreRank();
		scoreRank.setScore(keyValue);
		scoreRank.setId(id);
		
		if(size == 0){
			rankList.add(scoreRank);
		}else{
			do{
				int center = (pLeft + pRight) / 2;
				double centerScore = rankList.get(center).getScore();
				// 中央値と一致するか確認
				if(keyValue == centerScore){
					findFlag = center + 1;
					break;
				}else if(keyValue < centerScore){
					pLeft = center + 1;
				}else{
					pRight = center - 1;
				}
			}while(pLeft <= pRight);
			
			// Confirm size
			if(rankList.getLast().getScore() < keyValue){
				if(MSIZE <= size){
					rankList.remove(size - 1);
				}
				rankList.add(pLeft, scoreRank);
			}
			
			if(findFlag != 0) rankList.add(findFlag, scoreRank);
		}
	}
}