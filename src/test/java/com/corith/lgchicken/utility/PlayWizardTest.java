package com.corith.lgchicken.utility;

import com.corith.lgchicken.enums.CardRank;
import com.corith.lgchicken.enums.Suit;
import com.corith.lgchicken.models.Card;
import com.corith.lgchicken.models.CardDeck;
import com.corith.lgchicken.models.PlayPlate;
import com.corith.lgchicken.models.player.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class PlayWizardTest {

    @Test
    public void testPlayloopThreeCard() {
        assertGameCompletes(3, 100);
    }

    @Test
    public void testPlayloopSevenCard() {
        assertGameCompletes(7, 100);
    }

    @Test
    public void testPlayloopThirteenCard() {
        assertGameCompletes(13, 100);
    }

    @Test
    public void testPlayerWhoGoesOutGivesEveryoneElseOneMoreTurn() {
        RenderEngine.disable();

        ScriptedPlayer playerA = new ScriptedPlayer("A", false);
        ScriptedPlayer playerB = new ScriptedPlayer("B", true);
        ScriptedPlayer playerC = new ScriptedPlayer("C", false);

        playerA.setDealer(true);
        seedNonWinningHand(playerA);
        seedNonWinningHand(playerB);
        seedNonWinningHand(playerC);

        List<Player> players = Arrays.asList(playerA, playerB, playerC);
        PlayPlate playPlate = new PlayPlate(new CardDeck(3));
        playPlate.initializeDiscardPile();

        PlayWizard wizard = new PlayWizard(3, 14, "Test", playerA);
        wizard.runRound(players, playPlate);

        Assert.assertEquals(2, playerA.getTurnsTaken());
        Assert.assertEquals(1, playerB.getTurnsTaken());
        Assert.assertEquals(1, playerC.getTurnsTaken());
    }

    private void assertGameCompletes(int startingCardLimit, int targetRuns) {
        RenderEngine.disable();
        int run = 0;
        while (run < targetRuns) {
            PlayWizard playWizard = new PlayWizard(startingCardLimit);
            String output = playWizard.runGame();
            Assert.assertEquals("No game over after " + run + " cycles.", "Game Over.", output);
            Assert.assertEquals(14, playWizard.getCurrentCardLimit());
            run++;
        }
    }

    private void seedNonWinningHand(Player player) {
        player.getHand().getDeadwood().clear();
        player.getHand().getDeadwood().add(new Card(Suit.HEARTS, CardRank.TWO));
    }

    private static final class ScriptedPlayer extends Player {
        private final boolean goOutOnFirstTurn;
        private int turnsTaken;

        private ScriptedPlayer(String name, boolean goOutOnFirstTurn) {
            this.goOutOnFirstTurn = goOutOnFirstTurn;
            setName(name);
        }

        private int getTurnsTaken() {
            return turnsTaken;
        }

        @Override
        public void takeTurn(PlayPlate playPlate) {
            turnsTaken += 1;
            getHand().getDeadwood().clear();

            if (goOutOnFirstTurn && turnsTaken == 1) {
                getHand().getDeadwood().add(new Card(Suit.HEARTS, CardRank.FIVE));
                getHand().getDeadwood().add(new Card(Suit.HEARTS, CardRank.SIX));
                getHand().getDeadwood().add(new Card(Suit.HEARTS, CardRank.SEVEN));
                return;
            }

            getHand().getDeadwood().add(new Card(Suit.SPADES, CardRank.TWO));
        }

        @Override
        public void takeCard() {
        }

        @Override
        public Card discard() {
            return null;
        }
    }
}
