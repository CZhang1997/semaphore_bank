
public class Teller implements Runnable {
	
	private int tellerNum;
	
	public Teller(int i) {
		this.tellerNum = i;
	}

	public int getTellerNum() {
		return tellerNum;
	}

	public void setTellerNum(int tellerNum) {
		this.tellerNum = tellerNum;
	}

	private void processTask(Customer curr) throws InterruptedException {
		
		int customerNum = curr.getNum();
		int balance = curr.getBalance();
		int newBalance = balance + curr.getCurrentTaskAmount();
		curr.setBalance(newBalance);
		
		Thread.sleep(400); 
		if(curr.getCurrentTaskAmount() > 0)
			System.out.println("Teller #" + this.tellerNum + " processes "
					+ "deposit of $" + curr.getCurrentTaskAmount() + " for customer " + customerNum);
		else
			System.out.println("Teller #" + this.tellerNum + " processes "
					+ "withdrawal of $" + Math.abs(curr.getCurrentTaskAmount()) + " for customer " + customerNum);
		BankSimulation.performTask[customerNum].release(); 
	}
	
	@Override
	public void run() {
		System.out.println("Teller " + this.tellerNum + " created");
		while(true){
			try {
				
				BankSimulation.readyForTeller.acquire();
				BankSimulation.waitInTellerLine.acquire();
				Customer curCustomer = BankSimulation.tellerLine.poll();
				System.out.println("Teller " + tellerNum + " begins serving customer " + curCustomer.getNum());
				BankSimulation.waitInTellerLine.release();
				curCustomer.setCurrentEmployee(this.tellerNum);
				BankSimulation.assigned[curCustomer.getNum()].release();
				BankSimulation.customerRequestTeller[this.tellerNum].acquire();
				if(curCustomer.getCurrentTask() != 0){
					
					curCustomer.setCurrentTaskAmount(-1 * curCustomer.getCurrentTaskAmount());
				}
				
				processTask(curCustomer);
			} catch (InterruptedException e) {
				System.out.println("An error occurred.");
			}
		}
	}
}
