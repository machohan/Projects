import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.StringTokenizer;
import javax.imageio.*;
import javax.swing.*;


public class App extends JApplet
{
	private JButton f1,f2,f3, del, clr, btn0,btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9,str,end;
	private JLabel status;
	private JTextField number;
	private JPanel left, middle, right; 
	private JComboBox contacts;
	private BufferedReader input;
	private FileInputStream file;
	private String line, firstN, lastN, phoneNum;
	private StringTokenizer token;
	private String[] names, numbers;
	private int countNames=1, countNums=1;
	
	public static void main(String[] args){
		JFrame mainF = new JFrame("Phone Application");
		mainF.setSize(new Dimension(500,400));
		mainF.setResizable(false);
		JApplet applet = new App();
		applet.init();
		mainF.add(applet);
		mainF.setVisible(true);
	}
	public void init()
	{
		setSize(500,400);
		Container c = getContentPane();
		setLayout(new BorderLayout());
		
		Font font = new Font("Verdana", Font.PLAIN, 15);
		number = new JTextField("");
		number.setFont(font);
		number.setEditable(false);
		c.add(number,BorderLayout.NORTH);
		
		left = new JPanel(new GridLayout(3,1,2,2));
		f1 = new JButton(new ImageIcon("pic1.jpg"));
		f1.setPreferredSize(new Dimension(100,100));
		left.add(f1);
		f2 = new JButton(new ImageIcon("pic2.jpg"));
		f2.setPreferredSize(new Dimension(100,100));
		left.add(f2);
		f3 = new JButton(new ImageIcon("pic3.jpg"));
		f3.setPreferredSize(new Dimension(100,100));
		left.add(f3);
		c.add(left,BorderLayout.WEST);
		
		middle = new JPanel();
		middle.setLayout(new GridLayout(4,3,5,5));
		btn1 = new JButton("1");
		btn2 = new JButton("2");
		btn3 = new JButton("3"); 
		btn4 = new JButton("4"); 
		btn5 = new JButton("5"); 
		btn6 = new JButton("6"); 
		btn7 = new JButton("7"); 
		btn8 = new JButton("8"); 
		btn9 = new JButton("9"); 
		btn0 = new JButton("0"); 
		del = new JButton("Del"); 
		clr = new JButton("Clr"); 
		middle.add(btn7);
		middle.add(btn8);
		middle.add(btn9);
		middle.add(btn4);
		middle.add(btn5);
		middle.add(btn6);
		middle.add(btn1);
		middle.add(btn2);
		middle.add(btn3);
		middle.add(del);
		middle.add(btn0);
		middle.add(clr);
		c.add(middle,BorderLayout.CENTER);
		
		right = new JPanel(new GridLayout(3,1,2,2));
		str = new JButton(new ImageIcon("dial.jpg"));
		str.setPreferredSize(new Dimension(100,100));
		right.add(str);
		end = new JButton(new ImageIcon("hangup.jpg"));
		end.setPreferredSize(new Dimension(100,100));
		right.add(end);
		status = new JLabel(""); 
		status.setFont(font);
		right.add(status);
		c.add(right,BorderLayout.EAST);
		
		try 
		{
			names = new String[15];
			numbers = new String[15];
			file = new FileInputStream("contacts.txt");
			input = new BufferedReader(new InputStreamReader(file));
			line = input.readLine();
			while(line != null){
			token = new StringTokenizer(line," ");
				firstN = token.nextToken();
				lastN = token.nextToken();
				phoneNum = token.nextToken();
				
				names[countNames++] = firstN + " " + lastN;
				numbers[countNums++] = phoneNum;
				line = input.readLine();
			}
		} 	
		catch (IOException e) {e.printStackTrace();}
		finally {try {file.close();} catch (IOException e) {e.printStackTrace();}}

		contacts = new JComboBox(names);
		c.add(contacts,BorderLayout.SOUTH);		
		
		ButtonsHandler handlerB = new ButtonsHandler();
		btn0.addActionListener(handlerB);
		btn1.addActionListener(handlerB);
		btn2.addActionListener(handlerB);
		btn3.addActionListener(handlerB);
		btn4.addActionListener(handlerB);
		btn5.addActionListener(handlerB);
		btn6.addActionListener(handlerB);
		btn7.addActionListener(handlerB);
		btn8.addActionListener(handlerB);
		btn9.addActionListener(handlerB);
		del.addActionListener(handlerB);
		clr.addActionListener(handlerB);
		str.addActionListener(handlerB);
		end.addActionListener(handlerB);
		f1.addActionListener(handlerB);
		f2.addActionListener(handlerB);
		f3.addActionListener(handlerB);
		contacts.addActionListener(handlerB);
	}
	
	
	private class ButtonsHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event){
			if(event.getSource() == btn0){number.setText(number.getText() + "0");}
			if(event.getSource() == btn1){number.setText(number.getText() + "1");}
			if(event.getSource() == btn2){number.setText(number.getText() + "2");}
			if(event.getSource() == btn3){number.setText(number.getText() + "3");}
			if(event.getSource() == btn4){number.setText(number.getText() + "4");}
			if(event.getSource() == btn5){number.setText(number.getText() + "5");}
			if(event.getSource() == btn6){number.setText(number.getText() + "6");}
			if(event.getSource() == btn7){number.setText(number.getText() + "7");}
			if(event.getSource() == btn8){number.setText(number.getText() + "8");}
			if(event.getSource() == btn9){number.setText(number.getText() + "9");}
			if(event.getSource() == del){number.setText(number.getText().substring(0, number.getText().length()-1));}
			if(event.getSource() == clr){number.setText("");}
			
			if(event.getSource() == f1){
				number.setText(numbers[1]);
				contacts.setSelectedIndex(1);
				status.setText("Dailing...");}
			if(event.getSource() == f2){
				number.setText(numbers[2]);
				contacts.setSelectedIndex(2);
				status.setText("Dailing...");}
			if(event.getSource() == f3){
				number.setText(numbers[3]);
				contacts.setSelectedIndex(3);
				status.setText("Dailing...");}
			
			if(event.getSource() == str){status.setText("Dailing...");}
			if(event.getSource() == end){
				number.setText("");
				contacts.setSelectedIndex(-1);
				status.setText("Disconnected");}
			
			if(event.getSource() == contacts){
				if(contacts.getSelectedIndex()>=0)
				number.setText(numbers[contacts.getSelectedIndex()]);
				status.setText("Dailing...");
			}
		}
	}
}
	
