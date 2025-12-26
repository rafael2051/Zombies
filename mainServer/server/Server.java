package server;

import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;

import threadSocket.ThreadClientSocket;
import control.ControlRound;

public class Server extends Thread{

    private static final Server server = new Server();
    private static ServerSocket serverSocket = null;
    private static List<InetAddress> listIp = new ArrayList<InetAddress>();
    private static List<ThreadClientSocket> listThreadClientSocket = new ArrayList<ThreadClientSocket>();
    private static ControlRound controlRound = ControlRound.getInstance();
    private static String msg;
    private static Thread zombieAdder;
    private static boolean gameRunning;

    private Server(){
        gameRunning = false;
    }

    public static Server getInstance(){
        return server;
    }

    public void run(){
        System.out.println("Server is running...");

        try {
            System.out.println("Binding to port 8080");
            serverSocket = new ServerSocket(8080);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.getMessage();
        }

        while(true){
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection initialized with " + clientSocket.getInetAddress()
                                    + ":" + clientSocket.getPort());
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                msg = input.readLine();
                if(msg.equals("InitialConnection")){
                    if(gameRunning){
                        output.println("You cannot play right now!");
                    } else{
                        output.println("Connection initialized");
                        listIp.add(clientSocket.getInetAddress());
                        ThreadClientSocket threadClient = new ThreadClientSocket(clientSocket, controlRound);
                        threadClient.start();
                        listThreadClientSocket.add(threadClient);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.getMessage();
            }
        }
    }

    public static void setGameRunning(boolean gameRunningValue){
        gameRunning = gameRunningValue;
    }

    public void tellEveryoneToStart(){
        gameRunning = true;
        int clientId = 0;
        int noPlayers = listThreadClientSocket.size();
        String randomPositions = generateRandomPositions(noPlayers);
        for(ThreadClientSocket client : listThreadClientSocket){
            client.tellToStart(clientId, randomPositions, noPlayers, listIp);
            clientId++;
        }
        initZombieAdder();
    }

    private String generateRandomPositions(int noPlayers){
        Random random = new Random();
        int length_y = 850 / noPlayers;
        int posX = 200;
        int posY;
        String randomPositions = "";
        for(int i = 0;i < noPlayers;i++){
            posY = random.nextInt(20 + (i * length_y), (20 + length_y) * (i + 1));
            randomPositions += "id:" + i + "-" + posX + "-" + posY + ";";
        }
        return randomPositions;
    }

    private void initZombieAdder(){
        zombieAdder = new ZombieAdder();
        zombieAdder.start();
    }

    public void removeIp(SocketAddress socketAdress){
        listIp.remove(socketAdress);
    }

    public void removeClient(ThreadClientSocket threadClientSocket){
        listThreadClientSocket.remove(threadClientSocket);
    }

    private class ZombieAdder extends Thread{
        private Random random = new Random();
        private int pos_x;
        private int pos_y;
        public void run(){
            while(true) {
                if(!gameRunning){
                    break;
                }
                pos_x = 1000;
                pos_y = random.nextInt(10, 900);
                for(ThreadClientSocket client : listThreadClientSocket){
                    client.tellToAddZombie(pos_x, pos_y);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
