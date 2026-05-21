package com.corith.lgchicken.models;

import com.corith.lgchicken.models.player.ComputerPlayer;
import com.corith.lgchicken.models.player.Player;
import com.corith.lgchicken.models.player.UserPlayer;
import com.corith.lgchicken.utility.RenderEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PlayerTest {



    @Test
    public void testPlayerTakeTurn() {
        Player userPlayer = new UserPlayer();
        Player computerPlayer = new ComputerPlayer();
        List<Player> players = new ArrayList<>();
        players.add(userPlayer);
        players.add(computerPlayer);

    }

    @Test
    public void testWillCardBeUsed() {
        Player userPlayer = new UserPlayer();
        Player userPlayer1 = new UserPlayer();
        ComputerPlayer computer = new ComputerPlayer();
        CardDeck standardDeck = new CardDeck();
        userPlayer.shuffleCards(standardDeck.cards);

        List<Player> players = new ArrayList<>();
        players.add(userPlayer);
        players.add(userPlayer1);
        players.add(computer);
        userPlayer.deal(standardDeck.cards, players, 13);

        PlayPlate playPlate = new PlayPlate(standardDeck);
        playPlate.discardCards.add(standardDeck.cards.pop());

        RenderEngine.renderHand(computer.getHand());

        System.out.println("Will card be used? " + computer.wouldUseCard(playPlate.discardCards.peek()));
        System.out.println(playPlate.discardCards.peek().prettyPrint(true));
    }


    @Test
    public void testPlayerShuffleDeck() {
        Player userPlayer = new UserPlayer();
        CardDeck standardDeck = new CardDeck();
        CardDeck testDeck = new CardDeck();

        userPlayer.shuffleCards(testDeck.cards);
        Assert.assertNotEquals(standardDeck.cards, testDeck.cards); // The order should differ
        Assert.assertTrue(testDeck.cards.containsAll(standardDeck.cards));
        Assert.assertEquals(testDeck.cards.size(), standardDeck.cards.size()); // Sizes should match
    }


    @Test
    public void testPlayerDeal() {
        Player userPlayer = new UserPlayer();
        Player userPlayer1 = new UserPlayer();
        Player userPlayer2 = new UserPlayer();
        CardDeck standardDeck = new CardDeck();
        userPlayer.shuffleCards(standardDeck.cards);

        List<Player> players = new ArrayList<>();
        players.add(userPlayer);
        players.add(userPlayer1);
        players.add(userPlayer2);
        userPlayer.deal(standardDeck.cards, players, 13);

        Assert.assertEquals(13, userPlayer.getHand().deadwood.size());
        Assert.assertEquals(13, userPlayer1.getHand().deadwood.size());
        Assert.assertEquals(13, userPlayer2.getHand().deadwood.size());
        Assert.assertEquals(15, standardDeck.cards.size());
    }

    @Test
    public void testPlayerOverDeal() {
        Player userPlayer = new UserPlayer();
        Player userPlayer1 = new UserPlayer();
        Player userPlayer2 = new UserPlayer();
        CardDeck standardDeck = new CardDeck();
        userPlayer.shuffleCards(standardDeck.cards);

        List<Player> players = new ArrayList<>();
        players.add(userPlayer);
        players.add(userPlayer1);
        players.add(userPlayer2);
        try {
            userPlayer.deal(standardDeck.cards, players, 63);
            Assert.fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            Assert.assertEquals("Ran out of cards while dealing", e.getMessage());
        }
    }


}
