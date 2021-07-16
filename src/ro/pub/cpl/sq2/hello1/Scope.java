package ro.pub.cpl.sq2.hello1;

import java.util.HashMap;

class Scope {
	HashMap<String, Variable> lookupTable = new HashMap<String, Variable>();
	boolean hasReturned = false;
	String functionName = null;

	void addBinding(String id, Variable variable) {
		lookupTable.put(id, variable);
	}

	public Scope(String functionName) {
		this.functionName = functionName;
	}

	Variable getByName(String id) {
		return lookupTable.get(id);
	}

	void returnEncountered() {
		hasReturned = true;
	}
}