package com.popiin

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.popiin.activity.HomeActivity
import com.popiin.activity.SignInActivity
import com.popiin.callback.FireBaseTokenCallback
import com.popiin.databinding.ActivitySplashBinding
import com.popiin.locationutils.OceanLocation
import com.popiin.util.Constant
import com.popiin.util.Parameters
import com.popiin.util.PrefManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.popiin.req.EventDetailsReq
import com.popiin.res.ConfigRes
import com.popiin.res.EventDetailsRes
import com.popiin.util.LogHandler
import io.branch.referral.Branch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private var oceanLocation: OceanLocation? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private var permissions14Up = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.RECEIVE_BOOT_COMPLETED
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private var permissions = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.RECEIVE_BOOT_COMPLETED
    )

    private var permissionsBelow13 = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.RECEIVE_BOOT_COMPLETED
    )
    private var permissionAll = 1
    private var isFromGpsSettings = false
    private var manager: LocationManager? = null
    private var mInflater: LayoutInflater? = null

    override fun getLayout(): Int {
        return R.layout.activity_splash
    }
    fun clearAppCache(context: Context) {
        try {
            val cacheDir = context.cacheDir
            cacheDir.deleteRecursively() // Deletes cache files
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun initViews() {

        LogHandler.writeLog(mActivity!!, "${SplashActivity::class.java.simpleName} *****************", "INFO")
        LogHandler.writeLog(mActivity!!, "${SplashActivity::class.java.simpleName} Splase Activity", "INFO")

        clearAppCache(mActivity.baseContext)
        initView()
        mInflater = LayoutInflater.from(this)

        oceanLocation = OceanLocation.getInstance(applicationContext)
        manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!isGPSEnabled()) {
            turnGPSOn()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (!hasPermissions(this, permissions14Up)) {
                ActivityCompat.requestPermissions(this, permissions14Up, permissionAll)
            } else if (manager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                buildAlertMessageNoGps()
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasPermissions(this, permissions)) {
                ActivityCompat.requestPermissions(this, permissions, permissionAll)
            } else if (manager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
               buildAlertMessageNoGps()
            }
        }else {
            if (!hasPermissions(this, permissionsBelow13)) {
                ActivityCompat.requestPermissions(this, permissionsBelow13, permissionAll)
            } else if (manager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                buildAlertMessageNoGps()
            }
        }
        setupSplashUi()
        callConfigApi()
    }

    private fun turnGPSOn() {
        val provider = Settings.Secure.getString(contentResolver, LocationManager.GPS_PROVIDER)
        if (provider != null) {
            if (!provider.contains(Constant().gps)) { //if gps is disabled
                val poke = Intent()
                poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider")
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
                poke.data = Uri.parse(Constant().three)
                sendBroadcast(poke)
            }
        }

    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.txt_gps_seems_disabled))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.txt_yes)) { dialog, _ ->
                dialog.dismiss()
                isFromGpsSettings = true
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(getString(R.string.txt_no)
            ) { dialog, _ -> dialog.dismiss() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient!!.lastLocation.addOnSuccessListener(mActivity) { location ->
                if (location != null) {
                    PrefManager.lastLocation = location
                }
            }
        }
    }

    private fun hasPermissions(context: Context?, vararg permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        permission.toString())
                } != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (permissions.isEmpty()) {
                return
            }
            var allPermissionsGranted = true
            if (grantResults.isNotEmpty()) {
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false
                        break
                    }
                }
            }
            if (!allPermissionsGranted) {
                var somePermissionsForeverDenied = false
                for (permission in permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission!!)) {

                    } else {
                        if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {

                        } else {
                            somePermissionsForeverDenied = true
                        }
                    }
                }
                if (somePermissionsForeverDenied) {
                    val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle(getString(R.string.txt_permission_required))
                        .setMessage(getString(R.string.txt_forcefully_permission_required))
                        .setPositiveButton(getString(R.string.txt_settings)
                        ) { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", packageName, null))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .setNegativeButton(getString(R.string.txt_cancel)
                        ) { _, _ -> }
                        .setCancelable(false)
                        .create()
                        .show()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(this,
                            this.permissions, permissionAll)
                    }else{
                        ActivityCompat.requestPermissions(this, permissionsBelow13, permissionAll)
                    }
                }
            } else {
                if (!manager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!){
                    buildAlertMessageNoGps()
                } else {
                    oceanLocation?.buildGoogleApiClient()
                    getLocation()
                }
            }
        }
    }

    private fun initView() {
        binding.btnComeIn.setOnClickListener {
            startActivity(
                Intent(
                    this@SplashActivity,
                    SignInActivity::class.java
                )
            )
            if (!isFinishing) {
                finish()
            }
        }
    }


    private fun isGPSEnabled(): Boolean {
        val locationManager = mActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun setupSplashUi() {
        common.getFirebaseToken(object : FireBaseTokenCallback() {
            override fun onTokenGenerateSuccess(firebaseToken: String) {
                super.onTokenGenerateSuccess(firebaseToken)
                PrefManager.setFirebaseToken(firebaseToken)
                Looper.myLooper()?.let {
                    Handler(it).postDelayed({
                        if (isFinishing || isDestroyed) return@postDelayed // Prevents activity transition if already finishing

                        if (PrefManager.lastLocation == null) {
                            getLocation()
                        }

                        if (!PrefManager.getAccessToken().isNullOrEmpty()) {
                            if (!isFinishing) { // Double-check before starting a new activity
                                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                                finish()
                            }
                        } else {
                            binding.btnComeIn.visibility = View.VISIBLE
                        }
                    }, 3000)
                }
            }
        })


    }


    override fun onResume() {
        super.onResume()
        if (isFromGpsSettings){
            isFromGpsSettings = false

            if (!manager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!){
                buildAlertMessageNoGps()
            }else{
                oceanLocation?.buildGoogleApiClient()
                getLocation()
            }
        }
    }

    override fun onStart() {
        Branch.sessionBuilder(mActivity).withCallback { referringParams, error ->
            if (error == null) {

                // option 1: log data
                Log.i("BRANCH SDK", referringParams.toString())

                // option 2: save data to be used later
                try {
                    val deepLinkData = JSONObject(referringParams.toString())
                    Log.i("BRANCH SDK", "event : " + deepLinkData.has(Parameters().eventId))
                    Log.i("BRANCH SDK", "venue : " + deepLinkData.has(Parameters().venueId))
                    if (deepLinkData.has(Parameters().eventId) || deepLinkData.has(Parameters().venueId)) {
                        PrefManager.setDeepLinkData(referringParams.toString())
                    } else {
                        PrefManager.setDeepLinkData("")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.i("BRANCH SDK", "error ")
                }
            } else {
                Log.i("BRANCH SDK", error.message)
            }
          //  startOnCreate()
        }.withData(mActivity.intent.data).init()

        super.onStart()
    }

    private fun callConfigApi() {
        if (isInternetConnect()) {
            showProgress()
            val call: Call<ConfigRes> =
                apiInterface.getConfig()
            call.enqueue(object : Callback<ConfigRes> {
                override fun onResponse(call: Call<ConfigRes>, response: Response<ConfigRes>) {
                    hideProgress()
                    if (isValidResponse(response)) {
                        if (response.isSuccessful) {
                            PrefManager.config = response.body()?.data
                        }
                    }
                }

                override fun onFailure(call: Call<ConfigRes>, t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
        }
    }
}