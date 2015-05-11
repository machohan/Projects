import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import javax.swing.*;

public class TradeCal extends JApplet {
	
	private int				sharesNum;
	private double 			amtIn,amtOut,percentPlus,percentMinus,priceIn,priceOut,feeIn,feeOut,profit,loss, comissionSum;
	private boolean			comissionDeduct=false,sharesNumA=false,amtInA=false,amtOutA=false,percentPlusA=false,
							percentMinusA=false,priceInA=false,priceOutA=false,profitA=false,lossA=false,feeInA=false,
							feeOutA=false,comissionSumA=false;
	
	private JLabel 			sharesNumL,amtInL,amtOutL,percentPlusL,percentMinusL,priceInL,priceOutL,feeInL,feeOutL,profitL,lossL;
	private JTextField		sharesNumT,amtInT,amtOutT,percentPlusT,percentMinusT,priceInT,priceOutT,feeInT,feeOutT,profitT,lossT;
	private JPanel 			mainP,southP,southPW,southPE;
	private JButton 		calculate, reset;
	private JTextArea		notePad;
	private JScrollPane		scrollPane;
	
	public void init(){
		setSize(800,500);
		Container c = getContentPane();
		setLayout(new BorderLayout());
		mainP = new JPanel(new GridLayout(6,2,10,10));
		mainP.setPreferredSize(new Dimension(800,175));
		c.add(mainP,BorderLayout.NORTH);
		
		southP = new JPanel(new BorderLayout(2,2));
		southPW = new JPanel();
		notePad = new JTextArea();
		notePad.setLineWrap(true);
		notePad.setWrapStyleWord(true);
		scrollPane = new JScrollPane(notePad);
		scrollPane.setPreferredSize(new Dimension(790,300));		
		southPW.add(scrollPane);
		southP.add(southPW,BorderLayout.WEST);
		c.add(southP,BorderLayout.SOUTH);
		
		amtInL 			= new JLabel("Amount In($): ");
		amtInT 			= new JTextField(null);
		amtOutL 		= new JLabel("Amount Out($): ");
		amtOutT 		= new JTextField(null);
		percentPlusL 	= new JLabel("Percentage Gain(%): ");
		percentPlusT 	= new JTextField(null);
		percentMinusL 	= new JLabel("Percentage Loss(%): ");
		percentMinusT 	= new JTextField(null);
		priceInL		= new JLabel("Entry Share Price($): ");
		priceInT 		= new JTextField(null);
		priceOutL 		= new JLabel("Exit Share Price($): ");
		priceOutT 		= new JTextField(null);
		feeInL 			= new JLabel("Entry Commission($): ");
		feeInT			= new JTextField(null);
		feeOutL 		= new JLabel("Exit Commission($): ");
		feeOutT 		= new JTextField(null);
		profitL 		= new JLabel("Profit($): ");
		profitT 		= new JTextField(null);
		lossL			= new JLabel("Loss($): ");
		lossT			= new JTextField(null);
		sharesNumL 		= new JLabel("Number of Shares: ");
		sharesNumT 		= new JTextField(null);
		
		mainP.add(amtInL);
		mainP.add(amtInT);
		mainP.add(amtOutL);
		mainP.add(amtOutT);
		mainP.add(percentPlusL);
		mainP.add(percentPlusT);
		mainP.add(percentMinusL);
		mainP.add(percentMinusT);
		mainP.add(priceInL);
		mainP.add(priceInT);
		mainP.add(priceOutL);
		mainP.add(priceOutT);
		mainP.add(feeInL);
		mainP.add(feeInT);
		mainP.add(feeOutL);
		mainP.add(feeOutT);
		mainP.add(profitL);
		mainP.add(profitT);
		mainP.add(lossL);
		mainP.add(lossT);
		mainP.add(sharesNumL);
		mainP.add(sharesNumT);
		
		reset = new JButton("Reset");
		reset.setPreferredSize(new Dimension(100,50));
		calculate = new JButton("Calculate");
		calculate.setPreferredSize(new Dimension(100,50));
		mainP.add(reset, new FlowLayout());
		mainP.add(calculate, new FlowLayout());
		
		ButtonsHandler handlerB = new ButtonsHandler();
		reset.addActionListener(handlerB);
		calculate.addActionListener(handlerB);
		
		Validation validationT = new Validation();
		amtInT.addKeyListener(validationT);
		amtOutT.addKeyListener(validationT);
		percentPlusT.addKeyListener(validationT);
		percentMinusT.addKeyListener(validationT);
		priceInT.addKeyListener(validationT);
		priceOutT.addKeyListener(validationT);
		feeInT.addKeyListener(validationT);
		feeOutT.addKeyListener(validationT);
		profitT.addKeyListener(validationT);
		lossT.addKeyListener(validationT);
		sharesNumT.addKeyListener(validationT);
	}
	
	private class Validation implements KeyListener{
		public void keyTyped(KeyEvent e) {	
			char c = e.getKeyChar();
		      if (!(Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE) || (c == KeyEvent.VK_PERIOD))) {
		        getToolkit().beep();
		        e.consume();}
		      if( c == KeyEvent.VK_ESCAPE){
		    	  amtInT.setText(null);amtOutT.setText(null);
		    	  percentPlusT.setText(null);percentMinusT.setText(null);
		    	  priceInT.setText(null);priceOutT.setText(null);
		    	  feeInT.setText(null);feeOutT.setText(null);
		    	  profitT.setText(null);lossT.setText(null);
		    	  sharesNumT.setText(null);
		    	  amtIn=0;amtOut=0;percentPlus=0;percentMinus=0;
		    	  priceIn=0;priceOut=0;feeIn=0;feeOut=0;
		    	  profit=0;loss=0;comissionSum=0;sharesNum=0;
		    	  comissionDeduct=false;sharesNumA=false;amtInA=false;amtOutA=false;percentPlusA=false;percentMinusA=false;priceInA=false;
		    	  priceOutA=false;feeInA=false;feeOutA=false;profitA=false;lossA=false;comissionSumA=false;
				
		      }
		}
		public void keyPressed(KeyEvent arg0) {}
		public void keyReleased(KeyEvent arg0) {}
	}
	
	private class ButtonsHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			
			if(event.getSource() == reset){
				amtInT.setText(null);amtOutT.setText(null);
		    	percentPlusT.setText(null);percentMinusT.setText(null);
		    	priceInT.setText(null);priceOutT.setText(null);
		    	feeInT.setText(null);feeOutT.setText(null);
		    	profitT.setText(null);lossT.setText(null);
		    	sharesNumT.setText(null);
		    	amtIn=0;amtOut=0;percentPlus=0;percentMinus=0;
		    	priceIn=0;priceOut=0;feeIn=0;feeOut=0;
		    	profit=0;loss=0;comissionSum=0;sharesNum=0;
		    	comissionDeduct=false;sharesNumA=false;amtInA=false;amtOutA=false;percentPlusA=false;percentMinusA=false;priceInA=false;
		    	priceOutA=false;feeInA=false;feeOutA=false;profitA=false;lossA=false;comissionSumA=false;
			}
			
			if(event.getSource() == calculate){
				if(!sharesNumT.getText().isEmpty()){sharesNum = Math.abs(Integer.parseInt(sharesNumT.getText()));sharesNumA=true;}
				if(!amtInT.getText().isEmpty()){amtIn = Math.abs(Double.parseDouble(amtInT.getText()));amtInA=true;}
				if(!amtOutT.getText().isEmpty()){amtOut = Math.abs(Double.parseDouble(amtOutT.getText()));amtOutA=true;}
				if(!percentPlusT.getText().isEmpty()){percentPlus = Math.abs(Double.parseDouble(percentPlusT.getText()));percentPlusA=true;}
				if(!percentMinusT.getText().isEmpty()){percentMinus = Math.abs(Double.parseDouble(percentMinusT.getText()));percentMinusA=true;}
				if(!priceInT.getText().isEmpty()){priceIn = Math.abs(Double.parseDouble(priceInT.getText()));priceInA=true;}
				if(!priceOutT.getText().isEmpty()){priceOut = Math.abs(Double.parseDouble(priceOutT.getText()));priceOutA=true;}
				if(!feeInT.getText().isEmpty()){feeIn = Math.abs(Double.parseDouble(feeInT.getText()));feeInA=true;}
				if(!feeOutT.getText().isEmpty()){feeOut = Math.abs(Double.parseDouble(feeOutT.getText()));feeOutA=true;}
				if(!profitT.getText().isEmpty()){profit = Math.abs(Double.parseDouble(profitT.getText()));profitA=true;}
				if(!lossT.getText().isEmpty()){loss = Math.abs(Double.parseDouble(lossT.getText()));lossA=true;}
				if(!feeInT.getText().isEmpty() || !feeOutT.getText().isEmpty()){comissionSum = feeIn + feeOut; comissionSumA=true;feeInA=true;feeOutA=true;}
							
//				****Loss($) calculations****
				if(lossT.getText().isEmpty()){
				if( percentMinusA==true && amtInA==true){
					lossT.setText(String.valueOf(((percentMinus/100)*amtIn)-profit + (comissionSum)));
				} 				
				else if( priceInA==true && priceOutA==true && sharesNumA==true){
					if(((priceIn-priceOut)*sharesNum)- profit +(comissionSum) >=0){lossT.setText(String.valueOf(((priceIn-priceOut)*sharesNum) - profit +(comissionSum)));}
				}
				else if( amtInA==true && amtOutA==true){
					if(((amtIn - amtOut) -profit +(comissionSum)) >=0){lossT.setText(String.valueOf( (amtIn - amtOut) -profit +(comissionSum) ));}
				}}
				
//			****Profit($) Calculations****
				if(profitT.getText().isEmpty()){
				if( percentPlusA==true && amtInA==true){
					if((comissionSum) > (((percentPlus/100) *amtIn)-loss) ){lossT.setText(String.valueOf((comissionSum) -loss -((percentPlus/100) *amtIn)));}
					else{profitT.setText(String.valueOf(((percentPlus/100) *amtIn) - loss - (comissionSum)));}
				}
				else if( priceOutA==true && priceInA==true && sharesNumA==true){
					if( (comissionSum) >  (((priceOut-priceIn)*sharesNum) -loss)){lossT.setText(String.valueOf((comissionSum) -loss - ((priceOut-priceIn)*sharesNum) ));}
					else {profitT.setText(String.valueOf((((priceOut-priceIn)*sharesNum) -loss - (comissionSum))));}
				}
				else if( amtOutA==true && amtInA==true){
					if((comissionSum) > (amtOut - amtIn - loss)){lossT.setText(String.valueOf((comissionSum) -loss - (amtOut - amtIn) ));}
					else{profitT.setText(String.valueOf( amtOut - amtIn -loss - (comissionSum) ));}
				}}

//				****AmountIn Calculations****
				if(amtInT.getText().isEmpty()){
				if(priceInA==true && sharesNumA==true){
					amtInT.setText(String.valueOf(priceIn*sharesNum));
				}
				else if(priceOutA==true && sharesNumA==true){
					if(profitA==true && profit-loss < (priceOut*sharesNum)){amtInT.setText(String.valueOf(priceOut*sharesNum+loss-profit));}
				}
				else if(amtOutA==true && percentPlusA==true && percentPlus<=100){
						if(percentPlus==100){amtInT.setText(String.valueOf(0.5*amtOut));}
						else{amtInT.setText(String.valueOf(amtOut-(percentPlus/100)*amtOut));}
				}
				else if(amtOutA==true && percentMinusA==true){
					amtInT.setText(String.valueOf(amtOut+(percentMinus/100)*amtOut));
				}
				else if(amtOutA==true && profitA==true && profit<amtOut){
					amtInT.setText(String.valueOf(amtOut-profit));
				}
				else if(amtOutA==true && lossA==true){
					amtInT.setText(String.valueOf(amtOut+loss));
				}}
				
//				****AmountOut Calculations****
				if(amtOutT.getText().isEmpty()){
				if(priceOutA==true && sharesNumA==true){
					if(profitA==true || lossA==true){
						if(comissionDeduct==false){amtOutT.setText(String.valueOf((priceOut*sharesNum+profit-loss)-comissionSum)); comissionDeduct=true;}
						else{amtOutT.setText(String.valueOf(priceOut*sharesNum+profit-loss));}
					}
					else{
						if(comissionDeduct==false){amtOutT.setText(String.valueOf(priceOut*sharesNum-comissionSum)); comissionDeduct=true;}
						else{amtOutT.setText(String.valueOf(priceOut*sharesNum));}
					}
				}
				else if(priceInA==true && sharesNumA==true){
					if(profitA==true || lossA==true){
						if(comissionDeduct==false){amtOutT.setText(String.valueOf((priceIn*sharesNum)+profit -loss -comissionSum)); comissionDeduct=true;}
						else{amtOutT.setText(String.valueOf((priceIn*sharesNum)+profit -loss ));}
					}
					else{
						if(comissionDeduct==false){amtOutT.setText(String.valueOf(priceIn*sharesNum-comissionSum)); comissionDeduct=true;}
						else{amtOutT.setText(String.valueOf(priceIn*sharesNum));}
					}
				}
				else if(amtInA==true && (percentPlusA==true || percentMinusA==true)){
					if(comissionDeduct==false){amtOutT.setText(String.valueOf(amtIn + ((percentPlus/100)*amtIn) - ((percentMinus/100)*amtIn)-comissionSum)); comissionDeduct=true;}
					else{amtOutT.setText(String.valueOf(amtIn + ((percentPlus/100)*amtIn)- ((percentMinus/100)*amtIn) ));}
				}
				else if(amtInA==true && (profitA==true || lossA==true)){
					if(comissionDeduct==false){amtOutT.setText(String.valueOf(amtIn+profit-loss-comissionSum)); comissionDeduct=true;}
					else{amtOutT.setText(String.valueOf(amtIn+profit-loss));}
				}}

//				 ****Percentage Gain(%) Calculations****
				if(percentPlusT.getText().isEmpty()){
				if(priceOutA==true && sharesNumA==true && priceInA==true){
					if((((((priceOut*sharesNum)-comissionSum) - (priceIn*sharesNum)) / (priceIn*sharesNum)) * 100) >=0 && Double.isInfinite(((((priceOut*sharesNum) - ((priceIn*sharesNum)+comissionSum)) / (priceIn*sharesNum)) * 100))==false ){
						percentPlusT.setText(String.valueOf(         ((((priceOut*sharesNum)-comissionSum) - (priceIn*sharesNum)) / (priceIn*sharesNum)) * 100));
					}
				}
				else if(amtOutA==true && amtInA==true){
					if(((amtOut+profit-loss-(amtIn+comissionSum)) / (amtIn+comissionSum))*100 >=0 && Double.isInfinite(((amtOut+profit-loss-(amtIn+comissionSum)) / amtIn)*100)==false){
						percentPlusT.setText(String.valueOf( ( (amtOut+profit-loss-(amtIn+comissionSum)) / (amtIn+comissionSum))*100));
					}
				}
				else if(amtInA==true && profitA==true){
					if((((profit-loss-comissionSum)/amtIn)*100) >=0 && Double.isInfinite((((profit-loss-comissionSum)/amtIn)*100))==false){
						percentPlusT.setText(String.valueOf(   ((profit-loss-comissionSum)/amtIn)*100    ));
					}
				}
				else if(priceOutA==true && priceInA==true){
					if( (((priceOut-priceIn)/priceIn) *100) >=0 && Double.isInfinite((((priceOut-priceIn)/priceIn) *100))==false){
						percentPlusT.setText(String.valueOf(    ((priceOut-priceIn)/priceIn) *100));
					}
				}}
//				 ****Percentage Loss(%) Calculations****
				if(percentMinusT.getText().isEmpty()){
				if(priceInA==true && sharesNumA==true && priceOutA==true){
					if((((((priceOut*sharesNum)-comissionSum) - (priceIn*sharesNum)) / (priceIn*sharesNum)) * 100) <=0){
						percentMinusT.setText(String.valueOf( Math.abs(((((priceOut*sharesNum)-comissionSum) - (priceIn*sharesNum)) / (priceIn*sharesNum)) * 100)) );}
				}
				else if(amtInA==true && amtOutA==true){
					if( (((amtOut+profit-comissionSum)-amtIn) / amtIn )*100 <=0 && Double.isInfinite((((amtOut+profit-comissionSum)-amtIn) / amtIn )*100 )==false ){
						percentMinusT.setText(String.valueOf(Math.abs((((amtOut+profit-comissionSum)-amtIn) / amtIn )*100)  ));}
				}
				else if(amtInA==true && lossA==true){
					if((((loss+comissionSum)/amtIn)*100) >=0 && Double.isInfinite(((loss+comissionSum)/amtIn)*100)==false ){
						percentMinusT.setText(String.valueOf( ((loss+comissionSum)/amtIn)*100 ));}
				}
				else if(priceInA==true && priceOutA==true){
					if((((priceIn-priceOut)/priceOut)*100) >=0 && Double.isInfinite(((priceIn-priceOut)/priceOut)*100 )==false){
						percentMinusT.setText(String.valueOf( ((priceIn-priceOut)/priceOut)*100 ));}
				}}
				
//				 ****Number of Shares Calculations****	
				if(sharesNumT.getText().isEmpty()){
				if(amtInA==true && priceInA==true){
					sharesNumT.setText(String.valueOf((amtIn-comissionSum)/priceIn));
				}
				else if(amtOutA==true && priceInA==true){
					sharesNumT.setText(String.valueOf(((amtOut-((percentPlus/100)*amtOut)-profit+((percentMinus/100)*amtOut)+loss+comissionSum)/priceIn)));
					amtInT.setText("");
				}}

//				 ****Entry Share Price Calculations****
				if(priceInT.getText().isEmpty()){
				if(amtOutA==true && sharesNumA==true && amtInA==true && (lossA==true || profitA==true)){
					if(amtOut>=amtIn){priceInT.setText(String.valueOf( (amtIn/sharesNum) - (amtOut/sharesNum) + (loss/sharesNum) - (profit/sharesNum) ));}
					else{priceInT.setText(String.valueOf( (amtIn/sharesNum) + (amtOut/sharesNum) + (loss/sharesNum) - (profit/sharesNum) ));}
				}
				else if(amtOutA==true && sharesNumA==true && (percentPlusA==true || percentMinusA==true)){
					priceInT.setText(String.valueOf((amtOut/sharesNum)-((percentPlus/100)*(amtOut/sharesNum)) +((percentMinus/100)*(amtOut/sharesNum))));
				}
				else if(priceOutA==true && amtInA==true && (profitA==true || lossA==true)){
					priceInT.setText(String.valueOf(priceOut-(priceOut*(profit/amtIn))+(priceOut*(loss/amtIn))));
				}
				else if(priceOutA==true && (percentPlusA==true || percentMinusA==true)){
					priceInT.setText(String.valueOf(priceOut-((percentPlus/100)*priceOut)+((percentMinus/100)*priceOut)));
				}
				else if(amtInA==true && sharesNumA==true){
					priceInT.setText(String.valueOf(amtIn/sharesNum));
				}}
//				 ****Exit Share Price Calculations****
				if(priceOutT.getText().isEmpty()){
				if(amtInA==true && amtOutA==true && sharesNumA==true){
					if(amtOut>=amtIn){priceOutT.setText(String.valueOf(((amtOut-amtIn)/sharesNum) + (amtIn/sharesNum) ));}
					if(amtOut<amtIn){priceOutT.setText(String.valueOf( (amtIn/sharesNum) - ((amtIn-amtOut)/sharesNum) ));}
				}
				else if(sharesNumA==true && amtInA==true && (percentPlusA==true || percentMinusA==true)){
					priceOutT.setText(String.valueOf((amtIn/sharesNum)+((percentPlus/100)*(amtIn/sharesNum))-((percentMinus/100)*(amtIn/sharesNum))));
				}
				else if(sharesNumA==true && amtInA==true && (lossA==true || profitA==true)){
					priceOutT.setText(String.valueOf( (amtIn/sharesNum) - ((loss/amtIn)*(amtIn/sharesNum)) + ((profit/amtIn)*(amtIn/sharesNum)) ));
				}
				else if(priceInA==true && amtInA==true && amtOutA==true){
					if(amtOut>=amtIn){priceOutT.setText(String.valueOf(priceIn+(((amtOut-amtIn)/amtIn)*priceIn)));}
					if(amtOut<amtIn){priceOutT.setText(String.valueOf(priceIn+(((amtOut-amtIn)/amtIn)*priceIn)));}
				}
				else if(priceInA==true && amtInA==true && (lossA==true || profitA==true)){
					priceOutT.setText(String.valueOf(priceIn-((loss/amtIn)*priceIn) + ((profit/amtIn)*priceIn) ));
				}
				else if(priceInA==true && (percentPlusA==true || percentMinusA==true)){
					priceOutT.setText(String.valueOf(priceIn+((percentPlus/100)*priceIn)-((percentMinus/100)*priceIn)));
				}}
			}
		}
	}
}
