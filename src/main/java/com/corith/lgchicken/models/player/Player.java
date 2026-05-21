package com.corith.lgchicken.models.player;

import com.corith.lgchicken.interfaces.Playable;
import com.corith.lgchicken.models.Card;
import com.corith.lgchicken.models.Hand;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public abstract class Player implements Playable {

    UUID playerId;
    String name;
    boolean isDealer;
    boolean isTurn = false;
    int score;
    int riskScore = 3;

    Hand hand;

    public Player() {
        this.playerId = UUID.randomUUID();
        this.name = "";
        this.isDealer = false;
        this.score = 0;
        this.hand = new Hand();
    }

    public void shuffleCards(Deque<Card> cards) {
        List<Card> shuffler = new ArrayList<>(cards);
        Collections.shuffle(shuffler);
        cards.clear();
        cards.addAll(shuffler);
    }

    public void deal(Deque<Card> cards, List<Player> players, int limit) {
        for (int i=0; i<limit; i++) {
            for (Player p : players) {
                try {
                    Card card = cards.pop();
                    p.getHand().getDeadwood().add(card);
                } catch (NoSuchElementException e) {
                    throw new NoSuchElementException("Ran out of cards while dealing");
                }
            }
        }
    }

    public int deadwoodScore() {
        return getHand().getDeadWoodValue();
    }

    public void clearHand() {
        getHand().setCardGroups(new ArrayList<>());
        getHand().setHeartCards(new ArrayList<>());
        getHand().setDiamondCards(new ArrayList<>());
        getHand().setSpadeCards(new ArrayList<>());
        getHand().setClubCards(new ArrayList<>());
        getHand().setDeadwood(new ArrayList<>());
        getHand().setWildCards(new ArrayList<>());
    }
}
