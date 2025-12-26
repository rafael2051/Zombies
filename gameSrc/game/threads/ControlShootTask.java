package game.threads;

import client.GameClient;
import game.apis.ApiPlayerClient;
import game.logic.player.Player;
import game.logic.player.Bullet;
import game.logic.zombie.Zombie;
import game.logic.player.BulletStandard;

import game.logic.window.Window;

import java.util.ConcurrentModificationException;

public class ControlShootTask implements Runnable{

    private Player localPlayer;
    private Window window;
    private BulletStandard bulletStandard;
    
    public ControlShootTask(Window window, Player localPlayer, BulletStandard bulletStandard){
        this.window = window;
        this.localPlayer = localPlayer;
        this.bulletStandard = bulletStandard;
    }

    @Override
    public void run(){
        boolean collision;
        while(true){
            try{
                if(window.controleTecla[4] &&
                localPlayer.getNextImageReloading() == - 1){
                    localPlayer.updateNextImageShooting();
                    localPlayer.setShooting(true);
                }
                else{
                    localPlayer.setShooting(false);
                }
                if(window.controleTecla[5] &&
                localPlayer.getNextImageReloading() == -1){

                    localPlayer.setMustReload(true);
                }
                for(int i = 0;i < 5;i++){
                    for(Bullet bullet : window.bullets){
                        collision = false;
                        for(Zombie zombie : window.zombies){
                            if(bullet.getPosX() >= zombie.getPosX() &&
                                bullet.getPosX() <= zombie.getPosX() + zombie.getHeight() &&
                                bullet.getPosY() >= zombie.getPosY() + 20  &&
                                bullet.getPosY() <= zombie.getPosY() + zombie.getHeight() - 20 &&
                                !zombie.isDead()){
                                    if(bullet.getMustRender()){
                                        zombie.gettingShooted();
                                    }
                                    bullet.setMustRender(false);
                                    if(zombie.getHP() <= 0){
                                        zombie.kill();
                                    }
                                    collision = true;
                                    break;
                                }
                        }
                        if(!collision){
                            bullet.move();
                        }
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String msg = "";
                msg += ApiPlayerClient.getInstance().getId() + ";";
                if(localPlayer.getShooting() &&
                        localPlayer.getAmmo() > 0
                        && localPlayer.getMustReload() == false){
                    msg += "shoot;;;;";
                    try {
                        GameClient.getInstance().sendMessage(msg);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    localPlayer.shoot();
                    window.addBullet(localPlayer.getPosX() + 74, localPlayer.getPosY() + 59,
                            bulletStandard);
                }
                else if (localPlayer.getMustReload() == true){
                    msg += "reload;;;;";
                    try {
                        GameClient.getInstance().sendMessage(msg);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                    localPlayer.setMustReload(false);
                    localPlayer.reload();
                    localPlayer.updateNextImageReloading();
                }
            } catch(ConcurrentModificationException concurrentException){
                continue;
            }
        }
    }
}
