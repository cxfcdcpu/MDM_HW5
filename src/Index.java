import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by xc9pd on 4/13/2016.
 */
public class Index {
    //number of children every nodes usually have
    public long children;
    //if the number of data block is not exactly match the number of leaf index, then some leaf indexNode will contain more data.
    public long residue;
    //the root of this index object.
    public IndexNode root;
    //store all data the broadcast have. In this program we automatecally generate data.
    public ArrayDeque<String> allData;
    //the level of which level need to be replicated. 0 means only replicate the root. plus 1 will move the level lower.
    public long replicationLevel;
    //minimum leafnode data item number
    public long leafItemNum;
    //total level
    public long totalLevel;


    public Index(long children,long ele,long C,long replicationLevel){
        allData=new ArrayDeque<String>();
        //total time slot for all the data.
        long D=ele*C;
        this.replicationLevel=replicationLevel;
        //generating the data need to be broadcasting.format: Data_Label_timeslot;
        for(long i=1;i<=D;i++){
            this.allData.add("Data_"+((i-1)/C+1)+"_"+i%C);
        }
        //the number of child of every node
        this.children=children;
        //below are calculating the residue.
        long k=Math.round(Math.log(ele)/Math.log(children));
        long leafNum=(long)Math.pow(children,k-1);
        this.residue=(ele)%leafNum;
        this.leafItemNum=ele/leafNum;
        long rootItem=children;
        if(children<ele)rootItem=ele;
        this.totalLevel=children>ele?1:k;

        //set root;
        this.root=new IndexNode(1,ele,rootItem,0,true);

        //set leaf Index
        ArrayList<IndexNode> leafNodes=new ArrayList<IndexNode>();
        for(int i=1;i<=leafNum-residue;i++){
            IndexNode leaf=new IndexNode(leafItemNum*(i-1)+1,leafItemNum*i,leafItemNum*C,totalLevel,true);
            for(int j=0;j<leafItemNum*C;j++){
                String dataString=this.allData.poll();
                String label=dataString.substring(5);
                label=label.substring(0,label.indexOf("_"));
               //System.out.println(label);
                long labelLong=Long.parseLong(label);

                IndexNode data=new IndexNode(labelLong,labelLong,0,totalLevel+1,false);
                leaf.children.add(data);
            }
            leafNodes.add(leaf);
        }
        for(int i=0;i<residue;i++){
            IndexNode leaf=new IndexNode((leafNum-residue)*leafItemNum+i*(leafItemNum+1)+1,
                    (leafNum-residue)*leafItemNum+(i+1)*(leafItemNum+1),(leafItemNum+1)*C,totalLevel,true);
            for(int j=0;j<(leafItemNum+1)*C;j++){
                String dataString=this.allData.poll();
                String label=dataString.substring(5);
                label=label.substring(0,label.indexOf("_"));
                //System.out.println(label);
                long labelLong=Long.parseLong(label);
                IndexNode data=new IndexNode(labelLong,labelLong,0,totalLevel+1,false);
                leaf.children.add(data);
            }
            leafNodes.add(leaf);
        }

        //System.out.println(leafNum);
        for(IndexNode lf:leafNodes){
            System.out.println("leaf nodes from: "+lf.fromLabel+" to "+lf.toLabel);
        }



        //assign all index
        assignTree(root,children,totalLevel-1,0,leafNodes.size()-1,leafNodes);


    }

    public void assignTree(IndexNode root,long children,long levelLeft,int left, int right,ArrayList<IndexNode> leafNodes){
        if (levelLeft==0)return;
        levelLeft--;
        int n=(int)((right-left+1)/children);
        for(int i=0;i<children;i++){
            int s=left+i*n;
            int e=s+n-1;
            long startLabel=leafNodes.get(s).fromLabel;
            long endLabel=leafNodes.get(e).toLabel;
            if(n==1)root.children.add(leafNodes.get(s));
            else{
                IndexNode middle=new IndexNode(startLabel,endLabel,children,root.layer+1,true);
                root.children.add(middle);
                assignTree(middle,children,levelLeft-1,s,e,leafNodes);
            }

        }
    }








    public static void main(String[] arg){
        //C is the average time slot for a item
        long C=9;
        //ele is the number of total items.
        long ele=75;
        //children is the number of children of a index member
        long children=3;
        //replication level: if 1 then 1 level below the root need to be replicated.
        long replicationLevel=1;
        //initial the index object.
        Index id=new Index(children,ele,C,replicationLevel);

        System.out.println(id.root.children.get(2).fromLabel);







    }

}
