package com.example.fooda2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class recipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        var intent = getIntent()
        val foodname = intent.getStringExtra("foodname")
        Log.d("음식",foodname)

        initRecipe(foodname)
    }

    private fun initRecipe(foodname: String) {
        var retrofit = RetrofitInterface.RetrofitClient.getInstnace()
        var server = retrofit.create(RetrofitInterface::class.java)

        server.get_recipe(foodname).enqueue(object:
            Callback<RetrofitInterface.Recipe> {
            override fun onFailure(call: Call<RetrofitInterface.Recipe>, t: Throwable) {
                Log.d("레트로핏 결과1",t.message)
            }
            override fun onResponse(call: Call<RetrofitInterface.Recipe>, response: Response<RetrofitInterface.Recipe>) {
                if (response?.isSuccessful) {
                    imageView6.setImageResource(R.drawable.sandwich);

                    var title = response?.body()?.title
                    var level = response?.body()?.level
                    var source = response?.body()?.source
                    var step = response?.body()?.step

                    setRecipe(title, level, source, step)

                    Log.d("레트로핏 결과2",""+response?.body().toString())
                } else {
                    Toast.makeText(getApplicationContext(), "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }
        })

    }

    private fun setRecipe(title: String?, level: String?, source: List<String>?, step: List<String>?) {
        recipe_name.setText(title)
        recipe_level.setText(level)

        var str = "[재료]"
        for (st in source!!) {
            str += st + " "
        }

        recipe_source.setText(str)
        str = "[레시피]\n"
        for(st in step!!){
            str += st + "\n"
    }
        recipe_step.setText(str)
}


}