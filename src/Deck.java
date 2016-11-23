import java.util.*;

/**
 * A class whose objects represent individual decks of cards. A deck is shuffled using
 * the given RNG when created, after which 
 * @author Ilkka Kokkarinen
 */
public class Deck {

    // Use the same immutable 52 cards between all decks.
    private static Card[] cards = new Card[52];
    static {
        for(int suit = 0; suit < 4; suit++) {
            for(int rank = 0; rank < 13; rank++) {
                cards[suit*13+rank] = new Card(suit, rank);
            }
        }
    }

    // The cards that are still in the deck.
    private ArrayList<Card> currentCards;    
    private Random rng;   
    
    /**
     * Constructor for the class.
     * @param rng The random number generator used to shuffle the deck.
     */
    public Deck(Random rng) {
        this.rng = new Random();
        currentCards = new ArrayList<Card>(52);
        for(Card c: cards) { currentCards.add(c); }
        Collections.shuffle(currentCards, rng);
    }
    
    /** 
     * Draw one random card from the top of this deck.
     * @return The card that was drawn and removed from this deck.
     */
    public Card drawCard() {
        if(currentCards.size() < 1) { 
            throw new IllegalStateException("Trying to draw a card from an empty deck.");
        }
        return currentCards.remove(currentCards.size() - 1);
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
}