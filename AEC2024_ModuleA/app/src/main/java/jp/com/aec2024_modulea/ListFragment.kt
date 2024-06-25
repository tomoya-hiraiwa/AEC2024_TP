package jp.com.aec2024_modulea

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import jp.com.aec2024_modulea.databinding.EventListItemBinding
import jp.com.aec2024_modulea.databinding.FragmentListBinding
import java.text.SimpleDateFormat


class ListFragment : Fragment() {
    private lateinit var b: FragmentListBinding
    private lateinit var eventListAdapter: EventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentListBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            list.layoutManager = GridLayoutManager(requireContext(), 2)
            eventListAdapter = EventListAdapter(allData.events, requireContext()) { item ->
                eventDetailData = Pair(item.first, item.second)
                val manager = requireParentFragment().parentFragmentManager
                manager.beginTransaction()
                    .add(R.id.fragmentContainerView, EventDetailFragment())
                    .commit()
            }
            list.adapter = eventListAdapter

        }

    }
}

class EventListAdapter(
    private val dataList: Map<String, EventData>,
    private val context: Context,
    val onClick: (Pair<String, EventData>) -> Unit
) : RecyclerView.Adapter<EventListAdapter.EventListViewHolder>() {
    inner class EventListViewHolder(private val b: EventListItemBinding) :
        RecyclerView.ViewHolder(b.root) {
        @SuppressLint("Range")
        @RequiresApi(Build.VERSION_CODES.S)
        fun bindData(data: Pair<String, EventData>) {
            val imageFileName = when (data.first) {
                "aec-oc" -> "aec-oc.jpeg"
                "aec-cc" -> "aec-cc.jpeg"
                "aeg-beneficiary-concert" -> "aeg-beneficiary-concert.png"
                "pop-concert" -> "pop-concert.png"
                else -> "error"
            }
            val input = context.assets.open(imageFileName)
            val bitmap = BitmapFactory.decodeStream(input)
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val viewDateFormat = SimpleDateFormat("MMM-yy")
            val dateText = viewDateFormat.format(inputDateFormat.parse(data.second.date))

            b.apply {
                image.load(bitmap)
                date.text = dateText
                title.text = data.second.name
                divider.dividerColor = Color.parseColor(data.second.color)
                root.setOnClickListener {
                    onClick(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventListViewHolder {
        return EventListViewHolder(
            EventListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(holder: EventListViewHolder, position: Int) {
        val data = dataList.toList()
        holder.bindData(data = data[position])
    }
}
