package ru.job4j.grabber;

//import org.jsoup.Connection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
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

import java.io.InputStream;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private static String fileDb = Objects.requireNonNull(PsqlStoreTest.class.getClassLoader().
            getResource("app.properties")).getFile();
    private static String html = "";
    private static String detailHtml = "";
    private static final String JOBOFFER = "https://www.sql.ru/forum/job-offers/";
    private SqlRuParse sqlRuParse;
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

    @BeforeClass
    public static void setUp() throws SQLException {
        InputStream docHTML = Objects.requireNonNull(
                GrabberTest.class.getClassLoader()).getResourceAsStream("Doc.html");
        InputStream detailHTML = Objects.requireNonNull(
                GrabberTest.class.getClassLoader()).getResourceAsStream("Senior.html");
        html = readResource(docHTML);
        detailHtml = readResource(detailHTML);
        conn = ConnectionRollback.create(init());
    }

    @AfterClass
    public static void afterClass() throws SQLException {
        conn.close();
    }

    //@Test
    //public void a0000000web() throws Exception {
    //    ServerSocket srvSocket = mock(ServerSocket.class);
    //    Socket socket = mock(Socket.class);
    //    OutputStream out = mock(OutputStream.class);
    //    doReturn(false).when(srvSocket).isClosed();
    //    doReturn(false).when(srvSocket).isBound();
    //
    //    //connected
    //    //socket.isConnected()
    //    final SocketAddress socketAddress = new InetSocketAddress("localhost", 9000);
    //    final int timeout = 1;
    //
    //    //Whitebox.setInternalState(
    //    //        Socket.class,
    //    //        "connected",
    //    //        true
    //    //);
    //    socket.connect(socketAddress);
    //
    //    whenNew(Socket.class).withAnyArguments().thenReturn(socket);
    //    when(srvSocket.accept()).thenReturn(socket);
    //
    //    //doReturn(socket).when(srvSocket).accept();
    //    //Thread.sleep(Duration.ofSeconds(1).toMillis());
    //
    //    doReturn(out).when(socket).getOutputStream();
    //
    //    System.out.println("test grabber"
    //            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //    System.out.println("conn " + conn.getClass());
    //
    //    PsqlStore psqlStore = mockPsqlStoreConnect(conn);
    //    System.out.println("psqlStore " + psqlStore.getClass());
    //    whenNew(PsqlStore.class).withAnyArguments().thenReturn(psqlStore);
    //
    //    Document doc = Jsoup.parseBodyFragment(html);
    //    Document detail = Jsoup.parseBodyFragment(detailHtml);
    //
    //    mockStatic(Jsoup.class);
    //
    //    org.jsoup.Connection connection = mock(org.jsoup.Connection.class);
    //    org.jsoup.Connection connection1 = mock(org.jsoup.Connection.class);
    //    when(Jsoup
    //            .connect("https://www.sql.ru/forum/job-offers/1")).thenReturn(connection);
    //    doReturn(doc).when(connection).get();
    //    when(Jsoup
    //            .connect("https://www.sql.ru/forum/1327759/senior-oracle-developer-moskva-do-235-000-na-ruki"))
    //            .thenReturn(connection1);
    //    doReturn(detail).when(connection1).get();
    //
    //    System.out.println(" main запуск");
    //    Grabber.main(new String[]{});
    //
    //    Thread.sleep(Duration.ofSeconds(5).toMillis());
    //}

    @Test
    public void a0mainWithProxy() throws Exception {

        System.out.println("test grabber"
                + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        System.out.println("conn " + conn.getClass());

        PsqlStore psqlStore = mockPsqlStoreConnect(conn);
        System.out.println("psqlStore " + psqlStore.getClass());
        whenNew(PsqlStore.class).withAnyArguments().thenReturn(psqlStore);
        doNothing().when(psqlStore).close();

        psqlStore.save(List.of(POST));

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

        System.out.println(" main запуск");
        new Thread(new ClientThread(), "alpha").start();

        Grabber.main(new String[]{});

        System.out.println("begin");
        System.out.println(psqlStore.getAll().size() + "   psqlStore.getAll().size()");
        //System.out.println(psqlStore.getAll());

        Thread.sleep(Duration.ofSeconds(2).toMillis());
    }

    @Test
    public void zWebisClosed() throws Exception {
        //Grabber grab = new Grabber();
        //Grabber grab = spy(grab1);
        //Grabber grab = mock(Grabber.class);
        //Properties prop = mock(Properties.class);
        ServerSocket serverSocket = mock(ServerSocket.class);
        //whenNew(ServerSocket.class).withArguments(9000).thenThrow(new Exception());
        whenNew(ServerSocket.class).withAnyArguments().thenReturn(serverSocket);
        //doReturn(true).when(serverSocket).isClosed();
        when(serverSocket.isClosed()).thenReturn(true);
        //doReturn("9000").when(prop).getProperty("prop");
        //doCallRealMethod().when(rgab).web(any());
        Grabber.main(new String[]{});
        //grab.cfg();
        //grab.web(mockPsqlStoreConnect(conn));
    }
    //@Test
    //public void zWebisClosed() throws Exception {
    //    Grabber grab = new Grabber();
    //    //Grabber grab = spy(grab1);
    //    //Grabber grab = mock(Grabber.class);
    //    //Properties prop = mock(Properties.class);
    //    ServerSocket serverSocket = mock(ServerSocket.class);
    //    //whenNew(ServerSocket.class).withArguments(9000).thenThrow(new Exception());
    //    //whenNew(ServerSocket.class).withArguments(9000).thenReturn(serverSocket);
    //    doReturn(true).when(serverSocket).isClosed();
    //    //doReturn("9000").when(prop).getProperty("prop");
    //    //doCallRealMethod().when(rgab).web(any());
    //
    //    grab.cfg();
    //    grab.web(mockPsqlStoreConnect(conn));
    //}

    //@Test
    //public void a00mainWithProxy() throws Exception {
    //
    //    PsqlStore psqlStore = mockPsqlStoreConnect(conn);
    //    System.out.println("psqlStore " + psqlStore.getClass());
    //    whenNew(PsqlStore.class).withAnyArguments().thenReturn(psqlStore);
    //    //whenNew(ServerSocket.class).withAnyArguments().thenThrow(new Exception());
    //    whenNew(ServerSocket.class).withAnyArguments().thenReturn(null);
    //    doNothing().when(psqlStore).close();
    //
    //    //ServerSocket socket = mock(ServerSocket.class);
    //    //doThrow(new Exception()).when(socket).getOutputStream();
    //
    //    psqlStore.save(List.of(POST));
    //
    //    Document doc = Jsoup.parseBodyFragment(html);
    //    Document detail = Jsoup.parseBodyFragment(detailHtml);
    //
    //    mockStatic(Jsoup.class);
    //
    //    org.jsoup.Connection connection = mock(org.jsoup.Connection.class);
    //    org.jsoup.Connection connection1 = mock(org.jsoup.Connection.class);
    //    when(Jsoup
    //            .connect("https://www.sql.ru/forum/job-offers/1")).thenReturn(connection);
    //    doReturn(doc).when(connection).get();
    //    when(Jsoup
    //            .connect("https://www.sql.ru/forum/1327759/senior-oracle-developer-moskva-do-235-000-na-ruki"))
    //            .thenReturn(connection1);
    //    doReturn(detail).when(connection1).get();
    //
    //    new Thread(new ClientThread(), "alpha").start();
    //
    //    Grabber.main(new String[]{});
    //
    //    //Thread.sleep(Duration.ofSeconds(2).toMillis());
    //}

    @Test
    public void a121mainSchedulerException() throws Exception {
        whenNew(ArrayList.class).withNoArguments().thenThrow(new SchedulerException());
        //Whitebox.setInternalState(
        //        Grabber.GrabJob.class,
        //        "maxPage",
        //        0
        //);
        PsqlStore psqlStore = mockPsqlStoreConnect(conn);
        //    System.out.println("psqlStore " + psqlStore.getClass());
        whenNew(PsqlStore.class).withAnyArguments().thenReturn(psqlStore);
        doThrow(new SQLException()).when(psqlStore).close();
        Grabber.main(new String[]{});
        Thread.sleep(Duration.ofSeconds(1).toMillis());
    }

    //@Test
    //public void a14mainSchedulerException() throws Exception {
    //    whenNew(ArrayList.class).withNoArguments().thenThrow(new SchedulerException());
    //    Whitebox.setInternalState(
    //            Grabber.GrabJob.class,
    //            "maxPage",
    //            10
    //    );
    //    //PsqlStore psqlStore = mockPsqlStoreConnect(conn);
    //    ////    System.out.println("psqlStore " + psqlStore.getClass());
    //    //whenNew(PsqlStore.class).withAnyArguments().thenReturn(psqlStore);
    //    //doThrow(new SQLException()).when(psqlStore).close();
    //    Grabber.main(new String[]{});
    //    Thread.sleep(Duration.ofSeconds(1).toMillis());
    //}
    //
    @Test
    public void a13mainCurrExecJobs() throws Exception {
        Whitebox.setInternalState(
                Grabber.GrabJob.class,
                "currExecJobs",
                0
        );
        Grabber.main(new String[]{});
        Thread.sleep(Duration.ofSeconds(1).toMillis());
        //Whitebox.setInternalState(
        //        Grabber.GrabJob.class,
        //        "currExecJobs",
        //        1
        //);
    }

    @Test
    public void a12mainNeedPages() throws Exception {
        Whitebox.setInternalState(
                Grabber.GrabJob.class,
                "needPages",
                0
        );
        Grabber.main(new String[]{});
        Thread.sleep(Duration.ofSeconds(1).toMillis());
        //Whitebox.setInternalState(
        //        Grabber.GrabJob.class,
        //        "needPages",
        //        3
        //);
    }
    //
    //@Test
    //public void a133mainNeedPages() throws Exception {
    //    //Whitebox.setInternalState(
    //    //        Grabber.GrabJob.class,
    //    //        "needPages",
    //    //        0
    //    //);
    //    Whitebox.setInternalState(
    //            Grabber.GrabJob.class,
    //            "maxPage",
    //            0
    //    );
    //
    //    Grabber.main(new String[]{});
    //    Thread.sleep(Duration.ofSeconds(1).toMillis());
    //}
    //
    //@Test
    //public void a134mainNeedPages() throws Exception {
    //    Whitebox.setInternalState(
    //            Grabber.GrabJob.class,
    //            "needPages",
    //            0
    //    );
    //    Whitebox.setInternalState(
    //            Grabber.GrabJob.class,
    //            "maxPage",
    //            0
    //    );
    //
    //    Grabber.main(new String[]{});
    //    Thread.sleep(Duration.ofSeconds(1).toMillis());
    //}
    //
    //
    //
    //
    //@Test
    //public void a132mainNeedPages() throws Exception {
    //    //Whitebox.setInternalState(
    //    //        Grabber.GrabJob.class,
    //    //        "needPages",
    //    //        0
    //    //);
    //    Whitebox.setInternalState(
    //            Grabber.GrabJob.class,
    //            "maxPage",
    //            0
    //    );
    //
    //    Grabber.main(new String[]{});
    //    Thread.sleep(Duration.ofSeconds(1).toMillis());
    //}
    //
    //
}