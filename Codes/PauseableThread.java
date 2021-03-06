package com.bizfit.bizfit;


import com.bizfit.bizfit.activities.MainActivity;

import java.util.concurrent.TimeUnit;

public class PauseableThread extends Thread {
    boolean exit = false;
    long pauseEnds;
    long millis;
    User s;
    long messageCD= TimeUnit.HOURS.toMillis(12);

    public PauseableThread(long millis){
        this.millis=millis;

    }


    public void run () {
        while (true) {
            synchronized (this) {
                try {
                    while (System.currentTimeMillis()<pauseEnds)
                        wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (exit) return;
            s=User.getLastUser();
            if (s!=null) {
                Tracker[]t=s.getTrackers();
                for(Tracker tracker:t){
                    tracker.update();
                    //MainActivity.currentUser.getTrackers().get(i).testUpdate(1000);
                }
                if(System.currentTimeMillis()>=MainActivity.lastOpen+messageCD&&System.currentTimeMillis()>=MainActivity.lastMessageTime+messageCD){
                    MainActivity.lastMessageTime=System.currentTimeMillis();
                    NotificationSender.sendNotification("hello "+s.userName,"Remember to update your trackers!");
                }
            }

            pause(millis);
        }
    }



    public void pause(long pauseTime){
        pauseEnds=System.currentTimeMillis()+pauseTime;
    }





    /** Stops this thread */
    public void stopThread () {
        exit = true;
    }
}

