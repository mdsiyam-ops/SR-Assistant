package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderItem
import com.example.data.model.Party
import com.example.data.model.Product
import com.example.ui.theme.DenaRed
import com.example.ui.theme.DenaRedLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.PaonaGreen
import com.example.ui.theme.PaonaGreenLight
import com.example.utils.FormatUtils

// --- Add Customer or Supplier Dialog ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPartyDialog(
    initialType: String = "CUSTOMER",
    onDismiss: () -> Unit,
    onConfirm: (
        type: String,
        name: String,
        company: String?,
        phone: String,
        address: String?,
        initialBalance: Double,
        initialNote: String
    ) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialType) }
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var initialBalanceStr by remember { mutableStateOf("") }
    var initialNote by remember { mutableStateOf("পূর্বের দেনা/পাওনা") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (selectedType == "CUSTOMER") "নতুন কাস্টমার যোগ করুন" else "নতুন সাপ্লায়ার যোগ করুন",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Type Switch (Customer / Supplier)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedType == "CUSTOMER") PaonaGreen else Color.Transparent)
                            .clickable { selectedType = "CUSTOMER" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "কাস্টমার (খরিদ্দার)",
                            color = if (selectedType == "CUSTOMER") Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedType == "SUPPLIER") DenaRed else Color.Transparent)
                            .clickable { selectedType = "SUPPLIER" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "সাপ্লায়ার (মহাজন)",
                            color = if (selectedType == "SUPPLIER") Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (selectedType == "CUSTOMER") "কাস্টমারের নাম *" else "সাপ্লায়ারের নাম *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("party_name_input")
                )

                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("কোম্পানির নাম (ঐচ্ছিক)") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("party_company_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("ফোন নম্বর *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("party_phone_input")
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("ঠিকানা / লোকেশন") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("party_address_input")
                )

                OutlinedTextField(
                    value = initialBalanceStr,
                    onValueChange = { initialBalanceStr = it },
                    label = { Text(if (selectedType == "CUSTOMER") "পূর্বের পাওনা টাকা (যদি থাকে)" else "পূর্বের দেনা টাকা (যদি থাকে)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("party_balance_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        val initBal = initialBalanceStr.toDoubleOrNull() ?: 0.0
                        onConfirm(
                            selectedType,
                            name,
                            company,
                            phone,
                            address,
                            initBal,
                            initialNote
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == "CUSTOMER") PaonaGreen else DenaRed
                ),
                modifier = Modifier.testTag("party_submit_button")
            ) {
                Text("সংরক্ষণ করুন", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

// --- Add Product Dialog ---
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, price: Double, unit: String, stock: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("পিস") }
    var stockStr by remember { mutableStateOf("0") }

    val units = listOf("পিস", "কেজি", "ডজন", "বক্স", "লিটার", "কার্টন")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন প্রোডাক্ট যোগ করুন", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("প্রোডাক্টের নাম *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("product_name_input")
                )

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("মূল্য (টাকা) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("product_price_input")
                )

                Text("একক নির্বাচন করুন:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    units.take(4).forEach { u ->
                        FilterChip(
                            selected = unit == u,
                            onClick = { unit = u },
                            label = { Text(u, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = stockStr,
                    onValueChange = { stockStr = it },
                    label = { Text("প্রারম্ভিক স্টক পরিমাণ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("product_stock_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    val stock = stockStr.toIntOrNull() ?: 0
                    if (name.isNotBlank() && price > 0) {
                        onConfirm(name, price, unit, stock)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("product_save_button")
            ) {
                Text("সেভ করুন")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}

// --- Add Ledger Transaction Dialog ---
@Composable
fun AddTransactionDialog(
    party: Party,
    isCustomer: Boolean,
    defaultType: String, // "GIVEN", "RECEIVED", "PURCHASE", "PAYMENT", "RETURN"
    onDismiss: () -> Unit,
    onConfirm: (type: String, amount: Double, note: String) -> Unit
) {
    var currentType by remember { mutableStateOf(defaultType) }
    var amountStr by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val title = when (currentType) {
        "GIVEN" -> "মাল দিলাম / বাকি যোগ করুন"
        "RECEIVED" -> "টাকা পেলাম / জমা নিন"
        "PURCHASE" -> "পণ্য নিলাম / দেনা যোগ করুন"
        "PAYMENT" -> "টাকা পরিশোধ করলাম"
        "RETURN" -> "পণ্য ফেরত দিলাম (ফেরত হিসাব)"
        else -> "নতুন হিসাব এন্ট্রি"
    }

    val isGreen = currentType == "RECEIVED" || currentType == "PAYMENT" || currentType == "RETURN"
    val themeColor = if (isGreen) PaonaGreen else DenaRed

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = themeColor)
                Text("নাম: ${party.name}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Type selector
                if (isCustomer) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (currentType == "GIVEN") DenaRed else Color.Transparent)
                                .clickable { currentType = "GIVEN" }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("মাল দিলাম (বাকি)", color = if (currentType == "GIVEN") Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (currentType == "RECEIVED") PaonaGreen else Color.Transparent)
                                .clickable { currentType = "RECEIVED" }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("টাকা পেলাম (জমা)", color = if (currentType == "RECEIVED") Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (currentType == "PURCHASE") DenaRed else Color.Transparent)
                                .clickable { currentType = "PURCHASE" }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("পণ্য নিলাম", color = if (currentType == "PURCHASE") Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (currentType == "PAYMENT") PaonaGreen else Color.Transparent)
                                .clickable { currentType = "PAYMENT" }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("পরিশোধ", color = if (currentType == "PAYMENT") Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (currentType == "RETURN") EmeraldPrimary else Color.Transparent)
                                .clickable { currentType = "RETURN" }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("মাল ফেরত", color = if (currentType == "RETURN") Color.White else MaterialTheme.colorScheme.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("টাকার পরিমাণ (৳) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("txn_amount_input")
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("বিবরণ / মালের বিবরণ / নোট") },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("txn_note_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onConfirm(currentType, amt, note)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                modifier = Modifier.testTag("txn_save_button")
            ) {
                Text("যুক্ত করুন", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("বাতিল") }
        }
    )
}
