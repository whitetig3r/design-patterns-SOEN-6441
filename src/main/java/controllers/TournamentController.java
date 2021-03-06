package controllers;

import models.Context;
import models.GameMap;
import models.player.PlayerStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static views.ConsoleView.display;

/**
 * This is the Controller for the Tournament
 *
 * @author Warren White
 * @author Siddharth Singh
 * @version 1.0
 */
public class TournamentController {
  /** a property holding the max. number of turns for each game */
  public static int maxNumberOfTurnsProperty;

  /**
   * a map that holds the results table
   */
  public static Map<String, ArrayList<String>> resultTable = new HashMap<>();

  /**
   * starts the tournament based on command
   *
   * @param gameMap the GameMap instance / the reference to game map
   * @param command the command
   * @return successful / failed execution of the tournament
   */
  public static boolean startTournament(GameMap gameMap, String command) {
    String[] commandSplitOne = command.split(" -M ");
    String[] commandSplitTwo = commandSplitOne[1].split(" -P ");
    String[] commandSplitThree = commandSplitTwo[1].split(" -G ");

    String[] listOfMapFiles = commandSplitTwo[0].split(" ");
    String[] listOfPlayerStrategies = commandSplitThree[0].split(" ");
    int numberOfGames = Integer.parseInt(commandSplitThree[1].split(" -D ")[0]);
    maxNumberOfTurnsProperty = Integer.parseInt(commandSplitThree[1].split(" -D ")[1]);
    for (String map : listOfMapFiles) {
      for (int gameIndex = 0; gameIndex < numberOfGames; gameIndex++) {
        gameMap = GameMap.getGameMap();
        GameController.isTournament = true;
        // set players
        // Add index to game player names to make them unique
        StringBuilder stringBuilder = new StringBuilder("gameplayer");
        for (int idx = 0; idx < listOfPlayerStrategies.length; idx++) {
          String strategy = listOfPlayerStrategies[idx];
          stringBuilder.append(
              String.format(" -add %s-%d %s", strategy.toUpperCase(), idx, strategy));
        }
        String gamePlayerCmd = stringBuilder.toString();
        MainController.processGamePlayerCommand(gameMap, gamePlayerCmd);
        // load map
        String loadMapCmd = String.format("loadmap %s", map);
        if (!MainController.processLoadMapCommand(gameMap, loadMapCmd)) {
          GameMap.destroyGameMap();
          continue;
        }
        SetupController.processPopulateCountriesCommand(gameMap, null);
        StartUpController.processPlaceAllCommand(gameMap, null);
        GameMap.numberOfRounds = 0;
        GameMap.numOfTurns = 0;
        while (!GameMap.isGameOver && GameMap.numberOfRounds < maxNumberOfTurnsProperty) {
          GameController.startPhaseLoop(GameMap.getGameMap());
          GameMap.numOfTurns++;
        }
        String winner;
        if (GameMap.numberOfRounds >= TournamentController.maxNumberOfTurnsProperty) {
          display("Turns Exceeded! Ending the current game", true);
          winner = "Draw";
        } else {
          PlayerStrategy strategy = gameMap.getCurrentPlayer().getStrategy();
          winner = strategy.getPlayerName();
        }
        // save the winners name
        if (!resultTable.containsKey(map)) {
          resultTable.put(map, new ArrayList<String>());
        }
        resultTable.get(map).add(winner);
        GameMap.destroyGameMap();
      }
    }
    // pretty print the results
    System.out.println("RESULTS TABLE");
    for(Map.Entry<String, ArrayList<String>> entry:resultTable.entrySet()){
      System.out.println(String.format("%s -> %s",entry.getKey(), String.join(" ", entry.getValue())));
    }
    return true;
  }
}
