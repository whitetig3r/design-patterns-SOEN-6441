package models.player;

import controllers.BattleController;
import models.Card;
import models.Country;
import models.GameMap;
import views.CardExchangeView;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

import static views.ConsoleView.display;

public class PlayerAggressive extends Observable implements PlayerStrategy {
    /**
     * Maintains the number of sets traded in game
     */
    private static int numberOfTradedSet = 0;
    /**
     * Number of armies traded in for each set
     */
    private static int armiesTradedForSet = 0;
    /**
     * This instance variable holds the name of the player.
     */
    private String playerName = "Aggressor";
    /**
     * Stores the number of armies a player has.
     */
    private int numberOfArmies;
    /**
     * Stores the cards currently held by the player.
     */
    private ArrayList<Card> cardsInHand = new ArrayList<>();
    /** How many turns have elapsed */
    private int turnCount = 0;

    public PlayerAggressive(String name) {
        this.setPlayerName(name);
    }

    public void addObserver(CardExchangeView object) {
        super.addObserver(object);
    }

    private Map.Entry<String, Country> getStrongestCountry(GameMap gameMap){
      return gameMap.getCountries().entrySet().stream().filter(country -> country.getValue().getOwnerName().equals(this.getPlayerName())).reduce(new SimpleEntry<String, Country>("", null), (maxArmiesCountry, country) -> {
        if (maxArmiesCountry.getValue() == null) return country;
        int numOfArmies = country.getValue().getNumberOfArmies();
        if (numOfArmies > maxArmiesCountry.getValue().getNumberOfArmies()) return country;
        return maxArmiesCountry;
      });
    }

    private void attackUtil(GameMap gameMap) {
      Map.Entry<String, Country> countryWithMaxArmies = getStrongestCountry(gameMap);
      String countryToAttack = gameMap.getBorders().get(countryWithMaxArmies.getKey()).stream().reduce(null, (minArmiesNeighbor, country) -> {
        if (minArmiesNeighbor == null) return country;
        Country minArmiesNeighborCountry = gameMap.getCountries().get(minArmiesNeighbor);
        Country currentCountry = gameMap.getCountries().get(country);
        if (currentCountry.getNumberOfArmies() < minArmiesNeighborCountry.getNumberOfArmies()) return country;
        return minArmiesNeighbor;
      });

      String command = String.format("attack %s %s -allout", countryWithMaxArmies.getKey(), countryToAttack);
      BattleController battleController = new BattleController(gameMap, command);
      battleController.startBattle();
    }

    @Override
    public boolean attack(GameMap gameMap, String blankCommand) {
        // recursively with strongest valid country until he can't attack
        while(gameMap.getCurrentContext().name().contains("ATTACK")) attackUtil(gameMap);
        return true;
    }

    @Override
    public boolean reinforce(GameMap gameMap, String countryToPlace, int armiesToPlace) {
        // move all armies to strongest country
        return false;
    }

    @Override
    public boolean fortify(GameMap gameMap, String fromCountry, String toCountry, int armyToMove) {
        // get the country with max number of armies
        // check if fortify possible
        // aggregate armies in this country FROM the next strongest country
        return false;
    }

    /**
     * This method gives armies to the player
     *
     * @return int with the number of armies.
     */
    public int giveArmies() {
        if (numberOfTradedSet == 1) {
            armiesTradedForSet += 4;
        } else if (numberOfTradedSet < 6) {
            armiesTradedForSet += 2;
        } else if (numberOfTradedSet == 6) {
            armiesTradedForSet += 3;
        } else {
            armiesTradedForSet += 5;
        }

        return armiesTradedForSet;
    }

    /**
     * This method returns the name of the player.
     *
     * @return playerName the name of the player.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * This method sets the name of the player.
     *
     * @param playername the name of the player.
     */
    public void setPlayerName(String playername) {
        this.playerName = playername;
    }

    /**
     * Getter for number of armies the player owns.
     *
     * @return int with number of armies
     */
    public int getNumberOfArmies() {
        return this.numberOfArmies;
    }

    /**
     * Setter for number of armies the player owns.
     *
     * @param numberOfArmies int with the number of armies.
     */
    public void setNumberOfArmies(int numberOfArmies) {
        this.numberOfArmies = numberOfArmies;
    }

    /**
     * This is an override for pretty printing the name.
     */
    @Override
    public String toString() {
        return String.format("%s", this.playerName);
    }

    /**
     * Returns the player's hand.
     *
     * @return List with the Cards
     */
    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    /**
     * Sets the cards in the player's hand.
     *
     * @param cardsInHand A collection of Card objects.
     */
    public void setCardsInHand(ArrayList<Card> cardsInHand) {
        this.cardsInHand = cardsInHand;
        setChanged();
        notifyObservers();
    }

    /**
     * Adds a card to this player's hand.
     *
     * @param card The Card object to be added.
     */
    public void addCard(Card card) {
        this.cardsInHand.add(card);
        setChanged();
        notifyObservers();
    }

    /**
     * This method removes armies from the player
     *
     * @param count armies to subtract from the player
     */
    public void subtractArmies(int count) {
        this.numberOfArmies -= count;
    }

    /**
     * Exchange the card for armies.
     *
     * @param indices the positions of the cards in the list.
     */
    public void exchangeCardsForArmies(int[] indices) {
        Set<String> cardSet = new HashSet<>();
        for (int index : indices) {
            if (index >= 0 && index < cardsInHand.size()) {
                cardSet.add(cardsInHand.get(index).getCardValue());
            } else {
                display("One OR more of your card indices are INCORRECT", false);
                return;
            }
        }
        if (cardSet.size() == 1 || cardSet.size() == 3) {
            numberOfTradedSet++;
            int armiesAcquired = giveArmies();
            numberOfArmies += armiesAcquired;

            ArrayList<Card> cardsToAddToDeck = new ArrayList<>();
            for (int index : indices) {
                cardsToAddToDeck.add(cardsInHand.get(index));
            }

            ArrayList<Integer> listIndices =
                    Arrays.stream(indices)
                            .boxed()
                            .sorted(Comparator.reverseOrder())
                            .collect(Collectors.toCollection(ArrayList::new));

            ArrayList<Card> resultCardsInHand = new ArrayList<>();
            for (int i = 0; i < cardsInHand.size(); i++) {
                if (!listIndices.contains(i)) {
                    resultCardsInHand.add(cardsInHand.get(i));
                }
            }
            setCardsInHand(resultCardsInHand);

            Collections.shuffle(cardsToAddToDeck);
            GameMap.getGameMap()
                    .getDeck()
                    .addAll(
                            cardsToAddToDeck); // add the exchanged cards to deck after removing from player hand

            display("Acquired " + armiesAcquired + " through card exchange", false);
        } else {
            display(
                    "The set provided is not valid. Valid set: 3 cards of same type or 3 cards of different type",
                    false);
        }
    }
}