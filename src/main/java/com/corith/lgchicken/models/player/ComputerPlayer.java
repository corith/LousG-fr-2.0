package com.corith.lgchicken.models.player;

import com.corith.lgchicken.models.Card;
import com.corith.lgchicken.models.PlayPlate;
import com.corith.lgchicken.utility.Ansi;
import com.corith.lgchicken.utility.LousLogger;
import com.corith.lgchicken.utility.RenderEngine;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ComputerPlayer extends Player {

    @Override
    public void takeTurn(PlayPlate playPlate) {

        boolean shouldTakeDiscard = wouldUseCard(playPlate.getDiscardCards().peek());
        if (shouldTakeDiscard) {
            LousLogger.printRed("Choosing discard card " + playPlate.getDiscardCards().peek().prettyPrint(true));
            discardAfterTakingCard(playPlate.getDiscardCards().pop(), playPlate);
        } else {
            Card drawCard = playPlate.drawFromDeck();
            LousLogger.printRed("Drawing from deck " + drawCard.prettyPrint(true) + (drawCard.isWild() ? "wild": ""));
            boolean drawWillBeUsed = wouldUseCard(drawCard);
            if (drawWillBeUsed) {
                LousLogger.printRed("Will use " + drawCard.prettyPrint(true));
                discardAfterTakingCard(drawCard, playPlate);
            } else {
                LousLogger.printRed(Ansi.HIGH_INTENSITY+"Will not use draw card. Putting it in discard pile."+Ansi.RESET);
                playPlate.getDiscardCards().push(drawCard);
                getHand().evaluateBestGrouping();
            }
        }
    }

    @Override
    public void takeCard() {
        System.out.println("Pick up card computer");
    }

    @Override
    public Card discard() {
        List<Card> cardsToDiscard = getHand().getDeadwood().stream().sorted(Comparator.comparing(Card::getCardRank)).collect(Collectors.toList());

        Card discardCard = null;
        for (Card c : cardsToDiscard) {
            if (!c.isBeingUsed()) {
                discardCard = c;
                break;
            }
        }
        if (discardCard == null) {
            discardCard = cardsToDiscard.remove(cardsToDiscard.size() - 1);
        } else {
            cardsToDiscard.remove(discardCard);
        }
        getHand().getDeadwood().clear();
        getHand().getDeadwood().addAll(cardsToDiscard);
        return discardCard;
    }

    private void discardAfterTakingCard(Card cardToKeep, PlayPlate playPlate) {
        getHand().getDeadwood().add(cardToKeep);
        getHand().evaluateBestGrouping();
        Card discardedCard = discard();
        playPlate.getDiscardCards().push(discardedCard);
        LousLogger.printRed("Discarded card " + discardedCard.prettyPrint(true));
    }

    public boolean wouldUseCard(Card card) {
        getHand().getDeadwood().add(card);
        getHand().evaluateBestGrouping();
        boolean used = card.isBeingUsed();
        if (used && RenderEngine.shouldRender()) {
            System.out.println("Hand with card used:");
            RenderEngine.renderHand(getHand());
        }
        getHand().getDeadwood().remove(card);
        getHand().evaluateBestGrouping();
        return used;
    }


}
