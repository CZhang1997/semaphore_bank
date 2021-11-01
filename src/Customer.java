import java.util.Random;

public class Customer implements Runnable{
	
	public final static int NUM_OF_VISIT = 3;
	private int visits;
	private int currentTask;
	private int balance;
	private int loanAmount;
	private int num;
	private int currentEmployee;
	private int currentTaskAmount;
	
	public Customer(int n, int v, int bal){
		this.num = n;
		this.visits = v;
		this.balance = bal;
		this.loanAmount = 0;
	}
	
	// Getter and Setter methods
	
	public int getNum(){
		return num;
	}
	
	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getVisits() {
		return visits;
	}

	public void setVisits(int visits) {
		this.visits = visits;
	}
	
	public int getCurrentTask() {
		return currentTask;
	}

	public void setCurrentTask(int currentTask) {
		this.currentTask = currentTask;
	}

	public int getCurrentTaskAmount() {
		return currentTaskAmount;
	}

	public void setCurrentTaskAmount(int currentTaskAmount) {
		this.currentTaskAmount = currentTaskAmount;
	}

	public int getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(int loanAmount) {
		this.loanAmount = loanAmount;
	}

	public int getCurrentEmployee() {
		return currentEmployee;
	}

	public void setCurrentEmployee(int currentEmployee) {
		this.currentEmployee = currentEmployee;
	}
	private int getTask() {
		Random randomNum = new Random();
		return randomNum.nextInt(3);
	}
	private int getTaskAmount() {
		Random randomNum = new Random();
		int randomAmount = (randomNum.nextInt(5) + 1) * 100;
		return randomAmount;
	}

	@Override
	public void run() {
		System.out.println("Customer #" + this.num + " created");
		while(this.visits != NUM_OF_VISIT){
			this.currentTask = getTask();
			this.currentTaskAmount = getTaskAmount();
	    	
	    	if(this.currentTask == 0 || this.currentTask == 1){
				try {
					BankSimulation.waitInTellerLine.acquire();
					BankSimulation.tellerLine.add(this);
					BankSimulation.waitInTellerLine.release();
					BankSimulation.readyForTeller.release();
					BankSimulation.assigned[this.num].acquire();
					Thread.sleep(100);
					if(this.currentTask == 0){
						System.out.println("Customer #" + this.getNum() + " "
							+ "requests of teller #" + this.currentEmployee + " to make a deposit of $" + this.getCurrentTaskAmount());
					}
					else{
						System.out.println("Customer #" + this.getNum() + " "
							+ "requests of teller #" + this.currentEmployee + " to make a withdrawal of $" + this.getCurrentTaskAmount());
					}
					BankSimulation.customerRequestTeller[this.currentEmployee].release();
					BankSimulation.performTask[this.num].acquire();
					Thread.sleep(100); 
					if(this.currentTask == 0){
						System.out.println("Customer #" + this.num + " gets receipt from teller #" + this.currentEmployee);
					}
					else{
						System.out.println("Customer #" + this.num + " gets cash and receipt from teller #" + this.currentEmployee);
					}
				} catch (InterruptedException e) {
					System.out.println("An error occurred.");
				}	
	    	}
	    	else if(this.currentTask == 2){
				try {
					BankSimulation.waitInOfficerLine.acquire();
					BankSimulation.officerLine.add(this);
					BankSimulation.waitInOfficerLine.release();
					BankSimulation.readyForOfficer.release();
					BankSimulation.assigned[this.num].acquire();
					Thread.sleep(100);
					System.out.println("Customer #" + this.getNum() + " "
							+ "requests of teller #" + this.currentEmployee + " to apply for a loan of $" + this.getCurrentTaskAmount());
					BankSimulation.customerRequestOfficer.release(); 
					BankSimulation.performTask[this.num].acquire();
					Thread.sleep(100); 
					System.out.println("Customer #" + this.num + " gets loan from loan officer #" + this.currentEmployee);
					
				} catch (InterruptedException e) {
					System.out.println("An error occurred.");
				}	
	    	}
	    	
			this.visits++; 
		}
		System.out.println("Customer #" + this.num + " departs the bank");
	}
}
