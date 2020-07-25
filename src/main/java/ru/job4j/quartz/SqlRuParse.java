package ru.job4j.quartz;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse {
    private static final Logger LOG = LoggerFactory.getLogger(SqlRuParse.class);
    private static final String JOBOFFER = "https://www.sql.ru/forum/job-offers/";
    private static final List<String> IGNOR = List.of("485068", "1196621", "484798");

    public void pars() throws IOException {
        List<Vacancy> list = new ArrayList<>();
        int max = maxPage();
        for (int i = 1; i <= max; i++) {
            list.addAll(getVacancies(JOBOFFER.concat(String.valueOf(i))));
            LOG.info("{} anket {}", i, list.size());
        }
        //System.out.println(list);
    }

    List<Vacancy> getVacancies(String url) throws IOException {
        List<Vacancy> vacancies = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        Element forumTable = doc.getElementsByClass("forumTable").first();
        Elements rows = forumTable.getElementsByTag("tr");
        for (Element row : rows) {
            Elements columns = row.getElementsByTag("td");
            if (columns.size() > 0) {
                String href = columns.get(1).select("a").attr("href");
                String id = href.split("/")[4];
                if (IGNOR.contains(id)) {
                    continue;
                }
                String name = columns.get(1).text();
                String aftor = columns.get(2).text();
                String date = columns.get(5).text();
                vacancies.add(new Vacancy(id, href, name, aftor, date));
            }
        }
        return vacancies;
    }

    private int maxPage() throws IOException {
        Document doc = Jsoup.connect(JOBOFFER).get();
        Elements maxPages = doc.select("table[class=sort_options][style=font-weight: bold]");
        return Integer.parseInt(maxPages.select("a[href]").last().text());
    }

    private static class Vacancy {
        String id;
        String href;
        String name;
        String aftor;
        String date;

        public Vacancy(String id, String href, String name, String aftor, String date) {
            this.id = id;
            this.href = href;
            this.name = name;
            this.aftor = aftor;
            this.date = date;
        }

        @Override
        public String toString() {
            return "Vacancy{" +
                    "id='" + id + '\'' +
                    ", href='" + href + '\'' +
                    ", name='" + name + '\'' +
                    ", aftor='" + aftor + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }
}