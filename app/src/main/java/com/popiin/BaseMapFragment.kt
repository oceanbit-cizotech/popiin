package com.popiin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ViewDataBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.MapView
import java.lang.Exception

abstract class BaseMapFragment<DB : ViewDataBinding> : BaseFragment<DB>() {
    var googleMap: GoogleMap? = null

    //    SupportMapFragment supportMapFragment;
    private val mapAlphaHandler = Handler(Looper.myLooper()!!)
    private val googleMapReadyCallback = OnMapReadyCallback { map ->
        googleMap = map
        mapView
        onGoogleMapReady(googleMap)

//            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//                @Override
//                public void onMapLoaded() {
//
//                }
//            });
//            googleMap = map;
//            setupMap(requireContext(), googleMap);
//            onGoogleMapReady(googleMap);
    }

    //    public abstract int getMapFragmentId();
    abstract val mapView: MapView?
    abstract fun onGoogleMapReady(googleMap: GoogleMap?)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(getMapFragmentId());
//        if (supportMapFragment != null) {
//            supportMapFragment.getMapAsync(googleMapReadyCallback);
//            showProgress();
//        }
        if (mapView != null) {
            mapView!!.onCreate(savedInstanceState)
            mapView!!.getMapAsync(googleMapReadyCallback)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mapView != null) {
            mapView!!.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mapView != null) {
            mapView!!.onPause()
        }
    }

    override fun onDestroy() {
        mapAlphaHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
        try {
            if (mapView != null) {
                mapView!!.onDestroy()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        try {
            if (mapView != null) {
                mapView!!.onLowMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}