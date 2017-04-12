package ocha.itolab.flowdiff.core.seedselect;

//import java.util.ArrayList;

import ocha.itolab.flowdiff.core.streamline.Streamline;

public class Seed {
	int id;
	double score;
	int eid[];
	Streamline sl1, sl2;
	double entropy, diff; // added by sh
}
