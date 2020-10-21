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
        list.add(CustomItem("하루에 물 2리터 먹기","음식물을 통해 하루 0.5ℓ 정도의 수분이 섭취된다고 하니, 결국 물을 적어도 2ℓ를 마셔야 한다는 결론이 나옵니다. \n세계보건기구(WHO)가 권장하는 하루 물 섭취량은 1.5~2ℓ입니다. \n200mℓ가 들어가는 일반적인 컵으로 약 8~10잔 정도입니다",
            "멋쟁이연희", "#하루에물2리터 #건강관리"))
        list.add(CustomItem("빈혈에 좋은 음식","자가진단을 통해 빈혈이 의심된다면, 지금부터 소개하는 빈혈에 좋은 음식을 꾸준히 섭취해보세요\n" +
                "소고기 빈혈에 좋은 대표적인 음식은 붉은 살코기다.\n" +
                "달걀노른자 빈혈 예방 특히, 철 결핍성 빈혈에는 달걀노른자가 좋다.\n" +
                "미역\n" +
                "시금치\n" +
                "브로콜리\n" +
                "레드비트\n" +
                "피조개\n" +
                "굴", "현지맘", "#빈혈에 좋은 음식"))
        list.add(CustomItem("감기에 좋은 음식","파 넣은 콩나물국 콩나물은 몸의 열을 내려주고 간 기능을 회복시켜 감기를 다스리는 데 효과적이다", "주영간지", "#헬스 #다이어트"))
        list.add(CustomItem("당뇨에 좋은 음식","오트밀\n" +
                "통곡물 빵\n" +
                "당근, 녹두 같은 전분을 포함하지 않는 채소\n" +
                "콩류\n" +
                "고구마\n" +
                "통곡물 파스타\n" +
                "껍질을 벗긴 닭고기\n" +
                "냉수성 어류", "주영간지", "#당뇨"))

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
