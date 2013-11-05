package controllers;

import java.util.ArrayList;

import models.Bid;
import models.MyBiddingsProduct;
import models.Product;
import models.ProductForAuction;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
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
	
	public static Result getBidList(int productId){
		//El productId recibido es de un item for auction que esta relacionado a 0 o mas tuplas de la tabla Bid
		//Cada una de esas tuplas (filas) son bids que se le han puesto a este item.  Y cada una de ellas esta tambien
		//relacionada al usuario que puso el bid.  Por lo tanto, de ahi podemos sacar los bidders (de la tabla users) que 
		//que pusieron bids en este producto.
		
		//Buscar en la tabla de item_for_auction el item con este productId, sacarle el uId(user id q lo puso en subasta),
		//buscar en la tabla de bid los bids asociados a este item con iId y buscar los users q pusieron estos bids con uId.
		
		ObjectNode respJson = Json.newObject();
		ArrayNode array = respJson.arrayNode();
		ObjectNode bidderAndAmount = Json.newObject();
		bidderAndAmount.put("username", "Jose");bidderAndAmount.put("amount", 4.4);
		array.add(bidderAndAmount);
		bidderAndAmount = Json.newObject();
		bidderAndAmount.put("username", "Apu");bidderAndAmount.put("amount", 5.4);
		array.add(bidderAndAmount);
		bidderAndAmount = Json.newObject();
		bidderAndAmount.put("username", "Juaniquito");bidderAndAmount.put("amount", 3.4);
		array.add(bidderAndAmount);
		bidderAndAmount = Json.newObject();
		bidderAndAmount.put("username", "AAAhhh!");bidderAndAmount.put("amount", 9.9);
		array.add(bidderAndAmount);
		respJson.put("bidlist",array);
		Logger.info("Product ID Bidlist:" + productId);
		return ok(respJson);
	}
	
}
