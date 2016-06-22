
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gustavoh
 */
public class Roteador extends Thread {

    private int BufferMaxSize;
    public boolean FinalizarRoteador = false;
    private ArrayList<Pacote> buffer = new ArrayList<Pacote>();
    public TabelaRoteamento tabelaRoteamento = new TabelaRoteamento();
    public Hashtable<Integer, PortaComunicacao> Portas = new Hashtable<Integer, PortaComunicacao>();
    public ArrayList<String> Saida = new ArrayList();
    public ICallBackable UIOutput;
    private Random random = new Random();

//    public void SendData(String data) {
//
//        if (buffer.size() > BufferMaxSize) {
//            //Descarta o pacote, o buffer está cheio
//        } else {
//            //Cada pacote enviado/recebido por/de uma porta/thread entra pro buffer de processamento
//            buffer.add(data);
//        }
//
//    }
    public Roteador() {
    }
    public static String[] redes;

    public void Inicia() {

        //Criar as instâncias das portas:


        PortaComunicacao pc1 = new PortaComunicacao(1);
        pc1.SubRede = new IP();
        pc1.UIOutput = UIOutput;
        pc1.SubRede = Parameters.IpBaseRede01;

        pc1.bufferPacotes = buffer;
        Portas.put(pc1.PortNumber, pc1);

        PortaComunicacao pc2 = new PortaComunicacao(2);
        pc2.SubRede = new IP();
        pc2.UIOutput = UIOutput;
        pc2.SubRede = Parameters.IpBaseRede02;
        pc2.bufferPacotes = buffer;
        Portas.put(pc2.PortNumber, pc2);

        PortaComunicacao pc3 = new PortaComunicacao(3);
        pc3.SubRede = new IP();
        pc3.UIOutput = UIOutput;
        pc3.SubRede = Parameters.IpBaseRede03;
        pc3.bufferPacotes = buffer;
        Portas.put(pc3.PortNumber, pc3);


        redes = new String[3];
        redes[0] = pc1.SubRede.getIpBase();
        redes[1] = pc2.SubRede.getIpBase();
        redes[2] = pc3.SubRede.getIpBase();


        tabelaRoteamento.Portas = Portas;
        tabelaRoteamento.RotaPadrao = pc2;
        tabelaRoteamento.output = UIOutput;
        //Inicia a thread do roteador
        start();

        //Inicia a thread das portas
        pc1.start();
        pc2.start();
        pc3.start();

    }

    @Override
    public void run() {

        //fica eternamente roteando pacotes, sem critério para parada
        while (true && !FinalizarRoteador) {
            try {

                //Busca valor do buffer
                Pacote p = getBufferData();

                if (p != null) {
                    //Interpreta os dados recebidos

                    if (p.PacoteDeRoteamento) {
                        //Obviamente, atualiza a tabela de roteamento
                        tabelaRoteamento.AtualizaTabela(p);
                    } else {

                        //Decrementa o TTL e passa o pacote adiante
                        p.HeaderTTL--;

                        if (p.HeaderIpDestino.isLoopbackPacket()) {
                            Out("Pacote de loopback descartado.");
                        } else {
                            if (p.HeaderTTL > 0) {

                                //Encontra a melhor porta de saída
                                PortaComunicacao destino = tabelaRoteamento.ProcuraMelhorPorta(p);

                                //Pacote é encaminhado somente se estiver ligado a outro roteador:
                                if(destino.LigadoARoteador()){
                                    destino.EnviaPacoteParaRede(p);
                                }

                                Out("Roteado para: " + destino.PortNumber + " Pacote: " + p.getSerializatedDataPackage());

                            } else {
                                Out("Pacote descartado, TTL = 0");
                            }
                        }

                    //Pacote descartado, TTL zerado
                    }

                }

                EnviaPacoteDeTabelaRoteamento();
                //Faz o roteador esperar 1 milisegundo
                // para liberar a CPU para outras threads e processos do SO.
                sleep(1);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public long UltimoPacoteEnviado = 0;

    public void EnviaPacoteDeTabelaRoteamento() {

        //Se faz tempo que o útlimo pacote de rotamento não é enviado para a rede, o pacote é enviado.
        if (UltimoPacoteEnviado + Parameters.MilisegundosEnviarPacoteRoteamento < System.currentTimeMillis()) {

            //tabelaRoteamento.

            for (PortaComunicacao pc : tabelaRoteamento.Portas.values()) {

                //Coloca as portas locais na tabela de roteamento
                Pacote pck = new Pacote();
                pck.HeaderIpOrigem = pc.SubRede;
                pck.PortaComunicacao = pc.PortNumber;
                pck.Metrica = 1;
                tabelaRoteamento.AtualizaTabela(pck);

                //Envia a tabela local para a rede, em forma de broadcast
                for (TabelaRoteamentoItem tri : tabelaRoteamento.tabelaroteamento) {

                    Pacote p = new Pacote();
                    //p.HeaderIpDestino = tri.Ip;
                    p.HeaderIpOrigem = tri.Ip;
                    p.PortaComunicacao = tri.PortaComunicacao;
                    p.Metrica = tri.Metrica + 1;
                    p.PacoteDeRoteamento = true;
                    p.HeaderTTL = random.nextInt(15);
                    //p.EnderecoEPortaRoteador

                    pc.EnviaPacoteParaRede(p);
                }

            }

            UltimoPacoteEnviado = System.currentTimeMillis();
        }

    }

    public void FinalizaRoteador() {
        FinalizarRoteador = true;

        for (PortaComunicacao pc : Portas.values()) {
            pc.FinalizaPorta();
        }

    }

    private Pacote getBufferData() {

        if (buffer.size() > 0) {


            //Pega o valor do buffer
            Pacote p = buffer.get(0);

            //Remove do buffer
            buffer.remove(0);

            return p;

        } else {
            return null;
        }

    }

    public void Out(String s) {
        if (UIOutput != null) {
            UIOutput.PrintMessage(0, s);
        }
    }
}
