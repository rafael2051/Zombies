package client;

import java.net.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ArrayList;

import game.apis.ApiPlayerClient;
import game.apis.ApiZombieClient;
import game.logic.player.Player;

public class GameClient extends Thread{

    private static final GameClient gameClient = new GameClient();
    private Socket clientSocket;
    private PrintWriter output;
    private BufferedReader input;
    private Player localPlayer;
    private boolean startGame;
    private boolean tellServerIsReady;
    private boolean mustClose;
    private List<String> listIp;
    private String message;
    private boolean thereIsMessage;

    private GameClient(){
        mustClose = false;
        listIp = new ArrayList<String>();
        tellServerIsReady = false;
        startGame = false;
        thereIsMessage = false;
    }

    public static GameClient getInstance(){
        return gameClient;
    }

    @Override
    public void run(){
        try{
            clientSocket = new Socket("localhost", 8080);
            this.output = new PrintWriter(clientSocket.getOutputStream(), true);
            this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output.println("InitialConnection");
            String msg = input.readLine();
            String[] answer = msg.split(";");
            System.out.println(answer[0]);
            ReceiveMessagesFromServer rcvServer = new ReceiveMessagesFromServer();
            rcvServer.start();
            while(true){
                if(mustClose){
                    output.println("FinalConnection");
                    clientSocket.close();
                    break;
                }
                if(tellServerIsReady){
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

    private class ReceiveMessagesFromServer extends Thread{

        ApiZombieClient apiZombieClient;
        ApiPlayerClient apiPlayerClient;
        public ReceiveMessagesFromServer(){
            apiZombieClient = ApiZombieClient.getInstance();
            apiPlayerClient = ApiPlayerClient.getInstance();
        }

        @Override
        public void run(){
            try{
                while(true){
                    String msg = input.readLine();
                    System.out.println(msg);
                    if(msg.contains("StartTheGame")){
                        String[] parameters = msg.split(";");
                        int localId = Integer.parseInt(parameters[1].split(":")[1]);
                        apiPlayerClient.setId(localId);
                        int noPlayers = Integer.parseInt(parameters[2].split(":")[1]);
                        System.out.println("localId: " + localId);
                        System.out.println("noPlayers: " + noPlayers);
                        int i;
                        for(i = 0; i < noPlayers;i++){
                            int playerId = Integer.parseInt(parameters[3+ i].split("-")[0].split(":")[1]);
                            int posX = Integer.parseInt(parameters[3 + i].split("-")[1]);
                            int posY = Integer.parseInt(parameters[3 + i].split("-")[2]);
                            System.out.println("id: " + playerId + " posX: " + posX + " posY: " + posY);
                            boolean isLocalPlayer = false;
                            if(playerId == localId){
                                isLocalPlayer = true;
                            }
                            apiPlayerClient.addPlayersPos(playerId, posX, posY, isLocalPlayer);
                        }
                        for(int j = i; j < noPlayers + i;j++){
                            listIp.add(parameters[3 + j].substring(1));
                        }
                        System.out.println(listIp);
                        apiPlayerClient.setNoPlayers(noPlayers);
                        new MessagesForPlayers().start();
                        startGame = true;
                    }
                    if(msg.contains("AddNewZombie")){
                        String[] positions = msg.split(";");
                        int pos_x = Integer.parseInt(positions[1]);
                        int pos_y = Integer.parseInt((positions[2]));
                        apiZombieClient.addZombiePosOnBuffer(pos_x, pos_y);
                    }
                    try{
                        Thread.sleep(2000);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            } catch(Exception e){
                System.out.println("Entrei");
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message){
        this.message = message;
        thereIsMessage = true;
    }
    private class MessagesForPlayers extends Thread{
        ApiPlayerClient apiPlayerClient;

        private MessagesForPlayers(){
            apiPlayerClient = ApiPlayerClient.getInstance();
        }
        @Override
        public void run(){
            try {
                PrintWriter newOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String msg;
            while(true){
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try{
                    if(thereIsMessage){
                        thereIsMessage = false;
                        int id = 0;
                        for(String ip : listIp){
                            try {
                                DatagramSocket udpSocket = new DatagramSocket();
                                byte[] data = message.getBytes();
                                SocketAddress socketAddress = new InetSocketAddress(ip, 9765 + id);
                                DatagramPacket packet = new DatagramPacket(
                                data, data.length, socketAddress);
                                try {
                                    if(id != apiPlayerClient.getId()) {
                                        udpSocket.send(packet);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (SocketException e) {
                                e.printStackTrace();
                            }
                            id++;
                        }
                    }
                } catch(ConcurrentModificationException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
