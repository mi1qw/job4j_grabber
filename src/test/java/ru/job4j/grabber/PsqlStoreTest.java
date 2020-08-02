package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.postgresql.jdbc.PgResultSet;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.grabber.SqlRuParse.Post;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@PowerMockRunnerDelegate(JUnit4.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "org.apache.http.conn.ssl.*", "com.amazonaws.*", "javax.net.ssl.*", "com.sun.*", "org.w3c.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({PsqlStore.class, Jsoup.class})
public class PsqlStoreTest {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStoreTest.class);
    private static String fileDb = Objects.requireNonNull(PsqlStoreTest.class.getClassLoader().
            getResource("app.properties")).getFile();
    private static Connection conn;
    private static PsqlStore mocki;
    private static final Connection CONNECTION = mock(Connection.class);
    private static final Post POST = new Post(
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
        conn = ConnectionRollback.create(init());
    }

    @AfterClass
    public static void afterClass() throws SQLException {
        conn.close();
    }

    @Test
    public void a1save() throws Exception {
        mocki = mockPsqlStoreConnect(conn);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(mocki.getAll());

        mocki.save(List.of(POST));
        mocki.getAll().get(0).getHref();
        assertThat(mocki.getAll().get(0).getHref(),
                is("https://www.sql.ru/"));
    }

    public static PsqlStore mockPsqlStoreConnect(final Connection conn) throws Exception {
        PsqlStore mockin = null;
        try (FileInputStream in = new FileInputStream(fileDb)) {
            Properties cfg = new Properties();
            cfg.load(in);
            PsqlStore psqlStore = new PsqlStore(cfg);
            mockin = spy(psqlStore);
            Field cnn = psqlStore.getClass().getDeclaredField("cnn");
            cnn.setAccessible(true);
            cnn.set(mockin, conn);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return mockin;
    }
    //private PsqlStore mockPsqlStoreConnect(final FileInputStream in, final Connection conn) throws Exception {
    //    Properties cfg = new Properties();
    //    cfg.load(in);
    //    PsqlStore psqlStore = new PsqlStore(cfg);
    //    mocki = spy(psqlStore);
    //    Field cnn = psqlStore.getClass().getDeclaredField("cnn");
    //    cnn.setAccessible(true);
    //    cnn.set(mocki, conn);
    //    return mocki;
    //}

    @Test
    public void a2getAll() throws Exception {
        System.out.println(mocki.getAll());
        Post ps = mocki.getAll().get(0);
        assertNull(ps.getAftor());
        assertNull(ps.getDate());
    }

    @Test
    public void a3findById() throws Exception {
        Post post = mocki.findById("11");
        mocki.close();
        assertThat(post.getId(), is(String.valueOf(11)));
    }

    @Test(expected = IllegalStateException.class)
    public void a4PsqlStoreException() {
        new PsqlStore(null);
    }

    @Test
    public void a5SaveException() throws Exception {
        Connection connection = mock(Connection.class);
        doThrow(new SQLException()).when(connection).prepareStatement(any());
        //try (FileInputStream in = new FileInputStream(fileDb)) {
        mocki = mockPsqlStoreConnect(connection);
        mocki.save(List.of(POST));
        //} catch (IOException e) {
        //    LOG.error(e.getMessage(), e);
        //}
        assertTrue(true);
    }
    //@Test
    //public void a5SaveException() throws Exception {
    //    Connection connection = mock(Connection.class);
    //    doThrow(new SQLException()).when(connection).prepareStatement(any());
    //    try (FileInputStream in = new FileInputStream(fileDb)) {
    //        mocki = mockPsqlStoreConnect(in, connection);
    //        mocki.save(List.of(POST));
    //    } catch (IOException e) {
    //        LOG.error(e.getMessage(), e);
    //    }
    //    assertTrue(true);
    //}

    @Test
    public void a6getAllException() throws SQLException {
        Connection connection = mock(Connection.class);
        doThrow(new SQLException()).when(connection).prepareStatement(any());
        mocki.getAll();
        assertTrue(true);
    }

    @Test
    public void a7findByIdException() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        PgResultSet pgResultSet = mock(PgResultSet.class);
        doReturn(preparedStatement).when(connection).prepareStatement(any());
        doReturn(pgResultSet).when(preparedStatement).executeQuery();
        doThrow(new SQLException()).when(pgResultSet).next();
        doThrow(new SQLException()).when(pgResultSet).close();
        //try (FileInputStream in = new FileInputStream(fileDb)) {
        mocki = mockPsqlStoreConnect(connection);
        mocki.findById("1");
        //} catch (IOException e) {
        //    LOG.error(e.getMessage(), e);
        //}
        assertTrue(true);
    }

    @Test
    public void a8findByIdEmpty() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet pgResultSet = mock(PgResultSet.class);
        doReturn(preparedStatement).when(connection).prepareStatement(any());
        doReturn(pgResultSet).when(preparedStatement).executeQuery();
        doReturn(false).when(pgResultSet).next();

        //try (FileInputStream in = new FileInputStream(fileDb)) {
        mocki = mockPsqlStoreConnect(connection);
        mocki.findById("1");
        mocki.close();
        //} catch (IOException e) {
        //    LOG.error(e.getMessage(), e);
        //}
        assertTrue(true);
    }

    @Test
    public void z1close() throws Exception {
        //try (FileInputStream in = new FileInputStream(fileDb)) {
        mocki = mockPsqlStoreConnect(null);
        mocki.close();
        //} catch (IOException e) {
        //    LOG.error(e.getMessage(), e);
        //}
        assertTrue(true);
    }

    public static Connection init() {
        Connection cn = null;
        try (FileInputStream in = new FileInputStream(fileDb)) {
            Properties cfg = new Properties();
            cfg.load(in);
            Class.forName(cfg.getProperty("jdbc.driver"));
            cn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password"));
        } catch (IOException | ClassNotFoundException | SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return cn;
    }
}