package ocha.itolab.flowdiff.util;

import ocha.itolab.flowdiff.core.data.Grid;
import ocha.itolab.flowdiff.core.data.GridPoint;

public class DiffVectorCal {

	public Grid grid;
	public GridPoint angle[];
	public GridPoint length[];
	public double max = 0.0;

	/**
	 * 二つのベクトル間の角度を算出するメソッド
	 * @param grid1
	 * @param grid2
	 */
	public void calDiffAngle(Grid grid1,Grid grid2){
		
		grid = grid1;
		angle = new GridPoint[grid1.getNumGridPointAll()];

		//角度を求める
		for(int i=0; i<grid1.getNumGridPointAll(); i++){
			angle[i] = grid.getGridPoint(i);
			if(grid1.getEnvironment(i) != grid2.getEnvironment(i)){
				//建物のあるなしが異なっている場合、外れ値をいれる
				angle[i].setAngDiff(-1);
			}else{
				//角度の計算
				double[] vec1 = new double[3];
				double[] vec2 = new double[3];
				double cos = 0.0;

				vec1[0] = grid1.getGridPoint(i).getVector()[0];
				vec1[1] = grid1.getGridPoint(i).getVector()[1];
				vec1[2] = grid1.getGridPoint(i).getVector()[2];
				vec2[0] = grid2.getGridPoint(i).getVector()[0];
				vec2[1] = grid2.getGridPoint(i).getVector()[1];
				vec2[2] = grid2.getGridPoint(i).getVector()[2];

				//内積から角度を算出
				double innerProduct = vec1[0]*vec2[0] + vec1[1]*vec2[1] +vec1[2]*vec2[2];
				double dist1 = Math.sqrt(vec1[0]*vec1[0] + vec1[1]*vec1[1] + vec1[2]*vec1[2]);
				double dist2 = Math.sqrt(vec2[0]*vec2[0] + vec2[1]*vec2[1] + vec2[2]*vec2[2]);
				cos = innerProduct/(dist1*dist2);
				//System.out.println("cos="+cos);
				//System.out.println(Math.acos(cos));
				angle[i].setAngDiff(Math.acos(cos));
			}

		}
	}
	
	/**
	 * 二つのベクトル間の長さを算出するメソッド
	 * @param grid1
	 * @param grid2
	 */
	public void calDiffLen(Grid grid1,Grid grid2){
		
		grid = grid1;
		length = new GridPoint[grid1.getNumGridPointAll()];

		//角度を求める
		for(int i=0; i<grid1.getNumGridPointAll(); i++){
			
			length[i] = grid.getGridPoint(i);
			if(grid1.getEnvironment(i) != grid2.getEnvironment(i)){
				//建物のあるなしが異なっている場合、外れ値をいれる
				length[i].setLenDiff(-1);
			}else{
				//長さの計算
				double len1 = grid1.getGridPoint(i).getLength();
				double len2 = grid2.getGridPoint(i).getLength();
				if(i%1000 == 0){
					//System.out.println("len1="+len1+"len2="+len2);
				}
				length[i].setLenDiff(Math.abs(len1-len2));
			}
			if(max < length[i].getLenDiff()){
				if(i%1000 == 0){
					//System.out.println("max1="+max+"max2="+length[i].getLenDiff());
				}
				max = length[i].getLenDiff();
			}
		}
		//System.out.println("max="+max);
	}
	
	

}
