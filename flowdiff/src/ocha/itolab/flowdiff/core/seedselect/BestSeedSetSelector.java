package ocha.itolab.flowdiff.core.seedselect;

import java.util.*;

import ocha.itolab.flowdiff.core.streamline.*;
import ocha.itolab.flowdiff.core.data.*;
//import ocha.itolab.flowdiff.core.seedselect.BinarySearch;

public class BestSeedSetSelector {
	static int REPEAT1 = 1000, REPEAT2 = 20;
	static int NUMSEED = 20, NUMCANDIDATE = 200;
	
	/**
	 * Select the best set of streamlines
	 */
	public static StreamlineArray selectRandomly(Grid grid1, Grid grid2) {
		StreamlineArray bestset = null;
		double bestscore = 0.0;
		
//		SeedComparator comp = new SeedComparator();
//		TreeSet treeset = new TreeSet(comp);
		ArrayList<Seed> seedlist = new ArrayList<Seed>();
		
		// first random try to find good seeds
		// TODO: 1000回じゃなくてすべてのelementぶんまわす
		int[] total = grid1.getNumElement();
		int[] eleNum = new int[3];
		
//		for(int i = 0; i < REPEAT1; i++) {
		for(int i = 0; i < total[0] ; i++){
			eleNum[0] = i;
			for(int j = 0; j < total[1] ; j++){
				eleNum[1] = j;
				for(int k = 0; k < total[2]; k++){
					eleNum[2] = k;
					Seed seed = new Seed();
					seed.id = i;
//					seed.eid = setSeedRandomly1(grid1);
					seed.eid = eleNum;
//					System.out.println("eid = " + seed.eid[0] + "," + seed.eid[1] + ","+ seed.eid[2]);
					seed.sl1 = new Streamline();
					seed.sl2 = new Streamline();
					StreamlineGenerator.generate(grid1, seed.sl1, seed.eid, null);
					StreamlineGenerator.generate(grid2, seed.sl2, seed.eid, null);
					//seed.score = StreamlineArrayEvaluator.evaluate1(grid1, grid2, seed.sl1, seed.sl2);
					seed.score = SingleEvaluator.calcSingleValue(grid1, grid2, seed.sl1, seed.sl2);
					//add now
					seedlist.add(i, seed);
//					System.out.println("score = " + seed.score + "," + i);
//					treeset.add((Object)seed);
				}
			}
		}

//		Iterator it = treeset.iterator();
//		while (it.hasNext()) {
//			Seed s = (Seed)it.next();
//			seedlist.add(s); // 1000こぶんのidと評価値が入ってる
//		}

		// seedlistを評価値に並べ替え
		LinkedList<Seed> rankList = new LinkedList<Seed>();
		for(Seed key: seedlist){
//			System.out.println("key.socre = " + key.score);
			rankList = BinarySearch.binarySearch(rankList, key);
		}
//		視点に依存しない評価値で足切り
		ArrayList<Seed> mList = new ArrayList<Seed>(); // 意義のある流線ペア群
        for(int i = 0; i < NUMCANDIDATE; i++){
        	mList.add(rankList.get(i));
        }
        
        bestset = ViewDependentEvaluator.select(mList);
        return bestset;		
	}
	
	
	/**
	 * Randomly select a seed
	 */
	static int[] setSeedRandomly1(Grid grid) {
		int seedid[] = new int[3];
		int nume[] = grid.getNumElement();
		
		// for each seed0
		for(int j = 0; j < 3; j++) {
			int id = (int)(Math.random() * (double)nume[j]);
			if(id < 0) id = 0;
			if(id >= nume[j]) id = nume[j] - 1;
			seedid[j] = id;
		}
		
		return seedid;
	}
	
	
	/**
	 * Randomly select a set of seeds
	 */
	static StreamlineArray setSeedRandomly2(ArrayList<Seed> list) {
		Seed seeds[] = new Seed[NUMSEED];
		int ids[] = new int[NUMSEED];
		
		// for each seed0
		for(int i = 0; i < NUMSEED; i++) {
			boolean isValid = false;
			while(isValid == false) {
				int id = (int)(Math.random() * NUMCANDIDATE);
				if(id < 0) id = 0;
				if(id >= NUMCANDIDATE) id = NUMCANDIDATE - 1;
				isValid = true;
				for(int j = 0; j < i; j++) {
					if(ids[j] == id) {
						isValid = false;  break;
					}
				}
				if(isValid == true) {
					seeds[i] = list.get(id);
					ids[i] = id;   break;
				}
			}
		}
		
		
		StreamlineArray slset = new StreamlineArray();
		for(int i = 0; i < NUMSEED; i++) {
			slset.addList1(seeds[i].sl1);
			slset.addList2(seeds[i].sl2);
			slset.addDeperture(seeds[i].eid);
			slset.color.add(false);
		}
		return slset;
	}
	
//	static class SeedComparator implements Comparator {
//
//		public int compare(Object obj1, Object obj2) {
//
//			Seed seed1 = (Seed) obj1;
//			Seed seed2 = (Seed) obj2;
//			
//			if (seed2.score - seed1.score > 1.0e-10)
//				return 1;
//			if (seed1.score - seed2.score > 1.0e-10)
//				return -1;
//			if (seed1.id > seed2.id)	
//				return 1;
//			return -1;
//		}
//
//	}

}
