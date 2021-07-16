package ro.pub.cpl.sq2.hello1;

public class DeclArg {
	String name;
	SQ2Type type;

	public void setName(String name) {
		this.name = name;
	}

	public void setType(SQ2Type type) {
		this.type = type;
	}

	SemanticType getSemanticType() {
		if (type instanceof NumberType) {
			return SemanticType.NUMBER;
		}
		if (type instanceof ArrayType) {
			return SemanticType.ARRAYREF;
		}
		return SemanticType.UNDEFINED;
	}
}
