# Report and video draft (delete this file before zipping if you like)

Replace every [bracket] with your own details. Add your own screenshots. Put your own words into anything you do not fully understand, because you must be able to explain it.

## 3. Introduction
Food waste happens when people do not know what to cook with what they already have. Smart Pantry Manager lets a user record their pantry and suggests only recipes they can make immediately. It is aimed at students and households who want to save money and waste less food.

## 4. System design
**Screen flow:** Pantry List <-> Add/Edit Ingredient; Pantry List -> Suggested Recipes -> Recipe Detail; Settings. The bottom navigation bar links Pantry, Recipes and Settings.

**Data model (ER):**
- pantry (id PK, name, quantity, unit, expiry)
- recipes (id PK, name, steps)
- recipe_ingredients (id PK, recipe_id FK -> recipes.id, name, quantity, unit)
One recipe has many recipe_ingredients. Pantry is independent and is compared with recipe_ingredients by the matcher.

## 5. Screenshots (take these yourself from your running app)
1. Empty pantry message
2. Add Ingredient form
3. Validation error (empty name / invalid quantity)
4. Pantry list with items
5. Edit Ingredient
6. Delete confirmation dialog
7. Expiring-soon highlight (item with a date within 3 days)
8. Suggested Recipes with results
9. Suggested Recipes empty message
10. Recipe Detail
11. Settings screen

## 6. Key code snippets (pick 3-5)
1. `RecipeMatcher.canMake()`: the strict rule. Returns false as soon as one ingredient is missing or insufficient.
2. `NameUtil.normalize()`: lowercases, strips punctuation and singularises so "Tomatoes" matches "tomato".
3. `UnitUtil.toBase()`: converts kg/l/tbsp etc. to g/ml so quantities compare correctly.
4. `DatabaseHelper.getAllItems()` / `addItem()`: SQLite CRUD.
5. `PantryAdapter.onBindViewHolder()`: binds a row and applies expiry colours.

## 7. Challenges and solutions (replace with the real ones you hit)
- [e.g. Plural names not matching -> wrote NameUtil to normalise names]
- [e.g. List not refreshing after edit -> moved the reload into onResume()]
- [e.g. Different units not comparable -> convert everything to base units]

## 8. Conclusion and reflection
[What you learned: Activity lifecycle, SQLite, RecyclerView. Improvements: "Almost There" list, Room library, barcode scanning, shopping list.]

## 9. References (Harvard)
- Android Developers (2024) *Save data using SQLite*. Available at: https://developer.android.com/training/data-storage/sqlite (Accessed: [date]).
- Android Developers (2024) *Create dynamic lists with RecyclerView*. Available at: https://developer.android.com/develop/ui/views/layout/recyclerview (Accessed: [date]).
- Android Developers (2024) *Intents and intent filters*. Available at: https://developer.android.com/guide/components/intents-filters (Accessed: [date]).

---
# Video script (5-7 min, speak in your own words)
1. **GitHub (about 1 min):** open the repo, scroll the commit history, say how the project grew step by step.
2. **Live demo (2-3 min):** add an ingredient, edit it, delete one, show validation. Add ingredients until a recipe appears in Suggested Recipes, delete one ingredient and show it disappear. Close and reopen the app to show the data persists.
3. **Concepts (2-3 min), pick three, with code on screen:** how RecipeMatcher decides a recipe qualifies; how PantryAdapter and RecyclerView display data; how Intents pass the item id from MainActivity to AddEditActivity.
4. **Database (30-60 s):** SQLite is local, offline, needs no server, and fits a personal app.

# Suggested commit sequence (commit after each real step, do not backdate)
1. Create project and Gradle setup
2. Add model classes (PantryItem, Recipe, RecipeIngredient)
3. Add DatabaseHelper with pantry CRUD
4. Add pantry list layout and PantryAdapter
5. Add AddEditActivity with validation
6. Wire edit and delete in MainActivity
7. Seed 18 recipes in DatabaseHelper
8. Add NameUtil and UnitUtil
9. Implement RecipeMatcher (strict rule)
10. Add SuggestedActivity and RecipeAdapter
11. Add RecipeDetailActivity
12. Add SettingsActivity and bottom navigation
13. Add README
