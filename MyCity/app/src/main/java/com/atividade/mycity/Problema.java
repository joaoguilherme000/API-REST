package com.atividade.mycity;

public class Problema {
    private final int id;
    private final String foto;
    private final String descricao;

    public Problema(int id, String foto, String descricao) {
        this.id = id;
        this.foto = foto;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getFoto() {
        return foto;
    }

    public String getDescricao() {
        return descricao;
    }
}