import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BankSimulation {
	
	public final static int NUM_OF_CUSTOMERS = 5;
	public final static int NUM_OF_TELLERS = 2;
	public static Semaphore readyForTeller = new Semaphore(0,true);
	public static Semaphore readyForOfficer = new Semaphore(0,true);
	public static Queue<Customer> tellerLine = new LinkedList<Customer>();
	public static Queue<Customer> officerLine = new LinkedList<Customer>();
	public static Semaphore waitInTellerLine = new Semaphore(1, true);
	public static Semaphore waitInOfficerLine = new Semaphore(1, true);
	public static Semaphore[] customerRequestTeller = {new Semaphore(0, true), new Semaphore(0, true)};
	public static Semaphore[] performTask = {new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true)};
	public static Semaphore[] assigned = {new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true), new Semaphore(0, true)};
	public static Semaphore customerRequestOfficer = new Semaphore(0, true);
	
	public static void main(String[] args) {
		
		System.out.println("...Starting bank simulation...");
		Thread[] tellers = new Thread[NUM_OF_TELLERS];
		for(int i=0; i < NUM_OF_TELLERS; i++){
			tellers[i] = new Thread(new Teller(i));
			tellers[i].start();
		}
		Thread loanOfficer = new Thread(new LoanOfficer(0));
		loanOfficer.start();
		Thread[] customers = new Thread[NUM_OF_CUSTOMERS];
		Customer[] customerValues = new Customer[NUM_OF_CUSTOMERS];
		for(int i=0; i < NUM_OF_CUSTOMERS; i++){
			Customer newCustomer = new Customer(i,0,1000);
			customerValues[i] = newCustomer;
			customers[i] = new Thread(newCustomer);
			customers[i].start();
		}
		for(int i=0; i < NUM_OF_CUSTOMERS; i++){
			try {
				customers[i].join();
			} catch (InterruptedException e) {
				System.out.println("Could not join thread.");
			}
			System.out.println("Customer " + i + " is joined by main");
		}
		System.out.println("\n\t\tBanking Simulation Summary");
		System.out.println("\tEnding Balance\tLoan Amount");
		int balanceTotal = 0;
		int loanTotal = 0;
		for(int i=0; i< NUM_OF_CUSTOMERS; i++){
			balanceTotal += customerValues[i].getBalance();
			loanTotal += customerValues[i].getLoanAmount();
			System.out.println("Customer " + i + "\t" + customerValues[i].getBalance()
					+ "\t" + customerValues[i].getLoanAmount());
		}
		System.out.println("\nTotals \t" + balanceTotal + "\t" + loanTotal);
		System.out.println("\nEnd of simulation!");
		System.exit(0);
	}

}
