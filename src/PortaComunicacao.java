
/**
 *   Este arquivo é parte do programa SimuladorRedes
 *
 *   SimuladorRedes é um software livre; você pode redistribui-lo e/ou
 *   modificá-lo dentro dos termos da Licença Pública Geral GNU como
 *   publicada pela Fundação do Software Livre (FSF); na versão 3 da
 *   Licença.
 *
 *   SimuladorRedes é distribuído na esperança que possa ser útil,
 *   mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a qualquer
 *   MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a
 *   Licença Pública Geral GNU para maiores detalhes.
 *
 *   Você deve ter recebido uma cópia da Licença Pública Geral GNU
 *   junto com este programa, se não, veja em <http://www.gnu.org/licenses/>.
 *
 *
 *   Gustavo Augusto Hennig
 *   Setembro e Outubro de 2008
 *
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Gustavo Augusto Hennig
 * 09-10/2008
 */
public class PortaComunicacao extends Thread {

    public IP SubRede;
    public int PortNumber;
//    public String bufferEntrada;
//    public String bufferSaida;
    public ArrayList<Pacote> bufferPacotes = new ArrayList<Pacote>();
    public ArrayList<String> bufferEntrada = new ArrayList();
    public ArrayList<String> Saida = new ArrayList();
    public ICallBackable UIOutput;
    public int RoteadorAoQualEstaLigado;
    /**
     * Para enviar os pacotes
     */
    private UdpClient udpClient = new UdpClient();
    /**
     * Para receber os pacotes
     */
    private UdpListener udpListener = new UdpListener();

    public PortaComunicacao(int NumeroPorta) {
        PortNumber = NumeroPorta;
    }
    private Random random = new Random();
    private boolean FinalizaPorta = false;

    public void FinalizaPorta() {
        FinalizaPorta = true;
    }

    private int getRandomByte() {
        return random.nextInt(255);
    }
    public boolean LigadoARoteador(){
        return this.SubRede.getIpBase().equals("0.0.0");
    }
    @Override
    public void run() {

        while (true && !FinalizaPorta) {
            try {

                //Gera um pacote aleatório


                String s = getRandomPackage();

                //Adiciona no buffer do roteador(Agora somente dados recebido vão para o roteador
                //bufferPacotes.add(s );

                // Envia esse pacote pela rede
                EnviaPacoteParaRede(s);

                //Exibe o que foi gerado
                Out(s);


                while (BufferHasData()) {
                    String bfd = getBufferData();
                    Pacote p = new Pacote();
                    if (p.ParseRecievedData(bfd)) {


                        if (!p.ParaEsteRoteadorEPorta(this.PortNumber)) {
                            //Out("Pacote descartado, era para outro host.");
                            continue;
                        }

                        if(p.PacoteDeRoteamento){
                            p.PortaComunicacao = this.PortNumber;
                        }
                        bufferPacotes.add(p);
                    } else {
                        //Pacote descartado, n problemas
                        Out("Pacote mal formatado: " + p.ErrorMessage);
                    //System.out.println(p.ErrorMessage);

                    }

                }
                //Esperará entre 0 e 2 segundos.
                sleep(random.nextInt(2000));
            } catch (Exception e) {
                Out(e.getMessage());
            }
        }
    }

    /**
     * Gera um pacote aleatório
     *
     */
    private String getRandomPackage() {

        Pacote p = new Pacote();

        p.data = GeraPackageData(5);
        p.PortaComunicacao = this.PortNumber;
        p.HeaderIpDestino = IP.getRandom();
        p.HeaderIpOrigem = this.SubRede;
        p.DataPacketSize = 45;
        p.TRPacketSize = 20;
        p.HeaderTTL = random.nextInt(15);
        p.EnderecoEPortaRoteador = Parameters.EnderecoRoteador + this.PortNumber;


        if (random.nextInt(5) == 41) {
            //Nunca vai entrar, desativado

            //16,66% de chances de entrar aqui

            p.PacoteDeRoteamento = true;

            //Esse loop serve para não gerar métricas com valor 0
            do {
                p.Metrica = random.nextInt(15);
            } while (p.Metrica == 0);

            if (random.nextInt(4) == 1) {
                //20% de chances de gerar qualquer valor para a tabela de roteamento

                p.HeaderIpOrigem = IP.getRandom();

            } else if (random.nextInt(3) == 1) {
                //20% de chances

                //Tenta fazer um outro sorteio com números menos aloprados,
                // com a intenção de fazer com que duas portas apontem para a mesma rede
                p.HeaderIpOrigem = getRandom_IPDestinoValido();
            }

            return p.getSerializatedTRPackage();
        } else {
            //84,33% de chances de entrar aqui

            p.PacoteDeRoteamento = false;
            p.HeaderIpOrigem.addr3 = getRandomByte();

            if (random.nextInt(2) == 1) {
                //33% de chances de criar um destino inválido

                p.HeaderIpDestino = getRandom_IPDestinoValido();
            }


            return p.getSerializatedDataPackage();

        }

    }

    private IP getRandom_IPDestinoValido() {
        IP ret = new IP();

        String rnd = Parameters.RedesParaSorteio[random.nextInt(Parameters.RedesParaSorteio.length)];
        ret.setIp(rnd);
        ret.addr3 = getRandomByte();

        return ret;
    }

    public void Out(String s) {
        if (UIOutput != null) {
            UIOutput.PrintMessage(PortNumber, s);
        }
    }

    private String GeraPackageData(int Size) {


        byte[] package_data = new byte[Size];

        for (int i = 0; i < Size; i++) {
            package_data[i] = (byte) getRandomByte();
        }

        return new String(package_data);

    }

    public void EnviaPacoteParaRede(Pacote p) {
        
        if (p.PacoteDeRoteamento) {
            p.EnderecoEPortaRoteador = Parameters.EnderecoRoteador + "X";
            EnviaPacoteParaRede(p.getSerializatedTRPackage());
        } else {
            p.EnderecoEPortaRoteador = Parameters.EnderecoRoteador + this.PortNumber;
            EnviaPacoteParaRede(p.getSerializatedDataPackage());
        }

    }

    private void EnviaPacoteParaRede(String dados) {
        try {
            byte[] buf = new byte[UdpListener.BufferSize];

            //A mensagem
            String dString = dados;

            buf = dString.getBytes();

//                DatagramSocket socket = null;
            MulticastSocket socket = null;

            socket = new MulticastSocket();

            InetAddress address = InetAddress.getByName("224.0.0.1");
            //SocketAddress sa = new InetSocketAddress(_port);
            socket.joinGroup(address);

            //Enviar para todas as portas configuradas.
            //Envia a Mensagem multicast para todo o range configurado
            for (int port = Parameters.UdpPortIni; port <= Parameters.UdpPortEnd; port++) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length,
                        address,
                        port);

                socket.send(packet);
            }
            socket.close();

            
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
        }

    }

    private boolean BufferHasData() {
        return bufferEntrada.size() > 0;
    }

    private String getBufferData() {

        if (BufferHasData()) {


            //Pega o valor do buffer
            String p = bufferEntrada.get(0);

            //Remove do buffer
            bufferEntrada.remove(0);

            return p;

        } else {
            return "";
        }
    }

    @Override
    public synchronized void start() {

        //Inicia a thread que recebe os pacotes
        udpListener.inputBuffer = bufferEntrada;
        udpListener.isRunning = true;
        udpListener.start();

        super.start();
    }
//    
//    private void EnviarParaForaDaRede() {
//    }
//
//    private void RecebeuDaRede() {
//    }
}
