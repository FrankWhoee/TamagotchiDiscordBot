package bot.fun.tamagotchi;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import org.apache.commons.*;
import org.apache.commons.lang.StringEscapeUtils;


public class App extends ListenerAdapter
{
	
	static JDA jda;
	static boolean isResponding = true;
	static JsonObject tamagotchis;
	
    public static void main( String[] args ) throws Exception
    {
    	//Initialise Discord Bot
        jda = new JDABuilder(AccountType.BOT).setToken(Key.TOKEN).buildBlocking();
        jda.getPresence().setStatus(OnlineStatus.ONLINE); 
        jda.getPresence().setGame(Game.of(Game.GameType.DEFAULT,Ref.prefix + "incubate"));
        jda.addEventListener(new App());
        
        //Read and parse JSON file
        String body = Util.exec("cat " + "../" + Ref.foldername + "/" + Ref.filename);
        tamagotchis = new JsonParser().parse(body).getAsJsonObject();
    }
        
    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
    	//Objects
    	User objUser = evt.getAuthor();
    	MessageChannel objMsgCh= evt.getChannel();
    	Message objMsg = evt.getMessage();
    	Guild objGuild = evt.getGuild();
    	
    	if(objUser.getIdLong() == Ref.myId && objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "toggleResponse")) {
    		String reply = isResponding ? "Tamagotchi enabled." : "Tamagotchi disabled.";
    		isResponding = isResponding ? false : true;
    		objMsgCh.sendMessage(reply).queue();
    	}
    	
    	if(!isResponding) {
    		return;
    	}
    	
    	String userId = objUser.getId();
    	if(tamagotchis.has(userId)) {
    		JsonObject pet = (JsonObject) tamagotchis.get(userId);

    		if(objMsg.getContentRaw().startsWith(Ref.prefix + "feed")) {
        		/*Goal: Increment Hunger by 1*/
    			pet.addProperty("Hunger", pet.get("Hunger").getAsInt() + 1);
    			if(pet.has("Name")) {
    				objMsgCh.sendMessage(objUser.getAsMention() + " You just fed " + pet.get("Name") + ". `Hunger: " + pet.get("Hunger") + "`").queue();
    			}else {
    				objMsgCh.sendMessage(objUser.getAsMention() + " You just fed your pet!" + " `Hunger: " + pet.get("Hunger") + "`").queue();
    			}
    			save();
        	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "play")) {
        		/*Goal: Ask a trivia question*/
        		
        		//Get question...
        		JsonObject trivia = API.getTrivia();

        		
        		//Construct String
        		String question = StringEscapeUtils.unescapeHtml(trivia.get("results").getAsJsonArray().get(0).getAsJsonObject().get("question").getAsString());
        		String message = "**Answer this trivia question:**"
        				+ "\n" + question + ""
        				+ "\n :one: "
        				+ "\n :two: "
        				+ "\n :three: "
        				+ "\n :four: ";
        		
        		objMsgCh.sendMessage(message).queue();
        		
//    			pet.addProperty("Happy", pet.get("Happy").getAsInt() + 1);
//    			if(pet.has("Name")) {
//    				objMsgCh.sendMessage(objUser.getAsMention() + " You just played with " + pet.get("Name") + ". `Happy: " + pet.get("Happy") + "`").queue();
//    			}else {
//    				objMsgCh.sendMessage(objUser.getAsMention() + " You just played with your pet!"+ " `Happy: " + pet.get("Happy") + "`").queue();
//    			}
//    			save();
        	}
    		
			
		}else {
			if(objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "incubate")) {
	    		JsonObject value = new JsonObject();
	    		//Initialise values
	    		value.addProperty("Hunger", 0);
	    		value.addProperty("Happy", 0);
	    		value.addProperty("Discipline", 0);
	    		value.addProperty("Health", 50);
	    		value.addProperty("Age", 0);
	    		value.addProperty("Weight", 5);
	    		value.addProperty("Light", "on");
	    		
	    		//Initialise colour
	    		float h = (float) Math.random();
	    		float s = (float) Math.random();
	    		float b = (float) Math.random();
	    		Color random = new Color(h,s,b);
	    		JsonArray colours = new JsonArray();
	    		colours.add(h); colours.add(s); colours.add(b);
	    		value.add("Colour", colours);
	    		
	    		//Register user into json
	    		tamagotchis.add(userId, value);
	    		
	    		
	    		//Save json
	    		save();
	    		
	    		//After file is saved, send output to user.   		
	    		//Create an embedded message
	    		EmbedBuilder eb = new EmbedBuilder();
	    		eb.setTitle("Unknown Pet");
	    		
	    		eb.setColor(random);
	    		eb.setDescription(objUser.getAsMention() + "'s pet.");
	    		eb.addField("Hunger",value.get("Hunger").getAsString(),false);
	    		eb.addField("Happy",value.get("Happy").getAsString(),false);
	    		eb.addField("Discipline",value.get("Discipline").getAsString(),false);
	    		eb.addField("Health",value.get("Health").getAsString(),false);
	    		eb.addField("Age",value.get("Age").getAsString(),false);
	    		eb.addField("Weight",value.get("Weight").getAsString(),false);
	    		eb.setImage("https://raw.githubusercontent.com/FrankWhoee/TamagotchiDiscordBot/master/egg.jpeg");
	    		
	    		//Send embedded message to user
	    		objMsgCh.sendMessage(eb.build()).queue();
	    	}
		}
    	
    	if(objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "help")) {
    		objMsgCh.sendMessage("```How to raise a tamagotchi:"
    				+ "\n" + Ref.prefix + "incubate: Start your very own tamagotchi! This registers you into the tamagotchi database and you can begin your tamagotchi."
    				+ ""
    				+ ""
    				+ ""
    				+ "```").queue();
    	}
    }
    
    public static void save() {
    	Util.exec("rm " + "../" + Ref.foldername + "/" + Ref.filename);
		File file = new File("../" + Ref.foldername + "/" + Ref.filename);
		try {
			PrintWriter out = new PrintWriter(file);
			System.out.println(tamagotchis.toString());
			out.println(tamagotchis.toString());
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error occured while writing to " + file.getPath() + ". File not found.");
		}
    }
}
