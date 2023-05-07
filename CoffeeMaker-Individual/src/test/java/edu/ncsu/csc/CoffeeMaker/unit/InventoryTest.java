package edu.ncsu.csc.CoffeeMaker.unit;

import java.util.ArrayList;
import java.util.List;

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

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class InventoryTest {

    @Autowired
    private InventoryService inventoryService;

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
        final Inventory ivt = inventoryService.getInventory();

        coffee = new Ingredient();
        coffee.setName( "Coffee" );
        coffee.setAmount( 500 );
        milk = new Ingredient();
        milk.setName( "Milk" );
        milk.setAmount( 500 );
        sugar = new Ingredient();
        sugar.setName( "Sugar" );
        sugar.setAmount( 500 );
        chocolate = new Ingredient();
        chocolate.setName( "Chocolate" );
        chocolate.setAmount( 500 );
        cream = new Ingredient();
        cream.setName( "Cream" );
        cream.setAmount( 500 );

        ingredients = new ArrayList<>();
        ingredients.add( coffee );
        ingredients.add( milk );
        ingredients.add( sugar );
        ingredients.add( chocolate );
        ingredients.add( cream );

        ivt.updateIngredients( ingredients );

        inventoryService.save( ivt );
    }

    @Test
    @Transactional
    public void testConsumeInventory () {
        final Inventory i = inventoryService.getInventory();

        // Create a new recipe and use ingredients from inventory to make it
        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        recipe.addIngredient( coffee, 10 );
        recipe.addIngredient( milk, 20 );
        recipe.addIngredient( sugar, 5 );
        recipe.addIngredient( chocolate, 1 );

        recipe.setPrice( 5 );

        i.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields are now properly updated
         */

        Assertions.assertEquals( 490, (int) i.getIngredients().get( 0 ).getAmount() );
        Assertions.assertEquals( 480, (int) i.getIngredients().get( 1 ).getAmount() );
        Assertions.assertEquals( 495, (int) i.getIngredients().get( 2 ).getAmount() );
        Assertions.assertEquals( 499, (int) i.getIngredients().get( 3 ).getAmount() );
    }

    @Test
    @Transactional
    public void testAddInventory1 () {
        Inventory ivt = inventoryService.getInventory();

        // Add ingredients to inventory
        coffee.setAmount( 5 );
        milk.setAmount( 3 );
        sugar.setAmount( 7 );
        chocolate.setAmount( 2 );
        cream.setAmount( 0 );
        ivt.updateIngredients( ingredients );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        ivt = inventoryService.getInventory();

        Assertions.assertEquals( 505, (int) ivt.getIngredients().get( 0 ).getAmount(),
                "Adding to the inventory should result in correctly-updated values for coffee" );
        Assertions.assertEquals( 503, (int) ivt.getIngredients().get( 1 ).getAmount(),
                "Adding to the inventory should result in correctly-updated values for milk" );
        Assertions.assertEquals( 507, (int) ivt.getIngredients().get( 2 ).getAmount(),
                "Adding to the inventory should result in correctly-updated values sugar" );
        Assertions.assertEquals( 502, (int) ivt.getIngredients().get( 3 ).getAmount(),
                "Adding to the inventory should result in correctly-updated values chocolate" );
        Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 4 ).getAmount(),
                "Adding to the inventory should result in correctly-updated values cream" );

    }

    @Test
    @Transactional
    public void testAddInventory2 () {
        final Inventory ivt = inventoryService.getInventory();

        // Cannot set invalid ingredient quantities
        try {
            coffee.setAmount( -5 );
            milk.setAmount( 3 );
            sugar.setAmount( 7 );
            chocolate.setAmount( 2 );
            cream.setAmount( 0 );
            ivt.updateIngredients( ingredients );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 0 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 1 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 2 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 3 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 4 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- cream" );
        }
    }

    @Test
    @Transactional
    public void testAddInventory3 () {
        final Inventory ivt = inventoryService.getInventory();

        // Cannot set invalid ingredient quantities
        try {
            coffee.setAmount( 5 );
            milk.setAmount( -3 );
            sugar.setAmount( 7 );
            chocolate.setAmount( 2 );
            cream.setAmount( 0 );
            ivt.updateIngredients( ingredients );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 0 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for milk should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 1 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for milk should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 2 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for milk should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 3 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for milk should result in no changes -- chocolate" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 4 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for milk should result in no changes -- cream" );
        }

    }

    @Test
    @Transactional
    public void testAddInventory4 () {
        final Inventory ivt = inventoryService.getInventory();

        // Cannot set invalid ingredient quantities
        try {
            coffee.setAmount( 5 );
            milk.setAmount( 3 );
            sugar.setAmount( -7 );
            chocolate.setAmount( 2 );
            cream.setAmount( 0 );
            ivt.updateIngredients( ingredients );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 0 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for sugar should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 1 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for sugar should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 2 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for sugar should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 3 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for sugar should result in no changes -- chocolate" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 4 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for sugar should result in no changes -- cream" );
        }

    }

    @Test
    @Transactional
    public void testAddInventory5 () {
        final Inventory ivt = inventoryService.getInventory();

        // Cannot set invalid ingredient quantities
        try {
            coffee.setAmount( 5 );
            milk.setAmount( 3 );
            sugar.setAmount( 7 );
            chocolate.setAmount( -2 );
            cream.setAmount( 0 );
            ivt.updateIngredients( ingredients );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 0 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 1 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 2 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 3 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- chocolate" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 4 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- cream" );
        }

    }

    @Test
    @Transactional
    public void testAddInventory6 () {
        final Inventory ivt = inventoryService.getInventory();

        // Create a new empty inventory
        final Inventory i = new Inventory();
        Assertions.assertNull( i.getIngredients() );

        try {
            // Update the new inventory with the old one
            i.updateIngredients( ivt.getIngredients() );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 0 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values for coffee" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 1 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values for milk" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 2 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values sugar" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 3 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values chocolate" );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.fail( "Uninitialized inventory list should be able to update" );
        }

    }

    @Test
    @Transactional
    public void testAddInventory7 () {
        final Inventory ivt = inventoryService.getInventory();

        final Ingredient peppermint = new Ingredient( "Peppermint", 500 );

        try {
            // Add new ingredient to inventory
            ivt.addIngredient( peppermint );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 0 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values for coffee" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 1 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values for milk" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 2 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values sugar" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 3 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values chocolate" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 4 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values cream" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 5 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values peppermint" );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.fail( "New ingredient should be added to end of list" );
        }

        try {
            ivt.addIngredient( peppermint );
            Assertions.fail( "Duplicate ingredients should not be allowed" );
        }
        catch ( final IllegalArgumentException iae ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddInventory8 () {
        final Inventory ivt = inventoryService.getInventory();

        try {
            // Add new ingredient to inventory
            ivt.addIngredient( "peppermint", 500 );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 0 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values for coffee" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 1 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values for milk" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 2 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values sugar" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 3 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values chocolate" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 4 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values cream" );
            Assertions.assertEquals( 500, (int) ivt.getIngredients().get( 5 ).getAmount(),
                    "Adding to the inventory should result in correctly-updated values peppermint" );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.fail( "New ingredient should be added to end of list" );
        }

        try {
            ivt.addIngredient( "peppermint", 500 );
            Assertions.fail( "Duplicate ingredients should not be allowed" );
        }
        catch ( final IllegalArgumentException iae ) {
            // expected
        }

        try {
            ivt.addIngredient( "ingredient", -10 );
            Assertions.fail( "Cannot set invalid ingredient quantities" );
        }
        catch ( final IllegalArgumentException iae ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testToString () {
        final Inventory i = inventoryService.getInventory();
        Assertions.assertEquals( "Coffee: 500\nMilk: 500\nSugar: 500\nChocolate: 500\nCream: 500\n", i.toString() );

        // Make a recipe and use ingredients
        final Recipe recipe = new Recipe();
        recipe.addIngredient( coffee, 5 );
        recipe.addIngredient( milk, 2 );
        recipe.addIngredient( sugar, 1 );
        recipe.addIngredient( chocolate, 3 );

        recipe.setPrice( 10 );

        i.useIngredients( recipe );

        Assertions.assertEquals( "Coffee: 495\nMilk: 498\nSugar: 499\nChocolate: 497\nCream: 500\n", i.toString() );

        // Add more ingredients
        coffee.setAmount( 10 );
        milk.setAmount( 6 );
        sugar.setAmount( 20 );
        chocolate.setAmount( 15 );
        cream.setAmount( 0 );
        i.updateIngredients( ingredients );

        Assertions.assertEquals( "Coffee: 505\nMilk: 504\nSugar: 519\nChocolate: 512\nCream: 500\n", i.toString() );
    }

    @Test
    @Transactional
    public void testEnoughIngredients () {
        final Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe();
        recipe.setName( "Coffee" );
        recipe.addIngredient( coffee, 501 );
        recipe.addIngredient( milk, 0 );
        recipe.addIngredient( sugar, 0 );
        recipe.addIngredient( chocolate, 0 );

        recipe.setPrice( 10 );

        // Attempt to make recipe with more coffee than is in stock
        Assertions.assertFalse( i.useIngredients( recipe ) );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 0 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 1 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 2 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 3 ).getAmount() );

        recipe.addIngredient( coffee, 0 );
        recipe.addIngredient( milk, 501 );
        recipe.addIngredient( sugar, 0 );
        recipe.addIngredient( chocolate, 0 );

        // Attempt to make recipe with more milk than is in stock
        Assertions.assertFalse( i.useIngredients( recipe ) );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 0 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 1 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 2 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 3 ).getAmount() );

        recipe.addIngredient( coffee, 0 );
        recipe.addIngredient( milk, 0 );
        recipe.addIngredient( sugar, 501 );
        recipe.addIngredient( chocolate, 0 );

        // Attempt to make recipe with more sugar than is in stock
        Assertions.assertFalse( i.useIngredients( recipe ) );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 0 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 1 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 2 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 3 ).getAmount() );

        recipe.addIngredient( coffee, 0 );
        recipe.addIngredient( milk, 0 );
        recipe.addIngredient( sugar, 0 );
        recipe.addIngredient( chocolate, 501 );

        // Attempt to make recipe with more chocolate than is in stock
        Assertions.assertFalse( i.useIngredients( recipe ) );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 0 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 1 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 2 ).getAmount() );
        Assertions.assertEquals( 500, (int) i.getIngredients().get( 3 ).getAmount() );
    }

    @Test
    @Transactional
    public void testCheckIngredients () {
        final Inventory i = inventoryService.getInventory();

        // Input cannot be letters or negative numbers
        Assertions.assertThrows( IllegalArgumentException.class, () -> i.checkIngredient( "five" ),
                "Units of ingredient must be a positive integer" );
        Assertions.assertThrows( IllegalArgumentException.class, () -> i.checkIngredient( "-1" ),
                "Units of ingredient must be a positive integer" );
        try {
            // Numbers 0 or more are valid
            Integer c = i.checkIngredient( "0" );
            Assertions.assertEquals( 0, (int) c );
            c = i.checkIngredient( "5" );
            Assertions.assertEquals( 5, (int) c );
        }
        catch ( final IllegalArgumentException e ) {
            Assertions.fail( "Input is valid, should not throw exception" );
        }

    }
}
