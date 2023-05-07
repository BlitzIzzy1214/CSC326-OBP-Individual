package edu.ncsu.csc.CoffeeMaker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class TestDatabaseInteraction {
    @Autowired
    private RecipeService    recipeService;

    @Autowired
    private InventoryService iService;

    private Ingredient       coffee;
    private Ingredient       milk;
    private Ingredient       sugar;
    private Ingredient       chocolate;

    private List<Ingredient> ingredients;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        recipeService.deleteAll();
        iService.deleteAll();

        final Inventory ivt = iService.getInventory();

        ingredients = ivt.getIngredients();

        coffee = ingredients.get( 0 );
        coffee.setAmount( 15 );
        milk = ingredients.get( 1 );
        milk.setAmount( 15 );
        sugar = ingredients.get( 2 );
        sugar.setAmount( 15 );
        chocolate = ingredients.get( 3 );
        chocolate.setAmount( 15 );

        ivt.updateIngredients( ingredients );

        iService.save( ivt );
    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testRecipes () {
        final Recipe r = new Recipe();
        r.setName( "CoffeeRecipe" );
        r.setPrice( 10 );

        r.addIngredient( coffee, 3 );
        r.addIngredient( milk, 2 );
        r.addIngredient( sugar, 2 );
        r.addIngredient( chocolate, 2 );

        recipeService.save( r );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = dbRecipes.get( 0 );

        // Check that database contains correct recipe
        assertEquals( r.getName(), dbRecipe.getName() );
        assertEquals( r.getPrice(), dbRecipe.getPrice() );
        assertEquals( r.getAmounts().get( 0 ), dbRecipe.getAmounts().get( 0 ) );
        assertEquals( r.getAmounts().get( 1 ), dbRecipe.getAmounts().get( 1 ) );
        assertEquals( r.getAmounts().get( 2 ), dbRecipe.getAmounts().get( 2 ) );
        assertEquals( r.getAmounts().get( 3 ), dbRecipe.getAmounts().get( 3 ) );

        // Check that service contains correct recipe
        final Recipe nameRecipe = recipeService.findByName( "CoffeeRecipe" );
        assertEquals( r.getName(), nameRecipe.getName() );
        assertEquals( r.getPrice(), nameRecipe.getPrice() );
        assertEquals( r.getAmounts().get( 0 ), nameRecipe.getAmounts().get( 0 ) );
        assertEquals( r.getAmounts().get( 1 ), nameRecipe.getAmounts().get( 1 ) );
        assertEquals( r.getAmounts().get( 2 ), nameRecipe.getAmounts().get( 2 ) );
        assertEquals( r.getAmounts().get( 3 ), nameRecipe.getAmounts().get( 3 ) );

        // Check that updating database recipe updates created recipe
        dbRecipe.setPrice( 15 );
        recipeService.save( dbRecipe );
        assertEquals( 1, recipeService.findAll().size() );
        assertEquals( r.getName(), recipeService.findAll().get( 0 ).getName() );
        assertEquals( r.getAmounts().get( 0 ), recipeService.findAll().get( 0 ).getAmounts().get( 0 ) );
        assertEquals( r.getAmounts().get( 1 ), recipeService.findAll().get( 0 ).getAmounts().get( 1 ) );
        assertEquals( r.getAmounts().get( 2 ), recipeService.findAll().get( 0 ).getAmounts().get( 2 ) );
        assertEquals( r.getAmounts().get( 3 ), recipeService.findAll().get( 0 ).getAmounts().get( 3 ) );
        assertEquals( 15, recipeService.findAll().get( 0 ).getPrice() );
    }

}
