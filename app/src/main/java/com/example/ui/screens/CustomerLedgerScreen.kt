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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.Party
import com.example.ui.components.AddPartyDialog
import com.example.ui.components.AddTransactionDialog
import com.example.ui.theme.DenaRed
import com.example.ui.theme.DenaRedLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PaonaGreen
import com.example.ui.theme.PaonaGreenLight
import com.example.ui.theme.WhatsAppGreen
import com.example.utils.FormatUtils
import com.example.utils.IntentHelper
import com.example.viewmodel.AssistantViewModel
import com.example.viewmodel.PartyWithBalance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerLedgerScreen(
    viewModel: AssistantViewModel
) {
    val context = LocalContext.current
    val customersWithBalance by viewModel.customersWithBalance.collectAsState()
    val totalPaona by viewModel.totalPaona.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCustomer by remember { mutableStateOf<Party?>(null) }
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var showAddTxnDialog by remember { mutableStateOf(false) }
    var txnDialogDefaultType by remember { mutableStateOf("GIVEN") }

    val filteredCustomers = remember(customersWithBalance, searchQuery) {
        if (searchQuery.isBlank()) customersWithBalance
        else {
            customersWithBalance.filter {
                it.party.name.contains(searchQuery, ignoreCase = true) ||
                        it.party.phone.contains(searchQuery) ||
                        (it.party.companyName?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (selectedCustomer == null) {
            // --- ALL CUSTOMERS LIST VIEW ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "২। কাস্টমার হিসাব (খরিদ্দার)",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = PaonaGreen
                            )
                            Text(
                                text = "মোট পাওনা: ${FormatUtils.formatCurrency(totalPaona)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PaonaGreen
                            )
                        }

                        IconButton(
                            onClick = { showAddCustomerDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PaonaGreen)
                                .testTag("add_customer_header_btn")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "কাস্টমার যোগ", tint = Color.White)
                        }
                    }
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("কাস্টমারের নাম বা ফোন দিয়ে খুঁজুন...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_search_input")
                    )
                }

                if (filteredCustomers.isEmpty()) {
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
                                Text("কোন কাস্টমার পাওয়া যায়নি।", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(filteredCustomers) { item ->
                        CustomerCard(
                            partyWithBalance = item,
                            onClick = { selectedCustomer = item.party }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        } else {
            // --- SINGLE CUSTOMER LEDGER DETAIL VIEW ---
            val currentParty = selectedCustomer!!
            val partyTxns = allTransactions.filter { it.partyId == currentParty.id }
            val totalGiven = partyTxns.filter { it.type == "GIVEN" }.sumOf { it.amount }
            val totalReceived = partyTxns.filter { it.type == "RECEIVED" }.sumOf { it.amount }
            val netPaona = totalGiven - totalReceived

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header & Back
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selectedCustomer = null }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "পিছনে যান")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentParty.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (!currentParty.companyName.isNullOrBlank()) {
                                Text(
                                    text = currentParty.companyName,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Quick Communication Actions (Call, WhatsApp, SMS, Map)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Call
                            IconButton(onClick = { IntentHelper.makePhoneCall(context, currentParty.phone) }) {
                                Icon(Icons.Default.Call, contentDescription = "কল করুন", tint = EmeraldPrimary)
                            }
                            // WhatsApp
                            IconButton(
                                onClick = {
                                    val msg = "আসসালামু আলাইকুম ${currentParty.name},\nS.R Assistant অনুযায়ী আপনার মোট পাওনা বাকি: ${FormatUtils.formatCurrency(netPaona)}।"
                                    IntentHelper.openWhatsApp(context, currentParty.phone, msg)
                                }
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = WhatsAppGreen)
                            }
                            // SMS
                            IconButton(
                                onClick = {
                                    val msg = "জনাব ${currentParty.name}, আপনার বর্তমান বাকি হিসাব: ${FormatUtils.formatCurrency(netPaona)}।"
                                    IntentHelper.sendSms(context, currentParty.phone, msg)
                                }
                            ) {
                                Icon(Icons.Default.Message, contentDescription = "SMS", tint = EmeraldPrimary)
                            }
                            // Google Maps
                            if (!currentParty.address.isNullOrBlank()) {
                                IconButton(onClick = { IntentHelper.openGoogleMaps(context, currentParty.address) }) {
                                    Icon(Icons.Default.LocationOn, contentDescription = "ম্যাপ", tint = DenaRed)
                                }
                            }
                        }
                    }
                }

                // Ledger Balance Summary Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (netPaona >= 0) PaonaGreenLight else DenaRedLight
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "মোট পাওনা (বাকি)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (netPaona >= 0) PaonaGreen else DenaRed
                                    )
                                    Text(
                                        text = FormatUtils.formatCurrency(netPaona),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (netPaona >= 0) PaonaGreen else DenaRed
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("মোট মাল দিলাম: ${FormatUtils.formatCurrency(totalGiven)}", fontSize = 11.sp)
                                    Text("মোট টাকা পেলাম: ${FormatUtils.formatCurrency(totalReceived)}", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // Two Big Action Buttons: মাল দিলাম (+) & টাকা পেলাম (-)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // মাল দিলাম (বাকি বৃদ্ধি)
                        Button(
                            onClick = {
                                txnDialogDefaultType = "GIVEN"
                                showAddTxnDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("customer_mal_dilam_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = DenaRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+ মাল দিলাম (বাকি)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        // টাকা পেলাম (জমা)
                        Button(
                            onClick = {
                                txnDialogDefaultType = "RECEIVED"
                                showAddTxnDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("customer_taka_pelam_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = PaonaGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("- টাকা পেলাম (জমা)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // History Title
                item {
                    Text(
                        text = "লেনদেনের ইতিহাস (${FormatUtils.toBanglaNumber(partyTxns.size)} টি)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (partyTxns.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("এখনো কোন লেনদেন এন্ট্রি করা হয়নি।", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(partyTxns) { txn ->
                        val isReceived = txn.type == "RECEIVED"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isReceived) "টাকা পেলাম (জমা)" else "মাল দিলাম (বাকি)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isReceived) PaonaGreen else DenaRed
                                    )
                                    if (txn.description.isNotBlank()) {
                                        Text(
                                            text = txn.description,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = FormatUtils.formatDate(txn.date),
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = (if (isReceived) "- " else "+ ") + FormatUtils.formatCurrency(txn.amount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isReceived) PaonaGreen else DenaRed
                                    )
                                    IconButton(onClick = { viewModel.deleteTransaction(txn) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "মুছুন", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Share Full Statement Button
                item {
                    Button(
                        onClick = {
                            val statement = buildString {
                                appendLine("📜 *S.R Assistant হিসাব বিবরণী*")
                                appendLine("কাস্টমার: ${currentParty.name}")
                                if (!currentParty.companyName.isNullOrBlank()) appendLine("প্রতিষ্ঠান: ${currentParty.companyName}")
                                appendLine("ফোন: ${currentParty.phone}")
                                appendLine("--------------------------")
                                appendLine("মোট মাল দেওয়া: ${FormatUtils.formatCurrency(totalGiven)}")
                                appendLine("মোট জমা/পরিশোধ: ${FormatUtils.formatCurrency(totalReceived)}")
                                appendLine("👉 *বর্তমান নিট পাওনা: ${FormatUtils.formatCurrency(netPaona)}*")
                            }
                            IntentHelper.shareText(context, statement, "কাস্টমার হিসাব বিবরণী")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("হিসাবের বিবরণী শেয়ার করুন (WhatsApp/SMS)")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Add Customer Dialog
        if (showAddCustomerDialog) {
            AddPartyDialog(
                initialType = "CUSTOMER",
                onDismiss = { showAddCustomerDialog = false },
                onConfirm = { type, name, company, phone, address, initBal, note ->
                    viewModel.addParty(type, name, company, phone, address, null, initBal, note) {
                        showAddCustomerDialog = false
                        Toast.makeText(context, "কাস্টমার যুক্ত হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Add Transaction Dialog
        if (showAddTxnDialog && selectedCustomer != null) {
            AddTransactionDialog(
                party = selectedCustomer!!,
                isCustomer = true,
                defaultType = txnDialogDefaultType,
                onDismiss = { showAddTxnDialog = false },
                onConfirm = { type, amount, note ->
                    viewModel.addTransaction(
                        partyId = selectedCustomer!!.id,
                        partyType = "CUSTOMER",
                        type = type,
                        amount = amount,
                        description = note
                    ) {
                        showAddTxnDialog = false
                        Toast.makeText(context, "হিসাব যুক্ত হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
fun CustomerCard(
    partyWithBalance: PartyWithBalance,
    onClick: () -> Unit
) {
    val p = partyWithBalance.party
    val balance = partyWithBalance.netBalance

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("customer_item_${p.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PaonaGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = PaonaGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = p.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!p.companyName.isNullOrBlank()) {
                        Text(
                            text = p.companyName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = p.phone,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (balance > 0) "পাওনা পাবেন" else if (balance < 0) "অতিরিক্ত জমা" else "পরিশোধিত",
                    fontSize = 11.sp,
                    color = if (balance > 0) PaonaGreen else if (balance < 0) DenaRed else MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = FormatUtils.formatCurrency(balance),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (balance > 0) PaonaGreen else if (balance < 0) DenaRed else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
