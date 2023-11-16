package com.lzf.easyfloat.example.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.example.R
import com.lzf.easyfloat.example.databinding.ActivityMainBinding
import com.lzf.easyfloat.example.logger
import com.lzf.easyfloat.example.startActivity
import com.lzf.easyfloat.example.widget.*
import com.lzf.easyfloat.interfaces.OnPermissionResult
import com.lzf.easyfloat.interfaces.OnTouchRangeListener
import com.lzf.easyfloat.permission.PermissionUtils
import com.lzf.easyfloat.utils.DragUtils
import com.lzf.easyfloat.widget.BaseSwitchView
import kotlin.math.max


class MainActivity : BaseActivity(), View.OnClickListener {

    companion object {
        private const val TAG_1 = "TAG_1"
        private const val TAG_2 = "TAG_2"
        private const val TAG_3 = "TAG_3"
        private const val TAG_4 = "TAG_4"
    }

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.open1.setOnClickListener(this)
        binding.open2.setOnClickListener(this)
        binding.open3.setOnClickListener(this)
        binding.open4.setOnClickListener(this)

        binding.hide1.setOnClickListener(this)
        binding.hide2.setOnClickListener(this)
        binding.hide3.setOnClickListener(this)
        binding.hide4.setOnClickListener(this)

        binding.show1.setOnClickListener(this)
        binding.show2.setOnClickListener(this)
        binding.show3.setOnClickListener(this)
        binding.show4.setOnClickListener(this)

        binding.dismiss1.setOnClickListener(this)
        binding.dismiss2.setOnClickListener(this)
        binding.dismiss3.setOnClickListener(this)
        binding.dismiss4.setOnClickListener(this)

        binding.openSecond.setOnClickListener(this)
        binding.openSwipeTest.setOnClickListener(this)
        binding.openBorderTest.setOnClickListener(this)

        // 测试activity中onCreate就启动浮框
//        showActivityFloat(TAG_1)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.open1 -> showActivityFloat(TAG_1)
            binding.hide1 -> EasyFloat.hide(TAG_1)
            binding.show1 -> EasyFloat.show(TAG_1)
            binding.dismiss1 -> EasyFloat.dismiss(TAG_1)

            binding.open2 -> showActivity2(TAG_2)
            binding.hide2 -> EasyFloat.hide(TAG_2)
            binding.show2 -> EasyFloat.show(TAG_2)
            binding.dismiss2 -> EasyFloat.dismiss(TAG_2)

            // 检测权限根据需求考虑有无即可，权限申请为内部进行
            binding.open3 -> checkPermission()
            binding.hide3 -> EasyFloat.hide()
            binding.show3 -> EasyFloat.show()
            binding.dismiss3 -> EasyFloat.dismiss()

            binding.open4 -> checkPermission(TAG_4)
            binding.hide4 -> EasyFloat.hide(TAG_4)
            binding.show4 -> EasyFloat.show(TAG_4)
            binding.dismiss4 -> EasyFloat.dismiss(TAG_4)

            binding.openSecond -> startActivity<SecondActivity>(this)
            binding.openSwipeTest -> startActivity<SwipeTestActivity>(this)
            binding.openBorderTest -> startActivity<BorderTestActivity>(this)

            else -> return
        }
    }

    /**
     * 测试Callback回调
     */
    @SuppressLint("SetTextI18n")
    private fun showActivityFloat(tag: String) {
        EasyFloat.with(this)
            .setSidePattern(SidePattern.RESULT_HORIZONTAL)
            .setImmersionStatusBar(true)
            .setGravity(Gravity.END, 0, 10)
            // 传入View，传入布局文件皆可，如：MyCustomView(this)、R.layout.float_custom
            .setLayout(MyCustomView(this)) {
                it.findViewById<TextView>(R.id.textView).setOnClickListener { toast() }
            }
            .setTag(TAG_1)
            .registerCallback {
                // 在此处设置view也可以，建议在setLayout进行view操作
                createResult { isCreated, msg, _ ->
                    toast("isCreated: $isCreated")
                    logger.e("DSL:  $isCreated   $msg")
                }

                show { toast("show") }

                hide { toast("hide") }

                dismiss { toast("dismiss") }

                touchEvent { view, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        view.findViewById<TextView>(R.id.textView).apply {
                            text = "拖一下试试"
                            setBackgroundResource(R.drawable.corners_green)
                        }
                    }
                }

                drag { view, motionEvent ->
                    view.findViewById<TextView>(R.id.textView).apply {
                        text = "我被拖拽..."
                        setBackgroundResource(R.drawable.corners_red)
                    }
                    DragUtils.registerDragClose(motionEvent, object : OnTouchRangeListener {
                        override fun touchInRange(inRange: Boolean, view: BaseSwitchView) {
                            setVibrator(inRange)
                        }

                        override fun touchUpInRange() {
                            EasyFloat.dismiss(tag, true)
                        }
                    })
                }

                dragEnd {
                    it.findViewById<TextView>(R.id.textView).apply {
                        text = "拖拽结束"
                        val location = IntArray(2)
                        getLocationOnScreen(location)
                        setBackgroundResource(if (location[0] > 10) R.drawable.corners_left else R.drawable.corners_right)
                    }
                }
            }
            .show()
    }

    private fun showActivity2(tag: String) {
        // 改变浮窗1的文字
        EasyFloat.getFloatView(TAG_1)?.findViewById<TextView>(R.id.textView)?.text = "😆😆😆"

        EasyFloat.with(this)
            .setTag(tag)
            .setGravity(Gravity.CENTER)
            .setLayoutChangedGravity(Gravity.END)
            .setLayout(R.layout.float_seekbar) {
                it.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
                    EasyFloat.dismiss(tag)
                }
                val tvProgress = it.findViewById<TextView>(R.id.tvProgress)
                tvProgress.setOnClickListener { toast(tvProgress.text.toString()) }

                it.findViewById<SeekBar>(R.id.seekBar)
                    .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?, progress: Int, fromUser: Boolean
                        ) {
                            tvProgress.text = progress.toString()
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    })

                val layoutContent = it.findViewById<View>(R.id.layoutContent)
                it.findViewById<TextView>(R.id.viewOther).setOnClickListener {
                    layoutContent.visibility =
                        if (layoutContent.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
            }
            .setDragViewId(R.id.ivFull)
            .show()
    }

    private fun showAppFloat() {
        EasyFloat.with(this.applicationContext)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.RESULT_SIDE)
            .setImmersionStatusBar(true)
            .setGravity(Gravity.CENTER, -20, 10)
            .setDragEnable(true)
            .setLayout(R.layout.float_app) {
                it.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
                    EasyFloat.dismiss()
                }
                it.findViewById<TextView>(R.id.tvOpenMain).setOnClickListener {
                    startActivity<MainActivity>(this)
                }
                it.findViewById<CheckBox>(R.id.checkbox)
                    .setOnCheckedChangeListener { _, isChecked -> EasyFloat.dragEnable(isChecked) }

                val progressBar = it.findViewById<RoundProgressBar>(R.id.roundProgressBar).apply {
                    setProgress(66, "66")
                    setOnClickListener { toast(getProgressStr()) }
                }
                it.findViewById<SeekBar>(R.id.seekBar)
                    .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?, progress: Int, fromUser: Boolean
                        ) = progressBar.setProgress(progress, progress.toString())

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    })

//                // 解决 ListView 拖拽滑动冲突
//                it.findViewById<ListView>(R.id.lv_test).apply {
//                    adapter = MyAdapter(
//                        this@MainActivity,
//                        arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "...")
//                    )
//
//                    // 监听 ListView 的触摸事件，手指触摸时关闭拖拽，手指离开重新开启拖拽
//                    setOnTouchListener { _, event ->
//                        logger.e("listView: ${event.action}")
//                        EasyFloat.appFloatDragEnable(event?.action == MotionEvent.ACTION_UP)
//                        false
//                    }
//                }
            }
            .registerCallback {
                drag { _, motionEvent ->
                    DragUtils.registerDragClose(motionEvent, object : OnTouchRangeListener {
                        override fun touchInRange(inRange: Boolean, view: BaseSwitchView) {
                            setVibrator(inRange)
                            view.findViewById<TextView>(com.lzf.easyfloat.R.id.tv_delete).text =
                                if (inRange) "松手删除" else "删除浮窗"

                            view.findViewById<ImageView>(com.lzf.easyfloat.R.id.iv_delete)
                                .setImageResource(
                                    if (inRange) com.lzf.easyfloat.R.drawable.icon_delete_selected
                                    else com.lzf.easyfloat.R.drawable.icon_delete_normal
                                )
                        }

                        override fun touchUpInRange() {
                            EasyFloat.dismiss()
                        }
                    }, showPattern = ShowPattern.ALL_TIME)
                }
            }
            .show()
    }

    private fun showAppFloat2(tag: String) {
        EasyFloat.with(this.applicationContext)
            .setTag(tag)
            .setShowPattern(ShowPattern.FOREGROUND)
            .setLocation(100, 100)
            .setAnimator(null)
            .setFilter(SecondActivity::class.java)
            .setLayout(R.layout.float_app_scale) {
                val content = it.findViewById<RelativeLayout>(R.id.rlContent)
                val params = content.layoutParams as FrameLayout.LayoutParams
                it.findViewById<ScaleImage>(R.id.ivScale).onScaledListener =
                    object : ScaleImage.OnScaledListener {
                        override fun onScaled(x: Float, y: Float, event: MotionEvent) {
                            params.width = max(params.width + x.toInt(), 400)
                            params.height = max(params.height + y.toInt(), 300)
                            // 更新xml根布局的大小
//                            content.layoutParams = params
                            // 更新悬浮窗的大小，可以避免在其他应用横屏时，宽度受限
                            EasyFloat.updateFloat(tag, width = params.width, height = params.height)
                        }
                    }

                it.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
                    EasyFloat.dismiss(tag)
                }
            }
            .show()
    }

    /**
     * 检测浮窗权限是否开启，若没有给与申请提示框（非必须，申请依旧是EasyFloat内部进行）
     */
    private fun checkPermission(tag: String? = null) {
        if (PermissionUtils.checkPermission(this)) {
            if (tag == null) showAppFloat() else showAppFloat2(tag)
        } else {
            AlertDialog.Builder(this)
                .setMessage("使用浮窗功能，需要您授权悬浮窗权限。")
                .setPositiveButton("去开启") { _, _ ->
                    if (tag == null) showAppFloat() else showAppFloat2(tag)
                }
                .setNegativeButton("取消") { _, _ -> }
                .show()
        }
    }

    /**
     * 主动申请浮窗权限
     */
    private fun requestPermission() {
        PermissionUtils.requestPermission(this, object : OnPermissionResult {
            override fun permissionResult(isOpen: Boolean) {
                logger.i(isOpen)
            }
        })
    }

    private fun toast(string: String = "onClick") =
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()

}
