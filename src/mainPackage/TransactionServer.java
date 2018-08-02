package mainPackage;

import java.io.Serializable;

public class TransactionServer implements Serializable{
	private int from;
	private int to;
	private int amount;
	public TransactionServer(int from, int to, int amount)
	{
		this.from = from;
		this.to= to;
		this.amount = amount;
	}
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
}
