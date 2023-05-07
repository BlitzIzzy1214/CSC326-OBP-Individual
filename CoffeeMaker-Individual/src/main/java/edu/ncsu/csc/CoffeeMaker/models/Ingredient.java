package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

/**
 * Ingredient for CoffeeMaker. Contains the name and quantity for the
 * ingredient.
 *
 * @author Lev Emerson ememerso
 */
@Entity
public class Ingredient extends DomainObject {

    /** Ingredient ID */
    @Id
    @GeneratedValue
    private Long    id;

    /** Name of ingredient */
    private String  name;

    /** Quantity of ingredient in inventory */
    @Min ( 0 )
    private Integer amount;

    /**
     * Empty Ingredient constructor
     */
    public Ingredient () {
        this.name = "";
        this.amount = 0;
    }

    /**
     * Ingredient constructor with name and quantity
     *
     * @param name
     *            name of ingredient
     * @param amount
     *            inventory quantity of ingredient
     */
    public Ingredient ( final String name, final Integer amount ) {
        setName( name );
        setAmount( amount );
    }

    /**
     * Returns the name of the ingredient
     *
     * @return ingredient name
     */
    public String getName () {
        return name;
    }

    /**
     * Set the name of the ingredient
     *
     * @param name
     *            of ingredient
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the quantity of the ingredient in the inventory
     *
     * @return inventory quantity of ingredient
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * Set quantity of ingredient in inventory
     *
     * @param amount
     *            inventory quantity
     */
    public void setAmount ( final Integer amount ) {
        if ( amount < 0 ) {
            throw new IllegalArgumentException( "Amount cannot be negative" );
        }
        this.amount = amount;
    }

    /**
     * Get the ID of the Ingredient
     *
     * @return the ID
     */
    @Override
    public Serializable getId () {
        return id;
    }

    /**
     * Set the ID of the Ingredient (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final long id ) {
        this.id = id;
    }

    /**
     * Returns the name and quantity of the ingredient.
     *
     * @return String of ingredient
     */
    @Override
    public String toString () {
        return name + ": " + amount;
    }

}
