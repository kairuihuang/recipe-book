import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

// Code help from:
// https://stackoverflow.com/questions/18977144/how-to-parse-json-array-not-json-object-in-android

public class App {
  public static final String RECIPEBOOK = "src/recipebook.json";

  public static void save_recipe_to_file(Recipe r) {
    // create new recipe in json object format
    JSONObject jsonRecipe = new JSONObject();
    jsonRecipe.put("name", r.getName());
    jsonRecipe.put("description", r.getDescription());
    jsonRecipe.put("ingredients", r.getIngredients());
    jsonRecipe.put("instructions", r.getInstructions());

    JSONParser parser = new JSONParser();
    try {
      JSONArray jsonarray = (JSONArray) parser.parse(new FileReader(RECIPEBOOK));
      jsonarray.add(jsonRecipe); // add new recipe to list
      FileWriter f = new FileWriter(RECIPEBOOK, false);
      jsonarray.writeJSONString(jsonarray, f); // write to file
      f.close();
      System.out.println("Saved recipe!");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // prompt the user to enter information to create and save a new recipe
  public static void create_recipe(Scanner scanner) {
    // get name and description
    System.out.print("Name: ");
    String name = scanner.nextLine();
    System.out.print("Description: ");
    String description = scanner.nextLine();

    // get ingredients
    System.out.println("Enter the ingredients. Press enter after each ingredient. Enter `done` to finish.");
    ArrayList<String> ingredients = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String input = scanner.nextLine();
      if (input.equalsIgnoreCase("done")) {
        break;
      }
      ingredients.add(input);
    }

    // get instructions
    System.out.println("Enter the instructions. Press enter after each instruction. Enter `done` to finish.");
    ArrayList<String> instructions = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String input = scanner.nextLine();
      if (input.equalsIgnoreCase("done")) {
        break;
      }
      instructions.add(input);
    }

    Recipe r = new Recipe(name, description, ingredients, instructions);
    save_recipe_to_file(r);
  }

  // search function that allows user to search for a recipe by name
  public static void search_for_recipe(Scanner scanner){
    while(true){
      //Prompts the user to enter the name of the recipe. 
      //Case letters are ignored and substrings of recipe names are allowed to make this more user-friendly.
      System.out.println("To search for a specific recipe, please enter its name or enter 'back' to go back to the main menu: ");
      String name = scanner.nextLine();

      JSONParser parser = new JSONParser();
      JSONArray jsonarray;
      try {
        //Adds all the matches to an array
        ArrayList<JSONObject> recipe_array = new ArrayList<JSONObject>();
        jsonarray = (JSONArray) parser.parse(new FileReader(RECIPEBOOK));
        for (int i = 0; i < jsonarray.size(); i++) {
          JSONObject recipe = (JSONObject) jsonarray.get(i);
          if(name.equalsIgnoreCase("back")){
            return;
          }
          if (name.equalsIgnoreCase((String) recipe.get("name")) || ((String) recipe.get("name")).contains(name)){
            recipe_array.add(recipe);
          }
        }
        
        //Displays all the matches that were found and allows the user to choose the specific one they want to display.
        while(true){
          System.out.println("These are the matches that were found: ");

          for(int i = 0; i < recipe_array.size(); i++){
            System.out.printf("\t(%d) %s\n", i + 1, recipe_array.get(i).get("name"));
          }

          System.out.println("Please choose the number of the recipe you would like to display or type 'back' to return to the search menu: ");
          String input = scanner.nextLine();

          if(input.equalsIgnoreCase("back")){
            break;
          }
          else if (Integer.valueOf(input) > recipe_array.size()){
            System.out.println("Invalid input, please try again.");
            continue;
          }
          else{

            for(int i = 0; i < recipe_array.size(); i++){
              if(i + 1 == Integer.valueOf(input)){
                display_recipe(recipe_array.get(i), scanner);
                break;
              }
            }
          }
          break;
        }




      } catch (Exception e) {
        e.printStackTrace();
      }
    }


  }

  public static void step_through(JSONObject recipe, Scanner scanner) {
    List<String> instructions = (List<String>) recipe.get("instructions");
    String name = (String) recipe.get("name");

    System.out.println("Stepping through instructions for " + name + ", press enter to go to the next instruction.");
    System.out.println("Press enter to begin.");
    for (int i = 0; i < instructions.size(); i++) {
      scanner.nextLine();
      System.out.println("(" + (i + 1) + ") " + instructions.get(i));
    }
    System.out.println("Reached end of instructions! Press enter to return to the recipe.");
    scanner.nextLine();
  }

  // Display recipe information
  public static void display_recipe(JSONObject recipe, Scanner scanner) {
    while (true) {
      System.out.printf("You have chosen %s!\n", recipe.get("name"));
      JSONParser parser = new JSONParser();
      try {
        String name = (String) recipe.get("name");
        List<String> instructions = (List<String>) recipe.get("instructions");
        String description = (String) recipe.get("description");
        List<String> ingredients = (List<String>) recipe.get("ingredients");

        System.out.printf("\tName: %s\n", name);
        System.out.printf("\tDescription: %s\n", description);
        System.out.printf("\tIngredients: \n");
        for (int i = 0; i < ingredients.size(); i++) {
          System.out.printf("\t\t(%d) %s\n", i + 1, ingredients.get(i));
        }
        System.out.printf("\tInstructions: \n");
        for (int i = 0; i < instructions.size(); i++) {
          System.out.printf("\t\t(%d) %s\n", i + 1, instructions.get(i));
        }
        // process user input
        System.out.println("Enter (1) to go back, enter (2) to step through the instructions.");
        String input = scanner.nextLine();
        if (input.equals("1")) {
          return;
        } else if (input.equals("2")) {
          step_through(recipe, scanner);
        } else {
          System.out.println("Invalid input");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  // list all recipes and prompt user to select which one to view
  public static void list_all_recipes(Scanner scanner) {
    while (true) {
      System.out.println("Here are all the recipes we have stored!");
      System.out.println("If you would like to check a specific recipe, enter the corresponding number.");
      System.out.println("Enter `back` to go back to the main menu.");

      JSONParser parser = new JSONParser();
      JSONArray jsonarray;
      try {
        jsonarray = (JSONArray) parser.parse(new FileReader(RECIPEBOOK));
        for (int i = 0; i < jsonarray.size(); i++) {
          JSONObject recipe = (JSONObject) jsonarray.get(i);
          String name = (String) recipe.get("name");
          System.out.printf("\t(%d) %s\n", i + 1, name);
        }
        // process user input
        String input = scanner.nextLine();
        // if user wants to go back to main menu
        if (input.equalsIgnoreCase("back")) {
          return;
        }
        // display recipe based on number
        // TODO: handle non-integers and out of bounds integers
        display_recipe((JSONObject) jsonarray.get(Integer.parseInt(input) - 1), scanner);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  // display main menu prompt
  public static void main_menu() {
    System.out.println("--------");
    System.out.println("Welcome to Chefbook! \nWhat would you like to do?");
    System.out.println("\t(1) List all recipes in the book");
    System.out.println("\t(2) Create a recipe");
    System.out.println("\t(3) Search for a recipe");
    System.out.println("\t(press `x` to exit)");
  }

  public static void main(String[] args) throws Exception {
    /**
     * Main app / home page UI
     * Reads recipebook local file and displays choices to user
     */
    Scanner scanner = new Scanner(System.in);
    while (true) {
      main_menu();
      String input = scanner.nextLine();
      // exit condition
      if (input.equalsIgnoreCase("x")) {
        System.out.println("Good bye!");
        scanner.close();
        break;
      }
      switch (input) {
        case "1": {
          list_all_recipes(scanner);
          break;
        }
        case "2": {
          create_recipe(scanner);
          break;
        }
        case "3": {
          search_for_recipe(scanner);
          break;
        }
      }
    }
  }
}