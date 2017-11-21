package apps;
import java.util.Scanner;

public class LotterySimulator {

	
	public int [] userInput(){
		int [] numbersPicked = new int [6];
		Scanner numbers = new Scanner(System.in);
		for (int cnt = 0; cnt < 5; cnt++){
		    System.out.print("Enter your " + (cnt + 1) + " lottery number: ");
		    numbersPicked[cnt] = numbers.nextInt();
		}
		System.out.print("Enter your special lottery number: ");
	    numbersPicked[5] = numbers.nextInt();
	    return numbersPicked;
	}
	
	public int result(){
		int [] numSet = new int [6];
		numSet = userInput();
		int [] computersNumbers = new int[6];
		int match = 0;
		int timesRan = 0;
		
		while (match != 6){
			// generating lottery numbers
			match = 0;
			for (int cnt = 0; cnt < 5; cnt++){
				computersNumbers[cnt] = (int) (Math.random()*6)+1;
			}
			computersNumbers[5] = (int) (Math.random()*26
				)+1;
			if (computersNumbers[5] != numSet[5]){}
			else{
				match++;
				for (int cnt = 0; cnt < 5; cnt++){
					for (int cnt2 = 0; cnt2 < 5; cnt2++){
						if (computersNumbers[cnt] == numSet[cnt2]){
							match++;
							cnt2 = 5;
						}
					}
					if (match == 1){
						cnt = 5;
					}
				}
			}
			timesRan++;
		}

				
		return timesRan;
	}
	
	
	
	
	
	
	public static void main (String args[]){
		LotterySimulator t1 = new LotterySimulator();
		//t1.userInput();
		System.out.println(t1.result());
	}
	
	
}
