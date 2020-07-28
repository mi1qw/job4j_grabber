package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.grabber.SqlRuParse.Post;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@PowerMockRunnerDelegate(JUnit4.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "org.apache.http.conn.ssl.*", "com.amazonaws.*", "javax.net.ssl.*", "com.sun.*", "org.w3c.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({SqlRuParse.class, Jsoup.class})
public class SqlRuParseTest {
    private static final Logger LOG = LoggerFactory.getLogger(SqlRuParseTest.class);
    public static final String DOC = "Doc.html";
    private static String html = "";
    private static String detailHtml = "";
    private static final String JOBOFFER = "https://www.sql.ru/forum/job-offers/1";
    private static final String JOBODETAIL = "https://www.sql.ru/forum/1327759/senior-oracle-developer-moskva-do-235-000-na-ruki";
    private SqlRuParse sqlRuParse;

    @BeforeClass
    public static void beforeClass() {
        InputStream docHTML = Objects.requireNonNull(
                SqlRuParseTest.class.getClassLoader()).getResourceAsStream("Doc.html");
        InputStream detailHTML = Objects.requireNonNull(
                SqlRuParseTest.class.getClassLoader()).getResourceAsStream("Senior.html");
        html = readResource(docHTML);
        detailHtml = readResource(detailHTML);
    }

    public static String readResource(final InputStream file) {
        StringBuilder sb = new StringBuilder();
        String str = "";
        assert file != null;
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file, "UTF-8"))) {
            bf.lines().forEach(sb::append);
            str = sb.toString();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return str;
    }

    @Before
    public void setUp() {
        sqlRuParse = new SqlRuParse();
    }

    @Test
    public void list() throws IOException {
        Document doc = Jsoup.parseBodyFragment(html);
        Document detail = Jsoup.parseBodyFragment(detailHtml);
        mockStatic(Jsoup.class);
        Connection connection = mock(Connection.class);
        Connection connection1 = mock(Connection.class);
        when(Jsoup
                .connect(JOBOFFER)).thenReturn(connection);
        doReturn(doc).when(connection).get();
        when(Jsoup
                .connect(JOBODETAIL))
                .thenReturn(connection1);
        doReturn(detail).when(connection1).get();
        List<Post> posts = sqlRuParse.list(JOBOFFER);
        assertThat(posts.get(0).getId(), is(String.valueOf(1327759)));
    }

    @Test
    public void maxPage() throws IOException {
        Document doc = Jsoup.parseBodyFragment(html);
        mockStatic(Jsoup.class);
        Connection connection = mock(Connection.class);
        when(Jsoup
                .connect(JOBOFFER)).thenReturn(connection);
        doReturn(doc).when(connection).get();

        assertThat(sqlRuParse.maxPage(), is(1));
    }

    @Test
    public void listException() throws IOException {
        mockStatic(Jsoup.class);
        Connection connection = mock(Connection.class);
        when(Jsoup.connect(any())).thenReturn(connection);
        doThrow(new IOException()).when(connection).get();
        assertThat(sqlRuParse.list(JOBOFFER).size(), is(0));
    }

    @Test
    public void detailException() throws IOException {
        mockStatic(Jsoup.class);
        Connection connection = mock(Connection.class);
        when(Jsoup.connect(any())).thenReturn(connection);
        doThrow(new IOException()).when(connection).get();
        sqlRuParse.detail(JOBOFFER);
        assertTrue(true);
    }

    @Test
    public void maxPageException() throws IOException {
        mockStatic(Jsoup.class);
        Connection connection = mock(Connection.class);
        when(Jsoup.connect(any())).thenReturn(connection);
        doThrow(new IOException()).when(connection).get();
        assertThat(sqlRuParse.maxPage(), is(0));
    }
}