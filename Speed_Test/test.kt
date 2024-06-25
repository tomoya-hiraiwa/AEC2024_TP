fun main(){
    while (true){
       val result = countString("1")
        println(result.first)
        println(result.second)
    }
}

fun countString(text: String):Pair<String,Int>{
    var count = 1
    var lastChar = ""
    var returnString = ""
    var textCoount = 0
    for(i in text.length){
        if (i == 0){
            lastChar = text[0]
        }
        else{
            if (text[i] == lastChar){
                count +=1
                lastChar = text[i]
            }
            else{
                returnString = returnString + "${count}${lastChar}"
                count = 0
                lastChar = text[i]
            }
        }
    }
    textCoount = returnString.length
    return Pair(returnString,textCoount)
}