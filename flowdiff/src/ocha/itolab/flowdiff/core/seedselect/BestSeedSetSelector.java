package ocha.itolab.flowdiff.core.seedselect;

import java.util.*;

import ocha.itolab.flowdiff.core.streamline.*;
import ocha.itolab.flowdiff.core.data.*;

public class BestSeedSetSelector {
	static int REPEAT1 = 1000, REPEAT2 = 20;
	static int NUMSEED = 20, NUMCANDIDATE = 200;
	
	/**
	 * Select the best set of streamlines
	 */
	public static StreamlineArray selectRandomly(Grid grid1, Grid grid2) {
		StreamlineArray bestset = null;
		double bestscore = 0.0;
		
		SeedComparator comp = new SeedComparator();
		TreeSet treeset = new TreeSet(comp);
		ArrayList<Seed> seedlist = new ArrayList<Seed>();
		
		// first random try to find good seeds
		// TODO: 1000回じゃなくてすべての流線ペアぶんforをまわす
		for(int i = 0; i < REPEAT1; i++) {

			// randomly generate a set of seeds
			Seed seed = new Seed();
			seed.id = i;
//			seed.id = 0;
			seed.eid = setSeedRandomly1(grid1);
			//System.out.println("   seedid=" + seed.eid[0] + "," + seed.eid[1] + "," + seed.eid[2]);
			seed.sl1 = new Streamline();
			seed.sl2 = new Streamline();
			StreamlineGenerator.generate(grid1, seed.sl1, seed.eid, null);
			StreamlineGenerator.generate(grid2, seed.sl2, seed.eid, null);
			//seed.score = StreamlineArrayEvaluator.evaluate1(grid1, grid2, seed.sl1, seed.sl2);
			seed.score = SingleEvaluator.rankSingleValue(grid1, grid2, seed.sl1, seed.sl2);
//			System.out.println(seed.score + "," + i);
			treeset.add((Object)seed);
		}
		
		Iterator it = treeset.iterator();
        while (it.hasNext()) {
        	Seed s = (Seed)it.next();
			seedlist.add(s); // 1000こぶんのidと評価値が入ってる
        }
//        TODO: seedlistを評価値順に並べ替え
//        TODO: 視点に依存しない評価値による足切り
//        TODO: x番目までをbestsetに入れる
//		int nums = treeset.size();
//		for (int i = 0; i < nums; i++) {
//		
//		}
		
		// second random try to find the best set of seeds
		for(int i = 0; i < REPEAT2; i++) {

			// randomly generate a set of seeds
			StreamlineArray slset = setSeedRandomly2(seedlist); //  1000このうちの20この流線ペア
			
			// evaluate the streamline set
			double score = StreamlineArrayEvaluator.evaluate2(grid1, grid2, slset);
			if(score > bestscore) {
				bestscore = score;
				bestset = slset;
			}
		}
		//System.out.println(bestset.streamlines1.size());
		// return
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
	
	
	
	static class Seed {
		int id;
		double score;
		int eid[];
		Streamline sl1, sl2;
	}
	
	
	static class SeedComparator implements Comparator {

		public int compare(Object obj1, Object obj2) {

			Seed seed1 = (Seed) obj1;
			Seed seed2 = (Seed) obj2;
			
			if (seed2.score - seed1.score > 1.0e-10)
				return 1;
			if (seed1.score - seed2.score > 1.0e-10)
				return -1;
			if (seed1.id > seed2.id)	
				return 1;
			return -1;
		}

	}

}
