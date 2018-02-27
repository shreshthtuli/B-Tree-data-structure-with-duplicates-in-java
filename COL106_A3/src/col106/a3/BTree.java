package col106.a3;

import java.util.List;
import java.util.ArrayList;

public class BTree<Key extends Comparable<Key>,Value> implements DuplicateBTree<Key,Value> {

    static int b;
    int size = 0;
    int height = -1;
    BTreeNode root;

    public BTree(int b) throws bNotEvenException {  /* Initializes an empty b-tree. Assume b is even. */
        if(b%2!=0){throw new bNotEvenException();}
        this.b = b;
        root = new BTreeNode(b, null);
    }

    public void split(BTreeNode x, int i, BTreeNode y){
        BTreeNode temp = new BTreeNode(b, null);
        temp.isLeaf = y.isLeaf;
        temp.count = b/2-1;
        for(int j = 0; j<((b/2) -1); j++){
            temp.elements[j] = y.elements[j+b/2];
        }
        if(!y.isLeaf){
            for(int k=0; k< b/2; k++){
                temp.children[k] = y.children[k+b/2];
            }
        }
        y.count = b/2 -1;
        for(int j= x.count; j>i; j--){
            x.children[j+1] = x.children[j];
        }
        x.children[i+1] = temp;
        temp.parent=x;
        y.parent=x;
        for(int j = x.count-1; j>=i; j--){
            x.elements[j+1] = x.elements[j];
        }
        x.elements[i] = y.elements[b/2 -1];
        y.elements[((b/2) -1)] = null;
        for(int j=0; j<((b/2) -1); j++){
            y.elements[j+((b/2) -1)] = null;
        }
        x.count++;
    }

    @Override
    public boolean isEmpty() {
        return(this.root.count==0);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public List<Value> search(Key key) throws IllegalKeyException {
        List<Value> result = new ArrayList<Value>();
        searchNode(root, key, result);
        return result;
    }

    public void searchNode(BTreeNode a, Key key, List<Value> result){
        int j = 0;
        while(j<a.count && a.elements[j]!=null && key.compareTo((Key)a.elements[j].key)>=0){
            if(!a.isLeaf){a.children[j].parent=a;}
            if(key.compareTo((Key)a.elements[j].key)==0){
                result.add((Value)a.elements[j].val);
                if(!a.isLeaf)
                    searchNode(a.children[j], key, result);
            }
            j++;
        }
        if(!a.isLeaf)
            searchNode(a.children[j], key, result);
    }

    public void searchNode(BTreeNode a, Key key, List<BTreeNode> result, List<Integer> indices){
        int j = 0;
        while(a!=null && j<a.count && a.elements[j]!=null && key.compareTo((Key)a.elements[j].key)>=0){
            if(!a.isLeaf){a.children[j].parent=a;}
            if(key.compareTo((Key)a.elements[j].key)==0){
                result.add(a);
                indices.add(j);
                if(!a.isLeaf)
                    searchNode(a.children[j], key, result, indices);
            }
            j++;
        }
        if(a!=null && !a.isLeaf)
            searchNode(a.children[j], key, result, indices);
    }

    public void simpleinsert(BTreeNode x, Key key, Value val){
        int i = x.count;
        if(x.isLeaf){
            while(i>=1 && key.compareTo((Key)x.elements[i-1].key)<0){
                x.elements[i] = x.elements[i-1];
                i--;
            }
            x.elements[i] = new BTreeElement(key, val);
            x.count++;
        }
        else{
            int j=0;
            while(j<x.count && key.compareTo((Key)x.elements[j].key)>=0){
                j++;
            }
            if(x.children[j].count == b-1){
                split(x, j, x.children[j]);
                if(key.compareTo((Key)x.elements[j].key)>=0){j++;}
            }
            simpleinsert(x.children[j],key,val);
        }
    }

    @Override
    public void insert(Key key, Value val) {
        size+=1;
        BTreeNode r = this.root;
        if(r.count == b-1){
            BTreeNode s = new BTreeNode(b, null);
            this.root = s;
            s.isLeaf = false;
            s.count = 0;
            s.children[0] = r;
            r.parent = s;
            split(s, 0, r);
            simpleinsert(s, key, val);
            height+=1;
        }
        else{
            if(r.count==0){height+=1;}
            simpleinsert(r, key, val);
        }
    }

    @Override
    public void delete(Key key) throws IllegalKeyException {
        List<BTreeNode> result = new ArrayList<BTreeNode>();
        List<Integer> indices = new ArrayList<Integer>();
        searchNode(root, key, result, indices);
        //System.out.println(result);
        //System.out.println(indices);
        boolean swap = false;
        if(result.isEmpty())
            throw new IllegalKeyException();
        else{
            while(!result.isEmpty()){
                BTreeNode node;
                Integer j;
                node = result.get(0);
                j = indices.get(0);
                if(node.isLeaf){
                    if(node.count==b/2-1 && node!=this.root){
                        int c = 0;
                        BTreeNode parent;
                        parent = node.parent;
                        while(parent!=null && c<=parent.count){
                            if(parent.children[c]==node){break;}
                            c++;
                        }
                        //System.out.println(c);
                        //System.out.println(node);
                        //System.out.println(parent);
                        fill(c, node, parent);
                        if(!swap){
                            result.clear();
                            indices.clear();
                            searchNode(root, key, result, indices);
                            node = result.get(0);
                            j = indices.get(0);
                        }
                        //System.out.print("After Fill ");
                        //System.out.println(this);
                    }
                    //System.out.println(result);
                    //for(int q=0; q<=b-1; q++){
                    //        if(node.parent.children[q]!=null){System.out.println(q+"  "+node.parent.children[q].elements[0].key);}
                    //        else{System.out.println(q+"  "+"null");}
                    //    }
                    //System.out.println(indices);
                    int k = node.count-1;
                    while(k>=0 && key.compareTo((Key)node.elements[k].key)!=0){
                        k--;
                    }
                    for(int p= k; p<node.count-1; p++)
                        node.elements[p]=node.elements[p+1];
                    node.elements[node.count-1]=null;
                    node.count--;
                    size--;
                    if(size==0){height=-1;}
                    //System.out.print("Delete at leaf ");
                    //System.out.println(this);
                    swap=false;
                    result.clear();
                    indices.clear();
                    searchNode(root, key, result, indices);
                    //System.out.println(result);
                    //System.out.println(indices);
                }
                //Swap with inorder successor and add it to result and indices lists
                else{
                    BTreeNode temp;
                    temp = node.children[j+1];
                    node.children[j+1].parent = node;
                    while(!temp.isLeaf){
                        temp.children[0].parent = temp;
                        temp = temp.children[0];
                    }
                    BTreeElement temp_elem = new BTreeElement(node.elements[j].key, node.elements[j].val);
                    node.elements[j]=temp.elements[0];
                    temp.elements[0]=temp_elem;
                    result.add(0, temp);
                    indices.add(0, 0);
                    //System.out.print("Swap ");
                    //System.out.println(this);
                    swap = true;
                    //System.out.println(result);
                    //System.out.println(indices);
                }
            }
        }
    }

    public void fill(int c, BTreeNode node, BTreeNode parent){
        //System.out.println(c);
        //Borrow from left sibling
        if(c!=0 && parent.children[c-1].count>b/2-1){
            for(int p=node.count; p>0; p--)
                node.elements[p]=node.elements[p-1];
            node.elements[0]=parent.elements[c-1];
            parent.elements[c-1] = parent.children[c-1].elements[parent.children[c-1].count - 1];
            parent.children[c-1].elements[parent.children[c-1].count - 1] = null;
            if(parent.children[c-1].isLeaf==false){
                for(int p=node.count; p>=0; p--)
                    node.children[p+1]=node.children[p];
                node.children[0]=parent.children[c-1].children[parent.children[c-1].count];
                parent.children[c-1].children[parent.children[c-1].count] = null;
            }
            parent.children[c-1].count--;
            node.count++;
            //System.out.print("After Left borrow ");
            //System.out.println(this);
        }
        //Borrow from right sibling
        else if(parent.children[c+1]!=null && c!=parent.count && parent.children[c+1].count>b/2-1){
            node.elements[node.count]=parent.elements[c];
            parent.elements[c] = parent.children[c+1].elements[0];
            for(int p=0; p<parent.children[c+1].count-1; p++)
                parent.children[c+1].elements[p]=parent.children[c+1].elements[p+1];
            parent.children[c+1].elements[parent.children[c+1].count-1]=null;
            if(parent.children[c+1].isLeaf==false){
                node.children[node.count+1]=parent.children[c+1].children[0];
                for(int p=0; p<parent.children[c+1].count; p++)
                    parent.children[c+1].children[p]=parent.children[c+1].children[p+1];
                parent.children[c+1].children[parent.children[c+1].count]=null;
            }
            parent.children[c+1].count--;
            node.count++;
            //System.out.print("After right borrow ");
            //System.out.println(this);
            //for(int q=0; q<=b-1; q++){
            //                if(node.parent.children[q]!=null){System.out.println(q+"  "+node.parent.children[q].elements[0].key);}
            //                else{System.out.println(q+"  "+"null");}
            //            }
        }
        //Merge
        else if(c==0){
            //System.out.println(node.count);
            node.elements[node.count++]=parent.elements[0];
            for(int i = 0; i<parent.children[1].count; i++){
                node.elements[node.count]=parent.children[1].elements[i];
                node.count++;
            }
            if(node.isLeaf==false){
                for(int p=b/2-1; p<=node.count; p++)
                    node.children[p]=parent.children[1].children[p-b/2+1];
            }
            for(int k=0; k<parent.count; k++){
                if(k+1<parent.count){parent.elements[k]=parent.elements[k+1];}
                if(k+2<=parent.count){parent.children[k+1]=parent.children[k+2];}
            }
            parent.children[parent.count]=null;
            if(parent.count>0){parent.elements[parent.count-1]=null;}
            parent.count--;
            //System.out.print("After merge1 ");
            //System.out.println(this);
            //for(int q=0; q<=b-1; q++){
            //                if(node.parent.children[q]!=null){System.out.println(q+"  "+node.parent.children[q].elements[0].key);}
            //                else{System.out.println(q+"  "+"null");}
            //            }
            if(parent==this.root && parent.count==0){
                this.root = node;
                height--;
            }
            else if(parent.count==b/2-2 && parent!=this.root){
                node = parent;
                parent = node.parent;c=0;
                while(parent!=null && c<=parent.count){
                    if(parent.children[c]==node){break;}
                    c++;
                }
                fill(c, node, parent);
            }
            //System.out.println(this);
        }
        else{
            for(int k=0; k<=node.count; k++){
                if(k+b/2<b-1){node.elements[k+b/2]=node.elements[k];}
                if(node.isLeaf==false){node.children[k+b/2]=node.children[k];}
                node.elements[k]=node.parent.children[c-1].elements[k];
            }
            if(node.isLeaf==false){
                for(int p=0; p<=parent.children[c-1].count; p++)
                    node.children[p]=parent.children[c-1].children[p];
            }
            node.elements[b/2-1]=parent.elements[c-1];
            node.count=node.count+b/2;
            //System.out.println(this);
            //System.out.println(parent.count);
            //System.out.println(node.count);
            //System.out.println(c);
            for(int k=c-1; k<parent.count; k++){
                if(k!=b-2)
                    parent.elements[k]=parent.elements[k+1];
                else
                    parent.elements[k]=null;
            }
            for(int k=c-1; k<=parent.count; k++){
                if(k!=b-1)
                    parent.children[k]=parent.children[k+1];
                else
                    parent.children[k]=null;
            }
            parent.count--;
            //System.out.print("After merge2 ");
            //System.out.println(this);
            //for(int q=0; q<=b-1; q++){
            //                if(node.parent.children[q]!=null){System.out.println(q+"  "+node.parent.children[q].elements[0].key);}
            //                else{System.out.println(q+"  "+"null");}
            //            }
            if(parent==this.root && parent.count==0){
                this.root = node;
                height--;
            }
            else if(parent.count==b/2-2 && parent!=this.root){
                node = parent;
                parent = node.parent;
                c=0;
                while(parent!=null && c<=parent.count){
                    if(parent.children[c]==node){break;}
                    c++;
                }
                fill(c, node, parent);
            }
            //System.out.println(this);
        }
    }

    public String toString(){
        return(this.root.printNode());
    }
}
