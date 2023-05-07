package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc

public class APITest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private InventoryService      iService;

    private Ingredient            coffee;
    private Ingredient            milk;
    private Ingredient            sugar;
    private Ingredient            chocolate;

    private List<Ingredient>      ingredients;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

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
     * Tests the functions of the API
     */
    @Test
    @Transactional
    public void testAPI () throws Exception {
        String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        if ( !recipe.contains( "Mocha" ) ) {
            // Create a new Mocha recipe
            final Recipe r = new Recipe();
            r.setName( "Mocha" );
            r.setPrice( 5 );
            r.addIngredient( coffee, 3 );
            r.addIngredient( milk, 2 );
            r.addIngredient( sugar, 1 );
            r.addIngredient( chocolate, 1 );

            // Put Mocha in recipes
            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );
        }

        recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        // Check Mocha is in recipe list
        assertTrue( recipe.contains( "Mocha" ) );

        // Add ingredients to inventory
        coffee.setAmount( 50 );
        milk.setAmount( 50 );
        sugar.setAmount( 50 );
        chocolate.setAmount( 50 );

        final List<Ingredient> inventory = new ArrayList<Ingredient>();
        inventory.add( coffee );
        inventory.add( milk );
        inventory.add( sugar );
        inventory.add( chocolate );
        final Inventory i = new Inventory( inventory );
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );
        mvc.perform( get( "/api/v1/inventory" ) ).andExpect( status().isOk() );

        // Check that ingredients were saved
        final String ingredients = mvc.perform( get( "/api/v1/ingredients" ) ).andDo( print() )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        assertTrue( ingredients.contains( "Coffee" ) );
        assertTrue( ingredients.contains( "Milk" ) );
        assertTrue( ingredients.contains( "Sugar" ) );
        assertTrue( ingredients.contains( "Chocolate" ) );

        // Test making Mocha
        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", "Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 100 ) ) ).andExpect( status().isOk() ).andDo( print() );
    }
}
