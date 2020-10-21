package com.example.fooda2

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.fragment_board.*

class MenuActivity : AppCompatActivity() {
    /*private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val fragment_home = HomeFragment()
                openFragment(fragment_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_analyze -> {
                val fragment_analyze = AnalyzeFragment()
                openFragment(fragment_analyze)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                val fragment_board = BoardFragment()
                openFragment(fragment_board)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_mypage -> {
                val fragment_mypage = MypageFragment()
                openFragment(fragment_mypage)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        navigation.setNavigationChangeListener { view, position ->
            when(position){
                0-> {
                    val fragment_home = HomeFragment()
                    openFragment(fragment_home)
                }
                1-> {
                    val fragment_analyze = AnalyzeFragment()
                    openFragment(fragment_analyze)
                }
                2-> {
                    val fragment_recommend = RecommendFragment()
                    openFragment(fragment_recommend)
                }
                3-> {
                    val fragment_board = BoardFragment()
                    openFragment(fragment_board)
                }
                4->{
                    val fragment_mypage = MypageFragment()
                    openFragment(fragment_mypage)
                }
            }
        }
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        openFragment(HomeFragment()) //초기 세팅은 home으로 함.
        //actionBar.setTitle("Fooda!")
        //hideActionBar()
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun hideActionBar() {
        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            actionBar.hide()
        }
    }


}