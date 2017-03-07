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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @version V.5 - postavljena dodatna ogranicenja u komunikaciji radi otklanjanja eventualnih propusta protokola
 * @author Marko Stanimirovic
 */
public class Port {

    private final String PORT = "C:\\Users\\PC Servis\\Documents\\NetBeansProjects\\fajlovi\\port.txt";

    public void upisiPortUFajl(int port) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(PORT)));
            out.println(port);
            out.close();
        } catch (IOException ex) {

        }
    }

    public int ucitajPortIzFajla() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(PORT));
            String s = in.readLine();
            int port = Integer.parseInt(s);
            in.close();
            return port;
        } catch (FileNotFoundException ex) {
            
        } catch (IOException ex) {
            
        }
        return -1;
    }
}
