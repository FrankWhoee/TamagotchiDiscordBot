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
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


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
	static Long lastUpdate = System.currentTimeMillis();
	static Long now; 
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
        
        while(true) {
        	now = System.currentTimeMillis();
        	if(now - lastUpdate > Ref.updateInterval) {
        		for(String tamagotchi : tamagotchis.keySet()) {
        			if(tamagotchi.equals(Ref.specialId.toString())) {
        				continue;
        			}
        			
        			JsonObject t = tamagotchis.get(tamagotchi).getAsJsonObject();
        			
        			User objUser = jda.getUserById(tamagotchi);
        			
        			//Check if health = 0
        			if(t.get("Health").getAsInt() <= 0) {
        				objUser.openPrivateChannel().queue((channel) ->{
    			    		channel.sendMessage("Your pet has just died. To start a new pet, type .incubate").queue();
    			    		channel.sendMessage("Game over.").queue();
        					tamagotchis.remove(tamagotchi);
        					String rA9 = "";
        					for(int i = (int) (Math.random() * 100); i >= 0; i--) {
        						rA9 += Util.getrA9();
        					}
        					rA9 = Util.getrA9(rA9);
        					rA9 = "`" + rA9 + "`";
        					if(Math.random() * 100 < 0.001) {
        						channel.sendMessage(rA9).queue();
        					}
        					
        					channel.sendMessage("Tamagotchi deleted.").queue();
    	    			});
        			}
        			
        			TamagotchiController tc = new TamagotchiController(t);
        			if(t.get("Hunger").getAsInt() > 0) {
        				tc.change("Hunger", -1);
        				tc.change("Poops", 1);
        				tc.change("Weight", 0.2);
        			}else {
        				tc.change("Happy", -1);
        				tc.change("Health", -1);
        				tc.change("Weight", -0.1);
        			}
        			
        			if(t.get("Sleeping").getAsBoolean()) {
        				tc.changeHealth(1);
        			}
        			
        			if(t.get("Poops").getAsInt() > 5) {
        				tc.changeHealth(-1);
        			}
        			tc.change("Age", 0.08);
        		}
        		lastUpdate = now;
        		save();
        	}
        }
    }
    
    /*
	    		
	    		
	    		
	    		
	    		value.addProperty("Light", "on");
	    		value.addProperty("Sleeping", false);
	    		
	    		value.addProperty("dateIncubated", Ref.getTimeRaw().getTime());
	    		
	    		value.addProperty("ImageURL", "https://raw.githubusercontent.com/FrankWhoee/TamagotchiDiscordBot/master/Images/egg.jpeg");
     */
    
    
    
    
    @Override
    public void onMessageReceived(MessageReceivedEvent evt) {
    	//Objects
    	User objUser = evt.getAuthor();
    	MessageChannel objMsgCh= evt.getChannel();
    	Message objMsg = evt.getMessage();
    	Guild objGuild = evt.getGuild();
    	
    	
    	
    	if(objUser.getIdLong() == Ref.myId && objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "toggleResponse")) {
    		isResponding = isResponding ? false : true;
    		String reply = isResponding ? "Tamagotchi enabled." : "Tamagotchi disabled.";
    		objMsgCh.sendMessage(reply).queue();
    	}
    	
    	if(!isResponding) {
    		return;
    	}
    	
    	String userId = objUser.getId();
    	if(tamagotchis.has(userId)) {
    		JsonObject pet = (JsonObject) tamagotchis.get(userId);
    		TamagotchiController tc = new TamagotchiController(pet,objUser);
    		
    		if(objMsg.getContentRaw().startsWith(Ref.prefix + "feed")) {
        		/*Goal: Increment Hunger according to the food type*/
    			String input = objMsg.getContentRaw();
    			String food = input.substring(5).trim();
    			String type = Ref.foodEmojis.get(food);
    			if(food.equals("") || type == null){
    				objMsgCh.sendMessage("Your pet can't eat nothing! Put in a food emoji for your pet!").queue();
    				return;
    			}
    			
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
    				tc.change("Happy", 10);
    				tc.changeHealth(-10);
    			}else if(type.equals("medicine")) {
    				tc.change("Happy", -1);
    				tc.change("Health", 25);
    				objMsgCh.sendMessage("Your pet ate the medicine. Happiness will decrease. `Health: " + pet.get("Health")+ "`").queue();
    				return;
    			}else if(type.equals("poop")) {
    				tc.change("Happy", -1);
    				objMsgCh.sendMessage("Your pet threw the poop back at you. `Happy: " + pet.get("Happy")+ "`").queue();
    				return;
    			}else if(type.equals("knife")) {
    				tc.changeHealth(-10);
    				objMsgCh.sendMessage("You stabbed your pet. `Health: " + pet.get("Health") + "/" + pet.get("maxHealth") + "`").queue();
    				return;
    			}else if(type.equals("projectile")) {
    				tc.changeHealth(461312499787104256L);
    				objMsgCh.sendMessage("You shot your pet. `Health: " + pet.get("Health") + "/" + pet.get("maxHealth") + "`").queue(message -> {
    					try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					objMsgCh.sendMessage("Game over.").queue();
    					tamagotchis.remove(objUser.getId());
    					String rA9 = "";
    					for(int i = (int) (Math.random() * 100); i >= 0; i--) {
    						rA9 += Util.getrA9();
    					}
    					rA9 = Util.getrA9(rA9);
    					rA9 = "`" + rA9 + "`";
    					if(Math.random() * 100 < 0.001) {
    						objMsgCh.sendMessage(rA9).queue();
    					}
    					
    					objMsgCh.sendMessage("Tamagotchi deleted.").queue();
    					
    				});
    				return;
    			}else if(type.equals("bomb")) {
    				tc.changeHealth(461312499787104256L);
    				objMsgCh.sendMessage("You bombed your pet. `Health: " + pet.get("Health") + "/" + pet.get("maxHealth") + "`").queue(message -> {
    					try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					objMsgCh.sendMessage("Game over.").queue();
    					tamagotchis.remove(objUser.getId());
    					String rA9 = "";
    					for(int i = (int) (Math.random() * 100); i >= 0; i--) {
    						rA9 += Util.getrA9();
    					}
    					rA9 = Util.getrA9(rA9);
    					rA9 = "`" + rA9 + "`";
    					if(Math.random() * 100 < 0.001) {
    						objMsgCh.sendMessage(rA9).queue();
    					}
    					
    					objMsgCh.sendMessage("Tamagotchi deleted.").queue();
    					
    				});
    				return;
    			}else if(type.equals("special")) {
    				
    				objMsgCh.sendMessage(Util.getrA9()).queue();
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
    						+ "\n`Health: " + pet.get("Health") + "/" + pet.get("maxHealth") + "`").queue();
    				
    			}
    			
    			save();
        	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "medicine")) {
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
        	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "scold")) {
        		
        	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "lights")) {
        		tc.toggleLight();
        		objMsgCh.sendMessage("You turned the lights " + pet.get("Light").getAsString()).queue(message ->{
        			Long initTime = System.currentTimeMillis();
        			Long now = System.currentTimeMillis();
        			while(now - initTime < 60000 && pet.get("Light").getAsString().equals("off")) {
        				now = System.currentTimeMillis();
        			}
        			if(pet.get("Light").getAsString().equals("off")) {
        				
        				objMsgCh.sendMessage("Your pet is now sleeping.").queue();
        			}
        			
        			
        		});
        	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "nextRefresh")) {
        		objMsgCh.sendMessage("The next refresh will be in: " + (Ref.updateInterval - (now - lastUpdate))/1000 + " seconds").queue();
        	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "hatch")) {
        		/*Goal: Give pet a picture*/
        		//Check if user has already hatched pet
        		if(pet.get("ImageURL").getAsString().equals("https://raw.githubusercontent.com/FrankWhoee/TamagotchiDiscordBot/master/Images/egg.jpeg")) {
        			int picNumber = (int)(Math.random() * 12);
            		pet.addProperty("ImageURL", "https://raw.githubusercontent.com/FrankWhoee/TamagotchiDiscordBot/master/Images/tamagotchi"+picNumber+".jpg");
            		objMsgCh.sendMessage(Util.getStats(pet, objUser)).queue();
        			save();
        		}else {
        			objMsgCh.sendMessage(objUser.getAsMention() + " You can't hatch your pet twice!").queue();
        		}
        	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "clean")) {
        		/*Goal: Set Poops to zero*/
        		if(pet.get("Poops").getAsInt() > 0) {
        			pet.addProperty("Poops", 0);
        			objMsgCh.sendMessage(objUser.getAsMention() + " Poops cleaned. It suddenly smells a lot better. `Poops:" + pet.get("Poops").getAsInt() + "`").queue();
        		}
    			save();
        	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "name")) {
        		/*Goal: Set Name if pet qualifies*/
        		if(pet.get("Age").getAsInt() >= 2 && !pet.has("Name")) {
        			String input = objMsg.getContentRaw();
        			String name = input.substring(5).trim();
        			pet.addProperty("Name",name);
        			objMsgCh.sendMessage(objUser.getAsMention() + " You just named your pet!").queue();
        			save();
        		}else if(pet.get("Age").getAsInt() < 2) {
        			objMsgCh.sendMessage(objUser.getAsMention() + " Your pet isn't old enough to be named!").queue();
        		}else if(pet.has("Name")) {
        			objMsgCh.sendMessage(objUser.getAsMention() + " You've already named your pet!").queue();
        		}			
        	}else if(objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "stats")) {
        		/*Goal: Show pet's statistics to user*/    		
	    		//Send embedded message to user
	    		objMsgCh.sendMessage(Util.getStats(pet,objUser)).queue();
        	}else if(objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "s")) {
        		/*Goal: Show pet's statistics to user*/    		
	    		//Send embedded message to user
        		objMsgCh.sendMessage(Util.getStatsCond(pet,objUser)).queue();
        	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "play")) {
        		/*Goal: Ask a trivia question*/
        		
        		for(String msgId : triviaRequests.keySet()) {
        			if(triviaRequests.get(msgId).getUserId().equals(objUser.getId())) {
        				objMsgCh.sendMessage(objUser.getAsMention() + " You already have a question! Answer the previous question first.").queue();
        				return;
        			}
        		}
        		
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
        		
        		
        	}else if(objMsg.getContentRaw().startsWith(Ref.prefix + "incubate")) {
        		objMsgCh.sendMessage("You already have a pet!").queue();
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
	    		
	    		value.addProperty("Age", 0.0);
	    		value.addProperty("Weight", 5);
	    		value.addProperty("Poops", 0);
	    		value.addProperty("Light", "on");
	    		value.addProperty("Sleeping", false);
	    		
	    		value.addProperty("dateIncubated", Ref.getTimeRaw().getTime());
	    		
	    		value.addProperty("ImageURL", "https://raw.githubusercontent.com/FrankWhoee/TamagotchiDiscordBot/master/Images/egg.jpeg");
	    		
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
	    		objMsgCh.sendMessage(Util.getStats(value, objUser)).queue();
	    	}else if(objMsg.getContentRaw().startsWith(Ref.prefix)) {
	    		objMsgCh.sendMessage("To start your own tamagotchi, incubate an egg first by typing .incubate").queue();
	    	}
		}
    	
    	if(objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "help")) {
    		objMsgCh.sendMessage("```How to raise a tamagotchi:"
    				+ "\n" + Ref.prefix + "incubate: Start your very own tamagotchi! This registers you into the tamagotchi database and you can begin your tamagotchi."
    				+ "\n" + Ref.prefix + "stats: Get all the info on your pet."
    				+ "\n" + Ref.prefix + "s: Get all the info on your pet, without the picture."
    				+ "\n" + Ref.prefix + "feed :FOODEMOJI: : Feeds your tamagotchi the FOODEMOJI. Your pet should not eat too much!"
    				+ "\n" + Ref.prefix + "play: Play trivia with your pet. If you get the question right, your pet gains 1 Happy, and loses 1 Happy if you get it wrong."
    				+ "\n" + Ref.prefix + "medicine: Heal your pet to full health."
    				+ "\n" + Ref.prefix + "clean: Sweep away all the poop."
    				+ "\n" + Ref.prefix + "hatch: Hatch your egg."
    				+ "\n" + Ref.prefix + "name <NAME>: Name your pet. Pet has to be 2 days old to be named."
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
