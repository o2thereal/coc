package com.example.cochelper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val machine = AssistantStateMachine()

    private lateinit var stateText: TextView
    private lateinit var logText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stateText = findViewById(R.id.tvCurrentState)
        logText = findViewById(R.id.tvLog)

        findViewById<Button>(R.id.btnStartFlow).setOnClickListener {
            machine.start()
            refreshState()
            appendLog("进入主城，检查训练与资源")
        }

        findViewById<Button>(R.id.btnNextStep).setOnClickListener {
            val nextState = machine.next()
            refreshState()
            appendLog(sceneAction(nextState))
        }

        findViewById<Button>(R.id.btnRunAll).setOnClickListener {
            var safety = 0
            while (machine.state != Scene.DONE && safety < 50) {
                val nextState = machine.next()
                appendLog(sceneAction(nextState))
                safety++
            }
            refreshState()
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener {
            machine.reset()
            refreshState()
            logText.text = ""
            appendLog("状态已重置")
        }

        findViewById<Button>(R.id.btnStartOverlay).setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                startService(Intent(this, OverlayService::class.java))
                appendLog("悬浮窗服务已启动")
            } else {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                appendLog("请先授予悬浮窗权限")
            }
        }

        findViewById<Button>(R.id.btnStopOverlay).setOnClickListener {
            stopService(Intent(this, OverlayService::class.java))
            appendLog("悬浮窗服务已关闭")
        }

        refreshState()
        appendLog("初始化完成：可开始一轮打鱼 + 升级流程")
    }

    private fun refreshState() {
        stateText.text = getString(R.string.current_state, machine.state.title)
    }

    private fun appendLog(message: String) {
        val old = logText.text.toString()
        val updated = if (old.isBlank()) "- $message" else "$old\n- $message"
        logText.text = updated
    }

    private fun sceneAction(scene: Scene): String = when (scene) {
        Scene.IDLE -> "处于待机状态"
        Scene.HOME -> "检查兵营与法术，准备打鱼"
        Scene.FINDING_FARM_TARGET -> "识别搜索界面，筛选可打资源村"
        Scene.ATTACKING -> "执行下兵顺序并监测战斗结算"
        Scene.RETURN_HOME -> "战斗结束回城，统计资源"
        Scene.UPGRADE_BUILDING -> "优先升级采集器/仓库等建筑"
        Scene.UPGRADE_TROOPS -> "进入实验室，执行兵种升级"
        Scene.DONE -> "打鱼 + 升级流程已完成"
    }
}
