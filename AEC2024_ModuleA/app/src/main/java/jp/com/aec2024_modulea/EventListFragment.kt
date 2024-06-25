package jp.com.aec2024_modulea

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.tabs.TabLayout
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import jp.com.aec2024_modulea.databinding.EventListItemBinding
import jp.com.aec2024_modulea.databinding.FragmentEventListBinding
import java.io.File
import java.io.InputStream
import java.lang.reflect.Type

class EventListFragment : Fragment() {
    private lateinit var b: FragmentEventListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentEventListBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val input = requireContext().assets.open("Data.json")
        createFile(input)
        getData()
        b.apply {
            eventCount.text = "${allData.events.size} events found"
            pageTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab!!.position){
                        0 -> changeFragment(ListFragment())
                        1 -> changeFragment(CalenderFragment())
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })
        }
    }



    private fun createFile(input: InputStream) {
        eventFile = File(requireContext().filesDir, "event.json")
        if (!eventFile.exists()) {
            eventFile.outputStream().use { input.copyTo(it) }
        }
    }


    private fun changeFragment(fm: Fragment){
        childFragmentManager.beginTransaction()
            .replace(R.id.homeContainerView,fm)
            .commit()
    }
}
fun getData() {
    val textData = eventFile.inputStream().bufferedReader().use { it.readText() }
    println(textData)
    allData = gson.fromJson(textData,object: TypeToken<EventWrapper>(){}.type)
    println(allData)
    ticketData = allData.myTickets.toMutableList()

}

