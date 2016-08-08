package com.botty.wall.model;

import android.support.v7.widget.RecyclerView;

/**
 * Created by BottyIvan on 20/06/16.
 */
public class linkSocial {

    private String nomeSocial;
    private String linkSocial;
    private String url;

    public linkSocial(String nomeSocial,String linkSocial,String url){
        this.nomeSocial = nomeSocial;
        this.linkSocial = linkSocial;
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
}
