package cn.necttec.rserialclient

import android.app.Application

import cn.wch.ch34xuartdriver.CH34xUARTDriver
import org.acra.ACRA
import org.acra.ReportField
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes
import org.acra.sender.HttpSender

@ReportsCrashes(
formUri = "http://39.108.51.140/crash/crash_report.php",
reportType = HttpSender.Type.JSON,
httpMethod = HttpSender.Method.POST,
//formUriBasicAuthLogin = "username",
//formUriBasicAuthPassword = "password",
formKey = "", // This is required for backward compatibility but not used
customReportContent = [
ReportField.APP_VERSION_CODE,
ReportField.APP_VERSION_NAME,
ReportField.ANDROID_VERSION,
ReportField.BRAND,
ReportField.PHONE_MODEL,
ReportField.PACKAGE_NAME,
ReportField.REPORT_ID,
ReportField.BUILD,
ReportField.STACK_TRACE
],
mode = ReportingInteractionMode.SILENT)
class ThisApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ACRA.init(this)
    }
    companion object {
        var driver: CH34xUARTDriver? = null
    }
}
