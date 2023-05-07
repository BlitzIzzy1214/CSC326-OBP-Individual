package edu.ncsu.csc.CoffeeMaker.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 * @author Lev Emerson ememerso
 * @author Raj Patel rapate23
 */
@Entity
public class Inventory extends DomainObject {

    /** id for inventory entry */
    @Id
    @GeneratedValue
    private Long             id;

    /** List of all ingredients. */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Ingredient> ingredients;

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
    }

    /**
     * Inventory constructor with ingredients list
     *
     * @param i
     *            list of ingredients
     */
    public Inventory ( final List<Ingredient> i ) {
        ingredients = i;
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Add the number of ingredient units in the inventory to the current amount
     * of ingredient units.
     *
     * @param ingredient
     *            amount of ingredient
     * @return checked amount of ingredient
     * @throws IllegalArgumentException
     *             if the parameter isn't a positive integer
     */
    public Integer checkIngredient ( final String ingredient ) throws IllegalArgumentException {
        Integer amt = 0;
        try {
            amt = Integer.parseInt( ingredient );
        }
        catch ( final NumberFormatException e ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }
        if ( amt < 0 ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }

        return amt;
    }

    /**
     * Returns the list of ingredients in the inventory
     *
     * @return ingredient list
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param r
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        for ( int j = 0; j < r.getIngredients().size(); j++ ) {
            final Integer i = findIngredientByName( r.getIngredients().get( j ).getName() );
            if ( i == null || ingredients.get( i ).getAmount() < r.getAmounts().get( j ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds ingredient with same name in inventory
     *
     * @param name
     *            name of ingredient to find
     * @return index of ingredient in list
     */
    public Integer findIngredientByName ( final String name ) {
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( ingredients.get( i ).getName().equals( name ) ) {
                return i;
            }
        }

        return null;
    }

    /**
     * Removes the ingredients used to make the specified recipe. Assumes that
     * the user has checked that there are enough ingredients to make
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        if ( enoughIngredients( r ) ) {
            for ( int j = 0; j < r.getIngredients().size(); j++ ) {
                final Integer i = findIngredientByName( r.getIngredients().get( j ).getName() );
                if ( i != null ) {
                    ingredients.get( j ).setAmount( ingredients.get( j ).getAmount() - r.getAmounts().get( j ) );
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Adds ingredient quantities to the inventory
     *
     * @param i
     *            list of ingredients
     */
    public void updateIngredients ( final List<Ingredient> i ) {
        for ( int j = 0; j < i.size(); j++ ) {
            if ( i.get( j ).getAmount() < 0 ) {
                throw new IllegalArgumentException( "Amount cannot be negative" );
            }
        }

        if ( ingredients == null || ingredients.size() == 0 ) {
            ingredients = i;
        }
        else {
            for ( int j = 0; j < i.size(); j++ ) {
                final Integer idx = findIngredientByName( i.get( j ).getName() );
                if ( idx != null ) {
                    ingredients.get( idx ).setAmount( i.get( j ).getAmount() + ingredients.get( idx ).getAmount() );
                }
                else {
                    ingredients.add( i.get( j ) );
                }
            }
        }
    }

    /**
     * Adds a new ingredients to the inventory
     *
     * @param name
     *            name of ingredient
     * @param quantity
     *            quantity of ingredient
     */
    public void addIngredient ( final String name, final int quantity ) {
        for ( int j = 0; j < ingredients.size(); j++ ) {
            if ( name.toLowerCase().trim().equals( ingredients.get( j ).getName().toLowerCase().trim() ) ) {
                throw new IllegalArgumentException( "Ingredient already exists" );
            }
        }

        if ( quantity < 0 ) {
            throw new IllegalArgumentException( "Amount cannot be negative" );
        }

        ingredients.add( new Ingredient( name, quantity ) );
    }

    /**
     * Adds a new ingredients to the inventory
     *
     * @param i
     *            ingredient to add
     */
    public void addIngredient ( final Ingredient i ) {
        for ( int j = 0; j < ingredients.size(); j++ ) {
            if ( i.getName().toLowerCase().trim().equals( ingredients.get( j ).getName().toLowerCase().trim() ) ) {
                throw new IllegalArgumentException( "Ingredient already exists" );
            }
        }

        ingredients.add( i );
    }

    /**
     * Sets the quantity of the given ingredient
     *
     * @param ingredient
     *            name of the ingredient whose quantity is required to be
     *            changed
     * @param quantity
     *            amount of final quantity
     */
    public void setIngredient ( final String ingredient, final int quantity ) {
        if ( quantity < 0 ) {
            throw new IllegalArgumentException( "Quantity cannot be less than 0" );
        }

        final int pos = findIngredientByName( ingredient );
        ingredients.get( pos ).setAmount( quantity );
    }

    /**
     * Returns a string describing the current contents of the inventory.
     *
     * @return String of inventory
     */
    @Override
    public String toString () {
        final StringBuffer buf = new StringBuffer();
        for ( int i = 0; i < ingredients.size(); i++ ) {
            buf.append( ingredients.get( i ).toString() );
            buf.append( "\n" );
        }
        return buf.toString();
    }

}
