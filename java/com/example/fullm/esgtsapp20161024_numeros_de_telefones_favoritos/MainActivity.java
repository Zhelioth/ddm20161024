package com.example.fullm.esgtsapp20161024_numeros_de_telefones_favoritos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //declarar membros de dados
    Context mContext;
    static final int NUM_DIGITOS_DUM_TELEFONE_PORTUGUES = 9;
    //cautela com a grandeza deste número
    //tem que ser de 16 bits (até 32768) em Android <7
    static final int TELEFONES_FAVORITOS_CODE = 12345;

    Button mBtnEntrarTelefoneFavorito;
    EditText mEntrarTelefoneFavorito;
    //para representar o contentor de Buttons para telefonar os favoritos
    LinearLayout mLlTelefonesFavoritos;

    Set<Long> mSetTelefonesFavoritos;

    View.OnClickListener mTratadorDeClicksNoButtonParaEntrarTelefoneFavorito;
    //----------------------------------------------------------------------------------------------

    public boolean utilPedirQualquerPermissaoEmRuntime(
            String strPermissao //a permissao que pretendemos pedir
    ) {
        //XML : android.permission.CALL.PHONE
        //JAVA : Manifest.permission.CALL.PHONE;

        //começar por verificar se em ocasiões anteriores o user já
        // não autorizou a app para a permissao em causa
        int iResultadoDaConsulta =
                ContextCompat.checkSelfPermission(
                        mContext,
                        strPermissao
                );

        boolean bAutorizada =
                iResultadoDaConsulta == PackageManager.PERMISSION_GRANTED;

        if (bAutorizada) {
            //app já estava previamente autorizada
            return true;
        } else {
            //app não estava previamente autorizada
            try {
                ActivityCompat.requestPermissions(
                        MainActivity.this,//android.app.Activity
                        new String[]{strPermissao},//array de permissões
                        TELEFONES_FAVORITOS_CODE//código numérico da nossa app
                );
                //TODO: implementar um callback
                return true; //fomos capazes de fazer o pedido
            } catch (Exception e) {
                String strErroAoPedidoPermissoes = e.getMessage().toString();
                mUtilFeedback(strErroAoPedidoPermissoes);
                return false;
            }//catch
        }//else não estava a ser utilizada
    }//utilPedirQualquerPermissaoEmRuntime

    //----------------------------------------------------------------------------------------------
    public boolean utilMarcarNumeroDeTelefone (String strNumero){return utilDialPhoneNumber(strNumero);}
    public boolean utilDialPhoneNumber(
            String strNumero //string correspondente ao nº exº 112
    ) {
        /*
        //o código abaixo serviu apenas para explicitar paralelos entre abrir um URL externamente
        //e marcar um nº telefone
        //android.content.Intent
        Intent abrirUrlComOSistema = new Intent(Intent.ACTION_VIEW);
        //dados? que Url abrir
        //java.net.Uri
        Uri url = Uri.parse("http://www.dn.pt");
        abrirUrlComOSistema.setData(url);

        //confere uma "Activity Stack" própria ao componente

        abrirUrlComOSistema.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(abrirUrlComOSistema);
        */
        Intent telefonar = new Intent(Intent.ACTION_CALL);
        Uri dadosParaTelefonar = Uri.parse("tel:" + strNumero);
        telefonar.setData(dadosParaTelefonar);
        //desde android -5.0 que as permisões podem ser alteradas/aceites/rejetadas pelo utilizador
        // em runtime; ou seja, não basta expressá-las em AndroidManifest.xml
        //android.permission.CALL_PHONE
        try {
            startActivity(telefonar);
            return true;
        } catch (SecurityException e) {
            mUtilFeedback("Sem permissões runtime para telefonar");
            return false;
        }
    }//utilDialPhoneNumber

    public void personalizarEditTextParaEntradaDeInteiros(
            EditText et, //EditText que será personalizado
            int iMaxDigitsos //nº max de digitos a aceitar no EditText
    ) {
        //et.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS); //aceita tudo
        //et.setInputType(InputType.TYPE_CLASS_PHONE); //digitos e ainda aceita virgulas
        et.setInputType(InputType.TYPE_CLASS_NUMBER);

        /*
        como aceitar números com virgula


        só aceita numeros de 0 a 9
        et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        TODO: tentar manter NO SUGESTIONS mas ainda assim só aceitar deigitos
         */

        /*
        queremos um filtro. Que filtro? Um que estabeleça o tamanho max do input
         */
        InputFilter[] filtros = new InputFilter[1];
        filtros[0] = new InputFilter.LengthFilter(iMaxDigitsos);

        et.setFilters(filtros);
    }//personalizarEditTextParaEntradaDeInteiros

    public void mUtilFeedback(String mensagem) {
        //por defeito duração LONG
        Toast t = Toast.makeText(
                mContext,
                mensagem,
                Toast.LENGTH_LONG
        );
    }//mUtilFeedback

    public void mUtilFeedback(String mensagem, int duracao) {
        duracao = duracao < 30 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
        Toast t = Toast.makeText(
                mContext,
                mensagem,
                duracao
        );
        t.show();
    }//mUtilFeedback


    boolean adicionarTelefoneFavorito() {
        /*
        1) consultar a EditText que tem o telefone a adicionar
        garantir que é um inteiro, se tudo estiver ok, guardar no Set
        */

        String strTelCandidato = mEntrarTelefoneFavorito.getText().toString();
        try {
            Long novoTelefone = Long.parseLong(strTelCandidato); //consultar
            mSetTelefonesFavoritos.add(novoTelefone); //guardar no Set
            return true; //se acontecesse estaria errado : ainda há + para fazer
        }//try
        catch (Exception e) {
            String strCorreuMalPorque = e.getMessage().toString();
            mUtilFeedback(strCorreuMalPorque);
            return false;
        }//catch

        /*
        2) como o Set ficará modificado, se houver elementos de interface que
        dependam dos seus valores, esses elementos deveriam ser atualizados
        em runtime
        */
        //TODO : ainda não temos interface para mudar

        /*
        já temos interface (mLlTelefonesFavoritos)
         */
        atualizarButtonsParaTelefonar();
        return false;

    }//adicionarTelefoneFavorito

    void atualizarButtonsParaTelefonar(){
        Long[] numerosDeTelefoneEnquantoArrayDeLongs = mSetTelefonesFavoritos.toArray(
                new Long[mSetTelefonesFavoritos.toArray().length]
        );

        for (
                Long n : numerosDeTelefoneEnquantoArrayDeLongs
            ){
            Button b = new Button(mContext);
            b.setText(n.toString());
            b.setOnClickListener(metodoQueRespondeAClicksEmNumerosDeTelefone);
            mLlTelefonesFavoritos.addView(b);
        }//for
    }//atualizarButtonsParaTelefonar

    View.OnClickListener metodoQueRespondeAClicksEmNumerosDeTelefone =
            new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    if (view instanceof Button){
                        Button b = (Button)view;
                        String numero = b.getText().toString();
                        utilDialPhoneNumber(numero);
                    }//if
                }//onClick
            };

    void init() {
        mContext = MainActivity.this;
        utilPedirQualquerPermissaoEmRuntime(Manifest.permission.CALL_PHONE);
        //Não podemos fazer new Set<Long>() porque Set é uma class abstrata
        mSetTelefonesFavoritos = new HashSet<Long>();
        mBtnEntrarTelefoneFavorito = (Button) findViewById(R.id.id_btnEntrarTelefoneFavorito);
        mEntrarTelefoneFavorito = (EditText) findViewById(R.id.id_etEntrarTelefoneFavorito);
        mLlTelefonesFavoritos = (LinearLayout) findViewById(R.id.id_llTelefonesFavoritos);

        personalizarEditTextParaEntradaDeInteiros(mEntrarTelefoneFavorito, NUM_DIGITOS_DUM_TELEFONE_PORTUGUES);

        mTratadorDeClicksNoButtonParaEntrarTelefoneFavorito = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarTelefoneFavorito();
            }//onCLick
        };//mTratadorDeClicksNoButtonParaEntrarTelefoneFavorito

        mBtnEntrarTelefoneFavorito.setOnClickListener(mTratadorDeClicksNoButtonParaEntrarTelefoneFavorito);

        //utilDialPhoneNumber("123");
    }//init

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }//onCreate
}
