package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.local.AppDatabase
import com.example.data.local.CustomerDao
import com.example.data.local.OrderDao
import com.example.data.local.ProductDao
import com.example.data.local.SupplierDao
import com.example.data.model.AppSetting
import com.example.data.model.Customer
import com.example.data.model.LedgerTransaction
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItem
import com.example.data.model.Party
import com.example.data.model.Product
import com.example.data.model.Supplier
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class AssistantRepository(
    private val dao: AppDao,
    val customerDao: CustomerDao,
    val supplierDao: SupplierDao,
    val productDao: ProductDao,
    val orderDao: OrderDao
) {

    constructor(db: AppDatabase) : this(
        dao = db.appDao(),
        customerDao = db.customerDao(),
        supplierDao = db.supplierDao(),
        productDao = db.productDao(),
        orderDao = db.orderDao()
    )

    // User authentication & profile
    val activeUser: Flow<UserAccount?> = dao.getActiveUser()
    val allUsers: Flow<List<UserAccount>> = dao.getAllUsers()
    val userCount: Flow<Int> = dao.getUserCount()

    suspend fun insertUserDirect(user: UserAccount): Long = dao.insertUser(user)
    suspend fun resetUserPassword(userId: Long, newPass: String) = dao.resetUserPassword(userId, newPass)
    suspend fun getUserById(userId: Long): UserAccount? = dao.getUserById(userId)
    fun getPartiesByUser(userId: Long): Flow<List<Party>> = dao.getPartiesByUser(userId)
    fun getTransactionsByUser(userId: Long): Flow<List<LedgerTransaction>> = dao.getTransactionsByUser(userId)
    fun getProductsByUser(userId: Long): Flow<List<Product>> = dao.getProductsByUser(userId)
    fun getOrdersByUser(userId: Long): Flow<List<OrderEntity>> = dao.getOrdersByUser(userId)

    // Customer operations (Room Entity)
    val customersList: Flow<List<Customer>> = customerDao.getAllCustomers()
    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)
    suspend fun getCustomerById(id: Long): Customer? = customerDao.getCustomerById(id)

    suspend fun addCustomer(customer: Customer): Long {
        val id = customerDao.insertCustomer(customer)
        // Sync to legacy party table for unified transaction ledgers
        dao.insertParty(
            Party(
                id = id,
                type = "CUSTOMER",
                name = customer.name,
                companyName = customer.companyName,
                phone = customer.phone,
                address = customer.address,
                photoUri = customer.photoUri,
                messengerId = customer.messengerId,
                telegramUser = customer.telegramUser,
                createdAt = customer.createdAt
            )
        )
        return id
    }

    suspend fun updateCustomer(customer: Customer) {
        customerDao.updateCustomer(customer)
        dao.updateParty(
            Party(
                id = customer.id,
                type = "CUSTOMER",
                name = customer.name,
                companyName = customer.companyName,
                phone = customer.phone,
                address = customer.address,
                photoUri = customer.photoUri,
                messengerId = customer.messengerId,
                telegramUser = customer.telegramUser,
                createdAt = customer.createdAt
            )
        )
    }

    suspend fun deleteCustomer(customer: Customer) {
        customerDao.deleteCustomer(customer)
        dao.deleteParty(
            Party(
                id = customer.id,
                type = "CUSTOMER",
                name = customer.name,
                companyName = customer.companyName,
                phone = customer.phone,
                address = customer.address
            )
        )
    }

    // Supplier operations (Room Entity)
    val suppliersList: Flow<List<Supplier>> = supplierDao.getAllSuppliers()
    fun searchSuppliers(query: String): Flow<List<Supplier>> = supplierDao.searchSuppliers(query)
    suspend fun getSupplierById(id: Long): Supplier? = supplierDao.getSupplierById(id)

    suspend fun addSupplier(supplier: Supplier): Long {
        val id = supplierDao.insertSupplier(supplier)
        // Sync to legacy party table for unified transaction ledgers
        dao.insertParty(
            Party(
                id = id,
                type = "SUPPLIER",
                name = supplier.name,
                companyName = supplier.companyName,
                phone = supplier.phone,
                address = supplier.address,
                photoUri = supplier.photoUri,
                messengerId = supplier.messengerId,
                telegramUser = supplier.telegramUser,
                createdAt = supplier.createdAt
            )
        )
        return id
    }

    suspend fun updateSupplier(supplier: Supplier) {
        supplierDao.updateSupplier(supplier)
        dao.updateParty(
            Party(
                id = supplier.id,
                type = "SUPPLIER",
                name = supplier.name,
                companyName = supplier.companyName,
                phone = supplier.phone,
                address = supplier.address,
                photoUri = supplier.photoUri,
                messengerId = supplier.messengerId,
                telegramUser = supplier.telegramUser,
                createdAt = supplier.createdAt
            )
        )
    }

    suspend fun deleteSupplier(supplier: Supplier) {
        supplierDao.deleteSupplier(supplier)
        dao.deleteParty(
            Party(
                id = supplier.id,
                type = "SUPPLIER",
                name = supplier.name,
                companyName = supplier.companyName,
                phone = supplier.phone,
                address = supplier.address
            )
        )
    }

    // Product operations (Room Entity)
    val productsList: Flow<List<Product>> = productDao.getAllProducts()
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)
    fun getLowStockProducts(threshold: Int = 10): Flow<List<Product>> = productDao.getLowStockProducts(threshold)
    suspend fun addProduct(product: Product): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun updateProductStock(id: Long, stock: Int) = productDao.updateProductStock(id, stock)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    // Order operations (Room Entity)
    val ordersList: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    fun getOrdersByCustomer(customerId: Long): Flow<List<OrderEntity>> = orderDao.getOrdersByCustomer(customerId)
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>> = orderDao.getOrdersByStatus(status)
    suspend fun addOrder(order: OrderEntity): Long = orderDao.insertOrder(order)
    suspend fun updateOrder(order: OrderEntity) = orderDao.updateOrder(order)
    suspend fun updateOrderStatus(id: Long, status: String) = orderDao.updateOrderStatus(id, status)
    suspend fun deleteOrder(order: OrderEntity) = orderDao.deleteOrder(order)

    // Legacy/Composite Parties support for backwards compatibility
    val allCustomers: Flow<List<Party>> = dao.getPartiesByType("CUSTOMER")
    val allSuppliers: Flow<List<Party>> = dao.getPartiesByType("SUPPLIER")
    val allParties: Flow<List<Party>> = dao.getAllParties()
    val allTransactions: Flow<List<LedgerTransaction>> = dao.getAllTransactions()

    suspend fun getUserByPhone(phone: String): UserAccount? = dao.getUserByPhone(phone)

    suspend fun registerUser(user: UserAccount): Long {
        dao.logoutAllUsers()
        return dao.insertUser(user.copy(isLoggedIn = true))
    }

    suspend fun loginUser(phone: String) {
        dao.logoutAllUsers()
        dao.loginUser(phone)
    }

    suspend fun logout() {
        dao.logoutAllUsers()
    }

    suspend fun updateUserLogo(uri: String) {
        dao.updateActiveUserLogo(uri)
    }

    suspend fun addParty(party: Party): Long {
        val id = dao.insertParty(party)
        if (party.type == "CUSTOMER") {
            customerDao.insertCustomer(
                Customer(
                    id = id,
                    name = party.name,
                    companyName = party.companyName,
                    phone = party.phone,
                    address = party.address,
                    photoUri = party.photoUri,
                    messengerId = party.messengerId,
                    telegramUser = party.telegramUser,
                    createdAt = party.createdAt
                )
            )
        } else {
            supplierDao.insertSupplier(
                Supplier(
                    id = id,
                    name = party.name,
                    companyName = party.companyName,
                    phone = party.phone,
                    address = party.address,
                    photoUri = party.photoUri,
                    messengerId = party.messengerId,
                    telegramUser = party.telegramUser,
                    createdAt = party.createdAt
                )
            )
        }
        return id
    }

    suspend fun updateParty(party: Party) {
        dao.updateParty(party)
        if (party.type == "CUSTOMER") {
            customerDao.updateCustomer(
                Customer(
                    id = party.id,
                    name = party.name,
                    companyName = party.companyName,
                    phone = party.phone,
                    address = party.address,
                    photoUri = party.photoUri,
                    messengerId = party.messengerId,
                    telegramUser = party.telegramUser,
                    createdAt = party.createdAt
                )
            )
        } else {
            supplierDao.updateSupplier(
                Supplier(
                    id = party.id,
                    name = party.name,
                    companyName = party.companyName,
                    phone = party.phone,
                    address = party.address,
                    photoUri = party.photoUri,
                    messengerId = party.messengerId,
                    telegramUser = party.telegramUser,
                    createdAt = party.createdAt
                )
            )
        }
    }

    suspend fun deleteParty(party: Party) {
        dao.deleteParty(party)
        if (party.type == "CUSTOMER") {
            customerDao.deleteCustomerById(party.id)
        } else {
            supplierDao.deleteSupplierById(party.id)
        }
    }

    fun getTransactionsForParty(partyId: Long): Flow<List<LedgerTransaction>> =
        dao.getTransactionsByParty(partyId)

    suspend fun addTransaction(transaction: LedgerTransaction): Long =
        dao.insertTransaction(transaction)

    suspend fun deleteTransaction(transaction: LedgerTransaction) =
        dao.deleteTransaction(transaction)

    suspend fun saveSetting(key: String, value: String) =
        dao.saveSetting(AppSetting(key, value))

    suspend fun getSetting(key: String): String? = dao.getSetting(key)

    companion object {
        fun serializeOrderItems(items: List<OrderItem>): String {
            val jsonArray = JSONArray()
            for (item in items) {
                val obj = JSONObject().apply {
                    put("productId", item.productId)
                    put("productName", item.productName)
                    put("unitPrice", item.unitPrice)
                    put("quantity", item.quantity)
                    put("unit", item.unit)
                }
                jsonArray.put(obj)
            }
            return jsonArray.toString()
        }

        fun deserializeOrderItems(json: String?): List<OrderItem> {
            if (json.isNullOrBlank()) return emptyList()
            val list = mutableListOf<OrderItem>()
            try {
                val jsonArray = JSONArray(json)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        OrderItem(
                            productId = obj.optLong("productId"),
                            productName = obj.optString("productName"),
                            unitPrice = obj.optDouble("unitPrice"),
                            quantity = obj.optInt("quantity"),
                            unit = obj.optString("unit", "পিস")
                        )
                    )
                }
            } catch (_: Exception) { }
            return list
        }
    }
}
