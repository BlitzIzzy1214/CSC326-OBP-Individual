package edu.ncsu.csc.CoffeeMaker.unit;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class RecipeTest {

    @Autowired
    private RecipeService    service;

    @Autowired
    private InventoryService ivtService;

    private Ingredient       coffee;
    private Ingredient       milk;
    private Ingredient       sugar;
    private Ingredient       chocolate;
    private Ingredient       cream;

    private List<Ingredient> ingredients;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        service.deleteAll();
        ivtService.deleteAll();

        final Inventory i = ivtService.getInventory();

        ingredients = i.getIngredients();

        coffee = ingredients.get( 0 );
        coffee.setAmount( 500 );
        milk = ingredients.get( 1 );
        milk.setAmount( 500 );
        sugar = ingredients.get( 2 );
        sugar.setAmount( 500 );
        chocolate = ingredients.get( 3 );
        chocolate.setAmount( 500 );
        cream = new Ingredient();
        cream.setName( "Cream" );
        cream.setAmount( 500 );

        ingredients.add( cream );

        i.updateIngredients( ingredients );

        ivtService.save( i );
    }

    @Test
    @Transactional
    public void testAddRecipe () {

        // Create a couple recipes and save them to the service
        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );
        r1.addIngredient( coffee, 1 );
        r1.addIngredient( milk, 0 );
        r1.addIngredient( sugar, 0 );
        r1.addIngredient( chocolate, 0 );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( coffee, 1 );
        r2.addIngredient( milk, 1 );
        r2.addIngredient( sugar, 1 );
        r2.addIngredient( chocolate, 1 );
        service.save( r2 );

        final List<Recipe> recipes = service.findAll();
        Assertions.assertEquals( 2, recipes.size(),
                "Creating two recipes should result in two recipes in the database" );

        Assertions.assertEquals( r1, recipes.get( 0 ), "The retrieved recipe should match the created one" );
    }

    @Test
    @Transactional
    public void testNoRecipes () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Invalid recipe should not save to service
        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        r1.setPrice( -12 );
        r1.addIngredient( coffee, 1 );
        r1.addIngredient( milk, 0 );
        r1.addIngredient( sugar, 0 );
        r1.addIngredient( chocolate, 0 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( coffee, 1 );
        r2.addIngredient( milk, 1 );
        r2.addIngredient( sugar, 1 );
        r2.addIngredient( chocolate, 1 );

        final List<Recipe> recipes = List.of( r1, r2 );

        try {
            service.saveAll( recipes );
            Assertions.assertEquals( 0, service.count(),
                    "Trying to save a collection of elements where one is invalid should result in neither getting saved" );
        }
        catch ( final Exception e ) {
            Assertions.assertTrue( e instanceof ConstraintViolationException );
        }

    }

    @Test
    @Transactional
    public void testAddRecipe1 () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );

        // Save 1 recipe to service
        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( name ) );

    }

    /* Test2 is done via the API for different validation */

    @Test
    @Transactional
    public void testAddRecipe2 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";

        // Try to save recipe with invalid price
        try {
            final Recipe r1 = createRecipe( name, -50, 3, 1, 1, 0 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative price" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";

        // Try to save recipe with invalid coffee
        try {
            final Recipe r1 = createRecipe( name, 50, -3, 1, 1, 2 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of coffee" );
        }
        catch ( final ConstraintViolationException | IllegalArgumentException e ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";

        // Try to save recipe with invalid milk
        try {
            final Recipe r1 = createRecipe( name, 50, 3, -1, 1, 2 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of milk" );
        }
        catch ( final ConstraintViolationException | IllegalArgumentException e ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe5 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";

        // Try to save recipe with invalid sugar
        try {
            final Recipe r1 = createRecipe( name, 50, 3, 1, -1, 2 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of sugar" );
        }
        catch ( final ConstraintViolationException | IllegalArgumentException e ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe6 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";

        // Try to save recipe with invalid chocolate
        try {
            final Recipe r1 = createRecipe( name, 50, 3, 1, 1, -2 );
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of chocolate" );
        }
        catch ( final ConstraintViolationException | IllegalArgumentException e ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe7 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Create maximum amount of recipes and save to service
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Save a recipe and delete it
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );

        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );

        service.delete( r1 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe2 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Save maximum recipes, then delete all
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.deleteAll();

        Assertions.assertEquals( 0, service.count(), "`service.deleteAll()` should remove everything" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Save maximum recipes, then delete in random order
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.delete( r2 );

        Assertions.assertEquals( 2, service.count(), "`service.delete()` should only remove one recipe" );

        service.delete( r3 );

        Assertions.assertEquals( 1, service.count(), "`service.delete()` should only remove one recipe" );

        service.delete( r1 );

        Assertions.assertEquals( 0, service.count(), "`service.delete()` should remove the last recipe" );

        // Delete all and deleting nonexistent recipe shouldn't change anything
        service.deleteAll();

        Assertions.assertEquals( 0, service.count(), "There should be no Recipes in the CoffeeMaker" );

        service.delete( r2 );

        Assertions.assertEquals( 0, service.count(), "There should be no Recipes in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );
        final Recipe r4 = createRecipe( "other", 10, 3, 1, 2, 0 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        // Delete recipe not in service
        service.delete( r4 );

        Assertions.assertEquals( 3, service.count(), "Recipe 4 is not in the system and should not be deleted" );
    }

    @Test
    @Transactional
    public void testUpdateRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Create new recipe and save it
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );

        // Update recipe and save
        r1.setPrice( 70 );

        service.save( r1 );

        // Recipe found by service should match updated values
        final Recipe retrieved = service.findByName( "Coffee" );

        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        Assertions.assertEquals( 3, (int) retrieved.getAmounts().get( 0 ) );
        Assertions.assertEquals( 1, (int) retrieved.getAmounts().get( 1 ) );
        Assertions.assertEquals( 1, (int) retrieved.getAmounts().get( 2 ) );
        Assertions.assertEquals( 0, (int) retrieved.getAmounts().get( 3 ) );

        Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't duplicate it" );

    }

    @Test
    @Transactional
    public void testUpdateRecipe2 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Create new recipe and save it
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );

        // Check that retrieved recipe is same as created one
        final Recipe recipe = service.findByName( "Coffee" );
        Assertions.assertEquals( r1, recipe );

        // Update recipe with another recipe that has the same name
        final Recipe r2 = createRecipe( "Coffee", 20, 3, 2, 1, 0 );
        r1.updateRecipe( r2 );
        service.save( r1 );

        // Only one recipe should be in service, check values
        Assertions.assertEquals( 1, service.findAll().size() );

        Assertions.assertEquals( "Coffee", r1.getName() );
        Assertions.assertEquals( 20, r1.getPrice() );
        Assertions.assertEquals( 3, r1.getAmounts().get( 0 ) );
        Assertions.assertEquals( 2, r1.getAmounts().get( 1 ) );
        Assertions.assertEquals( 1, r1.getAmounts().get( 2 ) );
        Assertions.assertEquals( 0, r1.getAmounts().get( 3 ) );

    }

    @Test
    @Transactional
    public void testRemoveIngredient () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );

        // Cannot remove ingredient not in recipe
        Assertions.assertFalse( r1.removeIngredient( cream ) );

        // Remove sugar from recipe, check that it is no longer in the recipe
        Assertions.assertTrue( r1.removeIngredient( sugar ) );
        Assertions.assertEquals( coffee, r1.getIngredients().get( 0 ) );
        Assertions.assertEquals( milk, r1.getIngredients().get( 1 ) );
        Assertions.assertEquals( chocolate, r1.getIngredients().get( 2 ) );
        Assertions.assertEquals( 3, r1.getAmounts().get( 0 ) );
        Assertions.assertEquals( 1, r1.getAmounts().get( 1 ) );
        Assertions.assertEquals( 0, r1.getAmounts().get( 2 ) );

    }

    @Test
    @Transactional
    public void testEquals () {
        // Test equals method compares fields accurately
        final Recipe r1 = null;
        final Recipe r2 = createRecipe( "Coffee", 20, 3, 2, 1, 0 );
        Assertions.assertFalse( r2.equals( r1 ) );
        final Recipe r3 = createRecipe( "Coffee", 20, 3, 2, 1, 0 );
        Assertions.assertTrue( r2.equals( r3 ) );
        Assertions.assertTrue( r3.equals( r2 ) );
    }

    @Test
    @Transactional
    public void testEquals2 () {
        // Test equals method compares fields accurately
        final Recipe r1 = createRecipe( "Coffee", 20, 3, 2, 1, 0 );
        final Recipe r2 = createRecipe( "Coffee", 20, 3, 2, 1, 0 );
        Assertions.assertEquals( r1.toString(), r2.toString() );
        Assertions.assertEquals( r2.toString(), r1.toString() );
        Assertions.assertTrue( r1.equals( r2 ) );
        Assertions.assertTrue( r2.equals( r1 ) );
        Assertions.assertEquals( r1.hashCode(), r2.hashCode() );
        final Recipe r3 = createRecipe( null, 20, 3, 2, 1, 0 );
        Assertions.assertFalse( r2.equals( r3 ) );
        Assertions.assertFalse( r3.equals( r2 ) );
    }

    @Test
    @Transactional
    public void testCheckRecipe () {
        // Check recipe should return false on recipes with ingredients
        final Recipe r1 = createRecipe( "Coffee", 20, 3, 2, 1, 0 );
        service.save( r1 );
        Assertions.assertEquals( 1, service.findAll().size() );
        final Recipe coffee = service.findByName( "coffee" );
        Assertions.assertNotNull( coffee );
        Assertions.assertFalse( coffee.checkRecipe() );

        // Check recipe should return true on recipes with no ingredients
        final Recipe r2 = createRecipe( "Nothing", 10, 0, 0, 0, 0 );
        service.save( r2 );
        Assertions.assertEquals( 2, service.findAll().size() );
        final Recipe nothing = service.findByName( "nothing" );
        Assertions.assertTrue( nothing.checkRecipe() );
    }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( this.coffee, coffee );
        recipe.addIngredient( this.milk, milk );
        recipe.addIngredient( this.sugar, sugar );
        recipe.addIngredient( this.chocolate, chocolate );

        return recipe;
    }

}
