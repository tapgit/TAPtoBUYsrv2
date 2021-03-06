package models;

public class ProductForAuctionInfo extends ProductForAuction {
	//item_info
	//private int productInfoId;
	private String product;
	private String model;
	private String brand;
	private String dimensions;
	private String description;
	public ProductForAuctionInfo(int id, String title, String timeRemaining,
			double shippingPrice, String imgLink, String sellerUsername,
			double sellerRate, double startinBidPrice, double currentBidPrice,
			int totalBids, String product, String model,
			String brand, String dimensions, String description) {
		super(id, title, timeRemaining, shippingPrice, imgLink, sellerUsername,
				sellerRate, startinBidPrice, currentBidPrice, totalBids);
		this.product = product;
		this.model = model;
		this.brand = brand;
		this.dimensions = dimensions;
		this.description = description;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getDimensions() {
		return dimensions;
	}
	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
