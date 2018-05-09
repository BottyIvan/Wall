package com.botty.wall.model;

public class libs_model {

    private String nameLib;
    private String linkLib;
    private String iconLib;
    private String creator;

    public libs_model(){

    }

    public libs_model(String nameLib, String linkLib, String iconLib, String creator){
        this.nameLib = nameLib;
        this.linkLib = linkLib;
        this.iconLib = iconLib;
        this.creator = creator;
    }

    public void setNameLib(String nameLib) {
        this.nameLib = nameLib;
    }

    public String getNameLib() {
        return nameLib;
    }

    public void setLinkLib(String linkLib) {
        this.linkLib = linkLib;
    }

    public String getLinkLib() {
        return linkLib;
    }

    public void setIconLib(String iconLib) {
        this.iconLib = iconLib;
    }

    public String getIconLib() {
        return iconLib;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }
}
