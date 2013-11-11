package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Address;
import models.Bid;
import models.Product;
import models.ProductForAuction;
import models.ProductForAuctionInfo;
import models.ProductForSale;
import models.ProductForSaleInfo;
import models.User;

import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;

import dbman.DBManager;




import play.Logger;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import testmodels.Test;

public class ProductController extends Controller {
	public static String andrImgDir = "http://10.0.2.2:9000/images/";
	
	public static Result getProductInfo(int productId){
		
		try{
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
			Statement statement = connection.createStatement();

			ObjectNode itemInfoJson = Json.newObject();
			String findDate[] = {"days","hours","minutes","seconds"};
			int tCount = 0;
			String timeRemaining = "";
			ResultSet rset = statement.executeQuery("select * from item_for_sale where iid = " + productId + ";"); 
			
			if(rset.next()){//if for sale
				itemInfoJson.put("forBid", false);
				rset = statement.executeQuery("select iid,ititle,ishipping_price, username,avg(stars),remaining_quantity,instant_price,product,model,brand,dimensions,description, " +
											  "to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
											  "to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds " +
											  "from item natural join item_for_sale natural join item_info natural join users natural join ranks as rnk(b_uid,uid,stars) " +
											  "where iid = " + productId + " " +
											  "group by iid,ititle,ishipping_price,username,remaining_quantity,instant_price,product,model,brand,dimensions,description;");
				rset.next();
				for(int i=0;i<4;i++){
					if(!rset.getString(findDate[i]).equals("00") && tCount < 2){
						timeRemaining+=rset.getString(findDate[i]) + findDate[i].charAt(0) + " ";
						tCount++;
					}
				}
				Product itemInfo = new ProductForSaleInfo(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
						andrImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"),rset.getInt("remaining_quantity"), rset.getDouble("instant_price"),
						rset.getString("product"), rset.getString("model"), rset.getString("brand"), rset.getString("dimensions"), rset.getString("description"));
				itemInfoJson.putPOJO("productInfo", Json.toJson(itemInfo));
				return ok(itemInfoJson);//200
			}
			else{//for auction
				itemInfoJson.put("forBid", true);
				rset = statement.executeQuery("select iid,ititle,ishipping_price, username,avg(stars),total_bids,current_bid_price,product,model,brand,dimensions,description, " +
											  "to_char(istart_sale_date + itime_duration - current_timestamp,'DD') as days,to_char(istart_sale_date + itime_duration - current_timestamp,'HH24') as hours, " +
											  "to_char(istart_sale_date + itime_duration - current_timestamp,'MI') as minutes, to_char(istart_sale_date + itime_duration - current_timestamp,'SS') as seconds " +
											  "from item natural join item_for_auction natural join item_info natural join users natural join ranks as rnk(b_uid,uid,stars) " +
											  "where iid = " + productId + " " +
											  "group by iid,ititle,ishipping_price,username,total_bids,current_bid_price,product,model,brand,dimensions,description;");
				if(rset.next()){
					for(int i=0;i<4;i++){
						if(!rset.getString(findDate[i]).equals("00") && tCount < 2){
							timeRemaining+=rset.getString(findDate[i]) + findDate[i].charAt(0) + " ";
							tCount++;
						}
					}
					Product itemInfo = new ProductForAuctionInfo(rset.getInt("iid"), rset.getString("ititle"), timeRemaining, rset.getDouble("ishipping_price"), 
							andrImgDir + "img" + rset.getInt("iid") +".jpg", rset.getString("username"), rset.getDouble("avg"), -1, rset.getDouble("current_bid_price"), 
							rset.getInt("total_bids"), rset.getString("product"), rset.getString("model"), rset.getString("brand"), rset.getString("dimensions"), rset.getString("description"));
							itemInfoJson.putPOJO("productInfo", Json.toJson(itemInfo));		
					return ok(itemInfoJson);//200 
				}
				else{
					return notFound("No product found with the requested id");//404
				}
			}
		}
		catch (Exception e) {
			Logger.info("EXCEPTION ON PRODUCT INFO");
			e.printStackTrace();
			return notFound();
		}
	}

	////Selling products

	public static Result getAllSellingProducts(int userId){
		if(userId != 16){
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
			if(userId !=16){
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
			if(userId != 16){
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
		if(userId != 16){
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


	//	public static Result buyNow(int productId){
	//		if(!(productId >=0 && productId < 6)){
	//			return notFound("Product not found");//404
	//		}
	//		else{
	//			Product buyNowProduct = Test.getProductList().get(productId);
	//			if(buyNowProduct instanceof ProductForSale){
	//				User theUser = Test.getUser();
	//				Address defualtShippingAddress = theUser.getShipping_addresses()[0];
	//				ObjectNode json =  Json.newObject();
	//				json.putPOJO("shipping_address", Json.toJson(defualtShippingAddress));
	//				json.putPOJO("buyNowProduct", Json.toJson(buyNowProduct));
	//				return ok(json);//200
	//			}
	//			else{
	//				return badRequest("Cannot buy instantly items in auction");//400
	//			}
	//		}
	//	}
	public static Result buyNow(int userId){
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest("Expecting Json data");//400
		} 
		else {
			try {
				Logger.info(json.toString());
				JSONArray array = (new JSONObject(json.toString())).getJSONArray("productIdsToBuy");
				ArrayList<Integer> productsIdsTobuy  = new ArrayList<Integer>();
				for(int i=0;i<array.length();i++){
					productsIdsTobuy.add(array.getInt(i));
				}
				//buscar los productos para cada productId en el array y devolverlo junto con el shippingaddrs y creditcards del user

				ArrayList<Product> items = Test.getProductList();
				ObjectNode respJson = Json.newObject();
				ArrayNode respArray = respJson.arrayNode();

				ObjectNode itemJson = null;
				double pricesTotal = 0;
				double shippingsTotal = 0;
				for(Product p: items){
					if(productsIdsTobuy.contains(p.getId())){
						itemJson = Json.newObject();
						if(p instanceof ProductForSale){ //for sale
							itemJson.put("forBid", false);
							pricesTotal+=((ProductForSale) p).getInstantPrice();
						}
						else{ //for auction
							itemJson.put("forBid", true);
							pricesTotal+=((ProductForAuction)p).getCurrentBidPrice();
						}
						itemJson.putPOJO("item", Json.toJson(p));
						respArray.add(itemJson);
						shippingsTotal+=p.getShippingPrice();
					}
				}
				respJson.put("productsToBuy", respArray);
				
				respJson.put("total", "$"+pricesTotal+ " (Shipping: $" + shippingsTotal +")");
				return ok(respJson);

			} catch (Exception e) {
				e.printStackTrace();
				return notFound();
			}
		}
	}
	public static Result placeOrder(int userId){
		return TODO;
	}

}
