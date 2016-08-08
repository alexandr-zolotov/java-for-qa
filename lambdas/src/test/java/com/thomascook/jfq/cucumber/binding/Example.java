package com.thomascook.jfq.cucumber.binding;

import com.thomascook.jfq.cucumber.domains.cooking.*;
import cucumber.api.DataTable;
import cucumber.api.Transform;
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

    private static final Logger LOG = LoggerFactory.getLogger(Example.class);

    private Map<String, Ingredient> ingredients = new HashMap<>();
    private Set<Utensil> inventory = new HashSet<>();
    private Recipe recipe;
    private List<String> cookedDishes = new LinkedList<>();


    /**
     * Methods annotated @Before or @After are also called 'hooks'. Here goes the logic executed before EACH
     * scenario. We can perform some data or environment preparation/clean up here (run mock server for example).
     * IMPORTANT: This logic is the very first that is being executed. Steps from scenario 'Background' section
     * will be executed AFTER ALL @Before hooks for that scenario.
     * IMPORTANT: Cucumber looks for both step definitions and @Before/@After hooks in all classes found in location
     * defined by 'glue' parameter of @CucumberOptions annotation. This means for every scenario all hooks found
     * in all classes will be executed. See next method to find out how to manage it.
     */
    @Before
    public void setUp() {
        // for example we can clean up 'ingredients' in case something left there from previous scenario execution
        ingredients.clear();
        inventory.clear();
    }

    /**
     * This is another @Before. Class with steps can have many of them. They are executed sequentially but Cucumber
     * runner doesn't give explicit guarantees about execution order by default.
     * To force @Before hooks execution order you should provide a value for 'order'
     * parameter of the annotation. Default value is 10000 (applied to setUp() method above for example). @Before hooks
     * with lesser value (e.g 1) will be executed first. This method annotation has order value that
     * is higher than default. This means specialSetUp() will be executed after setUp().
     * <p>
     * There also can be the case when you don't want a hook to be executed for every feature/scenario. 'value'
     * annotation parameter to an aid. There you can defile a set of tags (annotations in *.feature files) where
     * this hook is applicable. Tags written in a single comma-separated string are treated in OR manner (hook will
     * be executed if scenario/feature has at least one of such tags). Tags written in separate strings are treated
     * in AND manner (hook will be executed if scenario has AT LEAST ONE TAG FROM EACH STRING). For this hook will be
     * executed if scenario/feature is annotated with ((@WithSpecialSetUp OR @SmokeTest) AND @HooksExamples)
     */
    @Before(order = 100500, value = {"@WithSpecialSetUp, @SmokeTest", "@HooksExamples"})
    public void specialSetUp() {
        LOG.info("Throwing stale stuff from the fridge");
        cookedDishes.clear();
    }

    /**
     * Here is an example of an @After hook. This hooks 'mirror' @Before hooks. They are used to clean up stuff after
     * scenario execution.
     */
    @After
    public void tearDown() {
        LOG.info("===== END OF STORY =====");
    }

    /**
     * IMPORTANT: 'order' annotation parameter serves same purpose as in @Before hooks but acts differently - in
     * reverse order. So, @After hooks with the low 'order' value will be executed after those with the higher values.
     * 'value' parameter acts exactly the same way as for @Before hooks.
     */
    @After(order = 1, value = "@SmokeTest")
    public void afterAll() {
        //There is no scenario annotated @SmokeTest in a *.feature file so this hook will not be executed at all
        LOG.info("Cleanup after @SmokeTest scenarios");
    }

    @Given("^I have (\\d+) units of (.*)$")
    public void addIngredient(int unitsCount, String ingredientName) {
        Amount amount = new Amount(unitsCount);
        Ingredient ingredient = new Ingredient(ingredientName, amount);
        addToInventory(ingredient);
    }

    @And("^a (.*) in inventory$")
    public void addToInventory(Utensil utensil) {
        String name = utensil.getName();
        if (inventory.add(utensil)) {
            LOG.info("Just added " + name + " to the inventory");
        } else {
            LOG.info("Already have the " + name);
        }
    }

    /**
     * Here is an example of @Transpose annotation usage. Unfortunately there is a known bug in Cucumber that was even
     * been fixed but not merged. The bug is that @Transpose annotation can work only with Lists and Maps when it should
     * be able to work with a single object of arbitrary type also. That's why if you want to use @Transpose for single
     * object binding you should use a List and then take its first element.
     * <p>
     * In essence @Transpose works exactly the same as if you where using DataTable (see collectRecipeDetails(...)
     * method below) and then call .transpose().asList(...) or .transpose().asMap(...) on it.
     */
    @And("mom gave me a book with recipe")
    public void useRecipe(@Transpose List<Recipe> recipes) {
        assertNull("Can't work on two recipes simultaneously", this.recipe);
        this.recipe = recipes.get(0);
    }

    /**
     * DataTable is a good choice when you need to pass a lot of structurally similar data from the scenario.
     * It has a rich set of useful methods that make it possible to create List or Map of arbitrary objects.
     * In case of List values from the first DataTable row are considered object properties names (see example above).
     * In case of a Map it is mandatory to have only two columns in the DataTable. First (left) column is used for
     * map keys creation, second (right) column is used for values. Note: both key and value classes need one-arg
     * constructor.
     */
    @And("^the recipe is")
    public void collectRecipeDetails(DataTable instructions) {
        instructions.asList(Recipe.Instruction.class).stream().forEachOrdered(this.recipe::withInstruction);

    }

    @When("^I cook \'(.*)\'$")
    public void cook(String recipeName) {
        if (!this.recipe.getName().equals(recipeName)) {
            throw new IllegalArgumentException("QA ninja is working on the " + this.recipe.getName() + " now");
        }

        this.recipe.getInstructions().stream().forEachOrdered((it -> {
            String ingredientName = it.getIngredient();
            assertTrue("QA ninja doesn't have required ingredient " + ingredientName, ingredients.containsKey(ingredientName));
            assertTrue("Not enough " + ingredientName, ingredients.get(ingredientName).getAmount().isAtLeast(it.getAmount()));

            Amount amountToUse = new Amount(it.getAmount());
            ingredients.replace(ingredientName, new Ingredient(ingredientName,
                    ingredients.get(ingredientName).getAmount().subtract(amountToUse)));

            LOG.info("{}ing {} {}(s)", it.getAction(), it.getAmount(), it.getIngredient());
        }));
        LOG.info("After {} minutes {} is ready", recipe.getTime(), recipeName);
        cookedDishes.add(recipeName);
    }

    /**
     * Here is an example of @Transform annotation usage. To explain Cucumber how a string value from scenario has to
     * be transformed into an object (or may be a collection if you need) you need to implement a custom transformer.
     * Custom transformer has to extend cucumber.api.Transformer abstract class with correct generic. For example
     * here we need string value to be transformed to an object of class Amount. To make the magic work put the class
     * of you transformer as a single @Transform annotation parameter
     */
    @Then("^I still have (\\d+) units of (.+) left$")
    public void checkRemaining(@Transform(AmountTransformer.class) Amount expectedAmount, String name) {
        assertTrue("No " + name + " at all", ingredients.containsKey(name));
        assertTrue("Remaining amount of " + name + " is less than expected", ingredients.get(name).getAmount().equals(expectedAmount));
    }

    /**
     * Here is the illustration that Given/When/And/Then words in scenario are completely interchangeable. This step
     * has And keyword in the scenario but it successfully binds to @Then
     */
    @Then("^I have something to eat$")
    public void checkQANinjaHasFood() {
        assertFalse("Nothing to eat", cookedDishes.isEmpty());
    }


    // Scenario Outline stuff
    @Given("there are (\\d+) (.+)\\(s\\) in the (.+)")
    public void setUpBiome(int numberBefore, String prey, String biome) {
        LOG.info("There where {} happy {}(s) living in the {}", numberBefore, prey, biome);
    }

    @When("the (.+) caught (\\d+) (.+)\\(s\\)")
    public void runSimulation(String predator, int numberCaught, String prey) {
        LOG.info("When hungry {} ate {} {}(s)", predator, numberCaught, prey);
    }

    @Then("only (\\d+) (.+)\\(s\\) left in (.+)")
    public void checkResults(int expectedRemainder, String prey, String biome){
        LOG.info("Only {} happy {}(s) left in {}", expectedRemainder, prey, biome);
    }

    private Ingredient addToInventory(Ingredient ingredientToAdd) {
        return ingredients.merge(ingredientToAdd.getName(), ingredientToAdd, (present, more) ->
                new Ingredient(present.getName(), present.getAmount().add(ingredientToAdd.getAmount())));
    }
}
