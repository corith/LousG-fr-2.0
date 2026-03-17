package com.corith.lgchicken.models;

import com.corith.lgchicken.enums.Suit;
import org.junit.Assert;
import org.junit.Test;

import java.util.Deque;

public class DeckTest {

    @Test
    public void testStandardDeckCreation() {
        CardDeck cardDeck = new CardDeck();
        Deque<Card> cards = cardDeck.cards;
        boolean hearts = cards.stream().filter(e->e.getSuit().equals(Suit.HEARTS)).count() == 13;
        boolean diamonds = cards.stream().filter(e->e.getSuit().equals(Suit.DIAMONDS)).count() == 13;
        boolean spades = cards.stream().filter(e->e.getSuit().equals(Suit.SPADES)).count() == 13;
        boolean clubs = cards.stream().filter(e->e.getSuit().equals(Suit.CLUBS)).count() == 13;
        boolean jokers = cards.stream().filter(e->e.getSuit().equals(Suit.JOKER)).count() == 2;
        Assert.assertTrue(hearts);
        Assert.assertTrue(diamonds);
        Assert.assertTrue(spades);
        Assert.assertTrue(clubs);
        Assert.assertTrue(jokers);
        Assert.assertEquals(54, cards.size());
        Assert.assertEquals(2, cardDeck.countWildsInDeck());
    }

    @Test
    public void testRoundWildsIncludeJokers() {
        CardDeck cardDeck = new CardDeck(3);
        Assert.assertEquals(6, cardDeck.countWildsInDeck());
    }

}
