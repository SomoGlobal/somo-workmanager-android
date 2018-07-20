package com.somo.analyticsworkmanager.remote

import android.content.Context
import android.util.Log

import com.somo.analyticsworkmanager.R

import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody

class MockClient(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url()
        val response = readFromFile()

        when (url.encodedPath()) {
            "/getConfig" -> return Response.Builder()
                    .code(200)
                    .message(response)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .body(ResponseBody.create(MediaType.parse("application/json"), response.toByteArray()))
                    .addHeader("content-type", "application/json")
                    .build()
            else -> return Response.Builder()
                    .code(200)
                    .message("Server Updated").request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .body(ResponseBody.create(MediaType.parse("application/json"), response.toByteArray()))
                    .addHeader("content-type", "application/json")
                    .build()
        }
    }


    private fun readFromFile(): String {
        var ret = ""

        try {
            val inputStream = context.resources.openRawResource(R.raw.api_config_response)

            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = null;
                val stringBuilder = StringBuilder()

                while ({ receiveString = bufferedReader.readLine(); receiveString }() != null) {
                    stringBuilder.append(receiveString)
                }

                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("Mock Client", "File not found: " + e.toString())
        } catch (e: IOException) {
            Log.e("Mock CLient", "Can not read file: " + e.toString())
        }

        return ret
    }
}
