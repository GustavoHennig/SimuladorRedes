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
 *
 * @author Gustavo Augusto Hennig
 */
public class TabelaRoteamentoItem {
    public IP Ip;
    public int PortaComunicacao;
    public long HorarioCriacao;
    public boolean Excluir;
    public int Metrica;
    public long NroUtilizacoes;

    public TabelaRoteamentoItem(IP ip, int portaComunicacao){
        this.Ip = ip;
        this.PortaComunicacao = portaComunicacao;
        this.HorarioCriacao =  System.currentTimeMillis();

    }
    
}
