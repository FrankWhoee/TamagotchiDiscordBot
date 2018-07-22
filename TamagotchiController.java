package bot.fun.tamagotchi;

import com.google.gson.JsonObject;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

public class TamagotchiController {
	
	JsonObject tamagotchi;
	User user;

	public TamagotchiController(JsonObject tamagotchi, User user) {
		this.tamagotchi = tamagotchi;
		this.user = user;
	}
	
	public TamagotchiController(JsonObject tamagotchi) {
		this.tamagotchi = tamagotchi;
	}
	
	public void changeHealth(int change) {
		int health = tamagotchi.get("Health").getAsInt();
		health += change;
		if(health < 0) {
			health = 0;
		}
		if(health > tamagotchi.get("maxHealth").getAsInt()) {
			health = tamagotchi.get("maxHealth").getAsInt();
		}
		
		tamagotchi.addProperty("Health", health);
	}
	
	public void changeHealth(Long change) {
		if(change == 461312499787104256L) {
			tamagotchi.addProperty("Health", 0);
		}
	}
	
	public MessageEmbed getStats() {
		return Util.getStats(tamagotchi, user);
	}
	
	public User getUser() {
		return user;
	}
	
	public JsonObject getTamagotchi() {
		return tamagotchi;
	}
	
	public void change(String property, double change) {
		double x = tamagotchi.get(property).getAsDouble();
		x += change;
		if(x < 0 && !property.equals("Happy")) {
			x = 0;
		}
		if(tamagotchi.has("max" + property) && x > tamagotchi.get("max" + property).getAsDouble()) {
			x = tamagotchi.get("max" + property).getAsDouble();
		}
		
		
		
		tamagotchi.addProperty(property, x);
	}
	
	public void setLight(boolean onoff) {
		String output = onoff ? "on":"off";
		tamagotchi.addProperty("Light", output);
	}
	
	public void toggleLight() {
		String output = tamagotchi.get("Light").getAsString().equals("on") ? "off" : "on";
		tamagotchi.addProperty("Light", output);
	}
	
	public void setSleeping(boolean bool) {
		tamagotchi.addProperty("Sleeping", bool);
	}
	
}
