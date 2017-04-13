package ocha.itolab.flowdiff.core.seedselect;


public class ScoreRank{
	private int[] eid;
	private double score;
	
	public int[] getEid(){
		return this.eid;
	}
	public void setEid(int[] eid){
		this.eid = eid;
	}
	
	public double getScore(){
		return this.score;
	}
	public void setScore(double score){
		this.score = score;
	}
}