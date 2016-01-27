package ocha.itolab.flowdiff.core.seedselect;

// グラフ用にエントロピーと差分を保存しておくためのクラス
public class IndependentValue{
	double entropy;
	double diff;
	
	public void inputValue(double d, double e){
		this.entropy = e;
		this.diff = d;
	}
	
	public double outputEntropy(){
		double e = this.entropy;
		return e;
	}
	public double outputDiff(){
		double d = this.diff;
		return d;
	}
}