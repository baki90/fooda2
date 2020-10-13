package com.example.fooda2

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
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
        val list: ArrayList<String> = ArrayList()
        list.add("클럽 샌드위치 440kcal/200g")
        list.add("닭가슴살 164kcal/100g")
        list.add("그린 샐러드 148kcal/150g")
        view.recommendlist.adapter = context?.let { AnalyzeAdapter(it, list) }
        analyzeInit(view)
        return view
    }

    private fun analyzeInit(view: View) {
        var retrofit = RetrofitInterface.RetrofitClient.getInstnace()
        var server = retrofit.create(RetrofitInterface::class.java)

        server.get_analyze_person(RetrofitInterface.RetrofitClient.token).enqueue(object:
            Callback<RetrofitInterface.Analyze> {
            override fun onFailure(call: Call<RetrofitInterface.Analyze>, t: Throwable) {
                Log.d("레트로핏 결과1",t.message)
            }
            override fun onResponse(call: Call<RetrofitInterface.Analyze>, response: Response<RetrofitInterface.Analyze>) {
                if (response?.isSuccessful) {
                    val style : String? = response?.body()?.style
                    val tan = response?.body()?.carbohydrate
                    val dan = response?.body()?.protein
                    val gi =response?.body()?.fat
                    view.analyze_type.setText("주간 섭취율을 통해 분석한 결과\n" + style + "타입입니다.")
                    view.analyze_total.setText("탄수화물 " + tan?.toInt().toString() + "g " +  "단백질 " + dan?.toInt().toString() + "g " + "지방 "+ gi?.toInt().toString() + "g")
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


private class AnalyzeAdapter(context: Context, array: ArrayList<String>): BaseAdapter() {

    private var list: ArrayList<String> = array
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        val view: View?
        val viewHolder: ViewHolder4

        if(p1 == null) {
            view = this.inflater.inflate(R.layout.item_analyze, p2,false)
            viewHolder = ViewHolder4(view)
            view.tag = viewHolder
        }
        else {
            view = p1
            viewHolder = view.tag as ViewHolder4
        }

        viewHolder.analyze.text = list[p0]

        return view!!
    }

    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}

private class ViewHolder4(view: View?) {

    val analyze: TextView = view?.findViewById(R.id.analyze_item) as TextView

}
