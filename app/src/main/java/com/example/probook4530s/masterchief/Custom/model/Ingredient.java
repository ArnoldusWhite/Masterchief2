package com.example.probook4530s.masterchief.Custom.model;

public class Ingredient {
    private String name;
    private String qte;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQte() {
        return qte;
    }

    public void setQte(String qte) {
        this.qte = qte;
    }

    public Ingredient(String name, String qte) {
        this.name = name;
        this.qte = qte;
    }


}
