package models;

public class MyHistoryProductForSale extends MyHistoryProduct{
	private int quantity;

	public MyHistoryProductForSale(int id, int order_id, String title,
			double paidPrice, double paidShippingPrice, String imgLink,
			int quantity) {
		super(id, order_id, title, paidPrice, paidShippingPrice, imgLink);
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
