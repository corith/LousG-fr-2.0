package com.corith.lgchicken.utility;

import com.corith.lgchicken.enums.GroupType;
import com.corith.lgchicken.models.Card;
import com.corith.lgchicken.models.CardGroup;
import com.corith.lgchicken.models.Hand;
import com.corith.lgchicken.models.PlayPlate;
import com.corith.lgchicken.models.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RenderEngine {
    private static boolean render = true;

    public static void disable() {
        render = false;
    }

    public static void enable() {
        render = true;
    }

    public static boolean shouldRender() {
        return render;
    }


    public static void renderCard(Card card) {
        System.out.println(card.prettyPrint(true));
    }

    public static void renderEmptyBlock(int size) {
        if (render) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                builder.append("\n");
            }
            System.out.println(builder);
        }
    }

    public static void renderHand(Hand hand) {
        if (!render) {
            return;
        }
        for (CardGroup cardGroup : hand.getCardGroups()) {
            System.out.println((cardGroup.getGroupType().equals(GroupType.RUN) ? "r" : "m")+cardGroup.getCards().stream()
                    .map(card -> card.prettyPrint(true))
                    .collect(Collectors.joining(", ", "[", "]")));
        }

        System.out.println("⚰️ "+ hand.getDeadwood().stream().filter(e->!e.isBeingUsed())
                .map(card -> card.prettyPrint(true))
                .collect(Collectors.joining(", ", "[", "]")));

        System.out.println("Deadwood value: " + hand.getDeadWoodValue());
        System.out.println(hand.getDeadwood().stream()
                .map(card -> card.prettyPrint(true))
                .collect(Collectors.joining(", ", "🖐️ ", "")));
    }

    public static void renderPlayPlate(PlayPlate playPlate) {
        if (!render) {
            return;
        }
        System.out.println();
        System.out.println(Ansi.HIGH_INTENSITY+Ansi.MAGENTA+"|_|    " + playPlate.getDiscardCards().peek().prettyPrint(true));
        System.out.println();
    }

    public static void renderFinalScores(List<Player> players) {
        if (render) {
            System.out.println("Scores");
            List<Player> sortedPlayers = players.stream().sorted(Comparator.comparing(Player::getScore)).collect(Collectors.toList());
            for (Player p : sortedPlayers) {
                System.out.println(p.getName() + "'s score: " + p.getScore());
            }
        }
    }

    public static void renderTurnTitle(String name) {
        if (render) {
            System.out.println("************************************");
            System.out.println(name + "'s turn!");
        }
    }
}
