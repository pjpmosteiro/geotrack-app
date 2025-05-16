package com.redp.geotrack

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class FIleManager {
    fun writeToFile(data: String?, context: Context) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput("coordenadas.txt", Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }

    private fun readFromFile(context: Context): String {
        var ret = ""

        try {
            val inputStream: InputStream? = context.openFileInput("coordenadas.txt")

            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()

                while ((bufferedReader.readLine().also { receiveString = it }) != null) {
                    stringBuilder.append("\n").append(receiveString)
                }

                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: $e")
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        }

        return ret
    }
}
