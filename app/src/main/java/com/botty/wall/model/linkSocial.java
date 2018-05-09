package com.botty.wall.model;

/**
 * Created by BottyIvan on 20/06/16.
 */
public class linkSocial {

    private String nomeSocial;
    private String linkSocial;
    private String url;
    private String urlLink;

    public linkSocial(String nomeSocial,String linkSocial,String url,String urlLink){
        this.nomeSocial = nomeSocial;
        this.linkSocial = linkSocial;
        this.urlLink = urlLink;
        this.url = url;
    }

    public void setNomeSocial(String nomeSocial){
        this.nomeSocial = nomeSocial;
    }

    public String getNomeSocial(){
        return this.nomeSocial;
    }

    public void setLinkSocial(String linkSocial){
        this.linkSocial = linkSocial;
    }

    public String getLinkSocial(){
        return this.linkSocial;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getUrl(){
        return this.url;
    }

    public void setUrlLink(String urlLink){this.urlLink  = urlLink;}

    public String getUrlLink() {
        return this.urlLink;
    }

}
