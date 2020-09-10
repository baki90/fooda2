package com.example.fooda2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_board.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

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
        val list: ArrayList<CustomItem2> = ArrayList()
        list.add(CustomItem2("아침","콩나물 해장국, 배추김치, 오징어무침", "738kcal"))
        list.add(CustomItem2("점심","감자, 고구마, 옥수수", "342kcal"))


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

        return view
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