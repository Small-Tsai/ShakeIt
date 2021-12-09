# 報茗牌 ShakeIt

<p align="center">
<img src="https://firebasestorage.googleapis.com/v0/b/shakeit-329401.appspot.com/o/SHAKEIT.png?alt=media&token=3716d909-65d4-4e29-a3fa-0f8525c99274" style="width:12%" /></center>
</p>

<p align="center">
   <img src="https://img.shields.io/badge/release-6.0-blue"> 
   <img src="https://img.shields.io/badge/platform-android-green"> 
</p>

<p align="center">A map that you can use several ways to search for nearby beverage shops which includes</p>
<p align="center">your transportation, traffic time or filter out your favorite stores</p>
<p align="center">then uses the menu system to create your orders. Also, you can share your orders by LINE messages</p>
<p align="center">and send notifications to other members who make the order together</p>

<p align="center">
<a href='https://play.google.com/store/apps/details?id=com.tsai.shakeit&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' style="width:25%;" /></a>
</p>

# 🧋Features


### Core features：

- Filter out the shop you like or search for the beverage you like
- Show your traffic time to the shop
- Navigation to the shop which you selected in the App
- Using menu system to create your order
- Leave a comment for the shop
- Share your orders with LINE messages
- Send notification to other members who make the order together

### ScreenShots：

- #### Home Page

>Filter out the shop and Search for the beverage 

<p>
<img src="ScreenShot/%E9%A1%AF%E7%A4%BA%E5%95%86%E5%AE%B6%E5%8E%BB%E8%83%8C.gif" alt="addShop" style="width:17%;" /> 
&nbsp&nbsp
<img src="ScreenShot/%E6%90%9C%E5%B0%8B%E5%8E%BB%E8%83%8C.gif" alt="4" style="width:17%" /> 
</p>

>Choose your transportation and setting your traffic time

<img src="https://github.com/s7025311/ShakeIt/blob/main/ScreenShot/%E6%99%82%E9%96%93%E5%8E%BB%E8%83%8C.gif?raw=true" alt="時間去背" style="width:17%;padding-left:100px" /> 

> Start navigation ( Walking or Driving )

<p>
<img src="https://firebasestorage.googleapis.com/v0/b/shakeit-329401.appspot.com/o/Blue Modern New App Promotion Instagram Post.svg?alt=media&token=796317ef-3ab2-4128-b934-f6c858e36c85" style="width:17%;padding-left:70px" />
&nbsp&nbsp
<img src="https://firebasestorage.googleapis.com/v0/b/shakeit-329401.appspot.com/o/carNAv.png?alt=media&token=63a44786-aa35-47a4-bad9-2561044c3887" style="width:17%;padding-left:36px" />
</p>

> Add new shop

<img src="ScreenShot/addShop.gif" alt="addShop" style="width:17%;" /> 

- #### Menu Page

> Select beverage which you want and choose amount of ice, sugar and capacity 

<p>
<img src="https://firebasestorage.googleapis.com/v0/b/shakeit-329401.appspot.com/o/Menu.png?alt=media&token=0c7cad16-118c-4e21-acee-e3d618a9d1d0" style="width:17%;padding-left:70px" />
&nbsp&nbsp
<img src="https://firebasestorage.googleapis.com/v0/b/shakeit-329401.appspot.com/o/detail.png?alt=media&token=235d795e-954e-447d-841d-dc75a6e7705a" style="width:17%;padding-left:50px" />
 </p>

> Share orders to your friends with LINE message

<img src="https://firebasestorage.googleapis.com/v0/b/shakeit-329401.appspot.com/o/line.png?alt=media&token=cf8b9365-21a4-41ad-838f-46530b5d649d" style="width:17%;" /> 

> Add new product

<img src="ScreenShot/newProduct.png" alt="newProduct" style="width:17%;" /> 

- #### Order Page

> Check and edit your order

<p>
<img src="ScreenShot/order.png" alt="order" style="width:17%;" />
&nbsp&nbsp
<img src="ScreenShot/addNew.gif" alt="addNew" style="width:17%;" />
 </p>

> Send notifications to other members who make the order together by FCM service

<p>
<img src="ScreenShot/sendNoti.gif" alt="sendNoti" style="width:17%;" />
&nbsp&nbsp
<img src="ScreenShot/backNoti.gif" alt="backNoti" style="width:17%;" />
</p>

> Complete order and leave comment 

<p>
<img src="ScreenShot/leaveComments.gif" alt="leaveComments" style="width:17%;" />
&nbsp&nbsp
<img src="ScreenShot/comment.png" alt="comment" style="width:17%;" />
</p>

- #### Favorite Page

  > Add to favorites 

<img src="ScreenShot/addFavoriteB.gif" alt="addFavoriteB" style="width:17%;"/> 

## :bulb: Technical Highlights

- Implemented MVVM pattern to make code further decoupled
- Deployed GoogleMaps SDK to embed maps inside the app and customized map markers style. Also customized map camera to move and build markerOnClick function to interact with BottomSheet
- Deployed FCM Service to subscribe app token on the server and received messages from the server to show notification even when App is closed
Share user orders on LINE by Deep-Links
- Control thread switching by Coroutine and Flow Get server data by Retrofit and parse it with Moshi
- Download images from URL and store them in the temporary storage by Glide
- Performed Unit Tests with JUnit and Mockito to ensure the App's stability

## Libraries

- [Notify-Android](https://github.com/Isradeleon/Notify-Android)
- [Image Picker](https://github.com/Dhaval2404/ImagePicker)
- [lottie-Android](https://github.com/airbnb/lottie-android)
- [PermissionX](https://github.com/guolindev/PermissionX)
- [Maps SDK for Android Utility Library](https://github.com/googlemaps/android-maps-utils)
- [SwipeRevealLayout](https://github.com/chthai64/SwipeRevealLayout)
- [Meow Bottom Navigation](https://github.com/oneHamidreza/MeowBottomNavigation)
- [Glide](https://github.com/bumptech/glide)
- [Mock K](https://github.com/mockk/mockk)

## Release Notes

| Version | Date | Description                                                                                     |
| :-------| :----|:------------------------------------------------------------------------------------------------|
| 6.0   | 2021.11.22 | Enable dark mode |
| 5.0   | 2021.11.22 | Minor bug fixes |
| 4.0   | 2021.11.21 | Minor bug fixes |
| 3.0   | 2021.11.21 | Update shop name data structure |
| 2.0   | 2021.11.20 | Set new logo |
| 1.0   | 2021.11.15 | Launched in google play Store|

## Requirement

- Android SDK 26

## Contact

TsungTing Tsai
[s7025311@gmail.com](s7025311@gmail.com)
