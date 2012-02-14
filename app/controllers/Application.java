package controllers;

import play.*;
import play.cache.Cache;
import play.libs.Codec;
import play.libs.F.Promise;
import play.mvc.*;

import java.util.*;

import com.sun.xml.internal.bind.v2.TODO;

import jobs.Updater;
import models.*;

public class Application extends Controller {

    public static void index() {
    	/** TODO: add some authentication checks here **/
    	Articles.index();
    }
    
    /**
     * Jobs activity page.
     * @param uuid
     */
    public static void jobs(String uuid) {
		String message = "";
		boolean done = false;
		try {
			message	= (String)  Cache.get("Job_" + uuid + "_message");
			done = Boolean.parseBoolean(Cache.get("Job_" + uuid + "_done").toString());
		} catch(Exception e) {;}
		render(uuid, message, done);
    }
    
    /**
     * Job: updater
     */
    public static void update() {
		String uuid = Codec.UUID();
		Promise<String> p = new Updater(uuid).now();
		jobs(uuid);
    }

}