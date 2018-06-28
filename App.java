package bot.fun.tamagotchi;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.core.events.emote.GenericEmoteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import org.apache.commons.*;
import org.apache.commons.lang.StringEscapeUtils;


public class App extends ListenerAdapter
{
	
	static JDA jda;
	static boolean isResponding = true;
	static JsonObject tamagotchis;
	//Key: Comment ID
	//Value: Question object
	static Map<String,TriviaQuestion> triviaRequests = new HashMap<String,TriviaQuestion>();
	static String tempStr = "";
	
	
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
        		/*Goal: Increment Hunger according to the food type*/
    			String input = objMsg.getContentRaw();
    			String food = input.substring(5).trim();
    			String type = Ref.foodEmojis.get(food);
    			if(type.equals("meal")) {
    				pet.addProperty("Hunger", pet.get("Hunger").getAsInt() + 2);
    			}else if(type.equals("sweet")) {
    				pet.addProperty("Hunger", pet.get("Hunger").getAsInt() + 1);
    				pet.addProperty("Happy", pet.get("Happy").getAsInt() + 1);
    				pet.addProperty("Health", pet.get("Health").getAsInt() - 1);
    			}else if(type.equals("snack")) {
    				pet.addProperty("Hunger", pet.get("Hunger").getAsInt() + 1);
    				pet.addProperty("Happy", pet.get("Happy").getAsInt() + 1);
    			}else if(type.equals("alcohol")) {
    				pet.addProperty("Happy", pet.get("Happy").getAsInt() + 10);
    				pet.addProperty("Health", pet.get("Health").getAsInt() - 10);
    			}else {
    				objMsgCh.sendMessage("Your pet can't eat nothing! Put in a food emoji for your pet!").queue();
    				return;
    			}
    			
    			if(pet.has("Name")) {
    				objMsgCh.sendMessage(objUser.getAsMention() + " You just fed " + pet.get("Name") + " some " + food + ". `Hunger: " + pet.get("Hunger") + "/" + pet.get("maxHunger") + "`").queue();
    			}else {
    				objMsgCh.sendMessage(objUser.getAsMention() + " You just fed your pet some " + food + "! `Hunger: " + pet.get("Hunger") +"/" + pet.get("maxHunger") + "`").queue();
    			}
    			
    			if(pet.get("Hunger").getAsInt() > pet.get("maxHunger").getAsInt()) {
    				pet.addProperty("Hunger", 0);
    				pet.addProperty("Health", pet.get("Health").getAsInt() - 25);
    				objMsgCh.sendMessage(objUser.getAsMention() + " Your pet is vomiting! Your pet is now sick. Give it some medicine!"
    						+ "\n`Hunger: " + pet.get("Hunger") + "/" + pet.get("maxHunger") + "`"
    						+ "\n'Health: " + pet.get("Health") + "/" + pet.get("maxHealth") + "`").queue();
    				
    			}
    			
    			save();
        	}if(objMsg.getContentRaw().startsWith(Ref.prefix + "medicine")) {
        		/*Goal: Set Health to max*/
        		if(pet.get("Health").getAsInt() < pet.get("maxHealth").getAsInt()) {
        			pet.add("Health", pet.get("maxHealth"));
        			if(pet.has("Name")) {
        				objMsgCh.sendMessage(objUser.getAsMention() + pet.get("Name").getAsString() + " has been healed! " + ". `Health: " + pet.get("Health") + "/" + pet.get("maxHealth") + "`").queue();
        			}else {
        				objMsgCh.sendMessage(objUser.getAsMention() +  " Your pet has been healed! " + " `Health: " + pet.get("Health") + "/" + pet.get("maxHealth") + "`").queue();
        			}
        			
        		}
    			save();
        	}if(objMsg.getContentRaw().startsWith(Ref.prefix + "stats")) {
        		/*Goal: Show pet's statistics to user*/
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
	    		eb.setImage("https://raw.githubusercontent.com/FrankWhoee/TamagotchiDiscordBot/master/egg.jpeg");
	    		
	    		//Send embedded message to user
	    		objMsgCh.sendMessage(eb.build()).queue();
        	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "play")) {
        		/*Goal: Ask a trivia question*/
        		
        		//Get question...
        		JsonObject trivia = API.getTrivia().get("results").getAsJsonArray().get(0).getAsJsonObject();

        		
        		//Construct String
        		String question = StringEscapeUtils.unescapeHtml(trivia.get("question").getAsString());
        		ArrayList<String> choices = new ArrayList<String>();
        		choices.add(StringEscapeUtils.unescapeHtml(trivia.get("correct_answer").getAsString()));
        		int i = 0;
        		while(true) {
        			try {
        				choices.add(StringEscapeUtils.unescapeHtml(trivia.get("incorrect_answers").getAsJsonArray().get(i).getAsString()));
        			}catch(Exception e) {
        				break;
        			}	
        			i++;
        		}
        		Collections.shuffle(choices);
        		
        		String msg = "**Answer this trivia question:**"
        				+ "\n" + question + ""
        				+ "\n :one: " + choices.get(0)
        				+ "\n :two: " + choices.get(1)
        				+ "\n :three: " + choices.get(2)
        				+ "\n :four: " + choices.get(3);
        		
        		
        		
        		objMsgCh.sendMessage(msg).queue(message -> {
        			int answerIndex = 0;
            		for(String s : choices) {
            			if(s.equals(StringEscapeUtils.unescapeHtml(trivia.get("correct_answer").getAsString()))) {
            				answerIndex = choices.indexOf(s);
            			}
            		}
        			
        			message.addReaction("\u0031\u20E3").queue();
        			message.addReaction("\u0032\u20E3").queue();
        			message.addReaction("\u0033\u20E3").queue();
        			message.addReaction("\u0034\u20E3").queue();
        			
        			String commentId = "" + message.getIdLong();
            		TriviaQuestion tq = new TriviaQuestion(userId,objMsg.getId(), answerIndex + 1);
            		triviaRequests.put(commentId,tq);
            		
        		});
        		
        		
        	}
    		
			
		}else {
			if(objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "incubate")) {
	    		JsonObject value = new JsonObject();
	    		//Initialise values
	    		value.addProperty("Hunger", 0);
	    		value.addProperty("maxHunger", 5);
	    		
	    		value.addProperty("Happy", 0);
	    		
	    		value.addProperty("Discipline", 0);
	    		
	    		value.addProperty("Health", 50);
	    		value.addProperty("maxHealth", 50);
	    		
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
    				+ "\n" + Ref.prefix + "feed: Feeds your tamagotchi. Your pet should not eat too much!"
    				+ "\n" + Ref.prefix + "play: Play trivia with your pet. If you get the question right, your pet gains 1 Happy, and loses 1 Happy if you get it wrong."
    				+ ""
    				+ "```").queue();
    	}
    }
    
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
    	MessageReaction.ReactionEmote objEmote = event.getReactionEmote();
    	String messageId = event.getMessageId();
    	User objUser = event.getUser();
    	MessageChannel objMsgCh = event.getChannel();
    	if(triviaRequests.containsKey(messageId)) {
    		TriviaQuestion tq = triviaRequests.get(messageId);
    		if(objUser.getId().equals(tq.getUserId())) {
    			JsonObject pet = (JsonObject) tamagotchis.get(event.getUser().getId());
    			int attempt = -1;
        		try{
        			 attempt = Ref.emojis.get(objEmote.getName());
        			 if(attempt == tq.getAnswer()) {
             			pet.addProperty("Happy", pet.get("Happy").getAsInt() + 1);
             			if(pet.has("Name")) {
             				objMsgCh.sendMessage(objUser.getAsMention() + " You just played with " + pet.get("Name") + ". `Happy: " + pet.get("Happy") + "`").queue();
             			}else {
             				objMsgCh.sendMessage(objUser.getAsMention() + " You just played with your pet!"+ " `Happy: " + pet.get("Happy") + "`").queue();
             			}
             			save();
             		}else {
             			pet.addProperty("Happy", pet.get("Happy").getAsInt() - 1);
             			objMsgCh.sendMessage(objUser.getAsMention() + " Wrong! Now your pet is sad." + " `Happy: " + pet.get("Happy") + "`").queue();
             			save();
             		}
        			triviaRequests.remove(messageId);
        		}catch(Exception e) {
        			 
        		}
    		}
    		
    	}
		
    }
    
    public static void save() {
    	Util.exec("rm " + "../" + Ref.foldername + "/" + Ref.filename);
		File file = new File("../" + Ref.foldername + "/" + Ref.filename);
		try {
			PrintWriter out = new PrintWriter(file);
			out.println(tamagotchis.toString());
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error occured while writing to " + file.getPath() + ". File not found.");
		}
    }
}
