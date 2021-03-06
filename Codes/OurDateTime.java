package com.bizfit.bizfit;

import java.util.GregorianCalendar;

public class OurDateTime extends GregorianCalendar {
    OurDateTime(long time){
        super();
        this.setTimeInMillis(time);
    }

    public int getYear(){
        return this.get(YEAR);
    }
    public int getMonth(){
        return this.get(MONTH);
    }
    public int getDay(){
        return this.get(DAY_OF_MONTH);
    }

    public boolean isSameDate(OurDateTime date){
        return date.getDay()==this.getDay()&&date.getMonth()==this.getMonth()&&date.getYear()==this.getYear();
    }

}
