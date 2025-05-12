package com.nhm.distribution.screens.mainActivity

//import com.google.android.play.core.appupdate.AppUpdateManager
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory
//import com.google.android.play.core.install.InstallStateUpdatedListener
//import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
//import com.google.android.play.core.install.model.AppUpdateType
//import com.google.android.play.core.install.model.InstallStatus
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.nhm.distribution.R
import com.nhm.distribution.databinding.MainActivityBinding
import com.nhm.distribution.datastore.DataStoreKeys
import com.nhm.distribution.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distribution.datastore.DataStoreUtil
import com.nhm.distribution.datastore.DataStoreUtil.readData
import com.nhm.distribution.models.Login
import com.nhm.distribution.networking.ConnectivityManager
import com.nhm.distribution.networking.Main
import com.nhm.distribution.networking.Screen
import com.nhm.distribution.networking.Start
import com.nhm.distribution.networking.USER_TYPE
import com.nhm.distribution.networking.delete_account
import com.nhm.distribution.networking.mobile_number
import com.nhm.distribution.networking.page
import com.nhm.distribution.networking.userId
import com.nhm.distribution.networking.user_id
import com.nhm.distribution.networking.user_type
import com.nhm.distribution.screens.main.NBPA.forms.NBPA
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.locale
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.userIdForGlobal
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.userType
import com.nhm.distribution.screens.mainActivity.menu.JsonHelper
import com.nhm.distribution.utils.LocaleHelper
import com.nhm.distribution.utils.autoScroll
import com.nhm.distribution.utils.callNetworkDialog
import com.nhm.distribution.utils.callPermissionDialog
import com.nhm.distribution.utils.getDensityName
import com.nhm.distribution.utils.getSignature
import com.nhm.distribution.utils.imageZoom
import com.nhm.distribution.utils.ioThread
import com.nhm.distribution.utils.loadImage
import com.nhm.distribution.utils.mainThread
import com.nhm.distribution.utils.showSnackBar
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        var PACKAGE_NAME = ""

        @JvmStatic
        var SIGNATURE_NAME = ""

        @JvmStatic
        var isBackApp = false

        @JvmStatic
        var isBackStack = false

        @JvmStatic
        lateinit var context: WeakReference<Context>

        @JvmStatic
        lateinit var activity: WeakReference<Activity>

        @JvmStatic
        lateinit var mainActivity: WeakReference<MainActivity>

        var logoutAlert: AlertDialog? = null
        var deleteAlert: AlertDialog? = null

        @SuppressLint("StaticFieldLeak")
        var navHostFragment: NavHostFragment? = null

        private var _binding: MainActivityBinding? = null
        val binding get() = _binding!!

        @JvmStatic
        lateinit var isOpen: WeakReference<Boolean>

        @JvmStatic
        var scale10: Int = 0

        @JvmStatic
        var fontSize: Float = 0f

        @JvmStatic
        var networkFailed: Boolean = false
    }

    var backPressValue = 1

    private val viewModel: MainActivityVM by viewModels()

    private val connectivityManager by lazy { ConnectivityManager(this) }
//    private lateinit var appUpdateManager: AppUpdateManager

    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
//            Log.e("TAG", "AAAAgranted " + granted)

        } else {
//            Log.e("TAG", "BBBBgranted " + granted)
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SdCardPath", "MutableImplicitPendingIntent", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        val policy = ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .build()
        StrictMode.setThreadPolicy(policy)

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


//        val old = StrictMode.getThreadPolicy()
//        StrictMode.setThreadPolicy(
//            ThreadPolicy.Builder(old)
//                .permitDiskWrites()
//                .build()
//        )
////        doCorrectStuffThatWritesToDisk()
//        StrictMode.setThreadPolicy(old)

        super.onCreate(savedInstanceState)
        _binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        context = WeakReference(this)
        activity = WeakReference(this)
        mainActivity = WeakReference(this)
//        appUpdateManager = AppUpdateManagerFactory.create(this)

//        val appLinkIntent: Intent = intent
//        val appLinkAction: String? = appLinkIntent.action
//        val appLinkData: Uri? = appLinkIntent.data
//
//        Log.e("TAG", "appLinkIntent "+appLinkIntent)
//        Log.e("TAG", "appLinkAction "+appLinkAction)
//        Log.e("TAG", "appLinkData "+appLinkData)

        PACKAGE_NAME = packageName
        SIGNATURE_NAME = getSignature()

        if(LocaleHelper.getLanguage(applicationContext) == "ur"){
            binding.root.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            binding.root.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }

//        checkUpdate()

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        callMediaPermissions()

//        if (Build.VERSION.SDK_INT >= 33) {
//            pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//        }




        window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            window.decorView.getWindowVisibleDisplayFrame(r)
            val height = window.decorView.height
            if (height - r.bottom > height * 0.1399) {
                isOpen = WeakReference(true)
            } else {
                isOpen = WeakReference(false)
            }
//            WindowCompat.getInsetsController(window, window.decorView)
//            window.decorView.setSystemUiVisibility(View.VISIBLE)
        }

//        var ff = writeOnDrawable(R.drawable.image_port, "SASDSFG")
//        binding.ivLogoAA.setImageDrawable(ff)

//        val bm: Bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_port)
//            .copy(Bitmap.Config.ARGB_8888, true)
//
//        addStampToImage(bm)


//        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val startTime = "2025-04-12 00:00:00"
//        val startTimeInMillis = dateFormat.parse(startTime).time.toString()
//
//        val endTime = "2025-04-11 00:00:00"
//        val endTimeInMillis = dateFormat.parse(endTime).time.toString()
//
//        Log.e("TAG", "startTimeInMillis "+startTimeInMillis)
//        Log.e("TAG", "endTimeInMillis "+endTimeInMillis)
//
//        if(startTimeInMillis >= endTimeInMillis){
//            Log.e("TAG", "AAAAAA "+startTimeInMillis)
//        } else {
//            Log.e("TAG", "BBBBBB "+startTimeInMillis)
//        }

//        var aaaStart = "2025-04-11"
//        var bbbEnd = "2025-04-11"



//        loadBanner()
        viewModel.itemAds.observe(this@MainActivity, Observer {
            if (it != null) {
                viewModel.itemAds.value?.let { it1 ->
                    binding.apply {
                        viewModel.bannerAdapter.submitData(it1)
                        banner.adapter = viewModel.bannerAdapter
                        tabDots.setupWithViewPager(banner, true)

                        banner.autoScroll()
                        when (screenValue) {
                            0 -> layoutBanner.visibility = View.GONE
                            in 1..2 -> layoutBanner.visibility = View.VISIBLE
                        }
                    }
                }
            }
        })


        observeConnectivityManager()


        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                var user = Gson().fromJson(loginUser, Login::class.java)
                userIdForGlobal = ""+user.id
                userType = ""+user.user_role
            }
        }


//        var formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
//        var date = LocalDate.parse("31-12-2018", formatter)

//        val current1 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
//        val millis1: Long = SimpleDateFormat("yyyy-MM-dd").parse(current1.toString())?.time ?: 0
//        Log.e("TAG", "current1 "+millis1)
//
//        val current2 = LocalDate.parse("2024-04-19", DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
//        val millis2: Long = SimpleDateFormat("yyyy-MM-dd").parse(current2.toString())?.time ?: 0
//        Log.e("TAG", "current2 "+millis2)

//        var newVersion: String? = ""
//        try {
//            newVersion =
//                Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.streetsaarthi.nasvi")
//                    .timeout(3000)
//                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                    .referrer("http://www.google.com")
//                    .get()
//                    .select("div[itemprop=softwareVersion]")
//                    .first()
//                    ?.ownText()
////            if((curVersion) == (newVersion)){
////                storeVersion = "1.0.5"
////            }else{
////                storeVersion = "1.0.0"
////            }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
////            storeVersion = ""
//        }
//        Log.e("TAG", "current2 "+newVersion)


//        try {
//            val updateURL = URL("https://play.google.com/store/apps/details?id=com.streetsaarthi.nasvi")
//            val conn = updateURL.openConnection()
//            val `is` = conn.getInputStream()
//            val bis = BufferedInputStream(`is`)
//            val baf = ByteArrayBuffer(50)
//            var current = 0
//            while (bis.read().also { current = it } != -1) {
//                baf.append(current.toByte().toInt())
//            }
//
//            /* Convert the Bytes read to a String. */
//            val s: String = String(baf.toByteArray())
//
//            Log.e("TAG", "current2 "+s)
//
////            /* Get current Version Number */
////            val curVersion = packageManager.getPackageInfo("com.XXXX.YYYY", 0).versionCode
////            val newVersion = s.toInt()
////
////            /* Is a higher version than the current already out? */if (newVersion > curVersion) {
////                /* Post a Handler for the UI to pick up and open the Dialog */
////                val intent =
////                    Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:com.XXXX.YYYY"))
////                startActivity(intent)
////            }
//        } catch (e: java.lang.Exception) {
//            Toast.makeText(applicationContext, e.stackTrace.toString(), Toast.LENGTH_LONG).show()
//        }


//        val aaa = arrayOf(2,4,1,5,8,3)
//        var lar = aaa[0]
//        for (i in aaa.iterator()){
//            if(lar < i){
//                lar = i
//            }
//        }
//        Log.e("TAG", "larlar "+lar)


//        getToken(){
//            Log.e("TAG", "getToken "+this)
//            saveData(DataStoreKeys.TOKEN, this)
//        }

//        val sd = 12345678.066
//
//        val sdd = sd.toDouble().roundOffDecimal()
//        Log.e("TAG", "getTokensdd "+sdd)
//        val sddd = String.format("%.2f", sdd)
//        Log.e("TAG", "getTokensddd "+sddd)

//        val value = 1217
//        val year = value / 365
//        val month = (value % 365) / 30
//        val week = ((value % 365) % 30) / 7
//        val day = ((value % 365) % 30) % 7
//        var totalValues = ""
//        if(year == 1) {
//            totalValues += "$year Year "
//        } else if(year >= 2) {
//            totalValues += "$year Years "
//        }
//
//        if(month == 1) {
//            totalValues += "$month Month "
//        } else if(month >= 2) {
//            totalValues += "$month Months "
//        }
//
//        if(week == 1) {
//            totalValues += "$week Week "
//        } else if(week >= 2) {
//            totalValues += "$week Weeks "
//        }
//
//        if(day == 1) {
//            totalValues += "$day Day"
//        } else if(day >= 2) {
//            totalValues += "$day Days"
//        }
//        Log.e("TAG", "totalValuesZZ "+totalValues)

//        var totalValues = ""
//        if(dd.toString().contains(".")){
//            val aa = dd.toString().split(".")[0]
//            val bb = dd.toString().split(".")[1]
//            if(aa.toDouble() > 0){
//                val return_val_in_english1 = EnglishNumberToWords.convertToIndianCurrency(aa)
//                Log.e("TAG", "return_val_in_english1 "+return_val_in_english1)
//                totalValues += return_val_in_english1 +" Rupees "
//            }
//            if(bb.toDouble() > 0){
//                val return_val_in_english2 = EnglishNumberToWords.convertToIndianCurrency(bb)
//                Log.e("TAG", "return_val_in_english2 "+return_val_in_english2)
//                totalValues += "And "
//                totalValues += return_val_in_english2 +" Paise"
//            }
//        } else {
//            val aa = dd.toString().split(".")[0]
//            if(aa.toDouble() > 0){
//                val return_val_in_english1 = EnglishNumberToWords.convertToIndianCurrency(aa)
//                Log.e("TAG", "return_val_in_english3 "+return_val_in_english1)
//                totalValues += return_val_in_english1 +" Rupees"
//            }
//        }

//        val dd = 1078009.34
//        val return_val_in_english1 = EnglishNumberToWords.convertToIndianCurrency2(dd)
//
//        Log.e("TAG", "return_val_in_english "+return_val_in_english1)



        val bundle = intent?.extras
        if (bundle != null) {
            showData(bundle)
        }

        binding.apply {
            val manager = packageManager
            val info = manager?.getPackageInfo(packageName, 0)
            val versionName = info?.versionName
            textVersion.text = getString(R.string.app_version_1_0, versionName)
            btLogout.singleClick {
                callLogoutDialog()
            }


            val mDrawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this@MainActivity, drawerLayout,
                R.string.open, R.string.close
            ) {
                override fun onDrawerClosed(view: View) {
                    super.onDrawerClosed(view)
                }

                override fun onDrawerOpened(drawerView: View) {
                    super.onDrawerOpened(drawerView)
                }
            }
            drawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            textTitleMain.singleClick {
                drawerLayout.close()
            }

            topLayout.ivMenu.singleClick {
                 if (backPressValue == 1) {
                     drawerLayout.open()
                 } else {
                     Log.e("TAG", "backPressValue "+backPressValue)
                     Companion.navHostFragment?.navController?.navigateUp()
                 }
            }


            mainThread {
                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val data = Gson().fromJson(loginUser, Login::class.java)
                        userIdForGlobal = ""+data.id
                        userType = ""+data.user_role
                        viewModel.itemMain =
                            JsonHelper(MainActivity.context.get()!!).getMenuData(locale, data.user_role)
                    }
                    recyclerView.setHasFixedSize(true)
                    recyclerView.adapter = viewModel.menuAdapter
                    viewModel.menuAdapter.submitList(viewModel.itemMain)
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                }
            }

        }




        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            @SuppressLint("UnsafeImplicitIntentLaunch")
            override fun handleOnBackPressed() {
                val backStackEntryCount = navHostFragment?.childFragmentManager?.backStackEntryCount
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.close()
                } else {
                    if (backStackEntryCount == 0) {
                        val setIntent = Intent(Intent.ACTION_MAIN)
                        setIntent.addCategory(Intent.CATEGORY_HOME)
                        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(setIntent)
                    } else {
                        if (isBackStack) {
                            isBackStack = false
                            val navOptions: NavOptions = NavOptions.Builder()
                                .setPopUpTo(R.id.navigation_bar, true)
                                .build()
                            runOnUiThread {
                                navHostFragment?.navController?.navigate(
                                    R.id.dashboard,
                                    null,
                                    navOptions
                                )
                            }
                        } else {
                            val fragmentInFrame =
                                navHostFragment!!.getChildFragmentManager().getFragments().get(0)
                            Log.e("TAG", "fragmentInFrame "+fragmentInFrame)
                            if (fragmentInFrame is NBPA) {
                                MaterialAlertDialogBuilder(this@MainActivity, R.style.LogoutDialogTheme)
                                    .setTitle(resources.getString(R.string.app_name))
                                    .setMessage(resources.getString(R.string.are_your_sure_want_to_exit))
                                    .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                                        dialog.dismiss()
                                        navHostFragment?.navController?.navigateUp()
                                    }
                                    .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .setCancelable(false)
                                    .show()
                            } else {
                                navHostFragment?.navController?.navigateUp()
                            }
                        }
                    }
                }
            }
        })
    }


    @SuppressLint("SuspiciousIndentation")
    fun callLogoutDialog() {
        if (logoutAlert?.isShowing == true) {
            return
        }

        logoutAlert = MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
            .setTitle(resources.getString(R.string.app_name))
            .setMessage(resources.getString(R.string.are_your_sure_want_to_logout))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                        requestBody.addFormDataPart(
                            mobile_number,
                            "" + Gson().fromJson(loginUser, Login::class.java).mobile_no
                        )
                        if (networkFailed) {
                            viewModel.logoutAccount(requestBody.build())
                        } else {
                            callNetworkDialog()
                        }
                    }
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
            }
            .setCancelable(false)
            .show()


        viewModel.itemLogoutResult.value = false
        viewModel.itemLogoutResult.observe(this@MainActivity, Observer {
            if (it) {
                clearData()
            }
        })
    }


    @SuppressLint("SuspiciousIndentation")
    fun callDeleteDialog() {
        if (deleteAlert?.isShowing == true) {
            return
        }
        deleteAlert = MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
            .setTitle(resources.getString(R.string.app_name))
            .setMessage(resources.getString(R.string.are_your_sure_want_to_delete))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(user_type, USER_TYPE)
                        requestBody.addFormDataPart(
                            user_id,
                            "" + Gson().fromJson(loginUser, Login::class.java).id
                        )
                        requestBody.addFormDataPart(delete_account, "Yes")
                        if (networkFailed) {
                            viewModel.deleteAccount(requestBody.build())
                        } else {
                            callNetworkDialog()
                        }
                    }
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
            }
            .setCancelable(false)
            .show()

        viewModel.itemDeleteResult.value = false
        viewModel.itemDeleteResult.observe(this@MainActivity, Observer {
            if (it) {
                showSnackBar(getString(R.string.request_delete))
                clearData()
            }
        })
    }


    @SuppressLint("SuspiciousIndentation")
    fun callSesstionDialog() {
        if (deleteAlert?.isShowing == true) {
            return
        }
        deleteAlert = MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
            .setTitle(resources.getString(R.string.app_name))
            .setMessage(resources.getString(R.string.are_your_sure_want_to_delete))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(user_type, USER_TYPE)
                        requestBody.addFormDataPart(
                            user_id,
                            "" + Gson().fromJson(loginUser, Login::class.java).id
                        )
                        requestBody.addFormDataPart(delete_account, "Yes")
                        if (networkFailed) {
                            viewModel.deleteAccount(requestBody.build())
                        } else {
                            callNetworkDialog()
                        }
                    }
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.drawerLayout.close()
                }, 500)
            }
            .setCancelable(false)
            .show()

        viewModel.itemDeleteResult.value = false
        viewModel.itemDeleteResult.observe(this@MainActivity, Observer {
            if (it) {
                showSnackBar(getString(R.string.request_delete))
                clearData()
            }
        })
    }


    fun clearData() {
        DataStoreUtil.removeKey(DataStoreKeys.LOGIN_DATA) {}
        DataStoreUtil.removeKey(DataStoreKeys.AUTH) {}
        DataStoreUtil.removeKey(DataStoreKeys.LIVE_SCHEME_DATA) {}
        DataStoreUtil.removeKey(DataStoreKeys.LIVE_NOTICE_DATA) {}
        DataStoreUtil.removeKey(DataStoreKeys.LIVE_TRAINING_DATA) {}
        DataStoreUtil.removeKey(DataStoreKeys.Complaint_Feedback_DATA) {}
        DataStoreUtil.removeKey(DataStoreKeys.Information_Center_DATA) {}
        DataStoreUtil.clearDataStore { }
        callBack()
        val navOptions: NavOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_bar, true)
            .build()
        runOnUiThread {
            navHostFragment?.navController?.navigate(R.id.onboard, null, navOptions)
        }
    }


    public override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.extras != null) {
            showData(intent?.extras!!)
        }
    }


    private fun showData(bundle: Bundle) {
        try {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.close()
            }
                val navOptions: NavOptions = NavOptions.Builder()
                .setPopUpTo(R.id.navigation_bar, true)
                .build()
            if (intent!!.hasExtra(Screen)) {
                val screen = intent.getStringExtra(Screen)
//                Log.e("TAG", "screenAA " + screen)
                if (screen == Main) {
                    binding.topLayout.topToolbar.visibility = View.VISIBLE
                }
                callBack()
                if (screen == Start) {
                    navHostFragment?.navController?.navigate(R.id.start, null, navOptions)
                } else if (screen == Main) {
                    if (bundle?.getString("key") != null) {
                        callRedirect(bundle)
                    } else {
//                        Log.e("key", "showDataBB ")
//                        Log.e("_id", "showDataBB ")
                        navHostFragment?.navController?.navigate(R.id.dashboard, null, navOptions)
                    }
                }
            } else {
//                Log.e("TAG", "screenBB ")
                if (bundle?.getString("key") != null) {
                    callRedirect(bundle)
                }
            }
        } catch (e: Exception) {
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun callRedirect(bundle: Bundle) {
        val key = bundle?.getString("key")
        val _id = bundle?.getString("_id")
//        Log.e("key", "showDataAA " + key)
//        Log.e("_id", "showDataAA " + _id)

        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val data = Gson().fromJson(loginUser, Login::class.java)
                when (data.status) {
                    "approved" -> {
                        isBackStack = true
                        when (key) {
                            "profile" -> navHostFragment!!.navController.navigate(R.id.profiles)
                            "membership" -> navHostFragment!!.navController.navigate(R.id.subscription)
                            "scheme" -> {
                                when(data.subscription_status) {
                                    null -> navHostFragment!!.navController.navigate(R.id.liveSchemes)
                                    "trial" -> navHostFragment!!.navController.navigate(R.id.liveSchemes)
                                    "active" -> navHostFragment!!.navController.navigate(R.id.liveSchemes)
                                    "expired" -> showSnackBar(resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
                                }
                            }
                            "notice" -> {
                                when(data.subscription_status) {
                                    null -> navHostFragment!!.navController.navigate(R.id.liveNotices)
                                    "trial" -> navHostFragment!!.navController.navigate(R.id.liveNotices)
                                    "active" -> navHostFragment!!.navController.navigate(R.id.liveNotices)
                                    "expired" -> showSnackBar(resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
                                }
                            }
                            "training" -> {
                                when(data.subscription_status) {
                                    null -> navHostFragment!!.navController.navigate(R.id.liveTraining)
                                    "trial" -> navHostFragment!!.navController.navigate(R.id.liveTraining)
                                    "active" -> navHostFragment!!.navController.navigate(R.id.liveTraining)
                                    "expired" -> showSnackBar(resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
                                }
                            }
                            "information" -> {
                                when(data.subscription_status) {
                                    null -> navHostFragment!!.navController.navigate(R.id.informationCenter)
                                    "trial" -> navHostFragment!!.navController.navigate(R.id.informationCenter)
                                    "active" -> navHostFragment!!.navController.navigate(R.id.informationCenter)
                                    "expired" -> showSnackBar(resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
                                }
                            }

                            "feedback" -> {
                                when(data.subscription_status) {
                                    null -> navHostFragment!!.navController.navigate(
                                        R.id.historyDetail,
                                        Bundle().apply {
                                            putString("key", _id)
                                        })
                                    "trial" -> navHostFragment!!.navController.navigate(
                                        R.id.historyDetail,
                                        Bundle().apply {
                                            putString("key", _id)
                                        })
                                    "active" -> navHostFragment!!.navController.navigate(
                                        R.id.historyDetail,
                                        Bundle().apply {
                                            putString("key", _id)
                                        })
                                    "expired" -> showSnackBar(resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
                                }
                            }
                        }
                    }

                    "unverified" -> {
                        showSnackBar(resources.getString(R.string.registration_processed))
                    }

                    "pending" -> {
                        if (key == "profile") {
                            isBackStack = true
                            navHostFragment!!.navController.navigate(R.id.profiles)
                        } else {
                            showSnackBar(resources.getString(R.string.registration_processed))
                        }
                    }

                    "rejected" -> {
                        if (key == "profile") {
                            isBackStack = true
                            navHostFragment!!.navController.navigate(R.id.profiles)
                        } else {
                            showSnackBar(resources.getString(R.string.registration_processed))
                        }
                    }
                }
            }
        }


//        else -> {
//            isBackStack = true
//            val status = bundle?.getString("status")
//            Log.e("TAG", "statusAZ "+status)
//            when (status) {
//                "profile" -> {
//                    navHostFragment!!.navController.navigate(R.id.profiles)
//                }
//                else -> showSnackBar(resources.getString(R.string.registration_processed))
//            }
//        }

    }


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, ""))
    }


    fun callBack() {
        binding.apply {
            readData(LOGIN_DATA) { loginUser ->
                if (loginUser == null) {
                    topLayout.topToolbar.visibility = View.GONE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    layoutBanner.visibility = View.GONE
                    textHeaderTxt1.visibility = View.GONE
                } else {
                    topLayout.topToolbar.visibility = View.VISIBLE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                    var imageUrl = ""
                    try {
                        val imageUrlLogin = Gson().fromJson(loginUser, Login::class.java)
                        if (imageUrlLogin != null) {
                            val imageUrlName = imageUrlLogin.profile_image_name
                            imageUrl = imageUrlName.url ?: ""
                        }
                    } catch (_: Exception) {
                    }
                    topLayout.ivImage.loadImage(type = 1, url = { imageUrl })
                    topLayout.ivImage.singleClick {
                        arrayListOf(imageUrl).imageZoom(topLayout.ivImage, 2)
                    }
                }
            }
//            loadBanner()
        }
    }


    var screenValue = 0
    fun callFragment(screen: Int) {
        screenValue = screen
        binding.apply {
            when (screen) {
                0 -> {
                    backPressValue = 1
                    textHeaderTxt1.visibility = View.GONE
                    layoutBanner.visibility = View.GONE
                }

                1 -> {
                    backPressValue = 1
                    textHeaderTxt1.visibility = View.VISIBLE
                    mainLayout.setBackgroundResource(R.color.white)
                    viewModel.itemAds.value?.let { it1 ->
                        if (it1.size > 0) {
                            if (screen == 1) {
                                layoutBanner.visibility = View.VISIBLE
                            } else {
                                layoutBanner.visibility = View.GONE
                            }
                        } else {
                            layoutBanner.visibility = View.GONE
                        }
                    }
                }

                2 -> {
                    backPressValue = 1
                    textHeaderTxt1.visibility = View.VISIBLE
                    mainLayout.setBackgroundResource(R.color._FFF3E4)
                    viewModel.itemAds.value?.let { it1 ->
                        if (it1.size > 0) {
                            if (screen == 2) {
                                layoutBanner.visibility = View.VISIBLE
                            } else {
                                layoutBanner.visibility = View.GONE
                            }
                        } else {
                            layoutBanner.visibility = View.GONE
                        }
                    }
                }

                3 -> {
                    backPressValue = 2
                    textHeaderTxt1.visibility = View.VISIBLE
                    mainLayout.setBackgroundResource(R.color.white)
                    topLayout.ivMenu.setImageResource(R.drawable.arrow_back_icon)
                    viewModel.itemAds.value?.let { it1 ->
                        if (it1.size > 0) {
                            if (screen == 1) {
                                layoutBanner.visibility = View.VISIBLE
                            } else {
                                layoutBanner.visibility = View.GONE
                            }
                        } else {
                            layoutBanner.visibility = View.GONE
                        }
                    }
                }

                4 -> {
                    backPressValue = 1
                    textHeaderTxt1.visibility = View.VISIBLE
                    mainLayout.setBackgroundResource(R.color.white)
                    topLayout.ivMenu.setImageResource(R.drawable.material_symbols_menu)
                    viewModel.itemAds.value?.let { it1 ->
                        if (it1.size > 0) {
                            if (screen == 1) {
                                layoutBanner.visibility = View.VISIBLE
                            } else {
                                layoutBanner.visibility = View.GONE
                            }
                        } else {
                            layoutBanner.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun requestLocationUpdates() {
        Log.e("TAG","requestLocationUpdates")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return

        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    @SuppressLint("SetTextI18n")
    private fun displayCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            Log.e("TAG", "addOnSuccessListener "+location.toString())
//            location?.let {
//                val address = getAddress(location.latitude, location.longitude)
//                val city = address.locality
//                val province = address.adminArea
//                val country = address.countryName
//
//                cityCountryTv.text = "$city, $province, $country"
//
//                findViewById<TextView>(R.id.long_lat_tv).text  = getSharedPreferences("LatLongSelection", Context.MODE_PRIVATE).toString()
//                val selectedLatLongText = sharedPreferencesLatLong.getString("selected_lat_long_text", getString(R.string.lat_long))
//
//                updateLatLongText(selectedLatLongText ?: getString(R.string.lat_long))
//
//                val currentDate =
//                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
//                val currentDay = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
//                val currentTime =
//                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
//
//                findViewById<TextView>(R.id.date_day_time_tv).text = "$currentDate, $currentDay, $currentTime"
//
//                findViewById<TextView>(R.id.plus_code_tv_main_location_details_card).text = getSharedPreferences("PLusCodeSelection", Context.MODE_PRIVATE).toString()
//
//                val selectedPlusCodeText = sharedPreferencesPlusCode.getString("selected_plus_code_text", getString(R.string.lat_long))
//
//                updatePlusCodeText(selectedPlusCodeText ?: getString(R.string.lat_long))
//
//                findViewById<TextView>(R.id.time_zone_tv_main_template).text = getSharedPreferences("TimeZoneSelection", Context.MODE_PRIVATE).toString()
//
//                val selectedTimeZoneText = sharedPreferencesTimeZone.getString("selected_time_zone_text", getString(R.string.lat_long))
//
//                updateTimeZoneText(selectedTimeZoneText ?: getString(R.string.lat_long))
//
//                findViewById<TextView>(R.id.weather_tv_main_template_card).text = getSharedPreferences("WeatherSelection", Context.MODE_PRIVATE).toString()
//
//                val selectedWeatherText = sharedPreferencesWeatherSelection.getString("selected_weather_text", getString(R.string.lat_long))
//
//                updateWeatherText(selectedWeatherText ?: getString(R.string.lat_long))
//
//                findViewById<TextView>(R.id.wind_tv_main_template).text = getSharedPreferences("WindSelection", Context.MODE_PRIVATE).toString()
//
//                val selectedWindText = sharedPreferencesWindSelection.getString("selected_wind_text", getString(R.string.lat_long))
//
//                updateWindText(selectedWindText ?: getString(R.string.lat_long))
//
//                findViewById<TextView>(R.id.pressure_tv_main_template).text = getSharedPreferences("PressureSelection", Context.MODE_PRIVATE).toString()
//
//                val selectedPressureText = sharedPreferencesPressureSelection.getString("selected_pressure_text", getString(R.string.lat_long))
//
//                updatePressureText(selectedPressureText ?: getString(R.string.lat_long))
//
//                findViewById<TextView>(R.id.altitude_tv_main_template).text = getSharedPreferences("AltitudeSelection", Context.MODE_PRIVATE).toString()
//
//                val selectedAltitudeText = sharedPreferencesAltitudeText.getString("selected_altitude_text", getString(R.string.lat_long))
//
//                updateAltitudeText(selectedAltitudeText ?: getString(R.string.lat_long))
//
//                findViewById<TextView>(R.id.accuracy_tv_template_main).text = getSharedPreferences("AccuracySelection", Context.MODE_PRIVATE).toString()
//
//                val selectedAccuracyText = sharedPreferencesAccuracySelection.getString("selected_accuracy_text", getString(R.string.lat_long))
//
//                updateAccuracyText(selectedAccuracyText ?: getString(R.string.lat_long))
//
//
//            }
//            val mapFragment =
//                supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
//            mapFragment.getMapAsync { googleMap ->
//                // Add a marker at the current location and move the camera
//                val latLng = location?.let { LatLng(it.latitude, location.longitude) }
//                latLng?.let { MarkerOptions().position(it).title("Marker") }
//                    ?.let { googleMap.addMarker(it) }
//                latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
//                    ?.let { googleMap.moveCamera(it) }
//            }
        }
    }



    private fun observeConnectivityManager() = try {
        connectivityManager.observe(this) {
            binding.tvInternet.isVisible = !it
            networkFailed = it
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }


//    var resultUpdate =
//        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
//            Log.e("TAG", "result.resultCode " + result.resultCode)
//            if (result.resultCode == RESULT_OK) {
//                // Handle successful app update
//                Log.e("TAG", "RESULT_OK")
//            } else if (result.resultCode == RESULT_CANCELED) {
////                finish()
//            } else if (result.resultCode == RESULT_IN_APP_UPDATE_FAILED) {
////                finish()
//            } else {
////                finish()
//            }
//        }


//    private fun checkUpdate() {
//        appUpdateManager
//            .appUpdateInfo
//            .addOnSuccessListener { appUpdateInfo ->
//                val starter =
//                    IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
//                        val request = IntentSenderRequest.Builder(intent)
//                            .setFillInIntent(fillInIntent)
//                            .setFlags(flagsValues, flagsMask)
//                            .build()
//                        resultUpdate.launch(request)
//                    }
//
//                appUpdateManager.startUpdateFlowForResult(
//                    appUpdateInfo,
//                    AppUpdateType.IMMEDIATE,
//                    starter,
//                    123,
//                )
//            }
//    }


    fun loadBanner() {
        if (viewModel.itemAds.value == null) {
            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null) {
                    viewModel.adsList()
                }
            }
        }
    }


    fun reloadActivity(language: String, screen: String) {
        LocaleHelper.setLocale(this, language)
        val refresh = Intent(Intent(this, MainActivity::class.java))
        refresh.putExtra(Screen, screen)
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        finish()
        finishAffinity()
        startActivity(refresh)
    }


    override fun onStart() {
        super.onStart()
        val fontScale = resources.configuration.fontScale
        scale10 = when (fontScale) {
            0.8f -> 13
            0.9f -> 12
            1.0f -> 11
            1.1f -> 10
            1.2f -> 9
            1.3f -> 8
            1.5f -> 7
            1.7f -> 6
            2.0f -> 5
            else -> 4
        }

        val densityDpi = getDensityName()
//        Log.e("TAG", "densityDpiAA " + densityDpi)
        fontSize = when (densityDpi) {
            "xxxhdpi" -> 9f
            "xxhdpi" -> 9.5f
            "xhdpi" -> 10.5f
            "hdpi" -> 10.5f
            "mdpi" -> 11f
            "ldpi" -> 11.5f
            else -> 12f
        }
    }



//    val listener = InstallStateUpdatedListener { state ->
//        if (state.installStatus() == InstallStatus.DOWNLOADING) {
//            val bytesDownloaded = state.bytesDownloaded()
//            val totalBytesToDownload = state.totalBytesToDownload()
//        }
//        if (state.installStatus() == InstallStatus.DOWNLOADED) {
//            Snackbar.make(
//                binding.root,
//                "New app is ready",
//                Snackbar.LENGTH_INDEFINITE
//            ).setAction("Restart") {
//                appUpdateManager.completeUpdate()
//            }.show()
//        }
//    }


    override fun onResume() {
        super.onResume()
        ioThread {
            delay(2500)
            callBack()
        }
//        appUpdateManager?.let {
//            it.registerListener(listener)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logoutAlert?.let {
            logoutAlert!!.cancel()
        }
        deleteAlert?.let {
            deleteAlert!!.cancel()
        }
//        appUpdateManager?.let {
//            it.unregisterListener(listener)
//        }
    }


    private fun callMediaPermissions() {
         val permissionList = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        activityResultLauncher.launch(permissionList)
    }

    @SuppressLint("MissingPermission")
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            if(!permissions.entries.toString().contains("false")){
                openCamera()
            } else {
                callPermissionDialog{
                    someActivityResultLauncher.launch(this)
                }
            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun openCamera() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            Log.e("TAG", "addOnSuccessListenerBB " + location.toString())
        }
    }


//    fun writeOnDrawable(drawableId: Int, text: String?): BitmapDrawable? {
//        val bm: Bitmap = BitmapFactory.decodeResource(getResources(), drawableId)
//            .copy(Bitmap.Config.ARGB_8888, true)
//
//        val paint: Paint = Paint()
//        paint.setStyle(Style.FILL)
//        paint.setColor(Color.BLACK)
//        paint.setTextSize(20f)
//
//        val canvas: Canvas = Canvas(bm)
//        canvas.drawText(text.toString(), 0f, (bm.getHeight() / 2).toFloat(), paint)
////        return BitmapDrawable(drawableId, bm);
//        var bitmapDrawable = BitmapDrawable(resources,bm)
//
//        return bitmapDrawable
//    }

//    // TODO: Step 1.6, Unsubscribe to location changes.
//    val removeTask = fusedLocationClient.removeLocationUpdates(locationCallback)
//    removeTask.addOnCompleteListener { task ->
//        if (task.isSuccessful) {
//            Log.d(TAG, "Location Callback removed.")
//            stopSelf()
//        } else {
//            Log.d(TAG, "Failed to remove Location Callback.")
//        }
//    }






    fun writeOnDrawable(drawableId: Int, text: String?) {
        val bm: Bitmap = BitmapFactory.decodeResource(getResources(), drawableId)
            .copy(Bitmap.Config.ARGB_8888, true)

       var bitmapDrawable =  addStampToImage(bm)

////        val paint: Paint = Paint()
////        paint.setStyle(Style.FILL)
////        paint.setColor(Color.BLACK)
////        paint.setTextSize(20f)
//
//
////        val r = Rect(10, 10, 200, 100)
////
////        val canvas: Canvas = Canvas(bm)
////        canvas.drawRect(r, paint);
////
////        // border
////        paint.setStyle(Paint.Style.STROKE);
////        paint.setColor(Color.BLACK);
////        canvas.drawRect(r, paint);
////        canvas.drawARGB(0, 255, 255, 255)
////        canvas.drawRGB(255, 255, 255)
//
//
//        val strokePaint = Paint()
//        val r: RectF = RectF(30f, 30f, 100f, 50f)
//
//        strokePaint.setStyle(Paint.Style.STROKE);
//        strokePaint.setColor(Color.BLACK);
//        strokePaint.setStrokeWidth(2f);
//
//        val canvas: Canvas = Canvas(bm)
////        canvas.drawRect(r, strokePaint)
////        canvas.translate(0f, 600f)
//        val cornerRadius = 50f
//        canvas.drawRoundRect(r, cornerRadius, cornerRadius, strokePaint) // stroke
////        canvas.drawText(text.toString(), 0f, (bm.getHeight() / 2).toFloat(), paint)
////        return BitmapDrawable(drawableId, bm);
////        canvas.drawBitmap(bm, 100f, 100f, strokePaint);
////        canvas.drawRect(0f, 0f, 21f, 12f, bm);
//        var bitmapDrawable = BitmapDrawable(resources,bm)

        return bitmapDrawable
    }


    private fun addStampToImage(originalBitmap: Bitmap) {
        val extraHeight = (originalBitmap.getHeight() * 0.15).toInt()

        val newBitmap = Bitmap.createBitmap(
            originalBitmap.getWidth(),
            originalBitmap.getHeight() + extraHeight, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.BLUE)
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)
        val resources: Resources = getResources()
        val scale: Float = resources.getDisplayMetrics().density

        val pText = Paint()
        pText.setColor(Color.WHITE)
        pText.setTextSize((20 * scale).toInt().toFloat())

        val text = "Maulik"

        /*Rect r = new Rect();
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        pText.setTextAlign(Paint.Align.LEFT);
        pText.getTextBounds(text, 0, text.length(), r);
        float x = -r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;

        int minusSpace = (int) (canvas.getClipBounds().bottom * 0.07);

        canvas.drawText(text, 0, canvas.getClipBounds().bottom - minusSpace, pText);*/
        val bounds = Rect()
        pText.getTextBounds(text, 0, text.length, bounds)
        val x = (newBitmap.getWidth() - bounds.width()) / 6
        val y = (newBitmap.getHeight() + bounds.height()) / 5

//        canvas.drawText(text, x * scale, y * scale, pText)

        binding.ivLogoAA.setImageBitmap(newBitmap)
    }



}
