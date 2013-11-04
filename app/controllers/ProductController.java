package controllers;

import java.util.ArrayList;

import models.Address;
import models.Bid;
import models.Product;
import models.ProductForAuction;
import models.ProductForAuctionInfo;
import models.ProductForSale;
import models.ProductForSaleInfo;
import models.User;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import testmodels.Test;

public class ProductController extends Controller {
	
	public static Result getProductInfo(int productId){
		ArrayList<Product> productInfos = Test.getProductInfoList();
		int target = -1;
		for(Product pInfo: productInfos){
			if(pInfo.getId() == productId){
				target = pInfo.getId();
				break;
			}
		}
		if(target == -1){
			return notFound("No product found with the requested id");//404
		}
		else{
			ObjectNode itemInfoJson = Json.newObject();
			if(productInfos.get(target) instanceof ProductForSaleInfo){//for sale
				itemInfoJson.put("forBid", false);
			}
			else{//for auction
				itemInfoJson.put("forBid", true);
			}
			itemInfoJson.putPOJO("productInfo", Json.toJson(productInfos.get(target)));
			return ok(itemInfoJson);//200
		}
	}
	
	////Selling products
	
	public static Result getAllSellingProducts(int userId){
		if(userId != 0){
			return notFound("User not found");//404
		}
		else{
			ArrayList<Product> sellingItems = Test.getSellingItemsList();
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			ObjectNode itemJson = null;

			for(Product p: sellingItems){
				itemJson = Json.newObject();
				if(p instanceof ProductForAuction){
					itemJson.put("forBid", true);
				}
				else{
					itemJson.put("forBid", false);
				}
				itemJson.putPOJO("item", Json.toJson(p));
				array.add(itemJson);
			}
			respJson.put("mySellingItems", array);
			return ok(respJson);//200
		}
	}
	public static Result sellAProduct(int userId){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
			if(userId != 0){
				return notFound("User not found");//404
			}
			else{
				Product theItem = null;
				JsonNode productInfoJson = json.get("productInfo");
				if(json.get("forBid").getBooleanValue()){
					theItem = new ProductForAuctionInfo(-1, productInfoJson.get("title").getTextValue(),
							productInfoJson.get("timeRemaining").getTextValue(), productInfoJson.get("shippingPrice").getDoubleValue(),
							productInfoJson.get("imgLink").getTextValue(), null, -1,  productInfoJson.get("startinBidPrice").getDoubleValue(),  
							productInfoJson.get("currentBidPrice").getDoubleValue(),  productInfoJson.get("totalBids").getIntValue(),
							productInfoJson.get("product").getTextValue(),productInfoJson.get("model").getTextValue(),
							productInfoJson.get("brand").getTextValue(),productInfoJson.get("dimensions").getTextValue(),
							productInfoJson.get("description").getTextValue());
				}
				else{
					theItem = new ProductForSaleInfo(-1, productInfoJson.get("title").getTextValue(), 
							productInfoJson.get("timeRemaining").getTextValue(), productInfoJson.get("shippingPrice").getDoubleValue(),
							productInfoJson.get("imgLink").getTextValue(), null, -1, productInfoJson.get("remainingQuantity").getIntValue(), 
							productInfoJson.get("instantPrice").getDoubleValue(),
							productInfoJson.get("product").getTextValue(),productInfoJson.get("model").getTextValue(),
							productInfoJson.get("brand").getTextValue(),productInfoJson.get("dimensions").getTextValue(),
							productInfoJson.get("description").getTextValue());
				}
				//calculate an id for the item (current id is -1 (not assigned)) 
				//and then add it to the db
				theItem.setId(0);

				return ok();//200 (product is now on sale)
			}

		}
	}
	public static Result updateASellingProduct(int userId, int productId){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
			if(userId != 0){
				return notFound("User not found");//404
			}
			else if(!(productId >=0 && productId < 6)){
				return notFound("Product not found");//404
			}
			else{
				Product theItem = null;
				JsonNode productInfoJson = json.get("productInfo");
				if(json.get("forBid").getBooleanValue()){
					theItem = new ProductForAuctionInfo(productId, productInfoJson.get("title").getTextValue(),
							productInfoJson.get("timeRemaining").getTextValue(), productInfoJson.get("shippingPrice").getDoubleValue(),
							productInfoJson.get("imgLink").getTextValue(),  null, -1,  productInfoJson.get("startinBidPrice").getDoubleValue(),  
							productInfoJson.get("currentBidPrice").getDoubleValue(),  productInfoJson.get("totalBids").getIntValue(),
							productInfoJson.get("product").getTextValue(),productInfoJson.get("model").getTextValue(),
							productInfoJson.get("brand").getTextValue(),productInfoJson.get("dimensions").getTextValue(),
							productInfoJson.get("description").getTextValue());
				}
				else{
					theItem = new ProductForSaleInfo(productId, productInfoJson.get("title").getTextValue(), 
							productInfoJson.get("timeRemaining").getTextValue(), productInfoJson.get("shippingPrice").getDoubleValue(),
							productInfoJson.get("imgLink").getTextValue(), null, -1, 
							productInfoJson.get("remainingQuantity").getIntValue(),productInfoJson.get("instantPrice").getDoubleValue(),
							productInfoJson.get("product").getTextValue(),productInfoJson.get("model").getTextValue(),
							productInfoJson.get("brand").getTextValue(),productInfoJson.get("dimensions").getTextValue(),
							productInfoJson.get("description").getTextValue());
				}
				//update item information in the db

				return ok();//200 (product is now on sale)
			}

		}
	}
	public static Result quitFromSelling(int userId, int productId){ //Includes items for sale and items in auctions
		Logger.info("user ID = " + userId + " product Id to remove = " + productId);
		if(userId != 0){
			return notFound("No cart found related to that user id");//404
		}
		else if(!(productId >=0 && productId < 6)){
			return notFound("Product not found");//404
		}
		else{
			//Quit from sale
			return noContent();//204 (product removed from sale successfully)
		}
	}
	

	public static Result buyNow(int productId){
		if(!(productId >=0 && productId < 6)){
			return notFound("Product not found");//404
		}
		else{
			Product buyNowProduct = Test.getProductList().get(productId);
			if(buyNowProduct instanceof ProductForSale){
				User theUser = Test.getUser();
				Address defualtShippingAddress = theUser.getShipping_addresses()[0];
				ObjectNode json =  Json.newObject();
				json.putPOJO("shipping_address", Json.toJson(defualtShippingAddress));
				json.putPOJO("buyNowProduct", Json.toJson(buyNowProduct));
				return ok(json);//200
			}
			else{
				return badRequest("Cannot buy instantly items in auction");//400
			}
		}
	}
	
}
