package br.com.usinasantafe.pbm.control;

import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.usinasantafe.pbm.model.bean.estaticas.ColabBean;
import br.com.usinasantafe.pbm.model.bean.estaticas.ComponenteBean;
import br.com.usinasantafe.pbm.model.bean.estaticas.EscalaTrabBean;
import br.com.usinasantafe.pbm.model.bean.estaticas.ItemOSBean;
import br.com.usinasantafe.pbm.model.bean.estaticas.OSBean;
import br.com.usinasantafe.pbm.model.bean.estaticas.ParadaBean;
import br.com.usinasantafe.pbm.model.bean.estaticas.ParametroBean;
import br.com.usinasantafe.pbm.model.bean.estaticas.ServicoBean;
import br.com.usinasantafe.pbm.model.bean.variaveis.ApontBean;
import br.com.usinasantafe.pbm.model.bean.variaveis.BoletimBean;
import br.com.usinasantafe.pbm.model.dao.ApontDAO;
import br.com.usinasantafe.pbm.model.dao.BoletimDAO;
import br.com.usinasantafe.pbm.model.dao.ColabDAO;
import br.com.usinasantafe.pbm.model.dao.ComponenteDAO;
import br.com.usinasantafe.pbm.model.dao.EscalaTrabDAO;
import br.com.usinasantafe.pbm.model.dao.ItemOSDAO;
import br.com.usinasantafe.pbm.model.dao.OSDAO;
import br.com.usinasantafe.pbm.model.dao.ParadaDAO;
import br.com.usinasantafe.pbm.model.dao.ParametroDAO;
import br.com.usinasantafe.pbm.model.dao.ServicoDAO;
import br.com.usinasantafe.pbm.util.AtualDadosServ;
import br.com.usinasantafe.pbm.util.Tempo;
import br.com.usinasantafe.pbm.util.VerifDadosServ;

public class MecanicoCTR {

    private ApontBean apontBean;

    public MecanicoCTR() {
    }

    ///////////////////////////////// VERIFICAR DADOS ////////////////////////////////////////////

    public boolean hasElementsColab(){
        ColabDAO colabDAO = new ColabDAO();
        return colabDAO.hasElements();
    }

    public boolean verMatricColab(Long matricColab){
        ColabDAO colabDAO = new ColabDAO();
        return colabDAO.verMatricColab(matricColab);
    }

    public boolean verApont(){
        List<ApontBean> apontList = apontBolApontList();
        boolean ret = (apontList.size() > 0);
        apontList.clear();
        return ret;
    }

    public boolean verOSApont(Long nroOS){
        BoletimDAO boletimDAO = new BoletimDAO();
        ApontDAO apontDAO = new ApontDAO();
        OSDAO osDAO = new OSDAO();
        return (apontDAO.verOSApont(boletimDAO.getBoletimApont().getIdBoletim(), nroOS) && osDAO.verOS(nroOS));
    }

    public boolean verBoletimFechado(){
        BoletimDAO boletimDAO = new BoletimDAO();
        return boletimDAO.verBoletimFechado();
    }

    public boolean verApontSemEnvio(){
        ApontDAO apontDAO = new ApontDAO();
        return apontDAO.verApontSemEnvio();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////// SALVAR/ATUALIZAR DADOS //////////////////////////////////////

    public void insertParametro(String parametros){

        try {

            JSONObject jObj = new JSONObject(parametros);
            JSONArray jsonArray = jObj.getJSONArray("parametro");

            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objeto = jsonArray.getJSONObject(i);
                    Gson gson = new Gson();
                    ParametroDAO parametroDAO = new ParametroDAO();
                    parametroDAO.insert(gson.fromJson(objeto.toString(), ParametroBean.class));
                }

            }

        } catch (Exception e) {
        }
    }

    public void atualSalvarBoletim(ColabBean colabBean){

        ConfigCTR configCTR = new ConfigCTR();

        BoletimDAO boletimDAO = new BoletimDAO();
        boletimDAO.atualSalvarBoletim(configCTR.getConfig().getEquipConfig(), colabBean.getIdColab(), getEscalaTrab(colabBean.getIdEscalaTrabColab()).getHorarioEntEscalaTrab());

    }

    public void atualBoletimSApont(){
        BoletimDAO boletimDAO = new BoletimDAO();
        boletimDAO.atualBoletimSApont();
    }

    public void salvarApont(){
        BoletimDAO boletimDAO = new BoletimDAO();
        ApontDAO apontDAO = new ApontDAO();
        apontDAO.salvarApont(apontBean, getEscalaTrab(getColabApont().getIdEscalaTrabColab()).getHorarioEntEscalaTrab(), boletimDAO.getBoletimApont());

    }

    public void fecharBoletim(){
        BoletimDAO boletimDAO = new BoletimDAO();
        boletimDAO.fecharBoletim(boletimDAO.getBoletimApont());
        ApontDAO apontDAO = new ApontDAO();
        apontDAO.fecharApont(getUltApont());
    }

    public void forcarFechBoletim(){
        BoletimDAO boletimDAO = new BoletimDAO();
        ApontDAO apontDAO = new ApontDAO();
        String dthrFinal = Tempo.getInstance().dataFinalizarBol(boletimDAO.getBoletimApont().getDthrInicialBoletim(), getEscalaTrab(getColabApont().getIdEscalaTrabColab()).getHorarioSaiEscalaTrab());
        boletimDAO.fecharBoletim(boletimDAO.getBoletimApont(), dthrFinal);
        apontDAO.fecharApont(getUltApont(), dthrFinal);
    }

    public void finalizarApont(){
        BoletimDAO boletimDAO = new BoletimDAO();
        ApontDAO apontDAO = new ApontDAO();
        apontDAO.finalizarApont(apontDAO.apontList(boletimDAO.getBoletimApont().getIdBoletim()).get(0));
    }

    public void interroperApont(){
        BoletimDAO boletimDAO = new BoletimDAO();
        ApontDAO apontDAO = new ApontDAO();
        apontDAO.interroperApont(apontDAO.apontList(boletimDAO.getBoletimApont().getIdBoletim()).get(0));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////// GET DADOS /////////////////////////////////////////////

    public BoletimBean getBoletimApont(){
        BoletimDAO boletimDAO = new BoletimDAO();
        return boletimDAO.getBoletimApont();
    }

    public EscalaTrabBean getEscalaTrab(Long idEscalaTrab){
        EscalaTrabDAO escalaTrabDAO = new EscalaTrabDAO();
        return escalaTrabDAO.getEscalaTrab(idEscalaTrab);
    }

    public ColabBean getColab(Long matricColab){
        ColabDAO colabDAO = new ColabDAO();
        return colabDAO.getMatricColab(matricColab);
    }

    public ColabBean getColabApont(){
        ColabDAO colabDAO = new ColabDAO();
        return colabDAO.getIdColab(getBoletimApont().getIdFuncBoletim());
    }

    public List<ApontBean> apontBolApontList(){
        ApontDAO apontDAO = new ApontDAO();
        return apontDAO.apontList(getBoletimApont().getIdBoletim());
    }

    public ApontBean getUltApont(){
        List<ApontBean> apontList = apontBolApontList();
        ApontBean apontBean = apontList.get(0);
        apontList.clear();
        return apontBean;
    }

    public ApontBean getApontBean() {
        return apontBean;
    }

    public OSBean getOS(){
        OSDAO osDAO = new OSDAO();
        return osDAO.getOS(apontBean.getOsApont());
    }

    public ServicoBean getServico(Long idServItemOS){
        ServicoDAO servicoDAO = new ServicoDAO();
        return servicoDAO.getServico(idServItemOS);
    }

    public ComponenteBean getComponente(Long idCompItemOS){
        ComponenteDAO componenteDAO = new ComponenteDAO();
        return componenteDAO.getComponente(idCompItemOS);
    }

    public List<ApontBean> apontList(){
        ApontDAO apontDAO = new ApontDAO();
        return apontDAO.apontList(getBoletimApont().getIdBoletim());
    }

    public List<ItemOSBean> itemOSList(){
        ItemOSDAO itemOSDAO = new ItemOSDAO();
        return itemOSDAO.itemOSList(getOS().getIdOS());
    }

    public List<ParadaBean> paradaList(){
        ParadaDAO paradaDAO = new ParadaDAO();
        return paradaDAO.paradaCodList();
    }

    public ParadaBean getParadaCod(Long codParada){
        ParadaDAO paradaDAO = new ParadaDAO();
        return paradaDAO.getParadaCod(codParada);
    }

    public ParadaBean getParadaId(Long idParada){
        ParadaDAO paradaDAO = new ParadaDAO();
        return paradaDAO.getParadaId(idParada);
    }

    public ParametroBean getParametro(){
        ParametroDAO parametroDAO = new ParametroDAO();
        return parametroDAO.getParametro();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////// SET DADOS /////////////////////////////////////////////

    public void setApontBean(ApontBean apontBean) {
        this.apontBean = apontBean;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////// VERIFICAÇÃO E ATUALIZAÇÃO DE DADOS POR SERVIDOR /////////////////////

    public void verOS(String dado, Context telaAtual, Class telaProx, ProgressDialog progressDialog){
        OSDAO osDAO = new OSDAO();
        osDAO.verOS(dado, telaAtual, telaProx, progressDialog);
    }

    public void atualDadosColab(Context telaAtual, Class telaProx, ProgressDialog progressDialog){
        ArrayList colabArrayList = new ArrayList();
        colabArrayList.add("ColabBean");
        AtualDadosServ.getInstance().atualGenericoBD(telaAtual, telaProx, progressDialog, colabArrayList);
    }

    public void atualDadosItemOS(Context telaAtual, Class telaProx, ProgressDialog progressDialog){
        ArrayList itemOSArrayList = new ArrayList();
        itemOSArrayList.add("ComponenteBean");
        itemOSArrayList.add("ServicoBean");
        AtualDadosServ.getInstance().atualGenericoBD(telaAtual, telaProx, progressDialog, itemOSArrayList);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////// RECEBER DADOS SERVIDOR ///////////////////////////////////////

    public void recDadosOS(String result){

        try {

            if (!result.contains("exceeded")) {

                int posicao = result.indexOf("#") + 1;
                String objPrinc = result.substring(0, result.indexOf("#"));
                String objSeg = result.substring(posicao);

                JSONObject jObj = new JSONObject(objPrinc);
                JSONArray jsonArray = jObj.getJSONArray("dados");

                if (jsonArray.length() > 0) {

                    OSDAO osDAO = new OSDAO();
                    osDAO.deleteAllOS();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objeto = jsonArray.getJSONObject(i);
                        Gson gson = new Gson();
                        osDAO.insertOS(gson.fromJson(objeto.toString(), OSBean.class));
                    }

                    jObj = new JSONObject(objSeg);
                    jsonArray = jObj.getJSONArray("dados");

                    ItemOSDAO itemOSDAO = new ItemOSDAO();
                    itemOSDAO.deleteAllItemOS();

                    for (int j = 0; j < jsonArray.length(); j++) {

                        JSONObject objeto = jsonArray.getJSONObject(j);
                        Gson gson = new Gson();
                        itemOSDAO.insertItemOS(gson.fromJson(objeto.toString(), ItemOSBean.class));

                    }

                    VerifDadosServ.getInstance().pulaTelaComTerm();

                } else {
                    VerifDadosServ.getInstance().msgComTerm("NÃO EXISTE O.S. PARA ESSE COLABORADOR! POR FAVOR, ENTRE EM CONTATO COM A AREA QUE CRIA O.S. PARA APONTAMENTO.");
                }

            } else {
                VerifDadosServ.getInstance().msgComTerm("EXCEDEU TEMPO LIMITE DE PESQUISA! POR FAVOR, PROCURE UM PONTO MELHOR DE CONEXÃO DOS DADOS.");
            }

        } catch (Exception e) {
            VerifDadosServ.getInstance().msgComTerm("FALHA DE PESQUISA DE OS! POR FAVOR, TENTAR NOVAMENTE COM UM SINAL MELHOR.");
        }

    }

    public void atualBolAberto(String retorno) {

        try {

            int pos1 = retorno.indexOf("#") + 1;
            int pos2 = retorno.indexOf("_") + 1;
            String objPrinc = retorno.substring(pos1, pos2);
            String objSeg = retorno.substring(pos2);

            JSONObject boletimJsonObject = new JSONObject(objPrinc);
            JSONArray boletimJsonArray = boletimJsonObject.getJSONArray("boletim");

            JSONObject apontJsonObject = new JSONObject(objSeg);
            JSONArray apontJsonArray = apontJsonObject.getJSONArray("apont");

            if (boletimJsonArray.length() > 0) {

                for (int i = 0; i < boletimJsonArray.length(); i++) {

                    JSONObject objeto = boletimJsonArray.getJSONObject(i);
                    Gson gson = new Gson();

                    BoletimBean boletimBean = gson.fromJson(objeto.toString(), BoletimBean.class);

                    BoletimDAO boletimDAO = new BoletimDAO();
                    boletimDAO.atualIdExtBol(boletimBean);

                    ApontDAO apontDAO = new ApontDAO();
                    apontDAO.updApontIdExtBoletim(boletimBean.getIdBoletim(), boletimBean.getIdExtBoletim());

                }

                for (int i = 0; i < apontJsonArray.length(); i++) {

                    JSONObject objeto = apontJsonArray.getJSONObject(i);
                    Gson gson = new Gson();

                    ApontBean apontBean = gson.fromJson(objeto.toString(), ApontBean.class);

                    ApontDAO apontDAO = new ApontDAO();
                    apontDAO.updApontEnviado(apontBean.getIdApont(), apontBean.getIdBolApont());

                }

            }



        } catch (Exception e) {
            Tempo.getInstance().setEnvioDado(true);
        }

    }

    public void delBolFechado(String retorno) {

        try {

            int pos1 = retorno.indexOf("#") + 1;
            String dados = retorno.substring(pos1);

            JSONObject jObj = new JSONObject(dados);
            JSONArray jsonArray = jObj.getJSONArray("boletim");

            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject objeto = jsonArray.getJSONObject(i);
                    Gson gson = new Gson();

                    BoletimBean boletimBean = gson.fromJson(objeto.toString(), BoletimBean.class);

                    BoletimDAO boletimDAO = new BoletimDAO();
                    boletimDAO.delBoletim(boletimBean);

                    ApontDAO apontDAO = new ApontDAO();
                    apontDAO.delApont(boletimBean.getIdBoletim());

                }

            }

        } catch (Exception e) {
            Tempo.getInstance().setEnvioDado(true);
        }

    }

    public void atualApont(String retorno) {

        try {

            int pos1 = retorno.indexOf("#") + 1;
            String dados = retorno.substring(pos1);

            JSONObject jObj = new JSONObject(dados);
            JSONArray jsonArray = jObj.getJSONArray("boletim");

            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject objeto = jsonArray.getJSONObject(i);
                    Gson gson = new Gson();

                    ApontDAO apontDAO = new ApontDAO();
                    apontDAO.updApont(gson.fromJson(objeto.toString(), ApontBean.class));

                }

            }

        } catch (Exception e) {
            Tempo.getInstance().setEnvioDado(true);
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////// ENVIO DADOS SERVIDOR ///////////////////////////////////////

    public String dadosEnvioBolFechado() {

        BoletimDAO boletimDAO = new BoletimDAO();
        String dadosBoletim = boletimDAO.dadosBolFechado();

        ApontDAO apontDAO = new ApontDAO();
        String dadosApont = apontDAO.dadosEnvioApont(boletimDAO.idBolFechadoList());

        return dadosBoletim + "_" + dadosApont;

    }

    public String dadosEnvioBolSemEnvio() {

        BoletimDAO boletimDAO = new BoletimDAO();
        ApontDAO apontDAO = new ApontDAO();

        String dadosBoletim = boletimDAO.dadosBolAbertoSemEnvio(apontDAO.idBolAbertoList());
        String dadosApont = apontDAO.dadosEnvioApont(apontDAO.idBolAbertoList());

        return dadosBoletim + "_" + dadosApont;

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

}
