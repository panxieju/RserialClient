package cn.necttec.rserialclient.presenter

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Message
import cn.necttec.rserialclient.ThisApp
import cn.wch.ch34xuartdriver.CH34xUARTDriver
import java.nio.charset.Charset

const val ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION"

/**
 * 使用顺序：
 * 1） checkUsbOTGSupported
 * 2)  openSerial
 * 3)  configSerial
 * 4)  writeCommand
 * @property context Context
 * @property listener IRSCPresenter
 * @property usbManager UsbManager
 * @property wirteBuffer ByteArray
 * @property readBuffer ByteArray
 * @property isOpen Boolean
 * @property totalRcv Int
 * @constructor
 */

const val BUFFSIZE = 32768

class RSCPresenter(val context: Context, val listener:IRSCPresenter){
    private lateinit var usbManager: UsbManager
    private val wirteBuffer = ByteArray(4096)
    private val readBuffer = ByteArray(BUFFSIZE)
    private var isOpen = false
    private var totalRcv = 0
    private lateinit var driver:CH34xUARTDriver
    init {
        //初始化USBManager
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        ThisApp.driver = CH34xUARTDriver(usbManager,context,ACTION_USB_PERMISSION)
        driver = ThisApp.driver!!
    }

    //@todo 检查是否支持USB OTG
    fun checkUsbOTGSupported() {
        val isSupported = ThisApp.driver!!.UsbFeatureSupported()
        listener.isUsbOTGSupported(isSupported)
    }

    //@todo 打开串口
    fun openSerial(){
        if (!isOpen){
            val retval = driver.ResumeUsbList()
            if(retval == -1){
                listener.onOpenSerial(false, "打开设备失败")
                driver.CloseDevice()
            }
            else if(retval == 0){
                if(!driver.UartInit()){ //初始化串口
                    listener.onOpenSerial(false,"设备初始化失败")
                    return
                }
                listener.onOpenSerial(true,"打开设备成功")
                isOpen = true
                ReadThread().start()    //开启读线程读取串口接收的数据
            }
            else{
                listener.onOpenSerial(false,"未获取使用USB权限")
            }
        }
        else{
            Thread.sleep(2000)
            listener.onOpenSerial(false,"正在关闭设备")
        }
    }



    //@tODO 设置串口参数
    //@param baudrate 波特率
    //@prarm databit 数据位
    //@param stopbit 停止位
    //@param parity 奇偶校验位
    //@param flowcontrl 流控
    fun configSerial(badurate:Int=38400,
                     databit:Int,
                     stopbit:Int,
                     parity:Int,
                     flowControl:Int
    ){
        val result = driver.SetConfig(
                badurate,
                databit.toByte(),
                stopbit.toByte(),
                parity.toByte(),
                flowControl.toByte()
        )
        driver.SetTimeOut(1000,1000)
        listener.setSerialConfigSuccess(result)
    }

    //@tODO 向串口写入数据
    //@param data要写入的数据
    fun writeCmd(data:String){
        val toSend = data.toByteArray()
        val success = driver.WriteData(toSend,toSend.size)
        if(success <0){
            listener.onDataSent(false)
        }
        else{
            listener.onDataSent(true)
        }
    }

    fun requestPermission(){
        val retval = driver.ResumeUsbPermission()
        if(retval == -2){
            listener.onUsbPermissionNotGranted()
        }
    }

    inner class ReadThread():Thread() {
        override fun run() {
            val buffer = ByteArray(BUFFSIZE)
            while(true){
                val msg = Message.obtain()
                if(!isOpen){
                    break
                }
                val length = driver!!.ReadData(buffer,BUFFSIZE)
                if(length>0){
                    totalRcv += length
                    val rcvData = buffer.toString(Charset.defaultCharset())
                    listener.onDataReceive(rcvData)
                }
            }
        }

    }
}


