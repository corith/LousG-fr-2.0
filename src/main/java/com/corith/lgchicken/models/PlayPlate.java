package com.corith.lgchicken.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayPlate {
    CardDeck deck;
    Deque<Card> discardCards = new ArrayDeque<>();

    private int shuffleCount = 0;

    public PlayPlate(CardDeck deck) {
        this.deck = deck;
    }

    public Card drawFromDeck() {
        return deck.getTopCard();
    }

    public boolean redistributeDiscards() {
        if (discardCards.size() <= 1) {
            return false;
        }

        Card topDiscard = discardCards.pop();
        List<Card> cardsToRecycle = new ArrayList<>(discardCards);
        Collections.shuffle(cardsToRecycle);

        deck.cards.addAll(cardsToRecycle);
        discardCards.clear();
        discardCards.push(topDiscard);
        shuffleCount += 1;
        return true;
    }

    public void initializeDiscardPile() {
        discardCards.push(deck.getTopCard());
    }
}
