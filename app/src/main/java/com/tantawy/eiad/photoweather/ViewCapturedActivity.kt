package com.tantawy.eiad.photoweather

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class ViewCapturedActivity : AppCompatActivity() {

    private lateinit var imageViewModel: ImageViewModel
    private lateinit var city: String
    private lateinit var description: String
    private lateinit var date: String
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_captured)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val imagePreview = findViewById<ImageView>(R.id.imagePreview)
        val shareBtn = findViewById<ImageView>(R.id.shareBtn)
        val imageBitmap = getIntent()?.extras?.get("data") as Bitmap
        val firstTime = getIntent()?.extras?.get("firstTime") as Boolean
        val uri = getIntent()?.extras?.get("uri") as String
        Log.d("hello2",uri)
        imagePreview.setImageBitmap(imageBitmap)
        imagePreview.minimumHeight = imageBitmap.height * 6
        imagePreview.minimumWidth = imageBitmap.width * 6

        shareBtn.setOnClickListener(View.OnClickListener {
//            val uriP = Uri.parse(uri)
//            val file = File(uriP.path)
//
//            Log.d("1st", uri)
//            Log.d("2nd",uriP.toString())
//
//            val share = Intent(Intent.ACTION_SEND)
//
//            val myUri = FileProvider.getUriForFile(this, "com.tantawy.eiad.photoweather.fileprovider",file)
//
//            share.setType("image/jpeg")
//            share.putExtra(Intent.EXTRA_STREAM, myUri)
//
//            startActivity(Intent.createChooser(share, "Share..."));

            try {
                val file = File(externalCacheDir, "devofandroid.png")
                val fOut = FileOutputStream(file)
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
                fOut.flush()
                fOut.close()
                file.setReadable(true, false)
                val intent = Intent(Intent.ACTION_SEND)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,"com.tantawy.eiad.photoweather.fileprovider",file))
                intent.type = "image/png"
                startActivity(Intent.createChooser(intent, "Share image via"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        if(firstTime) {
            imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
            val uri = saveImageToInternalStorage(imageBitmap)
            imageViewModel.insert(CapturedImage(0, uri.toString()))
            getLastLocation()
        }
    }

    private fun findWeather() {
        //0a62530fdb4237f7fa436cbdfc1c9b32 <- keyy


        //Obtain an instance of Retrofit by calling the static method.
        val retrofit: Retrofit = RetrofitClient.getClient()

        //The main purpose of Retrofit is to create HTTP calls from the Java interface based on the annotation associated with each method. This is achieved by just passing the interface class as parameter to the create method
        val weatherAPIs: WeatherService = retrofit.create(WeatherService::class.java)
        Log.d("myval",lat)
        Log.d("myval",long)
        //Invoke the method corresponding to the HTTP request which will return a Call object. This Call object will used to send the actual network request with the specified parameters
        val call = weatherAPIs.getWeather(
            "235bef5a99d6bc6193525182c409602c",
            "metric",
            lat.toDouble(),
            long.toDouble()
        )
        //This is the line which actually sends a network request. Calling enqueue() executes a call asynchronously. It has two callback listeners which will invoked on the main thread
        call.enqueue(object : Callback<WResponse> {
            override fun onResponse(call: Call<WResponse>?, response: Response<WResponse>) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                */
                if (response.body() != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address> = geocoder.getFromLocation(lat.toDouble(), long.toDouble(), 1)
                    val cityName: String = addresses[0].getAddressLine(0)
                    Toast.makeText(context,cityName,Toast.LENGTH_SHORT).show()
                    Log.d("cityyyy",cityName)
                    val wResponse: WResponse = response.body()!!
                    Log.d("infoNow",wResponse.main?.temp.toString())
                    Toast.makeText(context,cityName+" "+wResponse.main?.temp.toString(),Toast.LENGTH_LONG).show()
                    Log.d("infoNow",wResponse.main?.tempMin.toString())
                    Log.d("infoNow",wResponse.main?.pressure.toString())
                }
            }

            override fun onFailure(call: Call<WResponse>?, t: Throwable) {
                /*
                Error callback
                */
            }
        })

    }
    private lateinit var lat:String
    private lateinit var long:String
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        lat = location.latitude.toString()
                        long = location.longitude.toString()
                        findWeather()
                    }

                }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            lat = mLastLocation.latitude.toString()
            long = mLastLocation.longitude.toString()
            findWeather()

        }
    }


    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }



    private fun saveImageToInternalStorage(bitmap:Bitmap):Uri{
        // Get the image from drawable resource as drawable object
        //val drawable = ContextCompat.getDrawable(applicationContext,drawableId)

        // Get the bitmap from drawable object
        //val bitmap = (drawable as BitmapDrawable).bitmap

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)


        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return Uri.parse(file.absolutePath)
    }

    fun Bitmap.fixRotation(uri: Uri): Bitmap? {

        val ei = ExifInterface(uri.path)

        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage( 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage( 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage( 270f)
            ExifInterface.ORIENTATION_NORMAL -> this
            else -> this
        }
    }

    fun Bitmap.rotateImage(angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            this, 0, 0, width, height,
            matrix, true
        )
    }
}

