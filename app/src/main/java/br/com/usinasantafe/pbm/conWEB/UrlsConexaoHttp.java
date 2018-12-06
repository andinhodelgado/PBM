package br.com.usinasantafe.pbm.conWEB;

public class UrlsConexaoHttp {

    private int tipoEnvio = 1;


    public static String urlPrincipal = "http://www.usinasantafe.com.br/pbmdev/";
    public static String urlPrincEnvio = "http://www.usinasantafe.com.br/pbmdev/";

    public static String localPSTEstatica = "br.com.usinasantafe.pbm.to.estaticas.";
    public static String localUrl = "br.com.usinasantafe.pbm.conWEB.UrlsConexaoHttp";

    public static String ColabTO = urlPrincipal + "colab.php";
    public static String ComponenteTO = urlPrincipal + "componente.php";
    public static String ParadaTO = urlPrincipal + "parada.php";
    public static String ServicoTO = urlPrincipal + "servico.php";

    public UrlsConexaoHttp() {
        // TODO Auto-generated constructor stub
    }

    public String getsApontVCana() {
        return urlPrincEnvio + "apontvcana.php";
    }

    public String urlVerifica(String classe) {
        String retorno = "";
        if (classe.equals("Equip")) {
            retorno = urlPrincEnvio + "verequip.php";
        } else if (classe.equals("Colab")) {
            retorno = urlPrincEnvio + "colab.php";
        } else if (classe.equals("Parada")) {
            retorno = urlPrincEnvio + "parada.php";
        } else if (classe.equals("Atualiza")) {
            retorno = urlPrincEnvio + "atualizaaplic.php";
        }
        return retorno;
    }

}