package com.company.structure;

import java.util.LinkedList;

public class Files{

    public String filePath, name;
    public int index, size;
    public LinkedList<Integer> allocated= new LinkedList<>();

    public Files(String path, String name) {
        this.filePath = path;
        this.name = name;
    }
}
