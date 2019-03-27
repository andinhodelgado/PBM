package br.com.usinasantafe.pbm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.usinasantafe.pbm.bo.Tempo;
import br.com.usinasantafe.pbm.pst.EspecificaPesquisa;
import br.com.usinasantafe.pbm.to.estaticas.ColabTO;
import br.com.usinasantafe.pbm.to.estaticas.EscalaTrabTO;
import br.com.usinasantafe.pbm.to.variaveis.ApontTO;
import br.com.usinasantafe.pbm.to.variaveis.BoletimTO;

public class MenuFuncaoActivity extends ActivityGeneric {

    private ListView lista;
    private PBMContext pbmContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_funcao);

        pbmContext = (PBMContext) getApplication();

        ArrayList<String> itens = new ArrayList<String>();

        itens.add("APONTAMENTO");
        itens.add("FINALIZAR/INTERROPER");
        itens.add("FINALIZAR TURNO");

        AdapterList adapterList = new AdapterList(this, itens);
        lista = (ListView) findViewById(R.id.listViewMenuFuncao);
        lista.setAdapter(adapterList);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position,
                                    long id) {

                TextView textView = (TextView) v.findViewById(R.id.textViewItemList);
                String text = textView.getText().toString();

                ArrayList boletimPesqList = new ArrayList();
                EspecificaPesquisa pesquisa = new EspecificaPesquisa();
                pesquisa.setCampo("idFuncBoletim");
                pesquisa.setValor(pbmContext.getColabTO().getIdColab());
                boletimPesqList.add(pesquisa);

                EspecificaPesquisa pesquisa2 = new EspecificaPesquisa();
                pesquisa2.setCampo("statusBoletim");
                pesquisa2.setValor(1L);
                boletimPesqList.add(pesquisa2);

                BoletimTO boletimTO = new BoletimTO();
                List boletimList = boletimTO.get(boletimPesqList);
                boletimTO = (BoletimTO) boletimList.get(0);

                ApontTO apontTO = new ApontTO();
                List apontList = apontTO.getAndOrderBy("idBolApont", boletimTO.getIdBoletim(), "idApont", false);

                if (text.equals("APONTAMENTO")) {

                    Intent it;

                    if(apontList.size() == 0){
                        ColabTO colabTO = pbmContext.getColabTO();
                        EscalaTrabTO escalaTrabTO = new EscalaTrabTO();
                        List escalaTrabList = escalaTrabTO.get("idEscalaTrab",colabTO.getIdEscalaTrabColab());
                        escalaTrabTO = (EscalaTrabTO) escalaTrabList.get(0);
                        if(Tempo.getInstance().verifDataHora(Tempo.getInstance().dataSHoraComTZ() + " " + escalaTrabTO.getHorarioEntEscalaTrab())){
                            it = new Intent(MenuFuncaoActivity.this, OSActivity.class);
                            startActivity(it);
                            finish();
                        }
                        else{
                            pbmContext.setVerTela(1);
                            it = new Intent(MenuFuncaoActivity.this, ListaParadaActivity.class);
                            startActivity(it);
                            finish();
                        }
                    }
                    else{
                        apontTO = (ApontTO) apontList.get(0);
                        if(apontTO.getParadaApont() == 0L){
                            it = new Intent(MenuFuncaoActivity.this, OSActivity.class);
                            startActivity(it);
                            finish();
                        }
                        else{
                            if(Tempo.getInstance().verifDataHora(apontTO.getDthrFinalApont())){
                                it = new Intent(MenuFuncaoActivity.this, OSActivity.class);
                                startActivity(it);
                                finish();
                            }
                            else{
                                pbmContext.setVerTela(1);
                                it = new Intent(MenuFuncaoActivity.this, ListaParadaActivity.class);
                                startActivity(it);
                                finish();
                            }
                        }
                    }

                } else if (text.equals("FINALIZAR/INTERROPER")) {

                    if(apontList.size() > 0) {
                        apontTO = (ApontTO) apontList.get(0);
                        if(apontTO.getParadaApont() == 0L) {
                            Intent it = new Intent(MenuFuncaoActivity.this, OpcaoInterFinalActivity.class);
                            startActivity(it);
                            finish();
                        }
                        else{
                            AlertDialog.Builder alerta = new AlertDialog.Builder( MenuFuncaoActivity.this);
                            alerta.setTitle("ATENÇÃO");
                            alerta.setMessage("NÃO EXISTE APONTAMENTO PARA FINALIZAR/INTERROMPER.");
                            alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            alerta.show();
                        }

                    }
                    else{
                        AlertDialog.Builder alerta = new AlertDialog.Builder( MenuFuncaoActivity.this);
                        alerta.setTitle("ATENÇÃO");
                        alerta.setMessage("NÃO EXISTE APONTAMENTO PARA FINALIZAR/INTERROMPER.");
                        alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        alerta.show();
                    }

                } else if (text.equals("FINALIZAR TURNO")) {

                    Intent it;

                    if(apontList.size() > 0) {

                        apontTO = (ApontTO) apontList.get(0);
                        if(apontTO.getParadaApont() == 0L){

                            if(apontTO.getParadaApont() == 0L){
                                apontTO.setDthrFinalApont(Tempo.getInstance().datahora());
                                apontTO.setStatusApont(0L);
                                apontTO.update();
                            }

                            boletimTO.setDthrFinalBoletim(Tempo.getInstance().datahora());
                            boletimTO.setStatusBoletim(2L);
                            boletimTO.update();

                            it = new Intent(MenuFuncaoActivity.this, MenuInicialActivity.class);
                            startActivity(it);
                            finish();
                        }
                        else{
                            if(Tempo.getInstance().verifDataHora(apontTO.getDthrFinalApont())){

                                apontTO = (ApontTO) apontList.get(0);
                                if(apontTO.getParadaApont() == 0L){
                                    apontTO.setDthrFinalApont(Tempo.getInstance().datahora());
                                    apontTO.setStatusApont(0L);
                                    apontTO.update();
                                }

                                boletimTO.setDthrFinalBoletim(Tempo.getInstance().datahora());
                                boletimTO.setStatusBoletim(2L);
                                boletimTO.update();

                                it = new Intent(MenuFuncaoActivity.this, MenuInicialActivity.class);
                                startActivity(it);
                                finish();

                            }
                            else{
                                pbmContext.setVerTela(2);
                                it = new Intent(MenuFuncaoActivity.this, ListaParadaActivity.class);
                                startActivity(it);
                                finish();
                            }
                        }

                    }
                    else{
                        AlertDialog.Builder alerta = new AlertDialog.Builder( MenuFuncaoActivity.this);
                        alerta.setTitle("ATENÇÃO");
                        alerta.setMessage("O BOLETIM NÃO PODE SER ENCERRADO SEM APONTAMENTO! POR FAVOR, APONTE O MESMO.");
                        alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        alerta.show();
                    }


                }

            }

        });

    }

    public void onBackPressed()  {
    }

}
