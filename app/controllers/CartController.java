package controllers;

import java.util.ArrayList;

import models.Product;
import models.ProductForSale;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import testmodels.Test;

public class CartController extends Controller {


	public static Result getCartItems(int userId){	
		ArrayList<ProductForSale> itemsInCart = Test.getCartItemsList();
		
		if(userId==0){
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			ObjectNode itemJson = null;
			
			for(ProductForSale p: itemsInCart){
				itemJson = Json.newObject();
				itemJson.putPOJO("item", Json.toJson(p));
				array.add(itemJson);
			}
			respJson.put("cart", array);
			return ok(respJson);//200
		}
		else{
			return notFound("No cart found related to that user id");//404
		}
	}
	public static Result addItemToCart(int userId, int productId){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
			if(userId != 0){
				return notFound("No cart found related to that user id");//404
			}
			else if(!(productId >=0 && productId < 6)){
				return notFound("Product not found");//404
			}
			else{
				//Add item to cart
				
				return ok();//200 (item was added to cart successfully)
			}
		}
	}
	public static Result removeItemFromCart(int userId, int productId){
			if(userId != 0){
				return notFound("No cart found related to that user id");//404
			}
			else if(!(productId >=0 && productId < 6)){
				return notFound("Product not found");//404
			}
			else{
				//Delete item from cart
				
				return noContent();//204 (item removed from cart successfully)
			}
		}
	
	
}
