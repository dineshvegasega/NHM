package com.nhm.distribution.screens.main.NBPA.addForms

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.nhm.distribution.R
import com.nhm.distribution.databinding.Form3Binding
import com.nhm.distribution.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distribution.datastore.DataStoreUtil.readData
import com.nhm.distribution.models.Login
import com.nhm.distribution.networking.foodDate
import com.nhm.distribution.networking.foodHeight
import com.nhm.distribution.networking.foodIdentityImage
import com.nhm.distribution.networking.foodItemImage
import com.nhm.distribution.networking.foodMonth
import com.nhm.distribution.networking.foodSignatureImage
import com.nhm.distribution.screens.interfaces.CallBackListener
import com.nhm.distribution.screens.main.NBPA.NBPAViewModel
import com.nhm.distribution.screens.main.NBPA.addForms.NBPA_Form1.Companion.formFill1
import com.nhm.distribution.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.nhm.distribution.screens.onboarding.register.Register.Companion.imagePath
import com.nhm.distribution.screens.onboarding.register.Register.Companion.latLong
import com.nhm.distribution.utils.callNetworkDialog
import com.nhm.distribution.utils.callPermissionDialog
import com.nhm.distribution.utils.getAddress
import com.nhm.distribution.utils.getCameraPath
import com.nhm.distribution.utils.getImageName
import com.nhm.distribution.utils.getMediaFilePathFor
import com.nhm.distribution.utils.loadImage
import com.nhm.distribution.utils.mainThread
import com.nhm.distribution.utils.showDropDownDialog
import com.nhm.distribution.utils.showOptions
import com.nhm.distribution.utils.showSnackBar
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class NBPA_Form3 : Fragment() , CallBackListener {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form3Binding? = null
    private val binding get() = _binding!!

    companion object {
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
        var formFill3 = false
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = Form3Binding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
        callBackListener = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        binding.apply {

            ivMenu.singleClick {
                NBPA.callBackListener!!.onCallBack(1001)
            }

            signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
                override fun onStartSigning() {
                    //Event triggered when the pad is touched
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }

                override fun onSigned() {
                    //Event triggered when the pad is signed
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }

                override fun onClear() {
                    //Event triggered when the pad is cleared
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }
            })

            btComplete.singleClick {
                ivSignature.setImageBitmap(signaturePad.signatureBitmap)
                ivSignature.visibility = View.VISIBLE
                signaturePad.visibility = View.GONE
                btTryAgain.visibility = View.VISIBLE
                btComplete.visibility = View.GONE
                btClear.visibility = View.INVISIBLE

                callMediaPermissions()
            }

            btTryAgain.singleClick {
                signaturePad.clear()
                ivSignature.visibility = View.GONE
                signaturePad.visibility = View.VISIBLE
                btTryAgain.visibility = View.GONE
                btComplete.visibility = View.VISIBLE
                btClear.visibility = View.VISIBLE
                viewModel.foodSignatureImage = ""
            }

            btClear.singleClick {
                signaturePad.clear()
                viewModel.foodSignatureImage = ""
            }

            editTextMonth.singleClick {
                requireActivity().showDropDownDialog(type = 21){
                    editTextMonth.setText(title)
                    viewModel.foodMonth = title
                }
            }

            editTextDate.singleClick {
                requireActivity().showDropDownDialog(type = 22){
                    editTextDate.setText(title)
                    viewModel.foodDate = title
                }
            }



            ivImagePassportsizeImage.singleClick {
                imagePosition = 1
                callMediaPermissionsWithLocation()
            }

            ivImageIdentityImage.singleClick {
                imagePosition = 2
                callMediaPermissionsWithLocation()
            }


            btnImagePassportsize.singleClick {
                imagePosition = 1
                callMediaPermissionsWithLocation()
            }

            btnIdentityImage.singleClick {
                imagePosition = 2
                callMediaPermissionsWithLocation()
            }





            btSignIn.singleClick {
                isProductLoad = true
                getData(true)
            }

        }


    }




    private fun callMediaPermissionsWithLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activityResultLauncherWithLocation.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncherWithLocation.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            activityResultLauncherWithLocation.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    private val activityResultLauncherWithLocation =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            if (!permissions.entries.toString().contains("false")) {
                requireActivity().showOptions {
                    when (this) {
                        1 -> forCamera()
                        2 -> forGallery()
                    }
                }
            } else {
                requireActivity().callPermissionDialog {
                    someActivityResultLauncherWithLocation.launch(this)
                }
            }
        }


    var someActivityResultLauncherWithLocation = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissionsWithLocation()
    }




    private fun forCamera() {
        requireActivity().getCameraPath {
            uriReal = this
            captureMedia.launch(uriReal)
        }
    }

    private fun forGallery() {
        requireActivity().runOnUiThread() {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }




    var imagePosition = 0
    @SuppressLint("MissingPermission")
    private var pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            lifecycleScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
                if (uri != null) {
                    when (imagePosition) {
                        1 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
//                            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                Log.e("TAG", "addOnSuccessListenerRegisterAA " + location.toString())
                                imagePath = compressedImageFile.path
                                latLong = LatLng(location!!.latitude, location.longitude)

                                readData(LOGIN_DATA) { loginUser ->
                                    if (loginUser != null) {
                                        val data = Gson().fromJson(loginUser, Login::class.java)
                                        binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                        val currentDate = sdf.format(Date())

                                        binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name
                                        CoroutineScope(Dispatchers.IO).launch {
                                            withContext(Dispatchers.Main) {
                                                binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                                Log.e("TAG", "requireActivity().getAddress(latLong) "+getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong))
                                            }
                                        }
//                                        binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                        binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                        binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude
                                        mainThread {
                                            dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                viewModel.foodItemImage = this
                                                Log.e("TAG", "viewModel.foodItemImage " + this)
                                                binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
//                            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                Log.e("TAG", "addOnSuccessListenerRegisterBB " + location.toString())
                                imagePath = compressedImageFile.path
                                latLong = LatLng(location!!.latitude, location.longitude)

                                readData(LOGIN_DATA) { loginUser ->
                                    if (loginUser != null) {
                                        val data = Gson().fromJson(loginUser, Login::class.java)
                                        binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                        val currentDate = sdf.format(Date())

                                        binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name
                                        CoroutineScope(Dispatchers.IO).launch {
                                            withContext(Dispatchers.Main) {
                                                binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                                Log.e("TAG", "requireActivity().getAddress(latLong)2 "+getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong))
                                            }
                                        }
//                                        binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                        binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                        binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude
                                        mainThread {
                                            dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                viewModel.foodIdentityImage = this
                                                Log.e("TAG", "viewModel.foodItemImage " + this)
                                                binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    var uriReal: Uri? = null
    @SuppressLint("MissingPermission")
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        lifecycleScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
            if (uri == true) {

                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
//                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            Log.e("TAG", "addOnSuccessListenerRegisterAA " + location.toString())
                            imagePath = compressedImageFile.path
                            latLong = LatLng(location!!.latitude, location.longitude)

                            readData(LOGIN_DATA) { loginUser ->
                                if (loginUser != null) {
                                    val data = Gson().fromJson(loginUser, Login::class.java)
                                    binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                    val currentDate = sdf.format(Date())

                                    binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name
                                    CoroutineScope(Dispatchers.IO).launch {
                                        withContext(Dispatchers.Main) {
                                            binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                        }
                                    }
//                                    binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                    binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                    binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude
                                    mainThread {
                                        dispatchTakePictureIntent(binding.layoutMainCapture) {
                                            viewModel.foodItemImage = this
                                            Log.e("TAG", "viewModel.foodItemImage " + this)
                                            binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
//                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            Log.e("TAG", "addOnSuccessListenerRegisterBB " + location.toString())
                            imagePath = compressedImageFile.path
                            latLong = LatLng(location!!.latitude, location.longitude)

                            readData(LOGIN_DATA) { loginUser ->
                                if (loginUser != null) {
                                    val data = Gson().fromJson(loginUser, Login::class.java)
                                    binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                    val currentDate = sdf.format(Date())

                                    binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name
                                    CoroutineScope(Dispatchers.IO).launch {
                                        withContext(Dispatchers.Main) {
                                            binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                        }
                                    }
//                                    binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                    binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                    binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude
                                    mainThread {
                                        dispatchTakePictureIntent(binding.layoutMainCapture) {
                                            viewModel.foodIdentityImage = this
                                            Log.e("TAG", "viewModel.foodItemImage " + this)
                                            binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }







    private fun callMediaPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            )
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES)
            )
        } else{
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            if(!permissions.entries.toString().contains("false")){
                mainThread {
                    dispatchTakePictureIntent(binding.ivSignature){
                        viewModel.foodSignatureImage = this
                        Log.e("TAG", "viewModel.foodSignature "+viewModel.foodSignatureImage)
                    }
                }
            } else {
                requireActivity().callPermissionDialog{
                    someActivityResultLauncher.launch(this)
                }
            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
    }



    private fun dispatchTakePictureIntent(imageView: View, callBack: String.() -> Unit) {
        val bitmap: Bitmap = getBitmapFromView(imageView)
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        // val filename = System.currentTimeMillis().toString() + "." + "png" // change png/pdf
        val file = File(path, getImageName())
        try {
            if (!path.exists()) path.mkdirs()
            if (!file.exists()) file.createNewFile()
            val ostream: FileOutputStream = FileOutputStream(file)
            bitmap.compress(CompressFormat.PNG, 10, ostream)
            ostream.close()
            val data = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file)
            }else{
                val imagePath: File = File(file.absolutePath)
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", imagePath)
            }
//            Log.e("TAG", "viewModel.foodSignature "+viewModel.foodSignature)
            callBack(file.toString())

        } catch (e: IOException) {
            Log.w("ExternalStorage", "Error writing $file", e)
        }
    }

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    override fun onCallBack(pos: Int) {
        Log.e("TAG", "onCallBackNo3 "+pos)
        getData(false)
    }




    private fun getData(isButton: Boolean) {
        binding.apply {
            if (editTextMonth.text.toString() == "") {
                showSnackBar(getString(R.string.select_month))
                formFill3 = false
            } else if (editTextDate.text.toString() == "") {
                showSnackBar(getString(R.string.select_date))
                formFill3 = false
            } else if (editTextHeight.text.toString() == "") {
                showSnackBar(getString(R.string.enterWeight))
                formFill3 = false
            } else if (viewModel.foodSignatureImage == "") {
                showSnackBar(getString(R.string.add_signature))
                formFill3 = false
            } else if (viewModel.foodItemImage == "") {
                showSnackBar(getString(R.string.food_item_image))
                formFill3 = false
            } else if (viewModel.foodIdentityImage == "") {
                showSnackBar(getString(R.string.identity_imageStar))
                formFill3 = false
            } else {
                viewModel.foodHeight = editTextHeight.text.toString()
                formFill3 = true
                if (isButton){
                    NBPA.callBackListener!!.onCallBack(1003)
                } else {
                }
            }
        }
    }

//    override fun onStop() {
//        super.onStop()
//        isProductLoad = true
//        isProductLoadMember = true
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        isProductLoad = false
//        isProductLoadMember = false
//    }

}