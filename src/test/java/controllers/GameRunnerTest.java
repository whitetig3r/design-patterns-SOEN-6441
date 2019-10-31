package controllers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import models.GameMap;
import org.junit.Before;
import org.junit.Test;

public class GameRunnerTest {

  /**
   * players in the game should either be more than 2 and less than 6
   */
  String reason;

  ArrayList<String> playersList = new ArrayList<>();
  boolean result;

  @Before
  public void setUp() {
  }

  @Test
  public void validatePlayerCount() {
    String reason;
    GameMap gameMap = GameMap.getGameMap();
    gameMap.addGamePlayer("player1");
    boolean result = gameMap.validatePlayerCount();
    reason = "Player count should be greater than 2";
    assertFalse(reason, result);
    gameMap.addGamePlayer("player2");
    result = gameMap.validatePlayerCount();
    assertTrue(result);
    gameMap.addGamePlayer("player3");
    gameMap.addGamePlayer("player4");
    gameMap.addGamePlayer("player5");
    gameMap.addGamePlayer("player6");

    result = gameMap.validatePlayerCount();
    assertTrue(result);
    gameMap.addGamePlayer("player7");
    result = gameMap.validatePlayerCount();
    reason = "Player count should be less than 6";
    assertFalse(reason, result);
  }
}
