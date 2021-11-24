package com.tsai.shakeit.data.source.remote

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryBounds
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.maps.model.LatLng
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.*
import com.tsai.shakeit.R
import com.tsai.shakeit.data.*
import com.tsai.shakeit.data.directionPlaceModel.Direction
import com.tsai.shakeit.data.source.ShakeItDataSource
import com.tsai.shakeit.ext.mToast
import com.tsai.shakeit.network.ShakeItApi
import com.tsai.shakeit.ui.orderdetail.TOPIC
import com.tsai.shakeit.util.*
import com.tsai.shakeit.util.Util.isInternetConnected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object ShakeItRemoteDataSource : ShakeItDataSource {

    override suspend fun postFavorite(favorite: Favorite): Flow<Result<Boolean>> =
        flow {
            val favoriteCollection = FirebaseFirestore.getInstance().collection(FAVORITE)
            val document = favoriteCollection.document(favorite.shop.shop_Id)
            document.set(favorite)
            emit(Result.Success(true))
        }.flowOn(Dispatchers.IO).catch { Result.Fail(it.message.toString()) }

    override suspend fun postOrderToFireBase(
        order: Order,
        orderProduct: OrderProduct,
        otherUserId: String,
        hasOrder: Boolean
    ): Result<Boolean> =
        suspendCoroutine { continuation ->

            val myId = order.shop_Id.substring(0, 10) + UserInfo.userId.substring(0, 10)
            val otherId: String
            val orders = FirebaseFirestore.getInstance().collection(ORDERS)
            var document = orders.document(myId)

            if (otherUserId.isNotEmpty() && otherUserId != UserInfo.userId) {
                otherId = order.shop_Id.substring(0, 10) + otherUserId.substring(0, 10)
                document = orders.document(otherId)
                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC + otherId)
                document
                    .set(order, SetOptions.mergeFields("order_Price"))
            } else {
                if (!hasOrder) {
                    order.order_Id = myId
                    document.set(order)
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC + myId)
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

    override suspend fun deleteFavorite(shopId: String): Flow<Result<Boolean>> =
        flow {
            val favorite = FirebaseFirestore.getInstance().collection(FAVORITE)
            val document = favorite.document(shopId)
            document.delete()
            emit(Result.Success(true))
        }.flowOn(Dispatchers.IO).catch { Result.Fail(it.message.toString()) }

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

    override suspend fun getAllShop(center: LatLng, distance: Double): Flow<Result<List<Shop>>> =
        callbackFlow {

            if (!isInternetConnected()) {
                trySend(Result.Fail(Util.getString(R.string.internet_not_connected)))
                awaitClose()
            }

            trySend(Result.Loading)

            val shop = FirebaseFirestore.getInstance().collection(SHOP)
            val centerGeo = GeoLocation(center.latitude, center.longitude)
            val bounds: List<GeoQueryBounds> =
                GeoFireUtils.getGeoHashQueryBounds(centerGeo, distance)
            val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()

            for (b in bounds) {
                shop
                    .orderBy("geoPoint")
                    .startAt(b.startHash)
                    .endAt(b.endHash)
                tasks.add(shop.get())
            }

//            Collect all the query results together into a single list
            Tasks.whenAllComplete(tasks)
                .addOnCompleteListener { task ->
                    val matchingDocs: MutableList<Shop> = mutableListOf()
                    if (task.isSuccessful) {
                        for (mTask in tasks) {
                            val snap = mTask.result
                            for (doc in snap.documents) {
                                val lat = doc.getDouble("lat")!!
                                val lon = doc.getDouble("lon")!!
                                val docLocation = GeoLocation(lat, lon)
                                val distanceInM =
                                    GeoFireUtils.getDistanceBetween(docLocation, centerGeo)
                                if (distanceInM <= distance) {
                                    val shopDoc = doc.toObject(Shop::class.java)
                                    shopDoc?.let {
                                        matchingDocs.add(it)
                                    }
                                }
                            }
                        }
                        trySend(Result.Success(matchingDocs.distinct()))
                    }
                }
            awaitClose()
        }.flowOn(Dispatchers.IO).catch { Result.Fail(it.message.toString()) }


    override suspend fun getProduct(shop: Shop): Flow<Result<List<Product>>> =
        callbackFlow {

            trySend(Result.Loading)

            if (!isInternetConnected()) {
                trySend(Result.Fail(Util.getString(R.string.internet_not_connected)))
                awaitClose()
            }

            Logger.d(shop.name)
            val branchProduct = FirebaseFirestore.getInstance().collection(PRODUCT)
            val dbShop = FirebaseFirestore.getInstance().collection(SHOP)
            branchProduct
                .whereArrayContains("shop_Name", shop.name)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val shopData = task.result!!.toObjects(Product::class.java)
                        //update shopName to full name
                        updateShopName(shopData, shop, dbShop, task)
                        trySend(Result.Success(shopData))
                    }
                }
            awaitClose()
        }.flowOn(Dispatchers.IO).catch { Result.Fail(it.message.toString()) }

    private fun updateShopName(
        shopData: List<Product>,
        shop: Shop,
        dbShop: CollectionReference,
        task: Task<QuerySnapshot>
    ) {
        if (shopData.first().shop_Name.contains(shop.name) &&
            shop.name != shopData.first().shop_Name.last()
        ) {
            dbShop.document(shop.shop_Id)
                .update("name", shopData.first().shop_Name.last())
                .addOnCompleteListener {
                    if (task.isSuccessful) {
                        Logger.d("update shopName success")
                    } else {
                        Logger.w("update shopName fail")
                    }
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
            val otherId: String
            val order = FirebaseFirestore.getInstance().collection(ORDERS)
            var document = order.document(myId)

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

        if (!orderProduct.isNullOrEmpty()) {
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
//                            Logger.d(doc.id + " => " + doc.data)
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
                .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
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
//                            Logger.d(doc.id + " => " + doc.data)
                            val product = doc.toObject(OrderProduct::class.java)
                            list.add(product)
                        }
                        continuation.resume(Result.Success(list))
                    }
                }
        }

    override suspend fun getDirection(url: String): Flow<Result<Direction>> =
        flow {
            emit(Result.Loading)
            if (!isInternetConnected()) {
                emit(Result.Fail(Util.getString(R.string.internet_not_connected)))
            }

            val listResult = ShakeItApi.retrofitService.getDirection(url)

            emit(Result.Success(listResult))

        }.flowOn(Dispatchers.IO).catch { Result.Fail(it.message.toString()) }

    override suspend fun joinToOrder(orderId: String): Result<Boolean> =
        suspendCoroutine { continuation ->

            val orders = FirebaseFirestore.getInstance().collection(ORDERS)
            val document = orders.document(orderId)

            document
                .update("invitation", FieldValue.arrayUnion(UserInfo.userId))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "Error updateInvitation documents. ${it.message}"
                            )
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail("getComment Failed"))
                    }
                }
        }

    override suspend fun getAllProduct(): Flow<Result<List<Product>>> =
        callbackFlow {
            val product = FirebaseFirestore.getInstance().collection(PRODUCT)
            product
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val productData = task.result.toObjects(Product::class.java)
                        trySend(Result.Success(productData))
                    }
                }
            awaitClose()
        }.flowOn(Dispatchers.IO).catch { Result.Fail(it.message.toString()) }


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
                        continuation.resume(Result.Fail("postShopInfo Failed"))
                    }
                }
        }

    override suspend fun postImage(image: Uri): Result<String> =
        suspendCoroutine { continuation ->

            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("images/${image.lastPathSegment}")

            imageRef
                .putFile(image)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mToast("上傳中...")
                        imageRef.downloadUrl.addOnSuccessListener {
                            continuation.resume(Result.Success(it.toString()))
                        }
                    } else {
                        task.exception?.let {
                            Logger.w(
                                "Error updateFilterShop documents. ${it.message}"
                            )
                            continuation.resume(Result.Fail("上傳圖片失敗！"))
                            return@addOnCompleteListener
                        }
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
                        }
                        Logger.d("postUserInfo = fail")
                        continuation.resume(Result.Fail("postUser Failed"))
                    }
                }
        }

    override suspend fun createNewOrderForShare(order: Order): Result<Boolean> =
        suspendCoroutine { continuation ->

            val myId = order.shop_Id.substring(0, 10) + UserInfo.userId.substring(0, 10)
            val orders = FirebaseFirestore.getInstance().collection(ORDERS)
            val document = orders.document(myId)

            order.order_Id = myId

            document
                .set(order)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    }
                }
        }

    override fun getFilteredShopList(userId: String): MutableLiveData<List<String>> {

        val liveData = MutableLiveData<List<String>>()
//        Logger.d(userId)
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

    override fun getShopOrder(orderId: String): MutableLiveData<List<Order>> {

        Logger.d("shopId = $orderId")
        val liveData = MutableLiveData<List<Order>>()

        FirebaseFirestore.getInstance()
            .collection(ORDERS)
            .whereEqualTo("order_Id", orderId)
            .addSnapshotListener { snapshot, e ->

                val list = mutableListOf<Order>()

                if (snapshot != null) {
                    for (document in snapshot) {
//                        Logger.d( "Current data: ${document.data}")
                        val order = document.toObject(Order::class.java)
                        if (order.order_Id == orderId) {
//                            Logger.d("$order")
                            list.add(order)
                        }
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
                    val orderProduct = document.toObject(OrderProduct::class.java)
                    list.add(orderProduct)

                }
                liveData.value = list
            }
        return liveData
    }

    override fun getFavorite(userId: String): Flow<Result<List<Favorite>>> = callbackFlow {

        FirebaseFirestore.getInstance()
            .collection(FAVORITE)
            .whereEqualTo("user_Id", userId)
            .addSnapshotListener { snapshot, _ ->

                val list = mutableListOf<Favorite>()

                if (snapshot != null) {
                    for (document in snapshot) {
                        val shop = document.toObject(Favorite::class.java)
                        list.add(shop)
                    }
                }
                trySend(Result.Success(list))
            }
        awaitClose { Logger.i("get favorite await close") }
    }

    override fun updateUserTokenOnFireBase(newToken: String) {

        Logger.d("newToken = $newToken")
        val order = FirebaseFirestore.getInstance().collection(ORDERS)

        FirebaseFirestore.getInstance()
            .collection(ORDERS)
            .get()
            .addOnCompleteListener { task ->

                for (doc in task.result) {
                    order
                        .document(doc.id)
                        .collection(ORDER_PRODUCT)
                        .get()
                        .addOnCompleteListener { orderProduct ->

                            for (product in orderProduct.result) {
                                val mProduct = product.toObject(OrderProduct::class.java)
                                if (mProduct.user.user_Id == UserInfo.userId) {
                                    order
                                        .document(doc.id)
                                        .collection(ORDER_PRODUCT)
                                        .document(mProduct.orderProduct_Id)
                                        .update(
                                            "user", User(
                                                user_Id = UserInfo.userId,
                                                user_Name = UserInfo.userName,
                                                user_Image = UserInfo.userImage,
                                                user_Token = newToken
                                            )
                                        )
                                }
                            }
                        }
                }
            }
    }
}