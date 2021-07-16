package ro.pub.cpl.sq2.hello1;

import java.util.HashMap;

class LocalVariables {
	private HashMap<String, String> nameToType = new HashMap<String, String>();
	private HashMap<String, String> nameToSymbol = new HashMap<String, String>();

	public void add(String name, String symbol, String type) {
		nameToSymbol.put(name, symbol);
		nameToType.put(name, type);
	}

	public String getType(String name) {
		return nameToType.get(name);
	}

	public String getSymbol(String name) {
		return nameToSymbol.get(name);
	}

	public void clear() {
		nameToSymbol.clear();
		nameToType.clear();
	}
}