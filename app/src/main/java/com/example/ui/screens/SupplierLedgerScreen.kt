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
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
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
fun SupplierLedgerScreen(
    viewModel: AssistantViewModel
) {
    val context = LocalContext.current
    val suppliersWithBalance by viewModel.suppliersWithBalance.collectAsState()
    val totalDena by viewModel.totalDena.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSupplier by remember { mutableStateOf<Party?>(null) }
    var showAddSupplierDialog by remember { mutableStateOf(false) }
    var showAddTxnDialog by remember { mutableStateOf(false) }
    var txnDialogDefaultType by remember { mutableStateOf("PURCHASE") }

    val filteredSuppliers = remember(suppliersWithBalance, searchQuery) {
        if (searchQuery.isBlank()) suppliersWithBalance
        else {
            suppliersWithBalance.filter {
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
        if (selectedSupplier == null) {
            // --- ALL SUPPLIERS LIST VIEW ---
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
                                text = "৩। সাপ্লায়ার হিসাব (মহাজন)",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = DenaRed
                            )
                            Text(
                                text = "মোট দেনা: ${FormatUtils.formatCurrency(totalDena)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DenaRed
                            )
                        }

                        IconButton(
                            onClick = { showAddSupplierDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DenaRed)
                                .testTag("add_supplier_header_btn")
                        ) {
                            Icon(Icons.Default.AddBusiness, contentDescription = "সাপ্লায়ার যোগ", tint = Color.White)
                        }
                    }
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("সাপ্লায়ারের নাম বা ফোন দিয়ে খুঁজুন...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("supplier_search_input")
                    )
                }

                if (filteredSuppliers.isEmpty()) {
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
                                Text("কোন সাপ্লায়ার পাওয়া যায়নি।", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(filteredSuppliers) { item ->
                        SupplierCard(
                            partyWithBalance = item,
                            onClick = { selectedSupplier = item.party }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        } else {
            // --- SINGLE SUPPLIER DETAIL VIEW ---
            val currentParty = selectedSupplier!!
            val partyTxns = allTransactions.filter { it.partyId == currentParty.id }
            val totalPurchase = partyTxns.filter { it.type == "PURCHASE" }.sumOf { it.amount }
            val totalPayment = partyTxns.filter { it.type == "PAYMENT" }.sumOf { it.amount }
            val totalReturn = partyTxns.filter { it.type == "RETURN" }.sumOf { it.amount }
            val netDena = totalPurchase - totalPayment - totalReturn

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
                        IconButton(onClick = { selectedSupplier = null }) {
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

                // Quick Communication Actions
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
                            IconButton(onClick = { IntentHelper.makePhoneCall(context, currentParty.phone) }) {
                                Icon(Icons.Default.Call, contentDescription = "কল করুন", tint = EmeraldPrimary)
                            }
                            IconButton(
                                onClick = {
                                    val msg = "আসসালামু আলাইকুম ${currentParty.name},\nS.R Assistant অনুযায়ী আপনার নিকট মোট দেনা হিসাব: ${FormatUtils.formatCurrency(netDena)}।"
                                    IntentHelper.openWhatsApp(context, currentParty.phone, msg)
                                }
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = WhatsAppGreen)
                            }
                            IconButton(
                                onClick = {
                                    val msg = "জনাব ${currentParty.name}, আপনার নিকট বর্তমান দেনা হিসাব: ${FormatUtils.formatCurrency(netDena)}।"
                                    IntentHelper.sendSms(context, currentParty.phone, msg)
                                }
                            ) {
                                Icon(Icons.Default.Message, contentDescription = "SMS", tint = EmeraldPrimary)
                            }
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
                            containerColor = if (netDena > 0) DenaRedLight else PaonaGreenLight
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "মোট দেনা (পরিশোধযোগ্য)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (netDena > 0) DenaRed else PaonaGreen
                                    )
                                    Text(
                                        text = FormatUtils.formatCurrency(netDena),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (netDena > 0) DenaRed else PaonaGreen
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("চালান ক্রয়: ${FormatUtils.formatCurrency(totalPurchase)}", fontSize = 11.sp)
                                    Text("পরিশোধ: ${FormatUtils.formatCurrency(totalPayment)}", fontSize = 11.sp)
                                    if (totalReturn > 0) {
                                        Text("পণ্য ফেরত: ${FormatUtils.formatCurrency(totalReturn)}", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3 Action Buttons: পণ্য নিলাম (+), পরিশোধ করলাম (-), পণ্য ফেরত দিলাম (ফেরত হিসাব -)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // পণ্য নিলাম (PURCHASE)
                        Button(
                            onClick = {
                                txnDialogDefaultType = "PURCHASE"
                                showAddTxnDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("supplier_purchase_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = DenaRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+ পণ্য নিলাম", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }

                        // পরিশোধ করলাম (PAYMENT)
                        Button(
                            onClick = {
                                txnDialogDefaultType = "PAYMENT"
                                showAddTxnDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("supplier_payment_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = PaonaGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("- পরিশোধ", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }

                        // পণ্য ফেরত দিলাম (RETURN)
                        Button(
                            onClick = {
                                txnDialogDefaultType = "RETURN"
                                showAddTxnDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("supplier_return_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("↺ পণ্য ফেরত", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // History Header
                item {
                    Text(
                        text = "চালান, পরিশোধ ও ফেরত হিসাব ইতিহাস (${FormatUtils.toBanglaNumber(partyTxns.size)} টি)",
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
                                Text("এখনো কোন চালান বা পরিশোধ এন্ট্রি করা হয়নি।", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(partyTxns) { txn ->
                        val isDena = txn.type == "PURCHASE"
                        val isReturn = txn.type == "RETURN"
                        val typeTitle = when (txn.type) {
                            "PURCHASE" -> "পণ্য নিলাম (চালান দেনা)"
                            "PAYMENT" -> "টাকা পরিশোধ করলাম"
                            "RETURN" -> "পণ্য ফেরত দিলাম (ফেরত হিসাব)"
                            else -> txn.type
                        }
                        val itemColor = if (isDena) DenaRed else if (isReturn) EmeraldPrimary else PaonaGreen

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
                                        text = typeTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = itemColor
                                    )
                                    if (txn.description.isNotBlank()) {
                                        Text(
                                            text = txn.description,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "তারিখ: ${FormatUtils.formatDate(txn.date)}",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = (if (isDena) "+ " else "- ") + FormatUtils.formatCurrency(txn.amount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = itemColor
                                    )
                                    IconButton(onClick = { viewModel.deleteTransaction(txn) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "মুছুন", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Share Full Supplier Statement
                item {
                    Button(
                        onClick = {
                            val statement = buildString {
                                appendLine("📜 *S.R Assistant সাপ্লায়ার হিসাব*")
                                appendLine("সাপ্লায়ার: ${currentParty.name}")
                                if (!currentParty.companyName.isNullOrBlank()) appendLine("কোম্পানি: ${currentParty.companyName}")
                                appendLine("ফোন: ${currentParty.phone}")
                                appendLine("--------------------------")
                                appendLine("মোট মালামাল ক্রয়: ${FormatUtils.formatCurrency(totalPurchase)}")
                                appendLine("মোট পরিশোধ: ${FormatUtils.formatCurrency(totalPayment)}")
                                if (totalReturn > 0) appendLine("মোট পণ্য ফেরত: ${FormatUtils.formatCurrency(totalReturn)}")
                                appendLine("👉 *বর্তমান নিট দেনা: ${FormatUtils.formatCurrency(netDena)}*")
                            }
                            IntentHelper.shareText(context, statement, "সাপ্লায়ার হিসাব বিবরণী")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("সাপ্লায়ার হিসাব বিবরণী শেয়ার করুন")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Add Supplier Dialog
        if (showAddSupplierDialog) {
            AddPartyDialog(
                initialType = "SUPPLIER",
                onDismiss = { showAddSupplierDialog = false },
                onConfirm = { type, name, company, phone, address, initBal, note ->
                    viewModel.addParty(type, name, company, phone, address, null, initBal, note) {
                        showAddSupplierDialog = false
                        Toast.makeText(context, "সাপ্লায়ার যুক্ত হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Add Transaction Dialog
        if (showAddTxnDialog && selectedSupplier != null) {
            AddTransactionDialog(
                party = selectedSupplier!!,
                isCustomer = false,
                defaultType = txnDialogDefaultType,
                onDismiss = { showAddTxnDialog = false },
                onConfirm = { type, amount, note ->
                    viewModel.addTransaction(
                        partyId = selectedSupplier!!.id,
                        partyType = "SUPPLIER",
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
fun SupplierCard(
    partyWithBalance: PartyWithBalance,
    onClick: () -> Unit
) {
    val p = partyWithBalance.party
    val balance = partyWithBalance.netBalance

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("supplier_item_${p.id}"),
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
                        .background(DenaRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Store,
                        contentDescription = null,
                        tint = DenaRed,
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
                    text = if (balance > 0) "দেনা পরিশোধযোগ্য" else if (balance < 0) "অগ্রিম দেওয়া" else "পরিশোধিত",
                    fontSize = 11.sp,
                    color = if (balance > 0) DenaRed else if (balance < 0) PaonaGreen else MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = FormatUtils.formatCurrency(balance),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (balance > 0) DenaRed else if (balance < 0) PaonaGreen else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
