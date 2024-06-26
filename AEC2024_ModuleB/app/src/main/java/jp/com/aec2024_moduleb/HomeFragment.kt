package jp.com.aec2024_moduleb

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import coil.load
import com.google.gson.reflect.TypeToken
import jp.com.aec2024_moduleb.databinding.FragmentHomeBinding
import jp.com.aec2024_moduleb.databinding.FriendItemBinding
import kotlin.math.cos
import kotlin.math.sin

class HomeFragment : Fragment() {
    private lateinit var b: FragmentHomeBinding
    private lateinit var pref: SharedPreferences
    var web =  ""
    var mail = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentHomeBinding.inflate(inflater)
        return b.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
        mail = pref.getString("mail","")!!
        web = pref.getString("web","")!!
        val input = requireContext().assets.open("Data.json")
        peopleList = gson.fromJson(input.bufferedReader().use { it.readText() },
            object : TypeToken<Peoples>() {}.type
        )
        b.friendFrame.doOnLayout {
            createFriendView()
        }
        b.apply {
            val render = RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.MIRROR)
            backImage.setRenderEffect(render)
            //Navigation back button custom but cannot close app in this code
            view?.isFocusableInTouchMode = true
            view?.requestFocus()
            view?.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                    if (b.friendDetailFrame.isVisible) {
                        b.friendDetailFrame.isVisible = false
                    }
                    else if (b.m.root.isVisible){
                        b.m.detailFrame.visibility = View.VISIBLE
                        b.m.qrFrame.visibility = View.GONE
                        web = b.m.webEdit.text.toString()
                        mail = b.m.mailEdit.text.toString()
                        b.m.webText.isVisible = true
                        b.m.webEdit.isVisible = false
                        b.m.mailText.isVisible = true
                        b.m.mailEdit.isVisible = false
                        b.m.root.isVisible = false
                    }
                    return@setOnKeyListener true
                } else true

            }
        }
    }

    private fun createFriendView() {
        var r1FriendList = peopleList.people.subList(0, 12)
        var r2FriendList = peopleList.people.subList(12, 24)
        var r3FriendList = peopleList.people.subList(24, 36)
        b.friendFrame.removeAllViews()
        createRoundView(280, 0, r1FriendList)
        createRoundView(380, 15, r2FriendList)
        createRoundView(480, 0, r3FriendList)
    }

    private fun createRoundView(radius: Int, startAngle: Int, peopleList: MutableList<People>) {
        b.apply {

            val centerX = friendFrame.x + friendFrame.width / 2
            val centerY = friendFrame.y + friendFrame.height / 2
            b.lineFrame.startX = centerX
            b.lineFrame.startY = centerY
            val angleStep = 30
            var nowAngle = startAngle
            for (i in 0 until peopleList.size) {
                val data = peopleList[i]
                val angle = Math.toRadians(nowAngle.toDouble())
                val posX = (centerX + radius * cos(angle)).toFloat()
                val posY = (centerY + radius * sin(angle)).toFloat()
                val view = FriendItemBinding.inflate(layoutInflater)
                //use add friend data.
                val isFriend = pref.getBoolean(data.name, false)
                view.apply {
                    val imageInput = requireContext().assets.open("${data.name}.jpeg")
                    val bitmap = BitmapFactory.decodeStream(imageInput)
                    view.userImage.load(bitmap)
                    if (isFriend) {
                        val params = view.imageFrame.layoutParams as FrameLayout.LayoutParams
                        params.setMargins(5)
                        imageFrame.layoutParams = params
                    }
                    view.friendCardFrame.setOnClickListener {
                        //clickListener
                        selectPeople = data
                        setFriendDetail()
                    }
                    skillNo.text = data.skillId
                }
                val params = FrameLayout.LayoutParams(100, 100)
                view.root.layoutParams = params
                b.friendFrame.addView(view.root)
                view.root.doOnLayout {
                    view.root.x = posX - view.root.width / 2
                    view.root.y = posY - view.root.height / 2
                    println(view.root.width)
                    println(view.root.height)
                }
                b.lineFrame.addLineData(Position(posX, posY))
                nowAngle += angleStep
                if (view.skillNo.text == "08") alphaAnimation(view.root, false)
                else alphaAnimation(view.root, true)
                b.myFrame.setOnClickListener {
                    myProfSet()
                }
            }
        }
    }

    private fun alphaAnimation(view: View, isDelay: Boolean) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 0.7f, 1.0f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 0.7f, 1.0f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
            duration = 5000
            if (isDelay) startDelay = 2500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
        }
        animator.start()
    }

    private fun setFriendDetail() {
        b.apply {
            val userInput = requireContext().assets.open("${selectPeople.name}.jpeg")
            val flagInput = requireContext().assets.open("${selectPeople.countryId}.png")
            val userBitmap = BitmapFactory.decodeStream(userInput)
            val flagBitmap = BitmapFactory.decodeStream(flagInput)
            userImage.load(userBitmap)
            flagImage.load(flagBitmap)
            namText.text = selectPeople.name
            posText.text = selectPeople.role
            skillText.text = "Skill${selectPeople.skillId}: ${returnSkillName(selectPeople.skillId)}"
            friendDetailFrame.isVisible = true
            val isFriend = pref.getBoolean("${selectPeople.name}", false)
            val params = detailUserFrame.layoutParams as FrameLayout.LayoutParams
            if (isFriend) {
                friendButton.setImageResource(R.drawable.remove_friend_button)
                params.setMargins(10)
            } else {
                friendButton.setImageResource(R.drawable.add_friend_button)
                params.setMargins(0)
            }
            detailUserFrame.layoutParams = params
            friendButton.setOnClickListener {
                pref.edit().apply {
                    putBoolean("${selectPeople.name}", !isFriend)
                        .commit()
                }
                createFriendView()
                friendDetailFrame.isVisible = false
            }
        }
    }

    private fun myProfSet(){
        b.m.apply {
            root.isVisible = true
            root.setOnClickListener {
                detailFrame.visibility = View.VISIBLE
                qrFrame.visibility = View.GONE
                web = webEdit.text.toString()
                mail = mailEdit.text.toString()
                webText.isVisible = true
                webEdit.isVisible = false
                mailText.isVisible = true
                mailEdit.isVisible = false
                root.isVisible = false
            }
            webText.text = web
            webEdit.setText(web)
            mailText.text = mail
            mailEdit.setText(mail)
            editButton.setOnClickListener {
                webText.isVisible = false
                webEdit.isVisible = true
                mailText.isVisible = false
                mailEdit.isVisible = true
            }
            qrButton.setOnClickListener {
                detailFrame.visibility = View.GONE
                qrFrame.visibility = View.VISIBLE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pref.edit().apply {
            putString("web",web)
            putString("mail",mail)
            commit()
        }
    }

}


class LineView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.orange)
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }
    var startX = 0f
    var startY = 0f

    private val lineData = mutableListOf<Position>()

    fun addLineData(data: Position) {
        lineData.add(data)
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        lineData.forEach { item ->
            canvas!!.drawLine(startX, startY, item.x, item.y, paint)
        }
    }
}

