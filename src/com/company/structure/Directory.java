package com.company.structure;

import java.util.LinkedList;

public class Directory {

    public String path, name;
    public LinkedList<Files> files= new LinkedList<>();
    public LinkedList<Directory> subDirectories= new LinkedList<>();

    public Directory(){
        this.path = "root/";
        this.name = "root";
    }
    public Directory(String path, String name){
        this.path = path;
        this.name = name;

    }
}
