package col106.a3;

public class BTreeElement<Key extends Comparable<Key>,Value>{
	public Key key;
	public Value val;

	public BTreeElement(Key key, Value val){
		this.key = key;
		this.val = val;
	}
}