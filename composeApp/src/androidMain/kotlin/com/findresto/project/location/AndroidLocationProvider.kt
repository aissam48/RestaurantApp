package com.findresto.project.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.findresto.project.network.models.LocationData
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AndroidLocationProvider(private val context: Context) : LocationProvider {

    override suspend fun getCurrentLocation(): LocationData =
        suspendCancellableCoroutine { cont ->
            val fused = LocationServices.getFusedLocationProviderClient(context)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                cont.resume(LocationData(null, null))

                return@suspendCancellableCoroutine
            }
            fused.lastLocation
                .addOnSuccessListener {
                    if (it != null)
                        cont.resume(LocationData(it.latitude, it.longitude))
                    else
                        cont.resumeWithException(Exception("Location null"))
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
}
