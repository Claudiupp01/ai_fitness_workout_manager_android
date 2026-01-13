package com.example.ai_fitness_workout_manager.database

import com.example.ai_fitness_workout_manager.model.Ingredient
import com.example.ai_fitness_workout_manager.model.MealRecipe

object RecipeDatabase {

    private val breakfastRecipes = listOf(
        MealRecipe(
            id = "b1",
            name = "Classic Oatmeal with Berries",
            mealType = MealRecipe.TYPE_BREAKFAST,
            description = "Creamy oatmeal topped with fresh berries and honey",
            prepTime = 5,
            cookTime = 10,
            servings = 1,
            difficulty = "easy",
            calories = 350,
            protein = 12f,
            carbs = 55f,
            fat = 8f,
            fiber = 8f,
            ingredients = listOf(
                Ingredient("rolled oats", 50f, "g"),
                Ingredient("milk", 250f, "ml"),
                Ingredient("mixed berries", 100f, "g"),
                Ingredient("honey", 1f, "tbsp"),
                Ingredient("chia seeds", 1f, "tsp")
            ),
            steps = listOf(
                "Bring milk to a gentle boil in a small pot",
                "Add rolled oats and reduce heat to medium-low",
                "Cook for 8-10 minutes, stirring occasionally, until creamy",
                "Transfer to a bowl and top with fresh berries",
                "Drizzle with honey and sprinkle chia seeds on top"
            )
        ),
        MealRecipe(
            id = "b2",
            name = "Protein Pancakes",
            mealType = MealRecipe.TYPE_BREAKFAST,
            description = "Fluffy pancakes packed with protein",
            prepTime = 10,
            cookTime = 15,
            servings = 2,
            difficulty = "easy",
            calories = 420,
            protein = 35f,
            carbs = 45f,
            fat = 10f,
            fiber = 5f,
            ingredients = listOf(
                Ingredient("banana", 1f, "medium"),
                Ingredient("eggs", 3f, "whole"),
                Ingredient("protein powder", 30f, "g"),
                Ingredient("oat flour", 40f, "g"),
                Ingredient("baking powder", 1f, "tsp"),
                Ingredient("vanilla extract", 0.5f, "tsp")
            ),
            steps = listOf(
                "Mash banana in a bowl until smooth",
                "Add eggs and whisk together",
                "Mix in protein powder, oat flour, baking powder, and vanilla",
                "Heat a non-stick pan over medium heat",
                "Pour batter to form pancakes and cook 2-3 minutes per side",
                "Serve with maple syrup or fresh fruit"
            )
        ),
        MealRecipe(
            id = "b3",
            name = "Avocado Toast with Eggs",
            mealType = MealRecipe.TYPE_BREAKFAST,
            description = "Whole grain toast with creamy avocado and poached eggs",
            prepTime = 5,
            cookTime = 10,
            servings = 1,
            difficulty = "easy",
            calories = 380,
            protein = 18f,
            carbs = 35f,
            fat = 20f,
            fiber = 10f,
            ingredients = listOf(
                Ingredient("whole grain bread", 2f, "slices"),
                Ingredient("avocado", 1f, "medium"),
                Ingredient("eggs", 2f, "whole"),
                Ingredient("lemon juice", 1f, "tsp"),
                Ingredient("red pepper flakes", 0.5f, "tsp"),
                Ingredient("salt", 0.25f, "tsp")
            ),
            steps = listOf(
                "Toast bread until golden brown",
                "Mash avocado with lemon juice and salt",
                "Spread avocado mixture on toast",
                "Poach or fry eggs to your preference",
                "Place eggs on top of avocado toast",
                "Sprinkle with red pepper flakes and serve"
            )
        ),
        MealRecipe(
            id = "b4",
            name = "Greek Yogurt Parfait",
            mealType = MealRecipe.TYPE_BREAKFAST,
            description = "Layered yogurt with granola and fresh fruit",
            prepTime = 10,
            cookTime = 0,
            servings = 1,
            difficulty = "easy",
            calories = 320,
            protein = 22f,
            carbs = 42f,
            fat = 8f,
            fiber = 6f,
            ingredients = listOf(
                Ingredient("Greek yogurt", 200f, "g"),
                Ingredient("granola", 40f, "g"),
                Ingredient("strawberries", 80f, "g"),
                Ingredient("blueberries", 50f, "g"),
                Ingredient("honey", 1f, "tbsp"),
                Ingredient("almonds", 15f, "g")
            ),
            steps = listOf(
                "Slice strawberries into quarters",
                "In a glass or bowl, add a layer of Greek yogurt",
                "Add a layer of granola and berries",
                "Repeat layers until ingredients are used",
                "Top with chopped almonds and drizzle with honey"
            )
        ),
        MealRecipe(
            id = "b5",
            name = "Scrambled Eggs with Spinach",
            mealType = MealRecipe.TYPE_BREAKFAST,
            description = "Fluffy scrambled eggs with sautéed spinach and feta",
            prepTime = 5,
            cookTime = 8,
            servings = 1,
            difficulty = "easy",
            calories = 290,
            protein = 24f,
            carbs = 8f,
            fat = 18f,
            fiber = 2f,
            ingredients = listOf(
                Ingredient("eggs", 3f, "whole"),
                Ingredient("fresh spinach", 50f, "g"),
                Ingredient("feta cheese", 30f, "g"),
                Ingredient("butter", 10f, "g"),
                Ingredient("milk", 30f, "ml"),
                Ingredient("black pepper", 0.25f, "tsp")
            ),
            steps = listOf(
                "Whisk eggs with milk and black pepper",
                "Melt butter in a non-stick pan over medium heat",
                "Add spinach and sauté until wilted, about 2 minutes",
                "Pour in egg mixture and gently stir",
                "Cook until eggs are softly set, about 3-4 minutes",
                "Crumble feta on top before serving"
            )
        ),
        MealRecipe(
            id = "b6",
            name = "Banana Smoothie Bowl",
            mealType = MealRecipe.TYPE_BREAKFAST,
            description = "Thick smoothie bowl topped with your favorite toppings",
            prepTime = 10,
            cookTime = 0,
            servings = 1,
            difficulty = "easy",
            calories = 385,
            protein = 15f,
            carbs = 62f,
            fat = 10f,
            fiber = 9f,
            ingredients = listOf(
                Ingredient("frozen banana", 2f, "medium"),
                Ingredient("protein powder", 25f, "g"),
                Ingredient("almond milk", 100f, "ml"),
                Ingredient("peanut butter", 1f, "tbsp"),
                Ingredient("granola", 30f, "g"),
                Ingredient("sliced banana", 0.5f, "medium")
            ),
            steps = listOf(
                "Add frozen bananas, protein powder, almond milk, and peanut butter to blender",
                "Blend until thick and creamy (add more milk if too thick)",
                "Pour into a bowl",
                "Top with granola and sliced banana",
                "Add any other desired toppings (berries, coconut, seeds)"
            )
        ),
        MealRecipe(
            id = "b7",
            name = "French Toast with Cinnamon",
            mealType = MealRecipe.TYPE_BREAKFAST,
            description = "Classic French toast with a hint of cinnamon",
            prepTime = 5,
            cookTime = 10,
            servings = 2,
            difficulty = "easy",
            calories = 410,
            protein = 16f,
            carbs = 52f,
            fat = 14f,
            fiber = 4f,
            ingredients = listOf(
                Ingredient("thick bread slices", 4f, "slices"),
                Ingredient("eggs", 3f, "whole"),
                Ingredient("milk", 80f, "ml"),
                Ingredient("cinnamon", 1f, "tsp"),
                Ingredient("vanilla extract", 1f, "tsp"),
                Ingredient("butter", 20f, "g")
            ),
            steps = listOf(
                "Whisk together eggs, milk, cinnamon, and vanilla in a shallow bowl",
                "Heat butter in a large pan over medium heat",
                "Dip each bread slice in egg mixture, coating both sides",
                "Cook bread for 3-4 minutes per side until golden brown",
                "Serve with maple syrup, fresh berries, or powdered sugar"
            )
        ),
        MealRecipe(
            id = "b8",
            name = "Veggie Omelette",
            mealType = MealRecipe.TYPE_BREAKFAST,
            description = "Fluffy omelette filled with colorful vegetables",
            prepTime = 10,
            cookTime = 10,
            servings = 1,
            difficulty = "medium",
            calories = 310,
            protein = 22f,
            carbs = 12f,
            fat = 20f,
            fiber = 4f,
            ingredients = listOf(
                Ingredient("eggs", 3f, "whole"),
                Ingredient("bell pepper", 50f, "g"),
                Ingredient("mushrooms", 40f, "g"),
                Ingredient("onion", 30f, "g"),
                Ingredient("cheddar cheese", 30f, "g"),
                Ingredient("olive oil", 1f, "tbsp")
            ),
            steps = listOf(
                "Dice bell pepper, mushrooms, and onion",
                "Heat olive oil in a non-stick pan",
                "Sauté vegetables until softened, about 5 minutes",
                "Whisk eggs and pour over vegetables",
                "Cook until edges set, then add cheese to one half",
                "Fold omelette in half and cook 1 more minute"
            )
        ),
        MealRecipe(
            id = "b9",
            name = "Peanut Butter Overnight Oats",
            mealType = MealRecipe.TYPE_BREAKFAST,
            description = "No-cook overnight oats with peanut butter",
            prepTime = 5,
            cookTime = 0,
            servings = 1,
            difficulty = "easy",
            calories = 395,
            protein = 18f,
            carbs = 48f,
            fat = 15f,
            fiber = 8f,
            ingredients = listOf(
                Ingredient("rolled oats", 50f, "g"),
                Ingredient("milk", 150f, "ml"),
                Ingredient("Greek yogurt", 60f, "g"),
                Ingredient("peanut butter", 2f, "tbsp"),
                Ingredient("honey", 1f, "tbsp"),
                Ingredient("chia seeds", 1f, "tsp")
            ),
            steps = listOf(
                "Mix all ingredients in a jar or container",
                "Stir well to combine",
                "Cover and refrigerate overnight (at least 6 hours)",
                "In the morning, stir and add more milk if needed",
                "Top with banana slices or berries before eating"
            )
        ),
        MealRecipe(
            id = "b10",
            name = "Breakfast Burrito",
            mealType = MealRecipe.TYPE_BREAKFAST,
            description = "Hearty burrito with eggs, beans, and cheese",
            prepTime = 10,
            cookTime = 15,
            servings = 1,
            difficulty = "medium",
            calories = 520,
            protein = 28f,
            carbs = 48f,
            fat = 24f,
            fiber = 8f,
            ingredients = listOf(
                Ingredient("large tortilla", 1f, "whole"),
                Ingredient("eggs", 2f, "whole"),
                Ingredient("black beans", 80f, "g"),
                Ingredient("cheddar cheese", 40f, "g"),
                Ingredient("salsa", 3f, "tbsp"),
                Ingredient("avocado", 0.5f, "medium")
            ),
            steps = listOf(
                "Scramble eggs in a pan",
                "Warm black beans in a separate pan",
                "Heat tortilla for 20 seconds",
                "Layer eggs, beans, cheese, and salsa in center of tortilla",
                "Add sliced avocado",
                "Fold sides in and roll tightly into a burrito"
            )
        )
    )

    private val lunchRecipes = listOf(
        MealRecipe(
            id = "l1",
            name = "Grilled Chicken Caesar Salad",
            mealType = MealRecipe.TYPE_LUNCH,
            description = "Classic Caesar salad with grilled chicken breast",
            prepTime = 15,
            cookTime = 15,
            servings = 1,
            difficulty = "easy",
            calories = 450,
            protein = 42f,
            carbs = 22f,
            fat = 20f,
            fiber = 5f,
            ingredients = listOf(
                Ingredient("chicken breast", 150f, "g"),
                Ingredient("romaine lettuce", 150f, "g"),
                Ingredient("parmesan cheese", 30f, "g"),
                Ingredient("Caesar dressing", 3f, "tbsp"),
                Ingredient("croutons", 30f, "g"),
                Ingredient("lemon", 0.5f, "whole")
            ),
            steps = listOf(
                "Season chicken breast with salt and pepper",
                "Grill chicken for 6-7 minutes per side until cooked through",
                "Let chicken rest for 5 minutes, then slice",
                "Chop romaine lettuce and place in a large bowl",
                "Add Caesar dressing and toss to coat",
                "Top with sliced chicken, parmesan, croutons, and lemon juice"
            )
        ),
        MealRecipe(
            id = "l2",
            name = "Quinoa Buddha Bowl",
            mealType = MealRecipe.TYPE_LUNCH,
            description = "Nutritious bowl with quinoa, roasted vegetables, and tahini dressing",
            prepTime = 15,
            cookTime = 30,
            servings = 2,
            difficulty = "medium",
            calories = 485,
            protein = 18f,
            carbs = 62f,
            fat = 18f,
            fiber = 12f,
            ingredients = listOf(
                Ingredient("quinoa", 100f, "g"),
                Ingredient("sweet potato", 150f, "g"),
                Ingredient("chickpeas", 100f, "g"),
                Ingredient("kale", 80f, "g"),
                Ingredient("tahini", 2f, "tbsp"),
                Ingredient("lemon juice", 2f, "tbsp")
            ),
            steps = listOf(
                "Cook quinoa according to package instructions",
                "Cube sweet potato and roast at 200°C for 25 minutes",
                "Roast chickpeas with sweet potato",
                "Massage kale with a little olive oil",
                "Mix tahini with lemon juice and water to make dressing",
                "Assemble bowl with quinoa, vegetables, and drizzle with dressing"
            )
        ),
        MealRecipe(
            id = "l3",
            name = "Turkey and Avocado Wrap",
            mealType = MealRecipe.TYPE_LUNCH,
            description = "Fresh wrap with turkey, avocado, and vegetables",
            prepTime = 10,
            cookTime = 0,
            servings = 1,
            difficulty = "easy",
            calories = 420,
            protein = 32f,
            carbs = 38f,
            fat = 16f,
            fiber = 8f,
            ingredients = listOf(
                Ingredient("whole wheat tortilla", 1f, "large"),
                Ingredient("turkey breast", 100f, "g"),
                Ingredient("avocado", 0.5f, "medium"),
                Ingredient("lettuce", 50f, "g"),
                Ingredient("tomato", 1f, "medium"),
                Ingredient("mustard", 1f, "tbsp")
            ),
            steps = listOf(
                "Lay tortilla flat on a clean surface",
                "Spread mustard evenly on tortilla",
                "Layer turkey slices in the center",
                "Add sliced avocado, lettuce, and tomato",
                "Fold sides in and roll tightly",
                "Cut in half diagonally and serve"
            )
        ),
        MealRecipe(
            id = "l4",
            name = "Lentil Soup",
            mealType = MealRecipe.TYPE_LUNCH,
            description = "Hearty and warming lentil soup with vegetables",
            prepTime = 10,
            cookTime = 35,
            servings = 4,
            difficulty = "easy",
            calories = 340,
            protein = 20f,
            carbs = 52f,
            fat = 6f,
            fiber = 16f,
            ingredients = listOf(
                Ingredient("red lentils", 200f, "g"),
                Ingredient("carrots", 150f, "g"),
                Ingredient("celery", 100f, "g"),
                Ingredient("onion", 1f, "medium"),
                Ingredient("vegetable broth", 1000f, "ml"),
                Ingredient("cumin", 1f, "tsp")
            ),
            steps = listOf(
                "Dice carrots, celery, and onion",
                "Sauté vegetables in a large pot for 5 minutes",
                "Add lentils, broth, and cumin",
                "Bring to a boil, then reduce heat and simmer",
                "Cook for 25-30 minutes until lentils are tender",
                "Season with salt and pepper, serve hot"
            )
        ),
        MealRecipe(
            id = "l5",
            name = "Tuna Nicoise Salad",
            mealType = MealRecipe.TYPE_LUNCH,
            description = "French-inspired salad with tuna, eggs, and vegetables",
            prepTime = 20,
            cookTime = 15,
            servings = 2,
            difficulty = "medium",
            calories = 395,
            protein = 35f,
            carbs = 28f,
            fat = 16f,
            fiber = 7f,
            ingredients = listOf(
                Ingredient("tuna", 150f, "g"),
                Ingredient("green beans", 150f, "g"),
                Ingredient("baby potatoes", 200f, "g"),
                Ingredient("eggs", 2f, "whole"),
                Ingredient("cherry tomatoes", 100f, "g"),
                Ingredient("olives", 50f, "g")
            ),
            steps = listOf(
                "Boil eggs for 8 minutes, then peel and halve",
                "Boil potatoes until tender, about 12 minutes",
                "Blanch green beans for 3 minutes",
                "Arrange vegetables on a plate",
                "Add tuna, halved eggs, tomatoes, and olives",
                "Drizzle with olive oil and lemon juice"
            )
        ),
        MealRecipe(
            id = "l6",
            name = "Chicken Stir-Fry",
            mealType = MealRecipe.TYPE_LUNCH,
            description = "Quick chicken and vegetable stir-fry with soy sauce",
            prepTime = 15,
            cookTime = 12,
            servings = 2,
            difficulty = "easy",
            calories = 420,
            protein = 38f,
            carbs = 35f,
            fat = 14f,
            fiber = 5f,
            ingredients = listOf(
                Ingredient("chicken breast", 250f, "g"),
                Ingredient("broccoli", 150f, "g"),
                Ingredient("bell pepper", 1f, "medium"),
                Ingredient("soy sauce", 3f, "tbsp"),
                Ingredient("garlic", 3f, "cloves"),
                Ingredient("ginger", 1f, "tsp")
            ),
            steps = listOf(
                "Cut chicken into bite-sized pieces",
                "Mince garlic and ginger",
                "Heat oil in a wok or large pan over high heat",
                "Stir-fry chicken for 5 minutes until cooked",
                "Add vegetables and stir-fry for 4 minutes",
                "Add soy sauce, garlic, and ginger, cook 2 more minutes"
            )
        ),
        MealRecipe(
            id = "l7",
            name = "Caprese Panini",
            mealType = MealRecipe.TYPE_LUNCH,
            description = "Grilled sandwich with mozzarella, tomato, and basil",
            prepTime = 10,
            cookTime = 8,
            servings = 1,
            difficulty = "easy",
            calories = 445,
            protein = 22f,
            carbs = 42f,
            fat = 20f,
            fiber = 4f,
            ingredients = listOf(
                Ingredient("ciabatta bread", 1f, "roll"),
                Ingredient("mozzarella cheese", 80f, "g"),
                Ingredient("tomato", 1f, "large"),
                Ingredient("fresh basil", 10f, "g"),
                Ingredient("balsamic glaze", 1f, "tbsp"),
                Ingredient("olive oil", 1f, "tsp")
            ),
            steps = listOf(
                "Slice ciabatta roll in half",
                "Layer mozzarella, sliced tomato, and basil on bottom half",
                "Drizzle with balsamic glaze",
                "Place top half of bread on sandwich",
                "Brush outside with olive oil",
                "Grill in panini press or pan for 4 minutes per side"
            )
        ),
        MealRecipe(
            id = "l8",
            name = "Salmon Poke Bowl",
            mealType = MealRecipe.TYPE_LUNCH,
            description = "Hawaiian-inspired bowl with fresh salmon and rice",
            prepTime = 20,
            cookTime = 15,
            servings = 2,
            difficulty = "medium",
            calories = 510,
            protein = 32f,
            carbs = 58f,
            fat = 16f,
            fiber = 4f,
            ingredients = listOf(
                Ingredient("sushi rice", 150f, "g"),
                Ingredient("fresh salmon", 200f, "g"),
                Ingredient("cucumber", 100f, "g"),
                Ingredient("edamame", 80f, "g"),
                Ingredient("avocado", 1f, "medium"),
                Ingredient("soy sauce", 2f, "tbsp")
            ),
            steps = listOf(
                "Cook sushi rice according to package instructions",
                "Cube salmon into bite-sized pieces",
                "Marinate salmon in soy sauce for 10 minutes",
                "Dice cucumber and slice avocado",
                "Divide rice between bowls",
                "Top with salmon, cucumber, edamame, and avocado"
            )
        ),
        MealRecipe(
            id = "l9",
            name = "Veggie Burger",
            mealType = MealRecipe.TYPE_LUNCH,
            description = "Homemade black bean and vegetable burger",
            prepTime = 15,
            cookTime = 20,
            servings = 4,
            difficulty = "medium",
            calories = 385,
            protein = 16f,
            carbs = 52f,
            fat = 12f,
            fiber = 12f,
            ingredients = listOf(
                Ingredient("black beans", 400f, "g"),
                Ingredient("breadcrumbs", 60f, "g"),
                Ingredient("onion", 1f, "medium"),
                Ingredient("garlic", 2f, "cloves"),
                Ingredient("cumin", 1f, "tsp"),
                Ingredient("burger buns", 4f, "whole")
            ),
            steps = listOf(
                "Mash black beans in a bowl, leaving some chunks",
                "Finely dice onion and garlic",
                "Mix beans, breadcrumbs, onion, garlic, and cumin",
                "Form into 4 patties",
                "Cook patties in a pan for 5 minutes per side",
                "Serve on buns with your favorite toppings"
            )
        ),
        MealRecipe(
            id = "l10",
            name = "Greek Chicken Pita",
            mealType = MealRecipe.TYPE_LUNCH,
            description = "Pita stuffed with Greek-seasoned chicken and tzatziki",
            prepTime = 15,
            cookTime = 15,
            servings = 2,
            difficulty = "easy",
            calories = 465,
            protein = 36f,
            carbs = 48f,
            fat = 14f,
            fiber = 6f,
            ingredients = listOf(
                Ingredient("chicken breast", 250f, "g"),
                Ingredient("pita bread", 2f, "whole"),
                Ingredient("Greek yogurt", 100f, "g"),
                Ingredient("cucumber", 100f, "g"),
                Ingredient("oregano", 1f, "tsp"),
                Ingredient("lemon", 1f, "whole")
            ),
            steps = listOf(
                "Season chicken with oregano, salt, and pepper",
                "Grill or pan-fry chicken for 6-7 minutes per side",
                "Grate cucumber and mix with yogurt to make tzatziki",
                "Slice cooked chicken into strips",
                "Warm pita bread",
                "Fill pita with chicken, tzatziki, and lettuce"
            )
        )
    )

    private val dinnerRecipes = listOf(
        MealRecipe(
            id = "d1",
            name = "Salmon with Roasted Vegetables",
            mealType = MealRecipe.TYPE_DINNER,
            description = "Oven-baked salmon with colorful roasted vegetables",
            prepTime = 15,
            cookTime = 25,
            servings = 2,
            difficulty = "easy",
            calories = 520,
            protein = 42f,
            carbs = 28f,
            fat = 26f,
            fiber = 8f,
            ingredients = listOf(
                Ingredient("salmon fillet", 300f, "g"),
                Ingredient("broccoli", 150f, "g"),
                Ingredient("bell pepper", 1f, "medium"),
                Ingredient("zucchini", 1f, "medium"),
                Ingredient("olive oil", 2f, "tbsp"),
                Ingredient("lemon", 1f, "whole")
            ),
            steps = listOf(
                "Preheat oven to 200°C",
                "Cut vegetables into bite-sized pieces",
                "Toss vegetables with olive oil, salt, and pepper",
                "Place salmon and vegetables on a baking tray",
                "Bake for 20-25 minutes until salmon is cooked through",
                "Squeeze fresh lemon juice over salmon before serving"
            )
        ),
        MealRecipe(
            id = "d2",
            name = "Spaghetti Bolognese",
            mealType = MealRecipe.TYPE_DINNER,
            description = "Classic Italian pasta with rich meat sauce",
            prepTime = 15,
            cookTime = 45,
            servings = 4,
            difficulty = "medium",
            calories = 580,
            protein = 32f,
            carbs = 68f,
            fat = 18f,
            fiber = 6f,
            ingredients = listOf(
                Ingredient("spaghetti", 320f, "g"),
                Ingredient("ground beef", 400f, "g"),
                Ingredient("tomato sauce", 400f, "g"),
                Ingredient("onion", 1f, "medium"),
                Ingredient("garlic", 3f, "cloves"),
                Ingredient("parmesan cheese", 40f, "g")
            ),
            steps = listOf(
                "Dice onion and mince garlic",
                "Brown ground beef in a large pan",
                "Add onion and garlic, cook for 3 minutes",
                "Add tomato sauce and simmer for 30 minutes",
                "Cook spaghetti according to package instructions",
                "Serve sauce over spaghetti, topped with parmesan"
            )
        ),
        MealRecipe(
            id = "d3",
            name = "Chicken Curry with Rice",
            mealType = MealRecipe.TYPE_DINNER,
            description = "Creamy chicken curry with fragrant basmati rice",
            prepTime = 15,
            cookTime = 35,
            servings = 4,
            difficulty = "medium",
            calories = 550,
            protein = 38f,
            carbs = 62f,
            fat = 16f,
            fiber = 4f,
            ingredients = listOf(
                Ingredient("chicken thighs", 500f, "g"),
                Ingredient("coconut milk", 400f, "ml"),
                Ingredient("curry paste", 3f, "tbsp"),
                Ingredient("basmati rice", 250f, "g"),
                Ingredient("onion", 1f, "medium"),
                Ingredient("ginger", 1f, "tbsp")
            ),
            steps = listOf(
                "Cook rice according to package instructions",
                "Cut chicken into chunks",
                "Sauté onion and ginger in a large pan",
                "Add curry paste and cook for 1 minute",
                "Add chicken and cook until browned",
                "Pour in coconut milk and simmer for 20 minutes"
            )
        ),
        MealRecipe(
            id = "d4",
            name = "Beef Tacos",
            mealType = MealRecipe.TYPE_DINNER,
            description = "Seasoned ground beef tacos with fresh toppings",
            prepTime = 15,
            cookTime = 15,
            servings = 4,
            difficulty = "easy",
            calories = 480,
            protein = 28f,
            carbs = 42f,
            fat = 22f,
            fiber = 7f,
            ingredients = listOf(
                Ingredient("ground beef", 400f, "g"),
                Ingredient("taco shells", 8f, "shells"),
                Ingredient("lettuce", 100f, "g"),
                Ingredient("tomato", 2f, "medium"),
                Ingredient("cheddar cheese", 100f, "g"),
                Ingredient("taco seasoning", 2f, "tbsp")
            ),
            steps = listOf(
                "Brown ground beef in a pan over medium-high heat",
                "Add taco seasoning and water according to package",
                "Simmer for 5 minutes until thickened",
                "Warm taco shells according to package",
                "Shred lettuce and dice tomatoes",
                "Assemble tacos with beef and toppings"
            )
        ),
        MealRecipe(
            id = "d5",
            name = "Stuffed Bell Peppers",
            mealType = MealRecipe.TYPE_DINNER,
            description = "Bell peppers filled with rice, meat, and vegetables",
            prepTime = 20,
            cookTime = 40,
            servings = 4,
            difficulty = "medium",
            calories = 395,
            protein = 24f,
            carbs = 48f,
            fat = 12f,
            fiber = 8f,
            ingredients = listOf(
                Ingredient("bell peppers", 4f, "large"),
                Ingredient("ground turkey", 300f, "g"),
                Ingredient("brown rice", 150f, "g"),
                Ingredient("tomato sauce", 200f, "g"),
                Ingredient("onion", 1f, "medium"),
                Ingredient("mozzarella cheese", 80f, "g")
            ),
            steps = listOf(
                "Cook rice according to package instructions",
                "Cut tops off peppers and remove seeds",
                "Brown turkey with diced onion",
                "Mix cooked rice, turkey, and half the tomato sauce",
                "Stuff peppers with mixture and top with remaining sauce",
                "Bake at 180°C for 35 minutes, add cheese last 5 minutes"
            )
        ),
        MealRecipe(
            id = "d6",
            name = "Teriyaki Chicken Bowl",
            mealType = MealRecipe.TYPE_DINNER,
            description = "Sweet and savory chicken with vegetables over rice",
            prepTime = 15,
            cookTime = 20,
            servings = 2,
            difficulty = "easy",
            calories = 525,
            protein = 40f,
            carbs = 64f,
            fat = 12f,
            fiber = 4f,
            ingredients = listOf(
                Ingredient("chicken breast", 300f, "g"),
                Ingredient("white rice", 150f, "g"),
                Ingredient("teriyaki sauce", 4f, "tbsp"),
                Ingredient("broccoli", 150f, "g"),
                Ingredient("carrots", 100f, "g"),
                Ingredient("sesame seeds", 1f, "tsp")
            ),
            steps = listOf(
                "Cook rice according to package instructions",
                "Cut chicken into bite-sized pieces",
                "Stir-fry chicken in a pan until cooked through",
                "Add teriyaki sauce and cook for 2 minutes",
                "Steam broccoli and slice carrots",
                "Serve chicken over rice with vegetables, sprinkle sesame seeds"
            )
        ),
        MealRecipe(
            id = "d7",
            name = "Vegetable Lasagna",
            mealType = MealRecipe.TYPE_DINNER,
            description = "Layered pasta with vegetables and cheese",
            prepTime = 25,
            cookTime = 45,
            servings = 6,
            difficulty = "hard",
            calories = 465,
            protein = 22f,
            carbs = 52f,
            fat = 18f,
            fiber = 8f,
            ingredients = listOf(
                Ingredient("lasagna noodles", 250f, "g"),
                Ingredient("ricotta cheese", 400f, "g"),
                Ingredient("mozzarella cheese", 300f, "g"),
                Ingredient("spinach", 200f, "g"),
                Ingredient("zucchini", 2f, "medium"),
                Ingredient("marinara sauce", 500f, "g")
            ),
            steps = listOf(
                "Cook lasagna noodles according to package",
                "Sauté spinach and sliced zucchini until soft",
                "Mix ricotta with half the mozzarella",
                "Layer sauce, noodles, ricotta mixture, and vegetables",
                "Repeat layers, ending with sauce and mozzarella",
                "Bake at 180°C for 40 minutes until bubbly"
            )
        ),
        MealRecipe(
            id = "d8",
            name = "Grilled Steak with Sweet Potato",
            mealType = MealRecipe.TYPE_DINNER,
            description = "Perfectly grilled steak with roasted sweet potato",
            prepTime = 10,
            cookTime = 30,
            servings = 2,
            difficulty = "medium",
            calories = 620,
            protein = 48f,
            carbs = 42f,
            fat = 28f,
            fiber = 6f,
            ingredients = listOf(
                Ingredient("sirloin steak", 300f, "g"),
                Ingredient("sweet potato", 300f, "g"),
                Ingredient("green beans", 150f, "g"),
                Ingredient("butter", 20f, "g"),
                Ingredient("garlic", 2f, "cloves"),
                Ingredient("rosemary", 1f, "tsp")
            ),
            steps = listOf(
                "Cube sweet potato and roast at 200°C for 25 minutes",
                "Season steak with salt, pepper, and rosemary",
                "Grill steak for 4-5 minutes per side for medium-rare",
                "Let steak rest for 5 minutes before slicing",
                "Sauté green beans with garlic and butter",
                "Serve steak with sweet potato and green beans"
            )
        ),
        MealRecipe(
            id = "d9",
            name = "Shrimp Scampi Pasta",
            mealType = MealRecipe.TYPE_DINNER,
            description = "Garlic butter shrimp with linguine pasta",
            prepTime = 10,
            cookTime = 15,
            servings = 2,
            difficulty = "easy",
            calories = 545,
            protein = 36f,
            carbs = 58f,
            fat = 18f,
            fiber = 3f,
            ingredients = listOf(
                Ingredient("linguine", 200f, "g"),
                Ingredient("shrimp", 300f, "g"),
                Ingredient("butter", 40f, "g"),
                Ingredient("garlic", 4f, "cloves"),
                Ingredient("white wine", 100f, "ml"),
                Ingredient("parsley", 2f, "tbsp")
            ),
            steps = listOf(
                "Cook linguine according to package instructions",
                "Melt butter in a large pan over medium heat",
                "Add minced garlic and cook for 1 minute",
                "Add shrimp and cook until pink, about 4 minutes",
                "Add white wine and simmer for 2 minutes",
                "Toss pasta with shrimp, garnish with parsley"
            )
        ),
        MealRecipe(
            id = "d10",
            name = "Turkey Meatballs with Marinara",
            mealType = MealRecipe.TYPE_DINNER,
            description = "Lean turkey meatballs in tomato sauce",
            prepTime = 20,
            cookTime = 30,
            servings = 4,
            difficulty = "medium",
            calories = 420,
            protein = 36f,
            carbs = 38f,
            fat = 14f,
            fiber = 6f,
            ingredients = listOf(
                Ingredient("ground turkey", 500f, "g"),
                Ingredient("breadcrumbs", 60f, "g"),
                Ingredient("egg", 1f, "whole"),
                Ingredient("marinara sauce", 500f, "g"),
                Ingredient("spaghetti", 250f, "g"),
                Ingredient("parmesan cheese", 40f, "g")
            ),
            steps = listOf(
                "Mix turkey, breadcrumbs, egg, salt, and pepper",
                "Form into golf ball-sized meatballs",
                "Brown meatballs in a pan on all sides",
                "Add marinara sauce and simmer for 20 minutes",
                "Cook spaghetti according to package",
                "Serve meatballs and sauce over pasta with parmesan"
            )
        )
    )

    fun getBreakfastRecipes() = breakfastRecipes
    fun getLunchRecipes() = lunchRecipes
    fun getDinnerRecipes() = dinnerRecipes

    fun getRandomRecipes(mealType: String, count: Int): List<MealRecipe> {
        val recipes = when (mealType) {
            MealRecipe.TYPE_BREAKFAST -> breakfastRecipes
            MealRecipe.TYPE_LUNCH -> lunchRecipes
            MealRecipe.TYPE_DINNER -> dinnerRecipes
            else -> emptyList()
        }
        return recipes.shuffled().take(count)
    }

    fun getAllRecipes() = breakfastRecipes + lunchRecipes + dinnerRecipes

    fun getRecipeById(id: String): MealRecipe? {
        return getAllRecipes().find { it.id == id }
    }
}
