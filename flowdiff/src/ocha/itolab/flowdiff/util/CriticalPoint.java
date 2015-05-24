package ocha.itolab.flowdiff.util;

public class CriticalPoint {

	/*
	 * 一つの頂点の位置、ベクトル
	 */
	double pos[] = new double[3];

	public void setPosition(double x, double y, double z) {
		pos[0] = x;
		pos[1] = y;
		pos[2] = z;
	}
	
	public double[] getPosition() {
		return pos;
	}
}
