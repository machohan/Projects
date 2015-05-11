package source;

import driver.MyParser;

/**
 * @author Ammar
 *
 */
public class Presentor {

  /* 
   * This method calls methods of other classes to get required information
   * from webpages. General exception is thrown when for example file cannot be
   * opened or read or array of out bound exception.
   */
  
  /**
   * @param args
   */
  public static StringBuilder getScholarInfo(String[] args) {
    StringBuilder sb = new StringBuilder();
    
    // A loop get get all information from each file given in the argument.
    String inputFiles[] = args[0].split(",");
    for (String inputFile : inputFiles) {
      sb.append("-------------------------------------------------" + "\n");

      // prints out the name of the author.
      sb.append("1. Name of the Author:" + "\n");
      String scholarName = AuthorNameExtractor.extractAuthorsName(inputFile);
      sb.append("\t" + scholarName + "\n");

      // prints out the number of all citations.
      sb.append("2. Number of All Citations:" + "\n");
      String numCit = NumCitationsExtractor.extractNumCitations(inputFile);
      sb.append("\t" + numCit + "\n");

      // prints out i10-index after 2009.
      sb.append("3. Number of i10-index after 2009:" + "\n");
      String numITen = ITenIndexExtractor.extractNumITenIndex(inputFile);
      sb.append("\t" + numITen + "\n");

      // prints out the title of first three publications.
      sb.append("4. Title of the first 3 publications:" + "\n"
          + PublicationsExtractor.extractPublications(inputFile));

      // prints out total citations of first 5 papers.
      sb.append("5. Total paper citation(first 5 papers):" + "\n" 
          + "\t"  + TotalCitationsExtractor.extractTotalCitations(inputFile) 
          + "\n");

      // prints out total number of co-Authors.
      sb.append("6. Total Co-Authors:" + "\n" + "\t"
          + TotalCoAuthorsExtractor.extractTotalCoAuthors(inputFile) + "\n");

      /*
       * When loop is done extracting information from last html file, total 
       * number of co-authors for all files in argument gets printed and along 
       * with names of the co-authors.
       */
      if (inputFile == inputFiles[inputFiles.length - 1]) {
        sb.append("--------------------------------------" 
              + "-----------" + "\n");
        sb.append("7. Co-Author list sorted (Total: "
            + MyParser.getCoAuthorsNamesSet().size() + "):" + "\n");
        sb.append(CoAuthorsNamesExtractor.extractCoAuthorsNames());
      }
    }
    return sb;
  }
}
