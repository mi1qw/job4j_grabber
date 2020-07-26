package ru.job4j.grabber;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class DateGrab {
    protected DateGrab() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Convert date into timestamp.
     *
     * @param date the date
     * @return timestamp
     */
    public static Timestamp convertDate(final String date) {
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

    public static StringBuilder today(final String date) {
        LocalDateTime now = LocalDateTime.now();
        return new StringBuilder().append(now.getYear()).append("-").append(now.getMonthValue()).append("-")
                .append("сегодня".equals(date) ? now.getDayOfMonth() : now.minusDays(1).getDayOfMonth());
    }
}
