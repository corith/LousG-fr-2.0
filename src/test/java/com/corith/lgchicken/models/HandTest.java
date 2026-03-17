package com.corith.lgchicken.models;

import com.corith.lgchicken.enums.CardRank;
import com.corith.lgchicken.enums.Suit;
import com.corith.lgchicken.models.player.ComputerPlayer;
import com.corith.lgchicken.models.player.Player;
import com.corith.lgchicken.models.player.UserPlayer;
import com.corith.lgchicken.utility.RenderEngine;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class HandTest {

    @Test
    public void testCreateBestHand() {
        Player userPlayer = new UserPlayer();
        Card c0 = new Card(Suit.HEARTS, CardRank.SIX);
        Card c1 = new Card(Suit.HEARTS, CardRank.FOUR);
        Card c2 = new Card(Suit.HEARTS, CardRank.FIVE);
        Card c3 = new Card(Suit.HEARTS, CardRank.EIGHT);
        Card c4 = new Card(Suit.HEARTS, CardRank.NINE);
        Card c5 = new Card(Suit.HEARTS, CardRank.TEN);
        Card c6 = new Card(Suit.HEARTS, CardRank.SEVEN);
        userPlayer.getHand().deadwood.add(c0);
        userPlayer.getHand().deadwood.add(c1);
        userPlayer.getHand().deadwood.add(c2);
        userPlayer.getHand().deadwood.add(c3);
        userPlayer.getHand().deadwood.add(c4);
        userPlayer.getHand().deadwood.add(c5);
        userPlayer.getHand().deadwood.add(c6);
        userPlayer.getHand().evaluateBestGrouping();
        RenderEngine.renderHand(userPlayer.getHand());
        Assert.assertEquals(0, userPlayer.getHand().getDeadWoodValue());
    }

    @Test
    public void testWildRunGap() {
        Player computer = new ComputerPlayer();
        Card wild = new Card(Suit.CLUBS, CardRank.FOUR);
        wild.setWild(true);
        computer.getHand().deadwood.add(new Card(Suit.SPADES, CardRank.FIVE));
        computer.getHand().deadwood.add(wild);
        computer.getHand().deadwood.add(new Card(Suit.SPADES, CardRank.SEVEN));
        computer.getHand().deadwood.add(new Card(Suit.SPADES, CardRank.EIGHT));
        computer.getHand().evaluateBestGrouping();
        RenderEngine.renderHand(computer.getHand());
        Assert.assertEquals(0, computer.getHand().getDeadWoodValue());
    }

    @Test
    public void testWildRunGap01() {
        Player computer = new ComputerPlayer();
        Card wild = new Card(Suit.CLUBS, CardRank.FOUR);
        wild.setWild(true);
        computer.getHand().deadwood.add(new Card(Suit.SPADES, CardRank.NINE));
        computer.getHand().deadwood.add(wild);
        computer.getHand().deadwood.add(new Card(Suit.SPADES, CardRank.SEVEN));
        computer.getHand().deadwood.add(new Card(Suit.SPADES, CardRank.SIX));

        computer.getHand().evaluateBestGrouping();
        RenderEngine.renderHand(computer.getHand());
        Assert.assertEquals(0, computer.getHand().getDeadWoodValue());
    }


    @Test
    public void testRunsBasic() {
        Player computer = new ComputerPlayer();
//        Card wild = new Card(Suit.CLUBS, CardRank.FOUR);
//        wild.setWild(true);
        computer.getHand().deadwood.add(new Card(Suit.SPADES, CardRank.SIX));
//        computer.getHand().deadwood.add(wild);
        computer.getHand().deadwood.add(new Card(Suit.SPADES, CardRank.SEVEN));
        computer.getHand().deadwood.add(new Card(Suit.SPADES, CardRank.EIGHT));
        computer.getHand().evaluateBestGrouping();
        Assert.assertEquals(0, computer.getHand().getDeadWoodValue());
    }

    @Test
    public void testBasicRunsWithWild() {
        Player userPlayer = new UserPlayer();
        Card c0 = new Card(Suit.SPADES, CardRank.JACK);
        Card c1 = new Card(Suit.HEARTS, CardRank.FOUR);
        Card c2 = new Card(Suit.HEARTS, CardRank.FIVE);
        Card c3 = new Card(Suit.SPADES, CardRank.EIGHT);
        Card c4 = new Card(Suit.SPADES, CardRank.NINE);
        Card c5 = new Card(Suit.SPADES, CardRank.TEN);
        Card c6 = new Card(Suit.DIAMONDS, CardRank.SEVEN);
        c6.setWild(true);
        userPlayer.getHand().deadwood.add(c0);
        userPlayer.getHand().deadwood.add(c1);
        userPlayer.getHand().deadwood.add(c2);
        userPlayer.getHand().deadwood.add(c3);
        userPlayer.getHand().deadwood.add(c4);
        userPlayer.getHand().deadwood.add(c5);
        userPlayer.getHand().deadwood.add(c6);
        userPlayer.getHand().evaluateBestGrouping();
        RenderEngine.renderHand(userPlayer.getHand());
        Assert.assertEquals(0, userPlayer.getHand().getDeadWoodValue());
    }

    @Test
    public void testBasicMeld() {
        Player userPlayer = new UserPlayer();
        Card c0 = new Card(Suit.HEARTS, CardRank.SIX);
        Card c1 = new Card(Suit.DIAMONDS, CardRank.SIX);
        Card c2 = new Card(Suit.SPADES, CardRank.SIX);
        Card c6 = new Card(Suit.DIAMONDS, CardRank.SEVEN);
        userPlayer.getHand().deadwood.add(c0);
        userPlayer.getHand().deadwood.add(c1);
        userPlayer.getHand().deadwood.add(c2);
        userPlayer.getHand().deadwood.add(c6);
        userPlayer.getHand().evaluateBestGrouping();
        RenderEngine.renderHand(userPlayer.getHand());
        Assert.assertEquals(7, userPlayer.getHand().getDeadWoodValue());
    }

    @Test
    public void testTwoWilds() {
        Player userPlayer = new UserPlayer();
        Card c0 = new Card(Suit.HEARTS, CardRank.THREE);
        c0.setWild(true);
        Card c1 = new Card(Suit.DIAMONDS, CardRank.THREE);
        c1.setWild(true);
        Card c2 = new Card(Suit.SPADES, CardRank.NINE);
        userPlayer.getHand().deadwood.add(c0);
        userPlayer.getHand().deadwood.add(c1);
        userPlayer.getHand().deadwood.add(c2);
        userPlayer.getHand().evaluateBestGrouping();
        RenderEngine.renderHand(userPlayer.getHand());
        Assert.assertEquals(0, userPlayer.getHand().getDeadWoodValue());
    }

    @Test
    public void testJokerCanBridgeRunGap() {
        Player computer = new ComputerPlayer();
        computer.getHand().deadwood.add(new Card(Suit.SPADES, CardRank.FIVE));
        computer.getHand().deadwood.add(new Card(Suit.JOKER, CardRank.JOKER));
        computer.getHand().deadwood.add(new Card(Suit.SPADES, CardRank.SEVEN));
        computer.getHand().evaluateBestGrouping();
        Assert.assertEquals(0, computer.getHand().getDeadWoodValue());
    }

    @Test
    public void testThreeJokersCanFormAGroup() {
        Player computer = new ComputerPlayer();
        computer.getHand().deadwood.add(new Card(Suit.JOKER, CardRank.JOKER));
        computer.getHand().deadwood.add(new Card(Suit.JOKER, CardRank.JOKER));
        computer.getHand().deadwood.add(new Card(Suit.JOKER, CardRank.JOKER));
        computer.getHand().evaluateBestGrouping();
        Assert.assertEquals(0, computer.getHand().getDeadWoodValue());
    }

}
