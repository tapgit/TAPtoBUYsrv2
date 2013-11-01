package controllers;

import java.util.ArrayList;

import models.Bid;
import models.MyBiddingsProduct;
import models.Product;
import models.ProductForAuction;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import testmodels.Test;

public class BidController extends Controller {
	
	public static Result placeBid(){
		ArrayList<Product> allItems = Test.getProductList();
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else{
			Bid newBid = new Bid(json.get("user_id").getIntValue(), json.get("product_id").getIntValue(), 
					json.get("amount").getDoubleValue());

			if(newBid.getUser_id()!=0){
				return notFound("User not found");//404
			}
			else if(!(newBid.getProduct_id() >=0 && newBid.getProduct_id() < 6)){
				return notFound("Product not found");//404
			}
			else if(!(allItems.get(newBid.getProduct_id()) instanceof ProductForAuction)){
				return badRequest("Cannot place bid on this item");//400
			}
			else {//item is an auction product
				ProductForAuction product = (ProductForAuction) allItems.get(newBid.getProduct_id());
				if(newBid.getAmount() <= product.getCurrentBidPrice()){
					return badRequest("Cannot place that amount on the item");
				}
				else{
					//place bid
					return ok("Bid has been placed");//200
				}	
			}
		}
	}
	
	public static Result getMyBiddings(int userId){
		if(userId!=0){
			return notFound("User not found");//404
		}
		else{
			ArrayList<MyBiddingsProduct> itemsIHavePlacedBids = Test.getMyBiddingsItemList();
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			ObjectNode itemJson = null;
			for(MyBiddingsProduct p : itemsIHavePlacedBids){
				itemJson = Json.newObject();
				itemJson.putPOJO("item", Json.toJson(p));
				array.add(itemJson);
			}
			respJson.put("myBiddingsItems", array);
			return ok(respJson);//200
		}
	}
	
}
