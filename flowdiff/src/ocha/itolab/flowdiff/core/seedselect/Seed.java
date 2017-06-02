package ocha.itolab.flowdiff.core.seedselect;

//import java.util.ArrayList;

import ocha.itolab.flowdiff.core.streamline.Streamline;

public class Seed {
	int id;
	double score;
	int eid[];
	Streamline sl1, sl2;
	double entropy, diff; // added by sh
	
	public int getId(){
		return this.id;
	}
	
	public double getEntropy(){
		return this.entropy;
	}
	
	public double getDiff(){
		return this.diff;
	}
}