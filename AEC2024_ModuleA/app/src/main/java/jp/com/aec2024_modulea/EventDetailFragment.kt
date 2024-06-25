package jp.com.aec2024_modulea

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.android.material.tabs.TabLayout
import jp.com.aec2024_modulea.databinding.FragmentEventDetailBinding
import java.text.SimpleDateFormat


class EventDetailFragment : Fragment() {
private lateinit var b: FragmentEventDetailBinding
private var seatCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentEventDetailBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
        b.apply {
            back.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .remove(this@EventDetailFragment)
                    .commit()
            }
            buyTicket.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView,SeatFragment())
                    .commit()
            }
        }

    }
    @SuppressLint("Range")
    private fun setView(){
        b.apply {
            val imageFileName = when (eventDetailData.first) {
                "aec-oc" -> "aec-oc.jpeg"
                "aec-cc" -> "aec-cc.jpeg"
                "aeg-beneficiary-concert" -> "aeg-beneficiary-concert.png"
                "pop-concert" -> "pop-concert.png"
                else -> "error"
            }
            val input = requireContext().assets.open(imageFileName)
            val bitmap = BitmapFactory.decodeStream(input)
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val viewDateFormat = SimpleDateFormat("MMM-yy")
            val dateText = viewDateFormat.format(inputDateFormat.parse(eventDetailData.second.date))
            image.load(bitmap)
            eventName.text = eventDetailData.second.name
            date.text = dateText
            location.text = eventDetailData.second.location
            val priceText = if (eventDetailData.second.price != null) eventDetailData.second.price else "Free"
            price.text = priceText
            price.setTextColor(Color.parseColor(eventDetailData.second.color))
            seatCount = 224 - eventDetailData.second.bookedSeats.count()
            freeSeatCount.text = "${seatCount} seats\n available"
            desc.text = eventDetailData.second.description
            val parentTab = requireActivity().findViewById<TabLayout>(R.id.tab)
            parentTab.background = ColorDrawable(Color.parseColor(eventDetailData.second.color))
            buyTicket.backgroundTintList = ColorStateList.valueOf(Color.parseColor(eventDetailData.second.color))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val parentTab = requireActivity().findViewById<TabLayout>(R.id.tab)
        parentTab.background = ColorDrawable(primaryColorValue.data)
    }


}