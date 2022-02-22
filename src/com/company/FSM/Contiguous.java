package com.company.FSM;

import com.company.structure.Directory;
import com.company.structure.Files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class Contiguous extends Manager {

    public Contiguous(int size){
        this.DISK_SIZE= size;
        this.freeBlocks= size;
        for(int i=0; i<size; i++)
            this.Blocks.add("0");
    }

    public int allocate(int size) {
        int cunt = 0, idx = -1,smallestSize=0,smallestIdx = -1;
        boolean flag=false;
        LinkedList<Integer> blocks = new LinkedList();

        if (size>this.freeBlocks) //index block+ block content(size)
            return -1;

        for (int i = 0; i < this.DISK_SIZE; i++) {//select index block
            if (this.Blocks.get(i).equals("0")) {
                if (flag == false) {
                    idx = i;
                    flag = true;
                }
                cunt++;
            } else {
                if (cunt <= size && cunt <= smallestSize) {
                    smallestIdx = idx;
                    smallestSize = cunt;
                }
                cunt = 0;
                flag = false;
            }
        }
        if(cunt <= size && cunt <= smallestSize){
            smallestIdx = idx;
            smallestSize = cunt;
        }
        this.allocateSpace(smallestIdx,(smallestIdx+size),size);
        return smallestIdx;

    }

    public void allocateSpace(int start,int end,int size) { //sub function
        if(start == -1)
            return;
        this.Blocks.set(start,"1");
    }

    public void deallocate(int start,int end,int size) {
        this.freeBlocks+=size;
        Blocks.set(start, "0");
    }

    @Override
    public void createFile(String p, int size) {
        String[] path = p.split("/");
        String name = path[path.length - 1]; //get folder name from tha path: last index

        Files newFile = new Files(p, name);
        newFile.size= size;
        Directory parentDir= checkPath(root, path, 1, path.length - 2);

        if(parentDir == null) {
            System.out.println("Path doesn't exist");
        } else { //path exists
            if (checkName(name, parentDir,"file") == -1) { //valid name
                int indexes = this.allocate(size);
                if (indexes == -1) {
                    System.out.println("No Available space");
                    return;
                } else {
                    System.out.println("File is created Successfully");
                    parentDir.files.add(newFile); //add File
                    this.freeBlocks-=size; //allocated one=> unavailable block
                    //store which indexes=> first element(index block) stored in the file
                    newFile.allocated.add(indexes, indexes+size);
                }
            } else {
                System.out.println("File already exists");

            }
        }
    }

    @Override
    public void deleteFile(String p) {
        String[] path = p.split("/");
        String name = path[path.length - 1]; //get folder name from tha path: last index

        Directory parentDir= checkPath(root, path, 1, path.length - 2);

        if(parentDir == null) {
            System.out.println("Path doesn't exist");
        } else { //path exists
            int idx=checkName(name, parentDir, "file");
            if (idx==-1)
                System.out.println("There's no file with this name ");
            else{
                Files fileTobeDeleted=parentDir.files.get(idx);
                LinkedList<Integer> free = fileTobeDeleted.allocated;
                parentDir.files.remove(idx);
                deallocate(free.getFirst(),free.get(1),fileTobeDeleted.size);
                System.out.println("File is Deleted Successfully");
            }
        }
    }

    @Override
    public void displayDiskStructure(Directory node, int D) {
        printSpace(D);
        System.out.println("-<"+node.name+">");

        if(node.subDirectories.isEmpty() && node.files.isEmpty())
            return;

        for(Files file: node.files) {
            printSpace(D+1);
            System.out.println("-"+ file.name+ " "+ file.index+ " "+ file.allocated);
        }

        for(Directory folder: node.subDirectories)
            this.displayDiskStructure(folder,D+1);
    }

    @Override
    protected void saveFile(Directory node, FileWriter fr) throws IOException {
        System.out.println("Invalid Arguments");
    }

    @Override
    public void loadFile(String fname) throws IOException {
        System.out.println("Invalid Arguments");
    }
}
