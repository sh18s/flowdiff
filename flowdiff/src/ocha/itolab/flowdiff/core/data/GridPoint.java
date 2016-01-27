package ocha.itolab.flowdiff.core.data;

public class GridPoint {
	/*
	 * 格子データ(格子点データの集合？)
	 */
	double pos[] = new double[3];
	double vec[] = new double[3];
	double environment =0.0;
	int label = 0;
	double vorticity = 0.0;
	double ang_diff = 0.0;
	double len_diff = 0.0;
	double length = 0.0;
	

	public void setPosition(double x, double y, double z) {
		pos[0] = x;
		pos[1] = y;
		pos[2] = z;
	}
	
	public void setVector(double u, double v, double w) {
		vec[0] = u;
		vec[1] = v;
		vec[2] = w;
	}
	
	
	public double[] getPosition() {
		return pos;
	}
	
	public double[] getVector() {
		return vec;
	}
	/**
	 * 建物の種別
	 * @param e
	 */
	public void setEnvironment(double e) {
		this.environment = e;
	}
	
	public double getEnvironment() {
		return environment;
	}

	
	/**
	 * 建物の位置によって分類する(ラベリングのための値)
	 * @return
	 */
	public int getBuildingLabel() {
		return label;
	}

	public void setBuildingLabel(int label) {
		this.label = label;
	}
	/**
	 * 角度差分の表示
	 * @return
	 */
	public double getAngDiff() {
		return ang_diff;
	}

	public void setAngDiff(double diff) {
		this.ang_diff = diff;
	}
	/**
	 * 長さ差分の表示
	 * @return
	 */
	public double getLenDiff() {
		return len_diff;
	}

	public void setLenDiff(double diff) {
		this.len_diff = diff;
	}
	
	/**
	 * 長さの表示
	 * @return
	 */
	public double getLength() {
		length = Math.sqrt(vec[0]*vec[0]+vec[1]*vec[1]+vec[2]*vec[2]);
		return length;
	}
}
