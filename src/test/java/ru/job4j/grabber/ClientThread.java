package ru.job4j.grabber;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class ClientThread implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ClientThread.class);

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @SuppressFBWarnings({"UNENCRYPTED_SOCKET"})
    @Override
    public void run() {
        LOG.info(Thread.currentThread().getName());
        //System.out.println(Thread.currentThread().getName());
        int n = 200;
        while (n-- != 0) {
            try (Socket socket = new Socket(InetAddress.getLoopbackAddress(), 9000);
                 InputStreamReader inputStreamReader = new InputStreamReader(
                         socket.getInputStream(), StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(inputStreamReader);
                 PrintWriter writer = new PrintWriter(
                         socket.getOutputStream(),
                         true, StandardCharsets.UTF_8)) {
                while (reader.ready()) {
                    System.out.println("Client get - " + reader.readLine());
                }
                writer.println("GET /?msg=Exit HTTP/1.1");
                writer.flush();
                System.out.println("Client sended");
                await().atLeast(50, TimeUnit.MILLISECONDS)
                        .atMost(3000, TimeUnit.MILLISECONDS)
                        .until(socket::isConnected);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}

//public class EchoServerTest extends Mockito {
//    private static final Logger LOG = LoggerFactory.getLogger(EchoServerTest.class);
//    private static final String LN = System.lineSeparator();
//    private static Connection conn;
//    private static final SqlRuParse.Post POST = new SqlRuParse.Post(
//            "11",
//            "https://www.sql.ru/",
//            "name",
//            "aftor",
//            Timestamp.valueOf("2020-6-28 00:00:00"),
//            Timestamp.valueOf("2020-6-28 00:00:00"),
//            "text"
//    );

//@Test
//public void my() throws Exception {
//    conn = ConnectionRollback.create(init());
//    //conn = init();
//    //PsqlStore psqlStore = mockPsqlStoreConnect(conn);
//    PsqlStore psqlStore = mockPsqlStoreConnect(conn);
//    psqlStore.save(List.of(POST));
//    System.out.println(psqlStore.getAll());
//    System.out.println(conn + "   my() throws Exception");
//    System.out.println(psqlStore + "   my() throws Exception");
//    whenNew(PsqlStore.class).withAnyArguments().thenReturn(psqlStore);
//    doNothing().when(psqlStore).;
//
//    doNothing().when(psqlStore).close();
//
//
//    ClientThread clientThread = new ClientThread();
//    Thread alpha = new Thread(clientThread, "alpha");
//    alpha.start();
//
//    Grabber.main(new String[]{});
//    Thread.sleep(7000);
//    assertTrue(true);
//}
//}