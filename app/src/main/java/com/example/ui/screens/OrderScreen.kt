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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.DenaRed
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PaonaGreen
import com.example.ui.theme.PaonaGreenLight
import com.example.ui.theme.WhatsAppGreen
import com.example.utils.FormatUtils
import com.example.utils.IntentHelper
import com.example.viewmodel.AssistantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    viewModel: AssistantViewModel
) {
    val context = LocalContext.current
    val orders by viewModel.orders.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val products by viewModel.products.collectAsState()

    var showCreateOrderDialog by remember { mutableStateOf(false) }
    var selectedStatusFilter by remember { mutableStateOf("ALL") }

    val filteredOrders = remember(orders, selectedStatusFilter) {
        if (selectedStatusFilter == "ALL") orders
        else orders.filter { it.status == selectedStatusFilter }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
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
                            text = "৪। অর্ডার সংগ্রহ ও ব্যবস্থাপনা",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlueAccent
                        )
                        Text(
                            text = "মোট অর্ডার: ${FormatUtils.toBanglaNumber(orders.size)} টি | প্রোডাক্ট ভিত্তিক অর্ডার",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            if (customers.isEmpty()) {
                                Toast.makeText(context, "প্রথমে কাস্টমার যোগ করুন", Toast.LENGTH_SHORT).show()
                            } else if (products.isEmpty()) {
                                Toast.makeText(context, "ড্যাশবোর্ড থেকে আগে প্রোডাক্ট যোগ করুন", Toast.LENGTH_SHORT).show()
                            } else {
                                showCreateOrderDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("create_order_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ নতুন অর্ডার", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Status Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedStatusFilter == "ALL",
                        onClick = { selectedStatusFilter = "ALL" },
                        label = { Text("সকল (${FormatUtils.toBanglaNumber(orders.size)})", fontSize = 11.5.sp) }
                    )
                    FilterChip(
                        selected = selectedStatusFilter == "PENDING",
                        onClick = { selectedStatusFilter = "PENDING" },
                        label = { Text("পেন্ডিং", fontSize = 11.5.sp) }
                    )
                    FilterChip(
                        selected = selectedStatusFilter == "CONFIRMED",
                        onClick = { selectedStatusFilter = "CONFIRMED" },
                        label = { Text("কনফার্মড", fontSize = 11.5.sp) }
                    )
                    FilterChip(
                        selected = selectedStatusFilter == "RECEIVED",
                        onClick = { selectedStatusFilter = "RECEIVED" },
                        label = { Text("রিসিভড/ডেলিভার্ড", fontSize = 11.5.sp) }
                    )
                }
            }

            if (filteredOrders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("এই ফিল্টারে কোন অর্ডার পাওয়া যায়নি।", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                items(filteredOrders) { order ->
                    OrderCard(
                        order = order,
                        onStatusChange = { newStatus ->
                            viewModel.updateOrderStatus(order, newStatus)
                            Toast.makeText(context, "স্ট্যাটাস আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            viewModel.deleteOrder(order)
                            Toast.makeText(context, "অর্ডার ডিলিট হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Create Order Dialog
        if (showCreateOrderDialog) {
            CreateOrderDialog(
                customers = customers,
                products = products,
                onDismiss = { showCreateOrderDialog = false },
                onConfirm = { customer, items, total, note, status ->
                    viewModel.createOrder(
                        customerId = customer.id,
                        customerName = customer.name,
                        customerPhone = customer.phone,
                        items = items,
                        totalAmount = total,
                        note = note,
                        status = status
                    ) {
                        showCreateOrderDialog = false
                        Toast.makeText(context, "অর্ডার সফলভাবে সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderDialog(
    customers: List<Party>,
    products: List<Product>,
    onDismiss: () -> Unit,
    onConfirm: (
        customer: Party,
        items: List<OrderItem>,
        totalAmount: Double,
        note: String?,
        status: String
    ) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }
    var customerDropdownExpanded by remember { mutableStateOf(false) }
    val productQuantities = remember { mutableStateMapOf<Long, Int>() }
    var note by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("CONFIRMED") }

    // Initialize quantities with 0
    remember {
        products.forEach { p ->
            productQuantities[p.id] = 0
        }
    }

    val selectedItems = remember(productQuantities.toMap()) {
        products.filter { (productQuantities[it.id] ?: 0) > 0 }.map { p ->
            OrderItem(
                productId = p.id,
                productName = p.name,
                unitPrice = p.price,
                quantity = productQuantities[p.id] ?: 0,
                unit = p.unit
            )
        }
    }

    val calculatedTotal = remember(selectedItems) {
        selectedItems.sumOf { it.total }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("নতুন কাস্টমার অর্ডার তৈরি করুন", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BlueAccent)
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Customer Selection Dropdown
                item {
                    Text("১. কাস্টমার নির্বাচন করুন *", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    ExposedDropdownMenuBox(
                        expanded = customerDropdownExpanded,
                        onExpandedChange = { customerDropdownExpanded = !customerDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCustomer?.let { "${it.name} (${it.phone})" } ?: "কাস্টমার সিলেক্ট করুন",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = customerDropdownExpanded,
                            onDismissRequest = { customerDropdownExpanded = false }
                        ) {
                            customers.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text("${c.name} - ${c.phone} ${c.companyName?.let { "($it)" } ?: ""}") },
                                    onClick = {
                                        selectedCustomer = c
                                        customerDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 2. Select Products with counters
                item {
                    Text("২. প্রোডাক্ট ও পরিমাণ নির্বাচন করুন *", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                items(products) { product ->
                    val qty = productQuantities[product.id] ?: 0
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (qty > 0) BlueAccent.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp)
                                Text("${FormatUtils.formatCurrency(product.price)} / ${product.unit}", fontSize = 11.sp, color = EmeraldPrimary)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        if (qty > 0) productQuantities[product.id] = qty - 1
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "কমান", modifier = Modifier.size(16.dp))
                                }

                                Text(
                                    text = FormatUtils.toBanglaNumber(qty),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )

                                IconButton(
                                    onClick = {
                                        productQuantities[product.id] = qty + 1
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "বাড়ান", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Total Calculation Display
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("নির্বাচিত প্রোডাক্ট: ${FormatUtils.toBanglaNumber(selectedItems.size)} টি", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("মোট বিল: ${FormatUtils.formatCurrency(calculatedTotal)}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldPrimary)
                        }
                    }
                }

                // Status selector
                item {
                    Text("অর্ডার স্ট্যাটাস:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = status == "PENDING",
                            onClick = { status = "PENDING" },
                            label = { Text("পেন্ডিং", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = status == "CONFIRMED",
                            onClick = { status = "CONFIRMED" },
                            label = { Text("কনফার্মড", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = status == "RECEIVED",
                            onClick = { status = "RECEIVED" },
                            label = { Text("রিসিভড", fontSize = 11.sp) }
                        )
                    }
                }

                // Note
                item {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("অর্ডার নোট / ডেলিভারি ঠিকানা") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedCustomer != null && selectedItems.isNotEmpty()) {
                        onConfirm(
                            selectedCustomer!!,
                            selectedItems,
                            calculatedTotal,
                            note.ifBlank { null },
                            status
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                enabled = selectedCustomer != null && selectedItems.isNotEmpty(),
                modifier = Modifier.testTag("save_order_confirm_btn")
            ) {
                Text("অর্ডার নিশ্চিত করুন", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}

@Composable
fun OrderCard(
    order: OrderEntity,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val items = remember(order.itemsJson) {
        AssistantRepository.deserializeOrderItems(order.itemsJson)
    }

    val (statusLabel, statusColor) = when (order.status) {
        "PENDING" -> "পেন্ডিং (অপেক্ষমান)" to AmberAccent
        "CONFIRMED" -> "কনফার্মড (নিশ্চিত)" to BlueAccent
        "RECEIVED" -> "রিসিভড / সম্পন্ন" to PaonaGreen
        "DELIVERED" -> "ডেলিভার্ড" to PaonaGreen
        "CANCELLED" -> "বাতিল" to DenaRed
        else -> order.status to MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Customer Name & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(BlueAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = BlueAccent, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                        Text(order.customerPhone, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Products list breakdown
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "• ${item.productName} (${FormatUtils.toBanglaNumber(item.quantity)} ${item.unit})",
                            fontSize = 12.sp
                        )
                        Text(
                            text = FormatUtils.formatCurrency(item.total),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (!order.note.isNullOrBlank()) {
                Text(
                    text = "নোট: ${order.note}",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "অর্ডার তারিখ: ${FormatUtils.formatDate(order.orderDate)}",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "মোট: ${FormatUtils.formatCurrency(order.totalAmount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldPrimary
                )
            }

            // Actions: Change Status & Share Slip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (order.status != "RECEIVED" && order.status != "DELIVERED") {
                        Button(
                            onClick = { onStatusChange("RECEIVED") },
                            colors = ButtonDefaults.buttonColors(containerColor = PaonaGreen),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("রিসিভড মার্ক করুন", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    // Share Slip
                    IconButton(
                        onClick = {
                            val slip = buildString {
                                appendLine("📦 *S.R Assistant অর্ডার মেমো*")
                                appendLine("গ্রাহক: ${order.customerName}")
                                appendLine("মোবাইল: ${order.customerPhone}")
                                appendLine("তারিখ: ${FormatUtils.formatDate(order.orderDate)}")
                                appendLine("স্ট্যাটাস: $statusLabel")
                                appendLine("-------------------------")
                                items.forEach { itm ->
                                    appendLine("- ${itm.productName} (${FormatUtils.toBanglaNumber(itm.quantity)} ${itm.unit}) = ${FormatUtils.formatCurrency(itm.total)}")
                                }
                                appendLine("-------------------------")
                                appendLine("👉 *সর্বমোট মূল্য: ${FormatUtils.formatCurrency(order.totalAmount)}*")
                            }
                            IntentHelper.openWhatsApp(context, order.customerPhone, slip)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = "WhatsApp এ পাঠান", tint = WhatsAppGreen)
                    }

                    IconButton(
                        onClick = { IntentHelper.makePhoneCall(context, order.customerPhone) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "কল", tint = EmeraldPrimary)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "মুছুন", tint = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
