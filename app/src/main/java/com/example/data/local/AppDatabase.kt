package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.AppSetting
import com.example.data.model.Customer
import com.example.data.model.LedgerTransaction
import com.example.data.model.OrderEntity
import com.example.data.model.Party
import com.example.data.model.Product
import com.example.data.model.Supplier
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // User Accounts & Authentication
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUser(): Flow<UserAccount?>

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserAccount?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserAccount?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserAccount>>

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserAccount): Long

    @Update
    suspend fun updateUser(user: UserAccount)

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("UPDATE users SET isLoggedIn = 1 WHERE phone = :phone")
    suspend fun loginUser(phone: String)

    @Query("UPDATE users SET password = :newPass WHERE id = :userId")
    suspend fun resetUserPassword(userId: Long, newPass: String)

    @Query("UPDATE users SET logoUri = :logoUri WHERE isLoggedIn = 1")
    suspend fun updateActiveUserLogo(logoUri: String)

    // Parties (Customers & Suppliers)
    @Query("SELECT * FROM parties WHERE type = :type ORDER BY id DESC")
    fun getPartiesByType(type: String): Flow<List<Party>>

    @Query("SELECT * FROM parties WHERE type = :type AND userId = :userId ORDER BY id DESC")
    fun getPartiesByTypeAndUser(type: String, userId: Long): Flow<List<Party>>

    @Query("SELECT * FROM parties ORDER BY id DESC")
    fun getAllParties(): Flow<List<Party>>

    @Query("SELECT * FROM parties WHERE userId = :userId ORDER BY id DESC")
    fun getPartiesByUser(userId: Long): Flow<List<Party>>

    @Query("SELECT * FROM parties WHERE id = :id LIMIT 1")
    suspend fun getPartyById(id: Long): Party?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(party: Party): Long

    @Update
    suspend fun updateParty(party: Party)

    @Delete
    suspend fun deleteParty(party: Party)

    // Transactions
    @Query("SELECT * FROM transactions WHERE partyId = :partyId ORDER BY date DESC, id DESC")
    fun getTransactionsByParty(partyId: Long): Flow<List<LedgerTransaction>>

    @Query("SELECT * FROM transactions WHERE partyType = :partyType ORDER BY date DESC, id DESC")
    fun getTransactionsByPartyType(partyType: String): Flow<List<LedgerTransaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<LedgerTransaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC, id DESC")
    fun getTransactionsByUser(userId: Long): Flow<List<LedgerTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: LedgerTransaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: LedgerTransaction)

    // Products
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE userId = :userId ORDER BY name ASC")
    fun getProductsByUser(userId: Long): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    // Orders
    @Query("SELECT * FROM orders ORDER BY orderDate DESC, id DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY orderDate DESC")
    fun getOrdersByCustomer(customerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC, id DESC")
    fun getOrdersByUser(userId: Long): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    // Settings
    @Query("SELECT value FROM settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: AppSetting)
}

/**
 * Main Room Database configuration for the business assistant app.
 */
@Database(
    entities = [
        Customer::class,
        Supplier::class,
        Product::class,
        OrderEntity::class,
        Party::class,
        LedgerTransaction::class,
        UserAccount::class,
        AppSetting::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun supplierDao(): SupplierDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun appDao(): AppDao

    companion object {
        const val DATABASE_NAME = "sr_assistant_database.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
