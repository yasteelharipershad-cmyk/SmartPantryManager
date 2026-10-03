package com.smartpantry.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Local SQLite storage. Tables: pantry, recipes, recipe_ingredients.
 * Recipes are seeded once, in onCreate(), the first time the database is created.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smartpantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "recipe_id INTEGER NOT NULL, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, "
                + "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    // ---------------------------------------------------------------- Pantry CRUD

    /** CREATE */
    public long addItem(String name, double quantity, String unit, String expiry) {
        return getWritableDatabase().insert("pantry", null, toValues(name, quantity, unit, expiry));
    }

    /** READ (all) */
    public List<PantryItem> getAllItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("pantry", null, null, null, null, null,
                "name COLLATE NOCASE ASC")) {
            while (c.moveToNext()) items.add(readItem(c));
        }
        return items;
    }

    /** READ (one) */
    public PantryItem getItem(long id) {
        try (Cursor c = getReadableDatabase().query("pantry", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? readItem(c) : null;
        }
    }

    /** UPDATE */
    public int updateItem(long id, String name, double quantity, String unit, String expiry) {
        return getWritableDatabase().update("pantry", toValues(name, quantity, unit, expiry),
                "id = ?", new String[]{String.valueOf(id)});
    }

    /** DELETE */
    public int deleteItem(long id) {
        return getWritableDatabase().delete("pantry", "id = ?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(String name, double quantity, String unit, String expiry) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("quantity", quantity);
        cv.put("unit", unit);
        cv.put("expiry", expiry);
        return cv;
    }

    private PantryItem readItem(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.getString(c.getColumnIndexOrThrow("expiry")));
    }

    // ---------------------------------------------------------------- Recipes

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query("recipes", null, null, null, null, null, "name ASC")) {
            while (c.moveToNext()) list.add(readRecipe(db, c));
        }
        return list;
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query("recipes", null, "id = ?", new String[]{String.valueOf(id)},
                null, null, null)) {
            return c.moveToFirst() ? readRecipe(db, c) : null;
        }
    }

    private Recipe readRecipe(SQLiteDatabase db, Cursor c) {
        Recipe r = new Recipe(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getString(c.getColumnIndexOrThrow("steps")));
        List<RecipeIngredient> ings = new ArrayList<>();
        try (Cursor ic = db.query("recipe_ingredients", null, "recipe_id = ?",
                new String[]{String.valueOf(r.getId())}, null, null, "id ASC")) {
            while (ic.moveToNext()) {
                ings.add(new RecipeIngredient(
                        ic.getString(ic.getColumnIndexOrThrow("name")),
                        ic.getDouble(ic.getColumnIndexOrThrow("quantity")),
                        ic.getString(ic.getColumnIndexOrThrow("unit"))));
            }
        }
        r.setIngredients(ings);
        return r;
    }

    // ---------------------------------------------------------------- Seed data (18 recipes)

    private void seedRecipes(SQLiteDatabase db) {
        // Ingredient format: "name|quantity|unit"
        addRecipe(db, "Scrambled Eggs",
                "1. Whisk the eggs with the salt.\n2. Melt the butter in a pan on medium heat.\n3. Pour in the eggs and stir gently until just set.",
                "egg|3|pcs", "butter|10|g", "salt|1|g");
        addRecipe(db, "Tomato Omelette",
                "1. Chop the tomato.\n2. Beat the eggs with salt.\n3. Heat the oil, add the tomato for 1 minute.\n4. Pour in the eggs, cook until set and fold.",
                "egg|2|pcs", "tomato|1|pcs", "salt|1|g", "oil|1|tbsp");
        addRecipe(db, "Cheese Toast",
                "1. Butter the bread.\n2. Top with cheese.\n3. Grill or toast until the cheese melts and bubbles.",
                "bread|2|pcs", "cheese|30|g", "butter|10|g");
        addRecipe(db, "Pasta with Tomato Sauce",
                "1. Boil the pasta in salted water until al dente.\n2. Fry the crushed garlic in olive oil.\n3. Add chopped tomatoes and simmer for 10 minutes.\n4. Toss with the drained pasta.",
                "pasta|200|g", "tomato|3|pcs", "garlic|2|pcs", "olive oil|2|tbsp", "salt|1|tsp");
        addRecipe(db, "Egg Fried Rice",
                "1. Dice the onion and fry in oil.\n2. Push aside, scramble the eggs in the pan.\n3. Add the cooked rice and soy sauce.\n4. Stir-fry for 4 minutes on high heat.",
                "rice|200|g", "egg|2|pcs", "onion|1|pcs", "soy sauce|2|tbsp", "oil|1|tbsp");
        addRecipe(db, "Pancakes",
                "1. Mix the flour, sugar, milk and egg into a smooth batter.\n2. Melt butter in a pan.\n3. Pour in small rounds of batter.\n4. Cook 2 minutes each side.",
                "flour|150|g", "milk|200|ml", "egg|1|pcs", "sugar|1|tbsp", "butter|20|g");
        addRecipe(db, "Mashed Potatoes",
                "1. Peel and chop the potatoes.\n2. Boil until soft, about 15 minutes.\n3. Drain and mash with butter, milk and salt.",
                "potato|4|pcs", "butter|30|g", "milk|50|ml", "salt|1|tsp");
        addRecipe(db, "Chicken Stir Fry",
                "1. Slice the chicken, pepper and onion.\n2. Heat the oil, fry the chicken until golden.\n3. Add vegetables and garlic.\n4. Pour in soy sauce and cook for 5 minutes.",
                "chicken|300|g", "onion|1|pcs", "bell pepper|1|pcs", "soy sauce|2|tbsp", "oil|1|tbsp", "garlic|2|pcs");
        addRecipe(db, "Simple Guacamole",
                "1. Mash the avocados.\n2. Dice the tomato and stir in.\n3. Add lime juice and salt, mix well.",
                "avocado|2|pcs", "lime|1|pcs", "tomato|1|pcs", "salt|1|tsp");
        addRecipe(db, "Banana Smoothie",
                "1. Peel the bananas.\n2. Blend with milk and honey until smooth.",
                "banana|2|pcs", "milk|250|ml", "honey|1|tbsp");
        addRecipe(db, "Vegetable Soup",
                "1. Chop the carrots, potatoes and onion.\n2. Add to a pot with the stock.\n3. Simmer for 25 minutes.\n4. Season with salt.",
                "carrot|2|pcs", "potato|2|pcs", "onion|1|pcs", "vegetable stock|500|ml", "salt|1|tsp");
        addRecipe(db, "Garlic Bread",
                "1. Mix softened butter with crushed garlic.\n2. Spread on the bread.\n3. Bake at 200 C for 8 minutes.",
                "bread|4|pcs", "butter|40|g", "garlic|2|pcs");
        addRecipe(db, "Cheese Omelette",
                "1. Beat the eggs.\n2. Melt butter in a pan and add the eggs.\n3. Sprinkle cheese on top, fold and serve.",
                "egg|3|pcs", "cheese|40|g", "butter|10|g");
        addRecipe(db, "Tuna Salad",
                "1. Drain the tuna.\n2. Dice the onion.\n3. Mix everything with the mayonnaise.",
                "tuna|150|g", "mayonnaise|2|tbsp", "onion|1|pcs");
        addRecipe(db, "Peanut Butter Sandwich",
                "1. Spread peanut butter on one slice of bread.\n2. Top with the second slice.",
                "bread|2|pcs", "peanut butter|2|tbsp");
        addRecipe(db, "Rice and Beans",
                "1. Fry onion and garlic in oil.\n2. Add the beans and rice with water.\n3. Simmer for 20 minutes until the rice is cooked.",
                "rice|200|g", "beans|400|g", "onion|1|pcs", "garlic|2|pcs", "oil|1|tbsp");
        addRecipe(db, "Fruit Salad",
                "1. Wash and chop the apple.\n2. Slice the banana and peel the orange.\n3. Combine in a bowl.",
                "apple|1|pcs", "banana|1|pcs", "orange|1|pcs");
        addRecipe(db, "Loaded Baked Potatoes",
                "1. Prick the potatoes and bake at 200 C for 50 minutes.\n2. Cut open and add butter and salt.\n3. Top with grated cheese.",
                "potato|2|pcs", "butter|20|g", "cheese|40|g", "salt|1|tsp");
    }

    private void addRecipe(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("steps", steps);
        long recipeId = db.insert("recipes", null, cv);
        for (String s : ingredients) {
            String[] p = s.split("\\|");
            ContentValues ic = new ContentValues();
            ic.put("recipe_id", recipeId);
            ic.put("name", p[0]);
            ic.put("quantity", Double.parseDouble(p[1]));
            ic.put("unit", p[2]);
            db.insert("recipe_ingredients", null, ic);
        }
    }
}
