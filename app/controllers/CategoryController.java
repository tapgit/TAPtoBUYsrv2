package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import dbman.DBManager;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class CategoryController extends Controller {

	
	public static Result getSubcategories(int parentCatId){

		try {
			Class.forName(DBManager.driver);
			Connection connection = DriverManager.getConnection(DBManager.db,DBManager.user,DBManager.pass);
			Statement statement = connection.createStatement();
			Statement statement2 = connection.createStatement();
			ObjectNode respJson = Json.newObject();
			ArrayNode array = respJson.arrayNode();
			ObjectNode catJson = null;
			
			ResultSet rset = statement.executeQuery("select *" +
					                                "from category natural join has_subcat as subcat(cat_id,parent_id)"+ 
													"where parent_id = " + parentCatId + ";");
			
			
			ResultSet rset2 = null;
			while(rset.next()){
				catJson = Json.newObject();
				catJson.put("catId", rset.getString(1));
				catJson.put("name", rset.getString(2));
				rset2 = statement2.executeQuery("select count(*)" +
											   "from category natural join has_subcat as subcat(cat_id,parent_id)"+ 
											   "where parent_id = " + rset.getString(1) + ";");
				rset2.next();
				if(rset2.getInt(1)==0){
					catJson.put("hasSubCategories", false );
				}
				else{
					catJson.put("hasSubCategories", true );
				}
				array.add(catJson);
			}
			respJson.put("subcategories", array);
			return ok(respJson);
		} catch (Exception e) {
			Logger.info("EXCEPTION ON CATEGORIES");
			e.printStackTrace();
			return notFound();
		}
	}
}
