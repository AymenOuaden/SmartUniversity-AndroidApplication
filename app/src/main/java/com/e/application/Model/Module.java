package com.e.application.Model;

import java.io.Serializable;

public class Module implements Serializable {

    private String code_module = null;
    private String nom = null;

    public Module()
    {

    }

    public Module(String code_module, String nom)
    {
        this.code_module = code_module;
        this.nom = nom;
    }

    public String getCode_module()
    {
        return code_module;
    }

    public void setCode_module(String code_module)
    {
        this.code_module = code_module;
    }

    public String getNom()
    {
        return nom;
    }

    public void setNom(String nom)
    {
        this.nom = nom;
    }

    @Override
    public String toString()
    {
        return "Module [code_module=" + code_module + ", nom=" + nom + "]";
    }
}
