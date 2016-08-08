package com.botty.wall.model;

import java.io.Serializable;

/**
 * Created by BottyIvan on 08/08/16.
 */
public class ImageLocal implements Serializable {
    private String pathImg;

    public ImageLocal(String pathImg) {
        this.pathImg = pathImg;
    }

    public String getPathImg(){
        return this.pathImg;
    }

    void setPathImg(String  pathImg){
        this.pathImg = pathImg;
    }
}
