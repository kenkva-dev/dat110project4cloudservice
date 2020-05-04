package no.hvl.dat110.ac.restservice;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;
import static spark.Spark.delete;
import static spark.Spark.options;

import com.google.gson.Gson;

/**
 * Hello world!
 *
 */
public class App {
	
	static AccessLog accesslog = null;
	static AccessCode accesscode = null;
	
	public static void main(String[] args) {

		if (args.length > 0) {
			port(Integer.parseInt(args[0]));
		} else {
			port(8080);
		}

		// objects for data stored in the service
		
		accesslog = new AccessLog();
		accesscode  = new AccessCode();
		
		options("/*", (request,response)->{
			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
			    response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}
			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if(accessControlRequestMethod != null){
			    response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}			
			return "OK";
		});
		
		after((req, res) -> {
  		  res.type("application/json");
  		});
		
		// for basic testing purposes
		get("/accessdevice/hello", (req, res) -> {
		 	Gson gson = new Gson();
		 	return gson.toJson("IoT Access Control Device");
		});
		
		put("/accessdevice/log", (req,res) -> {
        	Gson gson = new Gson();
        	AccessMessage msg = gson.fromJson(req.body(), AccessMessage.class);
			int id = accesslog.add(msg.getMessage());
			return gson.toJson(accesslog.get(id));
        });
		
		get("/accessdevice/log", (req,res) -> accesslog.toJson());
		
		put("/accessdevice/code", (req,res) -> {
        	Gson gson = new Gson();
        	accesscode = gson.fromJson(req.body(), AccessCode.class);
			return gson.toJson(accesscode);
        });
		
		get("/accessdevice/code", (req,res) -> accesscode.toJson());
		
		get("/accessdevice/log/:id", (req,res) -> {
			Gson gson = new Gson();
			System.out.println();
			AccessEntry entry = accesslog.get(Integer.valueOf(req.params(":id")));
			return gson.toJson(entry);
		});
		
		delete("/accessdevice/log", (req,res) -> {
        	accesslog.clear();
			return accesslog.toJson();
        });
    }
}

