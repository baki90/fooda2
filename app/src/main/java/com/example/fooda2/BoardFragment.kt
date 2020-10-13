package com.example.fooda2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_board.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BoardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BoardFragment : Fragment() {
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
        val view :View = inflater.inflate(R.layout.fragment_board, container, false)
        val list: ArrayList<CustomItem> = ArrayList()
        list.add(CustomItem("건강관리 비법","다들 건강하게 살면 좋겠어서 공유합니다. ^^ \n오늘도 행복한 하루!", "멋쟁이연희", "#식이요법 #건강관리"))
        list.add(CustomItem("다들 맛난 밥 먹으시나용","오늘도 맛있는 저녁을 하려고 했는데 제대로 되진 않네요 ㅠㅠ\n 특별한 방법 있으면 공유 바라요~!", "현지맘", "#식단관리"))
        list.add(CustomItem("3대 500 어렵네요","근손실이 오지 않게 오늘도 노력하겠습니다.", "주영간지", "#헬스 #다이어트"))

        view.board_list.adapter = context?.let { CustomAdapter(it, list) }

        view.floatingActionButton2.setOnClickListener {
                val intent = Intent(context, BoarduploadActivity::class.java)
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
         * @return A new instance of fragment BoardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BoardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}


private class CustomAdapter(context: Context, array: ArrayList<CustomItem>): BaseAdapter() {

    private var list: ArrayList<CustomItem> = array
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        val view: View?
        val viewHolder: ViewHolder

        if(p1 == null) {
            view = this.inflater.inflate(R.layout.item_board, p2,false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }
        else {
            view = p1
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.title.text = list[p0].title
        viewHolder.content.text = list[p0].content
        viewHolder.username.text = list[p0].username
        viewHolder.hashtag.text = list[p0].hashtag

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

private class ViewHolder(view: View?) {

    val title: TextView = view?.findViewById(R.id.item_title) as TextView
    val content: TextView = view?.findViewById(R.id.item_content) as TextView
    val username: TextView = view?.findViewById(R.id.item_username) as TextView
    val hashtag: TextView = view?.findViewById(R.id.item_hashtag) as TextView
}

data class CustomItem(var title: String, var content: String, var username: String, var hashtag: String)
