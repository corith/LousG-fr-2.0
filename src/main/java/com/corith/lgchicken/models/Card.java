package com.corith.lgchicken.models;

import com.corith.lgchicken.enums.CardRank;
import com.corith.lgchicken.enums.Suit;
import com.corith.lgchicken.utility.Ansi;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    private static final int JOKER_SCORE = 15;

    private Suit suit;
    private CardRank cardRank;
    private int scoreValue = 0;
    private boolean isBeingUsed = false;
    private boolean isWild = false;

    public Card(Suit suit, CardRank cardRank) {
        this.suit = suit;
        this.cardRank = cardRank;
        this.isWild = suit == Suit.JOKER || cardRank == CardRank.JOKER;
        this.scoreValue = isWild ? JOKER_SCORE : cardRank.getRank();
    }

    public boolean isJoker() {
        return suit == Suit.JOKER || cardRank == CardRank.JOKER;
    }

    @Override
    public String toString() {
        return "Suit: " + suit.getStyle() +
                ",\nRank: " + cardRank +
                ",\nValue: " + scoreValue +
                ",\nIsWild: " + isWild +
                ",\nIsBeingUsed: " + isBeingUsed;
    }

    public String prettyPrint(boolean showNumber) {
        if (isJoker()) {
            return Ansi.HIGH_INTENSITY + Ansi.BACKGROUND_MAGENTA + Ansi.WHITE + "JOKER" + Ansi.RESET;
        }
        if (this.getCardRank().getRank() > 10 || this.getCardRank().getRank() == 1) {
            return Ansi.HIGH_INTENSITY+Ansi.BACKGROUND_BLACK+Ansi.GREEN + this.cardRank + " " + this.getSuit().getStyle();
        }
        String rank = showNumber ? String.valueOf(this.getCardRank().getRank()) : this.getCardRank().toString();
        return Ansi.HIGH_INTENSITY+Ansi.BACKGROUND_BLACK+Ansi.GREEN + rank + " " + this.getSuit().getStyle();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                                // Check for reference equality
        if (o == null || this.getClass() != o.getClass()) return false; // Check for null or incompatible types

        Card card = (Card) o;                          // Cast to Card
        return this.suit == card.suit && this.cardRank == card.cardRank; // Compare suit and rank fields
    }

    @Override
    public int hashCode() {
        return 31 * suit.hashCode() + cardRank.hashCode(); // Generate hash based on suit and rank
    }

}
