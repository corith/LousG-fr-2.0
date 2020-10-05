/****************************************************
 **             Cory Sebastian                     **
 **               Card class                       **
 ****************************************************/

public class Card
{
    private int number;
    private String suit;
    private int worth;
    private boolean isBeingUsed;
    private boolean isARun;
    private boolean isOfAKind;
    private boolean isWild = false;



    // getters
    public int getCardNumber() { return number; }
    public String getSuit() { return suit; }
    public int getWorth() { return worth; }
    public boolean isBeingUsed() { return isBeingUsed; }
    public boolean isARun() { return isARun; }
    public boolean isOfAKind() { return isOfAKind; }
    public boolean isWild() { return isWild; }
// setters
    public void setNumber(int number) { this.number = number; }
    public void setSuit(String suit)  { this.suit = suit; }
    public void setWorth(int worth) { this.worth = worth; }
    public void makeItRun() { this.isARun = !this.isBeingUsed; }
    public void makeItOfAKind() { this.isOfAKind = !this.isBeingUsed; }
    public void clearStatus() { this.isARun = false; this.isOfAKind = false; }

// used for setting the status of a card in a set
    public void release() { this.isBeingUsed = false; }
    public void use() { this.isBeingUsed = true; }

    public Card()
    {
        this.setSuit(null);
        this.setNumber(-1);
        worth = 0;
        isBeingUsed = false;
        isARun = false;
        isOfAKind = false;
        isWild = false;
    }

    public Card(String suit , int number)
    {
        this.setSuit(suit);
        this.setNumber(number);
        worth = 0;
        isARun = false;
        isOfAKind = false;
        isWild = false;
//        isWild = this.getCardNumber() == Player.getRound();
    }

    /**
        prints face cards and aces else prints the default card suit and number
     **/
    @Override
    public String toString()
    {
        if (this.getSuit() == null)
            return Ansi.HIGH_INTENSITY + Ansi.MAGENTA + "|_|" + Ansi.RESET;
        else if (this.getSuit().equals("<3"))
        {
            if (this.getCardNumber() >= 11 || this.getCardNumber() == 1)
                return faceCard(this) + " " + Ansi.RED + Ansi.HIGH_INTENSITY + this.getSuit() + Ansi.RESET;

            return this.getCardNumber() + " " + Ansi.RED + Ansi.HIGH_INTENSITY + "<3" + Ansi.RESET + "";
        } else if (this.getSuit().equals("<*")) {
            if (this.getCardNumber() >= 11 || this.getCardNumber() == 1)
                return faceCard(this) + " " + Ansi.RED + Ansi.HIGH_INTENSITY + this.getSuit() + Ansi.RESET;

            return this.getCardNumber() + " " + Ansi.RED + Ansi.HIGH_INTENSITY + "<*" + Ansi.RESET + "";
        } else if (this.getSuit().equals("^")) {
            if (this.getCardNumber() >= 11 || this.getCardNumber() == 1)
                return faceCard(this) + " " + Ansi.BACKGROUND_WHITE + Ansi.BLACK + Ansi.HIGH_INTENSITY + this.getSuit() + Ansi.RESET;

            return this.getCardNumber() + " " + Ansi.BACKGROUND_WHITE + Ansi.BLACK + Ansi.HIGH_INTENSITY + "^" + Ansi.RESET + "";
        } else if (this.getSuit().equals("#")) {
            if (this.getCardNumber() >= 11 || this.getCardNumber() == 1)
                return faceCard(this) + " " + Ansi.BACKGROUND_WHITE + Ansi.BLACK + Ansi.HIGH_INTENSITY + this.getSuit() + Ansi.RESET;

            return this.getCardNumber() + " " + Ansi.BACKGROUND_WHITE + Ansi.BLACK + Ansi.HIGH_INTENSITY + "#" + Ansi.RESET + "";
        } else
            return this.getCardNumber() + " " + this.getSuit() + "";
    }

    public String faceCard(Card card)
    {
        switch (card.getCardNumber()) {
            case 1:
                // make it an ace
                return "A";
            case 11:
                // make it a jack
                return "J";
            case 12:
                //make it a queen
                return "Q";
            case 13:
                // make a king
                return "K";
            default:
                return "oops";
        }
    }

    // Todo: check exampleTwo's class so the editor shuts up
    @Override
    public boolean equals(Object exampleTwo)
    {
        if (this == exampleTwo)
            return true;
        if (exampleTwo == null)
            return false;
        Card exampleDeux = (Card)exampleTwo;
        return getSuit().equals(exampleDeux.getSuit()) && getCardNumber() == exampleDeux.getCardNumber();
    }
} // end class Card




/************** BEGIN MAIN METHOD FOR TESTING PURPOSES ******************/

class CardTest
{
  public static void main(String[] args) {

    Card c1 = new Card("<3" , 5);
    Card c2 = new Card("<3" , 5);
    Card c3 = new Card("<*" , 3);
    Object test = new Card("<3" , 5);



    Card[] testCard = new Card[5];
    for (int i = 0; i < testCard.length; i++) {
      testCard[i] = new Card("<*" , 4);
      if (i == 3) {
        testCard[i] = new Card("^" , 9);
      }
      System.out.println(testCard[i]);
    }

    Card death = new Card("^" , 9);
    Card ref;
    for (int i = 0; i < testCard.length; i++) {
      ref = testCard[i];
      if (death.equals(ref)) {
        testCard[i] = new Card();
        System.out.println(testCard[i]);
      } else {
        System.out.println(testCard[i]);
      }
    }


    if (c1.equals(c2)) {
      System.out.println("EQUAL BITCH");
    }
    if (c1.equals(test)) {
      System.out.println("the object is equal");
    }
    if (!c1.equals(c3)) {
      System.out.println("they aint equal hehe");
    }
    else {
      System.out.println("STUPID BITCH");
    }

    //System.out.println(c3.isWild);
    //Card one = new Card("<3" , 2);
    //Card two = new Card("Spade" , 3);

    //System.out.print(one);
    //System.out.println(one.suit);

    //System.out.println(two.suit);
    //System.out.println(two.number);
  }
}
