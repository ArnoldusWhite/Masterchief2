package com.example.probook4530s.masterchief.Custom.model;

public class Data {

    private String title;
    private String qte;
    private int image;

    public String getQte() {
        return qte;
    }

    public void setQte(String qte) {
        this.qte = qte;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Data(String title, int image,String qte) {
        this.title = title;
        this.image = image;
        this.qte=qte;
    }

    public Data(){

    }

    public Data(String label,String qte){
        this.title=label;
        this.qte=qte;
    }
}
