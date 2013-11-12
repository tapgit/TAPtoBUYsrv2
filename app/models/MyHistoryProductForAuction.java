package models;


public class MyHistoryProductForAuction extends MyHistoryProduct {
	private int bidsAmount;

	public MyHistoryProductForAuction(int id, int order_id, String title,
			double paidPrice, double paidShippingPrice, String imgLink,
			int bidsAmount) {
		super(id, order_id, title, paidPrice, paidShippingPrice, imgLink);
		this.bidsAmount = bidsAmount;
	}

	public int getBidsAmount() {
		return bidsAmount;
	}

	public void setBidsAmount(int bidsAmount) {
		this.bidsAmount = bidsAmount;
	}
	
}
