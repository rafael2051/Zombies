package client;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketAddress;
import java.util.List;
import java.util.ArrayList;

import game.logica.player.Player;

public class GameClient extends Thread{

    private PrintWriter output;
    private BufferedReader input;
    private Player localPlayer;
    private boolean startGame;
    private boolean tellServerIsReady;
    private boolean mustClose;
    private List<String> listIp;

    public GameClient(){
        mustClose = false;
        listIp = new ArrayList<String>();
        tellServerIsReady = false;
        startGame = false;
    }

    @Override
    public void run(){
        try{
            Socket clientSocket = new Socket("localhost", 8080);
            this.output = new PrintWriter(clientSocket.getOutputStream(), true);
            this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output.println("InitialConnection");
            String msg = input.readLine();
            String[] answer = msg.split(";");
            System.out.println(answer[0]);
            for(String string : answer){
                if(string.equals("Connection initialized")){
                    continue;
                }
                listIp.add(string);
            }
            ReceiveMessages rcv = new ReceiveMessages();
            rcv.start();
            while(true){
                if(mustClose){
                    output.println("FinalConnection");
                    clientSocket.close();
                    break;
                }
                if(tellServerIsReady){
                    System.out.println("Entrei");
                    tellServerIsReady = false;
                    output.println("Ready");
                }
                try{
                    Thread.sleep(20);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setLocalPlayer(Player localPlayer){
        this.localPlayer = localPlayer;
    }

    public void closeClientSocket(boolean mustClose){
        this.mustClose = mustClose;
    }

    public void tellServerReady(){
        tellServerIsReady = true;
    }

    public boolean getStartGame(){
        return startGame;
    }

    private class ReceiveMessages extends Thread{

        @Override
        public void run(){
            try{
                while(true){
                    String msg = input.readLine();
                    System.out.println(msg);
                    if(msg.equals("StartTheGame")){
                        for(String string : listIp){
                            String[] adress = string.split(":");
                            String ip = adress[0].substring(1);
                            String port = adress[1];
                            System.out.println(ip);
                            System.out.println(port);
                        }
                        startGame = true;
                    }
                    try{
                        Thread.sleep(20);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }   
}
