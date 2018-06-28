package bot.fun.tamagotchi;

import java.util.HashMap;
import java.util.Map;

public class Ref {
	
	public final static String prefix = ".";
	public final static Long myId = 194857448673247235L;
	
	public final static String filename = "tamagotchis.json";
	public final static String foldername = "Tamagotchis";
	
	public static final Map<String, Integer> emojis;
    static
    {
    	emojis = new HashMap<String, Integer>();
    	emojis.put("1⃣", 1);
    	emojis.put("2⃣", 2);
    	emojis.put("3⃣", 3);
    	emojis.put("4⃣", 4);
    }
   
    public static final Map<String, String> foodEmojis;
    static
    {
    	foodEmojis = new HashMap<String, String>();
    	foodEmojis.put("🍞", "meal"); //Bread loaf
    	foodEmojis.put("🥖", "meal"); //Baguette
    	foodEmojis.put("🥙", "meal"); //Stuffed Flatbread
    	foodEmojis.put("🍅","snack"); //Tomato
    	foodEmojis.put("🍊","snack"); //Tangerine
    	foodEmojis.put("🍇","snack"); //Grapes
    	foodEmojis.put("🍈","snack"); //Melon
    	foodEmojis.put("🍉","snack"); //Watermelon
    	foodEmojis.put("🍌","snack"); //Banana
    	foodEmojis.put("🍍","snack"); //Pineapple
    	foodEmojis.put("🍎","snack"); //Red Apple
    	foodEmojis.put("🍏","snack"); //Green Apple
    	foodEmojis.put("🍐","snack"); //Pear
    	foodEmojis.put("🍑","snack"); //Peach
    	foodEmojis.put("🍒","snack"); //Cherry
    	foodEmojis.put("🍓","snack"); //Strawberry
    	foodEmojis.put("🥝","snack"); //Kiwi
    	foodEmojis.put("🥥","snack"); //Coconut
    	foodEmojis.put("🥑","snack"); //Avocado
    	foodEmojis.put("🍆","snack"); //Eggplant
    	foodEmojis.put("🥔","meal"); //Potato
    	foodEmojis.put("🥕","meal"); //Carrot
    	foodEmojis.put("🌽","snack"); //Corn
    	foodEmojis.put("🥒","meal"); //Cucumber
    	foodEmojis.put("🥦","snack"); //Broccoli
    	foodEmojis.put("🍄","snack"); //Mushroom
    	foodEmojis.put("🥜","snack"); //Peanuts
    	foodEmojis.put("🌰","snack"); //Chestnut
    	foodEmojis.put("🥐","meal"); //Croissant
    	foodEmojis.put("🥨","snack"); //Pretzel
    	foodEmojis.put("🥞","meal"); //Pancake
    	foodEmojis.put("🧀","meal"); //Cheese Wedge
    	foodEmojis.put("🍖","meal"); //Meat on Bone
    	foodEmojis.put("🍗","meal"); //Chicken Leg
    	foodEmojis.put("🥓","snack"); //Bacon
    	foodEmojis.put("🍔","meal"); //Hamburger
    	foodEmojis.put("🍟","snack"); //Fries
    	foodEmojis.put("🍕","meal"); //Pizza
    	foodEmojis.put("🌭","meal"); //Hotdog
    	foodEmojis.put("🌮","meal"); //Taco
    	foodEmojis.put("🌯","meal"); //Burrito
    	foodEmojis.put("🍲","meal"); //Stew
    	foodEmojis.put("🥗","snack"); //Salad
    	foodEmojis.put("🍿","snack"); //Popcorn
    	foodEmojis.put("🍱","meal"); //Bento box
    	foodEmojis.put("🍘","snack"); //Rice Cracker
    	foodEmojis.put("🍙","snack"); //Rice Ball
    	foodEmojis.put("🍚","meal"); //Cooked Rice
    	foodEmojis.put("🍛","meal"); //Curry Rice
    	foodEmojis.put("🍜","meal"); //Steaming Bowl
    	foodEmojis.put("🍝","meal"); //Spaghetti
    	foodEmojis.put("🍠","meal"); //Roasted Sweet Potato
    	foodEmojis.put("🍢","meal"); //Oden
    	foodEmojis.put("🍣","meal"); //Sushi
    	foodEmojis.put("🍤","snack"); //Fried Shrimp
    	foodEmojis.put("🍥","snack"); //Fish Cake With Swirl
    	foodEmojis.put("🍡","meal"); //Dango
    	foodEmojis.put("🍦","sweet"); //Soft Serve Ice Cream
    	foodEmojis.put("🍧","sweet"); //Shaved Ice
    	foodEmojis.put("🍨","sweet"); //Ice Cream
    	foodEmojis.put("🍩","sweet"); //Doughnut
    	foodEmojis.put("🍪","sweet"); //Cookie
    	foodEmojis.put("🎂","sweet"); //Birthday Cake
    	foodEmojis.put("🍰","sweet"); //Shortcake
    	foodEmojis.put("🍫","sweet"); //Chocolate
    	foodEmojis.put("🍬","sweet"); //Candy
    	foodEmojis.put("🍭","sweet"); //Lollipop
    	foodEmojis.put("🍮","sweet"); //Custard
    	foodEmojis.put("🍯","sweet"); //Honey
    	foodEmojis.put("🍼","meal"); //Baby Bottle
    	foodEmojis.put("🥛","snack"); //Milk
    	foodEmojis.put("☕","snack"); //Coffee
    	foodEmojis.put("🍵","snack"); //tea
    	foodEmojis.put("🍶","alcohol"); //sake
    	foodEmojis.put("🍾","alcohol"); //alcohol
    	foodEmojis.put("🍷","meal"); //Wine Glass
    	foodEmojis.put("🍸","meal"); //Cocktail Glass
    	foodEmojis.put("🍹","snack"); //Tropical Drink
    	foodEmojis.put("🍺","alcohol"); //Beer Mug
    	foodEmojis.put("🍻","alcohol"); //Clinking Beer Mugs
    	foodEmojis.put("🥂","alcohol"); //Clinking Glasses
    	foodEmojis.put("🥃","alcohol"); //Tumbler Glass
    	 
    }
}
