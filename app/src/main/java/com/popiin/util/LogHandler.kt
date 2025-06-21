package com.popiin.util

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date

object LogHandler {
    private const val LOG_FILE_NAME = "popiin_log.txt" // Log file name

    // Ensure log file exists
    private fun getLogFile(context: Context): File {
     //   val logFile = File(context.getExternalFilesDir(null), LOG_FILE_NAME)
        val logFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), LOG_FILE_NAME)

        if (!logFile.exists()) {
            try {
                logFile.createNewFile() // Create file if it doesn't exist
            } catch (e: Exception) {
                Log.e("LogHandler", "Error creating log file: ${e.message}")
            }
        }
        return logFile
    }

    // Write log to file (Appends without overriding)
    fun writeLog(context: Context, message: String, level: String = "INFO") {
      /*  val logFile = getLogFile(context) // Ensure file exists

        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        val logEntry = "[$timeStamp] [$level] $message\n"

        try {
            FileWriter(logFile, true).use { writer ->
                writer.append(logEntry)
                writer.flush()
            }
        } catch (e: Exception) {
            Log.e("LogHandler", "Error writing log: ${e.message}")
        }*/
    }

    // Read log file content
    fun readLog(context: Context): String {
        val logFile = getLogFile(context) // Ensure file exists

        return try {
            logFile.readText()
        } catch (e: Exception) {
            "Error reading log: ${e.message}"
        }
    }
}
