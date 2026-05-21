package com.corith.lgchicken.models.player;

import com.corith.lgchicken.enums.CardRank;
import com.corith.lgchicken.enums.Suit;
import com.corith.lgchicken.models.Card;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserPlayerTest {

    @Test
    public void testUserPlayerCanSelectJokerForDiscard() {
        UserPlayer userPlayer = new UserPlayer();
        Card joker = new Card(Suit.JOKER, CardRank.JOKER);
        userPlayer.getHand().getDeadwood().add(joker);

        List<String> input = new ArrayList<>();
        input.add("joker");
        input.add("j");

        Assert.assertEquals(joker, userPlayer.getDiscardCardFromUserInput(input));
    }
}
