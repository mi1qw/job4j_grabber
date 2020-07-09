package ru.job4j.quartz;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/14").get();

        Elements aaaa = doc.select("table[class=sort_options][style=font-weight: bold]");
        Elements aaaa44 = aaaa.get(0).getElementsByAttribute("href");

        //Elements pageq = doc.select("class:contains(sort_options)");
        //Elements pageq = doc.select("table.sort_options");
        //Elements pageq = doc.select("table[style]");
        Elements pageq = doc.select("table[class=sort_options][style=font-weight: bold]");
        Element links = pageq.select("a[href]").last();
        System.out.println(links.text());
        //for (Element link : links) {
        //    String url = link.attr("href");
        //    String text = link.text();
        //    System.out.println(text + ", " + url);
        //}

        Element rowq = doc.getElementsByClass("forumTable").first();
        /* разбить на блоки*/
        Elements sss = rowq.getElementsByTag("tr");
        for (Element aa : sss) {
            Elements tds = aa.getElementsByTag("td");
            if (tds.size() > 0) {
                System.out.println(tds.get(0).text());
                System.out.println(tds.get(1).text());
                System.out.println(tds.get(2).text());
                System.out.println(tds.get(3).text());
                System.out.println(tds.get(4).text());
                System.out.println(tds.get(5).text());
            }

            //for (Element td : tds) {
            //    //System.out.println("0 - " + td.text());
            //
            //    //System.out.println("1 - " + td.childNode(1));
            //    //System.out.println("2 - " + td.childNode(2));
            //    //System.out.println("3 - " + td.childNode(3));
            //    //System.out.println("4 - " + td.childNode(4));
            //    //System.out.println("5 - " + td.childNode(5));
            //}

        }

        Elements aaarowa = rowq.select(".altCol");
        Elements rowaa = rowq.select(".postslisttopic");

        //Elements row = doc.select(".postslisttopic");
        //for (Element td : row) {
        //    Element href = td.child(0);
        //    System.out.println(href.attr("href"));
        //    System.out.println(href.text());
        //}
        //<td style="text-align:center" class="altCol">25 июн 20, 09:57</td>

    }
}