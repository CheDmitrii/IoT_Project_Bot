package com.dmitrii.lockbot.Elements.History;

import java.time.LocalDate;
import java.time.LocalTime;

public class HistoryUnit{
    private LocalDate data;
    private LocalTime time;
    private int state;

    public HistoryUnit(LocalDate data, LocalTime time, int state) {
        this.data = data;
        this.time = time;
        this.state = state;
    }

    public static HistoryUnit getUnit(LocalDate data, LocalTime time, int state){
        return new HistoryUnit(LocalDate.now(), LocalTime.now(), state);
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
