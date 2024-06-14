package com.atividade.mycity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText;
    ImageButton loginButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.senha);
        loginButton = findViewById(R.id.toLogin);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        class LoginUserTask extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("senha", password);
                return requestHandler.sendPostRequest(Api.URL_READ_USERS, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("LoginResponse", s); // Log da resposta recebida do servidor

                try {
                    JSONObject obj = new JSONObject(s);
                    if (!obj.getBoolean("error")) {
                        JSONObject userJson = obj.getJSONObject("user");
                        Users user = new Users(
                                userJson.getInt("id"),
                                userJson.getString("nome"),
                                userJson.getString("email"),
                                password
                        );
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        LoginUserTask loginUserTask = new LoginUserTask();
        loginUserTask.execute();

        // Adicione logs para verificar os parâmetros enviados para o servidor
        Log.d("tentativa de login", "de login" + email + ", senha: " + password);
    }
}