package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.AddPartyDialog
import com.example.ui.theme.BankCardNavy
import com.example.ui.theme.BkashPink
import com.example.ui.theme.CalendarBlue
import com.example.ui.theme.DenaRed
import com.example.ui.theme.DenaRedLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.MapsRed
import com.example.ui.theme.MessengerBlue
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.PaonaGreen
import com.example.ui.theme.PaonaGreenLight
import com.example.ui.theme.RocketPurple
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.WhatsAppGreen
import com.example.utils.FormatUtils
import com.example.utils.IntentHelper
import com.example.viewmodel.AssistantViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: AssistantViewModel,
    onNavigateToCustomers: () -> Unit,
    onNavigateToSuppliers: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToAdmin: () -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val activeUser by viewModel.activeUser.collectAsState()
    val totalPaona by viewModel.totalPaona.collectAsState()
    val totalDena by viewModel.totalDena.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()

    var showAddPartyDialog by remember { mutableStateOf(false) }
    var showBankModal by remember { mutableStateOf(false) }
    var showCameraModal by remember { mutableStateOf(false) }
    var showLogoPickerModal by remember { mutableStateOf(false) }

    // Image Picker Launcher for Logo
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateLogo(it.toString())
            Toast.makeText(context, "লোগো সফলভাবে আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // --- TOP BANNER (App Name & Logo space) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(EmeraldPrimary, Color(0xFF064E3B))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeUser?.companyName ?: "S.R Enterprise",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(
                            onClick = {
                                viewModel.logout()
                                onLogout()
                            }
                        ) {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = "লগআউট",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // বড় করে অ্যাপসের নাম
                    Text(
                        text = "S.R Assistant",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // লেখার মাঝ বরাবর নিচে অ্যাপসের লোগো আপলোডের জায়গা (ফাঁকা / আপলোড সুবিধা)
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(2.5.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                            .clickable {
                                showLogoPickerModal = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!activeUser?.logoUri.isNullOrBlank()) {
                            AsyncImage(
                                model = activeUser?.logoUri,
                                contentDescription = "অ্যাপস লোগো",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "লোগো আপলোড",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "লোগো দিন",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Small edit badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(EmeraldSecondary)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = activeUser?.nameBn ?: "প্রোপ্রাইটার",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // --- ADMIN PANEL CARD (Visible ONLY for 01871752386 / Admin) ---
            if (activeUser?.isAdmin == true) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { onNavigateToAdmin() }
                        .testTag("admin_panel_entry_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BankCardNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
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
                                    .background(Color(0xFFFFD700)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin",
                                    tint = Color.Black,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "এডমিন কন্ট্রোল প্যানেল",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFFFD700),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "01871752386",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "সকল ব্যবহারকারীর তালিকা ও ব্যবহারের ইতিহাস দেখুন",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToAdmin,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = "প্রবেশ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- মোট দেনা ও মোট পাওনা হিসাব ---
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "টালি সারসংক্ষেপ (ব্যবসায়িক হিসাব)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // মোট দেনা কার্ড (সাপ্লায়ারদের কাছে)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToSuppliers() }
                            .testTag("total_dena_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DenaRedLight)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "মোট দেনা",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DenaRed
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(DenaRed.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = DenaRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = FormatUtils.formatCurrency(totalDena),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DenaRed
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${FormatUtils.toBanglaNumber(suppliers.size)} জন মহাজন/সাপ্লায়ার",
                                fontSize = 11.sp,
                                color = DenaRed.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // মোট পাওনা কার্ড (কাস্টমারদের কাছে)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToCustomers() }
                            .testTag("total_paona_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PaonaGreenLight)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "মোট পাওনা",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaonaGreen
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(PaonaGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = PaonaGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = FormatUtils.formatCurrency(totalPaona),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PaonaGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${FormatUtils.toBanglaNumber(customers.size)} জন খরিদ্দার/কাস্টমার",
                                fontSize = 11.sp,
                                color = PaonaGreen.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- নতুন কাস্টমার / সাপ্লায়ার এড করার অপশন ---
                Button(
                    onClick = { showAddPartyDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("add_party_main_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "+ নতুন কাস্টমার / সাপ্লায়ার যোগ করুন",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- সরাসরি কানেক্টেড ১০টি প্রয়োজনীয় অ্যাপস ---
                Text(
                    text = "সরাসরি কানেক্টেড অ্যাপস ও সেবা সমূহ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "এক ট্যাপেই সংশ্লিষ্ট অ্যাপ ওপেন করে লেনদেন ও যোগাযোগ করুন",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 10 Integration Tiles in 2-column or Flow Grid
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    maxItemsInEachRow = 2
                ) {
                    // 1. bKash
                    AppIntegrationTile(
                        title = "bKash (বিকাশ)",
                        subtitle = "পেমেন্ট ও ডায়াল *247#",
                        icon = Icons.Default.AccountBalance,
                        brandColor = BkashPink,
                        modifier = Modifier.weight(1f),
                        onClick = { IntentHelper.openBkash(context) }
                    )

                    // 2. Nagad
                    AppIntegrationTile(
                        title = "Nagad (নগদ)",
                        subtitle = "পেমেন্ট ও ডায়াল *167#",
                        icon = Icons.Default.AccountBalance,
                        brandColor = NagadOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { IntentHelper.openNagad(context) }
                    )

                    // 3. Rocket
                    AppIntegrationTile(
                        title = "Rocket (রকেট)",
                        subtitle = "পেমেন্ট ও ডায়াল *322#",
                        icon = Icons.Default.AccountBalance,
                        brandColor = RocketPurple,
                        modifier = Modifier.weight(1f),
                        onClick = { IntentHelper.openRocket(context) }
                    )

                    // 4. Bank Card
                    AppIntegrationTile(
                        title = "Bank Card",
                        subtitle = "কার্ড ও ব্যাংক ট্রান্সফার",
                        icon = Icons.Default.CreditCard,
                        brandColor = BankCardNavy,
                        modifier = Modifier.weight(1f),
                        onClick = { showBankModal = true }
                    )

                    // 5. WhatsApp
                    AppIntegrationTile(
                        title = "WhatsApp",
                        subtitle = "সরাসরি ইনভয়েস ও চ্যাট",
                        icon = Icons.Default.Chat,
                        brandColor = WhatsAppGreen,
                        modifier = Modifier.weight(1f),
                        onClick = { IntentHelper.openWhatsApp(context, "", "আসসালামু আলাইকুম, S.R Assistant থেকে আপনার হিসাবের তথ্য শেয়ার করা হচ্ছে।") }
                    )

                    // 6. Messenger
                    AppIntegrationTile(
                        title = "Messenger",
                        subtitle = "ফেসবুক মেসেঞ্জার চ্যাট",
                        icon = Icons.Default.Send,
                        brandColor = MessengerBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { IntentHelper.openMessenger(context, "") }
                    )

                    // 7. Telegram
                    AppIntegrationTile(
                        title = "Telegram",
                        subtitle = "টেলিগ্রাম বোট ও চ্যাট",
                        icon = Icons.Default.Share,
                        brandColor = TelegramBlue,
                        modifier = Modifier.weight(1f),
                        onClick = { IntentHelper.openTelegram(context, "") }
                    )

                    // 8. Google Maps
                    AppIntegrationTile(
                        title = "Google Maps",
                        subtitle = "কাস্টমার ঠিকানা ও রুট",
                        icon = Icons.Default.LocationOn,
                        brandColor = MapsRed,
                        modifier = Modifier.weight(1f),
                        onClick = { IntentHelper.openGoogleMaps(context, "Dhaka, Bangladesh") }
                    )

                    // 9. Calendar
                    AppIntegrationTile(
                        title = "Calender",
                        subtitle = "তাগাদা ও ডেলিভারি শিডিউল",
                        icon = Icons.Default.CalendarMonth,
                        brandColor = CalendarBlue,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            IntentHelper.addToCalendar(
                                context,
                                "S.R Assistant - দেনা/পাওনা তাগাদা",
                                "কাস্টমার/সাপ্লায়ার হিসাব ও পেমেন্ট রিমাইন্ডার",
                                System.currentTimeMillis() + 86400000L
                            )
                        }
                    )

                    // 10. Camera
                    AppIntegrationTile(
                        title = "Camera",
                        subtitle = "ভাউচার ও পণ্যের ছবি",
                        icon = Icons.Default.CameraAlt,
                        brandColor = EmeraldPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { showCameraModal = true }
                    )
                }
            }
        }

        // --- Modals & Dialogs ---
        if (showAddPartyDialog) {
            AddPartyDialog(
                onDismiss = { showAddPartyDialog = false },
                onConfirm = { type, name, company, phone, address, initBal, note ->
                    viewModel.addParty(
                        type = type,
                        name = name,
                        companyName = company,
                        phone = phone,
                        address = address,
                        initialBalance = initBal,
                        initialNote = note
                    ) {
                        showAddPartyDialog = false
                        Toast.makeText(context, "সফলভাবে যোগ করা হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Logo Upload / Picker Modal
        if (showLogoPickerModal) {
            AlertDialog(
                onDismissRequest = { showLogoPickerModal = false },
                title = { Text("অ্যাপসের লোগো সেট করুন", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("গ্যালারি থেকে আপনার ব্যবসা বা প্রতিষ্ঠানের লোগো নির্বাচন করুন:", fontSize = 13.sp)
                        Button(
                            onClick = {
                                showLogoPickerModal = false
                                imagePickerLauncher.launch("image/*")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("গ্যালারি / ক্যামেরা থেকে নির্বাচন")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLogoPickerModal = false }) {
                        Text("বন্ধ করুন")
                    }
                }
            )
        }

        // Bank Card Modal
        if (showBankModal) {
            AlertDialog(
                onDismissRequest = { showBankModal = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = BankCardNavy)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ব্যাংক কার্ড ও পেমেন্ট সার্ভিস", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("যেকোনো ভিসা, মাস্টারকার্ড বা ব্যাংক একাউন্ট থেকে লেনদেনের বিবরণী ও রসিদ সংরক্ষণ করুন।", fontSize = 13.sp)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("💳 ব্যাংক কার্ড পেমেন্ট স্লিপ:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("১. কাস্টমারদের POS কার্ড পেমেন্ট\n২. সাপ্লায়ারের ব্যাংক একাউন্ট ফান্ড ট্রান্সফার\n৩. অনলাইন পেমেন্ট লিংক শেয়ারিং", fontSize = 12.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showBankModal = false
                            Toast.makeText(context, "ব্যাংক সার্ভিস প্রস্তুত", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BankCardNavy)
                    ) {
                        Text("ঠিক আছে")
                    }
                }
            )
        }

        // Camera / Voucher Modal
        if (showCameraModal) {
            AlertDialog(
                onDismissRequest = { showCameraModal = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ক্যামেরা ও ভাউচার স্ক্যানার", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("চালান রশিদ, ক্যাশ মেমো অথবা নতুন প্রোডাক্টের ছবি সরাসরি যুক্ত করতে পারেন।", fontSize = 13.sp)
                        Button(
                            onClick = {
                                showCameraModal = false
                                imagePickerLauncher.launch("image/*")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text("ছবি নির্বাচন করুন")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCameraModal = false }) {
                        Text("বন্ধ করুন")
                    }
                }
            )
        }
    }
}

@Composable
fun AppIntegrationTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    brandColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("app_tile_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(brandColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
