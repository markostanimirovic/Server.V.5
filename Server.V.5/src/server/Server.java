/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * @version V.5 - postavljena dodatna ogranicenja u komunikaciji radi otklanjanja eventualnih propusta protokola
 * @author Marko Stanimirovic
 */
public class Server {

    static LinkedList<ServerNit> klijenti = new LinkedList<>();
    static LinkedList<String> imenaSvihKlijenata = new LinkedList<>();
    static Port udpPort = new Port();
    static int udpP = 20000;
    static Razgovor razgovor = Razgovor.vratiInstancu();

    public static void main(String[] args) {
        int port = 10101;
        udpPort.upisiPortUFajl(udpP);
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        Socket klijentSoket = null;
        
        razgovor.upisiUFajl("*** RAZGOVOR ***");
        
        try {
            ServerSocket serverSoket = new ServerSocket(port);
            while (true) {
                klijentSoket = serverSoket.accept();
                klijenti.add(new ServerNit(klijentSoket, klijenti, udpP));
                klijenti.getLast().start();
                udpPort.upisiPortUFajl(udpP);
                udpP++;
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
