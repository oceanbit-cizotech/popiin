package com.popiin.bottomDialogFragment

import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.popiin.BaseBottomSheetDialog
import com.popiin.R
import com.popiin.databinding.BottomFragmentDirectionBinding
import com.popiin.util.Constant
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

class DirectionBottomFragment(
    var context: AppCompatActivity,
    var source: LatLng,
    var destination: LatLng
) : BaseBottomSheetDialog<BottomFragmentDirectionBinding>(),
    OnMapReadyCallback {
    var mMap: GoogleMap? = null
    var markerOptions: MarkerOptions? = null
    override fun getLayout(): Int {
        return R.layout.bottom_fragment_direction
    }

    override fun initViews() {
        MapsInitializer.initialize(context)
        binding!!.map.getMapAsync(this)
        binding!!.imgClose.setOnClickListener { dismiss() }
        binding!!.tvDirection.setOnClickListener {
            val url =
                "http://maps.google.com/maps?saddr=" + source.latitude + "," + source.longitude + "&daddr=" + destination.latitude + "," + destination.longitude
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
            intent.setPackage(Constant().mapPackage)
            context.startActivity(intent)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.isBuildingsEnabled = false
        GoogleDirection.withServerKey(context.resources.getString(R.string.google_maps_key))
            .from(source)
            .to(destination)
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
                        markerOptions!!.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_point))
                        mMap!!.addMarker(markerOptions!!)
                        markerOptions = MarkerOptions()
                        markerOptions!!.position(direction.routeList[0].overviewPolyline.pointList[direction.routeList[0].overviewPolyline.pointList.size - 1])
                        markerOptions!!.title("")
                        markerOptions!!.flat(true)
                        markerOptions!!.infoWindowAnchor(0.5f, 0.2f)
                        markerOptions!!.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination_point))
                        mMap!!.addMarker(markerOptions!!)
                        lineOptions.addAll(direction.routeList[0].overviewPolyline.pointList)
                        lineOptions.width(5f)
                        lineOptions.color(Color.RED)
                        lineOptions.geodesic(true)
                        mMap!!.addPolyline(lineOptions)
                        val bounds = LatLngBounds.Builder()
                            .include(source)
                            .include(destination).build()
                        val displaySize = Point()
                        context.windowManager.defaultDisplay.getSize(displaySize)
                        mMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                (binding!!.flMap.width * 0.80).toInt(),
                                (binding!!.flMap.height * 0.80).toInt(),
                                50
                            )
                        )
                    } else {
                        // Do something
                    }
                }

                override fun onDirectionFailure(t: Throwable) {
                    onApiFailure(throwable = t)
                }
            })
    }
}