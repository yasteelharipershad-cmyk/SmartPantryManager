package com.smartpantry.app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/** Recipe Detail screen: full ingredient list and method. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long id = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = new DatabaseHelper(this).getRecipe(id);
        if (recipe == null) {
            finish();
            return;
        }
        setTitle("Recipe");

        ((TextView) findViewById(R.id.tvTitle)).setText(recipe.getName());

        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : recipe.getIngredients()) {
            sb.append("\u2022 ").append(UnitUtil.format(ing.getQuantity())).append(' ')
                    .append(ing.getUnit()).append(' ').append(ing.getName()).append('\n');
        }
        ((TextView) findViewById(R.id.tvIngredients)).setText(sb.toString().trim());
        ((TextView) findViewById(R.id.tvSteps)).setText(recipe.getSteps());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
