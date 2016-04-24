import java.util.ArrayList;

/**
 * Created by xc9pd on 4/14/2016.
 */
public class IndexNode {
    //the layer of the index. 0 is the root. one level lower plus 1;
    public long layer;
    //the label of the node. for example if it is a data node, the data is item 4, then then fromlabel be 4; the toLabel is 4 too;
    public long fromLabel;
    //the toLabel is the node range to;
    public long toLabel;
    //childrenNum represent the number of children a node have.
    public long childrenNum;
    //the children of a node, the label increasing from left to right of the array;
    public ArrayList<IndexNode> children;
    //check if the node a data node or an index node;
    public boolean isIndex;
    //check if the index use replicated index;
    public boolean useReplicationNode;
    //the if the node use own index;
    public boolean useOwn;
    //the replicated index;
    public IndexNode rpIndex;
    //time slot of that item:
    public int timeSlot;
    //replication string
    public String rpString;
    //control:
    public int control;
    //public
    public boolean hasControl;



    public IndexNode(long fromLabel,long toLabel,long childrenNum,long layer,boolean isIndex){
        this.layer=layer;
        this.fromLabel=fromLabel;
        this.toLabel=toLabel;
        this.childrenNum=childrenNum;
        this.isIndex=isIndex;
        this.children=new ArrayList<IndexNode>();
        this.useReplicationNode=false;
        this.useOwn=true;
        this.rpIndex=null;
        this.rpString="";
        this.hasControl=false;
    }

    public IndexNode(IndexNode in,String rpString){
        this.layer=in.layer;
        this.fromLabel=in.fromLabel;
        this.toLabel=in.toLabel;
        this.childrenNum=in.childrenNum;
        this.isIndex=in.isIndex;
        this.children=new ArrayList<IndexNode>(in.children);
        this.useReplicationNode=false;
        this.useOwn=true;
        this.rpIndex=null;
        this.rpString=rpString;
        this.hasControl=false;
    }

    public String toString(){
        return "["+fromLabel+", "+toLabel+"]";
    }


}
