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

public class ResolvidosFragment extends Fragment {
    private static final String URL_GET_RESOLVIDO = Api.URL_GET_RESOLVIDO;

    private List<Resolvido> resolvidosList;
    private ResolvidosAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resolvidos, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        resolvidosList = new ArrayList<>();
        adapter = new ResolvidosAdapter(resolvidosList);
        recyclerView.setAdapter(adapter);

        // Método para buscar resolvidos da API
        fetchResolvidos();

        return view;
    }

    private void fetchResolvidos() {
        @SuppressLint("NotifyDataSetChanged") StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GET_RESOLVIDO,
                response -> {
                    try {
                        Log.d("API Response", "Response: " + response); // Log para verificar a resposta da API

                        JSONObject jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            JSONArray resolvidosArray = jsonObject.getJSONArray("resolvidos");
                            for (int i = 0; i < resolvidosArray.length(); i++) {
                                JSONObject resolvidoObject = resolvidosArray.getJSONObject(i);
                                int id = resolvidoObject.getInt("id");
                                int problemaId = resolvidoObject.getInt("problema_id");
                                String fotoResolvida = resolvidoObject.getString("fotoResolvida");
                                Log.d("URL da Imagem", "URL: " + fotoResolvida);
                                String descricaoResolvida = resolvidoObject.getString("descricaoResolvida");

                                Resolvido resolvido = new Resolvido(id, problemaId, fotoResolvida, descricaoResolvida);
                                resolvidosList.add(resolvido);
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

    private static class ResolvidosAdapter extends RecyclerView.Adapter<ResolvidosAdapter.ViewHolder> {

        private final List<Resolvido> resolvidosList;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView textView;
            public final ImageView imageView;

            public ViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.textView);
                imageView = v.findViewById(R.id.imageView);
            }
        }

        public ResolvidosAdapter(List<Resolvido> resolvidosList) {
            this.resolvidosList = resolvidosList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.resolvidos_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Resolvido resolvido = resolvidosList.get(position);
            holder.textView.setText(resolvido.getDescricaoResolvida());

            // Carregar imagem usando Glide
            if (!resolvido.getFotoResolvida().isEmpty()) {
                Log.d("URL da Imagem", "URL: " + resolvido.getFotoResolvida()); // Log para verificar a URL da imagem
                Glide.with(holder.imageView.getContext())
                        .asBitmap()
                        .load(resolvido.getFotoResolvida())
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
            return resolvidosList.size();
        }
    }
}