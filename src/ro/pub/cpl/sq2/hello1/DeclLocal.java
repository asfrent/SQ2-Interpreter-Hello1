package ro.pub.cpl.sq2.hello1;

import java.util.ArrayList;

public class DeclLocal {
	ArrayList<LocalVar> vars = new ArrayList<LocalVar>();

	void addLocal(LocalVar local) {
		vars.add(local);
	}
}
