import java.util.*;
import java.lang.*;

public class FareGenerator
{
	public static void main (String[] args)
    {
		String end = "exit";
		boolean End = false;
		FlightSchedule flightSchedule = new FlightSchedule(); // creates new flight schedule object
		int numOfCities = flightSchedule.NUM_CITIES; // returns nummber of cities
		String [] namesOfCities = flightSchedule.getAllCities();
		int centsInDollar = Money.CENTS; //returns number of cents in a dollar
		Money amount = new Money (0,0); // new money object
		
		do
		{
		System.out.println("Enter 'exit' to quit");
		Scanner scan = new Scanner(System.in);
		System.out.print("from: ");
		String from = scan.nextLine();
		if (from.equals(end))
		{
			System.out.println("Thank you for using the ITEC1620 fare guide");
			System.exit(0);
		}
		System.out.print("to: ");
		String to = scan.nextLine();

		amount = flightSchedule.getFare(from, to);
		
		for (int i=0;i<namesOfCities.length ;i++ )
		{
			if (from != namesOfCities[i] || to != namesOfCities[i])
			{
				System.out.println("There are no available fares with at most one stop between here and there");
				break;
			}
		}

		if (amount != null)
		{
			System.out.println("Direct flight from " + from + " to " + to + " costs " + amount);
		}
		
		Money amountOne;
		Money amountTwo;
		for (String destination : namesOfCities)
		{
			amountOne = flightSchedule.getFare(from, destination);
			amountTwo = flightSchedule.getFare(destination, to);
			if (amountOne != null && amountTwo != null)
			{
				amountOne.addMoney(amountTwo);
				System.out.println("Indirect flight from " + from + " to " + to + " through " 
					+ destination + " costs " + amountOne);
			}

		}
		}
		while (true);
    }
}