import javax.swing.*;
import java.awt.*;

/**
 * Created by xc9pd on 4/25/2016.
 */
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;



public class Demo extends JFrame implements ActionListener{

    public JLabel jl1;
    public JLabel jl2;
    public JLabel jl3;
    public JLabel jl4;
    public JTextField jtf1;
    public JTextField jtf2;
    public JTextField jtf3;
    public JTextField jtf4;

    public JLabel jl5;
    public JLabel jl6;
    public JTextField jtf5;
    public JTextField jtf6;

    public JButton jb2;
    public JButton jb1;

    public String eleS;
    public String CS;
    public String childrenS;
    public String replicationLevelS;
    public String probeS;
    public String destS;

    public int t;
    public ArrayList<IndexNode> sd;
    public ArrayList<IndexNode> bd;
    public Demo(String name){
        super(name);
        this.t=0;
        Container c=this.getContentPane();
         jl1=new JLabel("Number of data");
         jl2=new JLabel("time slot each data item occupy");
         jl3=new JLabel("number of children of each node of the index tree");
         jl4=new JLabel("replication Level");
         jtf1=new JTextField("81",5);
         jtf2=new JTextField("3",5);
         jtf3=new JTextField("3",5);
         jtf4=new JTextField("2",5);

         jl5=new JLabel("Probe time slot");
         jl6=new JLabel("Data item what to find");
         jtf5=new JTextField("55",5);
         jtf6=new JTextField("66",5);

         jb2=new JButton("find");
         jb1=new JButton("create schedule");


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c.setLayout(new FlowLayout());
        this.setSize(new Dimension(1400,760));
        c.setSize(new Dimension(1400,760));



        c.add(jl1);
        c.add(jtf1);

        c.add(jl2);
        c.add(jtf2);

        c.add(jl3);
        c.add(jtf3);

        c.add(jl4);
        c.add(jtf4);

        c.add(jb1);

        c.add(jl5);
        c.add(jtf5);

        c.add(jl6);
        c.add(jtf6);

        c.add(jb2);

        jb1.addActionListener(this);
        jb2.addActionListener(this);
        this.setVisible(true);

    }

    public void paint(Graphics g) {
        Color[] col=new Color[7];
        col[0]=Color.RED;
        col[1]=Color.blue ;
        col[2]= Color.green;
        col[3]= Color.yellow;
        col[4]= Color.cyan;
        col[5]=Color.gray ;
        col[6]= Color.MAGENTA;
        int x=30;
        int y=80;
        //g.drawRect (x, y, 1300, 600);
        //g.drawString(sd.get(0).toString(),x+40,y+40);
        if(t==1){
            int i=0;

            for(IndexNode in:this.sd){

                //System.out.println("draw");
                int p=(i)%22;
                int q=i/22+1;
                //this.setBackground(col[(int)in.layer]);
                String out=in.fromLabel==in.toLabel?""+in.fromLabel+in.rpString:in.toString()+in.rpString;
                g.drawString(out,x+60*p,y+50*q);
                i++;

            }


        }

        if(t==2){
            int i=0;

            for(IndexNode in:this.sd){

                //System.out.println("draw");
                int p=(i)%22;
                int q=i/22+1;
                //this.setBackground(col[(int)in.layer]);
                String out=in.fromLabel==in.toLabel?""+in.fromLabel+in.rpString:in.toString()+in.rpString;
                g.drawString(out,x+60*p,y+50*q);
                i++;

            }

            for(IndexNode in:this.bd){

                //System.out.println("draw");
                int p=(in.timeSlot-1)%22;
                int q=(in.timeSlot-1)/22+1;
                //this.setBackground(col[(int)in.layer]);
                g.fillOval(x+60*p+10,y+50*q-30,10,10);

                try{
                    Thread.sleep(1000);
                }catch (InterruptedException ee) {
                        System.out.println("error");
                }

            }


        }



    }

    public void actionPerformed(ActionEvent e){


        if(e.getSource()==this.jb1){
            t=1;
            System.out.println("good");
             eleS=jtf1.getText();
            //ele=Long.parseLong(eleS);
             CS=jtf2.getText();
            // C=Long.parseLong(CS);
             childrenS=jtf3.getText();
            //children=Long.parseLong(childrenS);
             replicationLevelS=jtf4.getText();
            // replicationLevel=Long.parseLong(replicationLevelS);

            long ele=eleS.length()==0?0:Long.parseLong(eleS);
            long C=CS.length()==0?0:Long.parseLong(CS);
            long children=childrenS.length()==0?0:Long.parseLong(childrenS);
            long replicationLevel=replicationLevelS.length()==0?0:Long.parseLong(replicationLevelS);

            Index id=new Index(children,ele,C,replicationLevel);
            //replicate
            id.replicateToLowerLevel(id.root);

            id.broadcast(id.root);
            id.setControl();
            for(IndexNode in:id.schedule){
                if(in.hasControl)System.out.print("next rootIndex is: "+in.control);
                System.out.println(in.toString()+in.rpString+" current timeslot is: "+in.timeSlot);
            }
            //System.out.println(id.root.children.get(2).fromLabel);

            this.sd=id.schedule;




        }
        else{
            t=2;
            System.out.println("good");
            eleS=jtf1.getText();
            //ele=Long.parseLong(eleS);
            CS=jtf2.getText();
            // C=Long.parseLong(CS);
            childrenS=jtf3.getText();
            //children=Long.parseLong(childrenS);
            replicationLevelS=jtf4.getText();
            // replicationLevel=Long.parseLong(replicationLevelS);

            long ele=eleS.length()==0?0:Long.parseLong(eleS);
            long C=CS.length()==0?0:Long.parseLong(CS);
            long children=childrenS.length()==0?0:Long.parseLong(childrenS);
            long replicationLevel=replicationLevelS.length()==0?0:Long.parseLong(replicationLevelS);

            Index id=new Index(children,ele,C,replicationLevel);
            //replicate
            id.replicateToLowerLevel(id.root);

            id.broadcast(id.root);
            id.setControl();
            for(IndexNode in:id.schedule){
                if(in.hasControl)System.out.print("next rootIndex is: "+in.control);
                System.out.println(in.toString()+in.rpString+" current timeslot is: "+in.timeSlot);
            }
           // System.out.println(id.root.children.get(2).fromLabel);

            this.sd=id.schedule;
            probeS=jtf5.getText();
            destS=jtf6.getText();
            int probe=Integer.parseInt(probeS );
            long dest=Long.parseLong(destS);
            this.bd=id.findData(probe,dest);

        }


        repaint();
        this.setVisible(true);
    }






}
