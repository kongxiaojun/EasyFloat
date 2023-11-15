package com.lzf.easyfloat.example.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.example.R
import com.lzf.easyfloat.example.databinding.ActivityBorderTestBinding

/**
 * @author: liuzhenfeng
 * @date: 3/9/21  11:27
 * @Package: com.lzf.easyfloat.example.activity
 * @Description: 悬浮窗边界测试页面
 */
class BorderTestActivity : BaseActivity() {

    private val tag = "borderTest"

    private lateinit var binding: ActivityBorderTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBorderTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvShow.setOnClickListener { showBorderTest() }
        binding.tvDismiss.setOnClickListener { EasyFloat.dismiss(tag) }
    }

    private fun showBorderTest() {
        EasyFloat.with(this)
            .setTag(tag)
            .setLayout(R.layout.float_border_test) {
                val ivLogo = it.findViewById<ImageView>(R.id.iv_logo)
                val ivLogo2 = it.findViewById<ImageView>(R.id.iv_logo2)
                ivLogo.setOnClickListener {
                    ivLogo2.visibility =
                        if (ivLogo2.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                    EasyFloat.updateFloat(tag)
                }
                ivLogo2.setOnClickListener {
                    ivLogo.visibility =
                        if (ivLogo.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                    EasyFloat.updateFloat(tag)
                }
            }
            .setBorder(binding.viewBg.left, binding.viewBg.top, binding.viewBg.right, binding.viewBg.bottom)
            .setGravity(Gravity.CENTER)
            .setSidePattern(SidePattern.RESULT_SIDE)
            .show()
    }

}