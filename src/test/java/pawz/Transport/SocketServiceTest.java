package pawz.Transport;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.*;

class SocketServiceTest {

    static private class ExampleServer implements Runnable {
        private final ServerSocket serverSocket;

        private ExampleServer() {
            try {
                this.serverSocket = new ServerSocket(0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean receivedMessage = false;

        public int getLocalPort(){
            return serverSocket.getLocalPort();
        }


        @Override
        public void run() {
            try {
                var s = serverSocket.accept();
                s.getInputStream().readNBytes(4);
                receivedMessage = true;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testSend() throws InterruptedException {
        ExampleServer server = new ExampleServer();
        int port = server.getLocalPort();

        Thread exampleServer = new Thread(server);
        int handle = SocketService.getInstance().registerSocket("localhost", port);

        exampleServer.start();
        SocketService.getInstance().sendMsg(handle, new byte[4]);

        Thread.sleep(50);
        exampleServer.join();

        assertTrue(server.receivedMessage);

        try(var socketInfoService = SocketInfoService.getInstance()){
            var info = socketInfoService.getInfo(handle).orElseThrow();
            assertEquals(1, info.msgCounter());
            assertEquals(4, info.bytesSend());
            assertEquals(SocketStatus.active, info.status());
            assertEquals(0, info.errorCounter());
        } catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    @Test
    public void testFailedToSend() throws Exception {
        int handle = SocketService.getInstance().registerSocket("localhost", 0);

        SocketService.getInstance().sendMsg(handle, new byte[4]);

        Thread.sleep(50);


        try(var socketInfoService = SocketInfoService.getInstance()){
            var info = socketInfoService.getInfo(handle).orElseThrow();
            assertEquals(0, info.msgCounter());
            assertEquals(0, info.bytesSend());
            assertEquals(SocketStatus.inactive, info.status());
            assertEquals(1, info.errorCounter());
        } catch (Exception e){
            throw  new RuntimeException(e);
        }
    }
}