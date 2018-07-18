package cn.lockyluo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import cn.lockyluo.searchfieldview.R
import kotlinx.android.synthetic.main.activity_searchviewdemo.*

class SearchViewDemoActivity : AppCompatActivity() {
    private var view: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = View.inflate(applicationContext, R.layout.activity_searchviewdemo, null)
        setContentView(view)
//        showPopup()
        ll_background.setOnLongClickListener {
            showPopup()
            true
        }
    }

    fun showPopup() {
        val popup = View.inflate(applicationContext, R.layout.popuphistories_layout, null)
        val popupWindow = PopupWindow(popup, LinearLayout.LayoutParams.MATCH_PARENT, 200, true)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.BLUE))
        popupWindow.isTouchable = true

        popupWindow.showAsDropDown(view)
    }
}


