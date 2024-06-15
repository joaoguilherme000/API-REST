package com.atividade.mycity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PerfilFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private TextView usernameTextView;
    private EditText editUsername;
    private String profilePictureUrl;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        Button deleteButton = view.findViewById(R.id.delete_user);
        progressDialog = new ProgressDialog(requireContext());

        deleteButton.setOnClickListener(v -> showConfirmationDialog());

        ImageView profilePicture = view.findViewById(R.id.profile_picture);
        profilePicture.setOnClickListener(v -> openFileChooser());

        usernameTextView = view.findViewById(R.id.username);
        editUsername = view.findViewById(R.id.edit_username);
        Button alterarUsernameButton = view.findViewById(R.id.alterar_username_button);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Usuário");
        profilePictureUrl = sharedPreferences.getString("profile_picture_url", null);

        usernameTextView.setText(username);

        if (profilePictureUrl != null) {
            Glide.with(this).load(profilePictureUrl).into(profilePicture);
        }

        alterarUsernameButton.setOnClickListener(v -> updateProfile());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("perfilImages");
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpeg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        profilePictureUrl = uri.toString();
                        saveProfileInfo();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Falha ao enviar imagem: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void updateProfile() {
        String newUsername = editUsername.getText().toString().trim();

        if (TextUtils.isEmpty(newUsername)) {
            Toast.makeText(getActivity(), "Por favor, insira um nome de usuário", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageToFirebase();
        } else {
            saveProfileInfo();
        }
        updateUsernameOnServer(newUsername);
    }

    private void saveProfileInfo() {
        String newUsername = editUsername.getText().toString().trim();

        if (TextUtils.isEmpty(newUsername)) {
            Toast.makeText(getActivity(), "Por favor, insira um nome de usuário", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", newUsername);
        editor.putString("profile_picture_url", profilePictureUrl);
        editor.apply();

        Toast.makeText(getActivity(), "Informações do perfil atualizadas com sucesso", Toast.LENGTH_SHORT).show();

        usernameTextView.setText(newUsername);
    }

    private void updateUsernameOnServer(String newUsername) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.URL_UPDATE_USER,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", newUsername);
                            editor.apply();

                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            usernameTextView.setText(newUsername);
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getActivity(), "Erro ao atualizar usuário: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(getUserId()));
                params.put("nome", newUsername);
                return params;
            }
        };

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest);
    }

    private int getUserId() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirmação");
        builder.setMessage("Tem certeza de que deseja excluir sua conta?");
        builder.setPositiveButton("Sim", (dialog, which) -> deleteAccount());
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void deleteAccount() {
        progressDialog.setMessage("Excluindo conta...");
        progressDialog.show();

        int userId = getUserId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.URL_DELETE_USER + userId,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (!jsonObject.getBoolean("error")) {
                            clearUserData();
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Erro ao excluir conta: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(stringRequest);
    }


    private void clearUserData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}