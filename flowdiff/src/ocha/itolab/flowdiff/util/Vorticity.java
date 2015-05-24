package ocha.itolab.flowdiff.util;

public class Vorticity {
	/*
	 * 一つの頂点の位置、ベクトル
	 */
	double pos[] = new double[3];
	double vec[] = new double[3];
	double environment =0.0;
	int label = 0;
	double vorticity = 0.0;
	

	public void setPosition(double x, double y, double z) {
		pos[0] = x;
		pos[1] = y;
		pos[2] = z;
	}
	
	public double[] getPosition() {
		return pos;
	}
	/**
	 * 渦度
	 */
	public double getVorticity() {
		return vorticity;
	}

	public void setVorticity(double vorticity) {
		this.vorticity = vorticity;
	}
}
