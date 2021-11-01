
public class LoanOfficer implements Runnable {
	
	private int officerNum;
	
	public LoanOfficer(int i) {
		this.officerNum = i;
	}
	
	public int getOfficerNum() {
		return officerNum;
	}

	public void setOfficerNum(int officerNum) {
		this.officerNum = officerNum;
	}
	
	private void processLoan(Customer curr) throws InterruptedException {
		int balance = curr.getBalance();
		curr.setBalance(balance + curr.getCurrentTaskAmount());
		curr.setLoanAmount(curr.getLoanAmount() + curr.getCurrentTaskAmount());
		Thread.sleep(400); 
		System.out.println("Loan Officer #" + this.officerNum + " approves "
				+ "loan of $" + curr.getCurrentTaskAmount() + " for customer #" + curr.getNum());
		BankSimulation.performTask[curr.getNum()].release(); // Signal this customer that task is done
	}

	@Override
	public void run() {
		System.out.println("Loan Officer " + this.officerNum + " created");
		while(true){
			try {
				
				BankSimulation.readyForOfficer.acquire();
				BankSimulation.waitInOfficerLine.acquire();
				Customer curCustomer = BankSimulation.officerLine.poll();
				System.out.println("Loan Officer " + this.officerNum + " begins serving customer " + curCustomer.getNum());
				BankSimulation.waitInOfficerLine.release();
				curCustomer.setCurrentEmployee(this.officerNum);
			    BankSimulation.assigned[curCustomer.getNum()].release();
				BankSimulation.customerRequestOfficer.acquire();
				processLoan(curCustomer);
				
			} catch (InterruptedException e) {
			}
		}
	}

}
