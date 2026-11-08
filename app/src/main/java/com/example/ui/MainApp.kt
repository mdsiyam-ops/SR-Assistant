package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.CustomerLedgerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OrderScreen
import com.example.ui.screens.RegistrationScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.screens.SupplierLedgerScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.viewmodel.AssistantViewModel

sealed class NavTab(val index: Int, val title: String, val icon: ImageVector) {
    object Home : NavTab(0, "হোম", Icons.Default.Home)
    object Dashboard : NavTab(1, "ড্যাশবোর্ড", Icons.Default.Dashboard)
    object Customers : NavTab(2, "কাস্টমার", Icons.Default.People)
    object Suppliers : NavTab(3, "সাপ্লায়ার", Icons.Default.Store)
    object Orders : NavTab(4, "অর্ডার", Icons.Default.ShoppingCart)
    object Reports : NavTab(5, "রিপোর্ট", Icons.Default.Assessment)
}

@Composable
fun MainApp(viewModel: AssistantViewModel) {
    val activeUser by viewModel.activeUser.collectAsState()
    var isRegistering by remember { mutableStateOf(false) }
    var currentTabIndex by remember { mutableIntStateOf(0) }
    var showAdminPanel by remember { mutableStateOf(false) }

    if (activeUser == null) {
        if (isRegistering) {
            RegistrationScreen(
                viewModel = viewModel,
                onNavigateToLogin = { isRegistering = false },
                onRegisterSuccess = { isRegistering = false }
            )
        } else {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { isRegistering = true },
                onLoginSuccess = { isRegistering = false }
            )
        }
    } else if (showAdminPanel && activeUser?.isAdmin == true) {
        BackHandler {
            showAdminPanel = false
        }
        AdminPanelScreen(
            viewModel = viewModel,
            onBack = { showAdminPanel = false }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    val tabs = listOf(
                        NavTab.Home,
                        NavTab.Dashboard,
                        NavTab.Customers,
                        NavTab.Suppliers,
                        NavTab.Orders,
                        NavTab.Reports
                    )

                    tabs.forEach { tab ->
                        val isSelected = currentTabIndex == tab.index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTabIndex = tab.index },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 9.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = EmeraldSecondary.copy(alpha = 0.2f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.title.lowercase()}")
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTabIndex) {
                    0 -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToCustomers = { currentTabIndex = 2 },
                        onNavigateToSuppliers = { currentTabIndex = 3 },
                        onNavigateToOrders = { currentTabIndex = 4 },
                        onNavigateToAdmin = { showAdminPanel = true },
                        onLogout = {
                            isRegistering = false
                            showAdminPanel = false
                        }
                    )
                    1 -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToCustomers = { currentTabIndex = 2 },
                        onNavigateToSuppliers = { currentTabIndex = 3 }
                    )
                    2 -> CustomerLedgerScreen(viewModel = viewModel)
                    3 -> SupplierLedgerScreen(viewModel = viewModel)
                    4 -> OrderScreen(viewModel = viewModel)
                    5 -> ReportScreen(viewModel = viewModel)
                }
            }
        }
    }
}
