package ocha.itolab.flowdiff.core.seedselect;

import java.util.*;
//import ocha.itolab.flowdiff.core.seedselect.Seed;

public class BinarySearch{

	public static void binarySearch(LinkedList<Seed> rankList, Seed key, String string){
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
	
	public static void seedInfoBinarySearch(LinkedList<SeedInfo> rankList, SeedInfo key, double keyValue){
		int pLeft = 0;
		int pRight = rankList.size() -1;
//		System.out.println("key.socre = " + keyValue);
		
//		double keyValue = 0;
//		if(string == "score") keyValue = key.getScore();
//		else if(string == "entropy") keyValue = key.getEntropy();
//		else if(string == "diff") keyValue = key.getDiff();
//		else return;

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
				}else if(rankList.get(center).getScore() < keyValue){
					pLeft = center + 1;
				}else{
					pRight = center - 1;
				}
			}while(pLeft < pRight);
			rankList.add(pLeft, key);
		}
	}
}