
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gustavo
 */
public class Parameters {

    public static int UdpPortIni = 40000;
    public static int UdpPortEnd = 40032;
    private static int UdpLastUsedPort = 0;
    public static int MilisegundosEnviarPacoteRoteamento = 10000;
    public static String EnderecoRoteador;
    public static String [] RedesParaSorteio = {"10.0.0","20.0.0","30.0.0","40.0.0","50.0.0","60.0.0"};

    public static IP IpBaseRede01 =  new IP();
    public static IP IpBaseRede02 =  new IP();
    public static IP IpBaseRede03 =  new IP();
    public static ArrayList<String> RoteadoresConectados = new ArrayList<String>();

    public static int getNextPort() {
        if (UdpLastUsedPort == 0 || UdpLastUsedPort >= UdpPortEnd) {
            UdpLastUsedPort = UdpPortIni;
        }

        UdpLastUsedPort++;
        return UdpLastUsedPort;
    }

    //public int
}
