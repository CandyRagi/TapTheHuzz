package com.project.tapthehuzz

import android.content.ComponentName
import android.nfc.NfcAdapter
import android.nfc.cardemulation.CardEmulation
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.project.tapthehuzz.services.MyHostApduService
import com.project.tapthehuzz.userInterface.screens.HistoryScreen
import com.project.tapthehuzz.userInterface.screens.HomeScreen
import com.project.tapthehuzz.userInterface.screens.ProfileScreen
import com.project.tapthehuzz.userInterface.screens.SignInScreen
import com.project.tapthehuzz.userInterface.screens.SignUpScreen
import com.project.tapthehuzz.userInterface.theme.TapTheHuzzTheme

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var cardEmulation: CardEmulation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        cardEmulation = nfcAdapter?.let { CardEmulation.getInstance(it) }

        setContent {
            TapTheHuzzTheme {
                MainApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setPreferredHceService()
    }

    override fun onPause() {
        super.onPause()
        // Unset preferred service when app goes to background
        try {
            cardEmulation?.unsetPreferredService(this)
            Log.d("MainActivity", "Unset preferred HCE service")
        } catch (e: IllegalStateException) {
            Log.e("MainActivity", "Failed to unset preferred HCE service", e)
        }
    }

    // On some OEM skins (notably Samsung/One UI) onResume() can fire before the window
    // actually has focus, which makes setPreferredService() silently no-op. Re-asserting
    // here once focus is confirmed is the more reliable hook for those devices.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setPreferredHceService()
        }
    }

    private fun setPreferredHceService() {
        if (nfcAdapter?.isEnabled != true) return
        try {
            // Set our service as the preferred service when app is in foreground.
            // This only suppresses the "Select an app" chooser while this Activity is
            // resumed and focused - it cannot prevent the chooser when the app isn't in
            // the foreground, since our AID is registered under category="other" (the
            // NDEF tag application ID), which has no persistent system-wide default the
            // way category="payment" does. If another service on the device (e.g. a
            // Samsung system "embedded NFC tag" service) also claims that AID, the OS
            // will always ask while Tapzz isn't in front.
            cardEmulation?.setPreferredService(
                this,
                ComponentName(this, MyHostApduService::class.java)
            )
            Log.d("MainActivity", "Set preferred HCE service")
        } catch (e: IllegalStateException) {
            Log.e("MainActivity", "Failed to set preferred HCE service", e)
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object History : Screen("history", "History", Icons.Filled.History)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    Scaffold(
    ) { innerPadding ->
        val startDestination = "splash"

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                androidx.compose.animation.slideInHorizontally(initialOffsetX = { 1000 }) + androidx.compose.animation.fadeIn()
            },
            exitTransition = {
                androidx.compose.animation.slideOutHorizontally(targetOffsetX = { -1000 }) + androidx.compose.animation.fadeOut()
            },
            popEnterTransition = {
                androidx.compose.animation.slideInHorizontally(initialOffsetX = { -1000 }) + androidx.compose.animation.fadeIn()
            },
            popExitTransition = {
                androidx.compose.animation.slideOutHorizontally(targetOffsetX = { 1000 }) + androidx.compose.animation.fadeOut()
            }
        ) {
            composable("splash") {
                com.project.tapthehuzz.userInterface.screens.SplashScreen(
                    onNavigateToNext = { route ->
                        navController.navigate(route) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }
            composable("signIn") {
                SignInScreen(
                    onNavigateToSignUp = { navController.navigate("signUp") },
                    onSignInSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("signIn") { inclusive = true }
                        }
                    }
                )
            }
            composable("signUp") {
                SignUpScreen(
                    onNavigateToSignIn = { navController.navigate("signIn") },
                    onSignUpSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("signIn") { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                )
            }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onSignOut = {
                        navController.navigate("signIn") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}