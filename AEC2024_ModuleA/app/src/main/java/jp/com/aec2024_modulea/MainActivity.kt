package jp.com.aec2024_modulea

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import jp.com.aec2024_modulea.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        window.statusBarColor = resources.getColor(R.color.event_back,theme)
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary,primaryColorValue,true)
        b.apply {
            tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab!!.position) {
                        0 -> changeFragment(EventListFragment())
                        1 -> changeFragment(TicketListFragment())
                    }
                    b.tab.background = ColorDrawable(primaryColorValue.data)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })
        }
    }

    private fun changeFragment(fm: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fm)
            .commit()
    }
}