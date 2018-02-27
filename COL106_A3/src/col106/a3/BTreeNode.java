package col106.a3;

public class BTreeNode
{
	static int b;
	int count;
	BTreeElement elements[];
	BTreeNode[] children;
	boolean isLeaf;
	BTreeNode parent;

	public BTreeNode()
	{}

	public BTreeNode(int b, BTreeNode parent)
	{
		this.b = b;
		this.parent = parent;
		elements = new BTreeElement[b-1];
		children = new BTreeNode[b];
		this.isLeaf = true;
		this.count = 0;
	}

	public BTreeElement getVal(int i)
	{
		return elements[i];
	}

	public BTreeNode getChild(int i)
	{
		return children[i];
	}

	public String printNode(){
    	StringBuilder str = new StringBuilder("");
    	str.append("[");
    	if(this.count!=0){
    		for(int i=0; i<this.count; i++){
    			if(children[i]!=null){str.append(children[i].printNode()+",");}
    			if(elements[i]!=null && elements[i].key!=null){str.append(elements[i].key+"=");
    			str.append(elements[i].val);
    			if(i<this.count-1){str.append(",");}
    			}
    		}
    		if(children[this.count]!=null){str.append(","+children[this.count].printNode());}
    	}
    	str.append("]");
    	return str.toString();
	}
}