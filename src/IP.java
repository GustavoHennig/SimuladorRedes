
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
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Representa um IP
 *
 * @author Gustavo Augusto Hennig
 * Setembro - Outubro de 2008
 */
public class IP {

    private static Random random = new Random();

    static IP getRandom() {

        IP ip = new IP();
        ip.addr0 = getRandomByte();
        ip.addr1 = getRandomByte();
        ip.addr2 = getRandomByte();
        ip.addr3 = getRandomByte();

        return ip;

    }
    int addr0;
    int addr1;
    int addr2;
    int addr3;

    public void setIp(int[] v) throws Exception {
        addr0 = v[0];
        addr1 = v[1];
        addr2 = v[2];
        addr3 = v[3];
        ValidateBytes(addr0, addr1, addr2, addr3);
    }

    public void setIp(int addr0, int addr1, int addr2, int addr3) throws Exception {
        ValidateBytes(addr0, addr1, addr2, addr3);
        this.addr0 = addr0;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.addr3 = addr3;

    }

    public boolean setIp(String ip) {
        String[] v = ip.split("\\.");
        try {

            if (v.length == 3) {
                addr0 = Integer.valueOf(v[0]);
                addr1 = Integer.valueOf(v[1]);
                addr2 = Integer.valueOf(v[2]);
                ValidateBytes(addr0, addr1, addr2, addr3);
                return true;

            }

            if (v.length != 4) {
                //throw new Exception("Endereço IP inválido");
                return false;
            }

            addr0 = Integer.valueOf(v[0]);
            addr1 = Integer.valueOf(v[1]);
            addr2 = Integer.valueOf(v[2]);
            addr3 = Integer.valueOf(v[3]);
            ValidateBytes(addr0, addr1, addr2, addr3);
            return true;
        } catch (Exception e) {
            return false;
        //throw new Exception("Endereço IP inválido");
        }
    }

    private void ValidateBytes(int... bytes) throws Exception {
        for (int b : bytes) {
            if (b < 0 || b > 255) {
                throw new Exception("Número inválido no endereço IP");
            }
        }
    }

    public void setMachineAddress(int b) {
    }

    public String getIpBase() {
        return Integer.toString(addr0) + "." + Integer.toString(addr1) + "." + Integer.toString(addr2);
    }

    public String getIp() {
        return getIpBase() + "." + Integer.toString(addr3);
    }

    public String getIpFixedSize() {
        return getIpBaseFixedSize() + "." + FormatByte(addr3);
    }

    public String getIpBaseFixedSize() {

        return FormatByte(addr0) + "." + FormatByte(addr1) + "." + FormatByte(addr2);
    }

    private static int getRandomByte() {

        //Não exitste int unsigned
        //int b = (int% Math.abs(i);
        return random.nextInt(255);


    }

    public boolean isLoopbackPacket() {
        return (addr0 == 127 && addr1 == 0 && addr2 == 0);
    }

    private String FormatByte(int b) {

        DecimalFormat i = new DecimalFormat("000");


        return i.format(b);



    }
}

