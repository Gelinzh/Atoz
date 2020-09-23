package com.acelych.atoz.crawler;

import com.acelych.atoz.data.Word;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerHandler
{
    private static PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
    private static NameValuePair[] headersList = new NameValuePair[]{
        new BasicNameValuePair("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"),
        new BasicNameValuePair("Accept-Encoding", "gzip, deflate"),
        new BasicNameValuePair("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7"),
        new BasicNameValuePair("Cache-Control", "max-age=0"),
        new BasicNameValuePair("Connection", "keep-alive"),
//        new BasicNameValuePair("Cookie", "BAIDU_SSP_lcr=https://www.baidu.com/link?url=xB7Ux3sxjiK4oyFcLsBqCFoGfB2QpQFix4B3dUDZvum&wd=&eqid=f7086dd800020e75000000025f683457"),
        new BasicNameValuePair("Host", "www.iciba.com"),
        new BasicNameValuePair("If-None-Match", "W/\"1fbbf-ZC3WMXOSMSODkXswMzEZQjZJHm0\""),
        new BasicNameValuePair("Upgrade-Insecure-Requests", "1"),
        new BasicNameValuePair("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36")
    };

    public static Word generateWord(String name)
    {
        List<String> detailedTrans = getDetailedTranslation(name);
        if (detailedTrans == null)
            return null;

        boolean isCET4 = detailedTrans.get(0).matches("^.*CET4.*$");
        boolean isCET6 = detailedTrans.get(0).matches("^.*CET6.*$");

        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5<>\\[\\]…]+"); //[\u4e00-\u9fa5] //[^\x00-\xff，] //[\u4e00-\u9fa5<>\[\]…]+ //(?<!(?:&lt;)|(?:\[))[^\x00-\xff，]+
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 3; i < detailedTrans.size() && i < 5; i++)
        {
            String temp = detailedTrans.get(i).replaceAll("<.*>", "");
            temp = temp.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
            Matcher matcher = pattern.matcher(temp);
            if (matcher.find())
                stringBuilder.append(matcher.group(0));
            if (i + 1 < detailedTrans.size() && i + 1 != 5)
                stringBuilder.append(";");
        }

        return new Word(name, stringBuilder.toString(), 0, isCET4, isCET6);
    }

    public static List<String> getDetailedTranslation(String name)
    {
        String content = getHtmlContent(name);

        Pattern pattern = Pattern.compile("<h1 class=\"Mean_word__3SsvB\">.*</h1>" + //*标记匹配起点*//
                "(<p class=\"Mean_tag__2vGcf\">(.*?)</p>)?" + //---考试范围（可选）---//
                "<ul class=\"Mean_symbols__5dQX7\">.*<li>英<!-- -->(\\[.*?]).*<li>美<!-- -->(\\[.*?]).*</li></ul>.*" + //---音标---//
                "<ul class=\"Mean_part__1RA2V\">(.*?)</ul>"); //---详细翻译---//
        Matcher matcher = pattern.matcher(content);

        if (matcher.find())
            return contentSorting(matcher);
        else
            return null;
    }

    private static String getHtmlContent(String name)
    {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(manager).build();
        CloseableHttpResponse response = null;

        URIBuilder uriBuilder;
        HttpGet httpGet = null;

        //init target website‘s uri
        try
        {
            uriBuilder = new URIBuilder("https://iciba.com/word");
            uriBuilder.setParameter("w", name);
            httpGet = new HttpGet(uriBuilder.build());
        } catch (URISyntaxException e)
        {
            e.printStackTrace();
        } assert httpGet != null;

        //init headers
        for (NameValuePair temp : headersList)
            httpGet.setHeader(temp.getName(), temp.getValue());

        //set timeout count
        RequestConfig config = RequestConfig.custom().setConnectTimeout(1000).setConnectionRequestTimeout(500).setSocketTimeout(10*1000).build();
        httpGet.setConfig(config);

        //execute process
        try
        {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity, "utf8");
            }
            else
                return "null";
        } catch (IOException e)
        {
            e.printStackTrace();
            return "null";
        } finally
        {
            try
            {
                Objects.requireNonNull(response).close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static List<String> contentSorting(Matcher matcher)
    {
        List<String> result = new ArrayList<>();

        if (matcher.group(2) != null)
            result.add(matcher.group(2));
        else
            result.add("null");

        if (matcher.group(3) != null)
            result.add(matcher.group(3).replaceAll("&#x27;", "'"));
        else
            result.add("null");

        if (matcher.group(4) != null)
            result.add(matcher.group(4).replaceAll("&#x27;", "'"));
        else
            result.add("null");

        if (matcher.group(5) != null)
        {
            List<String> transList = new ArrayList<>();
            //extract string
            String temp = matcher.group(5);

            //Matcher
            Pattern pattern = Pattern.compile("<li>(.*?)</li>");
            Matcher psMatcher = pattern.matcher(temp);
            while (psMatcher.find())
            {
                transList.add(psMatcher.group(0));
                temp = temp.replace(psMatcher.group(0), "");
                psMatcher = pattern.matcher(temp);
            }

            //Format List
            for (int i = 0; i < transList.size(); i++)
            {
                transList.set(i, transList.get(i).replaceAll("<.*?>", ""));
//                transList.set(i, transList.get(i).replaceAll("&lt;", "<"));
//                transList.set(i, transList.get(i).replaceAll("&gt;", ">"));
                transList.set(i, "<b>" + transList.get(i));
                transList.set(i, transList.get(i).replaceFirst("\\.", ".</b> "));
            }

            result.addAll(transList);
        }

        boolean isAllNull = true;
        for (String string : result)
        {
            if (!string.equals("null"))
                isAllNull = false;
        }
        if (isAllNull)
            return null;

        return result;
    }
}
