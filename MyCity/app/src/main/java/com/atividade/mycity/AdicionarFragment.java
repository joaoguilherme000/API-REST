package com.atividade.mycity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdicionarFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 2;
    private StorageReference storageReference;
    private ImageView imagemSelecionada;
    private EditText editText;
    private Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_adicionar, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();

        CardView imagem = rootView.findViewById(R.id.imagem);
        imagemSelecionada = rootView.findViewById(R.id.imagemSelecionada);
        editText = rootView.findViewById(R.id.descricao);
        ImageButton addButton = rootView.findViewById(R.id.addButton);

        imagem.setOnClickListener(v -> pickImage());

        addButton.setOnClickListener(v -> uploadData());

        return rootView;
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);

                imagemSelecionada.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadData() {
        if (bitmap == null) {
            Toast.makeText(requireContext(), "Por favor, selecione uma imagem", Toast.LENGTH_SHORT).show();
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageData = baos.toByteArray();

        String imageFileName = "images/" + System.currentTimeMillis() + ".jpeg";
        StorageReference imageRef = storageReference.child(imageFileName);

        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Toast.makeText(requireContext(), "Upload bem sucedido: " + imageUrl, Toast.LENGTH_SHORT).show();

                    String descricao = editText.getText().toString().trim();
                    enviar(descricao, imageUrl);

                }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Falha ao obter URL da imagem", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(requireContext(), "Falha ao fazer upload: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviar(String descricao, String imageUrl) {
        class EnviarDadosParaServidor extends AsyncTask<Void, Void, String> {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Preparar os parâmetros da solicitação
                    URL url = new URL(Api.URL_UPLOAD_PROBLEMA);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    // Construir os parâmetros da solicitação
                    HashMap<String, String> params = new HashMap<>();
                    params.put("descricao", descricao);
                    params.put("foto", imageUrl);

                    // Enviar os dados
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                    writer.write(getParamsString(params));
                    writer.flush();
                    writer.close();
                    os.close();

                    // Obter a resposta do servidor
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                        }
                        in.close();
                        return sb.toString();
                    } else {
                        return "Erro ao enviar os dados: " + responseCode;
                    }
                } catch (Exception e) {
                    return "Exceção: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject obj = new JSONObject(result);
                    boolean error = obj.getBoolean("error");
                    String message = obj.getString("message");

                    if (!error) {
                        Toast.makeText(requireContext(), "Dados enviados com sucesso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Erro ao processar a resposta do servidor", Toast.LENGTH_SHORT).show();
                }
            }
        }

        EnviarDadosParaServidor enviarDadosParaServidor = new EnviarDadosParaServidor();
        enviarDadosParaServidor.execute();
    }

    private String getParamsString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}