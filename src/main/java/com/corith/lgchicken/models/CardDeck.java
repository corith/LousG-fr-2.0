package com.corith.lgchicken.models;

import com.corith.lgchicken.enums.CardRank;
import com.corith.lgchicken.enums.Suit;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;


public class CardDeck {

    private static final int JOKER_COUNT = 2;

    public Deque<Card> cards;

    public CardDeck() {
        this.cards = getCleanDeck();
    }

    public CardDeck(int wildRank) {
        this.cards = getCleanDeck();
        for (Card c : cards) {
            if (c.isJoker() || c.getCardRank().getRank() == wildRank) {
                c.setWild(true);
            }
        }
    }

    private Deque<Card> getCleanDeck() {
        Deque<Card> freshCards = new ArrayDeque<>();
        for (Suit suit : new Suit[]{Suit.HEARTS, Suit.DIAMONDS, Suit.CLUBS, Suit.SPADES}) {
            for (CardRank rank : new CardRank[]{
                    CardRank.ACE, CardRank.TWO, CardRank.THREE, CardRank.FOUR, CardRank.FIVE,
                    CardRank.SIX, CardRank.SEVEN, CardRank.EIGHT, CardRank.NINE, CardRank.TEN,
                    CardRank.JACK, CardRank.QUEEN, CardRank.KING
            }) {
                freshCards.add(new Card(suit, rank));
            }
        }
        for (int i = 0; i < JOKER_COUNT; i++) {
            freshCards.add(new Card(Suit.JOKER, CardRank.JOKER));
        }
        return freshCards;
    }

    public Card getTopCard() {
        try {
            return cards.pop();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Deck is empty");
        }
    }

    public int countWildsInDeck() {
        return (int) cards.stream().filter(Card::isWild).count();
    }

}
