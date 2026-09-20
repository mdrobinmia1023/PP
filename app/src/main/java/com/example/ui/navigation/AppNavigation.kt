package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.BookmarksAndNotesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PdfReaderScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StudyScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.MainViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("home", "হোম", Icons.Filled.Home, Icons.Outlined.Home)
    object Library : Screen("library", "লাইব্রেরি", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
    object Study : Screen("study", "স্টাডি", Icons.Filled.LocalFireDepartment, Icons.Outlined.LocalFireDepartment)
    object BookmarksNotes : Screen("bookmarks_notes", "নোটস", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    object Profile : Screen("profile", "প্রোফাইল", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val bottomNavScreens = listOf(
        Screen.Home,
        Screen.Library,
        Screen.Study,
        Screen.BookmarksNotes,
        Screen.Profile
    )

    val showBottomBar = currentDestination in bottomNavScreens.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    tonalElevation = 6.dp
                ) {
                    bottomNavScreens.forEach { screen ->
                        val selected = currentDestination == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.icon else screen.unselectedIcon,
                                    contentDescription = screen.label
                                )
                            },
                            label = {
                                Text(
                                    text = screen.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onOpenBook = { bookId ->
                        navController.navigate("reader/$bookId")
                    },
                    onNavigateToLibrary = { subject ->
                        viewModel.onSubjectSelected(subject)
                        navController.navigate(Screen.Library.route)
                    },
                    onNavigateToStudy = {
                        navController.navigate(Screen.Study.route)
                    },
                    onNavigateToCacheSettings = {
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }

            composable(Screen.Library.route) {
                LibraryScreen(
                    viewModel = viewModel,
                    onOpenBook = { bookId ->
                        navController.navigate("reader/$bookId")
                    }
                )
            }

            composable(Screen.Study.route) {
                StudyScreen(
                    viewModel = viewModel,
                    onNavigateToLibrary = {
                        navController.navigate(Screen.Library.route)
                    }
                )
            }

            composable(Screen.BookmarksNotes.route) {
                BookmarksAndNotesScreen(
                    viewModel = viewModel,
                    onOpenBookAtPage = { bookId, _ ->
                        navController.navigate("reader/$bookId")
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToAdmin = {
                        navController.navigate("admin")
                    }
                )
            }

            composable(
                route = "reader/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                PdfReaderScreen(
                    bookId = bookId,
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("admin") {
                AdminScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
