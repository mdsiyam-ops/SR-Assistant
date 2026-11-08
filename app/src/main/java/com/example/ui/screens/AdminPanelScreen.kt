package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.LedgerTransaction
import com.example.data.model.OrderEntity
import com.example.data.model.Party
import com.example.data.model.Product
import com.example.data.model.UserAccount
import com.example.data.repository.AssistantRepository
import com.example.ui.theme.BankCardNavy
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DenaRed
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.PaonaGreen
import com.example.ui.theme.PaonaGreenLight
import com.example.ui.theme.WhatsAppGreen
import com.example.utils.FormatUtils
import com.example.utils.IntentHelper
import com.example.viewmodel.AssistantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: AssistantViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val allUsers by viewModel.allUsers.collectAsState()
    val userCount by viewModel.userCount.collectAsState()
    val allCustomers by viewModel.customers.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedUserForDetail by remember { mutableStateOf<UserAccount?>(null) }
    var userToResetPassword by remember { mutableStateOf<UserAccount?>(null) }

    val filteredUsers = remember(allUsers, searchQuery) {
        if (searchQuery.isBlank()) {
            allUsers
        } else {
            val q = searchQuery.trim().lowercase()
            allUsers.filter {
                it.nameBn.lowercase().contains(q) ||
                it.nameEn.lowercase().contains(q) ||
                it.phone.contains(q) ||
                it.companyName.lowercase().contains(q)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "এডমিন কন্ট্রোল প্যানেল",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFFFFD700),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "সুপার এডমিন",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "অ্যাডমিন নম্বর: 01871752386 | মোট ইউজার: $userCount জন",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BankCardNavy
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Overview Metric Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "মোট ব্যবহারকারী",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$userCount জন",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = BankCardNavy.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = BankCardNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "মোট লেনদেন রেকর্ড",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${allTransactions.size} টি",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BankCardNavy
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ব্যবহারকারীর নাম, ফোন নম্বর বা প্রতিষ্ঠান দিয়ে খুঁজুন...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_user_search_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // User List Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "রেজিস্টার্ড ব্যবহারকারীদের তালিকা (${filteredUsers.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ক্লিক করে সম্পূর্ণ হিসাব দেখুন",
                        fontSize = 11.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredUsers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কোন ব্যবহারকারী পাওয়া যায়নি",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredUsers, key = { it.id }) { user ->
                            AdminUserCard(
                                user = user,
                                onViewDetail = { selectedUserForDetail = user },
                                onResetPassword = { userToResetPassword = user },
                                onCall = { IntentHelper.makePhoneCall(context, user.phone) },
                                onWhatsApp = {
                                    IntentHelper.openWhatsApp(
                                        context,
                                        user.phone,
                                        "আসসালামু আলাইকুম ${user.nameBn},\nS.R Assistant এডমিন থেকে যোগাযোগ করা হচ্ছে।"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog: User Full Detail and Business Ledger History
    selectedUserForDetail?.let { user ->
        UserFullLedgerDialog(
            user = user,
            viewModel = viewModel,
            onDismiss = { selectedUserForDetail = null },
            onResetPassword = {
                userToResetPassword = user
            }
        )
    }

    // Dialog: Reset Password
    userToResetPassword?.let { user ->
        AdminResetPasswordDialog(
            user = user,
            onDismiss = { userToResetPassword = null },
            onConfirm = { newPass ->
                viewModel.adminResetPassword(user.id, newPass) {
                    Toast.makeText(context, "${user.nameBn}-এর পাসওয়ার্ড সফলভাবে পরিবর্তন হয়েছে", Toast.LENGTH_SHORT).show()
                    userToResetPassword = null
                }
            }
        )
    }
}

@Composable
fun AdminUserCard(
    user: UserAccount,
    onViewDetail: () -> Unit,
    onResetPassword: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetail() }
            .testTag("admin_user_card_${user.phone}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (user.isAdmin) BankCardNavy else EmeraldPrimary
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.nameBn.take(1).ifBlank { "U" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.nameBn,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (user.isAdmin) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = BankCardNavy,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "এডমিন",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        if (user.companyName.isNotBlank()) {
                            Text(
                                text = user.companyName,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Phone Contact Buttons
                Row {
                    IconButton(
                        onClick = onCall,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Call",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onWhatsApp,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            Icons.Default.Message,
                            contentDescription = "WhatsApp",
                            tint = WhatsAppGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ফোন: ${user.phone}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "নিবন্ধনের তারিখ: ${FormatUtils.formatDate(user.createdAt)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "পাসওয়ার্ড: ${user.password}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onViewDetail,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "সমস্ত হিসাব দেখুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onResetPassword,
                    modifier = Modifier.height(38.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "পাসওয়ার্ড", fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserFullLedgerDialog(
    user: UserAccount,
    viewModel: AssistantViewModel,
    onDismiss: () -> Unit,
    onResetPassword: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val userParties by viewModel.getPartiesByUser(user.id).collectAsState(initial = emptyList())
    val userTransactions by viewModel.getTransactionsByUser(user.id).collectAsState(initial = emptyList())
    val userProducts by viewModel.getProductsByUser(user.id).collectAsState(initial = emptyList())
    val userOrders by viewModel.getOrdersByUser(user.id).collectAsState(initial = emptyList())

    val customers = remember(userParties) { userParties.filter { it.type == "CUSTOMER" } }
    val suppliers = remember(userParties) { userParties.filter { it.type == "SUPPLIER" } }

    val totalGiven = remember(userTransactions) {
        userTransactions.filter { it.type == "GIVEN" }.sumOf { it.amount }
    }
    val totalReceived = remember(userTransactions) {
        userTransactions.filter { it.type == "RECEIVED" }.sumOf { it.amount }
    }
    val totalCustomerDue = (totalGiven - totalReceived).coerceAtLeast(0.0)

    val totalPurchase = remember(userTransactions) {
        userTransactions.filter { it.type == "PURCHASE" }.sumOf { it.amount }
    }
    val totalPayment = remember(userTransactions) {
        userTransactions.filter { it.type == "PAYMENT" }.sumOf { it.amount }
    }
    val totalReturn = remember(userTransactions) {
        userTransactions.filter { it.type == "RETURN" }.sumOf { it.amount }
    }
    val totalSupplierDena = (totalPurchase - totalPayment - totalReturn).coerceAtLeast(0.0)

    var currentDetailTab by remember { mutableIntStateOf(0) } // 0: সারসংক্ষেপ, 1: লেনদেন, 2: কাস্টমার, 3: সাপ্লায়ার, 4: অর্ডার/পণ্য

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${user.nameBn}-এর সমস্ত হিসাব",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ফোন: ${user.phone} | প্রতিষ্ঠান: ${user.companyName.ifBlank { "ব্যক্তিগত" }}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detail Tabs
                ScrollableTabRow(
                    selectedTabIndex = currentDetailTab,
                    edgePadding = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    val tabs = listOf(
                        "সারসংক্ষেপ",
                        "লেনদেন (${userTransactions.size})",
                        "কাস্টমার (${customers.size})",
                        "সাপ্লায়ার (${suppliers.size})",
                        "অর্ডার ও স্টক"
                    )
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = currentDetailTab == index,
                            onClick = { currentDetailTab = index },
                            text = { Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    when (currentDetailTab) {
                        0 -> {
                            // Summary
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Balance Overview
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "হিসাব সারসংক্ষেপ",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = EmeraldPrimary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("মোট কাস্টমার পাওনা বাকি:", fontSize = 13.sp)
                                            Text(
                                                FormatUtils.formatCurrency(totalCustomerDue),
                                                fontWeight = FontWeight.Bold,
                                                color = PaonaGreen
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("মোট সাপ্লায়ার দেনা হিসাব:", fontSize = 13.sp)
                                            Text(
                                                FormatUtils.formatCurrency(totalSupplierDena),
                                                fontWeight = FontWeight.Bold,
                                                color = DenaRed
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("মোট লেনদেন সংখ্যা:", fontSize = 13.sp)
                                            Text("${userTransactions.size} টি", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // User Account Info Card
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text("ইউজার প্রোফাইল তথ্য", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("নাম (বাংলা): ${user.nameBn}", fontSize = 12.sp)
                                        Text("নাম (ইংরেজি): ${user.nameEn}", fontSize = 12.sp)
                                        Text("মোবাইল নম্বর: ${user.phone}", fontSize = 12.sp)
                                        Text("ইমেইল: ${user.email.ifBlank { "দেওয়া নেই" }}", fontSize = 12.sp)
                                        Text("প্রতিষ্ঠান: ${user.companyName.ifBlank { "ব্যক্তিগত" }}", fontSize = 12.sp)
                                        Text("বর্তমান পাসওয়ার্ড: ${user.password}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("অ্যাকাউন্ট তৈরির তারিখ: ${FormatUtils.formatDate(user.createdAt)}", fontSize = 12.sp)
                                    }
                                }

                                // Direct Assistance Message Generation
                                Button(
                                    onClick = {
                                        val memo = buildString {
                                            appendLine("=== S.R Assistant একাউন্ট বিবরণী ===")
                                            appendLine("গ্রাহক: ${user.nameBn}")
                                            appendLine("প্রতিষ্ঠান: ${user.companyName}")
                                            appendLine("মোবাইল: ${user.phone}")
                                            appendLine("তারিখ: ${FormatUtils.formatDate(System.currentTimeMillis())}")
                                            appendLine("--------------------------------")
                                            appendLine("মোট কাস্টমার পাওনা: ${FormatUtils.formatCurrency(totalCustomerDue)}")
                                            appendLine("মোট সাপ্লায়ার দেনা: ${FormatUtils.formatCurrency(totalSupplierDena)}")
                                            appendLine("মোট লেনদেন সংখ্যা: ${userTransactions.size} টি")
                                            appendLine("মোট রেজিস্টার্ড কাস্টমার: ${customers.size} জন")
                                            appendLine("মোট রেজিস্টার্ড সাপ্লায়ার: ${suppliers.size} জন")
                                            appendLine("--------------------------------")
                                            appendLine("আপনার একাউন্টের যেকোনো সহায়তায় যোগাযোগ করুন: 01871752386")
                                        }
                                        IntentHelper.openWhatsApp(context, user.phone, memo)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("WhatsApp এ সম্পূর্ণ হিসাব বিবরণী পাঠান", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val memo = "S.R Assistant হিসাব: আপনার মোট পাওনা ${FormatUtils.formatCurrency(totalCustomerDue)}, মোট দেনা ${FormatUtils.formatCurrency(totalSupplierDena)}। লগইন ফোন: ${user.phone}।"
                                        IntentHelper.sendSms(context, user.phone, memo)
                                    },
                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("SMS এ হিসাব পাঠান", fontSize = 13.sp)
                                }
                            }
                        }

                        1 -> {
                            // Transactions List
                            if (userTransactions.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("এই ব্যবহারকারীর কোনো লেনদেন রেকর্ড পাওয়া যায়নি", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(userTransactions) { txn ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    val typeName = when (txn.type) {
                                                        "GIVEN" -> "মাল দিলাম (বাকি)"
                                                        "RECEIVED" -> "টাকা পেলাম (জমা)"
                                                        "PURCHASE" -> "পণ্য ক্রয় (দেনা)"
                                                        "PAYMENT" -> "পরিশোধ"
                                                        "RETURN" -> "পণ্য ফেরত"
                                                        else -> txn.type
                                                    }
                                                    Text(text = typeName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    if (txn.description.isNotBlank()) {
                                                        Text(text = txn.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                    Text(text = FormatUtils.formatDate(txn.date), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }

                                                Text(
                                                    text = FormatUtils.formatCurrency(txn.amount),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = if (txn.type in listOf("GIVEN", "PURCHASE")) DenaRed else PaonaGreen
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // Customers
                            if (customers.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("কোনো কাস্টমার সংরক্ষিত নেই", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(customers) { c ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(c.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text("ফোন: ${c.phone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    if (!c.companyName.isNullOrBlank()) Text(c.companyName, fontSize = 11.sp)
                                                }
                                                IconButton(onClick = { IntentHelper.makePhoneCall(context, c.phone) }) {
                                                    Icon(Icons.Default.Call, contentDescription = "Call", tint = EmeraldPrimary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            // Suppliers
                            if (suppliers.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("কোনো সাপ্লায়ার সংরক্ষিত নেই", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(suppliers) { s ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(s.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text("ফোন: ${s.phone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    if (!s.companyName.isNullOrBlank()) Text(s.companyName, fontSize = 11.sp)
                                                }
                                                IconButton(onClick = { IntentHelper.makePhoneCall(context, s.phone) }) {
                                                    Icon(Icons.Default.Call, contentDescription = "Call", tint = EmeraldPrimary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        4 -> {
                            // Products & Orders
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("অর্ডারসমূহ (${userOrders.size} টি)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (userOrders.isEmpty()) {
                                    Text("কোনো অর্ডার নেই", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    userOrders.forEach { ord ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(ord.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text(FormatUtils.formatCurrency(ord.totalAmount), fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                                }
                                                Text("স্ট্যাটাস: ${ord.status}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("পণ্য ও স্টক (${userProducts.size} টি)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (userProducts.isEmpty()) {
                                    Text("কোনো পণ্য যুক্ত করা নেই", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                } else {
                                    userProducts.forEach { p ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text(p.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Text("মূল্য: ${FormatUtils.formatCurrency(p.price)} / ${p.unit}", fontSize = 11.sp)
                                                }
                                                Text("স্টক: ${p.stock} ${p.unit}", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Action: Close Dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("বন্ধ করুন", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminResetPasswordDialog(
    user: UserAccount,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Key, contentDescription = null, tint = EmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "পাসওয়ার্ড রিসেট করুন", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "ব্যবহারকারী: ${user.nameBn} (${user.phone})",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "বর্তমান পাসওয়ার্ড: ${user.password}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldPrimary
                )

                if (errorText != null) {
                    Text(text = errorText ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("নতুন পাসওয়ার্ড দিন") },
                    placeholder = { Text("যেমন: 123456") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_new_password_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.isBlank()) {
                        errorText = "দয়া করে নতুন পাসওয়ার্ড লিখুন"
                    } else {
                        onConfirm(newPassword.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("admin_confirm_reset_password_button")
            ) {
                Text("রিসেট সম্পন্ন করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
