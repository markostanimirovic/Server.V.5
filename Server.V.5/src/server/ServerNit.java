/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @version V.5 - postavljena dodatna ogranicenja u komunikaciji radi otklanjanja eventualnih propusta protokola
 * @author Marko Stanimirovic
 */
public class ServerNit extends Thread {

    BufferedReader ulazniTokOdKlijenta = null;
    PrintStream izlazniTokKaKlijentu = null;
    Socket soketZaKomunikaciju = null;
    LinkedList<ServerNit> klijenti;
    LinkedList<ServerNit> prijateljiNasegKlijenta;
    LinkedList<String> imena;

    String ime;
    String pol;
    String tekst;

    DatagramSocket datagramSoket = null;
    InetAddress ip = null;
    byte[] podaciZaKlijenta = null;
    byte[] podaciZaKlijenta1 = null;
    byte[] podaciZaKlijenta2 = null;
    byte[] podaciZaKlijenta3 = null;
    int udpPort;

    public ServerNit(Socket soketZaKomunikaciju, LinkedList<ServerNit> klijenti,int udpPort) {
        this.soketZaKomunikaciju = soketZaKomunikaciju;
        this.klijenti = klijenti;
        this.udpPort = udpPort;
        
    }

    public void udp() {
        try {
            datagramSoket = new DatagramSocket();
            ip = InetAddress.getByName("localhost");
            podaciZaKlijenta = new byte[1024];
            podaciZaKlijenta1 = new byte[1024];
            podaciZaKlijenta2 = new byte[1024];
            podaciZaKlijenta3 = new byte[1024];
        } catch (SocketException ex) {

        } catch (UnknownHostException ex) {

        }
    }
    
    public String vratiDatumIVreme() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

    public String getPol() {
        return pol;
    }

    public String getIme() {
        return ime;
    }

    public void ulazKorisnikaUSobu() {
        try {
            ulazniTokOdKlijenta = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
            izlazniTokKaKlijentu = new PrintStream(soketZaKomunikaciju.getOutputStream());
            boolean kraj = false;
            izlazniTokKaKlijentu.println("*** Unesite ime: ***");
            while (kraj == false) {
                ime = ulazniTokOdKlijenta.readLine();
                if (!Server.imenaSvihKlijenata.contains(ime)) {
                    Server.imenaSvihKlijenata.add(ime);
                    kraj = true;
                } else {
                    izlazniTokKaKlijentu.println("*** Vec postoji korisnik sa imenom koje ste uneli! ***");
                    izlazniTokKaKlijentu.println("*** Unesite novo ime: ***");
                }
            }

            while (true) {
                izlazniTokKaKlijentu.println("*** Unesite pol: (muski - zenski) ***");
                pol = ulazniTokOdKlijenta.readLine().trim();
                if (pol.equalsIgnoreCase("muski")) {
                    izlazniTokKaKlijentu.println("*** Dobrodosao " + ime + ". ***");
                    break;
                } else if (pol.equalsIgnoreCase("zenski")) {
                    izlazniTokKaKlijentu.println("*** Dobrodosla " + ime + ". ***");
                    break;
                } else {
                    izlazniTokKaKlijentu.println("*** Doslo je do greske pri unosenju pola! Pokusajte ponovo. ***");
                }
            }

            for (ServerNit serverNit : klijenti) {
                if (serverNit != this && serverNit != null && serverNit.getIme() != null && serverNit.getPol() != null && (!serverNit.getPol().equalsIgnoreCase(pol))) {
                    if (pol.equalsIgnoreCase("muski")) {
                        serverNit.izlazniTokKaKlijentu.println("*** Novi korisnik: " + ime + " je usao u chat sobu. ***");
                    } else if (pol.equalsIgnoreCase("zenski")) {
                        serverNit.izlazniTokKaKlijentu.println("*** Nova korisnica: " + ime + " je usla u chat sobu. ***");
                    } else {

                    }
                }
            }
        } catch (IOException ex) {

        }

    }

    public void izlazIzChatSobe() {
        try {
            for (ServerNit serverNit : klijenti) {
                if (serverNit != null && serverNit.getIme() != null && serverNit.getPol() != null && serverNit != this && (!serverNit.getPol().equalsIgnoreCase(pol))) {
                    if (pol.equalsIgnoreCase("muski")) {
                        serverNit.izlazniTokKaKlijentu.println("*** Korisnik: " + ime + " je izasao iz chat sobe. ***");
                    } else {
                        serverNit.izlazniTokKaKlijentu.println("*** Korisnica: " + ime + " je izasla iz chat sobe. ***");
                    }
                }
            }
            izlazniTokKaKlijentu.println("*** " + ime + ", dovidjenja! ***");
            soketZaKomunikaciju.close();
            datagramSoket.close();
            Server.imenaSvihKlijenata.remove(ime);
            klijenti.remove(this);
            if (this.isAlive()) {
                this.interrupt();
            }
            return;
        } catch (IOException ex) {

        }
    }

    public boolean daLiJePraznaSoba() {
        int i = 0;

        if (klijenti.isEmpty()) {
            return true;
        }
        if (pol != null && pol.equalsIgnoreCase("muski")) {
            for (ServerNit klijent : klijenti) {
                if (klijent != null && klijent.getPol() != null && klijent.getIme() != null) {
                    if (klijent.getPol().equalsIgnoreCase("zenski")) {
                        i++;
                    }
                }
            }
        } else if (pol != null && pol.equalsIgnoreCase("zenski")) {
            for (ServerNit klijent : klijenti) {
                if (klijent != null && klijent.getPol() != null && klijent.getIme() != null) {
                    if (klijent.getPol().equalsIgnoreCase("muski")) {
                        i++;
                    }
                }
            }
        }
        if (i == 0) {
            return true;
        } else {
            return false;
        }
    }

//    public void posaljiListuKorisnika() {
//        try {
//            while (true) {
//
//                if (daLiJePraznaSoba()) {
//                    izlazniTokKaKlijentu.println("*** Trenutno nema online osoba za komunikaciju! ***");
//                    izlazniTokKaKlijentu.println("*** Ako zelite da ponovo proverite da li ima online osoba ukucajte: mmk ***");
//                    izlazniTokKaKlijentu.println("*** Ako zelite da izadjete iz sobe ukucajte: mst ***");
//                    tekst = ulazniTokOdKlijenta.readLine().toLowerCase().trim();
//                    if (tekst.equalsIgnoreCase("mmk")) {
//                        continue;
//                    } else {
//                        izlazIzChatSobe();
//                    }
//                } else {
//                    if (pol.equalsIgnoreCase("muski")) {
//                        izlazniTokKaKlijentu.print("*** Online korisnice: ");
//                    } else {
//                        izlazniTokKaKlijentu.print("*** Online korisnici: ");
//                    }
//                    for (ServerNit serverNit : klijenti) {
//                        if (serverNit != this && serverNit.getIme() != null && (!serverNit.getPol().equalsIgnoreCase(pol)) && (serverNit.getIme() != null)) {
//                            this.izlazniTokKaKlijentu.print(serverNit.getIme() + "; ");
//                        }
//                    }
//                    this.izlazniTokKaKlijentu.println("***");
//                    break;
//                }
//            }
//        } catch (IOException ex) {
//
//        }
//    }
    public void posaljiListuKorisnika() {
        try {
            while (true) {
                if (daLiJePraznaSoba()) {
                    podaciZaKlijenta1 = ("*** Trenutno nema online osoba za komunikaciju! ***\n").getBytes();
                    DatagramPacket paketZaKlijenta1 = new DatagramPacket(podaciZaKlijenta1, podaciZaKlijenta1.length, ip, udpPort);
                    datagramSoket.send(paketZaKlijenta1);
                    podaciZaKlijenta2 = ("*** Ako zelite da ponovo proverite da li ima online osoba ukucajte: mmk ***\n").getBytes();
                    DatagramPacket paketZaKlijenta2 = new DatagramPacket(podaciZaKlijenta2, podaciZaKlijenta2.length, ip, udpPort);
                    datagramSoket.send(paketZaKlijenta2);
                    podaciZaKlijenta3 = ("*** Ako zelite da izadjete iz sobe ukucajte: mst ***\n").getBytes();
                    DatagramPacket paketZaKlijenta3 = new DatagramPacket(podaciZaKlijenta3, podaciZaKlijenta3.length, ip, udpPort);
                    datagramSoket.send(paketZaKlijenta3);
                    tekst = ulazniTokOdKlijenta.readLine().toLowerCase().trim();
                    if (tekst.equalsIgnoreCase("mst")) {
                        izlazIzChatSobe();
                    } else {
                        continue;
                    }
                } else {
                    String s = "";
                    if (pol.equalsIgnoreCase("muski")) {
                        s = "*** Online korisnice: "; 
                    } else {
                        s = "*** Online korisnici: ";
                    }
                    for (ServerNit serverNit : klijenti) {
                        if (serverNit != this && serverNit != null && serverNit.getPol() != null && serverNit.getIme() != null && (!serverNit.getPol().equalsIgnoreCase(pol))) {
                            s = s + serverNit.getIme() + "; ";
                        }
                    }
                    s = s + "***\n";
                    podaciZaKlijenta = (s).getBytes();
                    DatagramPacket paketZaKlijenta = new DatagramPacket(podaciZaKlijenta, podaciZaKlijenta.length, ip, udpPort);
                    datagramSoket.send(paketZaKlijenta);
                    break;
                }
            }
        } catch (IOException ex) {

        }
    }

    public void nadjiKorisnikePoImenu() {
        prijateljiNasegKlijenta = new LinkedList<>();
        for (String osoba : imena) {
            for (ServerNit klijent : klijenti) {
                if (osoba != null && klijent != null && klijent.getIme() != null && klijent.getPol() != null && !(klijent.getPol().equals(pol))) {
                    if (osoba.equals(klijent.getIme())) {
                        prijateljiNasegKlijenta.add(klijent);
                    }
                }
            }
        }
    }

    public void biranjeKorisnika() {
        imena = new LinkedList<>();
        try {
            while (true) {
                izlazniTokKaKlijentu.println("*** Navedite ime osobe sa kojom zelite da komunicirate: ***");
                String novoIme = ulazniTokOdKlijenta.readLine().trim();
                if (!imena.contains(novoIme)) {
                    imena.add(novoIme);
                }
                izlazniTokKaKlijentu.println("*** Da li zelite da komunicirate sa jos osoba? (da - ne)");
                tekst = ulazniTokOdKlijenta.readLine().toLowerCase().trim();
                if (tekst.equalsIgnoreCase("ne")) {
                    break;
                }
            }

            nadjiKorisnikePoImenu();

            izlazniTokKaKlijentu.println("*** Chat moze da pocne! Za izlaz posaljite: mst, za pregled online osoba posaljite: mmk***");

        } catch (IOException ex) {

        }
    }

    public void komunikacija() {

        try {
            posaljiListuKorisnika();
            biranjeKorisnika();

            while (true) {
                tekst = ulazniTokOdKlijenta.readLine();
                if (tekst.toLowerCase().trim().startsWith("mst")) {
                    izlazIzChatSobe();
                    break;
                } else if (tekst.toLowerCase().trim().startsWith("mmk")) {
                    posaljiListuKorisnika();
                    biranjeKorisnika();
                } else {
                    try {
                        String onlinePrijatelji = "";
                        String offlinePrijatelji = "";
                        for (int i = 0; i < prijateljiNasegKlijenta.size(); i++) {
                            if (klijenti.contains(prijateljiNasegKlijenta.get(i))) {
                                prijateljiNasegKlijenta.get(i).izlazniTokKaKlijentu.println(ime + ": " + tekst);
                                onlinePrijatelji = onlinePrijatelji + prijateljiNasegKlijenta.get(i).getIme() + "; ";
                            } else {
                                offlinePrijatelji = offlinePrijatelji + prijateljiNasegKlijenta.get(i).getIme() + "; ";
                                prijateljiNasegKlijenta.remove(i);
                                i--;
                            }
                        }
                        Server.razgovor.upisiUFajl(vratiDatumIVreme());
                        Server.razgovor.upisiUFajl(ime + ": " + tekst);
                        if (!onlinePrijatelji.equalsIgnoreCase("")) {
                            this.izlazniTokKaKlijentu.println("*** Poruka je poslata uspesno osobama: " + onlinePrijatelji + " ***");
                            Server.razgovor.upisiUFajl("*** Poruka je poslata uspesno osobama: " + onlinePrijatelji + " ***");
                        }
                        if (!offlinePrijatelji.equalsIgnoreCase("")) {
                            this.izlazniTokKaKlijentu.println("*** Poruka nije poslata uspesno osobama: " + offlinePrijatelji + " ***");
                            Server.razgovor.upisiUFajl("*** Poruka nije poslata uspesno osobama: " + offlinePrijatelji + " ***");
                        }
                        if (prijateljiNasegKlijenta.isEmpty()) {
                            posaljiListuKorisnika();
                            biranjeKorisnika();
                        }
                    } catch (Exception e) {
                        this.izlazniTokKaKlijentu.println("*** Poruka nije poslata uspesno! ***");
                    }
                }
            }
        } catch (IOException ex) {

        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServerNit other = (ServerNit) obj;
        if (!Objects.equals(this.ime, other.ime)) {
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        udp();
        ulazKorisnikaUSobu();
        komunikacija();
    }

}
