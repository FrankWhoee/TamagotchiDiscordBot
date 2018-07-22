package bot.fun.tamagotchi;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.JsonObject;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

public class Util {
	
	public static String exec(String command) {
    	//Build command 
    	Process process = null;
		try {
			process = new ProcessBuilder(new String[] {"bash", "-c", command})
			    .redirectErrorStream(true)
			    .directory(new File("."))
			    .start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        //Read output
        StringBuilder out = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null, previous = null;
        
        String output = "";
        try {
			while ((line = br.readLine()) != null)
			    if (!line.equals(previous)) {
			        previous = line;
			        out.append(line).append('\n');
			        output += line + "\n";
			    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return output;
    }
	
	public static MessageEmbed getStats(JsonObject pet, User objUser) {
		
		
		EmbedBuilder eb = new EmbedBuilder();
		if(pet.has("Name")) {
			eb.setTitle(pet.get("Name").getAsString());
		}else {
			eb.setTitle("Unknown Pet");
		}
		float h = pet.get("Colour").getAsJsonArray().get(0).getAsFloat();
		float s = pet.get("Colour").getAsJsonArray().get(1).getAsFloat();
		float b = pet.get("Colour").getAsJsonArray().get(2).getAsFloat();
		
		Color c = new Color(h,s,b);
		
		eb.setColor(c);
		eb.setDescription(objUser.getAsMention() + "'s pet.");
		eb.addField("Hunger",pet.get("Hunger").getAsString(),false);
		eb.addField("Happy",pet.get("Happy").getAsString(),false);
		eb.addField("Discipline",pet.get("Discipline").getAsString(),false);
		eb.addField("Health",pet.get("Health").getAsString(),false);
		eb.addField("Age",pet.get("Age").getAsString(),false);
		eb.addField("Weight",pet.get("Weight").getAsString(),false);
		eb.addField("Poops",pet.get("Poops").getAsString(),false);
		eb.setImage(pet.get("ImageURL").getAsString());
		
		return eb.build();
	}
	
public static MessageEmbed getStatsSmall(JsonObject pet, User objUser) {
		
		
		EmbedBuilder eb = new EmbedBuilder();
		if(pet.has("Name")) {
			eb.setTitle(pet.get("Name").getAsString());
		}else {
			eb.setTitle("Unknown Pet");
		}
		float h = pet.get("Colour").getAsJsonArray().get(0).getAsFloat();
		float s = pet.get("Colour").getAsJsonArray().get(1).getAsFloat();
		float b = pet.get("Colour").getAsJsonArray().get(2).getAsFloat();
		
		Color c = new Color(h,s,b);
		
		eb.setColor(c);
		eb.setDescription(objUser.getAsMention() + "'s pet.");
		eb.addField("Hunger",pet.get("Hunger").getAsString(),false);
		eb.addField("Happy",pet.get("Happy").getAsString(),false);
		eb.addField("Discipline",pet.get("Discipline").getAsString(),false);
		eb.addField("Health",pet.get("Health").getAsString(),false);
		eb.addField("Age",pet.get("Age").getAsString(),false);
		eb.addField("Weight",pet.get("Weight").getAsString(),false);
		eb.addField("Poops",pet.get("Poops").getAsString(),false);		
		return eb.build();
	}

public static String getStatsCond(JsonObject pet, User objUser) {
	String reply = "```";
	if(pet.has("Name")) {
		reply += (pet.get("Name").getAsString()) + "\n";
	}else {
		reply += ("Unknown Pet");
	}
	float h = pet.get("Colour").getAsJsonArray().get(0).getAsFloat();
	float s = pet.get("Colour").getAsJsonArray().get(1).getAsFloat();
	float b = pet.get("Colour").getAsJsonArray().get(2).getAsFloat();

	Color c = new Color(h,s,b);
	
	float [] rgb = new float[3];
	c.getRGBColorComponents(rgb);
	reply += "\n" +  (objUser.getName() + "'s pet.");
	reply += "\n" +  ("Hunger: "+pet.get("Hunger").getAsString());
	reply += "\n" +  ("Happy: "+pet.get("Happy").getAsString());
	reply += "\n" +  ("Discipline: "+pet.get("Discipline").getAsString());
	reply += "\n" +  ("Health: "+pet.get("Health").getAsString());
	reply += "\n" +  ("Age: "+pet.get("Age").getAsString());
	reply += "\n" +  ("Weight: "+pet.get("Weight").getAsString());
	reply += "\n" +  ("Poops: "+pet.get("Poops").getAsString());
	reply += "\n" +  "Colour Code (RGB): #" + Integer.toHexString((int)(rgb[0] * 255)) + Integer.toHexString((int)(rgb[1] * 255)) + Integer.toHexString((int)(rgb[2] * 255));
	reply += "```";
	return reply;
}

	public static String getrA9() {
		String word = "rA9deVIAnt.";
		ArrayList<Character> l = new ArrayList<>();
		for(char c :  word.toCharArray()) //for each char of the word selectionned, put it in a list
		    l.add(c); 
		Collections.shuffle(l); //shuffle the list

		StringBuilder sb = new StringBuilder(); //now rebuild the word
		for(char c : l)
		  sb.append(c);

		word = sb.toString();
		return word;
	}
	
	public static String getrA9(String us) {
		String word = us;
		ArrayList<Character> l = new ArrayList<>();
		for(char c :  word.toCharArray()) //for each char of the word selectionned, put it in a list
		    l.add(c); 
		Collections.shuffle(l); //shuffle the list

		StringBuilder sb = new StringBuilder(); //now rebuild the word
		for(char c : l)
		  sb.append(c);

		word = sb.toString();
		return word;
	}
	
}
