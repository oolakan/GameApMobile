package com.example.mygame.mygame.model;

/**
 * Created by swifta on 10/23/17.
 */

public class Game {
    private String id;
    private String name;
    private String gameDay;
    private String gameQuaterName;
    private String gameQuaterId;
    private String startTime;
    private String stopTIme;
    private String drawTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTIme() {
        return stopTIme;
    }

    public void setStopTIme(String stopTIme) {
        this.stopTIme = stopTIme;
    }

    public String getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(String drawTime) {
        this.drawTime = drawTime;
    }

    public String getGameDay() {
        return gameDay;
    }

    public void setGameDay(String gameDay) {
        this.gameDay = gameDay;
    }

    public String getGameQuaterName() {
        return gameQuaterName;
    }

    public void setGameQuaterName(String gameQuaterName) {
        this.gameQuaterName = gameQuaterName;
    }

    public String getGameQuaterId() {
        return gameQuaterId;
    }

    public void setGameQuaterId(String gameQuaterId) {
        this.gameQuaterId = gameQuaterId;
    }
}




