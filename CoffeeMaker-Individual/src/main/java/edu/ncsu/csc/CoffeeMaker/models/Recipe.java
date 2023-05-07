package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 * @author Lev Emerson ememerso
 */
@Entity
public class Recipe extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long             id;

    /** Recipe name */
    private String           name;

    /** Recipe price */
    @Min ( 0 )
    private Integer          price;

    /** List of all ingredients. */
    @ManyToMany ( fetch = FetchType.EAGER )
    @JoinTable ( joinColumns = @JoinColumn ( name = "recipe_id" ),
            inverseJoinColumns = @JoinColumn ( name = "ingredient_id" ) )
    @JsonFormat ( with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY )
    private List<Ingredient> ingredients;

    /** List of all ingredient amounts. */
    @ElementCollection
    @JsonFormat ( with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY )
    private List<Integer>    amounts;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
        this.price = 0;
        this.ingredients = new ArrayList<Ingredient>();
        this.amounts = new ArrayList<Integer>();
    }

    /**
     * Creates a Recipe for CoffeeMaker
     *
     * @param name
     *            name of recipe
     * @param price
     *            price of recipe
     * @param ingredients
     *            ingredients in recipe
     * @param amounts
     *            amounts of ingredients in recipe
     */
    public Recipe ( final String name, final int price, final List<Ingredient> ingredients,
            final List<Integer> amounts ) {
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
        this.amounts = amounts;
    }

    /**
     * Method for adding an ingredient to the list.
     *
     * @param i
     *            the ingredient to add
     * @param quantity
     *            the amount of the ingredient to add
     */
    public void addIngredient ( final Ingredient i, final int quantity ) {
        if ( quantity < 0 ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer" );
        }
        this.ingredients.add( i );
        this.amounts.add( quantity );
    }

    /**
     * Method for removing an ingredient from the list.
     *
     * @param i
     *            the ingredient to remove
     * @return true if ingredient was removed, false if not
     */
    public boolean removeIngredient ( final Ingredient i ) {
        for ( int j = 0; j < ingredients.size(); j++ ) {
            if ( ingredients.get( j ).equals( i ) ) {
                ingredients.remove( j );
                amounts.remove( j );
                return true;
            }
        }
        return false;
    }

    /**
     * Setter for the list of ingredients
     *
     * @param i
     *            list of ingredients
     */
    public void setIngredients ( final List<Ingredient> i ) {
        this.ingredients = i;
    }

    /**
     * Getter for the list of ingredients
     *
     * @return the list of ingredients
     */
    public List<Ingredient> getIngredients () {
        return this.ingredients;
    }

    /**
     * Setter for the list of ingredient amounts
     *
     * @param a
     *            list of ingredient amounts
     */
    public void setAmounts ( final List<Integer> a ) {
        this.amounts = a;
    }

    /**
     * Getter for the list of ingredient amounts
     *
     * @return the list of ingredient amounts
     */
    public List<Integer> getAmounts () {
        return this.amounts;
    }

    /**
     * Check if all ingredient fields in the recipe are 0
     *
     * @return true if all ingredient fields are 0, otherwise return false
     */
    public boolean checkRecipe () {
        if ( ingredients.size() == 0 ) {
            return false;
        }
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( amounts.get( i ) != 0 ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    /**
     * Updates the fields to be equal to the passed Recipe
     *
     * @param r
     *            with updated fields
     */
    public void updateRecipe ( final Recipe r ) {
        ingredients = r.getIngredients();
        amounts = r.getAmounts();
        setPrice( r.getPrice() );
    }

    /**
     * Returns the name of the recipe and additional contents.
     *
     * @return String of recipe
     */
    @Override
    public String toString () {
        final StringBuilder sb = new StringBuilder();
        sb.append( name + "\n" );
        sb.append( price + "\n" );
        for ( int i = 0; i < ingredients.size(); i++ ) {
            sb.append( ingredients.get( i ).toString() + "\n" );
        }
        return sb.toString();
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        Integer result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Recipe other = (Recipe) obj;
        if ( name == null ) {
            if ( other.name != null ) {
                return false;
            }
        }
        else if ( !name.equals( other.name ) ) {
            return false;
        }
        return true;
    }

}
