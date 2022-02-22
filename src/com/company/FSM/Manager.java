package com.company.FSM;

import com.company.structure.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;


public abstract class Manager{
    Scanner input= new Scanner(System.in);
    int DISK_SIZE, freeBlocks;
    public LinkedList<String> Blocks= new LinkedList<>();

    Directory root= new Directory();

    //---------------------------------------Abstract Function--------------------------------------------------------
    public abstract void createFile(String p, int size);
    public abstract void deleteFile(String p);
    public abstract void displayDiskStructure(Directory node, int D);

    protected abstract void saveFile(Directory node, FileWriter fr) throws IOException; //subFunction used in saveToFile()
    public abstract void loadFile(String fname) throws IOException;

    //----------------------------------------Same Implementation------------------------------------------------------
    public void createFolder(String P) { //p-> path
        String[] path= P.split("/");
        String name= path[path.length - 1]; //get folder name from tha path: last index

        Directory newFolder= new Directory(P, name);
        //i=1 to skip first index(root), n-> (read path till parent folder)-1-1(cause starting from zero)_
        Directory parentDir= checkPath(root, path, 1, path.length - 2);

        if(parentDir == null) {
            System.out.println("Path doesn't exist");
        } else { //path exists
            if(checkName(name, parentDir, "folder") == -1) { //valid name
                parentDir.subDirectories.add(newFolder); //add directory
                System.out.println("Directory is added Successfully");
            }else
                System.out.println("Directory already exists");
        }

    }

    public void deleteFolder(String P) {
        String[] path = P.split("/");
        String name = path[path.length - 1];
        if (name.equals("root")){
            System.out.println("Root can't be deleted");
            return;
        }
        Directory parentDir = checkPath(root, path, 1, path.length - 2);

        if(parentDir == null) {
            System.out.println("Path doesn't exist");
        } else { //path exists
            int idx = checkName(name, parentDir, "folder");
            if (idx == -1)
                System.out.println("There's no Directory with this name ");
            else{
                Directory dirTobeDeleted= parentDir.subDirectories.get(idx);
                this.delete(dirTobeDeleted);
                parentDir.subDirectories.remove(idx);
                System.out.println("Directory is deleted Successfully");

            }
        }
    }

    public void displayDiskStatus() {
        LinkedList allocated= new LinkedList(),
                   free = new LinkedList();

        System.out.println(this.Blocks);
        for(int i=0; i<this.Blocks.size(); i++){
            if(this.Blocks.get(i).equals("0"))
                free.add(i);
            else
                allocated.add(i);
        }
        System.out.println("Total Empty space: "+ this.freeBlocks);
        System.out.println("Total Allocated Space: "+ (this.DISK_SIZE-this.freeBlocks));

        System.out.println("\nEmpty Blocks: "+ free);
        System.out.println("Allocated Blocks: "+ allocated);
    }

    public void saveToFile(String fname) throws IOException{
        try{
            File file= new File(fname);
            //not exist->create new file
            if(!(file.exists())){
                System.out.println("create new file");
                Path filepath = file.toPath(); //convert File to Path
                Files.createFile(filepath);
            }
            //file exist
            FileWriter fr = new FileWriter(file, false); //true -> to append
            fr.write(fname+"#");
            String strBlocks="";
            for(int i=0; i<Blocks.size(); i++)
                strBlocks+=Blocks.get(i);
            fr.write(strBlocks + "#");
            fr.write(String.valueOf(this.DISK_SIZE) + "#");
            fr.write(String.valueOf(this.freeBlocks) + "#");

            saveFile(root, fr);

            fr.close();
        }catch (IOException e) {
            System.out.println("error!");
        }
    }

    public void execCommands(){
        while (true) {
            System.out.print("\n0-Exit\n1-Execute Commands\nEnter Your Request: ");
            int ch = input.nextInt();
            input.nextLine();
            if (ch == 0)
                break;
            else {
                System.out.println("Enter Commands");
                String rqst = input.nextLine();
                String[] cmd = rqst.split(" ");
                switch (cmd[0]) {
                    case "CreateFile":
                        if(cmd.length==3) {
                            this.createFile(cmd[1], Integer.parseInt(cmd[2]));
                        }else
                            System.out.println("Invalid Arguments");
                        break;
                    case "CreateFolder":
                        if(cmd.length==2) {
                            this.createFolder(cmd[1]);
                        }else
                            System.out.println("Invalid Arguments");
                        break;
                    case "DeleteFile":
                        if(cmd.length==2) {
                            this.deleteFile(cmd[1]);
                        }else
                            System.out.println("Invalid Arguments");
                        break;
                    case "DeleteFolder":
                        if(cmd.length==2) {
                            this.deleteFolder(cmd[1]);
                        }else
                            System.out.println("Invalid Arguments");
                        break;
                    case "DisplayDiskStatus":
                        this.displayDiskStatus();
                        break;
                    case "DisplayDiskStructure":
                        this.displayDiskStructure(root, 0);
                        break;
                    default:
                        System.out.println("Invalid Commands");
                        break;
                }
            }
        }
    }

    //-------------------------------------------SubFunctions----------------------------------------------------------
    public Directory checkPath(Directory node, String[] path, int i, int n){
        if(path.length== 2)
            return node;

        for(Directory dir: node.subDirectories){
            if(path[i].equals(dir.name)){
                if(i==n) //last folder exist
                    return dir;
                else
                    return checkPath(dir, path, i+1, n);
            }
        }

        return null;
    }

    public int checkName(String name, Directory dir, String key) {
        if(key.equals("file") && !dir.files.isEmpty()) {
            for (int i = 0; i < dir.files.size(); i++) {
                if (dir.files.get(i).name.equals(name))
                    return i; //any int
            }
        }
        else if(key.equals("folder") && !dir.subDirectories.isEmpty()){
            for (int i = 0; i < dir.subDirectories.size(); i++) {
                if (dir.subDirectories.get(i).name.equals(name))
                    return i; //any int
            }
        }
        return -1;
    }

    public void printSpace(int D){
        for(int i=0; i<D; i++) //to print spaces
            System.out.print(" ");
    }

    public void delete(Directory node) {
        int i=0, cunt=0, D=node.subDirectories.size();
        if(node.subDirectories.isEmpty() && node.files.isEmpty())
            return;

        for(com.company.structure.Files file: node.files)
            deleteFile(file.filePath);

        for(Directory folder: node.subDirectories)
            this.delete(folder);
    }
























}
