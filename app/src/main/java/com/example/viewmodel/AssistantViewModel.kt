package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.model.Customer
import com.example.data.model.LedgerTransaction
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItem
import com.example.data.model.Party
import com.example.data.model.Product
import com.example.data.model.Supplier
import com.example.data.model.UserAccount
import com.example.data.repository.AssistantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PartyWithBalance(
    val party: Party,
    val totalGivenOrPurchase: Double, // Customer: মাল দিলাম / Supplier: পণ্য নিলাম
    val totalReceivedOrPayment: Double, // Customer: টাকা পেলাম / Supplier: পরিশোধ করলাম
    val totalReturn: Double = 0.0, // Supplier: পণ্য ফেরত
    val netBalance: Double // Positive = Owed to/by
)

data class ProductReportItem(
    val product: Product,
    val totalQuantityOrdered: Int,
    val totalSalesAmount: Double
)

data class DayOrderReport(
    val dateString: String,
    val orders: List<OrderEntity>,
    val totalAmount: Double,
    val productSummary: Map<String, Int> // ProductName -> Total Pieces
)

class AssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = AssistantRepository(db)

    val activeUser: StateFlow<UserAccount?> = repository.activeUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Admin Panel users monitoring
    val allUsers: StateFlow<List<UserAccount>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userCount: StateFlow<Int> = repository.userCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Direct Room entities
    val customerEntities: StateFlow<List<Customer>> = combine(repository.customersList, activeUser) { list, user ->
        if (user == null) emptyList()
        else if (user.isAdmin) list
        else list.filter { it.userId == user.id || it.userId == 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supplierEntities: StateFlow<List<Supplier>> = combine(repository.suppliersList, activeUser) { list, user ->
        if (user == null) emptyList()
        else if (user.isAdmin) list
        else list.filter { it.userId == user.id || it.userId == 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val productEntities: StateFlow<List<Product>> = combine(repository.productsList, activeUser) { list, user ->
        if (user == null) emptyList()
        else if (user.isAdmin) list
        else list.filter { it.userId == user.id || it.userId == 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orderEntities: StateFlow<List<OrderEntity>> = combine(repository.ordersList, activeUser) { list, user ->
        if (user == null) emptyList()
        else if (user.isAdmin) list
        else list.filter { it.userId == user.id || it.userId == 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers: StateFlow<List<Party>> = combine(repository.allCustomers, activeUser) { list, user ->
        if (user == null) emptyList()
        else if (user.isAdmin) list
        else list.filter { it.userId == user.id || it.userId == 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suppliers: StateFlow<List<Party>> = combine(repository.allSuppliers, activeUser) { list, user ->
        if (user == null) emptyList()
        else if (user.isAdmin) list
        else list.filter { it.userId == user.id || it.userId == 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<LedgerTransaction>> = combine(repository.allTransactions, activeUser) { list, user ->
        if (user == null) emptyList()
        else if (user.isAdmin) list
        else list.filter { it.userId == user.id || it.userId == 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<Product>> = combine(repository.allProducts, activeUser) { list, user ->
        if (user == null) emptyList()
        else if (user.isAdmin) list
        else list.filter { it.userId == user.id || it.userId == 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = combine(repository.allOrders, activeUser) { list, user ->
        if (user == null) emptyList()
        else if (user.isAdmin) list
        else list.filter { it.userId == user.id || it.userId == 0L }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated balances for Customers
    val customersWithBalance: StateFlow<List<PartyWithBalance>> = combine(
        repository.allCustomers,
        repository.allTransactions
    ) { customerList, txnList ->
        customerList.map { customer ->
            val custTxns = txnList.filter { it.partyId == customer.id }
            val given = custTxns.filter { it.type == "GIVEN" }.sumOf { it.amount }
            val received = custTxns.filter { it.type == "RECEIVED" }.sumOf { it.amount }
            val net = given - received // Positive = পাওনা (Receivable)
            PartyWithBalance(
                party = customer,
                totalGivenOrPurchase = given,
                totalReceivedOrPayment = received,
                totalReturn = 0.0,
                netBalance = net
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated balances for Suppliers
    val suppliersWithBalance: StateFlow<List<PartyWithBalance>> = combine(
        repository.allSuppliers,
        repository.allTransactions
    ) { supplierList, txnList ->
        supplierList.map { supplier ->
            val supTxns = txnList.filter { it.partyId == supplier.id }
            val purchase = supTxns.filter { it.type == "PURCHASE" }.sumOf { it.amount }
            val payment = supTxns.filter { it.type == "PAYMENT" }.sumOf { it.amount }
            val returnAmt = supTxns.filter { it.type == "RETURN" }.sumOf { it.amount }
            val net = purchase - payment - returnAmt // Positive = দেনা (Payable)
            PartyWithBalance(
                party = supplier,
                totalGivenOrPurchase = purchase,
                totalReceivedOrPayment = payment,
                totalReturn = returnAmt,
                netBalance = net
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Total Paona (মোট পাওনা / Customer dues)
    val totalPaona: StateFlow<Double> = customersWithBalance.combine(MutableStateFlow(0)) { list, _ ->
        list.sumOf { if (it.netBalance > 0) it.netBalance else 0.0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Total Dena (মোট দেনা / Supplier payables)
    val totalDena: StateFlow<Double> = suppliersWithBalance.combine(MutableStateFlow(0)) { list, _ ->
        list.sumOf { if (it.netBalance > 0) it.netBalance else 0.0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Auth State
    private val _otpState = MutableStateFlow<String?>(null)
    val otpState: StateFlow<String?> = _otpState.asStateFlow()

    private val _pendingRegistration = MutableStateFlow<UserAccount?>(null)
    val pendingRegistration: StateFlow<UserAccount?> = _pendingRegistration.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        // Guarantee no pre-logged in user on cold start, and ensure Admin account 01871752386 exists
        // No dummy or starter customers, suppliers, products, or transactions exist - completely clean slate!
        viewModelScope.launch {
            val adminPhone = "01871752386"
            val existingAdmin = repository.getUserByPhone(adminPhone)
            if (existingAdmin == null) {
                repository.insertUserDirect(
                    UserAccount(
                        nameBn = "প্রধান এডমিন",
                        nameEn = "Super Admin",
                        phone = adminPhone,
                        email = "admin@srassistant.com",
                        companyName = "S.R Assistant HQ",
                        password = "admin",
                        role = "ADMIN",
                        isLoggedIn = false // NOT pre-logged in
                    )
                )
            }
        }
    }

    // --- Authentication ---
    fun startRegistration(
        nameBn: String,
        nameEn: String,
        phone: String,
        email: String,
        companyName: String,
        password: String,
        confirmPass: String,
        onOtpReady: () -> Unit = {}
    ): Boolean {
        _authError.value = null
        val cleanPhone = phone.trim()
        if (nameBn.isBlank() || nameEn.isBlank() || cleanPhone.isBlank() || password.isBlank()) {
            _authError.value = "অনুগ্রহ করে সকল প্রয়োজনীয় তথ্য পূরণ করুন"
            return false
        }
        if (cleanPhone.length < 11) {
            _authError.value = "সঠিক ১১ ডিজিটের মোবাইল নম্বর লিখুন (যেমন: 017XXXXXXXX)"
            return false
        }
        if (password != confirmPass) {
            _authError.value = "পাসওয়ার্ড ও কনফার্ম পাসওয়ার্ড মিলছে না"
            return false
        }

        viewModelScope.launch {
            // One account per phone number check (একটি নাম্বার দিয়ে একটি একাউন্ট)
            val existing = repository.getUserByPhone(cleanPhone)
            if (existing != null) {
                _authError.value = "এই ফোন নম্বর দিয়ে ইতিমধ্যে একটি অ্যাকাউন্ট তৈরি করা হয়েছে। অনুগ্রহ করে লগইন করুন।"
                return@launch
            }

            val role = if (cleanPhone == "01871752386") "ADMIN" else "USER"
            val newUser = UserAccount(
                nameBn = nameBn.trim(),
                nameEn = nameEn.trim(),
                phone = cleanPhone,
                email = email.trim(),
                companyName = companyName.trim(),
                password = password,
                role = role,
                isLoggedIn = false
            )
            _pendingRegistration.value = newUser
            _otpState.value = "123456" // Standard demo OTP
            onOtpReady()
        }
        return true
    }

    fun verifyOtpAndCompleteRegistration(enteredOtp: String, onSuccess: () -> Unit) {
        if (enteredOtp == _otpState.value || enteredOtp == "123456") {
            val user = _pendingRegistration.value
            if (user != null) {
                viewModelScope.launch {
                    val existing = repository.getUserByPhone(user.phone)
                    if (existing != null) {
                        _authError.value = "এই ফোন নম্বর দিয়ে ইতিমধ্যে একটি অ্যাকাউন্ট রয়েছে। অনুগ্রহ করে লগইন করুন।"
                        return@launch
                    }
                    repository.registerUser(user)
                    _pendingRegistration.value = null
                    _otpState.value = null
                    _authError.value = null
                    onSuccess()
                }
            }
        } else {
            _authError.value = "ভুল ওটিপি কোড দেওয়া হয়েছে! (ডেমো কোড: ১২৩৪৫৬)"
        }
    }

    fun login(phone: String, pass: String, onSuccess: () -> Unit) {
        _authError.value = null
        val cleanPhone = phone.trim()
        if (cleanPhone.isBlank() || pass.isBlank()) {
            _authError.value = "ফোন নম্বর এবং পাসওয়ার্ড প্রদান করুন"
            return
        }

        viewModelScope.launch {
            val user = repository.getUserByPhone(cleanPhone)
            if (user != null) {
                if (user.password == pass) {
                    repository.loginUser(user.phone)
                    onSuccess()
                } else {
                    _authError.value = "ভুল পাসওয়ার্ড! আবার চেষ্টা করুন।"
                }
            } else {
                _authError.value = "এই ফোন নম্বরে কোনো অ্যাকাউন্ট পাওয়া যায়নি। অনুগ্রহ করে রেজিষ্ট্রেশন করুন।"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    // Password Reset (Forgot Password)
    private val _resetOtpState = MutableStateFlow<String?>(null)
    val resetOtpState: StateFlow<String?> = _resetOtpState.asStateFlow()

    fun requestPasswordReset(phone: String, onOtpSent: () -> Unit) {
        _authError.value = null
        val cleanPhone = phone.trim()
        if (cleanPhone.isBlank()) {
            _authError.value = "অনুগ্রহ করে আপনার রেজিষ্ট্রেশন করা মোবাইল নম্বর দিন"
            return
        }
        viewModelScope.launch {
            val user = repository.getUserByPhone(cleanPhone)
            if (user == null) {
                _authError.value = "এই ফোন নম্বরে কোনো অ্যাকাউন্ট পাওয়া যায়নি।"
            } else {
                _resetOtpState.value = "123456" // Standard demo OTP
                onOtpSent()
            }
        }
    }

    fun completePasswordReset(
        phone: String,
        enteredOtp: String,
        newPass: String,
        confirmPass: String,
        onSuccess: () -> Unit
    ) {
        _authError.value = null
        val cleanPhone = phone.trim()
        if (enteredOtp != _resetOtpState.value && enteredOtp != "123456") {
            _authError.value = "ভুল ওটিপি কোড দেওয়া হয়েছে! (ডেমো কোড: ১২৩৪৫৬)"
            return
        }
        if (newPass.isBlank() || confirmPass.isBlank()) {
            _authError.value = "নতুন পাসওয়ার্ড প্রদান করুন"
            return
        }
        if (newPass != confirmPass) {
            _authError.value = "নতুন পাসওয়ার্ড ও কনফার্ম পাসওয়ার্ড মিলছে না"
            return
        }

        viewModelScope.launch {
            val user = repository.getUserByPhone(cleanPhone)
            if (user != null) {
                repository.resetUserPassword(user.id, newPass)
                _resetOtpState.value = null
                _authError.value = null
                onSuccess()
            } else {
                _authError.value = "ব্যবহারকারী খুঁজে পাওয়া যায়নি"
            }
        }
    }

    // Admin Panel queries
    fun getPartiesByUser(userId: Long) = repository.getPartiesByUser(userId)
    fun getTransactionsByUser(userId: Long) = repository.getTransactionsByUser(userId)
    fun getProductsByUser(userId: Long) = repository.getProductsByUser(userId)
    fun getOrdersByUser(userId: Long) = repository.getOrdersByUser(userId)

    fun adminResetPassword(userId: Long, newPass: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.resetUserPassword(userId, newPass)
            onDone()
        }
    }

    fun updateLogo(uri: String) {
        viewModelScope.launch {
            repository.updateUserLogo(uri)
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    // --- Parties ---
    fun addParty(
        type: String,
        name: String,
        companyName: String?,
        phone: String,
        address: String?,
        photoUri: String? = null,
        initialBalance: Double = 0.0,
        initialNote: String = "পূর্বের বাকি/হিসাব",
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val currentUserId = activeUser.value?.id ?: 0L
            val partyId = repository.addParty(
                Party(
                    userId = currentUserId,
                    type = type,
                    name = name.trim(),
                    companyName = companyName?.trim()?.ifBlank { null },
                    phone = phone.trim(),
                    address = address?.trim()?.ifBlank { null },
                    photoUri = photoUri
                )
            )

            if (initialBalance > 0) {
                val txnType = if (type == "CUSTOMER") "GIVEN" else "PURCHASE"
                repository.addTransaction(
                    LedgerTransaction(
                        userId = currentUserId,
                        partyId = partyId,
                        partyType = type,
                        type = txnType,
                        amount = initialBalance,
                        description = initialNote
                    )
                )
            }
            onDone()
        }
    }

    fun updateParty(party: Party) {
        viewModelScope.launch {
            repository.updateParty(party)
        }
    }

    fun deleteParty(party: Party) {
        viewModelScope.launch {
            repository.deleteParty(party)
        }
    }

    // Direct Customer Entity CRUD
    fun addCustomer(customer: Customer, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val currentUserId = activeUser.value?.id ?: 0L
            repository.addCustomer(customer.copy(userId = currentUserId))
            onDone()
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    // Direct Supplier Entity CRUD
    fun addSupplier(supplier: Supplier, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val currentUserId = activeUser.value?.id ?: 0L
            repository.addSupplier(supplier.copy(userId = currentUserId))
            onDone()
        }
    }

    fun updateSupplier(supplier: Supplier) {
        viewModelScope.launch {
            repository.updateSupplier(supplier)
        }
    }

    fun deleteSupplier(supplier: Supplier) {
        viewModelScope.launch {
            repository.deleteSupplier(supplier)
        }
    }

    // --- Transactions ---
    fun addTransaction(
        partyId: Long,
        partyType: String,
        type: String,
        amount: Double,
        description: String,
        date: Long = System.currentTimeMillis(),
        billPhotoUri: String? = null,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val currentUserId = activeUser.value?.id ?: 0L
            repository.addTransaction(
                LedgerTransaction(
                    userId = currentUserId,
                    partyId = partyId,
                    partyType = partyType,
                    type = type,
                    amount = amount,
                    description = description.trim(),
                    date = date,
                    billPhotoUri = billPhotoUri
                )
            )
            onDone()
        }
    }

    fun deleteTransaction(transaction: LedgerTransaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // --- Products ---
    fun addProduct(
        name: String,
        price: Double,
        unit: String,
        photoUri: String? = null,
        stock: Int = 0,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val currentUserId = activeUser.value?.id ?: 0L
            repository.addProduct(
                Product(
                    userId = currentUserId,
                    name = name.trim(),
                    price = price,
                    unit = unit.trim().ifBlank { "পিস" },
                    photoUri = photoUri,
                    stock = stock
                )
            )
            onDone()
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // --- Orders ---
    fun createOrder(
        customerId: Long,
        customerName: String,
        customerPhone: String,
        items: List<OrderItem>,
        totalAmount: Double,
        deliveryDate: Long? = null,
        note: String? = null,
        status: String = "PENDING",
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val currentUserId = activeUser.value?.id ?: 0L
            repository.addOrder(
                OrderEntity(
                    userId = currentUserId,
                    customerId = customerId,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    orderDate = System.currentTimeMillis(),
                    deliveryDate = deliveryDate,
                    totalAmount = totalAmount,
                    status = status,
                    itemsJson = AssistantRepository.serializeOrderItems(items),
                    note = note
                )
            )
            onDone()
        }
    }

    fun updateOrderStatus(order: OrderEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(status = newStatus))
        }
    }

    fun deleteOrder(order: OrderEntity) {
        viewModelScope.launch {
            repository.deleteOrder(order)
        }
    }
}
