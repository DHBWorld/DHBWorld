package com.main.dhbworld.Cantine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class MealTest {

    @Test
    public void createDefaultMeal(){
        Meal meal= new Meal();
        assertThat("No category", is(meal.getCategory()));
        assertThat(meal.getPrice(), is("-"));
    }

    @Test
    public void createSpecialMeal(){
        Meal meal= new Meal();
        meal.setName("Something special");
        meal.setCategory("Wahlessen 1");
        meal.setPrice("1.00");
        meal.addNote("vegan");

        assertThat(meal.getName(), is("Something special"));
        assertThat(meal.getCategory(), is("Wahlessen 1"));
        assertThat(meal.getPrice(), is("1.00"));
        assertThat(meal.getNotes().size(), is(1));
        assertThat(meal.getNotes().get(0), is("vegan"));
    }

    @Test
    public void variousPrices(){
        Meal meal= new Meal();
        meal.setName("Something special");
        meal.setCategory("Wahlessen 1");
        meal.addNote("vegan");

        meal.setPrice("1");
        assertThat(meal.getPrice(), is("1.00"));
        meal.setPrice("1.0");
        assertThat(meal.getPrice(), is("1.00"));
        meal.setPrice("1.01");
        assertThat(meal.getPrice(), is("1.01"));
        meal.setPrice("100.01");
        assertThat(meal.getPrice(), is("100.01"));
        meal.setPrice("1000");
        assertThat(meal.getPrice(), is("1000.00"));
        meal.setPrice("-");
        assertThat(meal.getPrice(), is("-"));
        meal.setPrice("null");
        assertThat(meal.getPrice(), is("-"));
        meal.setPrice("undefined");
        assertThat(meal.getPrice(), is("-"));
    }

}