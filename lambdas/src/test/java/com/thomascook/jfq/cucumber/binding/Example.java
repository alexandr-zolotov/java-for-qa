package com.thomascook.jfq.cucumber.binding;

import com.thomascook.jfq.cucumber.domains.cooking.Amount;
import com.thomascook.jfq.cucumber.domains.cooking.Ingredient;
import com.thomascook.jfq.cucumber.domains.cooking.Recipe;
import com.thomascook.jfq.cucumber.domains.cooking.Utensil;
import cucumber.api.DataTable;
import cucumber.api.Transpose;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.Assert.*;

public class Example {

    private static final Logger LOG = LoggerFactory.getLogger("Kitchen");

    private Map<String, Ingredient> ingredients = new HashMap<>();
    private Set<Utensil> inventory = new HashSet<>();
    private Recipe recipe;
    private List<String> cookedDishes = new LinkedList<>();

    @Before
    public void setUp() {
        /*
          Methods annotated @Before or @After are also called 'hooks'. Here goes the logic executed before EACH
          scenario. We can perform some data or environment preparation/clean up here (run mock server for example).
          IMPORTANT: This logic is the very first that is being executed. Steps from scenario 'Background' section
          will be executed AFTER ALL @Before hooks for that scenario.
          IMPORTANT: Cucumber looks for both step definitions and @Before/@After hooks in all classes found in location
          defined by 'glue' parameter of @CucumberOptions annotation. This means for every scenario all hooks found
          in all classes will be executed. See next method to find out how to manage it.
         */
        // for example we can clean up 'ingredients' in case something left there from previous scenario execution
        ingredients.clear();
        inventory.clear();
    }

    @Before(order = 100500, value = {"@WithSpecialSetUp, @SmokeTest", "@HooksExamples"})
    public void specialSetUp() {
        /*
          This is another @Before. Class with steps can have many of them. They are executed sequentially but Cucumber
          runner doesn't give explicit guarantees about execution order by default.
          To force @Before hooks execution order you should provide a value for 'order'
          parameter of the annotation. Default value is 10000 (applied to setUp() method above for example).
          @Before hooks with lesser value (e.g 1) will be executed first. This method annotation has order value that
          is higher than default. This means specialSetUp() will be executed after setUp().

          There also can be the case when you don't want a hook to be executed for every feature/scenario. 'value'
          annotation parameter to an aid. There you can defile a set of tags (annotations in *.feature files) where
          this hook is applicable. Tags written in a single comma-separated string are treated in OR manner (hook will
          be executed if scenario/feature has at least one of such tags). Tags written in separate strings are treated
          in AND manner (hook will be executed if scenario has AT LEAST ONE TAG FROM EACH STRING). For this hook will be
          executed if scenario/feature is annotated with ((@WithSpecialSetUp OR @SmokeTest) AND @HooksExamples)
         */
        cookedDishes.clear();
    }

    @After
    public void tearDown(){
        /*
         @After hooks 'mirror' @Before hooks. They are used to clean up stuff after scenario execution.
         */
    }

    @After(order = 1, value = "@SmokeTest")
    public void afterAll(){
        /*
         IMPORTANT: 'order' annotation parameter serves same purpose as in @Before hooks but acts differently - in
         reverse order. So, @After hooks with the low 'order' value will be executed after those with the higher values.
         'value' parameter acts exactly the same way as for @Before hooks.
         */
    }

    //I have unlimited amount of water
    @Given("^I have (\\d+) units of (.*)$")
    public void addIngredient(int unitsCount, String ingredientName) {
        Amount amount = new Amount(unitsCount);
        Ingredient ingredient = new Ingredient(ingredientName, amount);
        addToInventory(ingredient);
    }

    //todo use binding with one-arg constructor
    @And("^a (.*) in inventory$")
    public void addToInventory(Utensil utensil){
        String name = utensil.getName();
        if(inventory.add(utensil)){
            LOG.info("Just added " + name + " to the inventory\n");
        } else {
            LOG.info("Already have the " + name);
        }
    }

    //todo write comment about a bug
    @And("mom gave me a book with recipe")
    public void useRecipe(@Transpose List<Recipe> recipes){
        assertNull("Can't work on two recipes simultaneously", this.recipe);
        this.recipe = recipes.get(0);
    }

    @And("^the recipe is")
    public void collectRecipeDetails(DataTable instructions) {
        instructions.asList(Recipe.Instruction.class).stream().forEachOrdered(this.recipe::withInstruction);

    }

    @When("^I cook \'(.*)\'$")
    public void cook(String recipeName){
        if(!this.recipe.getName().equals(recipeName)) {
            throw new IllegalArgumentException("QA ninja is working on the " + this.recipe.getName() + " now");
        }

        this.recipe.getInstructions().stream().forEachOrdered((it -> {
            String ingredientName = it.getIngredient();
            assertTrue("QA ninja doesn't have required ingredient " + ingredientName, ingredients.containsKey(ingredientName));
            assertTrue("Not enough "+ingredientName, ingredients.get(ingredientName).getAmount().isAtLeast(it.getAmount()));

            Amount amountToUse = new Amount(it.getAmount());
            //todo handle 0
            ingredients.replace(ingredientName, new Ingredient(ingredientName,
                            ingredients.get(ingredientName).getAmount().subtract(amountToUse)));
            //todo
            LOG.info("{}ing {} {}(s)", it.getAction(), it.getAmount(), it.getIngredient());
        }));
        LOG.info("After {} minutes {} is ready", recipe.getTime(), recipeName);
        cookedDishes.add(recipeName);
    }

    @Then("^I still have at least (\\d+) units of (.+) left$")
    public void checkRemaining(int expectedAmount, String name) {
        assertTrue("No "+name+" at all", ingredients.containsKey(name));
        assertTrue("Remaining amount of "+name+" is less than expected", ingredients.get(name).getAmount().isAtLeast(expectedAmount));
    }

    //todo point that key work doesn't match
    @Then("^I have something to eat$")
    public void checkQANinjaHasFood(){
        assertFalse("Nothing to eat", cookedDishes.isEmpty());
    }


    private Ingredient addToInventory(Ingredient ingredientToAdd){
        return ingredients.merge(ingredientToAdd.getName(), ingredientToAdd, (present, more) ->
                new Ingredient(present.getName(), present.getAmount().add(ingredientToAdd.getAmount())));
    }
}
