package ro.pub.cpl.sq2.hello1;

class Variable {
	SQ2Type type;

	// NUMERIC variable
	int value;

	// ARRAY variable
	int[] values;

	private Variable() {
	}
	
	static Variable valueVariable(int value) {
		Variable v = new Variable();
		v.type = new NumberType();
		v.value = value;
		return v;
	}

	static Variable arrayVariable(int size) {
		Variable v = new Variable();
		v.type = new ArrayType(size);
		v.values = new int[size];
		return v;
	}

	int at(int index) {
		return values[index];
	}
}