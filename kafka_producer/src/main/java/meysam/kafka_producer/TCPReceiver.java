package meysam.kafka_producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@Component
public class TCPReceiver {

    private String str;
    private Socket socket;
    private ServerSocket serverSocket;
    private Thread tcpThread;
    private int port;
    private int packet = 0;
    private final myKafkaProducer producer;

    public TCPReceiver(myKafkaProducer producer) {
        this.producer = producer;
    }



    private void runReceiver() throws IOException {
        log.info("start tcp listening on " +
                InetAddress.getLocalHost().getHostAddress() +
                " : " + port);

        tcpThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    socket = serverSocket.accept();
                    socket.setKeepAlive(true);
//                    socket.setReceiveBufferSize(999999999);
//                    socket.setSendBufferSize(999999999);
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(),
                                    "UTF-8"));
//                    socket.setSoTimeout(1000000);
//                    serverSocket.setSoTimeout(1000000);
                    while (true) {
                        str = br.readLine();
                        if (str != null) {
							if(packet%10000==0)
								log.info("Received data count: {} ",packet);
							packet++;
                            producer.sendMessage(str);
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void startTcpReceiver(int port) throws IOException {
        this.port=port;
        if(tcpThread==null)
            runReceiver();
        tcpThread.start();
    }


    public void stopTcpReceiver() throws IOException {
        tcpThread.interrupt();
        socket.close();
        serverSocket.close();
    }

}





