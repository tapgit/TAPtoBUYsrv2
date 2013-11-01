package controllers;

import java.util.ArrayList;

import models.Product;
import models.ProductForSale;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import testmodels.Test;

public class SearchController extends Controller {
	
	public static Result getSearchResults(String searchString){
		
		ArrayList<Product> items = Test.getProductList();

		ObjectNode respJson = Json.newObject();
		ArrayNode array = respJson.arrayNode();

		ObjectNode itemJson = null;
		for(Product p: items){
			itemJson = Json.newObject();
			if(p instanceof ProductForSale){ //for sale
				itemJson.put("forBid", false);
			}
			else{ //for auction
				itemJson.put("forBid", true);
			}
			itemJson.putPOJO("item", Json.toJson(p));
			array.add(itemJson);
		}
		respJson.put("results", array);
		return ok(respJson);
	}
}
