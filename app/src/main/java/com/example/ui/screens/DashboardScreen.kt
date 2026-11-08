package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LedgerTransaction
import com.example.data.model.Party
import com.example.data.model.Product
import com.example.ui.components.AddProductDialog
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.DenaRed
import com.example.ui.theme.DenaRedLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.MessengerBlue
import com.example.ui.theme.PaonaGreen
import com.example.ui.theme.PaonaGreenLight
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.WhatsAppGreen
import com.example.utils.FormatUtils
import com.example.utils.IntentHelper
import com.example.viewmodel.AssistantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AssistantViewModel,
    onNavigateToCustomers: () -> Unit,
    onNavigateToSuppliers: () -> Unit
) {
    val context = LocalContext.current
    val customers by viewModel.customers.collectAsState()
    val suppliers by viewModel.suppliers.collectAsState()
    val totalPaona by viewModel.totalPaona.collectAsState()
    val totalDena by viewModel.totalDena.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val products by viewModel.products.collectAsState()

    var showAddProductDialog by remember { mutableStateOf(false) }
    var showSmsSettingsDialog by remember { mutableStateOf(false) }
    var selectedDateFilter by remember { mutableStateOf("ALL") } // "ALL", "TODAY", "YESTERDAY"

    val filteredTransactions = remember(allTransactions, selectedDateFilter) {
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L
        when (selectedDateFilter) {
            "TODAY" -> {
                val todayStr = FormatUtils.formatDateNumeric(now)
                allTransactions.filter { FormatUtils.formatDateNumeric(it.date) == todayStr }
            }
            "YESTERDAY" -> {
                val yesterdayStr = FormatUtils.formatDateNumeric(now - oneDayMs)
                allTransactions.filter { FormatUtils.formatDateNumeric(it.date) == yesterdayStr }
            }
            else -> allTransactions
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "১। ড্যাশবোর্ড ও ওভারভিউ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
                Text(
                    text = "কাস্টমার, সাপ্লায়ার হিসাব ও পণ্য ব্যবস্থাপনা",
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Customer count vs Supplier count cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Customers Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToCustomers() }
                            .testTag("dashboard_customer_stat_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(PaonaGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.People, contentDescription = null, tint = PaonaGreen, modifier = Modifier.size(18.dp))
                                }
                                Text(
                                    text = "কাস্টমার",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PaonaGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${FormatUtils.toBanglaNumber(customers.size)} জন",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "পাওনা: ${FormatUtils.formatCurrency(totalPaona)}",
                                fontSize = 11.5.sp,
                                color = PaonaGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Total Suppliers Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToSuppliers() }
                            .testTag("dashboard_supplier_stat_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(DenaRed.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Store, contentDescription = null, tint = DenaRed, modifier = Modifier.size(18.dp))
                                }
                                Text(
                                    text = "সাপ্লায়ার",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DenaRed
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${FormatUtils.toBanglaNumber(suppliers.size)} জন",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "দেনা: ${FormatUtils.formatCurrency(totalDena)}",
                                fontSize = 11.5.sp,
                                color = DenaRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Quick Actions: Product Add & SMS Send Option
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Option 1: Product Add
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showAddProductDialog = true }
                            .testTag("dashboard_product_add_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Product Add", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.5.sp)
                                Text("প্রোডাক্ট যোগ করুন", color = Color.White.copy(alpha = 0.85f), fontSize = 10.5.sp)
                            }
                        }
                    }

                    // Option 2: SMS Send Option
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showSmsSettingsDialog = true }
                            .testTag("dashboard_sms_send_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = BlueAccent)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Message, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("SMS Send", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.5.sp)
                                Text("হিসাব পাঠান মেসেজে", color = Color.White.copy(alpha = 0.85f), fontSize = 10.5.sp)
                            }
                        }
                    }
                }
            }

            // Products Quick List preview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "প্রোডাক্ট তালিকা (${FormatUtils.toBanglaNumber(products.size)} টি)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            TextButton(onClick = { showAddProductDialog = true }) {
                                Text("+ নতুন প্রোডাক্ট", color = EmeraldPrimary, fontSize = 12.sp)
                            }
                        }

                        if (products.isEmpty()) {
                            Text(
                                text = "কোন প্রোডাক্ট যোগ করা হয়নি। '+ নতুন প্রোডাক্ট' এ ক্লিক করে যোগ করুন।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                products.take(4).forEach { p ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(p.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            Text("স্টক: ${FormatUtils.toBanglaNumber(p.stock)} ${p.unit}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(
                                            FormatUtils.formatCurrency(p.price),
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Date-wise Transaction History
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "তারিখ অনুযায়ী দেনা/পাওনা লেনদেন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedDateFilter == "ALL",
                        onClick = { selectedDateFilter = "ALL" },
                        label = { Text("সব হিসাব", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedDateFilter == "TODAY",
                        onClick = { selectedDateFilter = "TODAY" },
                        label = { Text("আজকের হিসাব", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedDateFilter == "YESTERDAY",
                        onClick = { selectedDateFilter = "YESTERDAY" },
                        label = { Text("গতকালের হিসাব", fontSize = 12.sp) }
                    )
                }
            }

            if (filteredTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("এই তারিখে কোন দেনা/পাওনা লেনদেন পাওয়া যায়নি।", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(filteredTransactions) { txn ->
                    val isCredit = txn.type == "RECEIVED" || txn.type == "PAYMENT" || txn.type == "RETURN"
                    val isCustomer = txn.partyType == "CUSTOMER"
                    val typeLabel = when (txn.type) {
                        "GIVEN" -> "মাল দিলাম (পাওনা বৃদ্ধি)"
                        "RECEIVED" -> "টাকা পেলাম (জমা)"
                        "PURCHASE" -> "পণ্য নিলাম (দেনা বৃদ্ধি)"
                        "PAYMENT" -> "পরিশোধ করলাম"
                        "RETURN" -> "পণ্য ফেরত দিলাম"
                        else -> txn.type
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isCredit) PaonaGreen.copy(alpha = 0.15f) else DenaRed.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = if (isCredit) PaonaGreen else DenaRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = typeLabel,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isCredit) PaonaGreen else DenaRed
                                    )
                                    Text(
                                        text = if (txn.description.isNotBlank()) txn.description else (if (isCustomer) "কাস্টমার হিসাব" else "সাপ্লায়ার হিসাব"),
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "তারিখ: ${FormatUtils.formatDate(txn.date)}",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Text(
                                text = (if (isCredit) "- " else "+ ") + FormatUtils.formatCurrency(txn.amount),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = if (isCredit) PaonaGreen else DenaRed
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }

        // Add Product Dialog
        if (showAddProductDialog) {
            AddProductDialog(
                onDismiss = { showAddProductDialog = false },
                onConfirm = { name, price, unit, stock ->
                    viewModel.addProduct(name, price, unit, null, stock) {
                        showAddProductDialog = false
                        Toast.makeText(context, "প্রোডাক্ট সফলভাবে যোগ করা হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // SMS Send & Message Settings Dialog
        if (showSmsSettingsDialog) {
            var targetPhone by remember { mutableStateOf("") }
            var messageText by remember { mutableStateOf("সম্মানিত গ্রাহক, S.R Assistant থেকে আপনার বর্তমান হিসাব বিবরণী আপডেট করা হয়েছে।") }

            AlertDialog(
                onDismissRequest = { showSmsSettingsDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Message, contentDescription = null, tint = BlueAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("এসএমএস ও বার্তা পাঠান", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "কাস্টমার অথবা সাপ্লায়ারের কাছে হিসাব বিবরণী পাঠাতে চ্যানেল নির্বাচন করুন:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = targetPhone,
                            onValueChange = { targetPhone = it },
                            label = { Text("ফোন / হোয়াটসঅ্যাপ নম্বর") },
                            placeholder = { Text("01XXXXXXXXX") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            label = { Text("বার্তা / মেসেজ") },
                            maxLines = 4,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Quick Channel Buttons
                        Text("সরাসরি পাঠান:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    IntentHelper.openWhatsApp(context, targetPhone, messageText)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                            ) {
                                Text("WhatsApp", fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    IntentHelper.sendSms(context, targetPhone, messageText)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text("SMS", fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    IntentHelper.openTelegram(context, "", messageText)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = TelegramBlue)
                            ) {
                                Text("Telegram", fontSize = 11.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSmsSettingsDialog = false }) {
                        Text("সম্পন্ন")
                    }
                }
            )
        }
    }
}
