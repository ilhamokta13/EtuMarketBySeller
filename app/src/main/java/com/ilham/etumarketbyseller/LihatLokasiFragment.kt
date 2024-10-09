package com.ilham.etumarketbyseller

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.ilham.etumarketbyseller.databinding.FragmentLihatLokasiBinding
import com.ilham.etumarketbyseller.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LihatLokasiFragment : Fragment(), OnMapReadyCallback {

    lateinit var binding : FragmentLihatLokasiBinding
    lateinit var pref: SharedPreferences
    lateinit var adminVm : AdminViewModel
    lateinit var gMap: GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLihatLokasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        adminVm = ViewModelProvider(this).get(AdminViewModel::class.java)
        val token = pref.getString("token", "").toString()


        val mapFragment = childFragmentManager.findFragmentById(R.id.layout_maps) as SupportMapFragment
        mapFragment.getMapAsync(this)


        adminVm.datapesanan.observe(viewLifecycleOwner) { dataPesananList ->
            // Iterate through each dataPesanan object
            val boundsBuilder = LatLngBounds.Builder()
            dataPesananList.forEach { dataPesanan ->
                // Accessing latitude and longitude of destination
                val latitude = dataPesanan.destination.latitude
                val longitude = dataPesanan.destination.longitude

                // Do something with latitude and longitude, for example, logging
                Log.d("Destination", "Latitude: $latitude, Longitude: $longitude")

                val latLon = LatLng(latitude, longitude)

                gMap.addMarker(
                    MarkerOptions()
                        .position(latLon)
                        .icon(vectorToBitmap(R.drawable.ic_baseline_location_on_24, Color.RED))
                )

                boundsBuilder.include(latLon)
                val bounds: LatLngBounds = boundsBuilder.build()
                gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
            }
        }


        adminVm.getpesanan(token)



    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.map_style
            )
        )
        gMap.uiSettings.isZoomControlsEnabled = true
        gMap.uiSettings.isIndoorLevelPickerEnabled = true
        gMap.uiSettings.isCompassEnabled = true
        gMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION,) == PackageManager.PERMISSION_GRANTED
        ) {
            gMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }


}