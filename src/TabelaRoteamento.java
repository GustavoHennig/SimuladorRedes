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


/**
 *
 *
 * @author gustavoh
 */
public class TabelaRoteamento {

    //private int TamanhoTabela = 50;
    public Hashtable<Integer, PortaComunicacao> Portas = new Hashtable<Integer, PortaComunicacao>();
    public ArrayList<TabelaRoteamentoItem> tabelaroteamento = new ArrayList<TabelaRoteamentoItem>();
    public PortaComunicacao RotaPadrao;
    public int TempoVidaRegistro = 30000; //30 segundos
    public ICallBackable  output;
//    public PortaComunicacao getProcuraPortaNaTR(Pacote p) {
//
//
//        if (tabelaroteamento.containsKey(p.HeaderIpDestino.getIpBase())) {
//
//            TabelaRoteamentoItem tri = tabelaroteamento.get(p.HeaderIpDestino.getIpBase());
//            return Portas.get(tri.PortaComunicacao);
//
//        }else{
//            return RotaPadrao;
//        }
//
//
//    }    //private

    public void AtualizaTabela(Pacote p) {

        boolean atualizou = false;
        for(int i = 0; i <  tabelaroteamento.size() ; i++ ){
            TabelaRoteamentoItem t = tabelaroteamento.get(i);

            //Remove itens que expiraram
            if(t.HorarioCriacao + TempoVidaRegistro < System.currentTimeMillis()){
                tabelaroteamento.remove(i);
            }else{

                //Se já existe ele atualiza o tempo de vida na tabela
                if(t.Ip.getIpBase().equals(p.HeaderIpOrigem.getIpBase()) &&  t.PortaComunicacao == p.PortaComunicacao){
                    t.HorarioCriacao = System.currentTimeMillis();
                    atualizou= true;
                }
            }
        }

        if(!atualizou){
            TabelaRoteamentoItem tri = new TabelaRoteamentoItem(p.HeaderIpOrigem, p.PortaComunicacao);
            tri.Metrica = p.Metrica;
            tabelaroteamento.add(tri);
        }
        RefreshScreen();

    }

    /**
     * Procura a melhor rota
     *
     * @param Pacote
     */
    public PortaComunicacao ProcuraMelhorPorta(Pacote p){


        TabelaRoteamentoItem tri = null;
        int MaisPerto = 1000;

        //Percorre a tabela de roteamento e retorna a que tiver a menor métrica
        for( TabelaRoteamentoItem t: tabelaroteamento ){
            if(t.Ip.getIpBase().equals(p.HeaderIpDestino.getIpBase())){

                if(t.Metrica <  MaisPerto){

                    tri = t;
                    MaisPerto = t.Metrica;

                }
            }
        }

        if(tri == null){
            Out("Não econtrado na tabela, escolhida rota padrão");
            return RotaPadrao;
        }else{
            tri.NroUtilizacoes++;
            return Portas.get(tri.PortaComunicacao);
        }
    }

    public String getFormattedTable(){


        StringBuffer sb= new StringBuffer();

        sb.append("Porta\t\tRede\t\tMetrica(Saltos)\t\tAcessos\n");

        for( TabelaRoteamentoItem t: tabelaroteamento ){

            sb.append(t.PortaComunicacao + "\t\t" + t.Ip.getIpBase() + "\t\t" + t.Metrica + "\t\t" + t.NroUtilizacoes +  "\n");

        }
        sb.append("Itens na tabela: " + tabelaroteamento.size() + "\n" );
        return sb.toString();

    }

    void Out(String s){
        if( output != null){
            output.PrintMessage(0, s);
        }
    }

    void RefreshScreen(){
        if( output != null){
            output.AtualizaExibicaoTabelaRoteamento(getFormattedTable());
        }
    }
}
