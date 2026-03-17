package com.corith.lgchicken.models.player;

import com.corith.lgchicken.models.Card;
import com.corith.lgchicken.models.PlayPlate;
import com.corith.lgchicken.enums.Suit;
import com.corith.lgchicken.utility.Ansi;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class UserPlayer extends Player {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String DRAW_COMMAND = "d";
    private static final String TAKE_DISCARD_COMMAND = "p";
    private static final String JOKER_TOKEN = "jk";

    @Override
    public void takeTurn(PlayPlate playPlate) {
        System.out.println(Ansi.HIGH_INTENSITY+Ansi.MAGENTA+"Draw card ("+DRAW_COMMAND+") or pick ("+TAKE_DISCARD_COMMAND+") up from discard pile?"+Ansi.RESET);
        String choice;
        do {
            choice = scanner.nextLine();
        } while (!choice.trim().equalsIgnoreCase(DRAW_COMMAND) && !choice.trim().equalsIgnoreCase(TAKE_DISCARD_COMMAND));

        if (choice.equalsIgnoreCase(DRAW_COMMAND)) {
            Card drawCard = playPlate.drawFromDeck();
            System.out.println("Draw Card: " + drawCard.prettyPrint(true));
            getHand().getDeadwood().add(drawCard);
        } else if (choice.equalsIgnoreCase(TAKE_DISCARD_COMMAND)) {
            getHand().getDeadwood().add(playPlate.getDiscardCards().pop());
        }

        Card discardedCard = discard();
        playPlate.getDiscardCards().push(discardedCard);
        System.out.println(discardedCard.prettyPrint(true));
    }

    @Override
    public void takeCard() {

    }

    @Override
    public Card discard() {
        Card selectedCard;
        do {
            List<String> userInput = getUserInput("Which card do you want to discard?");
            selectedCard = getDiscardCardFromUserInput(userInput);
        } while(selectedCard == null);
        getHand().getDeadwood().remove(selectedCard);
        return selectedCard;
    }

    public List<String> getUserInput(String prompt) {
        return getCardInput(prompt);
    }

    /**
     * Validates and saves user input in the form of (1-13)(h,d,s,c) or jk for joker.
     * <p>
     * Ex: 12h, 2d, 10s, jk, etc.
     * @return [number, letter] (rank, suit).
     */
    public static List<String> getCardInput(String prompt) {
        List<String> result = new ArrayList<>();

        System.out.println(Ansi.HIGH_INTENSITY + Ansi.MAGENTA + prompt + Ansi.CYAN + "\nFormat:" + Ansi.RESET + " number (1-13) followed by a suit char (h, d, s, or c), or jk for joker.\n" + Ansi.CYAN + "Ex: 7h, 12d, 1c, 3s, jk" + Ansi.RESET);

        Pattern pattern = Pattern.compile("^(1[0-3]|[1-9])([hdsc])$");

        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase(JOKER_TOKEN)) {
                result.add("joker");
                result.add("j");
                break;
            }
            Matcher matcher = pattern.matcher(input);

            if (matcher.matches()) {
                result.add(matcher.group(1));
                result.add(matcher.group(2));
                break;
            } else {
                System.out.println("Invalid input format. Please enter a number (1-13) followed by a single character (h, d, s, or c), or jk for joker.");
            }
        }
        return result;
    }

    Card getDiscardCardFromUserInput(List<String> input) {
        if ("j".equalsIgnoreCase(input.get(1))) {
            for (Card card : getHand().getDeadwood()) {
                if (card.getSuit() == Suit.JOKER || card.isJoker()) {
                    return card;
                }
            }
            return null;
        }

        String selectedSuitName = "";

        switch (input.get(1)) {
            case "h":
                selectedSuitName = "HEARTS";
                break;
            case "d":
                selectedSuitName = "DIAMONDS";
                break;
            case "s":
                selectedSuitName = "SPADES";
                break;
            case "c":
                selectedSuitName = "CLUBS";
                break;

        }
        Card selectedCard = null;
        for (Card card : getHand().getDeadwood()) {
            if (card.getSuit().name().equals(selectedSuitName)) {
                if (card.getCardRank().getRank() == Integer.parseInt(input.get(0))) {
                    System.out.println("Found chosen card");
                    selectedCard = card;
                }
            }
        }
        return selectedCard;
    }
}
