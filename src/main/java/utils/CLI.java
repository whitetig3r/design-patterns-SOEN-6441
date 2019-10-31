package utils;

import models.Context;
import java.util.*;

/**
 * This singleton utility class handles all the text commands for the game.
 *
 * @author Siddhant Bansal
 */
public class CLI {

  /** The scanner object for reading input from the console. */
  public static Scanner input;

  /** The singleton instance of the CLI class. */
  private static CLI cli = null;

  /** A Map of all the valid commands for a specific Context. */
  static Map<Context, ArrayList<String>> validCommands = new HashMap<>();

  /**
   * The constructor for the CLI class, initiates all the command lists and puts them in the
   * validCommands map.
   */
  private CLI() {
    input = new Scanner(System.in);
  }

  /**
   * A method to get the existing instance of CLI, or creating one if it doesn't exist.
   *
   * @return The instance of the CLI class.
   */
  public static CLI getInstance() {
    if (cli == null) {
      cli = new CLI();
    }
    return cli;
  }

}
