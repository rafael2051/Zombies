package game.threads;


import game.apis.ApiZombieClient;
import game.logic.player.Player;
import game.logic.zombie.Zombie;
import game.logic.zombie.ZombieStandard;

import game.logic.window.Window;

import java.util.Random;

public class ZombieMoveTask implements Runnable{
    private Window window;
    private ZombieStandard zombieStandard;

    private ApiZombieClient apiZombieClient;
    private double currentTime;
    private double previousTime;
    private double spawnTime;


    public ZombieMoveTask(Window window, ZombieStandard zombieStandard){
        this.window = window;
        this.zombieStandard = zombieStandard;
        apiZombieClient = ApiZombieClient.getInstance();
        previousTime = System.currentTimeMillis();
        currentTime = 0;
        spawnTime = 2000;
    }

    @Override
    public void run() {
        while(true){
            currentTime = System.currentTimeMillis() - previousTime;
            for(Zombie zombie : window.zombies){
                if(zombie.isDead()){
                    continue;
                }
                if(zombie.getPosX() <= 0 && zombie.getMustRender()){
                    zombie.setMustRender(false);
                    window.decreasesHP();
                    continue;
                }
                for(Player player : window.players){
                    if((zombie.getPosX() >= 
                        (player.getPosX() + player.getWidth() - 60) &&
                        zombie.getPosX() <= 
                        (player.getPosX() + player.getWidth() - 40)) &&
                        (zombie.getPosY() >=
                        player.getPosY() - 20  &&
                        zombie.getPosY() <=
                        (player.getPosY() + player.getHeight()) - 40)){
                            zombie.attack(player.getId());
                            zombie.setAttacking(true);
                            player.gettingAttacked();
                    }
                    else if(zombie.getPlayerAttacked() == player.getId()){
                        zombie.setAttacking(false);
                        zombie.resetPlayerAttacked();
                    }
                }
                if(!zombie.getAttacking()){
                    zombie.move();
                    zombie.setAttacking(false);
                }
            }
            if(currentTime >= spawnTime && apiZombieClient.checkIfMustAddZombie()){
                previousTime = System.currentTimeMillis();
                currentTime = 0;
                int[] posZombie = apiZombieClient.getPosZombie();
                window.addZombie(new Zombie(posZombie[0], posZombie[1],
                                zombieStandard));
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
        }
    }   
}
