package ro.pub.cpl.sq2.hello1;

import java.util.HashMap;

public class StringTable {
	HashMap<String, String> table = new HashMap<String, String>();

	public String addString(String s) {
		String sID = newStringID();
		table.put(sID, s);
		return sID;
	}
	
	int lastStringID = 1;
	private String newStringID() {
		return "str" + lastStringID++;
	}
}
