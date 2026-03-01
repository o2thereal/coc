package com.example.cochelper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val machine = AssistantStateMachine()

    private lateinit var stateText: TextView
    private lateinit var logText: TextView
    private lateinit var coordinateListText: TextView

    private lateinit var etButtonName: EditText
    private lateinit var etButtonX: EditText
    private lateinit var etButtonY: EditText

    private lateinit var coordinateItems: MutableList<GameButtonCoordinate>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stateText = findViewById(R.id.tvCurrentState)
        logText = findViewById(R.id.tvLog)
        coordinateListText = findViewById(R.id.tvCoordinateList)

        etButtonName = findViewById(R.id.etButtonName)
        etButtonX = findViewById(R.id.etButtonX)
        etButtonY = findViewById(R.id.etButtonY)

        coordinateItems = loadCoordinates()

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

        findViewById<Button>(R.id.btnAddCoordinate).setOnClickListener {
            addCoordinateFromInput()
        }

        findViewById<Button>(R.id.btnExportCoordinates).setOnClickListener {
            exportCoordinates()
        }

        findViewById<Button>(R.id.btnClearCoordinates).setOnClickListener {
            coordinateItems.clear()
            saveCoordinates()
            refreshCoordinateList()
            toast("已清空坐标记录")
        }

        refreshState()
        refreshCoordinateList()
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

    private fun addCoordinateFromInput() {
        val name = etButtonName.text.toString().trim()
        val x = etButtonX.text.toString().trim().toIntOrNull()
        val y = etButtonY.text.toString().trim().toIntOrNull()

        if (name.isBlank() || x == null || y == null) {
            toast("请输入按钮名和合法的 X/Y 坐标")
            return
        }

        coordinateItems.removeAll { it.name == name }
        coordinateItems.add(GameButtonCoordinate(name, x, y))
        saveCoordinates()
        refreshCoordinateList()

        etButtonName.text?.clear()
        etButtonX.text?.clear()
        etButtonY.text?.clear()

        toast("已保存坐标：$name ($x,$y)")
    }

    private fun refreshCoordinateList() {
        if (coordinateItems.isEmpty()) {
            coordinateListText.text = "暂无坐标记录"
            return
        }
        coordinateListText.text = coordinateItems.joinToString(separator = "\n") {
            "${it.name}: (${it.x}, ${it.y})"
        }
    }

    private fun exportCoordinates() {
        val raw = CoordinateCodec.encode(coordinateItems)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("coc_coordinates", raw))
        toast("坐标已复制到剪贴板")
    }

    private fun saveCoordinates() {
        val sp = getSharedPreferences("coc_helper", MODE_PRIVATE)
        sp.edit().putString("button_coordinates", CoordinateCodec.encode(coordinateItems)).apply()
    }

    private fun loadCoordinates(): MutableList<GameButtonCoordinate> {
        val sp = getSharedPreferences("coc_helper", MODE_PRIVATE)
        val raw = sp.getString("button_coordinates", "") ?: ""
        return CoordinateCodec.decode(raw)
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
