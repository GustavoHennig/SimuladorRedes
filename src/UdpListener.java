
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Title: UdpListener</p>
 *
 * <p>Description: Fica esperando pacotes udp de outros clientes</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author Gustavo Augusto Hennig
 * @version 1.0
 */
public class UdpListener extends Thread {

    public static final int BufferSize = 128;
    private MulticastSocket socket = null;
public boolean isRunning = false;
    public ArrayList<String> inputBuffer = new ArrayList<String>();

    public UdpListener() {
        boolean sucess = false;
        do {
            try {

                socket = new MulticastSocket(Parameters.getNextPort());
                InetAddress address = InetAddress.getByName("224.0.0.1");
                socket.joinGroup(address);
                sucess = true;

            } catch (SocketException ex) {
                sucess = false;
            } catch (IOException ex) {
                sucess = false;
            }

            try {
            //Para não travar tudo, em caso de outro erro diferente de portas.
                Thread.sleep(300);
            } catch (InterruptedException ex1) {
                //Logger.getLogger(UdpListener.class.getName()).log(Level.SEVERE, null, ex1);
            }

        } while (!sucess);
    }

    @Override
    public void run() {

        while (isRunning) {

            try {
                byte[] buf = new byte[BufferSize];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                if (packet.getLength() > 0) {

                    //Cada pacote recebido é adicionado ao buffer do rotadore para ele interpretá-lo
                    String data =  new String(packet.getData());
                    data =  data.replaceAll("\0", "");
                    inputBuffer.add(data);


                    //Exemplo para retorno:
//                        String dString = "Oi, sou um servidor UDP";
                    // buf = dString.getBytes();
                    // send the response to the client at "address" and "port"

                    /*
                    InetAddress address = packet.getAddress();

                    System.out.println("Legal, encontrei um man�! " + ret +
                    packet.getAddress().getHostAddress());
                     */
                    // N�o ha necessidade de responder, apenas quero saber quem pode estar online
                        /*
                    int port = packet.getPort();
                    packet = new DatagramPacket(buf, buf.length, address,
                    port);
                    String teste2 = new String(packet.getData());
                    socket.send(packet);
                     */
                    }
                

                sleep(100);

            } catch (Exception e) {
                e.printStackTrace();
                isRunning = false;
            }

        }
        if (socket.isConnected()) {
            try {
                socket.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

    }
}
