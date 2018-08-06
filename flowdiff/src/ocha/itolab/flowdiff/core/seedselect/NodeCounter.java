package ocha.itolab.flowdiff.core.seedselect;

import java.io.*;
import java.io.FileReader;
import java.util.*;

import ocha.itolab.flowdiff.applet.flowdiff.PlotPanel;
import ocha.itolab.flowdiff.core.streamline.*;
import ocha.itolab.flowdiff.core.data.*;

public class NodeCounter{
	
	/**
	 * Count nodes(intersections) in Best set.
	 * @param bestset
	 * @return number of nodes
	 */
	public static int countNode(StreamlineArray bestset){
		int count = 0;
		Streamline sl1 = new Streamline();
		Streamline sl2 = new Streamline();
		
		for(int i = 0; i < bestset.getSize() -1 ; i ++){
			sl1 = bestset.getList1(i);
			sl2 = bestset.getList2(i);
			
			
		}
		
		return count;
	}
}