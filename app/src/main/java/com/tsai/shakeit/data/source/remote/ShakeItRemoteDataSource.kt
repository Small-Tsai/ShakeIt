package com.tsai.shakeit.data.source.remote

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.*
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.source.ShakeItDataSource
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import okhttp3.internal.wait
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val ORDERS = "orders"
private const val KEY_CREATED_TIME = "date"
private const val FAVORITE = "favorite"
private const val SHOP = "shop"
private const val ORDER_PRODUCT = "orderProduct"
private const val PRODUCT = "product"
private const val COMMENT = "Comment"
private const val FILTER_SHOP = "filterShop"
private const val USERS = "users"
private const val ORDER_HISTORY = "orderHistory"


object ShakeItRemoteDataSource : ShakeItDataSource {

    override suspend fun postFavorite(favorite: Favorite): Result<Boolean> =
        suspendCoroutine { continuation ->

            val favoriteCollection = FirebaseFirestore.getInstance().collection(FAVORITE)
            val document = favoriteCollection.document(favorite.shop.shop_Id)

            document
                .set(favorite)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Logger.d("Publish: $favorite")

                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("postFavorite Failed"))
                    }
                }
        }

    override suspend fun postOrderToFireBase(
        order: Order,
        orderProduct: OrderProduct,
        otherUserId: String,
        orderSize: Int
    ): Result<Boolean> =
        suspendCoroutine { continuation ->

            val myId = order.shop_Id.substring(0, 10) + UserInfo.userId.substring(0, 10)
            var otherId = ""

            val orders = FirebaseFirestore.getInstance().collection(ORDERS)
            var document = orders.document(myId)

            //當選擇的訂單UserId 不是本地Id則 document 導向該訂單Id 並且只修改price與product
            //else訂單Id = 本地Id 則直接set覆蓋
            if (otherUserId.isNotEmpty() && otherUserId != UserInfo.userId) {
                otherId = order.shop_Id.substring(0, 10) + otherUserId.substring(0, 10)
                document = orders.document(otherId)
                document
                    .set(order, SetOptions.mergeFields("order_Price"))
            } else {
                if (orderSize == 0) {
                    order.order_Id = myId
                    document.set(order)
                }
            }

            val orderProductDocument = document.collection(ORDER_PRODUCT).document()
            orderProduct.orderProduct_Id = orderProductDocument.id

            orderProductDocument
                .set(orderProduct)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error post documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("orderProduct Failed"))
                    }
                }
        }

    override suspend fun postProduct(product: Product): Result<Boolean> =
        suspendCoroutine { continuation ->

            val fireBaseProduct = FirebaseFirestore.getInstance().collection(PRODUCT)
            val document = fireBaseProduct.document()

            product.id = document.id

            document
                .set(product)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Logger.i("Product: $product")
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error post documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("Product Failed"))
                    }
                }


        }

    override suspend fun postComment(shopId: String, comment: Comment): Result<Boolean> =
        suspendCoroutine { continuation ->

            val shopCollection = FirebaseFirestore.getInstance().collection(SHOP)
            val document = shopCollection.document(shopId).collection(COMMENT).document()

            document
                .set(comment)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mToast("發佈成功！")
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error post comment. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("post comment Failed"))
                    }
                }
        }

    override suspend fun deleteFavorite(shopId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            val favorite = FirebaseFirestore.getInstance().collection(FAVORITE)
            val document = favorite.document(shopId)

            document
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mToast("已移除此收藏")
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error delete documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("deleteFavorite Failed"))
                    }
                }
        }

    override suspend fun deleteOrder(orderId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            val order = FirebaseFirestore.getInstance().collection(ORDERS)
            val document = order.document(orderId)


            document
                .collection(ORDER_PRODUCT)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.documents?.forEach { it.reference.delete() }
                        mToast("已移除訂單")
                        document.delete()
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error delete documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("deleteOrder Failed"))
                    }
                }
        }

    override suspend fun deleteOrderProduct(
        orderProductId: String,
        shopId: String,
        otherUserId: String
    ): Result<Boolean> =
        suspendCoroutine { continuation ->

            val myShopId = shopId.substring(0, 10) + UserInfo.userId.substring(0, 10)
            val otherShopId = shopId.substring(0, 10) + otherUserId.substring(0, 10)

            val orderProduct = FirebaseFirestore.getInstance().collection(ORDERS)


            var document =
                orderProduct.document(myShopId).collection(ORDER_PRODUCT).document(orderProductId)

            if (otherUserId != UserInfo.userId && otherUserId.isNotEmpty()) {
                Logger.d(otherShopId)
                Logger.d(orderProductId)
                document =
                    orderProduct
                        .document(otherShopId)
                        .collection(ORDER_PRODUCT)
                        .document(orderProductId)
            }

            document
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mToast("已移除商品")
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error delete product. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("deleteOrder Failed"))
                    }
                }
        }


    override suspend fun getShopInfo(shopId: String): Result<Shop> =
        suspendCoroutine { continuation ->

            val shopInfo = FirebaseFirestore.getInstance().collection(SHOP)
            val document = shopInfo.document(shopId)

            document
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val shopData = task.result!!.toObject(Shop::class.java)
                        continuation.resume(Result.Success(shopData!!))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error shopInfo documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("getShopInfo Failed"))
                    }
                }
        }

    override suspend fun getAllShop(): Result<List<Shop>> =
        suspendCoroutine { continuation ->

            val shop = FirebaseFirestore.getInstance().collection(SHOP)

            shop
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val shopData = task.result!!.toObjects(Shop::class.java)
                        continuation.resume(Result.Success(shopData))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error shopInfo documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("getShopInfo Failed"))
                    }
                }
        }

    override suspend fun getProduct(shopName: String): Result<List<Product>> =
        suspendCoroutine { continuation ->

            val branchProduct = FirebaseFirestore.getInstance().collection(PRODUCT)

            branchProduct
                .whereEqualTo("shop_Name", shopName)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val shopData = task.result!!.toObjects(Product::class.java)
                        continuation.resume(Result.Success(shopData))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error get documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("getProduct Failed"))
                    }
                }
        }

    override suspend fun updateOrderTotalPrice(
        totalPrice: Int,
        shopId: String,
        otherUserId: String
    ): Result<Boolean> =

        suspendCoroutine { continuation ->

            val myId = shopId.substring(0, 10) + UserInfo.userId.substring(0, 10)
            var otherId = ""
            val order = FirebaseFirestore.getInstance().collection(ORDERS)
            var document = order.document(myId)

            //當選擇的訂單UserId 不是本地Id則 document 導向該訂單Id 並且只修改price與product
            //else訂單Id = 本地Id 則直接set覆蓋
            if (otherUserId.isNotEmpty() && otherUserId != UserInfo.userId) {
                otherId = shopId.substring(0, 10) + otherUserId.substring(0, 10)
                document = order.document(otherId)
                document.update("order_Price", totalPrice)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(Result.Success(true))
                        } else {
                            task.exception?.let {
                                Logger.w(
                                    "[${this::class.simpleName}] Error update documents. ${it.message}"
                                )
                                return@addOnCompleteListener
                            }
                            continuation.resume(Result.Fail("update Failed"))
                        }
                    }

            } else {
                document
                    .update("order_Price", totalPrice)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(Result.Success(true))
                        } else {
                            task.exception?.let {
                                Logger.w(
                                    "[${this::class.simpleName}] Error update documents. ${it.message}"
                                )
                                return@addOnCompleteListener
                            }
                            continuation.resume(Result.Fail("update Failed"))
                        }
                    }
            }
        }

    override suspend fun getComment(shopId: String): Result<List<Comment>> =
        suspendCoroutine { continuation ->

            val comment = FirebaseFirestore.getInstance().collection(SHOP)
            val document = comment.document(shopId).collection(COMMENT)

            document
                .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val commentList = task.result.toObjects(Comment::class.java)
                        continuation.resume(Result.Success(commentList))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error getComment documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("getComment Failed"))
                    }
                }
        }

    override suspend fun postHistoryOrder(
        order: Order,
        orderProduct: List<OrderProduct>
    ): Result<Boolean> = suspendCoroutine { continuation ->

        val orderHistory = FirebaseFirestore.getInstance().collection(ORDER_HISTORY)
        val document = orderHistory.document()

        order.order_Id = document.id
        document.set(order)

        val last = orderProduct.last()
        orderProduct.forEach {
            val orderProductDocument = document.collection(ORDER_PRODUCT).document()
            it.orderProduct_Id = orderProductDocument.id
            orderProductDocument
                .set(it)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (it == last) {
                            continuation.resume(Result.Success(true))
                        }
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "[${this::class.simpleName}] Error post documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("post orderProduct Failed"))
                    }
                }
        }
    }

    override suspend fun getOrderProduct(orderId: String): Result<List<OrderProduct>> =
        suspendCoroutine { continuation ->

            val orderProduct = FirebaseFirestore.getInstance().collection(ORDERS)
            val document = orderProduct.document(orderId).collection(ORDER_PRODUCT)

            val list = mutableListOf<OrderProduct>()

            document
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        for (doc in task.result!!) {
                            Logger.d(doc.id + " => " + doc.data)
                            val product = doc.toObject(OrderProduct::class.java)
                            list.add(product)
                        }
                        continuation.resume(Result.Success(list))
                    }
                }
        }

    override suspend fun getOrderHistory(userId: String): Result<List<Order>> =
        suspendCoroutine { continuation ->

            val orderHistory = FirebaseFirestore.getInstance().collection(ORDER_HISTORY)

            val list = mutableListOf<Order>()

            orderHistory
                .whereEqualTo("user_Id", userId)
                .limit(10)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (doc in task.result!!) {
                            val order = doc.toObject(Order::class.java)
                            list.add(order)
                        }
                        continuation.resume(Result.Success(list))
                    }
                }
        }

    override suspend fun getHistoryOrderProduct(orderId: String): Result<List<OrderProduct>> =
        suspendCoroutine { continuation ->

            val orderProduct = FirebaseFirestore.getInstance().collection(ORDER_HISTORY)
            val document = orderProduct.document(orderId).collection(ORDER_PRODUCT)

            val list = mutableListOf<OrderProduct>()

            document
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        for (doc in task.result!!) {
                            Logger.d(doc.id + " => " + doc.data)
                            val product = doc.toObject(OrderProduct::class.java)
                            list.add(product)
                        }
                        continuation.resume(Result.Success(list))
                    }
                }
        }

    override suspend fun updateFilteredShop(shopList: FilterShop): Result<Boolean> =
        suspendCoroutine { continuation ->

            val filterShop = FirebaseFirestore.getInstance().collection(FILTER_SHOP)
            val document = filterShop.document(UserInfo.userId)

            document
                .set(shopList)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "Error updateFilterShop documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("getComment Failed"))
                    }
                }
        }

    override suspend fun postShopInfo(shop: Shop): Result<Boolean> =
        suspendCoroutine { continuation ->

            val shopCollection = FirebaseFirestore.getInstance().collection(SHOP)
            val document = shopCollection.document()

            shop.shop_Id = document.id

            document
                .set(shop)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "Error updateFilterShop documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("getComment Failed"))
                    }
                }
        }

    override suspend fun postImage(image: Uri): Result<String> =
        suspendCoroutine { continuation ->

            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images/${image.lastPathSegment}")
            Logger.d("上傳圖片中")
            imageRef
                .putFile(image)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        imageRef.downloadUrl.addOnSuccessListener {
                            continuation.resume(Result.Success(it.toString()))
                        }

                    } else {
                        task.exception?.let {
                            Logger.w(
                                "Error updateFilterShop documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        Logger.d("上傳圖片失敗！")
                        continuation.resume(Result.Fail("getComment Failed"))
                    }
                }
        }

    override suspend fun postUserInfo(user: User): Result<Boolean> =
        suspendCoroutine { continuation ->

            val users = FirebaseFirestore.getInstance().collection(USERS)
            val document = users.document(user.user_Id)

            document
                .set(user, SetOptions.merge())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "Error userInfo documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("postUser Failed"))
                    }
                }
        }

    override fun getFilteredShopList(userId: String): MutableLiveData<List<String>> {

        val liveData = MutableLiveData<List<String>>()
        Logger.d(userId)
        FirebaseFirestore.getInstance()
            .collection(FILTER_SHOP)
            .document(userId)
            .addSnapshotListener { snapshot, e ->

                val list = mutableListOf<String>()

                snapshot?.let {
                    val data = snapshot.toObject(FilterShop::class.java)
                    data?.filter_Shop?.forEach {
                        list.add(it)
                    }
                }

                liveData.value = list
            }
        return liveData
    }

    override fun getFireBaseOrder(userId: String): MutableLiveData<List<Order>> {

        val liveData = MutableLiveData<List<Order>>()

        FirebaseFirestore.getInstance()
            .collection(ORDERS)
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .whereArrayContains("invitation", userId)
            .addSnapshotListener { snapshot, e ->

                val list = mutableListOf<Order>()

                if (snapshot != null) {
                    for (document in snapshot) {
//                        Logger.d("Current data: ${document.data}")
                        val order = document.toObject(Order::class.java)
                        list.add(order)
                    }
                }

                liveData.value = list
            }
        return liveData
    }

    override fun getShopOrder(shopId: String): MutableLiveData<List<Order>> {

        val liveData = MutableLiveData<List<Order>>()

        FirebaseFirestore.getInstance()
            .collection(ORDERS)
            .whereEqualTo("shop_Id", shopId)
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->

                val list = mutableListOf<Order>()

                if (snapshot != null) {
                    for (document in snapshot) {
//                        Log.d(TAG, "Current data: ${document.data}")
                        val order = document.toObject(Order::class.java)
                        list.add(order)
                    }
                }
                liveData.value = list
            }
        return liveData
    }


    override fun getFireBaseOrderProduct(orderId: String): MutableLiveData<List<OrderProduct>> {


        val liveData = MutableLiveData<List<OrderProduct>>()

        FirebaseFirestore.getInstance()
            .collection(ORDERS)
            .document(orderId)
            .collection(ORDER_PRODUCT)
            .addSnapshotListener { snapshot, e ->

                val list = mutableListOf<OrderProduct>()

                for (document in snapshot!!) {
//                    Log.d(TAG, document.data.toString())
                    val orderProduct = document.toObject(OrderProduct::class.java)
                    list.add(orderProduct)

                }
                liveData.value = list
            }
        return liveData
    }

    override fun getFavorite(userId: String): MutableLiveData<List<Favorite>> {

        val liveData = MutableLiveData<List<Favorite>>()

        FirebaseFirestore.getInstance()
            .collection(FAVORITE)
            .whereEqualTo("user_Id", userId)
            .addSnapshotListener { snapshot, e ->

                val list = mutableListOf<Favorite>()

                if (snapshot != null) {
                    for (document in snapshot) {
//                        Log.d(TAG, "Current data: ${document.data}")
                        val shop = document.toObject(Favorite::class.java)
                        list.add(shop)
                    }
                }
                liveData.value = list
            }
        return liveData
    }
}