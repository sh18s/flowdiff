package ocha.itolab.flowdiff.core.seedselect;
//import java.util.Comparator;
import java.util.LinkedList;


public class BinarySearch{

	public static LinkedList<Double> binarySearch(LinkedList<Double> rankList, double key){
		int pLeft = 0;
		int pRight = rankList.size() -1;
		
		do{
			int center = (pLeft + pRight) / 2;
			// 一致するものがあるか確認
			if(rankList.contains(key)){
				rankList.add(rankList.lastIndexOf(key) + 1, key);
				return rankList;
			// なければ二分探索
			}else if(rankList.get(center) < key){
				pLeft = center + 1;
			}else{
				pRight = center - 1;
			}
		}while(pLeft < pRight);

		return rankList;
	}
}