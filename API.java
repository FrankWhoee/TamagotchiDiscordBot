package bot.fun.tamagotchi;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class API {
	
	public static JsonObject getTrivia() {
		try {
			URL url = new URL("https://opentdb.com/api.php?amount=1&type=multiple");
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);
			JsonObject json = new JsonParser().parse(body).getAsJsonObject();
			return json;
		}catch(Exception e) {
			
		}
		return null;
	}
	
}
