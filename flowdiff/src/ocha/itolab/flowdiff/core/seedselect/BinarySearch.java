package ocha.itolab.flowdiff.core.seedselect;
//import java.util.Comparator;
import java.util.LinkedList;
//import ocha.itolab.flowdiff.core.seedselect.ViewIndependentEvaluator.RankValue;
import ocha.itolab.flowdiff.core.seedselect.SingleEvaluator.RankValue;


public class BinarySearch{

	public static LinkedList<RankValue> binarySearch(LinkedList<RankValue> rankList, RankValue key){
		int pLeft = 0;
		int pRight = rankList.size() -1;
		
		if(rankList.size() == 0){
			rankList.add(key);
			return rankList;
		}
		else{
			do{
				int center = (pLeft + pRight) / 2;
				// 一致するものがあるか確認
				if(rankList.contains(key.value)){
					rankList.add(rankList.lastIndexOf(key) + 1, key);
					return rankList;
					// なければ二分探索
				}else if(rankList.get(center).value < key.value){
					pLeft = center + 1;
				}else{
					pRight = center - 1;
				}
			}while(pLeft < pRight);

			return rankList;
		}
	}
}