package com.example.fooda2

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_plus.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PlusActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1

    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var food_id : Int?= 0
    var day : Int = 0
    lateinit var currentPhotoPath : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plus)

        //Initiate storage
        storage = FirebaseStorage.getInstance()

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                day = position
                Log.d("position",day.toString())
            }

        }
        //갤러리 업로드
        plus_upload_btn.setOnClickListener {
            if(checkPersmission()) pickImage()
            else requestPermission()
        }

        plus_analyze_btn.setOnClickListener {
            dietAnalyze()
        }

        plus_diet_upload_btn.setOnClickListener {
            dietUplaod()
        }
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE, CAMERA),
            REQUEST_IMAGE_CAPTURE)
    }

    // 카메라 권한 체크
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    // 권한요청 결과
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "Permission: " + permissions[0] + "was " + grantResults[0] + "카메라 허가 받음 예이^^")
        }else{
            Log.d("TAG","카메라 허가 못받음 ㅠ 젠장!!")
        }
    }
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
    private fun pickImage() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                // 찍은 사진을 그림파일로 만들기
                val photoFile: File? =
                    try {
                        createImageFile()
                    } catch (ex: IOException) {
                        Log.d("TAG", "그림파일 만드는도중 에러생김")
                        null
                    }

                if (Build.VERSION.SDK_INT < 24) {
                    if(photoFile != null){
                        val photoURI = Uri.fromFile(photoFile)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        Log.d("tq", "takepicture put extra")
                    }
                }
                else{
                    // 그림파일을 성공적으로 만들었다면 startActivityForResult로 보내기
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this, "com.example.fooda2.fileprovider", it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    }
                }


                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                //intent.action = Intent.ACTION_GET_CONTENT

                val chooserIntent = Intent.createChooser(intent, "식단 사진을 업로드하세요.")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))
                startActivityForResult(chooserIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                //select photo
                photoUri = data?.data
                plus_food.setImageURI(photoUri)
            }else {
                //without selecting photo
                finish()
            }
        }
    }*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode){
            1 -> {
                if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){

                    data?.data?.let { uri ->
                        photoUri = uri
                        plus_food.setImageURI(photoUri)
                        Log.d("사진", photoUri.toString())
                    }

                    val file = File(currentPhotoPath)
                    val selectedUri = Uri.fromFile(file)
                    try{
                        selectedUri?.let {
                            if (Build.VERSION.SDK_INT < 28) {
                                val bitmap = MediaStore.Images.Media
                                    .getBitmap(contentResolver, selectedUri)
                                plus_food.setImageBitmap(bitmap)
                            }
                            else{
                                val decode = ImageDecoder.createSource(this.contentResolver,
                                    selectedUri)
                                val bitmap = ImageDecoder.decodeBitmap(decode)
                                plus_food.setImageBitmap(bitmap)
                            }
                        }

                    }catch (e: java.lang.Exception){
                        e.printStackTrace()
                    }

                }
            }
        }
    }
    fun dietUplaod(){

        var retrofit = RetrofitInterface.RetrofitClient.getInstnace()
        var server = retrofit.create(RetrofitInterface::class.java)

        server.post_diet_upload(RetrofitInterface.RetrofitClient.token, food_id!!, day).enqueue((object: Callback<RetrofitInterface.Message> {
            override fun onFailure(call: Call<RetrofitInterface.Message>, t: Throwable) {
                Log.d("레트로핏 결과1",t.message)
            }

            override fun onResponse(call: Call<RetrofitInterface.Message>, response: Response<RetrofitInterface.Message>) {
                if (response?.isSuccessful) {
                    if("success" == (response?.body()?.result)){
                        Toast.makeText(getApplicationContext(), "식단이 업데이트되었습니다.", Toast.LENGTH_LONG).show();
                        moveMainPage()
                    } else {
                        Toast.makeText(getApplicationContext(), response?.body()?.msg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }

        }))
    }
    fun moveMainPage(){
        startActivity(Intent(this, MenuActivity::class.java));
    }

    fun dietAnalyze(){
        //make filename
        var file : File
        if(photoUri==null) {file = File(currentPhotoPath)}
        else {
            file= File(getRealPathFromURI(photoUri))
            photoUri = null
        }
        Log.d("사진이름", file.toString())

        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png" //중복 방지

        var requestBody : RequestBody = RequestBody.create(MediaType.parse("image/*"),file)
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file",imageFileName,requestBody)

        var gson : Gson =  GsonBuilder()
            .setLenient()
            .create()

        var retrofit = RetrofitInterface.RetrofitClient.getInstnace()
        var server = retrofit.create(RetrofitInterface::class.java)

        server.post_diet_image(RetrofitInterface.RetrofitClient.token,body).enqueue(object: Callback<RetrofitInterface.Nutrient> {
            override fun onFailure(call: Call<RetrofitInterface.Nutrient>, t: Throwable) {
                Log.d("레트로핏 결과1",t.message)
            }
            override fun onResponse(call: Call<RetrofitInterface.Nutrient>, response: Response<RetrofitInterface.Nutrient>) {
                if (response?.isSuccessful) {
                    food_id = response?.body()?.food_id
                    plus_setting(response?.body())
                    Log.d("레트로핏 결과2",""+response?.body().toString())
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }
        })

    }

    private fun plus_setting(nutrient: RetrofitInterface.Nutrient?) {
        plus_foodname.setText(nutrient?.food_name)
        val cal : String = nutrient?.calories.toString() + "kcal / " + nutrient?.gram.toString() + "g"
        plus_cal.setText(cal)
        textView12.setText("탄수화물\n"+nutrient?.carbohydrate.toString() + "g")
        textView13.setText("단백질\n" + nutrient?.protein.toString()+"g")
        textView14.setText("지방\n" + nutrient?.fat.toString()+"g")

        //  plus_foodname.text = nutrient?.food_name
    }

    fun getRealPathFromURI(contentUri: Uri?): String? {
        val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri!!, proj, null, null, null)
        cursor!!.moveToNext()
        //Log.d("cursor", cursor.toString())
        val path: String =
            cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
        val uri = Uri.fromFile(File(path))
        cursor.close()
        return path
    }
}