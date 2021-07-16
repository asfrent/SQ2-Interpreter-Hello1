package ro.pub.cpl.sq2.hello1;

import java.util.ArrayList;

public class CallArgs {
	public ArrayList<TreeNode> argList = new ArrayList<TreeNode>();
	
	public void addArg(TreeNode statement) {
		argList.add(statement);
	}
}
