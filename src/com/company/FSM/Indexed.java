package com.company.FSM;

import com.company.structure.Directory;
import com.company.structure.Files;

import javax.sound.sampled.Line;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Scanner;

public class Indexed extends Manager{

    public Indexed(){}

    public LinkedList allocate(int size) {
        int cunt = 0;
        LinkedList<Integer> blocks = new LinkedList();

        if (size+1>this.freeBlocks) //index block+ block content(size)
            return null;

        for (int i = 0; i < this.DISK_SIZE; i++) {//select index block
            if (this.Blocks.get(i).equals("0")) {
                blocks.addFirst(i); //index block always first index of list
                this.Blocks.set(i, "1");
                break;
            }
        }
        for (int i = 0; i < this.DISK_SIZE; i++) {//select content of the block
            if (this.Blocks.get(i).equals("0") && cunt < size) {
                this.Blocks.set(i, "1");
                blocks.add(i);
                cunt++;
            }
        }
        System.out.println(blocks);
        return blocks;
    }

    public void deallocate(int idx, LinkedList<Integer>blocks){
        Blocks.set(idx, "0");
        for (int i=0; i<blocks.size(); i++) {
            idx= blocks.get(i);
            Blocks.set(idx, "0");
        }
    }

    @Override
    public void createFile(String p, int size) {
        String[] path = p.split("/");
        String name = path[path.length - 1]; //get folder name from tha path: last index

        Files newFile = new Files(p, name);

        Directory parentDir= checkPath(root, path, 1, path.length - 2);

        if(parentDir == null) {
            System.out.println("Path doesn't exist");
        } else { //path exists
            if (checkName(name, parentDir,"file") == -1) { //valid name
                LinkedList indexes = this.allocate(size);
                System.out.println(indexes);
                if (indexes == null) {
                    System.out.println("No Available space");
                    return;
                } else {
                    parentDir.files.add(newFile); //add File
                    this.freeBlocks -= indexes.size(); //allocated one=> unavailable block
                    //store which indexes=> first element(index block) stored in the file
                    newFile.index= (int)indexes.get(0);
                    indexes.removeFirst();
                    newFile.allocated= indexes;
                    System.out.println("File is created Successfully");
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
                this.freeBlocks+= (fileTobeDeleted.allocated.size() + 1);
                this.deallocate(fileTobeDeleted.index, fileTobeDeleted.allocated);
                parentDir.files.remove(idx);
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
        fr.write("Path-"+ node.path+ '#');
        if(node.subDirectories.isEmpty() && node.files.isEmpty()){
            return;
        }
        else{
            for(Files file: node.files) { //Data-path-idx-allocated#
                fr.write("Data-"+ file.filePath+"-"+ String.valueOf(file.index));
                for(int i=0; i<file.allocated.size(); i++)
                    fr.write("-"+String.valueOf(file.allocated.get(i)));
                fr.write("#");
            }

            for(Directory folder: node.subDirectories)
                this.saveFile(folder,fr);
        }
    }

    @Override
    public void loadFile(String fname) throws IOException{ //Called at the start of the program
        try{
            File file= new File(fname);
            //not exist->return
            if(!(file.exists())){
                System.out.println("No file Exists");
                return;
            }
            //file exist
            Scanner fr= new Scanner(file);
            String lines= fr.nextLine();
            String[] Lines= lines.split("#");

            //Lines[0]= filename
            if(Lines.length >1) {
                for (int i = 0; i < Lines[1].length() ; i++) {
                    String cur= Character.toString(Lines[1].charAt(i));
                    this.Blocks.add(i, cur);
                }
                this.DISK_SIZE = Integer.parseInt(Lines[2]);
                this.freeBlocks = Integer.parseInt(Lines[3]);
                for (int i = 4; i < Lines.length; i++) {
                    String[] data = Lines[i].split("-");
                    if (data[0].equals("Path") && !data[1].equals("root/"))
                        this.createFolder(data[1]);
                    else if (data[0].equals("Data")) {
                        //store file
                        //get file name
                        String[] path = data[1].split("/");
                        String name = path[path.length - 1];
                        Files newFile = new Files(data[1], name);
                        newFile.index = Integer.parseInt(data[2]);
                        for (int j = 3; j < data.length; j++)
                            newFile.allocated.add(Integer.valueOf(data[j]));
                        //add to parent dir
                        Directory parentDir = checkPath(root, path, 1, path.length - 2);
                        parentDir.files.add(newFile);
                    }
                }
            }
            fr.close();
        }catch (IOException e) {
            System.out.println("error!");
        }
    }

}
