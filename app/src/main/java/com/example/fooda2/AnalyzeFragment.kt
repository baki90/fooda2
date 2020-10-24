package com.example.fooda2

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.diet_alert.*
import kotlinx.android.synthetic.main.fragment_analyze.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnalyzeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnalyzeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view :View = inflater.inflate(R.layout.fragment_analyze, container, false)
        analyzeInit(view)
        view.sna.setOnClickListener {
            showPopUp()
        }
        return view
    }

    private fun analyzeInit(view: View) {
        var retrofit = RetrofitInterface.RetrofitClient.getInstnace()
        var server = retrofit.create(RetrofitInterface::class.java)

        server.get_analyze_total(RetrofitInterface.RetrofitClient.token).enqueue(object:
            Callback<RetrofitInterface.Total> {
            override fun onFailure(call: Call<RetrofitInterface.Total>, t: Throwable) {
                Log.d("레트로핏 결과1",t.message)
            }
            override fun onResponse(call: Call<RetrofitInterface.Total>, response: Response<RetrofitInterface.Total>) {
                if (response?.isSuccessful) {
                    val mon = response.body()?.mon
                    val lun = response.body()?.lun
                    val din = response.body()?.din
                    val sna = response.body()?.sna
                    val style = response.body()?.style
                    view.analyze_type2.setText(style)
                    val total = mon!! + lun!! + din!!
                    view.total_eat.setText("총 식사 횟수: " + total.toString() +"회")
                    view.mon.setText("아침\n" + mon.toString() + "회")
                    view.lun.setText("점심\n" +lun.toString()+ "회")
                    view.din.setText("저녁\n" +din.toString()+ "회")
                    view.sna.setText("간식\n" +sna.toString()+ "회")
                    //view.analyze_total.setText("탄수화물 " + tan?.toInt().toString() + "g " +  "단백질 " + dan?.toInt().toString() + "g " + "지방 "+ gi?.toInt().toString() + "g")
                    Log.d("레트로핏 결과2",""+response?.body().toString())
                } else {
                }
            }
        })

        server.get_analyze_image(RetrofitInterface.RetrofitClient.token).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("레트로핏 결과1",t.message)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response?.isSuccessful) {
                    if(response.body() != null){
                        val bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())
                        view.analyzeImage.setImageResource(0);
                        view.analyzeImage.setImageBitmap(bmp)
                    }
                    Log.d("레트로핏 결과2",""+response?.body().toString())
                } else {
                }
            }
        })
    }

    private fun showPopUp(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("섭취 일자")
        builder.setMessage("2020-10-23 햄버거 426kcal")
        builder.setPositiveButton("확인", null)

        builder.show()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AnalyzeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnalyzeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

