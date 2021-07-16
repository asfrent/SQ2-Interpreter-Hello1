package ro.pub.cpl.sq2.hello1;

class NumberType extends SQ2Type {
	@Override
	public String getLLVMType() {
		return "i32";
	}
}