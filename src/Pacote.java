
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

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
/**
 * Representa um pacote.
 *
 * Formato:
 * Pacote de dados:
 * <#>>IP destino#IP origem#TTL#Porta<##>DADOS<<#>
 *
 * @author Gustavo Augusto Hennig
 */
public class Pacote {

    //Formatadores
    public static final String SepSeq = "<##>";
    public static final String SepStart = "<#>>";
    public static final String SepEnd = "<<#>";
    /**
     * Dados do pacote
     */
    public String data = "";
    /**
     * /Tamanho predeterminado do pacote
     */
    public int DataPacketSize = 64;
    public int TRPacketSize = 12;

    /* Ip origem*/
    public IP HeaderIpOrigem;
    /*Ip destino*/
    public IP HeaderIpDestino;
    /* //TTL*/
    public int HeaderTTL;
    /** 
     * Saltos
     */
    public int Metrica;
    /** 
     * Ação do pacote de roteamento
     */
    public int AcaoPctRoteamento;

    /*Error message*/
    public String ErrorMessage = "";
    public int PortaComunicacao = 0;
    //public int PortaDeQuemEnviou = 0;
    public String EnderecoEPortaRoteador = "XXX";
    public boolean PacoteDeRoteamento = false;
//    public Pacote() {
//        data = "";
//    }
//
//    public Pacote(String data) {
//        this.data = data;
//    }
//
//    public Pacote(byte[] data) {
//        this.data = new String(data);
//    }

    /**
     * Interpreta e valida pacote recebido
     * 
     * 
     * @return Retorna false se qualquer erro de formatação for
     *  detectado;
     */
    public boolean ParseRecievedData(String data) {

        try {

//            //Tamanho inválido
//            if (data.length() == DataPacketSize) {
//            } else if (data.length() == TRPacketSize) {
//                this.PacoteDeRoteamento = true;
//            } else {
//                ErrorMessage = "Tamanho inválido do pacote";
//                return false;
//            }

            //Temp variable
            String liquid_data = "";

            //Valida início e fim do pacote
            if (data.startsWith(Pacote.SepStart) && data.endsWith(Pacote.SepEnd)) {
                liquid_data = data.substring(Pacote.SepStart.length(), data.length() - Pacote.SepEnd.length());
            } else {
                ErrorMessage = "Não encontrado início e fim do pacote, o pacote deve estar entre: " + SepStart + " e " + SepEnd;
                //Não encontrado inicio e fim.
                return false;
            }

            PacoteDeRoteamento = !(data.indexOf(Pacote.SepSeq) > 0);

            if (PacoteDeRoteamento) {
                return ParsePacoteRoteamento(liquid_data);
            } else {
                return ParsePacoteDados(liquid_data);
            }



        } catch (Exception exception) {
            //Qualquer erro, como IP inválido
            return false;
        }


    }

    private boolean ParsePacoteRoteamento(String liquid_data) {

        String header[] = liquid_data.split("#");
        //h[0] = Ação
        //h[1] = Origem
        //h[2] = Saltos
        //h[3] = Porta
        if (header.length != 5) {
            ErrorMessage = "Cabeçalho mal formatado. Os 3 campos do cabeçalho da TR devem ser separados por #";
            //Cabeçalho mal formatado
            return false;
        }

        this.AcaoPctRoteamento = getByte(header[0]);

        //Carrega IP Origem
        this.HeaderIpOrigem = new IP();
        if (!this.HeaderIpOrigem.setIp(header[1])) {
            ErrorMessage = "IP origem inválido";
            return false;
        }

        //Cerrega TTL
        this.Metrica = getByte(header[2]);
        this.PortaComunicacao = getByte(header[3]);
        this.EnderecoEPortaRoteador = header[4];

        return true;
    }

    private boolean ParsePacoteDados(String liquid_data) {

        //Quebra em duas partes:
        // v[0] = HEADER
        // v[1] = DATA
        String v[] = liquid_data.split(SepSeq);

        //Valida se foram geradas duas partes
        if (!(v.length == 1 || v.length == 2)) {
            //Pacote mal formatado
            ErrorMessage = "Pacote mal formatado. O cabeçalho deve ser separdo dos dados atraves da sequencia: " + SepSeq;
            return false;
        }

        //Quebra o cabeçalho em 3 partes:
        // header[0] = Ip destino
        // header[1] = Ip origem
        // header[2] = TTL
        // header[3] = Porta
        String header[] = v[0].split("#");


        if (header.length != 5) {
            ErrorMessage = "O cabeçalho mal formatado. Os 5 campos do cabeçado devem ser separados por #";
            //Cabeçalho mal formatado
            return false;
        }

        //Carrega IP Destino
        this.HeaderIpDestino = new IP();
        if (!this.HeaderIpDestino.setIp(header[0])) {
            ErrorMessage = "IP destino inválido";
            return false;
        }

        //Very OLDDDDDDD
//            if (this.HeaderIpDestino.getIp().equals("0.0.0.0")) {
//                this.PacoteDeRoteamento = true;
//            }


        //Carrega IP Origem
        this.HeaderIpOrigem = new IP();
        if (!this.HeaderIpOrigem.setIp(header[1])) {
            ErrorMessage = "IP origem inválido";
            return false;
        }

        //Cerrega TTL
        this.HeaderTTL = getByte(header[2]);

        this.PortaComunicacao = getByte(header[3]);
        this.EnderecoEPortaRoteador = header[4];
        //Dados do pacote
        if (v.length > 1) {
            this.data = v[1];
        }
        return true;
    }

    public String getSerializatedDataPackage() {
        return getSerializatedDataPackage(false);
    }

    public String getSerializatedDataPackage(boolean NoData) {


        StringBuffer sb = new StringBuffer();
        sb.append(SepStart);

        // header[0] = Ip destino
        // header[1] = Ip origem
        // header[2] = TTL

        //HEADER
        sb.append(this.HeaderIpDestino.getIp());
        sb.append("#");
        sb.append(this.HeaderIpOrigem.getIp());
        sb.append("#");
        sb.append(this.HeaderTTL);
        sb.append("#");
        sb.append(this.PortaComunicacao);
        sb.append("#");
        sb.append(this.EnderecoEPortaRoteador);
        sb.append(SepSeq);

        int DataSize = this.DataPacketSize - (sb.length() + SepEnd.length());
        if (DataSize > 0) {
            if (!NoData) {
                sb.append(this.data);
            //sb.append(FormatStringSize(this.data, DataSize));
            }
        }


        sb.append(SepEnd);

        return sb.toString();
    }

    public String getPackageHeader() {

        if (this.PacoteDeRoteamento) {
            return getSerializatedTRPackage();
        } else {
            return getSerializatedDataPackage(true);
        }
    }

    public String getSerializatedTRPackage() {
        StringBuffer sb = new StringBuffer();
        sb.append(SepStart);

        //h[0] = Ação
        //h[1] = Origem
        //h[2] = Saltos

        //HEADER
        sb.append(this.AcaoPctRoteamento);
        sb.append("#");
        sb.append(this.HeaderIpOrigem.getIpBase());
        sb.append("#");
        sb.append(this.Metrica);
        sb.append("#");
        sb.append(this.PortaComunicacao);
        sb.append("#");
        sb.append(this.EnderecoEPortaRoteador);

        sb.append(SepEnd);

        return sb.toString();
    }

    private String FormatStringSize(String s, int size) {

        int dif = size - s.length();
        StringBuffer sb = new StringBuffer(s);

        if (dif > 0) {
            for (int i = 0; i < dif; i++) {
                sb.append(" ");
            }
            return sb.toString();
        } else {
            return sb.substring(0, size);
        }
    }

    private int getByte(String data) {
        try {
            return Integer.valueOf(data);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Somente aceita pacotes eviados para esse roteador
     * Elimina a chance de ocorre loopback, e criar conexões inexistente(atalhos)
     * @return
     */
    public boolean ParaEsteRoteadorEPorta(int Porta) {


        String rot = this.EnderecoEPortaRoteador.substring(0, 2);

        if (!this.EnderecoEPortaRoteador.substring(2).equals("X")) {
            if (getByte(this.EnderecoEPortaRoteador.substring(2)) != Porta) {
                //Pacote não é para essa porta
                return false;
            }
        }

        //Verifica se existe uma ligação com ese roteador.
        for (String s : Parameters.RoteadoresConectados) {
            if (s.startsWith(rot)) {
                return true;
            }
        }
        return false;


    //return Parameters.RoteadoresConectados.contains(this.EnderecoEPortaRoteador);


    //return (this.EnderecoEPortaRoteador.substring(0,2).equalsIgnoreCase(Parameters.EnderecoRoteador));

    }
}
