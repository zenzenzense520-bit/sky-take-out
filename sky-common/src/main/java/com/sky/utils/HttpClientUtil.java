package com.sky.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * HTTP 客户端工具，封装 GET 请求（微信登录等第三方接口调用使用）
 */
public class HttpClientUtil {

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 10000;

    /**
     * 发送 GET 请求
     *
     * @param url    基础地址
     * @param params 查询参数，可为空
     * @return 响应体字符串
     */
    public static String doGet(String url, Map<String, String> params) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                params.forEach(uriBuilder::setParameter);
            }
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setConfig(config);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    return EntityUtils.toString(response.getEntity(), "UTF-8");
                }
                return null;
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("HTTP 请求调用失败: " + e.getMessage(), e);
        }
    }
}
