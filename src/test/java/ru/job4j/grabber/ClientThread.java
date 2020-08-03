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
                    LOG.info("Client get - {}", reader.readLine());
                }
                writer.println("GET /?msg=Exit HTTP/1.1");
                writer.flush();
                LOG.info("Client sended");

                await().atLeast(50, TimeUnit.MILLISECONDS)
                        .atMost(3000, TimeUnit.MILLISECONDS)
                        .until(socket::isConnected);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
