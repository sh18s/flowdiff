package ocha.itolab.flowdiff.core.data;

import java.io.*;
import java.io.FileReader;
import java.util.*;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class TecPlotFileReader {
	static BufferedReader breader = null;
	static int np[];
	
	
	/**
	 * ファイルを読んでGridを得る
	 */
	public static Grid getGrid(String path) {
		Grid grid = new Grid();
		open(path);
		System.out.println("   ... reading " + path);
		
		// ファイルを1行ずつ読む
		boolean isValues = false;
		int count = 0, total = 0;
		np = new int[3];
		while(true) {
			String words[] = readLine();
			if(words == null) break;

			// 格子数をセット
			if(np[0] == 0 && words[0].startsWith("I=") == true) {
				for(int i = 0; i < 3; i++) {
					String v = words[i].substring(2);
					v = v.replace(",", "");
					np[i] = Integer.parseInt(v);
				}
				grid.setNumGridPoint(np[0], np[1], np[2]);
				total = np[0] * np[1] * np[2];
				continue;
			}
			
			// 次の行から数値であることを確認
			if(words[0].startsWith("DT=(") == true) {
				isValues = true;   continue;
			}
			
			// 値を読み始める
			if(isValues == true) {
				GridPoint gp = grid.getGridPoint(count++);
				double x = Double.parseDouble(words[0]);
				double y = Double.parseDouble(words[1]);
				double z = Double.parseDouble(words[2]);
				double u = Double.parseDouble(words[4]);
				double v = Double.parseDouble(words[5]);
				double w = Double.parseDouble(words[6]);
				gp.setPosition(x, y, z);
				gp.setVector(u, v, w);
				
				
				if(count >= total)
					isValues = false;
			}
			
		}
		
		close();
		System.out.println("    .... done. " + count);
		grid.finalize();
		return grid;
	}

	
	/**
	 * ファイルを開く
	 */
	static void open(String path) {
		
		try {
			File file = new File(path);
			breader = new BufferedReader(new FileReader(file));
			breader.ready();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	
	/**
	 * ファイルを閉じる
	 */
	static void close() {
		try {
			breader.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	/**
	 * Plotファイルの1行を読む
	 */
	static String[] readLine() {
		String wordarray[] = null;
		
		try {
			
			// EOFまで読み続ける
			String line = breader.readLine();
			if (line == null) return null;
			
			// 空行であれば無意味な内容を返す
			if(line.length() <= 0) {
				wordarray = new String[1];
				wordarray[0] = "";
				return wordarray;
			}
			
			// 1行を単語ごとに区切る
			StringTokenizer tokenBuffer = new StringTokenizer(line);
			wordarray = new String[tokenBuffer.countTokens()];
			for(int i = 0; i < wordarray.length; i++) {
				wordarray[i] = tokenBuffer.nextToken();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}

		// 解読結果を返す
		return wordarray;
	}

}
