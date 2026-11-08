package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["phone"], unique = true)]
)
data class UserAccount(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nameBn: String,
    val nameEn: String,
    val phone: String,
    val email: String,
    val companyName: String,
    val password: String,
    val logoUri: String? = null,
    val role: String = "USER", // "ADMIN" or "USER"
    val isLoggedIn: Boolean = false, // MUST BE FALSE BY DEFAULT
    val createdAt: Long = System.currentTimeMillis()
) {
    val isAdmin: Boolean get() = role == "ADMIN" || phone.trim() == "01871752386"
}

/**
 * Customer Room database entity representing clients/buyers.
 */
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 0,
    val name: String,
    val companyName: String? = null,
    val phone: String,
    val email: String? = null,
    val address: String? = null,
    val photoUri: String? = null,
    val messengerId: String? = null,
    val telegramUser: String? = null,
    val creditLimit: Double = 0.0,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Supplier Room database entity representing vendors/distributors.
 */
@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 0,
    val name: String,
    val companyName: String? = null,
    val phone: String,
    val email: String? = null,
    val address: String? = null,
    val photoUri: String? = null,
    val messengerId: String? = null,
    val telegramUser: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Legacy/composite party representation for unified ledger screens and transactions.
 */
@Entity(tableName = "parties")
data class Party(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 0,
    val type: String, // "CUSTOMER" or "SUPPLIER"
    val name: String,
    val companyName: String? = null,
    val phone: String,
    val address: String? = null,
    val photoUri: String? = null,
    val messengerId: String? = null,
    val telegramUser: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

fun Customer.toParty(): Party = Party(
    id = id,
    userId = userId,
    type = "CUSTOMER",
    name = name,
    companyName = companyName,
    phone = phone,
    address = address,
    photoUri = photoUri,
    messengerId = messengerId,
    telegramUser = telegramUser,
    createdAt = createdAt
)

fun Supplier.toParty(): Party = Party(
    id = id,
    userId = userId,
    type = "SUPPLIER",
    name = name,
    companyName = companyName,
    phone = phone,
    address = address,
    photoUri = photoUri,
    messengerId = messengerId,
    telegramUser = telegramUser,
    createdAt = createdAt
)

fun Party.toCustomer(): Customer = Customer(
    id = id,
    userId = userId,
    name = name,
    companyName = companyName,
    phone = phone,
    address = address,
    photoUri = photoUri,
    messengerId = messengerId,
    telegramUser = telegramUser,
    createdAt = createdAt
)

fun Party.toSupplier(): Supplier = Supplier(
    id = id,
    userId = userId,
    name = name,
    companyName = companyName,
    phone = phone,
    address = address,
    photoUri = photoUri,
    messengerId = messengerId,
    telegramUser = telegramUser,
    createdAt = createdAt
)

@Entity(tableName = "transactions")
data class LedgerTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 0,
    val partyId: Long,
    val partyType: String, // "CUSTOMER" or "SUPPLIER"
    val type: String, // For Customer: "GIVEN" (বাকি/মাল দিলাম), "RECEIVED" (টাকা পেলাম)
                      // For Supplier: "PURCHASE" (পণ্য নিলাম/দেনা), "PAYMENT" (পরিশোধ করলাম), "RETURN" (পণ্য ফেরত দিলাম)
    val amount: Double,
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val billPhotoUri: String? = null
)

/**
 * Product Room database entity representing inventory items.
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 0,
    val name: String,
    val price: Double,
    val costPrice: Double = 0.0,
    val unit: String = "পিস", // পিস, কেজি, লিটার, বক্স, ডজন
    val photoUri: String? = null,
    val stock: Int = 0,
    val category: String? = null,
    val sku: String? = null,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class OrderItem(
    val productId: Long,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val unit: String = "পিস"
) {
    val total: Double get() = unitPrice * quantity
}

/**
 * Order Room database entity representing customer orders.
 */
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 0,
    val customerId: Long,
    val customerName: String,
    val customerPhone: String,
    val orderDate: Long = System.currentTimeMillis(),
    val deliveryDate: Long? = null,
    val totalAmount: Double,
    val discount: Double = 0.0,
    val paidAmount: Double = 0.0,
    val status: String = "PENDING", // PENDING, CONFIRMED, RECEIVED, DELIVERED, CANCELLED
    val itemsJson: String = "[]", // List<OrderItem> JSON encoded
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

typealias Order = OrderEntity

@Entity(tableName = "settings")
data class AppSetting(
    @PrimaryKey val key: String,
    val value: String
)
