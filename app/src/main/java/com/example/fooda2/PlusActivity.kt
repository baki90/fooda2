package com.example.fooda2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_plus.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.PI

class PlusActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plus)

        //Initiate storage
        storage = FirebaseStorage.getInstance()

        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }
        else {
            Toast.makeText(this, "권한 설정을 완료해 주세요.",Toast.LENGTH_LONG).show()
        }

        plus_upload_btn.setOnClickListener {
            contentUpload()
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

    fun contentUpload(){
        //make filename
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png" //중복 방지

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this,"사진이 업로드되었습니다.", Toast.LENGTH_LONG).show()
        }
    }
}