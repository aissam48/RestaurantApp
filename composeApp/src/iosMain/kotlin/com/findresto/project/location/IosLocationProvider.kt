package com.findresto.project.location

import com.findresto.project.network.models.LocationData
import com.findresto.project.screens.RestaurantsViewModel
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import platform.CoreLocation.*
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

class IosLocationProvider : LocationProvider, KoinComponent {

    private val viewModel: RestaurantsViewModel by lazy { get<RestaurantsViewModel>() }

    private val locationManager = CLLocationManager()
    private val delegate = LocationDelegate()

    init {
        locationManager.delegate = delegate
    }

    override suspend fun getCurrentLocation(): LocationData? {

        return suspendCancellableCoroutine { continuation ->

            delegate.continuation = { locationData ->
                println("Location result: $locationData")
                continuation.resume(locationData)
            }

            continuation.invokeOnCancellation {
                delegate.continuation = null
                locationManager.stopUpdatingLocation()
            }
        }
    }

    inner class LocationDelegate : NSObject(), CLLocationManagerDelegateProtocol {

        var continuation: ((LocationData?) -> Unit)? = null

        @OptIn(ExperimentalForeignApi::class)
        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            val currentContinuation = continuation ?: return

            continuation = null
            manager.stopUpdatingLocation()

            val loc = didUpdateLocations.firstOrNull() as? CLLocation
            val locationData = loc?.let {
                LocationData(
                    latitude = it.coordinate.useContents { latitude },
                    longitude = it.coordinate.useContents { longitude }
                )
            }

            currentContinuation(locationData)
        }

        override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
            val currentContinuation = continuation ?: return

            continuation = null
            manager.stopUpdatingLocation()
            currentContinuation(null)
        }

        override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
            when (manager.authorizationStatus) {
                kCLAuthorizationStatusRestricted,
                kCLAuthorizationStatusDenied -> {
                    println("denied or restricted")
                    viewModel.onPermissionResult(false)
                }

                kCLAuthorizationStatusAuthorizedAlways,
                kCLAuthorizationStatusAuthorizedWhenInUse -> {
                    println("granted")
                    viewModel.onPermissionResult(true)
                    manager.startUpdatingLocation()
                }

                kCLAuthorizationStatusNotDetermined -> {
                    println("requesting permission")
                    locationManager.requestWhenInUseAuthorization()
                }
            }
        }
    }
}
