/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @version V.5 - postavljena dodatna ogranicenja u komunikaciji radi otklanjanja eventualnih propusta protokola
 * @author Marko Stanimirovic
 */
public class Razgovor {
    
    private static final String RAZGOVOR = "C:\\Users\\PC Servis\\Documents\\NetBeansProjects\\fajlovi\\razgovor.txt";
    
    private static Razgovor razgovor = null;
    
    private Razgovor() {
        
    }
    
    public static Razgovor vratiInstancu() {
        if(razgovor == null) {
            razgovor = new Razgovor();
        }
        return razgovor;
    }
    
    public synchronized void upisiUFajl(String poruka) {
        LinkedList<String> tekst = vratiRedoveIzFajla();
        if(tekst == null) {
            tekst = new LinkedList<>();
        }
        tekst.add(poruka);
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(RAZGOVOR)));
            for(int i = 0; i < tekst.size(); i++) {
                out.println(tekst.get(i));
            }
            out.close();
        } catch (IOException ex) {
            
        }
    }
    
    private LinkedList<String> vratiRedoveIzFajla() {
        LinkedList<String> tekst = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(RAZGOVOR));
            boolean kraj = false;
            tekst = new LinkedList<>();
            while(!kraj) {
                String ucitanTekst = in.readLine();
                if(ucitanTekst == null) {
                    kraj = true;
                } else {
                    tekst.add(ucitanTekst);
                }
            }
            in.close();            
        } catch (IOException ex) {
            
        }
        return tekst;
    }
    
}
