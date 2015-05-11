import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 

public class CardDeck extends JFrame implements ActionListener {
   private CardLayout cardManager;
   private JPanel deck;
   private JButton controls[];
   private String names[] = { "First card", "Next card", "Previous card", "Last card" };

   // set up GUI

   public CardDeck()   {
      super( "CardLayout " );
      Container container = getContentPane();

      // create the JPanel with CardLayout
      deck = new JPanel();
      cardManager = new CardLayout(); 
      deck.setLayout( cardManager );  

      // add cards to JPanel deck
      deck.add( card1Panel(), "c1" );
      deck.add( card2Panel(), "c2" );
      deck.add( card3Panel(), "c3" );

      // create and layout buttons that will control deck
      JPanel buttons = new JPanel();
      buttons.setLayout( new GridLayout( 2, 2 ) );
      controls = new JButton[ names.length ]; 

      for ( int count = 0; count < controls.length; count++  
) {
         controls[ count ] = new JButton( names[ count ] );
         controls[ count ].addActionListener( this );
         buttons.add( controls[ count ] );
      }

      // add JPanel deck and JPanel buttons to the applet
      container.add( buttons, BorderLayout.WEST );
      container.add( deck, BorderLayout.EAST );
      setSize( 450, 200 );
      setVisible( true );
   }  // end constructor


   public JPanel card1Panel(){ 
      JLabel label1 = new JLabel( "card one", SwingConstants.CENTER );
      JPanel card1 = new JPanel();
      card1.add( label1 ); 
      return card1;
   }     

      
   public JPanel card2Panel(){
      JLabel label2 = new JLabel( "card two", SwingConstants.CENTER );
      JPanel card2 = new JPanel();
      card2.setBackground( Color.yellow );
      card2.add( label2 );
      return card2;
   }
   
      
   public JPanel card3Panel(){
      JLabel label3 = new JLabel( "card three" );
      JPanel card3 = new JPanel();
      card3.setLayout( new BorderLayout() );  
      card3.add( new JButton( "North" ), BorderLayout.NORTH);
      card3.add( new JButton( "West" ), BorderLayout.WEST );
      card3.add( new JButton( "East" ), BorderLayout.EAST );
      card3.add( new JButton( "South" ), BorderLayout.SOUTH);
      card3.add( label3, BorderLayout.CENTER );
      return card3;
   } 


   // handle button events by switching cards

   public void actionPerformed( ActionEvent event )   {
      // show first card
      if ( event.getSource() == controls[ 0 ] )    
         cardManager.first( deck ); 
      // show next card
      else if ( event.getSource() == controls[ 1 ] )    
         cardManager.next( deck );  
      // show previous card
      else if ( event.getSource() == controls[ 2 ] )
         cardManager.previous( deck );  
      // show last card  
      else if ( event.getSource() == controls[ 3 ] )
         cardManager.last( deck );            
   }


     public static void main( String args[] )   {
      CardDeck cardDeckDemo = new CardDeck();
      cardDeckDemo.setDefaultCloseOperation(
         JFrame.EXIT_ON_CLOSE );
   }

}  // end class CardDeck

