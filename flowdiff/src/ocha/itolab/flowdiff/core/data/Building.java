package ocha.itolab.flowdiff.core.data;

public class Building {

	public Grid grid = new Grid();
	
	int[] init(Grid grid, int[] lookup){
		for(int i=0; i<grid.getNumGridPointAll();i++){
			grid.getGridPoint(i).setBuildingLabel(0);
		}
		for(int i=0;i<lookup.length;i++){
			lookup[i] = i;
		}
		return lookup;
	}
	
	public void labeling(Grid grid){
		int label = 1;
		int[] lookup = new int[20];
		
		//初期化処理(全てに０を割り当てる)
		lookup = this.init(grid,lookup);
		
		//建物のラベリング
		for (int i = 0; i < grid.getNumGridPointAll(); i++) {
			
			if(grid.getGridPoint(i).getEnvironment() != 0.0){
				int x = grid.getNumGridPoint()[0];
				int y = grid.getNumGridPoint()[1];
				int z = grid.getNumGridPoint()[2];
				int min = 0;
				
				//周り三点のラベルを調べる
				int label1 = 0;
				int label2 = 0;
				int label3 = 0;
				if(i-1>=0){
					label1 = grid.getGridPoint(i-1).getBuildingLabel();
				}
				if(i-x>=0){
					label2 = grid.getGridPoint(i-x).getBuildingLabel();
				}
				if(i-x*y>=0){
					label3 = grid.getGridPoint(i-x*y).getBuildingLabel();
				}
				//ラベルの最小値を得る
				min = minValue(label1,label2,label3);
				
				//ラベルをふる
				if(label1 == 0 && label2 == 0 && label3 == 0){
					grid.getGridPoint(i).setBuildingLabel(label);
					label++;
				}else{
					grid.getGridPoint(i).setBuildingLabel(min);
					//ルックアップテーブルの更新
					if(label1 != 0 && label1 != min){
						lookup[label1] = min;
					}
					if(label2 != 0 && label2 != min){
						lookup[label2] = min;
					}
					if(label3 != 0 && label3 != min){
						lookup[label3] = min;
					}
				}
				
			}
		}
		for(int i=0;i<grid.getNumGridPointAll();i++){
			int num = grid.getGridPoint(i).getBuildingLabel();
			if(lookup[num] != num){
				grid.getGridPoint(i).setBuildingLabel(lookup[num]);
			}	
		}
		
	}
	
	int minValue(int a,int b,int c){
		int min = 0;
		if(a==0 && b==0 && c==0){
			min = 0;
			return min;
		}
		if(a !=0){
			min = a;
			if(min > b && b != 0) min = b;
			if(min > c && c != 0) min = c;
		}else if(b!=0){
			min = b;
			if(min > c && c != 0) min = c;
		}else if(c!=0){
			min = c;
		}
		return min;
	}
	
	
	/**
	 * ラベルの端を取得するメソッド
	 * @param grid
	 * @param label
	 * @return
	 */
	public GridPoint[] minmaxPos(Grid grid,int label){
		
		GridPoint[] gp = new GridPoint[8];
		int num = 0;
		
		for(int i = 0; i < grid.getNumGridPointAll(); i++) {
			if(grid.getGridPoint(i).getBuildingLabel() == label){
				//同じラベルのみ
				int x = grid.getNumGridPoint()[0];
				int y = grid.getNumGridPoint()[1];
				int count = 0;
				if(i-1>=0){
					if(label == grid.getGridPoint(i-1).getBuildingLabel()){
						count++;
					}
				}
				if(i+1<grid.getNumGridPointAll()){
					if(label == grid.getGridPoint(i+1).getBuildingLabel()){
						count++;
					}
				}
				if(i-x>=0){
					if(label == grid.getGridPoint(i-x).getBuildingLabel()){
						count++;
					}
				}
				if(i+x<grid.getNumGridPointAll()){
					if(label == grid.getGridPoint(i+x).getBuildingLabel()){
						count++;
					}
				}
				if(i-x*y>=0){
					if(label == grid.getGridPoint(i-x*y).getBuildingLabel()){
						count++;
					}
				}
				if(i+x*y<grid.getNumGridPointAll()){
					if(label == grid.getGridPoint(i+x*y).getBuildingLabel()){
						count++;
					}
				}
				if(count==2 && label > 5){
					gp[num] = grid.getGridPoint(i);
					num++;
					//System.out.println("num="+num);
				}else if(count == 3 && label < 6){
					gp[num] = grid.getGridPoint(i);
					num++;
					//System.out.println("num="+num);
				}
			}
		}
		return gp;
		
	}
	
	/**
	 * 
	 * @param grid
	 * @param label
	 * @return
	 */
public GridPoint[] minmaxPosXZ(Grid grid,int label){
		
		GridPoint[] gp = new GridPoint[4];
		double minX = 10000;//この一万は適当な値　大きければなんでもよかった・・・・
		double minZ = 10000;
		double maxX= grid.getGridPoint(0).getPosition()[0];
		double maxZ = grid.getGridPoint(0).getPosition()[2];
		double Y = grid.getGridPoint(0).getPosition()[1];
		
		for(int i = 0; i < grid.getNumGridPointAll(); i++) {
			if(grid.getGridPoint(i).getBuildingLabel() == label){
				if(minX > grid.getGridPoint(i).getPosition()[0]){
					minX = grid.getGridPoint(i).getPosition()[0];
				}
				if(minZ > grid.getGridPoint(i).getPosition()[2]){
					minZ = grid.getGridPoint(i).getPosition()[2];
				}
				if(maxX < grid.getGridPoint(i).getPosition()[0]){
					maxX = grid.getGridPoint(i).getPosition()[0];
				}
				if(maxZ < grid.getGridPoint(i).getPosition()[2]){
					maxZ = grid.getGridPoint(i).getPosition()[2];
				}
			}
		}
		for(int i=0;i<4;i++){
			gp[i] = new GridPoint();
		}
		gp[0].setPosition(minX-0.01, Y, minZ); //ちょこっと小細工　-0.1　汎用性なくてごめんなさい
		gp[1].setPosition(minX-0.01, Y, maxZ);
		gp[2].setPosition(maxX, Y, maxZ);
		gp[3].setPosition(maxX, Y, minZ);
		return gp;
}
	/*
	public void labeling(Grid grid){
		int label = 1;
		
		//初期化処理(全てに０を割り当てる)
		this.init(grid);
		
		//建物のラベリング
		for (int i = 0; i < grid.getNumGridPointAll(); i++) {
			int x = grid.getNumGridPoint()[0];
			int y = grid.getNumGridPoint()[1];
			int z = grid.getNumGridPoint()[2];
			//double environment = grid.getGridPoint(i).environment;
			if(grid.getGridPoint(i).getBuildingLabel() != 0){
				continue;
			}
			
			int num = i;
			while(grid.getGridPoint(num).getEnvironment()!=0.0){
				//始点から探索
				grid.getGridPoint(num).setBuildingLabel(label);
				
				
				//終了判定
				
				if(grid.getGridPoint(num+1).getBuildingLabel()==0){
					//横
					num++;
				}else if(grid.getGridPoint(num+x).getBuildingLabel()==0){
					//上
					num = num+x;
				}else if(grid.getGridPoint(num-1).getBuildingLabel()==0){
					//横
					num--;
				}else if(grid.getGridPoint(num-x).getBuildingLabel()==0){
					//下
					num = num-x;
				}else if(grid.getGridPoint(num-x*y).getBuildingLabel()==0){
					//縦
					num = num -x*y;
				}else if(grid.getGridPoint(num+x*y).getBuildingLabel()==0){
					//縦
					num = num+x*y;
				}else{
					label++;
					break;
				}
			}
		}
		
	}*/
	

}
