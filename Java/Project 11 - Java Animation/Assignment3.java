//Name: 
//Student ID: 
//Description:

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.applet.Applet;
import java.applet.AudioClip;
import java.io.*;
import java.util.Random;

import javax.sound.sampled.*;
import javax.swing.*;

public class Assignment3 extends JApplet implements Runnable, ActionListener{
	
	private JPanel buttonP;	//JPanel for Button
	private JButton turnB;	//JButton for Turn Red Circle Button
	int x1 = (int)Math.round(Math.random()*100);	//initial x-coordinate of Red circle
	int y1 = (int)Math.round(Math.random()*100);	//initial y-coordinate of Red circle
	int x2 = (int)Math.round(Math.random()*100);	//initial x-coordinate of Green circle
	int y2 = (int)Math.round(Math.random()*100);	//initial y-coordinate of Green circle
	int x3 = (int)Math.round(Math.random()*100);	//initial x-coordinate of Blue circle
	int y3 = (int)Math.round(Math.random()*100);	//initial y-coordinate of Blue circle
	private Random randomGenerator = new Random();	//To determince if circles should move horizontally or vertically
	private int rNum1 = randomGenerator.nextInt(2)+1, //To determince if Red circle move vertically or horizontally
				rNum2 = randomGenerator.nextInt(2)+1, //To determince if Green circle move vertically or horizontally
				rNum3 = randomGenerator.nextInt(2)+1; //To determince if Blue circle move vertically or horizontally
	Thread animator;	//initializing thread animator
	int xSize = 25;		//width/diameter of circle
	int ySize = 25;		//height/diameter of circle
	int width = 200;	//width of applet
	int height = 200;	//height of applet
	AudioClip sound1;	//initializing AudioClip object for Audio.au file
	AudioClip sound2;	//initializing AudioClip object for Audio.au file
	boolean chgDirH1,	//use to change horizontal direction of red circle 
			chgDirV1,	//use to change vertical direction of red circle
			chgDirH2,	//use to change horizontal direction of green circle
			chgDirV2,	//use to change vertical direction of green circle
			chgDirH3,	//use to change horizontal direction of blue circle
			chgDirV3,	//use to change vertical direction of blue circle
			oppAngle;	//use to change movement angle of circles in opposite direction where collision occurs
	int		angle = 0;	//records circles current movement direction/angle
	
	public void paint(Graphics g) {	//method to paint circles
		super.paint(g);	//calls paint method of super class
		Graphics2D b1 = (Graphics2D) g; //declaration and initialization of red circle
		b1.setColor(Color.RED);			//setting color to red for red circle
		b1.drawOval(x1, y1, xSize, ySize);	//drawing red circle with x1, y1 determined and xSize=25 ySize=25
		b1.fillOval(x1, y1, xSize, ySize);	//filling red circle with red color
		
		Graphics2D b2 = (Graphics2D) g;	//declaration and initialization of green circle
		b2.setColor(Color.GREEN);		//setting color to green for green circle
		b2.drawOval(x2, y2, xSize, ySize);	//drawing green circle with x2, y2 determined and xSize=25 ySize=25
		b2.fillOval(x2, y2, xSize, ySize);	//filling green circle with green color
		
		Graphics2D b3 = (Graphics2D) g;	//declaration and initialization of blue circle
		b3.setColor(Color.BLUE);		//setting color to blue for blue circle
		b3.drawOval(x3, y3, xSize, ySize);	//drawing blue circle with x3, y3 determined and xSize=25 ySize=25
		b3.fillOval(x3, y3, xSize, ySize);	//filling blue circle with blue color
	}	
	public void wallBallCollision(){	//method to deal with circles and wall of applet/Frame width or height or boundaries
		if(rNum1 ==1){	//when red circle is moving horizontally
			if(x1<=0){sound1.play();sound2.play(); chgDirH1 =false;}
			if(chgDirH1==false){x1=x1+2;}
			if(x1+xSize>=200){sound1.play();sound2.play(); chgDirH1=true;}
			if(chgDirH1==true){x1=x1-2;}
			}
		
		if(rNum1 ==2){	//when red circle is moving vertically
			if(y1<=0){sound1.play();sound2.play(); chgDirV1=false;}
			if(chgDirV1==false){y1=y1+2;}
			if(y1+ySize>=200){sound1.play();sound2.play(); chgDirV1=true;}
			if(chgDirV1==true){y1=y1-2;}
			}
		
		if(rNum2 ==1){	//when green circle is moving horizontally
			if(x2<=0){sound1.play();sound2.play(); chgDirH2 = false;}
			if(chgDirH2==false){x2=x2+4;}
			if(x2+xSize>=200){sound1.play();sound2.play(); chgDirH2 = true;}
			if(chgDirH2==true){x2=x2-4;}
			}
		if(rNum2 ==2){	//when green circle is moving vertically
			if(y2<=0){sound1.play();sound2.play(); chgDirV2 = false;}
			if(chgDirV2==false){y2=y2+4;}
			if(y2+ySize>=200){sound1.play();sound2.play(); chgDirV2 = true;}
			if(chgDirV2==true){y2=y2-4;}
			}
		
		if(rNum3 ==1){	//when blue circle is moving horizontally
			if(x3<=0){sound1.play();sound2.play(); chgDirH3 = false;}
			if(chgDirH3==false){x3=x3+5;}
			if(x3+xSize>=200){sound1.play();sound2.play(); chgDirH3 = true;}
			if(chgDirH3==true){x3=x3-5;}
			}
		if(rNum3 ==2){	//when blue circle is moving vertically
			if(y3<=0){sound1.play();sound2.play(); chgDirV3 = false;}
			if(chgDirV3==false){y3=y3+5;}
			if(y3+ySize>=200){sound1.play();sound2.play(); chgDirV3 = true;}
			if(chgDirV3==true){y3=y3-5;}
			}
	}
	
	public void ballsCollision(){//method that deal when circles collide together
		if(Math.sqrt(Math.pow(Math.abs(x1-x2), 2) + Math.pow(Math.abs(y1-y2), 2)) <=xSize){//when red circle and green circle collide
			sound1.play();sound2.play();
			chgDirH1 = !chgDirH1;
			chgDirH2 = !chgDirH2;
			chgDirV1 = !chgDirV1;
			chgDirV2 = !chgDirV2;
			oppAngle = !oppAngle;
		}
		
		if(Math.sqrt(Math.pow(Math.abs(x2-x3),2) + Math.pow(Math.abs(y2-y3),2)) <=xSize){//when green circle and blue circle collide
			sound1.play();sound2.play();
			chgDirH2 = !chgDirH2;
			chgDirH3 = !chgDirH3;
			chgDirV2 = !chgDirV2;
			chgDirV3 = !chgDirV3;
		}
		if(Math.sqrt(Math.pow(Math.abs(x1-x3),2) + Math.pow(Math.abs(y1-y3),2)) <=xSize){//when red circle and blue circle collide
			sound1.play();sound2.play();
			chgDirH1 = !chgDirH1;
			chgDirH3 = !chgDirH3;
			chgDirV1 = !chgDirV1;
			chgDirV3 = !chgDirV3;
			oppAngle = !oppAngle;
		}
	}
	
	 public void run() { //overiding run method of interface Runnable
		 while (true) {	//continuous loop
			wallBallCollision(); //calling wallBallCollision method
			ballsCollision();	//calling ballsCollision method
			repaint();	//callin repaint method
		 try {
			 Thread.sleep(100); //delaying thread(animation) by 100milliseconds
		 	 }catch (InterruptedException e) {}
		  }
	 }
	 
	 public void start(){ //overiding start method
		 animator = new Thread(this); //initializing thread
		 animator.start(); //calling start method of thread class
	 }
	 
	 public void init(){ //applet init method
		 setSize(new Dimension(200,200)); //setting applet dimension
		 sound1 = getAudioClip(getDocumentBase(), "Audio1.au"); //loading audio1.au file
		 sound2 = getAudioClip(getDocumentBase(), "Audio2.au");	//loading audio2.au file
		 
		 Container c = getContentPane(); //getting contents for container
		 setLayout(new BorderLayout());	//setting layout for applet
		 turnB = new JButton("Turn Red Circle"); //initializing turn button with label
		 turnB.addActionListener(this);	//adding action listerning of same class to turn button
		 buttonP = new JPanel(); //initializing button panel
		 buttonP.add(turnB); //adding turn button to button panel
		 c.add(buttonP,BorderLayout.SOUTH); //addingbutton panel to container south side
	 }
	 
	 public void actionPerformed(ActionEvent e){ //action performed method
		 if(e.getSource() == turnB){ //when Turn Red Circle is clicked
			 if(angle<315){angle+=45;} else{angle=0;} //increases angle by 45 clockwise
		}
	 }
}
