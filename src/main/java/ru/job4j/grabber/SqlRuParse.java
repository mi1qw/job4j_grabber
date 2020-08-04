package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {
    private static final Logger LOG = LoggerFactory.getLogger(SqlRuParse.class);
    private static final String JOBOFFER = "https://www.sql.ru/forum/job-offers/1";
    private static final List<String> IGNOR = List.of("485068", "1196621", "484798");
    private String id;
    private String href;
    private String name;
    private String aftor;
    private Timestamp date;

    /**
     * Gets vacancies.
     *
     * @param url the url
     * @return the vacancies
     */
    @Override
    public List<Post> list(final String url) {
        List<Post> vacancies = new ArrayList<>();
        Document doc = null;
        try {
            while (doc == null) {
                doc = Jsoup.connect(url).get();
            }
            Element forumTable = doc.getElementsByClass("forumTable").first();
            Elements rows = forumTable.getElementsByTag("tr");
            for (Element row : rows) {
                Elements columns = row.getElementsByTag("td");
                if (!columns.isEmpty()) {
                    href = columns.get(1).select("a").attr("href");
                    id = href.split("/")[4];
                    if (IGNOR.contains(id)) {
                        continue;
                    }
                    name = columns.get(1).text();
                    aftor = columns.get(2).text();
                    date = DateGrab.convertDate(columns.get(5).text());
                    vacancies.add(detail(href));
                }
            }
        } catch (IOException e) {
            LOG.warn("Нет инета list");
            LOG.error(e.getMessage(), e);
        }
        return vacancies;
    }

    /**
     * detail of Post.
     *
     * @param url of Post
     * @return new Post
     */
    @Override
    public Post detail(final String url) {
        Timestamp created = null;
        String text = null;
        Document doc = null;
        try {
            while (doc == null) {
                doc = Jsoup.connect(url).get();
            }
            Element msg = doc.select(".msgTable").first();
            String strDate = msg.select(".msgFooter").first().text();
            created = DateGrab.convertDate(strDate.substring(0, strDate.indexOf("[") - 1));
            text = msg.select(".msgBody").last().text();
        } catch (IOException e) {
            LOG.warn("Нет инета detail");
            LOG.error(e.getMessage(), e);
        }
        return new Post(id, href, name, aftor, date, created, text);
    }

    /**
     * max number of Page.
     *
     * @return int max number
     * @throws IOException IOException
     */
    public int maxPage() {
        int res = 0;
        Document doc = null;
        try {
            while (doc == null) {
                doc = Jsoup.connect(JOBOFFER).get();
            }
            Elements maxPages = doc.select("table[class=sort_options][style=font-weight: bold]");
            res = Integer.parseInt(maxPages.select("a[href]").last().text());
        } catch (IOException e) {
            LOG.warn("Нет инета maxPage");
            LOG.error(e.getMessage(), e);
        }
        return res;
    }

    public static class Post {
        private String id;
        private String href;
        private String name;
        private String aftor;
        private Timestamp date;
        private Timestamp created;
        private String text;

        Post(final String id, final String href, final String name, final String aftor,
             final Timestamp date, final Timestamp created, final String text) {
            this.id = id;
            this.href = href;
            this.name = name;
            this.aftor = aftor;
            this.date = date;
            this.created = created;
            this.text = text;
        }

        /**
         * Gets id.
         *
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * Gets href.
         *
         * @return the href
         */
        public String getHref() {
            return href;
        }

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets aftor.
         *
         * @return the aftor
         */
        public String getAftor() {
            return aftor;
        }

        /**
         * Gets date.
         *
         * @return the date
         */
        public Timestamp getDate() {
            return date;
        }

        /**
         * Gets created.
         *
         * @return the created
         */
        public Timestamp getCreated() {
            return created;
        }

        /**
         * Gets text.
         *
         * @return the text
         */
        public String getText() {
            return text;
        }

        /**
         * toString.
         *
         * @return toString
         */
        @Override
        public String toString() {
            return new StringBuilder("{id=").append(id).append(", href=").append(href)
                    .append(", name=").append(name).append(", aftor=").append(aftor)
                    .append(", date=").append(date).append(", created=").append(created)
                    .append(", text=").append(text).append("}").append(System.lineSeparator()).toString();
        }
    }
}
