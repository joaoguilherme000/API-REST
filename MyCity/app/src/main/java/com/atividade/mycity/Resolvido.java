package com.atividade.mycity;

public class Resolvido {
    private int id;
    private int problema_id;
    private String fotoResolvida;
    private String descricaoResolvida;

    public void Problema(int id, int problema_id , String fotoResolvida, String descricaoResolvida) {
        this.id = id;
        this.problema_id = problema_id;
        this.fotoResolvida = fotoResolvida;
        this.descricaoResolvida = descricaoResolvida;
    }

    public Resolvido(int id, int problemaId, String fotoResolvida, String descricaoResolvida) {
        this.id = id;
        problema_id = problemaId;
        this.fotoResolvida = fotoResolvida;
        this.descricaoResolvida = descricaoResolvida;
    }

    public int getId() {
        return id;
    }

    public int getproblema_Id() {
        return problema_id;
    }

    public String getFotoResolvida() {
        return fotoResolvida;
    }

    public String getDescricaoResolvida() {
        return descricaoResolvida;
    }
}
