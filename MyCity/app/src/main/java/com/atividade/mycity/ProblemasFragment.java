package com.atividade.mycity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class ProblemasFragment extends Fragment {

    private static final String URL_GET_PROBLEMA = Api.URL_GET_PROBLEMA;

    private List<Problema> problemasList;
    private ProblemasAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_problemas, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        problemasList = new ArrayList<>();
        adapter = new ProblemasAdapter(problemasList);
        recyclerView.setAdapter(adapter);

        // Método para buscar problemas da API
        fetchProblemas();

        return view;
    }

    private void fetchProblemas() {
        @SuppressLint("NotifyDataSetChanged") StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_PROBLEMA,
                response -> {
                    try {
                        Log.d("API Response", "Response: " + response); // Log para verificar a resposta da API

                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            JSONArray problemasArray = jsonObject.getJSONArray("problemas");
                            for (int i = 0; i < problemasArray.length(); i++) {
                                JSONObject problemaObject = problemasArray.getJSONObject(i);
                                int id = problemaObject.getInt("id");
                                String foto = problemaObject.getString("foto");
                                Log.d("URL da Imagem", "URL: " + foto);
                                String descricao = problemaObject.getString("descricao");

                                Problema problema = new Problema(id, foto, descricao);
                                problemasList.add(problema);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Erro ao processar JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getActivity(), "Erro na requisição: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "Erro na requisição: " + error.getMessage()); // Log para verificar erros na requisição
                });

        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(stringRequest);
    }


    private static class ProblemasAdapter extends RecyclerView.Adapter<ProblemasAdapter.ViewHolder> {

        private final List<Problema> problemasList;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView textView;
            public final ImageView imageView;

            public ViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.textView);
                imageView = v.findViewById(R.id.imageView);
            }
        }

        public ProblemasAdapter(List<Problema> problemasList) {
            this.problemasList = problemasList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.problemas_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Problema problema = problemasList.get(position);
            holder.textView.setText(problema.getDescricao());

            // Carregar imagem usando Glide
            if (!problema.getFoto().isEmpty()) {
                Log.d("URL da Imagem", "URL: " + problema.getFoto()); // Log para verificar a URL da imagem
                Glide.with(holder.imageView.getContext())
                        .asBitmap()
                        .load(problema.getFoto())
                        .placeholder(R.drawable.ic_default_image)
                        .error(R.drawable.ic_default_image)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.imageView.setImageBitmap(resource);

                                if (resource.getHeight() > resource.getWidth()) {
                                    holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                } else {
                                    holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                holder.imageView.setImageDrawable(placeholder);
                            }
                        });
            } else {
                holder.imageView.setImageResource(R.drawable.ic_default_image);
            }
        }

        @Override
        public int getItemCount() {
            return problemasList.size();
        }
    }
}