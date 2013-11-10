package models;

public class MyHistoryProduct {
	private int id;
	private String title;
	private double paidPrice;
	private double paidShippingPrice;
	private String imgLink;
	private int quantityOrBidAmount;
	public MyHistoryProduct(int id, String title, double paidPrice,
			double paidShippingPrice, String imgLink, int quantityOrBidAmount) {
		super();
		this.id = id;
		this.title = title;
		this.paidPrice = paidPrice;
		this.paidShippingPrice = paidShippingPrice;
		this.imgLink = imgLink;
		this.quantityOrBidAmount = quantityOrBidAmount;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double getPaidPrice() {
		return paidPrice;
	}
	public void setPaidPrice(double paidPrice) {
		this.paidPrice = paidPrice;
	}
	public double getPaidShippingPrice() {
		return paidShippingPrice;
	}
	public void setPaidShippingPrice(double paidShippingPrice) {
		this.paidShippingPrice = paidShippingPrice;
	}
	public String getImgLink() {
		return imgLink;
	}
	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}
	public int getQuantityOrBidAmount() {
		return quantityOrBidAmount;
	}
	public void setQuantityOrBidAmount(int quantityOrBidAmount) {
		this.quantityOrBidAmount = quantityOrBidAmount;
	}
}
