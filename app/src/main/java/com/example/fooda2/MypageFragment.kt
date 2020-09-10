package com.example.fooda2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MypageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MypageFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)
        // Inflate the layout for this fragment
        val list: ArrayList<CustomItem3> = ArrayList()
        list.add(CustomItem3("사용자 설정","개인정보 변경이 가능합니다."))
        list.add(CustomItem3("게시글","사용자가 쓴 게시글 확인."))
        list.add(CustomItem3("고객센터","문의사항 설정 가능."))
        view.mypage_listview.adapter = context?.let { CustomAdapter3(it, list) }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MypageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MypageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

private class CustomAdapter3(context: Context, array: ArrayList<CustomItem3>): BaseAdapter() {

    private var list: ArrayList<CustomItem3> = array
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        val view: View?
        val viewHolder: ViewHolder3

        if(p1 == null) {
            view = this.inflater.inflate(R.layout.item_mypage, p2,false)
            viewHolder = ViewHolder3(view)
            view.tag = viewHolder
        }
        else {
            view = p1
            viewHolder = view.tag as ViewHolder3
        }

        viewHolder.title.text = list[p0].title
        viewHolder.dis.text = list[p0].dis

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

private class ViewHolder3(view: View?) {

    val title: TextView = view?.findViewById(R.id.mp_title) as TextView
    val dis: TextView = view?.findViewById(R.id.mp_dis) as TextView

}

data class CustomItem3(var title: String, var dis: String)