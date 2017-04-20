package ocha.itolab.flowdiff.core.seedselect;


/**
* Class to decode JSON file
*/
public class SeedInfo{
	private double score;
	private double entropy;
	private double diff;
	private int[] eid;
	private int id;
	
	public double getScore(){
		return this.score;
	}
	public void setScore(double score){
		this.score = score;
	}
	
	public double getEntropy(){
		return this.entropy;
	}
	public void setEntropy(double entropy){
		this.entropy = entropy;
	}
	
	public double getDiff(){
		return this.diff;
	}
	public void setDiff(double diff){
		this.diff = diff;
	}
	
	public int[] getEid(){
		return this.eid;
	}
	public void setEid(int[] eid){
		this.eid = eid;
	}
	
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}
	
	/**
	 * Get seed information from SeedInfo
	 */
	public void getSeedInfo(Seed seed){
		seed.score = this.getScore();
		seed.entropy = this.getEntropy();
		seed.diff = this.getDiff();
		seed.eid = this.getEid();
	}
	
	/**
	 * Set seed information to SeedInfo
	 */
	public void setSeedInfo(Seed seed){
		this.score = seed.score;
		this.entropy = seed.entropy;
		this.diff = seed.diff;
		this.eid = seed.eid;
		this.id = seed.id;
	}
}