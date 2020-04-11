package com.houliu.utils;

import com.houliu.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author houliu
 * @create 2020-04-10 18:41
 *
 * 爬取京东数据
 */

@Component
public class HtmlParseUtil {
//    public static void main(String[] args) throws Exception {
//        new HtmlParseUtil().parseJD("spring").forEach(System.out::println);
//    }

    public List<Content> parseJD(String keyword) throws IOException {
        List<Content> goodsList = new ArrayList<>();
        //获取请求   https://search.jd.com/Search?keyword=java    前提：一定要联网，
        String url = "https://search.jd.com/Search?keyword=" + keyword;
        //解析网页,jsoup返回的document就是浏览器document对象
        Document document = Jsoup.parse(new URL(url), 30000);
        //所以在js里可以用的方法在这里都能用
        Element element = document.getElementById("J_goodsList");
        //获取所有的li标签
        Elements elements = element.getElementsByTag("li");
        //获取元素中的内容
        for (Element el : elements) {
            //关于这种图片特别多的网站，图片都是懒加载的
            String img = el.getElementsByTag("img").eq(0).attr("source-data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            Content content = new Content();
            content.setTitle(title);
            content.setImg(img);
            content.setPrice(price);
            goodsList.add(content);
//            goodsList.add(new Content(title,img,price));
        }
        return goodsList;
    }

}
