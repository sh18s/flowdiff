package ocha.itolab.flowdiff.core.seedselect;

import java.nio.*;
import java.util.*;
import javax.media.opengl.glu.gl2.GLUgl2;

import com.jogamp.common.os.MachineDescription.StaticConfig;

import ocha.itolab.flowdiff.core.streamline.*;


public class ViewDependentEvaluator {
	static int numseed = 20;
	
	static DoubleBuffer model = null, proj = null;
	static IntBuffer view = null;
	static GLUgl2 glu2;
	// 2次元座標を一時的に保存するためのArrayList型
	static ArrayList<double[]> pline1, pline2;
	// 既に描画された流線ペア(の2次元座標)を保存するためのArrayList型
	static ArrayList<ArrayList> plinelist;	
	
	public static StreamlineArray select(ArrayList<Seed> seedlist) {
		plinelist = new ArrayList<ArrayList>();
		StreamlineArray sarray = new StreamlineArray();
		
		pline1 = new ArrayList<double[]>();
		pline2 = new ArrayList<double[]>();
		
		// for each seed
		int counter = 0;
		int counter2 = 0;
		for(int i = 0; i < seedlist.size(); i++) {
			Seed seed = seedlist.get(i);
			// 射影
			boolean ret1 = project(seed.sl1, pline1);
			if(ret1 == false) {
				counter2++;
				pline1.clear();
				continue;
			}
			// 射影
			boolean ret2 = project(seed.sl2, pline2);
			if(ret2 == false) {
				counter2++;
				pline2.clear();
				continue;
			}
			
			// add a new pair of streamlines
			sarray.addList(seed.sl1, seed.sl2, seed.eid);
			if(++counter >= numseed) break;
			// project関数によって価値があると判定された流線ペアをplinelistに追加
			if(ret1 == true && ret2 == true){
				plinelist.add(pline1);
				plinelist.add(pline2);
			}
			pline1 = new ArrayList<double[]>();
			pline2 = new ArrayList<double[]>();
			
		}
		System.out.println("counter2 = " + counter2);
		System.out.println("** " + sarray.streamlines1.size());
		return sarray;
	}
	
	
	/**
	 * Set view configuration
	 */
	public static void setViewConfiguration(DoubleBuffer m, DoubleBuffer p, IntBuffer v, GLUgl2 g) {
		model = m;   proj = p;   view = v;  glu2 = g;
	}
	
	public static double DIST_TH = 5.0;  // ディスプレイ上の距離の2乗値の閾値
	public static int COUNTER_TH = 1500;  // 既存流線に近隣する頂点数の閾値
	
	/**
	 * ある流線を描画するかどうか2次元上の距離を計算して決定する関数
	 * 引数1: 対象の流線
	 * 引数2: 対象の流線の2次元座標
	 */
	static boolean project(Streamline sl, ArrayList<double[]> pline) {
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
			pline.add(pos2);
		}
		
		// for each previously registered streamline
		for(int i = 0; i < plinelist.size(); i++) {
			ArrayList<double[]> pl = plinelist.get(i);
			int counter = 0;
			
			// for each vertex of the current streamline
			for(int j = 0; j < pline.size(); j++) {
				double p1[] = pline.get(j);
				
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
