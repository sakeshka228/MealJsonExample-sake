package com.example.mealjsonexample

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import coil.compose.AsyncImage


@Composable
fun Navigation(
    modifier: Modifier,
    navigationController: NavHostController,
    locationPickerViewModel: LocationPickerViewModel,
    locationViewModel: LocationViewModel
) {
    val viewModel: MealsViewModel = viewModel()
    NavHost(
        modifier = modifier,
        navController = navigationController,
        startDestination = Graph.mainScreen.route
    ) {
        composable(route = Graph.mainScreen.route) {
            MainScreen(viewModel, navigationController, locationViewModel)
        }
        composable(route = Graph.secondScreen.route) {
            SecondScreen(viewModel, navigationController)
        }
        composable(route = "${Graph.mealDetailsScreen.route}/{mealId}") { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId")
            mealId?.let { viewModel.getMealDetails(it) }
            MealDetailsScreen(viewModel) {
                navigationController.popBackStack()
            }
        }
        composable(route = Graph.locationPickerScreen.route) {
            LocationPickerScreen(navigationController, locationPickerViewModel)
        }
    }
}

@Composable
fun SecondScreen(viewModel: MealsViewModel, navigationController: NavHostController) {
    val categoryName = viewModel.chosenCategoryName.collectAsState()
    val dishesState = viewModel.mealsState.collectAsState()
    val searchQuery = viewModel.searchQuery.collectAsState()

    Column {
        TextField(
            value = searchQuery.value,
            onValueChange = { newValue ->
                viewModel.updateSearchQuery(newValue)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = { Text("Поиск блюда:") },
            maxLines = 1
        )

        if (dishesState.value.isLoading) {
            LoadingScreen()
        }

        if (dishesState.value.isError) {
            ErrorScreen(dishesState.value.error!!)
        }

        if (dishesState.value.result.isNotEmpty()) {
            DishesScreen(dishesState.value.result, navigationController)
        }
    }
}

@Composable
fun DishesScreen(result: List<Meal>, navigationController: NavHostController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(result) { meal ->
            DishItem(meal) { mealId ->
                navigationController.navigate("${Graph.mealDetailsScreen.route}/$mealId")
            }
        }
    }
}

@Composable
fun DishItem(meal: Meal, onItemClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .border(2.dp, Color.Red, shape = RoundedCornerShape(8.dp))
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable { onItemClick(meal.idMeal) }
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                modifier = Modifier.height(150.dp),
                model = meal.strMealThumb,
                contentDescription = null
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = meal.mealName
            )
        }
    }
}

@Composable
fun MainScreen(viewModel: MealsViewModel, navigationController: NavHostController, locationViewModel: LocationViewModel){


    val categoriesState = viewModel.categoriesState.collectAsState()

    if (categoriesState.value.isLoading){
        LoadingScreen()
    }
    if (categoriesState.value.isError){
        ErrorScreen(categoriesState.value.error!!)
    }
    if (categoriesState.value.result.isNotEmpty()){
        CategoriesScreen(viewModel, categoriesState.value.result, navigationController, locationViewModel)
    }

}

@Composable
fun CategoriesScreen(viewModel: MealsViewModel, result: List<Category>, navigationController: NavHostController, locationViewModel: LocationViewModel){

    val context = LocalContext.current
    val locationUtils = LocationUtils(context, locationViewModel)
    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
                permission ->
            if (permission[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permission[android.Manifest.permission.ACCESS_FINE_LOCATION] == true){
                locationUtils.getLocation()
            }
            else{
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity, android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        context, android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )){
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                locationPermissionRequest.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
                navigationController.navigate(Graph.locationPickerScreen.route)
            }
        ) {
            Text("pick Location")
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {
            items(result){
                CategoryItem(viewModel, it, navigationController)
            }
        }
    }
}

@Composable
fun CategoryItem(viewModel: MealsViewModel, category: Category, navigationController: NavHostController) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .border(2.dp, Color.Red, shape = RoundedCornerShape(8.dp)) // Добавляем границу
            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)

            .clickable {
                viewModel.setChosenCategory(category.strCategory)
                navigationController.navigate("${Graph.secondScreen.route}")
            }
    ){
       Column(
           modifier = Modifier.fillMaxSize(),
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.Center
       ) {
           AsyncImage(
               model = category.strCategoryThumb,
               contentDescription = null
           )
           Spacer(Modifier.height(5.dp))
           Text(
               text = category.strCategory
           )
       }
    }
}

@Composable
fun ErrorScreen(error: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error
        )
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun DishScreen(
    viewModel: MealsViewModel
) {
    val mealState = viewModel.mealsState.collectAsState()

    val mealName = viewModel.chosenMealName.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.searchMealByName(mealName.value)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (mealState.value.isLoading) {
            LoadingScreen()
        }
        else if (mealState.value.isError) {
            ErrorScreen(mealState.value.error.toString())
        }
        else if (mealState.value.result.isNotEmpty()) {
            AsyncImage(
                model = mealState.value.result[0].strMealThumb,
                contentDescription = mealState.value.result[0].mealName
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = mealState.value.result[0].mealName,
                textAlign = TextAlign.Center
            )
        }
        else {
            Text("No meals found.")
        }
    }
}
@Composable
fun MealDetailsScreen(viewModel: MealsViewModel, onBackClick: () -> Unit) {
    val mealDetails by viewModel.selectedMealDetails.collectAsState()

    mealDetails?.let { details ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            AsyncImage(
                model = details.strMealThumb,
                contentDescription = details.strMeal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Text(
                text = details.strMeal,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "Category: ${details.strCategory}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Area: ${details.strArea}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Instructions:",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Text(
                text = details.strInstructions,
                style = MaterialTheme.typography.bodyLarge
            )

            // Добавьте список ингредиентов и мер

            Button(
                onClick = onBackClick,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Back")
            }
        }
    } ?: run {
        // Показать загрузку или сообщение об ошибке
    }
}
