package com.redp.geotrack

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class GetToken {
    @get:Throws(IOException::class)
    val token: String
        get() {
            val url = "https://git.redp.icu/pjpmosteiro/ext/-/raw/main/tokenapp"
            val con = URL(url).openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            //response status
            val status = con.responseCode
            println(status)
            //response body
            if (status == 200) {
                var line: String?

                val `in` =
                    BufferedReader(InputStreamReader(con.inputStream))

                val sb = StringBuilder()

                while ((`in`.readLine().also { line = it }) != null) {
                    sb.append(line)
                }
                return sb.toString()
            } else {
                println("error GET info")
            }

            return url
        }
}