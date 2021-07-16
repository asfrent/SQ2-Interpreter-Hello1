package ro.pub.cpl.sq2.hello1;

class ArrayType extends SQ2Type {
	int size;

	public ArrayType(int size) {
		this.size = size;
	}

	@Override
	public String getLLVMType() {
		return "i32*";
	}
}