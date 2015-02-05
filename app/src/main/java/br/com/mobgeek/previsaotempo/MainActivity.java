package br.com.mobgeek.previsaotempo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    private static final String URL_PREVISAO_TEMPO = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String ICONE = "http://openweathermap.org/img/w/";

    protected RequestQueue fila;

    private EditText edtCidade;
    private Button btnPesquisar;
    private TextView tvTemperatura;
    private ImageView imgIcone;
    private ProgressBar progressBar;

    public void limparUI() {
        progressBar.setVisibility(View.INVISIBLE);
        tvTemperatura.setText("");
        imgIcone.setImageDrawable(null);
    }

    public void pesquisar(View v) {

        String nomeCidade = edtCidade.getText().toString().replace(" ","+").trim();
        if(nomeCidade.equals("")) {
            Toast.makeText(this, "Informe a cidade", Toast.LENGTH_SHORT).show();
            return;
        }


        JsonObjectRequest requisicao = new JsonObjectRequest(
                Request.Method.GET,
                URL_PREVISAO_TEMPO + nomeCidade,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("suemar", "Resposta: " + response.toString());
                        progressBar.setVisibility(View.INVISIBLE);

                        try {
                            JSONObject json_main = response.getJSONObject("main");
                            double temp = json_main.getDouble("temp");
                            temp -= 273.15;
                            tvTemperatura.setText(String.format("%.1f .C",temp));

                            JSONArray json_weather = response.getJSONArray("weather");
                            JSONObject tempo1 = json_weather.getJSONObject(0);

                            // Url completa do ícone
                            String icone = ICONE + tempo1.getString("icon") + ".png";

                            Picasso.with(MainActivity.this).load(icone).into(imgIcone);
                        }
                        catch(Exception ex) {
                            Toast.makeText(MainActivity.this,"Ocorreu um erro", Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("suemar", "Erro: " + error.getMessage());
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this,"Erro: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }
        );

        limparUI();
        progressBar.setVisibility(View.VISIBLE);
        fila.add(requisicao);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fila = Volley.newRequestQueue(this);

        edtCidade = (EditText) findViewById(R.id.edtCidade);
        btnPesquisar = (Button) findViewById(R.id.btnPesquisar);
        tvTemperatura = (TextView) findViewById(R.id.tvTemperatura);
        imgIcone = (ImageView) findViewById(R.id.imgIcone);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        limparUI();
    }




}
