import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

//Student class starts
class Student
{
	private String sdtN, prgmN, u1, u2, u3;
	private boolean uni1, uni2, uni3;

	public Student (String sN, String pN, String uOne, String uTwo, String uThree)
	{
		sdtN = sN;
		prgmN = pN;
		u1 = uOne;
		u2 = uTwo;
		u3 = uThree;
	}

	public String getName()
	{return sdtN;}
	public String getProg()
	{return prgmN;}
	public String getU1()
	{return u1;}
	public String getU2()
	{return u2;}
	public String getU3()
	{return u3;}

	public String getUni1()
	{ 
		if (uni1 == true)
		{return "admitted";}
		else 
		{return "rejected";}
	}
	
	public String getUni2()
	{ 
		if (uni2 == true)
		{return "admitted";}
		else 
		{return "rejected";}
	}

	public String getUni3()
	{ 
		if (uni3 == true)
		{return "admitted";}
		else 
		{return "rejected";}
		
	}
	
	public void setUni1(boolean acptRjt)
	{ 
		if (acptRjt == true)
		{uni1 = true;}
		else
		{uni1 = false;}
	}

	public void setUni2(boolean acptRjt)
	{ 
		if (acptRjt == true)
		{uni2 = true;}
		else
		{uni2 = false;}
	}
	
	public void setUni3(boolean acptRjt)
	{ 
		if (acptRjt == true)
		{uni3 = true;}
		else
		{uni3 = false;}
	}
	
	public String toString()
	{
		return sdtN + " " + prgmN + " " + u1 + " " + u2 + " " + u3;
	}

}// Student class ends


//UnderGraduate class starts
class Undergrad extends Student
{
	private double hSchoolAvgMark;

	public Undergrad(String sN, String pN, String uOne, String uTwo, String uThree, double hSAMark)
	{
		super(sN, pN, uOne, uTwo, uThree);
		hSchoolAvgMark = hSAMark;
	}

	public double getHSAMark()
	{
		return hSchoolAvgMark;
	}

	public String toString()
	{
		return super.getName() + "," + " average="+getHSAMark() + ", " + super.getProg()+"," 
			+ " Undergrad, " + super.getU1() + "-" + super.getUni1() + ", "+ super.getU2() + "-" + super.getUni2() + ", "
		     +  super.getU3() + "-" + super.getUni3() + ".\n";
	}
}// //UnderGraduate class ends

//PostGraduate class starts
class Postgrad extends Student
{
	private double uGradAvgMark;
	private String degreeType;
	
	public Postgrad(String sN, String pN, String uOne, String uTwo, String uThree, double uGradAM, String degree)
	{
		super(sN, pN, uOne, uTwo, uThree);
		uGradAvgMark = uGradAM;
		degreeType = degree;
	}

	public double getUGradAvgMark()
	{
		return uGradAvgMark;
	}

	public String getDegreeType()
	{
		return degreeType;
	}

	public String toString()
	{
		//Mary Jones, average=90, History, PhD, York-admitted, UofT-admitted, Brock-admitted
		return super.getName() + "," + " average="+getUGradAvgMark() + ", " + super.getProg()+", " 
			+ getDegreeType()+ ", " + super.getU1() + "-" + super.getUni1() + ", "+ super.getU2() + "-" + super.getUni2() + ", "
		     +  super.getU3() + "-" + super.getUni3() + ".\n";
	}

}//PostGraduate class ends

//Applet Application starts
public class ApplicationCentre extends JApplet
{
	private JButton inputB, admitB, disAllB, disOneB, inputSubmitB, admitSubmitB;	//declared encapsulated buttons
	private JPanel buttonsP, panelsDeck, inputP, fieldsPanel, listPanel, admitP, admitPanels, admitP1,admitP2, admitP3, disAllP, disOneP, disOneP1, disOneP2; //declared encapsulated panels
	private JLabel stdNameL, progL, avgMarkL, postGradDegTypeL, selectUnis, uni1, uni2, uni3, disOneEnterL; //declared encapsulated labels - first four used in used in inputP(Input Panel)
	private JTextField stdNameTF, progTF, avgMarkTF, postGradDegTypeTF, disOneTF; //declared encapsulated textfields - all used in inputP(Input Panel)
	private JRadioButton  accept[] = new JRadioButton [3];
	private JRadioButton  reject[] = new JRadioButton [3];
	private ButtonGroup  rButtonGroup[] = new ButtonGroup[3];
	private JTextArea displayAllTA, displayOneTA;
	private JList unis; // encapsulated declaration of JList object - used in inputP - contains list of universities
	private JComboBox stdNamesCB;
	private CardLayout panelsManager; //CardLayout object encapsulated declaration
	private int countStudents =0; //encapsulated declaration and initialized value of -1 to indicate no student has been enterred
	Student stdRecord [] = new Student [100];	//declared an array of 100 indexs which will contain refs to various objects
	String stdNames [] = new String[stdRecord.length];
	String [] unisList = {"Toronto", "York", "Western", "Brock", "Guelph", 
		"Waterloo", "McGill", "Concordia", "Laval", "Macmaster"}; //declaration and initialization of array contain refs to strings objects containing names of universities - array ref used as parameter for JList

//=======================================================================================================================================================================================================================
	public void init()
	{
		Container c = getContentPane();
		c.setLayout(new GridLayout (1,2,15,15));
		
		//creation of Buttons panel, buttons, addition of buttons into Buttons Panel and addition of Buttons panel into container
		buttonsP = new JPanel (new GridLayout (4,1)); 
		inputB = new JButton ("Input");
		buttonsP.add(inputB);
		admitB = new JButton ("Admit");
		buttonsP.add(admitB);
		disAllB = new JButton ("Display All");
		buttonsP.add(disAllB);
		disOneB = new JButton ("Display One");
		buttonsP.add(disOneB);
		c.add(buttonsP);
		
		//creation of panels deck, initialization of CardLayout, setting deck layout to CardLayout. Creation of all Panels in deck, their addition into the deck and deck addtion into the container
		panelsDeck = new JPanel();
		panelsManager = new CardLayout();
		panelsDeck.setLayout(panelsManager);
		
		inputP = new JPanel();
		panelsDeck.add(inputP, "inputPanel");
		c.add(panelsDeck, BorderLayout.EAST);
		
		//creation of inputP(input Panel) starts
		//panel inside inputP called fieldsPanel - GridLayout 5rows 1column
		//declaring and initializing labels and textfields and adding them to fieldsPanel.
		fieldsPanel = new JPanel (new GridLayout(5,1));
		fieldsPanel.setPreferredSize(new Dimension(340, 100));
		stdNameL = new JLabel ("Enter Student Name: ");
		fieldsPanel.add(stdNameL);
		stdNameTF = new JTextField ();
		fieldsPanel.add(stdNameTF);
			
		progL = new JLabel ("Enter Choosen Program: ");
		fieldsPanel.add(progL);
		progTF = new JTextField ();
		fieldsPanel.add(progTF);
			
		avgMarkL = new JLabel ("Average Mark: ");
		fieldsPanel.add(avgMarkL);
		avgMarkTF = new JTextField ();
		fieldsPanel.add(avgMarkTF);
			
		postGradDegTypeL = new JLabel ("Type of Postgrad Degree: ");
		fieldsPanel.add(postGradDegTypeL);
		postGradDegTypeTF = new JTextField ();
		fieldsPanel.add(postGradDegTypeTF);		
		
		inputP.add(fieldsPanel); //adding fieldsPanel to input Panel

		selectUnis = new JLabel ("Please Select 3 Universities: ");
		fieldsPanel.add(selectUnis); //new Label in fields Panel

		unis = new JList (unisList);
		unis.setFixedCellWidth(300);
		unis.setFixedCellHeight(20);
		inputP.add(unis); //list of unis into input Panel

		inputSubmitB = new JButton ("Submit");
		inputSubmitB.setPreferredSize(new Dimension (300,20));
		inputP.add(inputSubmitB); //adding submit button in input Panel - creation of input panel ends
		
		//creation of admitP(admit Panel) starts
		admitP = new JPanel();
		stdNamesCB = new JComboBox (stdNames);
		stdNamesCB.setPreferredSize(new Dimension (300,20));
		JLabel comboBoxL = new JLabel ("Please select your name from Drop down list");
		admitP.add(comboBoxL);
		admitP.add(stdNamesCB);
		panelsDeck.add(admitP, "admitPanel");

		admitPanels = new JPanel(new GridLayout(3,1,20,20));
		admitP.add(admitPanels, BorderLayout.SOUTH);

		admitSubmitB = new JButton ("Submit");
		admitSubmitB.setPreferredSize(new Dimension (350,20));
		admitP.add(admitSubmitB);
		admitSubmitB.setVisible(false);

		disAllP = new JPanel();
		panelsDeck.add(disAllP, "displayAllPanel");

		disOneP = new JPanel();		
		panelsDeck.add(disOneP, "displayOnePanel");

		ButtonsHandler handlerB = new ButtonsHandler();
		inputB.addActionListener(handlerB);
		admitB.addActionListener(handlerB);
		disAllB.addActionListener(handlerB);
		disOneB.addActionListener(handlerB);
		inputSubmitB.addActionListener(handlerB);
		admitSubmitB.addActionListener(handlerB);
		
		ComboBoxHandler handlerCB = new ComboBoxHandler();
		stdNamesCB.addItemListener(handlerCB);

		for (int i=0; i<3;i++)
		{
			rButtonGroup [i] = new ButtonGroup ();
			accept[i] = new JRadioButton("Accept", false);
			reject[i] = new JRadioButton("Reject", true);
			rButtonGroup[i].add(accept[i]);
			rButtonGroup[i].add(reject[i]);
		}

		disAllP = new JPanel();	
		displayAllTA = new JTextArea();
		Font font1 = new Font("Serif", Font.ITALIC, 12);
		displayAllTA.setFont(font1);
		JScrollPane scroll = new JScrollPane(displayAllTA);
		scroll.setVerticalScrollBarPolicy( JScrollPane. VERTICAL_SCROLLBAR_ALWAYS );
		scroll.setHorizontalScrollBarPolicy( JScrollPane. HORIZONTAL_SCROLLBAR_ALWAYS );
		disAllP.add(scroll);
		scroll.setPreferredSize(new Dimension(330, 485));
		panelsDeck.add(disAllP, "displayAllPanel");

		disOneP = new JPanel();
		disOneP1 = new JPanel();
		disOneEnterL = new JLabel ("Enter student's name");
		disOneP1.add(disOneEnterL);
		disOneTF = new JTextField();
		disOneTF.setPreferredSize(new Dimension (200,20));
		disOneTF.addActionListener(handlerB);
		disOneP1.add(disOneTF);
		disOneP.add(disOneP1, BorderLayout.NORTH);


		disOneP2 = new JPanel();
		displayOneTA = new JTextArea();
		Font font2 = new Font("Arial",Font.PLAIN, 14);
		displayOneTA.setFont(font2);
		displayOneTA.setEditable(false);
		
		JScrollPane scroll2 = new JScrollPane(displayOneTA);
		scroll2.setVerticalScrollBarPolicy( JScrollPane. VERTICAL_SCROLLBAR_ALWAYS );
		scroll2.setHorizontalScrollBarPolicy( JScrollPane. HORIZONTAL_SCROLLBAR_ALWAYS );
		disOneP.add(scroll2, BorderLayout.SOUTH);
		scroll2.setPreferredSize(new Dimension(330, 450));
		panelsDeck.add(disOneP, "displayOnePanel");

	}

//=======================================================================================================================================================================================================================
	private class ButtonsHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if(event.getSource() == inputB)
			{
				panelsManager.show(panelsDeck, "inputPanel");
			}
			if(event.getSource() == admitB){
				panelsManager.show(panelsDeck, "admitPanel");
			}
			if(event.getSource() == disAllB)
			{
				panelsManager.show(panelsDeck, "displayAllPanel");
				displayAllTA.setText(null);
						Student hold;
		for (int pass=0; pass<countStudents-1; pass++)
		{
			for (int s=0;s<=countStudents-2; s++)
			{
				if (stdRecord[s] instanceof Undergrad && stdRecord[s+1] instanceof Undergrad)
				{
					if ( ((Undergrad)stdRecord[s]).getHSAMark()<((Undergrad)stdRecord[s+1]).getHSAMark())
					{
						hold = stdRecord[s];
						stdRecord[s] = stdRecord[s+1];
						stdRecord[s+1] = hold;
					}
				}
				if (stdRecord[s] instanceof Postgrad && stdRecord[s+1] instanceof Postgrad)
				{
					if ( ((Postgrad)stdRecord[s]).getUGradAvgMark()<((Postgrad)stdRecord[s+1]).getUGradAvgMark())
					{
						hold = stdRecord[s];
						stdRecord[s] = stdRecord[s+1];
						stdRecord[s+1] = hold;
					}
				}
				if (stdRecord[s] instanceof Undergrad && stdRecord[s+1] instanceof Postgrad)
				{
					if ( ((Undergrad)stdRecord[s]).getHSAMark() < ((Postgrad)stdRecord[s+1]).getUGradAvgMark() )
					{
						hold = stdRecord[s];
						stdRecord[s] = stdRecord[s+1];
						stdRecord[s+1] = hold;
					}
				}
				if (stdRecord[s] instanceof Postgrad && stdRecord[s+1] instanceof Undergrad)
				{
					if ( ((Postgrad)stdRecord[s]).getUGradAvgMark() < ((Undergrad)stdRecord[s+1]).getHSAMark() )
					{
						hold = stdRecord[s];
						stdRecord[s] = stdRecord[s+1];
						stdRecord[s+1] = hold;
					}
				}
			}
		}

				for (int x=0;x<=countStudents-1 ;x++ )
				{
					if (stdRecord[x] instanceof Undergrad)
					{
						displayAllTA.append(stdRecord[x].toString());
					}
					if (stdRecord[x] instanceof Postgrad)
					{
						displayAllTA.append(stdRecord[x].toString());
					}
				}
			}
			if(event.getSource() == disOneB){
				displayOneTA.setText(null);
				disOneTF.setText(null);
				panelsManager.show(panelsDeck, "displayOnePanel");
			}

			if(event.getSource() == inputSubmitB)
			{
				boolean created = false;
				int [] selectedIdx = unis.getSelectedIndices();
				if (selectedIdx.length !=3)
				{
					String message1 = "Select ONLY 3 universities";
					JOptionPane.showMessageDialog(null, message1);
				}
				else if (selectedIdx.length ==3)
				{
					Object [] unisListSelected = unis.getSelectedValues();
					String [] selectedUnis = new String[unisListSelected.length];
					for (int i=0;i<selectedUnis.length; i++ ){
						selectedUnis[i] = (String) unisListSelected[i];
					}
					
					if (postGradDegTypeTF.getText().equals("")){
						stdRecord[countStudents] = new Undergrad(stdNameTF.getText(), progTF.getText(), selectedUnis[0], selectedUnis[1], selectedUnis[2], 
							Double.parseDouble(avgMarkTF.getText()));
						created = true;
					
					}
					else if (Double.parseDouble(avgMarkTF.getText())>= 70.0 && Double.parseDouble(avgMarkTF.getText())< 80.0 && postGradDegTypeTF.getText().equals("masters"))
					{
						stdRecord[countStudents] = new Postgrad(stdNameTF.getText(), progTF.getText(), selectedUnis[0], selectedUnis[1], selectedUnis[2], 
							Double.parseDouble(avgMarkTF.getText()), postGradDegTypeTF.getText());
						stdRecord[countStudents].setUni1(true); stdRecord[countStudents].setUni2(true); stdRecord[countStudents].setUni3(true);
						created = true;

					}
					else if (Double.parseDouble(avgMarkTF.getText())>= 80.0)
					{
						stdRecord[countStudents] = new Postgrad(stdNameTF.getText(), progTF.getText(), selectedUnis[0], selectedUnis[1], selectedUnis[2], 
							Double.parseDouble(avgMarkTF.getText()), postGradDegTypeTF.getText());
						stdRecord[countStudents].setUni1(true); stdRecord[countStudents].setUni2(true); stdRecord[countStudents].setUni3(true);
						created = true;
					}
				}
				if (created == true)
				{
					stdNames[countStudents] = stdRecord[countStudents].getName();
					if (stdRecord[countStudents] instanceof Undergrad)
					{
						stdNamesCB.insertItemAt(stdNames[countStudents], countStudents);
					}
					countStudents++;
					String message2 = new String ("Student " + countStudents + " out of 100");
					JOptionPane.showMessageDialog(null, message2);
					stdNameTF.setText(null);
					progTF.setText(null);
					avgMarkTF.setText(null);
					postGradDegTypeTF.setText(null);	
				}
				else
				{
					String message3 = new String ("Average mark for Masters must be atleast 70. Average mark for PhD must be atleast 80");
					JOptionPane.showMessageDialog(null, message3);
				}
			}
			
			
			if (event.getSource() == admitSubmitB)
			{
				setAcceptance(stdNamesCB.getSelectedIndex());
			}

			if(event.getSource() == disOneTF)
			{
				int found = 0;
				for (int y=0; y<countStudents; y++)
				{
					
					if (stdRecord[y].getName().compareTo(disOneTF.getText()) == 0)
					{
						found++;
						if (stdRecord[y] instanceof Undergrad)
						{
							displayOneTA.append(stdRecord[y].toString());
						}
						if (stdRecord[y] instanceof Postgrad)
						{
							displayOneTA.append(stdRecord[y].toString());
						}
					}
				}
				if (found ==0)
				{
					displayOneTA.append("Student not found\n");
				}

			}

		}

	}
//=======================================================================================================================================================================================================================
	private class ComboBoxHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent event)
		{
			if(event.getStateChange() == ItemEvent.SELECTED)
			{
				admitPanels.setVisible(true);
				admitP1 = new JPanel();
				admitPanels.add(admitP1, BorderLayout.NORTH);
				uni1 = new JLabel (stdRecord[stdNamesCB.getSelectedIndex()].getU1());
				admitP1.add(uni1);
				admitP1.add(accept[0]);
				admitP1.add(reject[0]);
				
				admitP2 = new JPanel();
				admitPanels.add(admitP2, BorderLayout.CENTER);
				uni2 = new JLabel (stdRecord[stdNamesCB.getSelectedIndex()].getU2());
				admitP2.add(uni2);
				admitP2.add(accept[1]);
				admitP2.add(reject[1]);
				
				admitP3 = new JPanel();
				admitPanels.add(admitP3, BorderLayout.SOUTH);
				uni3 = new JLabel (stdRecord[stdNamesCB.getSelectedIndex()].getU3());
				admitP3.add(uni3);
				admitP3.add(accept[2]);
				admitP3.add(reject[2]);
				admitSubmitB.setVisible(true);
			}
			if(event.getStateChange() == ItemEvent.DESELECTED)
			{
				admitPanels.removeAll();
				admitSubmitB.setVisible(false);
				admitPanels.setVisible(false);
				reject[0].setSelected(true);
				reject[1].setSelected(true);
				reject[2].setSelected(true);
			}

		}

	}
//=======================================================================================================================================================================================================================
public void setAcceptance(int n){
	for (int k=0;k<3 ;k++ )
	{
		if (k==0)
		{
			if (accept[k].isSelected())
				{stdRecord[n].setUni1(true);}
			else
				{stdRecord[n].setUni1(false);}
		}
		if (k==1)
		{
			if (accept[k].isSelected())
				{stdRecord[n].setUni2(true);}
			else
				{stdRecord[n].setUni2(false);}
		}
		if (k==2)
		{
			if (accept[k].isSelected())
				{stdRecord[n].setUni3(true);}
			else
				{stdRecord[n].setUni3(false);}
		}
	}
}

}//Applet Application ends
/*
Tested Data
Undergrad
Ammar
ITEC
80

Chloe
Medical
70

David
Natural Science
60

Postgrad
Amir
Computer Science
90
phd

Shayan
Engineering
70
masters*/
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
