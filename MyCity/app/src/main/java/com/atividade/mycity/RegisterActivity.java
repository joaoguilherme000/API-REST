package com.atividade.mycity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText nomeEditText, emailEditText, senhaEditText;
    ImageButton registerButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nomeEditText = findViewById(R.id.nome);
        emailEditText = findViewById(R.id.email);
        senhaEditText = findViewById(R.id.senha);
        registerButton = findViewById(R.id.toRegister);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String nome = nomeEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String senha = senhaEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nome)) {
            nomeEditText.setError("Por favor entre com o nome");
            nomeEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nome)) {
            emailEditText.setError("Por favor entre com o email");
            emailEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nome)) {
            senhaEditText.setError("Por favor entre com o senha");
            senhaEditText.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("nome", nome);
        params.put("senha", senha);
        params.put("email", email);
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_USER, params, CODE_POST_REQUEST);
        request.execute();
    }

    private void lerUser() {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_USERS, null, CODE_GET_REQUEST);
        request.execute();
    }



    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                Toast.makeText(RegisterActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                // depois de dar certo ele entra
                if (!obj.getBoolean("error")) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Finaliza a atividade de registro para não retornar pra ela pressionando Voltar
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestCode == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);


            if (requestCode == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}