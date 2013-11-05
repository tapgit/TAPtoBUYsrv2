package controllers;

import java.io.File;
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

import dbman.DBManager;

import play.*;
import play.mvc.*;
import play.libs.Json;
import testmodels.Test;
import views.html.*;

public class Application extends Controller {

	
	public static Result testdb(){
		DBManager.getAll();
		return ok();
	}
	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}
	
	public static Result getUserActivityHistory(int userId){////////Busca dentro de las ordenes donde este userId es seller y donde es buyer
		if(userId!=0){
			return notFound("User not found");//404
		}
		else{
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();//array de {forBid?,sold?,productObject}
			ObjectNode itemJson = null;
			ArrayList<Product> myHistoryItems = Test.getHistoryItemsList();
			for(Product p:myHistoryItems){
				itemJson = Json.newObject();
				if(p instanceof ProductForSale){
					itemJson.put("forBid", false);
				}
				else{
					itemJson.put("forBid", true);
				}
				if(p.getId() == 0 || p.getId() == 2){//sold items
					itemJson.put("sold", true);
				}
				else{
					itemJson.put("sold", false);
				}
				itemJson.putPOJO("item", Json.toJson(p));
				array.add(itemJson);
			}
			respJson.put("myHistory", array);
			return ok(respJson);//200
		}
	}

}
