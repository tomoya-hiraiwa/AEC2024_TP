package jp.com.aec2024_modulea

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.tabs.TabLayout
import jp.com.aec2024_modulea.databinding.ExpirationDialogBinding
import jp.com.aec2024_modulea.databinding.FragmentSeatBinding
import jp.com.aec2024_modulea.databinding.SeatItemBinding


class SeatFragment : Fragment() {
    private lateinit var b: FragmentSeatBinding
    private var seatData = mutableListOf<SeatView>()
    private var selectSeat = mutableListOf<Seat>()
    private lateinit var seatAdapter: SeatListAdapter
    private var isSelect = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentSeatBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createSeatData()
        setInitView()
        b.apply {

        }
    }

    @SuppressLint("Range")
    private fun setInitView() {
        b.apply {
            seatList.layoutManager = GridLayoutManager(requireContext(), 16)
            seatAdapter =
                SeatListAdapter(seatData, eventDetailData.second.bookedSeats, requireContext()) {
                    if (selectSeat.contains(it)) {
                        selectSeat.remove(it)
                    } else selectSeat.add(it)
                    seatAdapter.notifyDataSetChanged()
                    println(selectSeat)
                }
            seatList.adapter = seatAdapter

            divider.dividerColor = Color.parseColor(eventDetailData.second.color)
            val imageFileName = when (eventDetailData.first) {
                "aec-oc" -> "aec-oc.jpeg"
                "aec-cc" -> "aec-cc.jpeg"
                "aeg-beneficiary-concert" -> "aeg-beneficiary-concert.png"
                "pop-concert" -> "pop-concert.png"
                else -> "error"
            }
            val input = requireContext().assets.open(imageFileName)
            val bitmap = BitmapFactory.decodeStream(input)
            image.load(bitmap)
            back.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .remove(this@SeatFragment)
                    .commit()
            }
            title.text = eventDetailData.second.name
            fab.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(eventDetailData.second.color))
            fab.setOnClickListener {
                if (!isSelect) {
                    fabSelect()

                } else {
                    if (!nameBox.text.isNullOrEmpty() && !familyNameBox.text.isNullOrEmpty() && !cardNumberBox.text.isNullOrEmpty() && !                        cvcBox.text.isNullOrEmpty() && expiration.text != "Expiration") {
                        val addSeatData = MyTicket(eventDetailData.first, selectSeat)
                        allData.myTickets.add(addSeatData)
                        val targetEvent = allData.events[eventDetailData.first]
                        targetEvent!!.bookedSeats.addAll(selectSeat)
                        val newData = gson.toJson(allData)
                        eventFile.outputStream().use {
                            it.write(newData.toByteArray())
                            it.flush()
                        }
                        val parenTab = requireActivity().findViewById<TabLayout>(R.id.tab)
                        parenTab.getTabAt(1)?.select()
                    } else Toast.makeText(
                        requireContext(),
                        "Please enter your all Information.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun fabSelect() {
        if (selectSeat.size != 0) {
            //do user data page
            isSelect = true
            b.apply {
                println("call fab")
                seatFrame.visibility = View.GONE
                requestText.text = "Enter your details:"
                userFrame.visibility = View.VISIBLE
                fab.text = "Confirm"
                expirationFrame.setOnClickListener {
                    expirationDialog {
                        expiration.text = it
                        expiration.setTextColor(requireContext().getColor(R.color.black))
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "Please select at least one seat", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun expirationDialog(conf: (String) -> Unit) {
        Dialog(requireContext()).apply {
            val db = ExpirationDialogBinding.inflate(layoutInflater)
            setContentView(db.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            db.apply {
                monthNo.minValue = 7
                monthNo.maxValue = 12
                yearNo.minValue = 24
                yearNo.maxValue = 32
                confButton.setOnClickListener {
                    val text = "${monthNo.value}/${yearNo.value}"
                    conf(text)
                    cancel()
                }
                close.setOnClickListener {
                    cancel()
                }
                setCancelable(false)
            }
        }.show()
    }

    private fun createRow(
        row: Int,
        c1C: Int = 0,
        c2: Int = 0,
        c2C: Int = 0,
        c3: Int = 0,
        c3C: Int = 0,
        c4: Int = 0,
        c4C: Int = 0,
        c5: Int = 0,
        c5C: Int = 0,
        c6: Int = 0,
        c6C: Int = 0,
        c7: Int = 0,
        c7C: Int = 0
    ): MutableList<SeatView> {
        var rowList = mutableListOf<SeatView>()
        var color = resources.getColor(c1C, requireActivity().theme)
        for (i in 1 until 17) {
            if (c2 == i) color = resources.getColor(c2C, requireActivity().theme)
            else if (c3 == i) color = resources.getColor(c3C, requireActivity().theme)
            else if (c4 != 0 && c4 == i) color = resources.getColor(c4C, requireActivity().theme)
            else if (c5 != 0 && c5 == i) color = resources.getColor(c5C, requireActivity().theme)
            else if (c6 != 0 && c6 == i) color = resources.getColor(c6C, requireActivity().theme)
            else if (c7 != 0 && c7 == i) color = resources.getColor(c7C, requireActivity().theme)
            val seatView = SeatView(row = row, seat = i, color = color)
            rowList.add(seatView)
        }
        return rowList
    }

    private fun createSeatData() {
        val r1List =
            createRow(1, R.color.t3, 2, R.color.t2, 4, R.color.t1, 14, R.color.t2, 16, R.color.t3)
        val r2List =
            createRow(2, R.color.t3, 2, R.color.t2, 4, R.color.t1, 14, R.color.t2, 16, R.color.t3)
        val r3List =
            createRow(3, R.color.t3, 2, R.color.t2, 4, R.color.t1, 14, R.color.t2, 16, R.color.t3)
        val r4List =
            createRow(4, R.color.t3, 2, R.color.t2, 5, R.color.t1, 13, R.color.t2, 16, R.color.t3)
        val r5List =
            createRow(5, R.color.t3, 3, R.color.t2, 5, R.color.t1, 13, R.color.t2, 15, R.color.t3)
        val r6List =
            createRow(6, R.color.t3, 3, R.color.t2, 5, R.color.t1, 13, R.color.t2, 15, R.color.t3)
        val r7List =
            createRow(7, R.color.t3, 3, R.color.t2, 6, R.color.t1, 12, R.color.t2, 15, R.color.t3)
        val r8List =
            createRow(8, R.color.t3, 4, R.color.t2, 6, R.color.t1, 12, R.color.t2, 14, R.color.t3)
        val r9List =
            createRow(9, R.color.t3, 4, R.color.t2, 7, R.color.t1, 11, R.color.t2, 14, R.color.t3)
        val r10List = createRow(
            10,
            R.color.t4,
            2,
            R.color.t3,
            5,
            R.color.t2,
            8,
            R.color.t1,
            10,
            R.color.t2,
            13,
            R.color.t3,
            16,
            R.color.t4
        )
        val r11List =
            createRow(11, R.color.t4, 2, R.color.t3, 6, R.color.t2, 13, R.color.t3, 16, R.color.t4)
        val r12List =
            createRow(12, R.color.t4, 3, R.color.t3, 6, R.color.t2, 13, R.color.t3, 15, R.color.t4)
        val r13List =
            createRow(13, R.color.t4, 4, R.color.t3, 7, R.color.t2, 11, R.color.t3, 1, R.color.t4)
        val r14List = createRow(14, R.color.t4, 5, R.color.t3, 13, R.color.t4)
        seatData.apply {
            addAll(r1List)
            addAll(r2List)
            addAll(r3List)
            addAll(r4List)
            addAll(r5List)
            addAll(r6List)
            addAll(r7List)
            addAll(r8List)
            addAll(r9List)
            addAll(r10List)
            addAll(r11List)
            addAll(r12List)
            addAll(r13List)
            addAll(r14List)
        }
    }
}


class SeatListAdapter(
    private val seatList: MutableList<SeatView>,
    private val bookedList: List<Seat>,
    private val context: Context,
    val onClick: (Seat) -> Unit
) : RecyclerView.Adapter<SeatListAdapter.SeatViewHolder>() {
    private var selectSeat = mutableListOf<Seat>()

    inner class SeatViewHolder(private val b: SeatItemBinding) : RecyclerView.ViewHolder(b.root) {
        fun bindData(seat: SeatView) {
            b.apply {
                frame.setCardBackgroundColor(seat.color)
                println(selectSeat)
                val booked = bookedList.firstOrNull { it.seat == seat.seat && it.row == seat.row }
                val select = selectSeat.firstOrNull { it.seat == seat.seat && it.row == seat.row }
                if (booked != null) frame.alpha = 0.3f
                if (select != null) frame.setCardBackgroundColor(context.getColor(R.color.seat_select))
                frame.setOnClickListener {
                    if (frame.alpha == 1.0f) {
                        if (select == null) {
                            val addSeat = Seat(seat.row, seat.seat)
                            selectSeat.add(addSeat)
                            onClick(addSeat)
                        } else {
                            val removeSeat = Seat(seat.row, seat.seat)
                            selectSeat.remove(removeSeat)
                            onClick(removeSeat)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        return SeatViewHolder(
            SeatItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return seatList.size
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.bindData(seatList[position])
    }
}