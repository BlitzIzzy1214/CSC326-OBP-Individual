package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService         service;

    private Ingredient            coffee;
    private Ingredient            milk;
    private Ingredient            sugar;
    private Ingredient            chocolate;

    private List<Ingredient>      ingredients;

    @Autowired
    private InventoryService      ivtService;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

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

        i.updateIngredients( ingredients );

        ivtService.save( i );
    }

    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        service.deleteAll();

        final Recipe r = new Recipe();
        r.addIngredient( coffee, 3 );
        r.addIngredient( milk, 4 );
        r.addIngredient( sugar, 8 );
        r.addIngredient( chocolate, 5 );
        r.setName( "Mocha" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

    }

    @Test
    @Transactional
    public void testRecipeAPI () throws Exception {

        service.deleteAll();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        recipe.addIngredient( coffee, 1 );
        recipe.addIngredient( milk, 20 );
        recipe.addIngredient( sugar, 5 );
        recipe.addIngredient( chocolate, 10 );

        recipe.setPrice( 5 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    @Test
    @Transactional
    public void testAddRecipeDuplicate () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );

        service.save( r1 );

        final Recipe r2 = createRecipe( name, 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testAddRecipeMaximum () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        final Recipe r4 = createRecipe( "Hot Chocolate", 75, 0, 2, 1, 2 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isInsufficientStorage() );

        Assertions.assertEquals( 3, service.count(), "Creating a fourth recipe should not get saved" );
    }

    @Test
    @Transactional
    public void testEditRecipe () throws Exception {

        /* Tests to make sure the recipe is updated in the database */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r = createRecipe( "Mocha", 50, 3, 1, 1, 2 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) );

        Assertions.assertEquals( 1, service.count(),
                "Creating one recipe should result in one recipe in the database" );

        r.setPrice( 10 );

        mvc.perform( put( String.format( "/api/v1/recipes/%s", r.getName() ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe () throws Exception {
        service.deleteAll();
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Cannot delete from empty service
        mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Nothing" ) ) )
                .andExpect( status().is4xxClientError() );

        // Create maximum amount of recipes
        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isOk() );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r3 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        mvc.perform( get( String.format( "/api/v1/recipes/%s", "Invalid" ) ) ).andExpect( status().is4xxClientError() );
        mvc.perform( get( String.format( "/api/v1/recipes/%s", "Coffee" ) ) ).andExpect( status().isOk() );
        mvc.perform( get( String.format( "/api/v1/recipes/%s", "Mocha" ) ) ).andExpect( status().isOk() );
        mvc.perform( get( String.format( "/api/v1/recipes/%s", "Latte" ) ) ).andExpect( status().isOk() );

        // Delete invalid recipe
        mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Invalid" ) ) )
                .andExpect( status().is4xxClientError() );

        final String test = mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Invalid" ) ) )
                .andExpect( status().is4xxClientError() ).andReturn().getResponse().getContentAsString();

        Assertions.assertTrue( test.contains( "No recipe found for name Invalid" ) );

        // Delete valid recipes
        final String mochaDelete = mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Mocha" ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        Assertions.assertEquals( 2, service.count() );
        Assertions.assertTrue( mochaDelete.contains( "Mocha was deleted successfully" ) );

        // Should not be able to delete same recipe twice
        final String mochaTwiceDelete = mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Mocha" ) ) )
                .andExpect( status().is4xxClientError() ).andReturn().getResponse().getContentAsString();
        Assertions.assertEquals( 2, service.count() );
        Assertions.assertTrue( mochaTwiceDelete.contains( "No recipe found for name Mocha" ) );

        final String latteDelete = mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Latte" ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        Assertions.assertEquals( 1, service.count() );
        Assertions.assertTrue( latteDelete.contains( "Latte was deleted successfully" ) );

        final String coffeeDelete = mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Coffee" ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        Assertions.assertEquals( 0, service.count() );
        Assertions.assertTrue( coffeeDelete.contains( "Coffee was deleted successfully" ) );

        // Cannot delete from empty service
        final String wrongDelete = mvc.perform( delete( String.format( "/api/v1/recipes/%s", "Empty Delete" ) ) )
                .andExpect( status().is4xxClientError() ).andReturn().getResponse().getContentAsString();
        Assertions.assertTrue( wrongDelete.contains( "No recipe found for name Empty Delete" ) );
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
