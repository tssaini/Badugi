import java.util.*;

public class HumanBadugiPlayer implements BadugiPlayer
{

    private static int count = 0;
    private int id;
    
    public HumanBadugiPlayer() { this.id = ++count; }
    
    public void startNewHand(int position, int handsToGo, int currentScore) {
        
    }
    
    public int bettingAction(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Betting decision for human player " + id + " with " + drawsRemaining + " draws remaining.");
        System.out.println("Current hand is " + hand + ", pot is " + pot + ", " + toCall + " to call.");
        if(opponentDrew > -1) { 
            System.out.println("The opponent drew " + opponentDrew + " cards in the previous draw.");
        }
        while(true) {
            System.out.println("(C)all, (R)aise or (F)old?");
            String ans = scanner.next().toLowerCase().trim();
            if(ans.startsWith("c")) { return 0; }
            if(ans.startsWith("r") || ans.startsWith("b")) { return +1; }
            if(ans.startsWith("f")) { return -1; }
        }
    }
    
    public List<Card> drawingAction(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Drawing decision for human player " + id + ", " + drawsRemaining + " draws remaining.");
        System.out.println("Current hand is " + hand);
        if(dealerDrew > -1) { System.out.println("The opponent drew " + dealerDrew + " cards."); }
        System.out.println("Enter 4-letter string of 'd' and 'k' for draw and keep:");
        String ans = scanner.next().toLowerCase().trim();
        ArrayList<Card> drawCards = new ArrayList<Card>();
        for(int i = 0; i < 4; i++) {
            if(ans.charAt(i) == 'd') { drawCards.add(hand.getAllCards().get(i)); }
        }
        return drawCards;
    }
    
    public void showdown(BadugiHand yourHand, BadugiHand opponentHand) {
        System.out.println("Showdown seen by human player " + id + ".");
        System.out.println("Your hand at showdown: " + yourHand);
        System.out.println("Opponent hand at showdown: " + opponentHand);
    }
    
    public String getAgentName() { return "HumanPlayer #" + id; }
    
    public String getAuthor() { return "Kokkarinen, Ilkka"; }
    
}
