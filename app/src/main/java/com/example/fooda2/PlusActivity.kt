package com.example.fooda2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import java.text.SimpleDateFormat
import java.util.*

class PlusActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var food_id : Int?= 0
    var day : Int = 0
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
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
            }
            else {
                Toast.makeText(this, "권한 설정을 완료해 주세요.",Toast.LENGTH_LONG).show()
            }
        }

        plus_analyze_btn.setOnClickListener {
            dietAnalyze()
        }

        plus_diet_upload_btn.setOnClickListener {
            dietUplaod()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
        var file = File(getRealPathFromURI(photoUri))
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
        val path: String =
            cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
        val uri = Uri.fromFile(File(path))
        cursor.close()
        return path
    }
}