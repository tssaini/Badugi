import java.util.Dictionary;
import java.util.List;
import java.util.Random;

public class IBadugiPlayer implements BadugiPlayer {
	private static int count = 0;
	private int id;
	private String nick;
	private int position;
	private int totalRaises; // total number of bets and raises in the entire
	
	boolean raise;
								// hand
	private Random rng = new Random();

	public IBadugiPlayer() {
		this.id = ++count;
	}

	public IBadugiPlayer(String nick) {
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
		raise = false;
	}

	public int bettingAction(int drawsRemaining, BadugiHand hand, int bets, int pot, int toCall, int opponentDrew) {
		
		//A great way to bluff in Badugi is to stand pat on the draw rounds. 
		//Players will start to assume that you have a powerful hand and there is no need to draw further cards. 
		//Standing on the second draw instead of the first could be used in a semi-bluff or partial bluff. 
		//Players may then assume that you connected or completed your good hand on that draw and players will think 
		//you have at least a decent hand and they will be more likely to fold if they have a mediocre hand. 
		//This type of bluff is more powerful if you have a badugi hand, but with the weaker higher valued cards.
		
		
		//semi-bluff means you have a decent but not a great hand, the semi-bluff strategy also works well later 
		//in the round since you have something to work with when less people are going to want to fold. 
		//If you are going to semi-bluff, you should have at least a 4 card hand or a powerful 3 card hand like A-2-3 or else fold on anything else.
		
		
		//if 4 card badugi and lower than 8, raise 
		
		
		//if 4 card badugi and higher than 8, keep playing
		
		
		//detect bluff: if they are raising but drawing at the same time
		
		
		if(drawsRemaining == 3){//initial cards given
			
			//Straight bluffing is generally good to do early on in the game. traight bluff usually has a horrible hand that would lose if anyone called it
			
			
			//snowing can be done early and works well if players are drawing lots of cards on each round, indicating many weak hands that will probably fold.
			
			//if two of your cards have a value of 4 or lower, they are off suit and there are no pairs, continue
			
			//three cards valued at 7 or lower and they are not suited or paired, continue
			
			//else fold
			
			
		}else if(drawsRemaining == 2){// 1 draw done 
			//you having a 3 card hand with cards less than seven and all the cards should be off-suited
			
			
			 //If players are not drawing card in any round, then you should be cautious and try determining if they are bluffing or not
			
			
			
			
		}else if(drawsRemaining == 1){// 2 draws done
			
			//watch to see if another player has been betting and raising while drawing cards at the same time. 
			//it may indicate that they have a powerful three card hand but not a badugi
			
			
			// use the snow strategy aggressively if there are few rounds of drawing left and a player has just drawn 2 cards, which indicates a very weak hand.
			
		}else if(drawsRemaining == 0){// no more draws
			
			//If they stood pat on the last draw, there is a chance they do have a badugi hand.
			
			
		}
		
		
		
		
		if (toCall > 0) {
			totalRaises++;
		}
		int beatsIdx = 0;
		if (opponentDrew < 0) {
			opponentDrew = 0;
		}
		while (beatsIdx < 5 && hand.compareTo(thresholdHands[drawsRemaining][beatsIdx]) > 0) {
			beatsIdx++;
		}
		int off = beatsIdx - bets - totalRaises / 3 + position + opponentDrew - 2;
		double ran = rng.nextDouble();
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
		
		//r1: when you have 3 cards and are drawing one card to find that last suit, the odds are roughly 25% that you will come through.
		
		//r2: When you have an off-suited four card hand with no card higher than a jack, then there is no need to draw.
		
		
		// is always an advantage to be the last person to act or bet since you can see what everyone else has done. 
		//This provides a great opportunity to bluff because of the additional knowledge you now know
		
		
		return hand.getInactiveCards();
	}

	public void showdown(BadugiHand yourHand, BadugiHand opponentHand) {
	}

	public String getAgentName() {
		if (nick != null) {
			return nick;
		} else {
			return "IBadugiPlayer";
		}
	}

	public String getAuthor() {
		return "Saini, Taranpreet";
	}
}
