package com.corith.lgchicken.models;

import com.corith.lgchicken.enums.CardRank;
import com.corith.lgchicken.enums.Suit;
import org.junit.Assert;
import org.junit.Test;

public class CardTest {

    @Test
    public void testEquals() {
        Card card1 = new Card(Suit.CLUBS, CardRank.SEVEN);
        Card card2 = new Card(Suit.CLUBS, CardRank.EIGHT);
        Assert.assertNotEquals(card1, card2);
        card1 = new Card(Suit.HEARTS, CardRank.SEVEN);
        card2 = new Card(Suit.CLUBS, CardRank.SEVEN);
        Assert.assertNotEquals(card1, card2);
        card1 = new Card(Suit.CLUBS, CardRank.NINE);
        card2 = new Card(Suit.CLUBS, CardRank.NINE);
        Assert.assertEquals(card1, card2);
    }

    @Test
    public void testJokerConstruction() {
        Card joker = new Card(Suit.JOKER, CardRank.JOKER);
        Assert.assertTrue(joker.isJoker());
        Assert.assertTrue(joker.isWild());
        Assert.assertEquals(15, joker.getScoreValue());
    }

}
