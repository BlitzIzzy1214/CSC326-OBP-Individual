package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIIngredientTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IngredientService     service;

    private Ingredient            coffee;
    private Ingredient            cream;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();

    }

    @Test
    @Transactional
    public void ensureIngredient () throws Exception {
        service.deleteAll();

        cream = new Ingredient();
        cream.setName( "Cream" );
        cream.setAmount( 50 );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( cream ) ) ).andExpect( status().isOk() );

    }

    @Test
    @Transactional
    public void testIngredientAPI () throws Exception {

        service.deleteAll();

        final Ingredient peppermint = new Ingredient();
        peppermint.setName( "Peppermint" );
        peppermint.setAmount( 50 );

        // Check ingredient is added
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( peppermint ) ) );

        Assertions.assertEquals( 5, (int) service.count() );

        final Ingredient pumpkinSpice = new Ingredient();
        pumpkinSpice.setName( "Pumpkin Spice" );
        pumpkinSpice.setAmount( 50 );
        cream = new Ingredient();
        cream.setName( "Cream" );
        cream.setAmount( 50 );
        final Ingredient sweetener = new Ingredient();
        sweetener.setName( "Sweetener" );
        sweetener.setAmount( 50 );

        // Check multiple ingredients can be added
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pumpkinSpice ) ) );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( cream ) ) );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( sweetener ) ) );

        Assertions.assertEquals( 8, (int) service.count() );

    }

    @Test
    @Transactional
    public void testGetIngredientInvalid () throws Exception {

        service.deleteAll();

        coffee = new Ingredient();
        coffee.setName( "Coffee" );
        coffee.setAmount( 50 );

        // Ingredient has not yet been saved
        mvc.perform( get( String.format( "/api/v1/ingredients/%s", coffee.getName() ) ) )
                .andExpect( status().is4xxClientError() );

        // Invalid ingredient name
        mvc.perform( get( String.format( "/api/v1/ingredients/%s", "Invalid" ) ) )
                .andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 0, (int) service.count() );

    }

    @Test
    @Transactional
    public void testAddIngredientDuplicate () throws Exception {

        /*
         * Tests an ingredient with a duplicate name to make sure it's rejected
         */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Ingredients in the CoffeeMaker" );
        coffee = new Ingredient();
        coffee.setName( "Coffee" );
        coffee.setAmount( 50 );

        service.save( coffee );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( coffee ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one ingredient in the CoffeeMaker" );
    }

}
