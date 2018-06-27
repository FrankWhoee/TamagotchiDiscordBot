package bot.fun.tamagotchi;

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
    		isResponding = isResponding ? false : true;
    		String reply = isResponding ? "Tamagotchi enabled." : "Tamagotchi disabled.";
    		objMsgCh.sendMessage(reply).queue();
    	}
    	
    	if(!isResponding) {
    		return;
    	}

    	if(objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "help")) {
    		objMsgCh.sendMessage("```How to raise a tamagotchi:"
    				+ "\n" + Ref.prefix + "incubate: Start your very own tamagotchi! This registers you into the tamagotchi database and you can begin your tamagotchi."
    				+ ""
    				+ ""
    				+ ""
    				+ "```").queue();
    	}else if(objMsg.getContentRaw().equalsIgnoreCase(Ref.prefix + "incubate")) {
    		String userId = objUser.getId();
    		JsonObject value = new JsonObject();
    		value.addProperty("Hunger", "0");
    		value.addProperty("Happy", "0");
    		value.addProperty("Discipline", "0");
    		value.addProperty("Health", "50");
    		value.addProperty("Age", "0");
    		value.addProperty("Weight", "5");
    		tamagotchis.add(userId, value);
    		
    		EmbedBuilder eb = new EmbedBuilder();
    		eb.setTitle("Unknown Pet");
    		eb.setDescription(objUser.getAsMention() + "'s pet.");
    		eb.addField("Hunger",value.get("Hunger").getAsString(),false);
    		eb.addField("Happy",value.get("Happy").getAsString(),false);
    		eb.addField("Discipline",value.get("Discipline").getAsString(),false);
    		eb.addField("Health",value.get("Health").getAsString(),false);
    		eb.addField("Age",value.get("Age").getAsString(),false);
    		eb.addField("Weight",value.get("Weight").getAsString(),false);
    		eb.setImage(url)
    	}
    	
    	
    	
    	
    	
    }
}
