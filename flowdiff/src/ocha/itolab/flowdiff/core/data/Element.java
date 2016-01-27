package ocha.itolab.flowdiff.core.data;

import ocha.itolab.flowdiff.core.streamline.Streamline;
import ocha.itolab.flowdiff.core.streamline.StreamlineGenerator;

// 格子点データ
public class Element {
	// 格子データ？
	public GridPoint gp[] = new GridPoint[8];
	
	/**
	 * エレメント内の特定要素を返す
	 */
	public GridPoint getElement(int num){
		
		GridPoint ele = new GridPoint();
		ele = gp[num];
		return ele;
	}
	
	/**
	 * エレメント内の位置関係を表示するテストメソッド
	 */
	public GridPoint[] getElement2(int num1,int num2){
		
		GridPoint ele[] = new GridPoint[2];
		ele[0] = gp[num1];
		ele[1] = gp[num2];
		return ele;
	}
	/**
	 * 
	 * @param myId
	 * @param sl
	 * @param mode
	 * @return
	 */
	
	// 自分とStreamlineの交差判定を行うメソッド
//	boolean intersect(int myId, Streamline sl, int mode) {
//		if (mode == 0){ // 「かなりむず」だったら
//			return StreamlineGenerator.lastElementId() == myId;
//		}
//		else if (mode == 1) { // 「ちょいむず」だったら（初期設定）
//			int elementLength = StreamlineGenerator.elementIds.size();
//			int i;
//			for (i = 0; i < elementLength; i++){
//				if (myId == StreamlineGenerator.elementIds.get(i)) return true;
//			}
//			return false;
//		}
//		else return false;
//				
//	}

}
