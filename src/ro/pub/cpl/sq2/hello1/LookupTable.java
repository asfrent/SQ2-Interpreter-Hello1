package ro.pub.cpl.sq2.hello1;

import java.util.Stack;

public class LookupTable {
	Stack<Scope> scopes = new Stack<Scope>();
	Variable loookup(String name) {
		return scopes.peek().lookupTable.get(name);
	}
}
