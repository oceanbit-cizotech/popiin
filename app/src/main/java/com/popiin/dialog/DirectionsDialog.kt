package com.popiin.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.popiin.R
import com.popiin.databinding.DialogDirectionsBinding
import com.popiin.util.Common
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*


class DirectionsDialog(context: AppCompatActivity, source: LatLng, desination: LatLng) :
    AppCompatDialog(context), OnMapReadyCallback {
    var binding: DialogDirectionsBinding
    var mMap: GoogleMap? = null
    var markerOptions: MarkerOptions? = null
    var source: LatLng
    var desti: LatLng
    var activity: FragmentActivity

    init {
        activity = context
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
        window!!.statusBarColor = ContextCompat.getColor(context, R.color.colorTransparent)
        this.source = source
        desti = desination
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_directions,
            null,
            false
        )
        setContentView(binding.root)
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
        window!!.setDimAmount(0.5f)
        MapsInitializer.initialize(context)
        val mMapView: MapView? = findViewById<View>(R.id.map) as MapView?
        mMapView!!.onCreate(onSaveInstanceState())
        mMapView.onResume()
        binding.map.getMapAsync(this)
        binding.imgClose.setOnClickListener { dismiss() }
        binding.tvDirection.setOnClickListener {
            val commonDialog = CommonDialog(
                context,
                context.getString(R.string.txt_open_now),
                context.getString(R.string.cancel),
                "",
                "\"popiin\" wants to open \"Google Maps\""
            )
            commonDialog.binding.btnPositive.setOnClickListener {
                commonDialog.dismiss()
                val url =
                    "http://maps.google.com/maps?saddr=" + source.latitude + "," + source.longitude + "&daddr=" + desti.latitude + "," + desti.longitude
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
                intent.setPackage("com.google.android.apps.maps")
                context.startActivity(intent)
            }
            commonDialog.binding.btnNegative.setOnClickListener{
                commonDialog.dismiss()
            }

            commonDialog.show()


        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()
        mMap!!.isBuildingsEnabled = false
        GoogleDirection.withServerKey(activity.resources.getString(R.string.google_maps_key))
            .from(source)
            .to(desti)
            .avoid(AvoidType.FERRIES)
            .avoid(AvoidType.HIGHWAYS)
            .transportMode(TransportMode.DRIVING)
            .execute(object : DirectionCallback {
                override fun onDirectionSuccess(direction: Direction?) {
                    if (direction!!.isOK) {
                        val lineOptions = PolylineOptions()
                        markerOptions = MarkerOptions()
                        markerOptions!!.position(direction.routeList[0].overviewPolyline.pointList[0])
                        markerOptions!!.title("")
                        markerOptions!!.flat(true)
                        markerOptions!!.infoWindowAnchor(0.5f, 0.2f)
                        markerOptions!!.icon(Common.instance!!.bitmapFromVector(context,
                            R.drawable.ic_current_point))
                        mMap!!.addMarker(markerOptions!!)
                        markerOptions = MarkerOptions()
                        markerOptions!!.position(
                            direction.routeList[0].overviewPolyline.pointList[direction.routeList[0].overviewPolyline.pointList.size - 1]
                        )
                        markerOptions!!.title("")
                        markerOptions!!.flat(true)
                        markerOptions!!.infoWindowAnchor(0.5f, 0.2f)
                        markerOptions!!.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_blue_destination_point))
                        mMap!!.addMarker(markerOptions!!)
                        lineOptions.addAll(direction.routeList[0].overviewPolyline.pointList)
                        lineOptions.width(8f)
                        lineOptions.color(Color.BLACK)
                        lineOptions.geodesic(true)
                        mMap!!.addPolyline(lineOptions)
                        val bounds = LatLngBounds.Builder()
                            .include(source)
                            .include(desti).build()
                        activity.runOnUiThread { }
                        val displaySize = Point()
                        @Suppress("DEPRECATION")
                        activity.windowManager.defaultDisplay.getSize(displaySize)
                        mMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                (binding.flMap.width * 0.85).toInt(),
                                (binding.flMap.height * 0.85).toInt(),
                                50
                            )
                        )
                    } else {
                        // Do something
                    }
                }

                override fun onDirectionFailure(t: Throwable) {
                    // Do something
                    t.printStackTrace()
                }
            })
    }

    private fun setMapStyle() {
        try {
            val styleJson = loadJsonFromAssets("mapstyle.json")
            val success = mMap!!.setMapStyle(MapStyleOptions(styleJson))
            if (!success) {
            }
        } catch (e: Exception) {
            Log.e("MapsActivity", "Error loading map style.", e)
        }
    }

    private fun loadJsonFromAssets(filename: String): String {
        return context!!.assets.open(filename).bufferedReader().use { it.readText() }
    }

}