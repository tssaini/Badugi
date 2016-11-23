import java.util.*;

/**
 * This interface defines the methods that every badugi player subclass must implement.
 * @author Ilkka Kokkarinen
 */
public interface BadugiPlayer
{
    
    /**
     * The method to tell the agent that a new hand is starting.
     * @param position 0 if the agent is the dealer in this hand, 1 if the opponent.
     * @param handsToGo The number of hands left to play in this heads-up tournament.
     * @param currentScore The current score of the tournament.
     */
    public void startNewHand(int position, int handsToGo, int currentScore);
    
    /**
     * The method to ask the agent what betting action it wants to perform.
     * @param drawsRemaining How many draws are remaining after this betting round.
     * @param hand The current hand held by this player.
     * @param bets How many bets and raises there have been in this betting round.
     * @param pot The current size of the pot.
     * @param toCall The cost to call to stay in the pot.
     * @param opponentDrew How many cards the opponent drew in the previous drawing round. In the
     * first betting round, this argument will be -1.
     * @return The desired betting action given as an integer whose sign determines the action. Any negative
     * number means FOLD/CHECK, zero means CHECK/CALL, and any positive number means BET/RAISE.
     */
    public int bettingAction(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew);
    
    /**
     * The method to ask the agent which cards it wants to replace in this drawing round.
     * @param drawsRemaining How many draws are remaining, including this drawing round.
     * @param hand The current hand held by this player.
     * @param pot The current size of the pot.
     * @param dealerDrew How many cards the dealer drew in this drawing round. When this method is called
     * for the dealer, this argument will be -1.
     * @return The list of cards in the hand that the agent wants to replace.
     */
    public List<Card> drawingAction(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew);
    
    /**
     * The agent observes the showdown at the end of the hand.
     * @param yourHand The hand held by this agent.
     * @param opponentHand The hand held by the opponent.
     */
    public void showdown(BadugiHand yourHand, BadugiHand opponentHand);
    
    /**
     * Returns the nickname of this agent.
     * @return The nickname of this agent.
     */
    public String getAgentName();
    
    /**
     * Returns the author of this agent. The name should be given in the format "Last, First".
     * @return The author of this agent.
     */
    public String getAuthor();
}
