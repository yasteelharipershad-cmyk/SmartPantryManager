package com.smartpantry.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/** Suggested Recipes screen: runs the strict-matching logic against the current pantry. */
public class SuggestedActivity extends BaseActivity {

    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        setTitle("Suggested Recipes");

        db = new DatabaseHelper(this);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rv = findViewById(R.id.rvRecipes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });
        rv.setAdapter(adapter);

        setupBottomNav(R.id.nav_suggested);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Recipe> suggested = RecipeMatcher.getSuggested(db.getAllItems(), db.getAllRecipes());
        adapter.setRecipes(suggested);
        // Friendly feedback instead of a blank screen when nothing matches.
        tvEmpty.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
