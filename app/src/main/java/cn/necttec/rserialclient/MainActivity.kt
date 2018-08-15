package cn.necttec.rserialclient

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import cn.necttec.rserialclient.Utils.toast
import cn.necttec.rserialclient.presenter.IRSCPresenter
import cn.necttec.rserialclient.presenter.RSCPresenter

const val MSG_OPEN_SERIAL_FAIL = 0x0001
const val MSG_OPEN_SERIAL_SUCCESS = 0x0002
const val MSG_OBTAIN_MESSAGE = 0x0003

class MainActivity : AppCompatActivity(), IRSCPresenter {
    private lateinit var presenter: RSCPresenter
    private lateinit var handler: Handler
    private var rcvData:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val tvData = findViewById<TextView>(R.id.tv_rcv_data)
        with(tvData){
            movementMethod = ScrollingMovementMethod.getInstance()
        }
        val etData = findViewById<EditText>(R.id.et_data)
        val btnSend = findViewById<Button>(R.id.btn_send)
        with(btnSend) {
            setOnClickListener { it ->
                if (etData.text.isNotBlank()) {
                    presenter.writeCmd(etData.text.toString())
                }
            }
        }

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            presenter = RSCPresenter(this@MainActivity, this@MainActivity)
            presenter.checkUsbOTGSupported()
            presenter.openSerial()
            presenter.configSerial(38400,8,1,0,0)
        }

        handler = Handler(Handler.Callback {
            when (it.what) {
                MSG_OPEN_SERIAL_FAIL -> {
                    btnSend.isEnabled = false
                    etData.isEnabled = false
                    fab.isEnabled = true
                }
                MSG_OPEN_SERIAL_SUCCESS -> {
                    btnSend.isEnabled = true
                    etData.isEnabled = true
                    fab.isEnabled = false
                }
                MSG_OBTAIN_MESSAGE -> {
                    rcvData = (it.obj as String)
                    tvData.append(rcvData)
                    val offset = tvData.lineCount * tvData.lineHeight
                    if(offset > (tvData.height - tvData.lineHeight - 20)){
                        tvData.scrollTo(0,offset-tvData.height + tvData.lineHeight+20)
                    }
                }
            }
            true
        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun isUsbOTGSupported(supported: Boolean) {
        /**
        if (supported){
        this toast "本设备支持USB OTG"
        }
        else{
        this toast "本设备不支持USB OTG"
        }
         */
    }

    override fun setSerialConfigSuccess(result: Boolean) {
        if (result) {
            this toast "设置参数成功"
        } else {
            this toast "设置参数失败"
        }

    }

    override fun onDataSent(b: Boolean) {
    }

    override fun onUsbPermissionNotGranted() {
        this toast "PermissionDenied"
    }

    override fun onOpenSerial(isOpen: Boolean, message: String) {
        this toast "${isOpen}, ${message}"
    }

    override fun onDataReceive(rcvData: String) {
        val msg = Message()
        msg.what = MSG_OBTAIN_MESSAGE
        msg.obj = rcvData
        handler.sendMessage(msg)
    }
}
