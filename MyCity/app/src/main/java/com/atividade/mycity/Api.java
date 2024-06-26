package com.atividade.mycity;

public class Api {

    private static final String ROOT_URL = "http://192.168.60.129/API-REST/Api/v1/Api.php?apicall=";

    public static final String URL_CREATE_USER = ROOT_URL + "createuser";
    public static final String URL_READ_USERS = ROOT_URL + "login";
    public static final String URL_UPDATE_USER = ROOT_URL + "updateuser";
    public static final String URL_DELETE_USER = ROOT_URL + "deleteusers&id=";
    public static final String URL_UPLOAD_PROBLEMA = ROOT_URL + "createproblema";
    public static final String URL_GET_PROBLEMA = ROOT_URL + "getProblemas";
    public static final String URL_GET_RESOLVIDO = ROOT_URL + "getResolvido";
}
