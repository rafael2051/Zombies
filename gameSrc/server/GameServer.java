package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.sql.SQLOutput;

import game.apis.ApiPlayerClient;
import game.logic.window.Window;

public class GameServer extends Thread{
    private static final GameServer gameServer = new GameServer();
    private ApiPlayerClient apiPlayerClient;

    private String message;

    private GameServer(){
        apiPlayerClient = ApiPlayerClient.getInstance();
    }
    public static GameServer getInstance(){
        return gameServer;
    }

    @Override
    public void run(){
        DatagramSocket datagramSocket = null;
        try {
            System.out.println(9765 + apiPlayerClient.getId());
            datagramSocket = new DatagramSocket(9765 + apiPlayerClient.getId());
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while(true){
            byte[] data = new byte[30];
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
            try {
                datagramSocket.receive(datagramPacket);
                String msg = new String(datagramPacket.getData(), "UTF-8");
                apiPlayerClient.setMessageReceived(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
