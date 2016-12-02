import java.util.*;
import java.io.*;
import java.security.*;

public class BadugiRunner {

    // The initial ante posted by both players in the hand.
    private static final int ANTE = 1;
    // How many bets and raises are allowed during one betting round.
    private static final int MAXRAISES = 4;
    // In limit betting, the fixed bet sizes for the number of draws remaining.
    private static final int[] betSize = { 8, 4, 2, 2 };
    // Number of hands in each heads-up match.
    public static final int HANDS_PER_MATCH = 10_000;
    
    // A utility method to output a message to the given PrintWriter, forcing it to flush() after the message.
    private static void message(PrintWriter out, String msg) {
        if(out != null) {
            out.println(msg);
            out.flush();
        }
    }
    
    /**
     * Play one hand of badugi, with both players first placing an ANTE, after which the betting takes place in fixed size
     * increments depending on the street. For simplicity, both players are assumed to have deep enough stacks so that the
     * concept of all-in does not emerge.
     * @param deck The deck of cards used to play this hand.
     * @param players The two-element array of the players in this hand, in the order (dealer, opponent).
     * @param out The PrintWriter used to write the verbose messages about the events in this hand. If null, there is no output.
     * @param handsToGo How many hands are left in the current heads-up match.
     * @param currentScore The current score of player 0.
     * @return The result of the hand, as indicated by the amount won by player 0 from player 1. A negative result
     * therefore means that the player 0 lost the hand.
     */
    public static int playOneHand(EfficientDeck deck, BadugiPlayer[] players, PrintWriter out, int handsToGo, int currentScore) {
        message(out, "\n----\nStarting a new hand for " + players[0].getAgentName() + " vs. " + players[1].getAgentName() + ".");
        int pot = 2 * ANTE;
        deck.restoreCards();
        int[] totalBets = new int[2];
        totalBets[0] = totalBets[1] = ANTE;
        int[] drawCounts = new int[2];
        drawCounts[0] = drawCounts[1] = -1;
        BadugiHand[] hands = new BadugiHand[2];
        hands[0] = deck.drawBadugiHand();
        hands[1] = deck.drawBadugiHand();
        
        try {
            players[0].startNewHand(0, handsToGo, currentScore);
        } catch(Exception e) { return -1000; }
        try {
            players[1].startNewHand(1, handsToGo, -currentScore);
        } catch(Exception e) { return +1000; }
        // A single badugi hand consists of four betting streets.
        for(int drawsRemaining = 3; drawsRemaining >= 0; drawsRemaining--) {
            message(out, drawsRemaining + " draws remain, " + players[0].getAgentName() + " has " + hands[0] +
            ", " + players[1].getAgentName() + " has " + hands[1] + ".");
            // Betting action for the current street
            int currPlayer = 0; // Dealer starts the betting on each street
            int calls = -1; // Number of consecutive calls made in this betting round.
            int raises = 0; // The number of bets and raises made in this betting round.
            int action; // The current action chosen by the active player.
            
            while(calls < 1) { // Betting ends when there is a call, or when both players call in the beginning.
                int otherPlayer = 1-currPlayer;
                int toCall = totalBets[otherPlayer] - totalBets[currPlayer];
                try {
                    action = players[currPlayer].bettingAction(drawsRemaining, hands[currPlayer], raises, pot, toCall, drawCounts[otherPlayer]);
                    if(toCall == 0 && action < 0) { action = 0; }
                    message(out, players[currPlayer].getAgentName() + " " +
                        (action < 0 ? "FOLDS" : (action > 0 ? (toCall == 0 ? "BETS" : (raises > 1 ? "RERAISES" : "RAISES")) 
                        + " " + betSize[drawsRemaining] :
                        (toCall == 0 ? "CHECKS" : "CALLS " + betSize[drawsRemaining]) )) + ".");
                } catch(Exception e) { // Any failure is considered a fold.
                    message(out, players[currPlayer].getAgentName() + " bettingAction method failed! " + e);
                    message(out, players[otherPlayer].getAgentName() + " won " + totalBets[currPlayer] + ".");
                    return totalBets[currPlayer] * (currPlayer == 1 ? +1 : -1);
                }
                if(action < 0) { // current player folds, the hand is finished
                    message(out, players[otherPlayer].getAgentName() + " won " + totalBets[currPlayer] + ".");
                    return totalBets[currPlayer] * (currPlayer == 1 ? +1 : -1);
                }
                else if(action == 0 || raises == MAXRAISES) { // current player calls
                    calls++;
                    pot += toCall;
                    totalBets[currPlayer] += toCall;
                }
                else { // current player raises
                    pot += toCall + betSize[drawsRemaining];
                    totalBets[currPlayer] += toCall + betSize[drawsRemaining];
                    raises++;
                    calls = 0;
                }
                currPlayer = 1 - currPlayer;
            }
            
            if(drawsRemaining > 0) { // Drawing action for the current street
                for(currPlayer = 0; currPlayer <= 1; currPlayer++) {
                    List<Card> cards = hands[currPlayer].getAllCards();
                    List<Card> toReplace;
                    try {
                        toReplace = players[currPlayer].drawingAction(drawsRemaining, hands[currPlayer], pot,
                        currPlayer == 0 ? -1: drawCounts[0]);
                        if(toReplace.size() > 4) { throw new IllegalArgumentException("Trying to replace too many cards."); }
                        message(out, players[currPlayer].getAgentName() + " replaces cards " + toReplace + ".");
                        for(Card c: toReplace) {
                            if(!cards.contains(c)) {
                                throw new IllegalArgumentException("Trying to replace nonexistent card " + c);
                            }
                            hands[currPlayer].replaceCard(c, deck);
                        }
                        drawCounts[currPlayer] = toReplace.size();
                    } catch(Exception e) { // Again, any failure is considered a fold.
                        message(out, players[currPlayer].getAgentName() + " drawingAction method failed: " + e);
                        return totalBets[currPlayer] * (currPlayer == 1 ? +1 : -1);
                    }
                }
            }
        }
        message(out, "The hand has reached the showdown.");
        message(out, players[0].getAgentName() + " has " + hands[0] + ".");
        message(out, players[1].getAgentName() + " has " + hands[1] + ".");
        int showdown = hands[0].compareTo(hands[1]);
        int result;
        if(showdown < 0) { // Dealer lost
            message(out, players[1].getAgentName() +" won " + totalBets[0] + ".");
            result = -totalBets[0];
        }
        else if(showdown > 0) { // Dealer won
            message(out, players[0].getAgentName() +" won " + totalBets[1] + ".");
            result = totalBets[1];
        }
        else {
            message(out, "Both players brought equal badugi hands to showdown.");
            result = 0;
        }
        try { players[0].showdown(hands[0], hands[1]); } catch(Exception e) { }
        try { players[1].showdown(hands[1], hands[0]); } catch(Exception e) { }
        return result;
    }
    
    /**
     * Play the given number of hands of heads-up badugi between the two players, alternating the dealer position
     * between each round.
     * @param rng The random number generator used to initialize the deck in each hand.
     * @param players The two players participating in this heads-up match.
     * @param out The PrintWriter used to write the verbose messages about the events in this hand. To silence this output,
     * use e.g. new FileWriter("/dev/null") as this argument in an Unix system.
     * @param hands How many hands to play in this heads-up match.
     * @return The result of the match, as indicated by the amount won by player 0 from player 1. A negative result
     * therefore means that the player 0 lost the match.
     */
    public static int playHeadsUp(EfficientDeck deck, BadugiPlayer[] players, PrintWriter out, int hands) {
        int score = 0;
        BadugiPlayer[] thisRoundPlayers = new BadugiPlayer[2];
        while(--hands >= 0) {
            if(hands % 2 == 0) { thisRoundPlayers[0] = players[0]; thisRoundPlayers[1] = players[1]; }
            else { thisRoundPlayers[0] = players[1]; thisRoundPlayers[1] = players[0]; }
            int sign = (hands % 2 == 0 ? +1 : -1);
            score += sign * playOneHand(deck, thisRoundPlayers, out, hands, sign * score);
        }
        return score;
    }
    
    
    /**
     * Play the entire multiagent Badugi tournament, one heads-up match between every possible pair of agents.
     * @param agentClassNames A string array containing the names of agent subclasses.
     * @param out A PrintWriter to write the results of the individual heads-up matches into.
     * @param results A PrintWriter to write the tournament results into.
     */
    public static void badugiTournament(String[] agentClassNames, PrintWriter out, PrintWriter results) {
        
        // Create the list of player agents.
        List<BadugiPlayer> players = new ArrayList<BadugiPlayer>();
        for(String agent: agentClassNames) {
            Class c = null;
            try { 
                c = Class.forName(agent);
            } catch(Exception e) {
                System.out.println("Unable to load class bytecode for [" + agent + "]. Exiting.");
                return;
            }
            BadugiPlayer bp = null;
            try {
                bp = (BadugiPlayer)(c.newInstance());
            } catch(Exception e) {
                System.out.println("Unable to instantiate class [" + agent + "]. Exiting.");
                return;
            }
            players.add(bp);
        }
        int[] scores = new int[players.size()];
        Random rng;
        String seed = "This string is to be used as seed of secure random number generator " + System.currentTimeMillis();
        try { rng = new SecureRandom(seed.getBytes()); } 
        catch(Exception e) { 
            message(out, "Unable to create secure RNG: " + e);
            message(out, "Using system Random class instead.");
            rng = new Random();
        }
        // One and the same deck is used through the entire tournament.
        EfficientDeck deck = new EfficientDeck(rng);
        //Collections.shuffle(players); // Uncomment for extra randomization.
        
        // Play and score the individual heads-up matches.
        for(int i = 0; i < players.size(); i++) {
            for(int j = i+1; j < players.size(); j++) {
                BadugiPlayer[] playersArr = { players.get(i), players.get(j) } ;
                out.print("["+players.get(i).getAgentName() + "] vs. [" + players.get(j).getAgentName() + "]: "); 
                int result = playHeadsUp(deck, playersArr, null, HANDS_PER_MATCH);
                if(result < 0) { scores[j] += 2; }
                else if(result > 0) { scores[i] += 2; }
                else { scores[j]++; scores[i]++; }
                out.println(result);
                out.flush();
            }
        }
        
        // Output the results.
        for(int i = 0; i < players.size(); i++) {
            int max = 0;
            for(int j = 1; j < players.size(); j++) {
                if(scores[j] > scores[max]) { max = j; }
            }
            results.println(players.get(max).getAgentName() + " : " + scores[max]);
            scores[max] = -1;
        }
    }
    
    /**
     * Play three hands in the verbose mode. Suitable for watching your agents play.
     */
    public static void playThreeHandTournament() throws IOException {
        BadugiPlayer[] players = { 
            /* Replace these by instantiating your player classes */
            new RuleBasedBadugiPlayer("Alice"), 
            new RuleBasedBadugiPlayer("Bob")
        };
        Random rng;
        String seed = "This string is to be used as seed of secure random number generator";
        try { rng = new SecureRandom(seed.getBytes()); } 
        catch(Exception e) { 
            System.out.println("Unable to create secure RNG. Using system Random class instead.");
            rng = new Random();
        }
        EfficientDeck deck = new EfficientDeck(rng);
        int result = playHeadsUp(deck, players, new PrintWriter(System.out), 3);
        System.out.println("\n\nMatch result is " + result + ".");
    }
    
    /**
     * Run the entire badugi tournament between agents from classes listed inside this method.
     */
    public static void main(String[] args) throws IOException {
        /* Modify this array to include the player classes that participate in the tournament. */
        String[] playerClasses = {
          "RuleBasedBadugiPlayer",  
          "RuleBasedBadugiPlayer",
          "MyBadugiPlayer",
          "Badugi500574791",
          "IBadugiPlayer"
        };
        
        PrintWriter out = new PrintWriter(System.out);
        PrintWriter result = new PrintWriter(new FileWriter("results.txt"));
        badugiTournament(playerClasses, out, result);
        result.close();
    }   
}