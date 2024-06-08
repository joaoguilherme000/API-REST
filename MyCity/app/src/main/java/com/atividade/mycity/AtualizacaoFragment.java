package com.atividade.mycity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AtualizacaoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_atualizacao, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        List<String> data = new ArrayList<>();
        // Adicionando 5 itens iguais
        for (int i = 0; i < 5; i++) {
            data.add("descrição do que aconteceu " + (i + 1));
        }

        MyAdapter adapter = new MyAdapter(data);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private final List<String> mData;

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;
            public ImageView imageView;

            public MyViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.textView);
                imageView = v.findViewById(R.id.imageView);
            }
        }

        public MyAdapter(List<String> data) {
            mData = data;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.atualizacao_item, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.textView.setText(mData.get(position));
            // Defina a imagem aqui, se necessário
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}