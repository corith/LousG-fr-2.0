package com.corith.lgchicken.utility;

import com.corith.lgchicken.models.Card;
import com.corith.lgchicken.models.CardDeck;
import com.corith.lgchicken.models.PlayPlate;
import com.corith.lgchicken.models.player.ComputerPlayer;
import com.corith.lgchicken.models.player.Player;
import com.corith.lgchicken.models.player.UserPlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayWizard {
    private static final int DEFAULT_STARTING_CARD_LIMIT = 3;
    private static final int DEFAULT_ROUND_LIMIT = 14;
    private static final int ROUND_TURN_SAFETY_LIMIT = 10_000;
    private static final String DEFAULT_PLAYER_NAME = "Cory Sebastian";
    private static final String GAME_OVER_MESSAGE = "Game Over.";

    private final int roundLimit;
    private final String playerName;
    private final Player leadPlayer;
    private int currentCardLimit;

    public PlayWizard() {
        this(DEFAULT_STARTING_CARD_LIMIT, DEFAULT_ROUND_LIMIT, DEFAULT_PLAYER_NAME, createLeadPlayer());
    }

    public PlayWizard(int startingCardLimit) {
        this(startingCardLimit, DEFAULT_ROUND_LIMIT, DEFAULT_PLAYER_NAME, createLeadPlayer());
    }

    PlayWizard(int startingCardLimit, int roundLimit, String playerName, Player leadPlayer) {
        this.currentCardLimit = startingCardLimit;
        this.roundLimit = roundLimit;
        this.playerName = playerName;
        this.leadPlayer = leadPlayer;
    }

    public static String playLoop() {
        return new PlayWizard().runGame();
    }

    public static String playLoop(int startingCardLimit) {
        return new PlayWizard(startingCardLimit).runGame();
    }

    public int getCurrentCardLimit() {
        return currentCardLimit;
    }

    public String runGame() {
        if (RenderEngine.shouldRender()) {
            System.out.println(Ansi.CYAN + "Lous Game!" + Ansi.RESET);
        }

        List<Player> players = createPlayers(playerName);

        while (currentCardLimit < roundLimit) {
            PlayPlate playPlate = initializeRound(players);
            runRound(players, playPlate);
            scoreRound(players);

            currentCardLimit += 1;
            if (currentCardLimit >= roundLimit) {
                break;
            }

            clearHands(players);
            switchDealer(players);
        }

        RenderEngine.renderFinalScores(players);
        return GAME_OVER_MESSAGE;
    }

    void runRound(List<Player> players, PlayPlate playPlate) {
        int currentPlayerIndex = 0;
        Player roundWinner = null;
        int finalTurnsRemaining = -1;
        int safetyTurnCount = 0;

        while (roundWinner == null || finalTurnsRemaining > 0) {
            if (safetyTurnCount++ > ROUND_TURN_SAFETY_LIMIT) {
                LousLogger.printYellow("Round safety limit reached. Ending round on current hands.");
                return;
            }

            Player currentPlayer = players.get(currentPlayerIndex);
            if (!ensureDeckHasCards(playPlate)) {
                return;
            }

            runTurn(currentPlayer, playPlate);

            if (roundWinner == null && hasWinningHand(currentPlayer)) {
                roundWinner = currentPlayer;
                finalTurnsRemaining = players.size() - 1;
            } else if (roundWinner != null) {
                finalTurnsRemaining -= 1;
            }

            if (roundWinner != null && finalTurnsRemaining == 0) {
                return;
            }

            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }

    private static Player createLeadPlayer() {
        return System.getenv("human") == null ? new ComputerPlayer() : new UserPlayer();
    }

    private void runTurn(Player player, PlayPlate playPlate) {
        RenderEngine.renderTurnTitle(player.getName());
        RenderEngine.renderPlayPlate(playPlate);
        player.getHand().evaluateBestGrouping();
        RenderEngine.renderHand(player.getHand());
        player.takeTurn(playPlate);
        player.getHand().evaluateBestGrouping();
        RenderEngine.renderHand(player.getHand());
        RenderEngine.renderPlayPlate(playPlate);
    }

    private PlayPlate initializeRound(List<Player> players) {
        CardDeck gameDeck = new CardDeck(currentCardLimit);
        Player dealer = getDealer(players);
        dealer.shuffleCards(gameDeck.cards);
        dealer.deal(gameDeck.cards, players, currentCardLimit);

        PlayPlate playPlate = new PlayPlate(gameDeck);
        playPlate.initializeDiscardPile();
        return playPlate;
    }

    private void scoreRound(List<Player> players) {
        for (Player player : players) {
            player.getHand().evaluateBestGrouping();
            player.setScore(player.getScore() + player.deadwoodScore());

            if (player.getHand().getDeadWoodValue() == 0) {
                RenderEngine.renderEmptyBlock(2);
                LousLogger.printGreen(Ansi.BLINK + "------------------- 0 D-wood -------------------" + Ansi.RESET);
                LousLogger.printGreen(Ansi.BLINK + player.getName() + " has zero wood..." + Ansi.RESET);
                LousLogger.printGreen(Ansi.BLINK + "------------------------------------------------" + Ansi.RESET);
                RenderEngine.renderHand(player.getHand());
            }
        }
    }

    private boolean ensureDeckHasCards(PlayPlate playPlate) {
        if (!playPlate.getDeck().cards.isEmpty()) {
            return true;
        }

        LousLogger.printYellow(Ansi.BLINK + "Deck is out of cards" + Ansi.RESET);
        return playPlate.redistributeDiscards();
    }

    private void clearHands(List<Player> players) {
        for (Player player : players) {
            player.clearHand();
        }
        RenderEngine.renderEmptyBlock(4);
    }

    private void switchDealer(List<Player> players) {
        int dealerIndex = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).isDealer()) {
                players.get(i).setDealer(false);
                dealerIndex = i;
                break;
            }
        }

        int nextDealerIndex = (dealerIndex + 1) % players.size();
        players.get(nextDealerIndex).setDealer(true);
    }

    private Player getDealer(List<Player> players) {
        for (Player player : players) {
            if (player.isDealer()) {
                return player;
            }
        }
        return players.get(0);
    }

    private List<Player> createPlayers(String name) {
        leadPlayer.setDealer(true);
        leadPlayer.setScore(0);
        leadPlayer.clearHand();
        leadPlayer.setName(name);

        List<Player> players = new ArrayList<>();
        players.add(leadPlayer);
        players.add(createComputerPlayer("CPU 0"));
        players.add(createComputerPlayer("CPU 1"));
        return players;
    }

    private Player createComputerPlayer(String name) {
        Player player = new ComputerPlayer();
        player.setName(name);
        return player;
    }

    private boolean hasWinningHand(Player player) {
        if (player.deadwoodScore() != 0) {
            return false;
        }

        for (Card card : player.getHand().getDeadwood()) {
            if (!card.isBeingUsed()) {
                return false;
            }
        }

        return true;
    }
}
