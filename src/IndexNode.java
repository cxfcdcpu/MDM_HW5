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
    public long[] children;
    //check if the node a data node or an index node;
    public boolean isIndex;

    public IndexNode(long fromLabel,long toLabel,long childrenNum,long layer,boolean isIndex){
        this.layer=layer;
        this.fromLabel=fromLabel;
        this.toLabel=toLabel;
        this.childrenNum=childrenNum;
        
    }



}
