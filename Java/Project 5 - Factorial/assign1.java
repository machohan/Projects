import java.io.*;

// User enters integer N.  The program calculates N factorial.
//
public class assign1
{
  public static void main (String[] args ) throws IOException
  {
    BufferedReader userin = new BufferedReader
        (new InputStreamReader(System.in));
    String inputData;
    int    N, fact = 1;

    System.out.println( "Enter The number of which you want to find factorial of: " );
    inputData = userin.readLine();
    N         = Integer.parseInt( inputData );

    if ( N >= 0 )
    {
      while ( N > 1 )
      {
        fact = fact * N;
        N    = N - 1;
      }
      System.out.println( "factorial is " + fact );
    }
    else
    {
      System.out.println("N must be zero or greater");
    }
  }
}