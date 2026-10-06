# Smart Pantry Manager

An Android app (Java) that helps reduce food waste. The user records the ingredients they already have at home, and the app suggests only the recipes they can cook right now, with no shopping trip needed.

## Features
- Pantry management: add, edit and delete ingredients (name, quantity, unit, optional expiry date)
- Pantry list (RecyclerView with a custom adapter) loaded from the database
- 18 recipes seeded into the database on first run
- Suggested Recipes screen using the strict-matching rule: a recipe appears only if every ingredient is in the pantry in at least the required quantity
- Matching copes with plural/singular names ("tomato" vs "Tomatoes"), letter case and unit differences (0.5 kg flour satisfies 200 g flour)
- Recipe detail screen (ingredients and method)
- Settings screen (expiring-soon alert toggle)
- Friendly message when no recipes match
- No Google Maps, mapping SDK or location features

## Database choice: SQLite
SQLite via `SQLiteOpenHelper` runs locally on the device, needs no account, server or internet, and persists data between app launches. That makes it a good fit for a personal pantry app that should work offline.

## Setup / run
1. Install Android Studio (Hedgehog or newer).
2. Clone this repository: `git clone <your-repo-url>`
3. Open the folder with *File > Open* and let Gradle sync.
4. Run the `app` configuration on an emulator or phone (Android 7.0 / API 24 or higher).

## Project structure
| File | Purpose |
|---|---|
| `DatabaseHelper` | SQLite tables, CRUD and recipe seeding |
| `RecipeMatcher` | Strict-matching algorithm |
| `NameUtil` / `UnitUtil` | Name and unit normalisation |
| `MainActivity` + `PantryAdapter` | Pantry list |
| `AddEditActivity` | Add/Edit form with validation |
| `SuggestedActivity` + `RecipeAdapter` | Suggested recipes |
| `RecipeDetailActivity` | Recipe details |
| `SettingsActivity` | Settings |

## Author
Yasteel Haripershad, 402306816, Mobile App Development 700, Richfield 
