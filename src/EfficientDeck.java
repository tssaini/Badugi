import java.util.*;

/**
 * A more efficient implementation of deck to draw cards from in the game of badugi.
 * This class uses only one random number per each card drawn, instead of shuffling
 * the entire deck and drawing just a couple of cards from it.
 * 
 * @author Ilkka Kokkarinen
 * @version Wednesday November 30, 2016
 */
public class EfficientDeck
{
    // Use the same immutable 52 cards all the time.
    private static Card[] cards = new Card[52];
    static {
        for(int suit = 0; suit < 4; suit++) {
            for(int rank = 0; rank < 13; rank++) {
                cards[suit*13+rank] = new Card(suit, rank);
            }
        }
    }
    
    private Random rng;
    private Card[] deckCards;
    private int cardsRemaining;
    
    /**
     * Constructor for the class.
     * @param rng The random number generator used to shuffle the deck.
     */
    public EfficientDeck(Random rng)
    {
        this.rng = rng;
        this.deckCards = new Card[52];
        System.arraycopy(cards, 0, deckCards, 0, 52);
        this.cardsRemaining = 52;
    }

    /** 
     * Draw one card from random position of the remaining cards, moving that card
     * to the right subarray that contains cards that have already been drawn.
     * @return The card that was drawn and removed from this deck.
     */
    public Card drawCard() {
        if(cardsRemaining < 1) { 
            throw new IllegalStateException("Trying to draw a card from an empty deck.");
        }
        int idx = this.rng.nextInt(cardsRemaining);
        Card result = deckCards[idx];
        deckCards[idx] = deckCards[--cardsRemaining];
        deckCards[cardsRemaining] = result;
        return result;
    }
    
    /**
     * Put all the drawn cards back to the deck.
     */
    public void restoreCards() {
        cardsRemaining = 52;
    }
    
    /**
     * For debugging purposes, a toString method for this class.
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < 52; i++) {
            result.append(deckCards[i]);
            if(i == cardsRemaining) { result.append(" | "); }
            else { result.append(" "); }
        }
        return result.toString();
    }
    
    /**
     * Create a new four-card badugi hand by drawing from the top of this deck.
     * @return The badugi hand object thus created.
     */
    public BadugiHand drawBadugiHand() {
        List<Card> cards = new ArrayList<Card>();
        for(int i = 0; i < 4; i++) {
            cards.add(drawCard());
        }
        return new BadugiHand(cards);
    }    
    
    public static void demo() {
        EfficientDeck deck = new EfficientDeck(new Random());
        for(int i = 0; i < 10; i++) {
            System.out.println(deck);
            for(int j = 0; j < 13; j++) {
                BadugiHand hand = deck.drawBadugiHand();
                System.out.println(hand);
            }
            System.out.println("---------------");
            deck.restoreCards();
        }
    }
}