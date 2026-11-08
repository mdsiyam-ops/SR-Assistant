package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItem
import com.example.data.model.Party
import com.example.data.model.Product
import com.example.data.repository.AssistantRepository
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PaonaGreen
import com.example.ui.theme.PaonaGreenLight
import com.example.utils.FormatUtils
import com.example.utils.IntentHelper
import com.example.viewmodel.AssistantViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: AssistantViewModel
) {
    val context = LocalContext.current
    val orders by viewModel.orders.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val products by viewModel.products.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: কাস্টমার রিপোর্ট, 1: প্রোডাক্ট বিক্রয়, 2: তারিখ ভিত্তিক

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "৫। বিস্তারিত ব্যবসায়িক রিপোর্ট",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary
            )
            Text(
                text = "কাস্টমার অর্ডার, প্রোডাক্ট বিক্রয় সংখ্যা ও নির্দিষ্ট তারিখ ভিত্তিক রিপোর্ট",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("১. কাস্টমার রিপোর্ট", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("২. প্রোডাক্ট বিক্রয়", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("৩. তারিখ ভিত্তিক", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> CustomerReportTab(customers = customers, orders = orders)
                1 -> ProductSalesSummaryTab(products = products, orders = orders)
                2 -> DateWiseOrdersTab(orders = orders)
            }
        }
    }
}

// --- TAB 1: কাস্টমার ভিত্তিক বিস্তারিত রিপোর্ট ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerReportTab(
    customers: List<Party>,
    orders: List<OrderEntity>
) {
    val context = LocalContext.current
    var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val customerOrders = remember(orders, selectedCustomer) {
        if (selectedCustomer == null) emptyList()
        else orders.filter { it.customerId == selectedCustomer!!.id }
    }

    val totalSpent = remember(customerOrders) {
        customerOrders.sumOf { it.totalAmount }
    }

    val totalPiecesOrdered = remember(customerOrders) {
        customerOrders.sumOf { ord ->
            val items = AssistantRepository.deserializeOrderItems(ord.itemsJson)
            items.sumOf { it.quantity }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Customer Selector Dropdown
            Text("কাস্টমার নির্বাচন করুন:", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCustomer?.name ?: "কাস্টমার সিলেক্ট করুন",
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("report_customer_dropdown")
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    customers.forEach { c ->
                        DropdownMenuItem(
                            text = { Text("${c.name} (${c.phone})") },
                            onClick = {
                                selectedCustomer = c
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        if (selectedCustomer != null) {
            item {
                // Customer Summary Metrics Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "গ্রাহক: ${selectedCustomer!!.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = EmeraldPrimary
                        )
                        if (!selectedCustomer!!.companyName.isNullOrBlank()) {
                            Text(text = "কোম্পানি: ${selectedCustomer!!.companyName}", fontSize = 12.sp)
                        }
                        Text(text = "মোবাইল: ${selectedCustomer!!.phone}", fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(PaonaGreenLight)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("মোট অর্ডার", fontSize = 11.sp, color = PaonaGreen)
                                Text("${FormatUtils.toBanglaNumber(customerOrders.size)} টি", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PaonaGreen)
                            }
                            Column {
                                Text("মোট পণ্য সংখ্যা", fontSize = 11.sp, color = PaonaGreen)
                                Text("${FormatUtils.toBanglaNumber(totalPiecesOrdered)} পিস", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PaonaGreen)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("সর্বমোট মূল্য", fontSize = 11.sp, color = PaonaGreen)
                                Text(FormatUtils.formatCurrency(totalSpent), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = PaonaGreen)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "তারিখ অনুযায়ী অর্ডারের তালিকা (${FormatUtils.toBanglaNumber(customerOrders.size)} টি)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            if (customerOrders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("এই কাস্টমারের কোন পূর্ববর্তী অর্ডার নেই।", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(customerOrders) { order ->
                    val items = AssistantRepository.deserializeOrderItems(order.itemsJson)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("অর্ডার তারিখ: ${FormatUtils.formatDate(order.orderDate)}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (order.status == "RECEIVED") PaonaGreenLight else BlueAccent.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (order.status == "RECEIVED") "রিসিভড" else if (order.status == "CONFIRMED") "কনফার্মড" else "পেন্ডিং",
                                        fontSize = 10.5.sp,
                                        color = if (order.status == "RECEIVED") PaonaGreen else BlueAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Items breakdown
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                items.forEach { itm ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("• ${itm.productName} × ${FormatUtils.toBanglaNumber(itm.quantity)} ${itm.unit}", fontSize = 12.sp)
                                        Text(FormatUtils.formatCurrency(itm.total), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("মোট পিস: ${FormatUtils.toBanglaNumber(items.sumOf { it.quantity })} টি", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("অর্ডার মূল্য: ${FormatUtils.formatCurrency(order.totalAmount)}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- TAB 2: প্রোডাক্ট বিক্রয় ও সর্বমোট পিস রিপোর্ট ---
@Composable
fun ProductSalesSummaryTab(
    products: List<Product>,
    orders: List<OrderEntity>
) {
    val context = LocalContext.current

    // Aggregate piece counts per product across all orders
    val productAggregates = remember(products, orders) {
        val countMap = mutableMapOf<Long, Int>()
        val revenueMap = mutableMapOf<Long, Double>()

        orders.forEach { ord ->
            val items = AssistantRepository.deserializeOrderItems(ord.itemsJson)
            items.forEach { item ->
                countMap[item.productId] = (countMap[item.productId] ?: 0) + item.quantity
                revenueMap[item.productId] = (revenueMap[item.productId] ?: 0.0) + item.total
            }
        }

        products.map { p ->
            val totalOrderedPieces = countMap[p.id] ?: 0
            val totalRev = revenueMap[p.id] ?: 0.0
            Triple(p, totalOrderedPieces, totalRev)
        }.sortedByDescending { it.second }
    }

    val grandTotalOrderedPieces = remember(productAggregates) {
        productAggregates.sumOf { it.second }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Overall summary banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "সব মিলিয়ে সর্বমোট প্রোডাক্ট অর্ডার সংখ্যা",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${FormatUtils.toBanglaNumber(grandTotalOrderedPieces)} পিস সর্বমোট অর্ডার হয়েছে",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "মোট সক্রিয় প্রোডাক্ট: ${FormatUtils.toBanglaNumber(products.size)} টি",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "কোন প্রোডাক্ট কত পিস অর্ডার হয়েছে (বিস্তারিত হিসাব)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (productAggregates.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("এখনো কোন প্রোডাক্ট যোগ করা হয়নি।", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(productAggregates) { (prod, orderedQty, salesAmt) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Inventory, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("একক মূল্য: ${FormatUtils.formatCurrency(prod.price)} / ${prod.unit}", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("বর্তমান স্টক: ${FormatUtils.toBanglaNumber(prod.stock)} ${prod.unit}", fontSize = 11.sp, color = EmeraldPrimary)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PaonaGreenLight)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${FormatUtils.toBanglaNumber(orderedQty)} ${prod.unit} অর্ডার",
                                    color = PaonaGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "বিক্রয়: ${FormatUtils.formatCurrency(salesAmt)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- TAB 3: নির্দিষ্ট তারিখ অনুযায়ী সমস্ত অর্ডার ও প্রোডাক্ট পিস হিসাব ---
@Composable
fun DateWiseOrdersTab(
    orders: List<OrderEntity>
) {
    val context = LocalContext.current
    var selectedTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
            }
            selectedTimestamp = cal.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val targetDateStr = remember(selectedTimestamp) {
        FormatUtils.formatDateNumeric(selectedTimestamp)
    }

    val dayOrders = remember(orders, targetDateStr) {
        orders.filter { FormatUtils.formatDateNumeric(it.orderDate) == targetDateStr }
    }

    val dayProductSummary = remember(dayOrders) {
        val map = mutableMapOf<String, Int>()
        dayOrders.forEach { ord ->
            val items = AssistantRepository.deserializeOrderItems(ord.itemsJson)
            items.forEach { item ->
                val key = "${item.productName} (${item.unit})"
                map[key] = (map[key] ?: 0) + item.quantity
            }
        }
        map
    }

    val dayTotalAmount = remember(dayOrders) {
        dayOrders.sumOf { it.totalAmount }
    }

    val dayTotalPieces = remember(dayProductSummary) {
        dayProductSummary.values.sum()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date Selector Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() }
                    .testTag("date_picker_trigger_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("নির্দিষ্ট তারিখ নির্বাচন করুন:", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(FormatUtils.formatDate(selectedTimestamp), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        }
                    }

                    OutlinedButton(onClick = { datePickerDialog.show() }) {
                        Text("তারিখ বদলান", fontSize = 11.5.sp)
                    }
                }
            }
        }

        // Day Summary Aggregation Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BlueAccent)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "একদিনে সমস্ত অর্ডার সারসংক্ষেপ (${FormatUtils.formatDate(selectedTimestamp)})",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("মোট অর্ডার", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("${FormatUtils.toBanglaNumber(dayOrders.size)} টি", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        }
                        Column {
                            Text("মোট পিস বিক্রয়", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("${FormatUtils.toBanglaNumber(dayTotalPieces)} পিস", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("মোট বিক্রয় মূল্য", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                            Text(FormatUtils.formatCurrency(dayTotalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // কোন প্রোডাক্ট কত পিস একসঙ্গে অর্ডার হয়েছে একদিনে
        item {
            Text(
                text = "কোন প্রোডাক্ট কত পিস একসঙ্গে অর্ডার হয়েছে একদিনে:",
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (dayProductSummary.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("নির্বাচিত তারিখে কোন অর্ডার পাওয়া যায়নি।", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.5.sp)
                    }
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dayProductSummary.forEach { (prodName, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("• $prodName", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PaonaGreenLight)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("${FormatUtils.toBanglaNumber(count)} পিস", color = PaonaGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Day's Detailed Orders List
            item {
                Text(
                    text = "ঐ দিনের অর্ডারের তালিকা:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }

            items(dayOrders) { ord ->
                val items = AssistantRepository.deserializeOrderItems(ord.itemsJson)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(ord.customerName, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text(FormatUtils.formatCurrency(ord.totalAmount), fontWeight = FontWeight.Bold, color = EmeraldPrimary, fontSize = 13.5.sp)
                        }
                        Text(
                            text = items.joinToString(", ") { "${it.productName} (${FormatUtils.toBanglaNumber(it.quantity)} ${it.unit})" },
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Share Day Summary Button
            item {
                Button(
                    onClick = {
                        val reportText = buildString {
                            appendLine("📊 *S.R Assistant দৈনিক অর্ডার রিপোর্ট*")
                            appendLine("তারিখ: ${FormatUtils.formatDate(selectedTimestamp)}")
                            appendLine("মোট অর্ডার: ${FormatUtils.toBanglaNumber(dayOrders.size)} টি")
                            appendLine("মোট বিক্রি: ${FormatUtils.formatCurrency(dayTotalAmount)}")
                            appendLine("--------------------------")
                            appendLine("*প্রোডাক্ট ভিত্তিক বিক্রয়:*")
                            dayProductSummary.forEach { (name, count) ->
                                appendLine("• $name : ${FormatUtils.toBanglaNumber(count)} পিস")
                            }
                        }
                        IntentHelper.shareText(context, reportText, "দৈনিক অর্ডার রিপোর্ট")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("দৈনিক রিপোর্ট শেয়ার করুন (WhatsApp / SMS)")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
