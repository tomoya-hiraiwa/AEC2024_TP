package jp.com.aec2024_moduleb

import com.google.gson.Gson

data class People(var name: String = "",var countryId:String = "",var role: String = "",var skillId: String = "")


data class Peoples(var people: MutableList<People> = mutableListOf())

data class Position(var x: Float,var y: Float)

var peopleList = Peoples()

var selectPeople = People()

fun returnSkillName(id: String): String{
    return when(id){
        "08"-> "Mobile Applications Development"
        "09" -> "IT Software Solutions for Business"
        else -> "Other"
    }
}

fun returnCountries(id: String): String{
    return when(id){
        "CH" -> "Switzerland"
        "DE" -> "Germany"
        "FI" -> "Finland"
        "FR" -> "France"
        "HR" -> "Croatia"
        "HU" -> "Hungary"
        "KR" -> "Korea"
        "KZ" -> "Kazakhstan"
        "LI" -> "Liechtenstein"
        "TW" -> "Chinese Taipei"
        else -> "Other Country"
    }
}

