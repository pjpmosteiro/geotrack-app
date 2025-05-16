package com.redp.geotrack

import android.content.Context
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HttpPostForm(
    requestURL: String?,
    private val charset: String,
    headers: Map<String?, String?>?,
    queryParams: MutableMap<String, Any>?,
    applicationContext: Context?
) {
    private var httpConn: HttpURLConnection
    private var queryParams: MutableMap<String, Any>? = null
    private val applicationContext: Context?

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @param charset
     * @param headers
     * @param queryParams
     * @throws IOException
     */
    init {
        if (queryParams == null) {
            this.queryParams = HashMap()
        } else {
            this.queryParams = queryParams
        }
        this.applicationContext = applicationContext
        val url = URL(requestURL)
        httpConn = url.openConnection() as HttpURLConnection
        httpConn.useCaches = false
        httpConn.doOutput = true // indicates POST method
        httpConn.doInput = true
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        if (headers != null && headers.size > 0) {
            val it = headers.keys.iterator()
            while (it.hasNext()) {
                val key = it.next()
                val value = headers[key]
                httpConn.setRequestProperty(key, value)
            }
        }
    }

    @JvmOverloads
    constructor(
        requestURL: String?,
        charset: String,
        headers: Map<String?, String?>? = null,
        applicationContext: Context? = null
    ) : this(requestURL, charset, headers, null, applicationContext)

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    fun addHeader(name: String, value: Any) {
        queryParams!![name] = value
    }

    /**
     * Adds a header to the request
     *
     * @param key
     * @param value
     */
    fun addHeader(key: String?, value: String?) {
        httpConn.setRequestProperty(key, value)
    }

    /**
     * Convert the request fields to a byte array
     *
     * @param params
     * @return
     */
    private fun getParamsByte(params: Map<String, Any>): ByteArray? {
        var result: ByteArray? = null
        val postData = StringBuilder()
        for ((key, value) in params) {
            if (postData.length != 0) {
                postData.append('&')
            }
            postData.append(this.encodeParam(key))
            postData.append('=')
            postData.append(this.encodeParam(value.toString()))
        }
        try {
            result = postData.toString().toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * URL-encoding keys and values
     *
     * @param data
     * @return
     */
    private fun encodeParam(data: String): String {
        var result = ""
        try {
            result = URLEncoder.encode(data, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return String as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun finish(): String {
        var response = ""
        val postDataBytes = this.getParamsByte(queryParams!!)
        httpConn.outputStream.write(postDataBytes)
        // Check the http status
        val status = httpConn.responseCode
        if (status == HttpURLConnection.HTTP_OK) {
            val result = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var length: Int
            while ((httpConn.inputStream.read(buffer).also { length = it }) != -1) {
                result.write(buffer, 0, length)
            }
            response = result.toString(this.charset)
            httpConn.disconnect()
            Toast.makeText(applicationContext, R.string.post_ok, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, R.string.post_fail, Toast.LENGTH_SHORT).show()
            throw IOException("Server returned non-OK status: $status")
        }
        return response
    }
}