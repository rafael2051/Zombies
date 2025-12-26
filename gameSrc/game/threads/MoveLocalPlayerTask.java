package game.threads;

import client.GameClient;
import game.Keys;
import game.apis.ApiPlayerClient;
import game.logic.player.BulletStandard;

import game.logic.window.Window;

import game.logic.player.Player;

public class MoveLocalPlayerTask implements Runnable{

    private Window window;
    private Player localPlayer;

    public MoveLocalPlayerTask(Window window, Player localPlayer, BulletStandard bulletStandard){
        this.window = window;
        this.localPlayer = localPlayer;
    }
    @Override
    public void run() {
        while(true){
            String msg = "";
            msg += ApiPlayerClient.getInstance().getId() + ";";
            if(window.controleTecla[Keys.UP] && 
                localPlayer.getPosY() > 20){
                localPlayer.walkUp();
                msg += "walk;up;" + localPlayer.getPosX() +
                        ";" + localPlayer.getPosY() + ";";
                localPlayer.setWalking(true);
                localPlayer.updateNextImage();
            }
            else if(window.controleTecla[Keys.DOWN] &&
                localPlayer.getPosY() < window.getHeight() - 130){
                localPlayer.walkDown();
                msg += "walk;down;" + localPlayer.getPosX() +
                        ";" + localPlayer.getPosY() + ";";
                localPlayer.setWalking(true);
                localPlayer.updateNextImage();
            }
            else if(window.controleTecla[Keys.LEFT] &&
                localPlayer.getPosX() > 0){
                localPlayer.walkLeft();
                msg += "walk;left;" + localPlayer.getPosX() +
                        ";" + localPlayer.getPosY() + ";";
                localPlayer.setWalking(true);
                localPlayer.updateNextImage();
            }
            else if(window.controleTecla[Keys.RIGHT] &&
                localPlayer.getPosX() < window.getWidth() - 100){
                localPlayer.walkRight();
                msg += "walk;right;" + localPlayer.getPosX() +
                        ";" + localPlayer.getPosY() + ";";
                localPlayer.setWalking(true);
                localPlayer.updateNextImage();
            }
            else{
                msg += "donothing;;;;";
                localPlayer.setWalking(false);
            }
            try{
                GameClient.getInstance().sendMessage(msg);
            } catch(Exception e){
                e.printStackTrace();
            }
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }  
}
