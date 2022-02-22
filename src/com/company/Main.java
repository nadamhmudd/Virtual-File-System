package com.company;

import com.company.FSM.Contiguous;
import com.company.FSM.Indexed;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        Scanner input= new Scanner(System.in);
        System.out.print("1-Contiguous Allocation\n2-Indexed Location\nEnter Number: ");
        int ch= input.nextInt();
        if(ch==1){
            System.out.print("Enter Size of Disk: ");
            Contiguous ctg= new Contiguous(input.nextInt());
            ctg.execCommands();
            System.out.println(ctg.Blocks);

        }else if(ch==2){
            Indexed indx= new Indexed();
            indx.loadFile("Indexed.vfs");
            indx.execCommands();
            System.out.println(indx.Blocks);
            indx.saveToFile("Indexed.vfs");
        }

    }
}
