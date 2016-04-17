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


    public Index(long children,long ele,long C,long replicationLevel){
        allData=new ArrayDeque<String>();
        //total time slot for all the data.
        long D=ele*C;
        this.replicationLevel=replicationLevel;
        //generating the data need to be broadcasting.
        for(long i=1;i<=D;i++){
            this.allData.add("Data_"+((i-1)/C+1)+"_"+i%C);
        }
        //the number of child of every node
        this.children=children;
        //below are calculating the residue.
        long k=Math.round(Math.log(ele)/Math.log(children));
        long leafNum=(long)Math.pow(children,k-1);
        if((long)Math.pow(children,k)<ele)
        this.residue=(ele)%leafNum;
        else this.residue=0;

    }








    public static void main(String[] arg){
        //C is the average time slot for a item
        long C=9;
        //ele is the number of total items.
        long ele=81;
        //children is the number of children of a index member
        long children=3;
        //replication level: if 1 then 1 level below the root need to be replicated.
        long replicationLevel=1;
        //initial the index object.
        Index id=new Index(children,ele,C,replicationLevel);







    }

}
