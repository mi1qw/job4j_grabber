package ru.job4j.quartz;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse {
    private static final Logger LOG = LoggerFactory.getLogger(SqlRuParse.class);
    private static final String JOBOFFER = "https://www.sql.ru/forum/job-offers/";
    private static final List<String> IGNOR = List.of("485068", "1196621", "484798");

    /**
     * Pars.
     *
     * @throws IOException the io exception
     */
    public void pars() throws IOException {
        List<Vacancy> list = new ArrayList<>();
        int max = maxPage();
        for (int i = 1; i <= max; i++) {
            list.addAll(getVacancies(JOBOFFER.concat(String.valueOf(i))));
            LOG.info("{} Vacancies {}", i, list.size());
            System.out.println(list);
        }
    }

    /**
     * Gets vacancies.
     *
     * @param url the url
     * @return the vacancies
     * @throws IOException the io exception
     */
    List<Vacancy> getVacancies(final String url) throws IOException {
        List<Vacancy> vacancies = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        Element forumTable = doc.getElementsByClass("forumTable").first();
        Elements rows = forumTable.getElementsByTag("tr");
        for (Element row : rows) {
            Elements columns = row.getElementsByTag("td");
            if (!columns.isEmpty()) {
                String href = columns.get(1).select("a").attr("href");
                String id = href.split("/")[4];
                if (IGNOR.contains(id)) {
                    continue;
                }
                String name = columns.get(1).text();
                String aftor = columns.get(2).text();
                Timestamp date = convertDate(columns.get(5).text());
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

    private Timestamp convertDate(final String date) {
        String[] d = date.split(",");
        String time = d[1].concat(":00");
        List<String> month = List.of("янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт",
                "ноя", "дек");
        StringBuilder dat = new StringBuilder();
        if ("сегодня".equals(d[0]) || "вчера".equals(d[0])) {
            dat.append(today(d[0]));
        } else {
            String[] dd = d[0].split(" ");
            dat = new StringBuilder().append("20").append(dd[2]).append("-").append(month.indexOf(dd[1]) + 1)
                    .append("-").append(dd[0]);
        }
        String strTstmp = dat.append(time).toString();
        return Timestamp.valueOf(strTstmp);
    }

    private StringBuilder today(final String date) {
        LocalDateTime now = LocalDateTime.now();
        return new StringBuilder().append(now.getYear()).append("-").append(now.getMonthValue()).append("-")
                .append("сегодня".equals(date) ? now.getDayOfMonth() : now.minusDays(1).getDayOfMonth());
    }

    private static class Vacancy {
        private String id;
        private String href;
        private String name;
        private String aftor;
        private Timestamp date;

        Vacancy(final String id, final String href, final String name, final String aftor,
                final Timestamp date) {
            this.id = id;
            this.href = href;
            this.name = name;
            this.aftor = aftor;
            this.date = date;
        }

        @Override
        public String toString() {
            return new StringBuilder("{id=").append(id).append(", href=").append(href)
                    .append(", name=").append(name).append(", aftor=").append(aftor)
                    .append(", date=").append(date).append("}").append(System.lineSeparator()).toString();
        }
    }
}
