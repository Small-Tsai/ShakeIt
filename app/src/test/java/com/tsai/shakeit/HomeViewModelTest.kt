package com.tsai.shakeit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.PolylineOptions
import com.tsai.shakeit.app.DRIVING
import com.tsai.shakeit.app.WALKING
import com.tsai.shakeit.data.Favorite
import com.tsai.shakeit.data.Result
import com.tsai.shakeit.data.Shop
import com.tsai.shakeit.data.directionPlaceModel.*
import com.tsai.shakeit.data.source.DefaultShakeItRepository
import com.tsai.shakeit.ui.home.HomeViewModel
import com.tsai.shakeit.util.UserInfo
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @MockK
    lateinit var mockRepository: DefaultShakeItRepository

    private lateinit var viewModel: HomeViewModel

    @MockK
    lateinit var mockApplication: ShakeItApplication

    @MockK
    lateinit var user: UserInfo

    @RelaxedMockK
    lateinit var mockFavoriteListObserver: Observer<List<Favorite>>

    @RelaxedMockK
    lateinit var mockShopListObserver: Observer<List<Shop>>

    @RelaxedMockK
    lateinit var mockShopNameListObserver: Observer<List<String>>

    @RelaxedMockK
    lateinit var mockIsInMyFavoriteObserver: Observer<Boolean>

    @RelaxedMockK
    lateinit var mockSelectedShopObserver: Observer<Shop>

    @RelaxedMockK
    lateinit var mockShopSnippetObserver: Observer<String?>

    @RelaxedMockK
    lateinit var mockTrafficModeObserver: Observer<String>

    @RelaxedMockK
    lateinit var mockGetDirectionDoneObserver: Observer<Boolean>

    private val dispatcher = UnconfinedTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Before
    fun setUp() {

        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)
        ShakeItApplication.instance = mockApplication

        every { (ShakeItApplication.instance.isLiveDataDesign()) }.returns(false)

        user = UserInfo
        user.userId = "testId"
        user.userCurrentLat = 25.04146949999999
        user.userCurrentLng = 121.5656769
        user.userCurrentSettingTrafficTime = "60"
        user.userCurrentSelectTrafficMode = DRIVING

        viewModel = HomeViewModel(mockRepository)

        viewModel.favoriteList.observeForever(mockFavoriteListObserver)
        viewModel.shopListLiveData.observeForever(mockShopListObserver)
        viewModel.allShopName.observeForever(mockShopNameListObserver)
        viewModel.isInMyFavorite.observeForever(mockIsInMyFavoriteObserver)
        viewModel.selectedShop.observeForever(mockSelectedShopObserver)
        viewModel.selectedShopId.observeForever(mockShopSnippetObserver)
        viewModel.trafficMode.observeForever(mockTrafficModeObserver)
        viewModel.getDirectionDone.observeForever(mockGetDirectionDoneObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
    }

    @Test
    fun userInfo_isCorrect() {
        assertEquals("testId", user.userId)
        assertEquals(25.04146949999999, user.userCurrentLat)
        assertEquals(121.5656769, user.userCurrentLng)
        assertEquals(LatLng(25.04146949999999, 121.5656769), user.userCurrentLocation)
        assertEquals(DRIVING, user.userCurrentSelectTrafficMode)
        assertEquals("60", user.userCurrentSettingTrafficTime)
    }

    @Test
    fun calculateDistanceDefault_isCorrect() {
        assertEquals(18000.0, viewModel.distance)
    }

    @Test
    fun getMyFavorite_and_checkHasFavorite() = scope.runTest {

        every { mockRepository.getFavorite(any()) }.returns(flowOf(Result.Success(listOf())))

        viewModel.getMyFavorite(user.userId)
        viewModel.checkHasFavorite()

        verify { mockFavoriteListObserver.onChanged(listOf()) }
        assertEquals(false, viewModel.isInMyFavorite.getOrAwaitValue())
    }

    @Test
    fun getAllShop_and_checkShopNameTransForm_success() = scope.runTest {

        coEvery {
            mockRepository.getAllShop(any(), any())
        }.returns(flowOf(Result.Success(listOf())))

        viewModel.getShopData(user.userCurrentLocation)

        coVerify { mockShopListObserver.onChanged(listOf()) }
        coVerify { mockShopNameListObserver.onChanged(listOf()) }
    }

    @Test
    fun getSelectedShop_isCorrect() = scope.runTest {

        val testShopId = "testShop"
        val shopInfo = Shop(shop_Id = testShopId)
        val shopInfoList = listOf(shopInfo)

        // Get mockShopDataList
        coEvery { mockRepository.getAllShop(any(), any()) }
            .returns(flowOf(Result.Success(shopInfoList)))

        viewModel.getShopData(user.userCurrentLocation)
        verify { mockShopListObserver.onChanged(shopInfoList) }

        // Get ShopId
        viewModel.getSelectedShopSnippet(testShopId)
        verify { mockShopSnippetObserver.onChanged(testShopId) }
        assertEquals(testShopId, viewModel.selectedShopId.getOrAwaitValue())

        // Use mock snippet(shopId) to filter mockShopDataList
        viewModel.filterShopListByShopId(testShopId, shopInfoList)
        assertEquals(shopInfo, viewModel.selectedShop.getOrAwaitValue())
        verify { mockSelectedShopObserver.onChanged(shopInfo) }
    }

    @Test
    fun selectWalking_isCorrect() = scope.runTest {
        viewModel.selectWalk()
        assertEquals(WALKING, viewModel.trafficMode.getOrAwaitValue())
    }

    @Test
    fun selectDriving_isCorrect() = scope.runTest {
        viewModel.selectDriving()
        assertEquals(DRIVING, viewModel.trafficMode.getOrAwaitValue())
    }

    @Test
    fun getDirection_success() = scope.runTest {

        val url = "testUrl"
        val testDistance = "testDistance"
        val testDuration = "testDuration"

        val directionData =
            Direction(
                routes = listOf(
                    Route(
                        legs = listOf(
                            Leg(
                                distance = Distance(text = testDistance),
                                duration = Duration(text = testDuration)
                            )
                        )
                    )
                )
            )

        val navOption = PolylineOptions()

        coEvery { mockRepository.getDirection(url) }.returns(flowOf(Result.Success(directionData)))

        viewModel.getDirection(url, navOption)

        verify { mockGetDirectionDoneObserver.onChanged(true) }

        assertEquals(testDistance, viewModel.distanceLiveData.getOrAwaitValue())
        assertEquals(testDuration, viewModel.trafficTimeLiveData.getOrAwaitValue())
    }
}
