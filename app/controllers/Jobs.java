package controllers;

import jobs.Updater;
import play.cache.Cache;
import play.libs.Codec;
import play.libs.F.Promise;
import play.mvc.Controller;

public class Jobs extends Controller {

    /**
     * Jobs activity page.
     * @param uuid
     */
    public static void result(String uuid) {
		String message = "";
		boolean done = false;
		int percent = 0;
		try {
			message	= (String)  Cache.get("Job_" + uuid + "_message");
			done = Boolean.parseBoolean(Cache.get("Job_" + uuid + "_done").toString());
			percent = Integer.parseInt(Cache.get("Job_" + uuid + "_percent").toString());
		} catch(Exception e) {;}
		render(uuid, message, done, percent);
    }
    
    /**
     * Job: updater
     */
    public static void update() {
		String uuid = Codec.UUID();
		Promise<String> p = new Updater(uuid).now();
		result(uuid);
    }
	
}
