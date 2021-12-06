package at.chaoticbits.api

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.entity.BufferedHttpEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicHeader
import org.apache.http.util.EntityUtils


/**
 * Http Client Helper
 */
object Api {

    private val client: CloseableHttpClient = HttpClientBuilder.create().setSSLHostnameVerifier(NoopHostnameVerifier()).build()


    /**
     * Request a resource from the given url
     *
     * @param url [String] Endpoint
     * @return [Response] Containing the body and status code
     */
    fun fetch(url: String, headers: Array<BasicHeader> = arrayOf()): Response {

        val request =HttpGet(url)
        request.setHeaders(headers)

        try {
            client.execute(request).use { response: CloseableHttpResponse ->
                return Response(
                        response.statusLine.statusCode,
                        EntityUtils.toString(BufferedHttpEntity(response.entity), "UTF-8"))

            }
        } catch(e: Exception) {
            throw IllegalStateException("Error fetching endpoint: $url! ${e.message}")
        }
    }
}
