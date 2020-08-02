//package ru.job4j.grabber;
//
//import org.junit.Test;
//
//import java.net.ServerSocket;
//
//import static org.powermock.api.mockito.PowerMockito.*;
//import static ru.job4j.grabber.PsqlStoreTest.init;
//import static ru.job4j.grabber.PsqlStoreTest.mockPsqlStoreConnect;
//
//public class AaaaTest {
//    @Test
//    public void zWebisClosed() throws Exception {
//
//        Grabber grab = new Grabber();
//        ServerSocket serverSocket = mock(ServerSocket.class);
//
//        //doReturn(true).when(serverSocket).isClosed();
//        when(serverSocket.isClosed()).thenReturn(false);
//        grab.cfg();
//        grab.web(mockPsqlStoreConnect(ConnectionRollback.create(init())));
//    }
//
//    //private Aaaa aa;
//    //
//    //@Before
//    //public void be() {
//    //    aa = new Aaaa();
//    //}
//    //
//    //@Test
//    //public void execute() {
//    //    aa.execute();
//    //}
//    //@Test
//    //public void execute2() throws NoSuchFieldException, IllegalAccessException {
//    //    setField(aa, "needPages", 0);
//    //    aa.execute();
//    //}
//    //@Test
//    //public void execute2() throws NoSuchFieldException, IllegalAccessException {
//    //    setField(aa, "maxPage", 0);
//    //    setField(aa, "needPages", 0);
//    //    aa.execute();
//    //}
//    //
//    //@Test
//    //public void execute1() throws NoSuchFieldException, IllegalAccessException {
//    //    //Field q = aa.getClass().getDeclaredField("maxPage");
//    //    //q.setAccessible(true);
//    //    //q.set(aa, 0);
//    //    setField(aa, "maxPage", 0);
//    //    aa.execute();
//    //}
//
//    //public final <T> void setField(final Object ob, final String name, final T val) throws
//    //        NoSuchFieldException, IllegalAccessException {
//    //    Field q = ob.getClass().getDeclaredField(name);
//    //    q.setAccessible(true);
//    //    q.set(aa, val);
//    //}
//}
