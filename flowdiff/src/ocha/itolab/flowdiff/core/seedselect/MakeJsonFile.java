package ocha.itolab.flowdiff.core.seedselect;

import net.arnx.jsonic.JSON;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import ocha.itolab.flowdiff.core.streamline.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Make JSON File which stores coordinate of streamlines
 */
public class MakeJsonFile {
	
	public void makeJsonFile(StreamlineArray slArray) throws JSONException{
		
		ArrayList<Streamline> slArray1 = StreamlineArray.getAllList1(); // Get all streamlines in "bestset"
		ArrayList<Streamline> slArray2 = StreamlineArray.getAllList2();
		
		JSONArray positions = new JSONArray();
		
		for(Integer i = 0; i < slArray1.size(); i++){
			JSONArray coordinates1 = new JSONArray();
			JSONArray coordinates2 = new JSONArray();
			
			Streamline sl1 = slArray1.get(i);
			Streamline sl2 = slArray2.get(i);
			
			for(int j = 0; j < sl1.getNumVertex(); j++){
				coordinates1.put(sl1.getPosition(j)); // add coordinate and coordinate ID to HashMap
			}for(int j = 0; j < sl2.getNumVertex(); j++){
				coordinates2.put(sl2.getPosition(j)); // add coordinate and coordinate ID to HashMap
			}
			addPair(positions, coordinates1, coordinates2);
		}
		
		
		try{
			FileWriter fileWriter= new FileWriter("jsonfile.txt", false);
			fileWriter.write(positions.toString());
			fileWriter.close();
			System.out.println("Done.");
		}catch(IOException e){
			System.out.println("failed...");
		}
	}
	
	void addPair(JSONArray positions, JSONArray coordinates1, JSONArray coordinates2) throws JSONException{
		JSONObject pair = new JSONObject();
		
		pair.put("sl1", coordinates1);
		pair.put("sl2", coordinates2);
		positions.put(pair);
	}
}