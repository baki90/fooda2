package com.example.fooda2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RegisterActivity : AppCompatActivity() {
    var sex = 'F'
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_go_btn.setOnClickListener {
            contentSend()
        }
        sex_radio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked == R.id.female) sex = 'F'
            else sex = 'M'
        }
    }

    fun contentSend() {

        var gson : Gson =  GsonBuilder()
            .setLenient()
            .create()

        var retrofit = RetrofitInterface.RetrofitClient.getInstnace()
        var server = retrofit.create(RetrofitInterface::class.java)
        server.post_register(register_email_input.text.toString(), register_password_input.text.toString(),
            register_name_input.text.toString(), register_age_input.text.toString().toInt(),
            register_weight_input.text.toString().toInt(),register_height_input.text.toString().toInt(),
            sex).enqueue(object: Callback<RetrofitInterface.Message> {
            override fun onFailure(call: Call<RetrofitInterface.Message>, t: Throwable) {
                Log.d("레트로핏 결과1",t.message)
            }

            override fun onResponse(call: Call<RetrofitInterface.Message>, response: Response<RetrofitInterface.Message>) {
                if (response?.isSuccessful) {
                    if("success" == (response?.body()?.result)){
                        Toast.makeText(getApplicationContext(), "가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                        RetrofitInterface.RetrofitClient.token = response?.body()?.token!!
                        movepage()
                    } else {
                        Toast.makeText(getApplicationContext(), response?.body()?.msg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }


        })

    }
    fun movepage(){
        startActivity(Intent(this, MenuActivity::class.java))
    }
}