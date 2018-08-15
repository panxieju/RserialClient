package cn.necttec.rserialclient.Utils

import android.content.Context


infix fun Context.toast(msg:String) = android.widget.Toast.makeText(this,msg, android.widget.Toast.LENGTH_LONG).show()