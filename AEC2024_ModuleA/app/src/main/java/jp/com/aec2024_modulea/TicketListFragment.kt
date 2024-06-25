package jp.com.aec2024_modulea

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.google.android.material.tabs.TabLayout
import jp.com.aec2024_modulea.databinding.FragmentTicketListBinding
import jp.com.aec2024_modulea.databinding.TicketPagaerItemBinding
import jp.com.aec2024_modulea.databinding.TicketSeatItemBinding
import java.text.SimpleDateFormat


class TicketListFragment : Fragment() {
  private lateinit var b: FragmentTicketListBinding
  private var currentEventData = EventData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentTicketListBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        b.apply { 
         pager.adapter = TicketPagerAdapter(ticketData,requireContext())
            pager.offscreenPageLimit = 2
            pager.setPageTransformer { page, position ->
                val dimen = requireContext().resources.getDimensionPixelOffset(R.dimen.pager_margin)
                page.translationX = -position*(2*dimen)
            }
            pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                @SuppressLint("Range")
                override fun onPageSelected(position: Int) {
                    val ticketData = allData.myTickets[position]
                    currentEventData = allData.events.get(ticketData.eventId)!!
                    val parentTab = requireActivity().findViewById<TabLayout>(R.id.tab)
                    parentTab.background = ColorDrawable(Color.parseColor(currentEventData.color))
                   ticketInd.setTabTextColors(Color.parseColor(currentEventData.color),requireContext().resources.getColor(R.color.white,requireActivity().theme))
                    ticketInd.setSelectedTabIndicatorColor(Color.parseColor(currentEventData.color))
                    val backDr = ticketInd.background as GradientDrawable
                    backDr.mutate()
                    backDr.setStroke(3,Color.parseColor(currentEventData.color))
                }
            })
        }
    }
}

class TicketPagerAdapter(private val ticketList: MutableList<MyTicket>,private val context: Context): RecyclerView.Adapter<TicketPagerAdapter.TicketViewHolder>(){
    inner class TicketViewHolder(private val b: TicketPagaerItemBinding): RecyclerView.ViewHolder(b.root){
        @SuppressLint("Range")
        fun bindData(data: MyTicket){
            val eventData = allData.events.get(data.eventId)
            b.apply {
                val imageFileName = when (data.eventId) {
                    "aec-oc" -> "aec-oc.jpeg"
                    "aec-cc" -> "aec-cc.jpeg"
                    "aeg-beneficiary-concert" -> "aeg-beneficiary-concert.png"
                    "pop-concert" -> "pop-concert.png"
                    else -> "error"
                }
                val input = context.assets.open(imageFileName)
                val bitmap = BitmapFactory.decodeStream(input)
                image.load(bitmap)
                divider.dividerColor = Color.parseColor(eventData!!.color)
                name.text = eventData.name
                val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
                val viewDateFormat = SimpleDateFormat("MMM-yy")
                val dateText = viewDateFormat.format(inputDateFormat.parse(eventData.date))
                date.text = dateText
                location.text = eventData.location
                seatFrame.removeAllViews()
                for (i in 0 until data.seats.size){
                    val seatData = data.seats[i]
                    val view = TicketSeatItemBinding.inflate(LayoutInflater.from(context),seatFrame,false)
                    view.apply {
                        rowText.text = seatData.row.toString()
                        seatText.text = seatData.seat.toString()
                    }
                    seatFrame.addView(view.root)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        return TicketViewHolder(TicketPagaerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return ticketList.size
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bindData(data = ticketList[position])
    }
}