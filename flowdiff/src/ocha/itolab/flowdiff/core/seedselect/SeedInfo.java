package ocha.itolab.flowdiff.core.seedselect;


/**
* Class to decode JSON file
*/
public class SeedInfo{
	private double score;
	private double entropy;
	private double diff;
	private int[] eid;
	
	public double getScore(){
		return this.score;
	}
	public double getEntropy(){
		return this.entropy;
	}
	public double getDiff(){
		return this.diff;
	}
	public int[] getEid(){
		return this.eid;
	}
}