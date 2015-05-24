package ocha.itolab.flowdiff.core.streamline;

import java.util.ArrayList;

public class StreamlineArray {

	public static ArrayList<Streamline> streamlines1 = new ArrayList<Streamline>();//流線の集合リスト
	public static ArrayList<Streamline> streamlines2 = new ArrayList<Streamline>();
	public static ArrayList<int[]> deperture = new ArrayList<int[]>();//流線始点のリスト
	public static ArrayList<Boolean> color = new ArrayList<Boolean>(); //色の変化　初期ビット数
	
	/**
	 * 流線リストのセット
	 * @param s1
	 */
	public static void addList(Streamline s1,Streamline s2,int[] eIjk){
		deperture.add(eIjk);
		streamlines1.add(s1);
		streamlines2.add(s2);
		color.add(false);
	}
	public static void addList1(Streamline s1){
		streamlines1.add(s1);
	}
	public static void addList2(Streamline s2){
		streamlines2.add(s2);
	}
	
	/**
	 * 流線リスト全体を返す
	 * @return 
	 */
	public static ArrayList<Streamline> getAllList1(){
		return streamlines1;
	}
	public static ArrayList<Streamline> getAllList2(){
		return streamlines2;
	}
	/**
	 * 流線リスト特定の位置を返す
	 * @return 
	 */
	public static Streamline getList1(int i){
		return streamlines1.get(i);
	}
	public static Streamline getList2(int i){
		return streamlines2.get(i);
	}
	
	/**
	 * 流線リスト全体をクリアする
	 * @return 
	 */
	
	public static void  clearAllList(){
		streamlines1.clear();
		streamlines2.clear();
		deperture.clear();
		color.clear();
	}
	
	public static void  clearAllList1(){
		streamlines1.clear();
	}
	public static void clearAllList2(){
		streamlines2.clear();
	}
	
	
	/**
	 * 流線リストの特定の位置を削除する
	 * @return 
	 */
	
	public static void  clearList(int i){
		streamlines1.remove(i);
		streamlines2.remove(i);
		deperture.remove(i);
		color.remove(i);
	}
	
	public static void  clearList1(int i){
		streamlines1.remove(i);
	}
	public static void  clearList2(int i){
		streamlines2.remove(i);
	}
	
	/**
	 * 流線始点のリストのセット・返す・クリアする
	 */
	public static void addDeperture(int[] eIjk){
		deperture.add(eIjk);
	}
	public static ArrayList<int[]> getAllDeperture(){
		return deperture;
	}
	public static void clearAllDeperture(){
		deperture.clear();
	}
	public static void clearDeperture(int i){
		deperture.remove(i);
	}
	
	/**
	 * 流線の色切り替えメソッド
	 */
	public static void setStreamlineColor(int i,boolean b){
		color.set(i,b);
	}
}
