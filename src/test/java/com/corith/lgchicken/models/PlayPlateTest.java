package com.corith.lgchicken.models;

import com.corith.lgchicken.enums.CardRank;
import com.corith.lgchicken.enums.Suit;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Deque;

public class PlayPlateTest {

    @Test
    public void testRedistributeDiscardsKeepsTopDiscard() {
        PlayPlate playPlate = new PlayPlate(new CardDeck());
        playPlate.getDeck().cards.clear();

        Card topDiscard = new Card(Suit.HEARTS, CardRank.FIVE);
        Card olderDiscardOne = new Card(Suit.SPADES, CardRank.SIX);
        Card olderDiscardTwo = new Card(Suit.CLUBS, CardRank.SEVEN);

        Deque<Card> discardCards = new ArrayDeque<>();
        discardCards.push(topDiscard);
        discardCards.add(olderDiscardOne);
        discardCards.add(olderDiscardTwo);
        playPlate.setDiscardCards(discardCards);

        Assert.assertTrue(playPlate.redistributeDiscards());
        Assert.assertEquals(topDiscard, playPlate.getDiscardCards().peek());
        Assert.assertEquals(2, playPlate.getDeck().cards.size());
        Assert.assertEquals(1, playPlate.getShuffleCount());
    }

    @Test
    public void testRedistributeDiscardsNeedsMoreThanTopDiscard() {
        PlayPlate playPlate = new PlayPlate(new CardDeck());
        playPlate.getDeck().cards.clear();
        playPlate.getDiscardCards().clear();
        playPlate.getDiscardCards().push(new Card(Suit.HEARTS, CardRank.FIVE));

        Assert.assertFalse(playPlate.redistributeDiscards());
        Assert.assertEquals(0, playPlate.getDeck().cards.size());
    }
}
