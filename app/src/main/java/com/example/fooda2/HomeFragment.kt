package com.example.fooda2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_board.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val list: ArrayList<CustomItem2> = ArrayList()
    val fooday : List<String> = listOf<String>("아침", "점심", "저녁","간식")
    
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
        // Inflate the layout for th`is fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        val now = System.currentTimeMillis();
        val date : Date = Date(now)
        val datetime : String = SimpleDateFormat("yyyy-MM-dd").format(date)
        Log.d("time", datetime)
        home_init(datetime, view)
        val mFormat : SimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
        val time : String = mFormat.format(date)

        view.home_date_txt.text = time

        //list.add(CustomItem2("아침","햄버거", "619kcal / 278g"))
        //list.add(CustomItem2("점심","비빔밥", "586kcal / 400g"))


        view.home_list.adapter = context?.let { CustomAdapter2(it, list) }
        view.cal_btn.setOnClickListener { view ->
            val intent = Intent(context, MonthActivity::class.java)
            startActivity(intent)
        }

        view.home_fab.setOnClickListener{
            view ->
            val intent = Intent(context, PlusActivity::class.java)
            startActivity(intent)
        }

       // view.home_toolbar.bringChildToFront(view)

        return view
    }

    private fun home_init(datetime: String, view : View) {

        var retrofit = RetrofitInterface.RetrofitClient.getInstnace()
        var server = retrofit.create(RetrofitInterface::class.java)

        server.get_total_diet(RetrofitInterface.RetrofitClient.token,datetime).enqueue(object:
            Callback<RetrofitInterface.Nutrient> {
            override fun onFailure(call: Call<RetrofitInterface.Nutrient>, t: Throwable) {
                Log.d("레트로핏 결과1",t.message)
            }
            override fun onResponse(call: Call<RetrofitInterface.Nutrient>, response: Response<RetrofitInterface.Nutrient>) {
                if (response?.isSuccessful) {
                    val kcal = response?.body()?.calories?.toInt()
                    val tan = response?.body()?.carbohydrate
                    val dan = response?.body()?.protein
                    val gi =response?.body()?.fat
                    val kicho = response?.body()?.kicho?.toInt()

                    view.home_total.setText(kcal.toString() + " / " + kicho + "\n잔여 칼로리 " + (kicho!!-kcal!!) + "kcal")
                    view.home_cal.setText("탄수화물\n"+tan.toString()+'g')
                    view.home_pro.setText("단백질\n"+dan.toString()+'g')
                    view.home_fat.setText("지방\n"+gi.toString()+'g')

                    Log.d("레트로핏 결과2",""+response?.body().toString())
                } else {
                }
            }
        })

        server.get_total_diet_list(RetrofitInterface.RetrofitClient.token, datetime).enqueue(object: Callback<List<RetrofitInterface.foodlist>>{
            override fun onFailure(call: Call<List<RetrofitInterface.foodlist>>, t: Throwable) {
                Log.d("레트로핏 결과1",t.message)
            }
            override fun onResponse(call: Call<List<RetrofitInterface.foodlist>>, response: Response<List<RetrofitInterface.foodlist>>) {
                if (response?.isSuccessful) {
                    val i = 0
                    for(res in response.body()!!){
                        list.add(CustomItem2(fooday[res.day], res.food_name, res.cal.toInt().toString() + "kcal / "  +res.gram.toInt().toString() +"g"))
                    }
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

private class CustomAdapter2(context: Context, array: ArrayList<CustomItem2>): BaseAdapter() {

    private var list: ArrayList<CustomItem2> = array
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        val view: View?
        val viewHolder: ViewHolder2

        if(p1 == null) {
            view = this.inflater.inflate(R.layout.item_home, p2,false)
            viewHolder = ViewHolder2(view)
            view.tag = viewHolder
        }
        else {
            view = p1
            viewHolder = view.tag as ViewHolder2
        }

        viewHolder.food_date.text = list[p0].date
        viewHolder.food_name.text = list[p0].foodname
        viewHolder.food_kcal.text = list[p0].kcal

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

private class ViewHolder2(view: View?) {

    val food_date: TextView = view?.findViewById(R.id.item_date) as TextView
    val food_name: TextView = view?.findViewById(R.id.item_foodname) as TextView
    val food_kcal: TextView = view?.findViewById(R.id.item_kcal) as TextView
}

data class CustomItem2(var date: String, var foodname: String, var kcal: String)