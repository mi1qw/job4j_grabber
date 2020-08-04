package ru.job4j.grabber;

import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DateGrabTest {
    @Test
    public void convertDate() {
        assertThat(DateGrab.convertDate("23 июл 20, 17:28"),
                is(Timestamp.valueOf("2020-07-23 17:28:00")));
    }

    @Test
    public void convertDateYesreday() {
        LocalDateTime now = LocalDateTime.now();
        StringBuilder sb = new StringBuilder().append(now.getYear()).append("-").append(now.getMonthValue())
                .append("-").append(now.minusDays(1).getDayOfMonth()).append(" 03:03:00");
        assertThat(DateGrab.convertDate("вчера, 03:03"),
                is(Timestamp.valueOf(sb.toString())));
    }

    @Test
    public void convertDateToday() {
        LocalDateTime now = LocalDateTime.now();
        StringBuilder sb = new StringBuilder().append(now.getYear()).append("-").append(now.getMonthValue())
                .append("-").append(now.getDayOfMonth()).append(" 15:05:00");
        assertThat(DateGrab.convertDate("сегодня, 15:05"),
                is(Timestamp.valueOf(sb.toString())));
    }

    @Test(expected = IllegalStateException.class)
    public void today() {
        new DateGrab();
    }
}
