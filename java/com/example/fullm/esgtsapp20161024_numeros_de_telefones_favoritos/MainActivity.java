package com.example.fullm.esgtsapp20161024_numeros_de_telefones_favoritos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    static final int NUM_DIGITOS_DUM_TELEFONE_PORTUGUES = 9;

    //declarar membros de dados
    Button mBtnEntrarTelefoneFavorito;
    EditText mEntrarTelefoneFavorito;

    Set<Long> mSetTelefonesFavoritos;

    View.OnClickListener mTratadorDeClicksNoButtonParaEntrarTelefoneFavorito;


    void personalizarEditTextParaEntradaDeInteiros(
        EditText et, //EditText que será personalizado
        int iMaxDigitsos //nº max de digitos a aceitar no EditText
    ){
        //et.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); //aceita tudo
        //et.setInputType(InputType.TYPE_CLASS_PHONE); //digitos e ainda aceita virgulas
        et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL); //só aceita numeros de 0 a 9
        /*
        TODO: tentar manter NO SUGESTIONS mas ainda assim só aceitar deigitos
         */

        /*
        queremos um filtro. Que filtro? Um que estabeleça o tamanho max do input
         */
        InputFilter[] filtros = new InputFilter[1];
        filtros[0] = new InputFilter.LengthFilter(iMaxDigitsos);

        et.setFilters(filtros);
    }//personalizarEditTextParaEntradaDeInteiros

    boolean adicionarTelefoneFavorito(){
        /*
        1) consultar a EditText que tem o telefone a adicionar
        garantir que é um inteiro, se tudo estiver ok, guardar no Set
        */

        String strTelCandidato = mEntrarTelefoneFavorito.getText().toString();
        try{
            Long novoTelefone = Long.parseLong(strTelCandidato); //consultar
            mSetTelefonesFavoritos.add(novoTelefone); //guardar no Set
            //return true; //se acontecesse estaria errado : ainda há + para fazer
        }//try
        catch (Exception e){
            String strCorreuMalPorque = e.getMessage().toString();
            mUtilFeedback (strCorreuMalPorque);
            return false;
        }//catch

        /*
        2) como o Set ficará modificado, se houver elementos de interface que
        dependam dos seus valores, esses elementos deveriam ser atualizados
        em runtime
        */
        //TODO : ainda não temos interface para mudar
    }//adicionarTelefoneFavorito

    void init(){
        //Não podemos fazer new Set<Long>() porque Set é uma class abstrata
        mSetTelefonesFavoritos = new HashSet<Long>();
        mBtnEntrarTelefoneFavorito = (Button)findViewById(R.id.id_btnEntrarTelefoneFavorito);
        mEntrarTelefoneFavorito = (EditText)findViewById(R.id.id_etEntrarTelefoneFavorito);

        personalizarEditTextParaEntradaDeInteiros (mEntrarTelefoneFavorito, NUM_DIGITOS_DUM_TELEFONE_PORTUGUES);

        mTratadorDeClicksNoButtonParaEntrarTelefoneFavorito = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarTelefoneFavorito();
            }//onCLick
        };//mTratadorDeClicksNoButtonParaEntrarTelefoneFavorito

        mBtnEntrarTelefoneFavorito.setOnClickListener(mTratadorDeClicksNoButtonParaEntrarTelefoneFavorito);

    }//init

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }//onCreate
}
