package source;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import driver.MyParser;

public class CoAuthorsNamesExtractor {

  /**
   * Method to convert a hashSet containing all the names of co-authors
   * mentioned in all html files given in argument to a TreeSet to print
   * names is alphabetical order.
   */
  public static StringBuilder extractCoAuthorsNames(){
    StringBuilder sb = new StringBuilder();
    Set<String> treeSet = new TreeSet<String>(MyParser.getCoAuthorsNamesSet());
    Iterator<String> coAuthorsItr = treeSet.iterator();
    while(coAuthorsItr.hasNext()){
      sb.append("\t" + coAuthorsItr.next() + "\n");
    }
    return sb;
  }
}
