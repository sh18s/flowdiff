package ocha.itolab.flowdiff.core.seedselect;

import java.nio.*;
import java.util.*;
import javax.media.opengl.glu.gl2.GLUgl2;

import com.jogamp.common.os.MachineDescription.StaticConfig;

import ocha.itolab.flowdiff.core.streamline.*;


public class ViewDependentEvaluator {
	static int numseed = 20;
	
	public static double DIST_TH = 5.0;  // ディスプレイ上の距離の2乗値の閾値
	public static int COUNTER_TH = 1500;  // 既存流線に近隣する頂点数の閾値
	
	static DoubleBuffer model = null, proj = null;
	static IntBuffer view = null;
	static GLUgl2 glu2;
	
//	static ArrayList<double[]> coordinates1, coordinates2; // Keep 2d coordinates of streamline pair
//	static ArrayList<ArrayList<double[]>> preList;	// Keep coordinates of already selected　streamlines
	
	public static StreamlineArray select(ArrayList<Seed> seedlist) {
		ArrayList<ArrayList<double[]>> preList = new ArrayList<ArrayList<double[]>>();
//		preList = new ArrayList<ArrayList<double[]>>();
//		coordinates1 = new ArrayList<double[]>(); // delete temporarily by sh
//		coordinates2 = new ArrayList<double[]>();
		StreamlineArray sarray = new StreamlineArray();
//		sarray.clear();
		
		// for each seed
		int counter = 0;
		int rejectCounter = 0;
		for(int i = 0; i < seedlist.size(); i++) {
			ArrayList<double[]> coordinates1 = new ArrayList<double[]>(); // added by sh
			ArrayList<double[]> coordinates2 = new ArrayList<double[]>();
			
			Seed seed = seedlist.get(i);
			// Projection
			boolean shouldSelect1 = project(seed.sl1, preList, coordinates1);
			if(shouldSelect1 == false) {
				rejectCounter++;
				coordinates1.clear();
				continue;
			}
			// Projection
			boolean shouldSelect2 = project(seed.sl2, preList,coordinates2);
			if(shouldSelect2 == false) {
				rejectCounter++;
				coordinates2.clear();
				continue;
			}
			
			// Add a new pair of streamlines
			sarray.addList(seed.sl1, seed.sl2, seed.eid);
			
			if(++counter >= numseed) break;
			// project関数によって価値があると判定された流線ペアをpreListに追加
			if(shouldSelect1 == true && shouldSelect2 == true){
				preList.add(coordinates1);
				preList.add(coordinates2);
			}
//			coordinates1 = new ArrayList<double[]>(); // delete temporarily by sh
//			coordinates2 = new ArrayList<double[]>();
			
		}
		System.out.println("rejectCounter = " + rejectCounter);
		System.out.println("** " + sarray.streamlines1.size());
		return sarray;
	}
	
	/**
	 * Set view configuration
	 */
	public static void setViewConfiguration(DoubleBuffer m, DoubleBuffer p, IntBuffer v, GLUgl2 g) {
		model = m;   proj = p;   view = v;  glu2 = g;
	}
	
	/**
	 * Decide whether select the streamline by calculate the distance on the display. 
	 * @param sl: Target streamline 
	 * @param coordinates: 2d coordinates of target streamline
	 * @return boolean: select or not
	 */
	static boolean project(Streamline sl, ArrayList<ArrayList<double[]>> preList, ArrayList<double[]> coordinates) {
		if(model == null || proj == null || view == null)
			return true;
		
		DoubleBuffer ppos = DoubleBuffer.allocate(3);
		
		// for each vertex of the given streamline
		for(int i = 0; i < sl.getNumVertex(); i++) {
			double pos[] = sl.getPosition(i);
			double y = view.get(3) - pos[1] + 1;
			glu2.gluProject(pos[0], y, pos[2], model, proj, view, ppos);
			double pos2[] = new double[2];
			pos2[0] = ppos.get(0);
			pos2[1] = ppos.get(1);
			coordinates.add(pos2);
		}
		// for each previously registered streamline
		for(int i = 0; i < preList.size(); i++) {
			ArrayList<double[]> pl = preList.get(i);
			int counter = 0;
			// for each vertex of the current streamline
			for(int j = 0; j < coordinates.size(); j++) {
				double p1[] = coordinates.get(j);
				// for each vertex of the previously registered streamline
				for(int k = 0; k < pl.size(); k++) {
					double p2[] = pl.get(k);
					double dist = (p1[0] - p2[0]) * (p1[0] - p2[0])
							    + (p1[1] - p2[1]) * (p1[1] - p2[1]);
					if(dist < DIST_TH) counter++;
					if(counter >= COUNTER_TH) return false;
				}
			}
		}
		return true;
	}
	
	
	
}
