package com.main.dhbworld.Cantine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest

public class MealDailyPlanTest {

    private String inputFromApi="[{\"id\":10995080,\"name\":\"Reine Kalbsbratwurst mit Currysauce und Baguette\",\"category\":\"Wahlessen 1\",\"prices\":{\"students\":2.0,\"employees\":2.4,\"pupils\":2.4,\"others\":2.4},\"notes\":[\"Sellerie\",\"Senf\",\"Weizen\",\"enthält Rindfleisch\"]},{\"id\":10995081,\"name\":\"Pommes\",\"category\":\"Wahlessen 1\",\"prices\":{\"students\":1.05,\"employees\":1.05,\"pupils\":1.05,\"others\":1.3},\"notes\":[\"veganes Gericht\"]},{\"id\":10995082,\"name\":\"Gemüse\",\"category\":\"Wahlessen 1\",\"prices\":{\"students\":0.9,\"employees\":0.9,\"pupils\":0.9,\"others\":1.15},\"notes\":[\"Sellerie\",\"veganes Gericht\"]},{\"id\":10995083,\"name\":\"Vegane Bratwurst mit Currysauce und Baguette\",\"category\":\"Wahlessen 2\",\"prices\":{\"students\":2.0,\"employees\":2.4,\"pupils\":2.4,\"others\":2.4},\"notes\":[\"Sellerie\",\"Senf\",\"Soja\",\"Weizen\",\"veganes Gericht\"]},{\"id\":10995084,\"name\":\"Verschiedene Dessert\",\"category\":\"Wahlessen 2\",\"prices\":{\"students\":1.05,\"employees\":1.05,\"pupils\":1.05,\"others\":1.3},\"notes\":[\"mit Farbstoff \",\"Milch/Laktose\",\"vegetarisches Gericht\"]},{\"id\":10995085,\"name\":\"Grüner Mischsalat\",\"category\":\"Wahlessen 2\",\"prices\":{\"students\":0.9,\"employees\":0.9,\"pupils\":0.9,\"others\":1.15},\"notes\":[\"Sellerie\",\"veganes Gericht\"]}]";
    @Test
    public void test_createMealPlan() throws JSONException {
        MealDailyPlan plan= new MealDailyPlan(inputFromApi);
        assertThat(plan.getMeal()[0].getName(), is("Reine Kalbsbratwurst mit Currysauce und Baguette"));
        assertThat(plan.getMeal()[0].getCategory(), is("Wahlessen 1"));
        assertThat(plan.getMeal()[0].getPrice(), is("2.00"));
        assertThat(plan.getMeal()[0].getNotes().size(), is(4));
        assertThat(plan.getMeal()[0].getNotes().get(0), is("Sellerie"));
        assertThat(plan.getMeal()[0].getNotes().get(1), is("Senf"));

    }

}
