package com.corith.lgchicken.models;

import com.corith.lgchicken.enums.GroupType;
import com.corith.lgchicken.enums.Suit;
import com.corith.lgchicken.utility.Ansi;
import com.corith.lgchicken.utility.RenderEngine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class Hand {
    private static final int MIN_GROUP_SIZE = 3;

    public Hand() {
    }

    List<Card> deadwood = new ArrayList<>();
    List<CardGroup> cardGroups = new ArrayList<>();

    List<Card> heartCards = new ArrayList<>();
    List<Card> diamondCards = new ArrayList<>();
    List<Card> clubCards = new ArrayList<>();
    List<Card> spadeCards = new ArrayList<>();
    List<Card> wildCards = new ArrayList<>();

    /**
     * Build the highest-value collection of runs and melds, then use any leftover
     * wild cards in a valid group where possible.
     */
    public void createBestHand() {
        evaluateBestGrouping();
    }

    public void evaluateBestGrouping() {
        organizeCards();
        cardGroups = new ArrayList<>();

        if (deadwood.isEmpty()) {
            return;
        }

        List<Card> nonWildCards = deadwood.stream()
                .filter(card -> !card.isWild())
                .sorted(Comparator.comparing(Card::getCardRank))
                .collect(Collectors.toList());

        List<Card> availableWildCards = wildCards.stream()
                .sorted(Comparator.comparingInt(Card::getScoreValue).reversed())
                .collect(Collectors.toList());

        if (!nonWildCards.isEmpty()) {
            List<GroupCandidate> groupCandidates = buildGroupCandidates(nonWildCards, availableWildCards.size());
            SearchResult bestGrouping = findBestGrouping(nonWildCards, availableWildCards, groupCandidates);
            applyGroupingResult(bestGrouping, nonWildCards, availableWildCards);
        }

        placeUnusedWildCards(availableWildCards);
    }

    private List<GroupCandidate> buildGroupCandidates(List<Card> nonWildCards, int maxWildCount) {
        List<GroupCandidate> groupCandidates = new ArrayList<>();
        Set<String> seenCandidateKeys = new HashSet<>();
        buildMeldCandidates(nonWildCards, maxWildCount, groupCandidates, seenCandidateKeys);
        buildRunCandidates(nonWildCards, maxWildCount, groupCandidates, seenCandidateKeys);
        groupCandidates.sort(Comparator.comparingInt(GroupCandidate::getCardScore).reversed());
        return groupCandidates;
    }

    private void buildMeldCandidates(
            List<Card> nonWildCards,
            int maxWildCount,
            List<GroupCandidate> candidates,
            Set<String> seenCandidates
    ) {
        Map<Integer, List<Integer>> cardsByRank = new HashMap<>();
        for (int i = 0; i < nonWildCards.size(); i++) {
            int rank = nonWildCards.get(i).getCardRank().getRank();
            cardsByRank.computeIfAbsent(rank, ignored -> new ArrayList<>()).add(i);
        }

        for (List<Integer> indices : cardsByRank.values()) {
            int subsetLimit = 1 << indices.size();
            for (int subset = 1; subset < subsetLimit; subset++) {
                int cardMask = 0;
                int cardScore = 0;
                int cardCount = 0;

                for (int bit = 0; bit < indices.size(); bit++) {
                    if ((subset & (1 << bit)) == 0) {
                        continue;
                    }
                    int cardIndex = indices.get(bit);
                    cardMask |= 1 << cardIndex;
                    cardScore += nonWildCards.get(cardIndex).getScoreValue();
                    cardCount += 1;
                }

                int wildNeeded = Math.max(0, MIN_GROUP_SIZE - cardCount);
                if (wildNeeded > maxWildCount) {
                    continue;
                }

                addCandidate(candidates, seenCandidates, GroupType.MELD, cardMask, wildNeeded, cardScore);
            }
        }
    }

    private void buildRunCandidates(
            List<Card> nonWildCards,
            int maxWildCount,
            List<GroupCandidate> candidates,
            Set<String> seenCandidates
    ) {
        Map<Suit, List<Integer>> cardsBySuit = new EnumMap<>(Suit.class);
        for (int i = 0; i < nonWildCards.size(); i++) {
            cardsBySuit.computeIfAbsent(nonWildCards.get(i).getSuit(), ignored -> new ArrayList<>()).add(i);
        }

        for (List<Integer> suitIndices : cardsBySuit.values()) {
            suitIndices.sort(Comparator.comparingInt(index -> nonWildCards.get(index).getCardRank().getRank()));

            for (int start = 0; start < suitIndices.size(); start++) {
                int cardMask = 0;
                int cardScore = 0;

                for (int end = start; end < suitIndices.size(); end++) {
                    int cardIndex = suitIndices.get(end);
                    cardMask |= 1 << cardIndex;
                    cardScore += nonWildCards.get(cardIndex).getScoreValue();

                    int cardCount = end - start + 1;
                    if (cardCount < MIN_GROUP_SIZE - 1) {
                        continue;
                    }

                    int firstRank = nonWildCards.get(suitIndices.get(start)).getCardRank().getRank();
                    int lastRank = nonWildCards.get(suitIndices.get(end)).getCardRank().getRank();
                    int gapWilds = (lastRank - firstRank + 1) - cardCount;
                    int wildNeeded = Math.max(gapWilds, MIN_GROUP_SIZE - cardCount);

                    if (wildNeeded > maxWildCount) {
                        continue;
                    }

                    addCandidate(candidates, seenCandidates, GroupType.RUN, cardMask, wildNeeded, cardScore);
                }
            }
        }
    }

    private void addCandidate(
            List<GroupCandidate> candidates,
            Set<String> seenCandidates,
            GroupType groupType,
            int cardMask,
            int wildNeeded,
            int cardScore
    ) {
        String key = groupType + ":" + cardMask + ":" + wildNeeded;
        if (seenCandidates.add(key)) {
            candidates.add(new GroupCandidate(groupType, cardMask, wildNeeded, cardScore));
        }
    }

    private SearchResult findBestGrouping(
            List<Card> nonWildCards,
            List<Card> availableWildCards,
            List<GroupCandidate> groupCandidates
    ) {
        int[] wildScorePrefixSums = new int[availableWildCards.size() + 1];
        for (int i = 0; i < availableWildCards.size(); i++) {
            wildScorePrefixSums[i + 1] = wildScorePrefixSums[i] + availableWildCards.get(i).getScoreValue();
        }

        Map<SearchState, SearchResult> memo = new HashMap<>();
        int remainingCardMask = (1 << nonWildCards.size()) - 1;
        return searchBestGrouping(remainingCardMask, 0, groupCandidates, wildScorePrefixSums, memo);
    }

    private SearchResult searchBestGrouping(
            int remainingCardMask,
            int usedWildCount,
            List<GroupCandidate> groupCandidates,
            int[] wildScorePrefixSums,
            Map<SearchState, SearchResult> memo
    ) {
        SearchState state = new SearchState(remainingCardMask, usedWildCount);
        SearchResult cachedResult = memo.get(state);
        if (cachedResult != null) {
            return cachedResult;
        }

        SearchResult bestResult = new SearchResult(0, new ArrayList<>());
        int availableWildCount = wildScorePrefixSums.length - 1 - usedWildCount;

        for (GroupCandidate candidate : groupCandidates) {
            if ((candidate.cardMask & remainingCardMask) != candidate.cardMask) {
                continue;
            }
            if (candidate.wildNeeded > availableWildCount) {
                continue;
            }

            SearchResult nextResult = searchBestGrouping(
                    remainingCardMask ^ candidate.cardMask,
                    usedWildCount + candidate.wildNeeded,
                    groupCandidates,
                    wildScorePrefixSums,
                    memo
            );

            int totalScore = candidate.cardScore
                    + getWildScore(wildScorePrefixSums, usedWildCount, candidate.wildNeeded)
                    + nextResult.score;

            if (totalScore > bestResult.score) {
                List<GroupCandidate> selectedGroups = new ArrayList<>();
                selectedGroups.add(candidate);
                selectedGroups.addAll(nextResult.groups);
                bestResult = new SearchResult(totalScore, selectedGroups);
            }
        }

        memo.put(state, bestResult);
        return bestResult;
    }

    private int getWildScore(int[] wildScorePrefixSums, int usedWildCount, int wildNeeded) {
        return wildScorePrefixSums[usedWildCount + wildNeeded] - wildScorePrefixSums[usedWildCount];
    }

    private void applyGroupingResult(
            SearchResult bestGrouping,
            List<Card> nonWildCards,
            List<Card> availableWildCards
    ) {
        int usedWildCount = 0;

        for (GroupCandidate candidate : bestGrouping.groups) {
            CardGroup group = new CardGroup(candidate.groupType);

            for (int i = 0; i < nonWildCards.size(); i++) {
                if ((candidate.cardMask & (1 << i)) == 0) {
                    continue;
                }
                Card card = nonWildCards.get(i);
                card.setBeingUsed(true);
                group.cards.add(card);
            }

            for (int i = 0; i < candidate.wildNeeded; i++) {
                Card wildCard = availableWildCards.get(usedWildCount++);
                wildCard.setBeingUsed(true);
                group.cards.add(wildCard);
            }

            group.setPoints(calculateGroupScore(group));
            cardGroups.add(group);
        }
    }

    private void placeUnusedWildCards(List<Card> availableWildCards) {
        List<Card> unusedWildCards = availableWildCards.stream()
                .filter(card -> !card.isBeingUsed())
                .collect(Collectors.toList());

        if (unusedWildCards.isEmpty()) {
            return;
        }

        if (!cardGroups.isEmpty()) {
            CardGroup firstGroup = cardGroups.get(0);
            for (Card wildCard : unusedWildCards) {
                wildCard.setBeingUsed(true);
                firstGroup.cards.add(wildCard);
            }
            firstGroup.setPoints(calculateGroupScore(firstGroup));
            return;
        }

        if (unusedWildCards.size() >= MIN_GROUP_SIZE) {
            CardGroup wildGroup = new CardGroup(GroupType.WILD);
            for (Card wildCard : unusedWildCards) {
                wildCard.setBeingUsed(true);
                wildGroup.cards.add(wildCard);
            }
            wildGroup.setPoints(calculateGroupScore(wildGroup));
            cardGroups.add(wildGroup);
            return;
        }

        List<Card> unusedNaturalCards = deadwood.stream()
                .filter(card -> !card.isBeingUsed() && !card.isWild())
                .sorted(Comparator.comparingInt(Card::getScoreValue).reversed())
                .collect(Collectors.toList());

        if (unusedWildCards.size() >= MIN_GROUP_SIZE - 1 && !unusedNaturalCards.isEmpty()) {
            CardGroup meldGroup = new CardGroup(GroupType.MELD);
            Card anchorCard = unusedNaturalCards.get(0);
            anchorCard.setBeingUsed(true);
            meldGroup.cards.add(anchorCard);
            for (Card wildCard : unusedWildCards) {
                wildCard.setBeingUsed(true);
                meldGroup.cards.add(wildCard);
            }
            meldGroup.setPoints(calculateGroupScore(meldGroup));
            cardGroups.add(meldGroup);
        }
    }

    /**
     * Sorts deadwood by ascending card rank.
     */
    public void sortDeadwood() {
        deadwood.sort(Comparator.comparing(Card::getCardRank));
    }

    public int getDeadWoodValue() {
        int deadwoodValue = 0;
        for (Card card : deadwood) {
            if (!card.isBeingUsed()) {
                deadwoodValue += card.getScoreValue();
            }
        }
        return deadwoodValue;
    }

    private void organizeCards() {
        wildCards = extractWildCards();
        heartCards = extractCardsBySuit(Suit.HEARTS);
        diamondCards = extractCardsBySuit(Suit.DIAMONDS);
        clubCards = extractCardsBySuit(Suit.CLUBS);
        spadeCards = extractCardsBySuit(Suit.SPADES);

        heartCards.sort(Comparator.comparing(Card::getCardRank));
        clubCards.sort(Comparator.comparing(Card::getCardRank));
        diamondCards.sort(Comparator.comparing(Card::getCardRank));
        spadeCards.sort(Comparator.comparing(Card::getCardRank));
        sortDeadwood();

        if (RenderEngine.shouldRender()) {
            System.out.println(
                    Ansi.RED + "Hearts: " + heartCards.size()
                            + " diamonds: " + diamondCards.size()
                            + " clubs: " + clubCards.size()
                            + " spades: " + spadeCards.size()
                            + " wilds: " + wildCards.size() + Ansi.RESET
            );
        }

        for (Card card : deadwood) {
            card.setBeingUsed(false);
        }
    }

    private List<Card> extractCardsBySuit(Suit suit) {
        List<Card> suitedCards = new ArrayList<>();
        for (Card card : deadwood) {
            if (card.getSuit().equals(suit) && !card.isWild()) {
                suitedCards.add(card);
            }
        }
        return suitedCards;
    }

    private List<Card> extractWildCards() {
        return deadwood.stream().filter(Card::isWild).collect(Collectors.toList());
    }

    private int calculateGroupScore(CardGroup group) {
        return group.cards.stream().mapToInt(Card::getScoreValue).sum();
    }

    private static final class GroupCandidate {
        private final GroupType groupType;
        private final int cardMask;
        private final int wildNeeded;
        private final int cardScore;

        private GroupCandidate(GroupType groupType, int cardMask, int wildNeeded, int cardScore) {
            this.groupType = groupType;
            this.cardMask = cardMask;
            this.wildNeeded = wildNeeded;
            this.cardScore = cardScore;
        }

        private int getCardScore() {
            return cardScore;
        }
    }

    private static final class SearchState {
        private final int remainingMask;
        private final int usedWildCount;

        private SearchState(int remainingMask, int usedWildCount) {
            this.remainingMask = remainingMask;
            this.usedWildCount = usedWildCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SearchState that = (SearchState) o;
            return remainingMask == that.remainingMask && usedWildCount == that.usedWildCount;
        }

        @Override
        public int hashCode() {
            return Objects.hash(remainingMask, usedWildCount);
        }
    }

    private static final class SearchResult {
        private final int score;
        private final List<GroupCandidate> groups;

        private SearchResult(int score, List<GroupCandidate> groups) {
            this.score = score;
            this.groups = groups;
        }
    }
}
