package com.aymen.metastore.util

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BarcodeScanner @Inject constructor(context: Context) {

    private val option = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()

    private val scanner = GmsBarcodeScanning.getClient(context, option)

suspend fun startScan() : String? {
    return try {
        scanner.startScan().await().rawValue.toString()
    }catch (ex : Exception){
        null
    }
}

}















