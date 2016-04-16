import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by xc9pd on 4/13/2016.
 */
public class Index {

    public long children;
    public long residue;
    public IndexNode root;
    public ArrayDeque<String> allData=new ArrayDeque<String>();
    public long replicationLevel;

    public Index(long children,long ele,long C,long replicationLevel){
        long D=ele*C;
        this.replicationLevel=replicationLevel;
        for(long i=1;i<=D;i++){
            this.allData.add("Data_"+((i-1)/C+1)+"_"+i%C);
        }
        this.children=children;
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
        long replicationLevel=2;
        //initial the index object.
        Index id=new Index(children,ele,C,replicationLevel);







    }

}
