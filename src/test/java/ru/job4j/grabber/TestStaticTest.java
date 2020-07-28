//package ru.job4j.grabber;
//
//import org.jsoup.Jsoup;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
//import org.powermock.core.classloader.annotations.PowerMockIgnore;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.Objects;
//
//import static org.powermock.api.mockito.PowerMockito.whenNew;
//import static ru.job4j.grabber.PsqlStoreTest.init;
//import static ru.job4j.grabber.PsqlStoreTest.mockPsqlStoreConnect;
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
////@PowerMockRunnerDelegate(JUnit4.class)
//@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "org.apache.http.conn.ssl.*", "com.amazonaws.*", "javax.net.ssl.*", "com.sun.*", "org.w3c.*"})
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({SqlRuParse.class, PsqlStore.class, Jsoup.class})
//public class TestStaticTest {
//    private static Connection conn;
//    private static String fileDb = Objects.requireNonNull(PsqlStoreTest.class.getClassLoader().
//            getResource("app.properties")).getFile();
//
//    static {
//        try {
//            conn = ConnectionRollback.create(init());
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @PrepareForTest(Grabber.class)
//    @Test
//    public void testStatic() throws Exception {
//        //spy(Grabber.class);
//
//        //when(Grabber.main(new String[]{}))
//        PsqlStore psqlStore = mockPsqlStoreConnect(conn);
//        //when(Grabber.store()).thenReturn(psqlStore);
//        whenNew(PsqlStore.class).withAnyArguments().thenReturn(psqlStore);
//        Grabber.main(new String[]{});
//    }
//}
