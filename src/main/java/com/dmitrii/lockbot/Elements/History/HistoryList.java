package com.dmitrii.lockbot.Elements.History;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/*
* 1 - state open in application
* 2 - state close in application
* 3 - state open by password
* 4 - state close by password*/
public class HistoryList {
    private static List<HistoryUnit> historyUnitList = new ArrayList<>();

    public static void addHistory(int state){
        historyUnitList.add(new HistoryUnit(LocalDate.now(), LocalTime.now().truncatedTo(ChronoUnit.SECONDS), state));
    }

    public static String getHistory(){
        if(historyUnitList.isEmpty()){
            return "history is empty\n";
        }
        String str = "";
        int j = 1;
        for (HistoryUnit i:historyUnitList){
            str += j + ". Lock was ";
            switch (i.getState()){
                case 1:
                    str += "open in application ";
                    break;
                case 2:
                    str += "closed in application ";
                    break;
                case 3:
                    str += "opened by passsword ";
                    break;
                case 4:
                    str += "closed by password ";
                    break;
                default:
                    str += "no information ";
                    break;
            }
            str += i.getData().format(DateTimeFormatter.ISO_DATE) + " ";
            str += "in " + i.getTime().format(DateTimeFormatter.ISO_TIME) + "\n";
            ++j;
        }
        return str;
    }
}
