import java.util.*;
public class assign3
{
    public static void main (String [] args)
    {      
        Random generator = new Random ();
        int dice1Player1;
        int dice2Player1;
        int dice1Player2;
        int dice2Player2;
        int player1Sum =0;
        int player2Sum = 0;
        int highest = 75;
        boolean win = false;

        do 
        {
			do
            {
                dice1Player1 = generator.nextInt(6) +1;
                dice2Player1 = generator.nextInt(6) +1;
                
                    player1Sum = player1Sum +(dice1Player1 + dice2Player1);
                    System.out.println("Player 1 rolls a " + dice1Player1 + " and a " + dice2Player1);
                    System.out.println("Player 1 now has " + player1Sum);
                    
                    if ((dice1Player1 == dice2Player1) && ((dice1Player1 != 1 && dice2Player1 != 1) && (dice1Player1 != 6 && dice2Player1 != 6)))
                    {
                        System.out.println("Player 1 gets to roll again");
                    }
					if ((dice1Player1 == dice2Player1) && ((dice1Player1 == 1 && dice2Player1 == 1) || (dice1Player1 == 6 && dice2Player1 == 6)))
					{
						System.out.println("Player 1 loses a turn");
					}

                    if (player1Sum >= highest)
                    {
                        win = true;
                        System.out.println("Player 1 wins with a total of " + player1Sum);
                    }
                
           }
           while ((!win) && (dice1Player1 == dice2Player1) && ((dice1Player1 != 1 && dice2Player1 != 1) && (dice1Player1 != 6 && dice2Player1 != 6)));

           if (!win)
           {
			do
            {
                dice1Player2 = generator.nextInt(6) +1;
                dice2Player2 = generator.nextInt(6) +1;
                
                    player2Sum = player2Sum +(dice1Player2 + dice2Player2);
                    System.out.println("Player 2 rolls a " + dice1Player2 + " and a " + dice2Player2);
                    System.out.println("Player 2 now has " + player2Sum);
                    
                    if ((dice1Player2 == dice2Player2) && ((dice1Player2 != 1 && dice2Player2 != 1) && (dice1Player2 != 6 && dice2Player2 != 6)))
                    {
                        System.out.println("Player 2 gets to roll again");
                    }
					if ((dice1Player2 == dice2Player2) && ((dice1Player2 == 1 && dice2Player2 == 1) || (dice1Player2 == 6 && dice2Player2 == 6)))
					{
						System.out.println("Player 2 loses a turn");
					}

                    if (player2Sum >= highest)
                    {
                        win = true;
                        System.out.println("Player 2 wins with a total of " + player2Sum);
                    }
                
           }
           while ((!win) && (dice1Player2 == dice2Player2) && ((dice1Player2 != 1 && dice2Player2 != 1) && (dice1Player2 != 6 && dice2Player2 != 6)));
		}
	}
        while ((player1Sum < highest) && (player2Sum < highest));

}
}
