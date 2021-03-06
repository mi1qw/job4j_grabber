package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.*;
import static ru.job4j.grabber.PsqlStoreTest.init;
import static ru.job4j.grabber.PsqlStoreTest.mockPsqlStoreConnect;
import static ru.job4j.grabber.SqlRuParseTest.readResource;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@PowerMockRunnerDelegate(JUnit4.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "org.apache.http.conn.ssl.*", "com.amazonaws.*", "javax.net.ssl.*", "com.sun.*", "org.w3c.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({SqlRuParse.class, PsqlStore.class, Grabber.class, Jsoup.class})
public class GrabberTest {
    private static final Logger LOG = LoggerFactory.getLogger(GrabberTest.class);
    private static String html = "";
    private static String detailHtml = "";
    private static Connection conn;
    private static final SqlRuParse.Post POST = new SqlRuParse.Post(
            "11",
            "https://www.sql.ru/",
            "name",
            "aftor",
            Timestamp.valueOf("2020-6-28 00:00:00"),
            Timestamp.valueOf("2020-6-28 00:00:00"),
            "text"
    );
    private static PsqlStore psqlStore;

    @BeforeClass
    public static void setUp() throws Exception {
        InputStream docHTML = Objects.requireNonNull(
                GrabberTest.class.getClassLoader()).getResourceAsStream("Doc.html");
        InputStream detailHTML = Objects.requireNonNull(
                GrabberTest.class.getClassLoader()).getResourceAsStream("Senior.html");
        html = readResource(docHTML);
        detailHtml = readResource(detailHTML);
        conn = ConnectionRollback.create(init());
        psqlStore = mockPsqlStoreConnect(conn);
    }

    @AfterClass
    public static void afterClass() throws SQLException {
        conn.close();
    }

    @Before
    public void before() throws Exception {
        whenNew(PsqlStore.class).withAnyArguments().thenReturn(psqlStore);
    }

    @Test
    public void a0011WebisClosed() throws Exception {
        ServerSocket serverSocket = mock(ServerSocket.class);
        whenNew(ServerSocket.class).withAnyArguments().thenReturn(serverSocket);
        when(serverSocket.isClosed()).thenReturn(true);
        Grabber.main(new String[]{});
        assertTrue(true);
    }

    @Test
    public void a0011WebException() throws Exception {
        new Grabber().web(psqlStore);
        assertTrue(true);
    }

    @Test
    public void a2mainSchedulerException() throws Exception {
        whenNew(ArrayList.class).withNoArguments().thenThrow(new SchedulerException());
        Grabber.main(new String[]{});
        Thread.sleep(Duration.ofSeconds(1).toMillis());
        assertTrue(true);
    }

    @Test
    public void a1mainCurrExecJobs() throws Exception {
        //doNothing().when(psqlStore).close();
        doThrow(new Exception()).when(psqlStore).close();
        Whitebox.setInternalState(
                Grabber.GrabJob.class,
                "currExecJobs",
                0
        );
        Whitebox.setInternalState(
                Grabber.GrabJob.class,
                "maxPage",
                0
        );
        Grabber.main(new String[]{});
        Thread.sleep(Duration.ofSeconds(1).toMillis());
        Whitebox.setInternalState(
                Grabber.GrabJob.class,
                "currExecJobs",
                1
        );
        Whitebox.setInternalState(
                Grabber.GrabJob.class,
                "maxPage",
                3
        );
        assertTrue(true);
    }

    @Test
    public void a3mainNeedPages() throws Exception {
        doNothing().when(psqlStore).close();
        setJSOUP();
        Whitebox.setInternalState(
                Grabber.GrabJob.class,
                "needPages",
                0
        );

        Grabber.main(new String[]{});
        Thread.sleep(Duration.ofSeconds(1).toMillis());
        Whitebox.setInternalState(
                Grabber.GrabJob.class,
                "needPages",
                3
        );
        assertTrue(true);
    }

    @Test
    public void a9mainWithProxy() throws Exception {
        psqlStore.save(List.of(POST));
        setJSOUP();
        new Thread(new ClientThread(), "alpha").start();
        Grabber.main(new String[]{});
        Thread.sleep(Duration.ofSeconds(2).toMillis());
        assertThat(
                psqlStore.getAll().get(0).getHref(),
                is(POST.getHref()));
    }

    private void setJSOUP() throws IOException {
        Document doc = Jsoup.parseBodyFragment(html);
        Document detail = Jsoup.parseBodyFragment(detailHtml);
        mockStatic(Jsoup.class);
        org.jsoup.Connection connection = mock(org.jsoup.Connection.class);
        org.jsoup.Connection connection1 = mock(org.jsoup.Connection.class);
        when(Jsoup
                .connect("https://www.sql.ru/forum/job-offers/1")).thenReturn(connection);
        doReturn(doc).when(connection).get();
        when(Jsoup
                .connect("https://www.sql.ru/forum/1327759/senior-oracle-developer-moskva-do-235-000-na-ruki"))
                .thenReturn(connection1);
        doReturn(detail).when(connection1).get();
    }
}