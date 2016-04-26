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
    //
    public ArrayList<IndexNode> schedule;
    //global index
    public int globalIndex;
    public long totalElement;

    public Index(long children,long ele,long C,long replicationLevel){
        this.totalElement=ele;
        globalIndex=0;
        schedule=new ArrayList<IndexNode>();
        allData=new ArrayDeque<String>();
        //total time slot for all the data.
        long D=ele*C;
        this.replicationLevel=replicationLevel;
        //generating the data need to be broadcasting.format: Data_Label_timeslot;
        for(long i=0;i<D;i++){
            this.allData.add("Data_"+(i/C+1)+"_"+(i%C+1));
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
                String rp=label.substring(label.indexOf("_"));
                label=label.substring(0,label.indexOf("_"));
               //System.out.println(label);
                long labelLong=Long.parseLong(label);

                IndexNode data=new IndexNode(labelLong,labelLong,0,totalLevel+1,false);
                data.rpString=rp;
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
                String rp=label.substring(label.indexOf("_"));
                label=label.substring(0,label.indexOf("_"));

                System.out.println(label);



                //System.out.println(label);
                long labelLong=Long.parseLong(label);
                IndexNode data=new IndexNode(labelLong,labelLong,0,totalLevel+1,false);
                data.rpString=rp;
                leaf.children.add(data);
            }
            leafNodes.add(leaf);
        }

        //System.out.println(leafNum);
/*
        for(IndexNode lf:leafNodes){
            System.out.println("leaf nodes from: "+lf.fromLabel+" to "+lf.toLabel);
        }
      */
        //assign all index
        assignTree(root,children,totalLevel-1,0,leafNodes.size()-1,leafNodes);

        System.out.println(totalLevel);

        ArrayDeque<IndexNode> help=new ArrayDeque<IndexNode>();
        help.add(root);
        while(!help.isEmpty()){
            IndexNode helpNode=help.poll();
            System.out.println("Tree Node "+helpNode.toString()+helpNode.rpString);
            for(IndexNode ii:helpNode.children){
                help.add(ii);
            }
        }

    }

    public void replicateToLowerLevel(IndexNode roots){
        if(replicationLevel==0)return;

            roots.useOwn=false;
            roots.useReplicationNode=true;
        if(roots.layer<replicationLevel) {
            int i = 1;
            for (IndexNode in : roots.children) {

                in.rpString = "_" + i;
                in.rpIndex =new IndexNode(roots,in.rpString);
                i++;

                replicateToLowerLevel(in);
                System.out.println(in.toString()+": "+in.rpIndex.toString() + in.rpString);
            }
        }

    }


    public void assignTree(IndexNode root,long children,long levelLeft,int left, int right,ArrayList<IndexNode> leafNodes){
        System.out.println("AssigningTree "+root.toString()+root.rpString+" left: "+left+" right: "+right+" LevelLeft: "+levelLeft);
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
                assignTree(middle,children,levelLeft,s,e,leafNodes);
            }

        }
    }

    public void broadcast(IndexNode roots){
        if(roots.useReplicationNode&&roots.rpIndex==null){
            for(IndexNode in:roots.children){
                broadcast(in);
            }
        }
        else if(roots.useReplicationNode&&roots.rpIndex!=null&&roots.layer<replicationLevel){
            this.globalIndex++;
            this.schedule.add(roots.rpIndex);
            ArrayList<IndexNode> buf=new ArrayList<IndexNode>();
            for(IndexNode tt:roots.rpIndex.children){
                if(!tt.children.isEmpty())
                buf.add(tt.children.get(0).rpIndex);
            }
            roots.rpIndex.children.clear();
            for(IndexNode tt:buf){
                roots.rpIndex.children.add(tt);
            }


            roots.rpIndex.timeSlot=globalIndex;
            for(IndexNode in:roots.children){
                broadcast(in);
            }

        }
        else if(roots.useReplicationNode&&roots.rpIndex!=null&&roots.layer==replicationLevel){
            this.globalIndex++;
            this.schedule.add(roots.rpIndex);
            roots.rpIndex.timeSlot=globalIndex;
            ArrayDeque<IndexNode> nodeQueue=new ArrayDeque<IndexNode>();
            nodeQueue.add(roots);
            while(!nodeQueue.isEmpty()){
                IndexNode buffer=nodeQueue.poll();
                this.globalIndex++;
                this.schedule.add(buffer);
                schedule.get(globalIndex-1).timeSlot=globalIndex;
                for(IndexNode in:buffer.children){
                    nodeQueue.add(in);
                }
            }



        }

    }

    public void setControl(){
        String initial=this.root.toString().substring(1);
        long secondRange=(totalElement/children)-2;
        int end=this.schedule.size()+1;
        for(int i=end-2;i>0;i--){
            IndexNode n=schedule.get(i);
            if(n.toLabel-n.fromLabel>=secondRange&&n.toString().indexOf(initial)!=1){
                n.hasControl=true;
                n.control=end;
            }
            if(n.toString().indexOf(initial)==1)end=i+1;
        }
    }

    public ArrayList<IndexNode> findData(int probe,long destination){
        int mod=schedule.size();
        ArrayList<IndexNode> result=new ArrayList<IndexNode>();

        int in=probe-1;

        boolean breaks=true;
        while(breaks){
            System.out.println(in+1);

            if(schedule.get(in).toLabel==destination&&schedule.get(in).fromLabel==destination&&schedule.get(in).rpString.equals("_1")){
                result.add(schedule.get(in));
                breaks=false;
            }
            else if(schedule.get(in).isIndex&&destination<=schedule.get(in).toLabel&&destination>=schedule.get(in).fromLabel&&
                    schedule.get(in).toLabel!=schedule.get(in).fromLabel){
                int i=0;
                result.add(schedule.get(in));
                boolean get=true;

                System.out.println(schedule.get(in).toString());

                while(get){


                    IndexNode node=schedule.get(in).children.get(i);
                    if(destination>=node.fromLabel&&destination<=node.toLabel){
                        in=node.timeSlot-1;
                        get=false;
                        System.out.println("find");
                    }

                   System.out.println(node.toString());

                    i++;
                }
            }
            else if(schedule.get(in).isIndex&&schedule.get(in).hasControl){
                result.add(schedule.get(in));
                in=schedule.get(in).control-1;
            }
            else{
                result.add((schedule.get(in)));
                in=(in+1)%mod;
            }
        }
        return result;

    }



}
