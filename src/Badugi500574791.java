import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Random;

public class Badugi500574791 implements BadugiPlayer {
	private static int count = 0;
	private int id;
	private String nick;
	private int position;
	private int totalRaises; // total number of bets and raises in the entire
	private boolean bluff;
	
	private int numberDroppedCards; // the number of hands I last dropped
								
	private Random rng = new Random();

	public Badugi500574791() {
		this.id = ++count;
		
	}

	public Badugi500574791(String nick) {
		this.nick = nick;
		
	}

	private static String[][] thresholdString = {
			// 0 draws remaining
			{ "Kh7h6d5c", "9h8d7s6c", "8h7d6s5c", "7h6d5s4c", "6h5d4s3c" },

			// 1 draw remaining
			{ "Kh9h8s7d", "Kh7h6d5c", "9h8d7s6c", "8h7d6s5c", "7h6d5s4c" },

			// 2 draws remaining
			{ "KhQh3h2s", "Kh9h8s7d", "Kh7h6d5c", "KhQdJsTc", "9h8d7s6c" },

			// 3 draws remaining
			{ "KhQh8h4s", "KhQh3h2s", "Kh9h8s7d", "Kh7h4d3s", "KhQdJsTc" }

	};

	private static BadugiHand[][] thresholdHands = new BadugiHand[4][5];
	static { // static initializer block is executed once when JVM loads the
				// class bytecode
		for (int draws = 0; draws < 4; draws++) {
			for (int bets = 0; bets < 5; bets++) {
				thresholdHands[draws][bets] = new BadugiHand(thresholdString[draws][bets]);
			}
		}
	}
	
	public void startNewHand(int position, int handsToGo, int currentScore) {
		this.position = position;
		totalRaises = 0;
		numberDroppedCards = 0;
		bluff = false;
		
	}

	public int initialAction(BadugiHand hand){
		List<Card> activeCards = hand.getActiveCards();
		int activeSize = activeCards.size();
		
		//if two of your cards have a value of 4 or lower, they are off suit and there are no pairs, continue
		int lessThan4 = 0;
		if(activeSize > 1){
			for(Card c: activeCards){
				if(c.getRank() < 5){
					lessThan4++;
				}
			}
		}
		if(lessThan4 > 1){
			return 0;
		}
		
		//three cards valued at 7 or lower and they are not suited or paired, continue
		boolean lessThan7 = true;
		if(activeSize > 2){
			for(Card c: activeCards){
				if(c.getRank() > 7){
					lessThan7 = false;
					break;
				}
			}
			if(lessThan7)
				return 0;
			else
				return -1;
		}else{//else fold
			return -1;
		}
		
		
	}
	
	public int bettingAction(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew) {
		List<Card> activeCards = hand.getActiveCards();
		int activeSize = activeCards.size();
		
		double ran = rng.nextDouble();
		
		
		if (toCall > 0) {
			totalRaises++;
		}
		//keep bluffing 
		if(bluff && opponentDrew != 0){
			//System.out.println("bluffing");
			return 1;
		}
		
		//A great way to bluff in Badugi is to stand pat on the draw rounds. 
		//Players will start to assume that you have a powerful hand and there is no need to draw further cards. 
		//Standing on the second draw instead of the first could be used in a semi-bluff or partial bluff. 
		//Players may then assume that you connected or completed your good hand on that draw and players will think 
		//you have at least a decent hand and they will be more likely to fold if they have a mediocre hand. 
		//This type of bluff is more powerful if you have a badugi hand, but with the weaker higher valued cards.
		
		
		//semi-bluff means you have a decent but not a great hand, the semi-bluff strategy also works well later 
		//in the round since you have something to work with when less people are going to want to fold. 
		//If you are going to semi-bluff, you should have at least a 4 card hand or a powerful 3 card hand like A-2-3 or else fold on anything else.
		
		
		//if 4 card badugi and lower than 10, raise else keep playing
		if(activeSize == 4){
			boolean lowerThanT = true;
			for(Card c : activeCards){
				if(c.getRank() > 9){
					lowerThanT = false;
					break;
				}
			}
			if(lowerThanT)
				return 1;
			else
				return 0;
		}
		
		//if he drew cards and you have a 4 hand badugi (doesnt work everytime)
		if(activeSize == 4 && opponentDrew > 0){
			bluff = true;
			return 1;
		}
		
		//detect dumb bluffs: if they are raising but drawing at the same time
		//if opponent was raising last round but drew 2 cards after
		if(totalRaises - bets > 1 && opponentDrew > 1){
			return 0;
		}
		
		
		if(drawsRemaining ==3){
			//Straight bluffing is generally good to do early on in the game. straight bluff usually has a horrible hand that would lose if anyone called it
			if(ran < 0.01 && activeSize < 3){
				bluff = true;
				return 1;
			}
		}
		
		//keep doing the initial action as long as there is no one betting
		if(totalRaises-1 < 1){
			return initialAction(hand);
		}
		
		
		
		
		if(drawsRemaining == 3){//initial cards given
			
			
			
			
			//snowing can be done early and works well if players are drawing lots of cards on each round, indicating many weak hands that will probably fold.
			
			return initialAction(hand);
			
			
			
		}else if(drawsRemaining == 2){// 1 draw done 
			
			//you having a 3 card hand with cards less than seven and all the cards should be off-suited
			
			
			//If players are not drawing card in any round, then you should be cautious and try determining if they are bluffing or not
			if(ran < 0.7 && opponentDrew == 0){
				return 0;
			}
			
			
			
		}else if(drawsRemaining == 1){// 2 draws done
			
			
			if(activeSize  < 3){
				return -1;
			}
			
			if(ran < 0.7 && opponentDrew == 0){
				return 0;
			}
			
			//watch to see if another player has been betting and raising while drawing cards at the same time. 
			//it may indicate that they have a powerful three card hand but not a badugi
			
			
			// use the snow strategy aggressively if there are few rounds of drawing left and a player has just drawn 2 cards, which indicates a very weak hand.
			
		}else if(drawsRemaining == 0){// no more draws
			
			
			//If they stood pat on the last draw, there is a chance they do have a badugi hand.
			
			
		}
		
		
		
		
		
		int beatsIdx = 0;
		if (opponentDrew < 0) {
			opponentDrew = 0;
		}
		while (beatsIdx < 5 && hand.compareTo(thresholdHands[drawsRemaining][beatsIdx]) > 0) {
			beatsIdx++;
		}
		int off = beatsIdx - bets - totalRaises / 3 + position + opponentDrew - 2;
		
		if (off < 0) { // Looks like we are behind
			if (ran < .6) {
				return -1;
			}
			if (ran < .8) {
				return 0;
			}
			return +1; // raise as a bluff anyway
		} else if (off == 0) { // Looks like we are par
			if (ran < .1) {
				return -1;
			}
			if (ran < .7) {
				return 0;
			} else
				return +1;
		} else { // Looks like we are ahead, ram and jam
			if (ran < .5 - .2 * off) {
				return 0;
			} else
				return +1;
		}
	}
	//draws remaining: including this round
	public List<Card> drawingAction(int drawsRemaining, BadugiHand hand, int pot, int dealerDrew) {
		List<Card> cardsToDrop;
		//r1: when you have 3 cards and are drawing one card to find that last suit, the odds are roughly 25% that you will come through.
		
		//r2: When you have an off-suited four card hand with no card higher than a jack, then there is no need to draw.
		
		
		// is always an advantage to be the last person to act or bet since you can see what everyone else has done. 
		//This provides a great opportunity to bluff because of the additional knowledge you now know
		
		
		cardsToDrop = hand.getInactiveCards();
		
		numberDroppedCards = cardsToDrop.size(); 
		
		if(bluff){
			return new ArrayList<Card>();
		}
		return cardsToDrop;
	}

	public void showdown(BadugiHand yourHand, BadugiHand opponentHand) {
	}

	public String getAgentName() {
		if (nick != null) {
			return nick;
		} else {
			return "0ToA100";
		}
	}

	public String getAuthor() {
		return "Saini, Taranpreet";
	}
}
