package cn.necttec.rserialclient.Utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.nio.charset.Charset


infix fun Context.toast(msg: String) = android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_LONG).show()

fun Context.lf(data: String, fileName: String = "log.dat") {
    val filePath = Environment.getExternalStorageDirectory().absolutePath + "/RserialLog"
    val fileName = fileName

    val directory = File(filePath)
    if (!directory.exists()){
        directory.mkdirs()
    }
    val file = File(filePath,fileName)
    if(file.exists()){
        file.createNewFile()
    }

    val fos = FileOutputStream(file, true)
    val osw = OutputStreamWriter(fos, Charsets.UTF_8)
    osw.write(data)
    fos.close()
    osw.close()
}