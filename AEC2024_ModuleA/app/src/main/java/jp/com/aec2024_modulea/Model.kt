package jp.com.aec2024_modulea

import android.util.TypedValue
import com.google.gson.Gson
import java.io.File

val gson = Gson()
var ticketData = mutableListOf<MyTicket>()
lateinit var eventFile: File
var allData = EventWrapper()
var eventDetailData: Pair<String,EventData> = Pair("", EventData())
val primaryColorValue = TypedValue()

data class EventData(
    var name: String = "",
    var date: String = "",
    var location: String = "",
    var color: String = "",
    var price: String? = null,
    var description: String = "",
    var bookedSeats: MutableList<Seat> = mutableListOf()
)

data class Seat(
    var row: Int = 0,
    var seat: Int = 0
)

data class EventWrapper(
    var events: Map<String, EventData> = mapOf(),
    var myTickets: MutableList<MyTicket> = mutableListOf()
)

data class MyTicket(
    var eventId: String = "",
    var seats: MutableList<Seat> = mutableListOf()
)

data class SeatView(var row: Int = 0, var seat: Int = 0, var color: Int = 0)



