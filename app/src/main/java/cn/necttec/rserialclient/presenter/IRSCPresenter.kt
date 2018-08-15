package cn.necttec.rserialclient.presenter


interface IRSCPresenter{
    fun isUsbOTGSupported(supported: Boolean)
    fun setSerialConfigSuccess(result: Boolean)
    fun onDataSent(b: Boolean)
    fun onUsbPermissionNotGranted()
    fun onOpenSerial(isOpen: Boolean, message: String)
    abstract fun onDataReceive(rcvData: String)
}