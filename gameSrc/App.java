import game.apis.ApiPlayerClient;
import game.logic.window.Window;
import game.threads.ControlOtherPlayersTask;
import game.threads.MoveLocalPlayerTask;
import game.threads.ControlShootTask;
import game.threads.ZombieMoveTask;
import game.logic.player.Bullet;
import game.logic.player.BulletStandard;
import game.logic.player.Player;
import game.logic.zombie.Zombie;
import game.logic.zombie.ZombieStandard;
import server.GameServer;
import client.GameClient;

import java.util.ArrayList;
import java.util.List;

import java.util.Random;

public class App {
    public static void main(String[] args) throws Exception {

        Random random = new Random();

        Window window = new Window(1000, 1000);
        ZombieStandard zombieStandard = new ZombieStandard(80, 80);

        BulletStandard bulletStandard = new BulletStandard(10, 2);
        Bullet bullet = new Bullet(0, 0, bulletStandard);

        GameClient gameClient = null;
        GameServer gameServer = null;

        Player localPlayer;

        while(true){

            if(window.getStatus() == 0){
                while(true){
                    window.changeMenuImage();
                    window.checkEnterPressed();
                    if(window.getStatus() == 1 ||
                        window.getGameExit() == 1){
                        Thread.sleep(1000);
                        break;
                    }
                    window.render();
                    Thread.sleep(40);
                }
            }

            if(window.getGameExit() == 1){
                System.exit(0);
            }


            else if(window.getStatus() == 1){
                gameClient = GameClient.getInstance();
                gameClient.start();
                window.setGameClient(gameClient);
                while(true){
                    window.checkEnterPressed();
                    window.checkStartGame();
                    if(window.getStatus() == 2){
                        Thread.sleep(1000);
                        break;
                    }
                    window.render();
                    Thread.sleep(40);
                }
            }

            else if (window.getStatus() == 2){
                gameServer = GameServer.getInstance();
                gameServer.start();
                String positions = ApiPlayerClient.getInstance().getPlayersPos();
                String[] playersPos = positions.split(";");
                int noPlayers = ApiPlayerClient.getInstance().getNoPlayers();
                ApiPlayerClient.getInstance().initMessage(noPlayers);
                window.initBulletsFromOtherPlayers(noPlayers);
                localPlayer = null;
                for(int i = 0 ; i < noPlayers;i++){
                    String[] parameters = playersPos[i].split("/");
                    int id = Integer.parseInt(parameters[0]);
                    int posX = Integer.parseInt(parameters[1]);
                    int posY = Integer.parseInt(parameters[2]);
                    if(parameters[3].equals("true")){
                        localPlayer = new Player(id, posX, posY, 80, 80);
                        window.addPlayer(localPlayer);
                    } else if(parameters[3].equals("false")){
                        window.addPlayer(new Player(id, posX, posY, 80, 80));
                    }
                }
                gameClient.setLocalPlayer(localPlayer);
                Runnable tarefaMove = new MoveLocalPlayerTask(window, localPlayer, bulletStandard);
                Thread threadMove = new Thread(tarefaMove);
                threadMove.start();
                for(Player player : window.players){
                    if(player.getId() != ApiPlayerClient.getInstance().getId()){
                        Runnable tarefaMoveOtherPlayers = new ControlOtherPlayersTask(window, bulletStandard, player);
                        Thread threadMoveOtherPlayers = new Thread(tarefaMoveOtherPlayers);
                        threadMoveOtherPlayers.start();
                    }
                }
                Runnable tarefaControlShoot = new ControlShootTask(window, localPlayer, bulletStandard);
                Thread threadControlShoot = new Thread(tarefaControlShoot);
                threadControlShoot.start();
                Runnable tarefaMoveZombie = new ZombieMoveTask(window, zombieStandard);
                Thread threadMoveZombie = new Thread(tarefaMoveZombie);
                threadMoveZombie.start();
                while(true){
                    if(window.getFortressHP() <= 0 || 
                        window.players.stream().allMatch((p) -> p.getHP() <= 0)){
                        window.finishGame();
                        window.clean();
                        break;
                    }
                    window.render();
                    Thread.sleep(40);
                }
            }

            else if(window.getStatus() == 3){
                window.render();
                Thread.sleep(40);
            }

        }
    }
}