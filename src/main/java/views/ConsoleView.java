package views;

/**
 * View for cli
 */
public class ConsoleView {

  /**
   * Displays text to the console
   *
   * @param text string to display
   */
  public static void display(String text) {
    System.out.println("-> " + text);
  }
}
