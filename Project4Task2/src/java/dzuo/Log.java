/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dzuo;

/**
 *
 * @author zuodo
 */
public class Log {
    //This class is the data model of log
    //This is used to display log information in jsp

    String log1,log2,log3,log4,log5,log6,log7,log8;
    public Log(String []strs){
        log1=strs[0];
        log2=strs[1];
        log3=strs[2];
        log4=strs[3];
        log5=strs[4];
        log6=strs[5];
        log7=strs[6];
        log8=strs[7];
    }
    public String getLog1() {
        return log1;
    }

    public String getLog2() {
        return log2;
    }

    public String getLog3() {
        return log3;
    }

    public String getLog4() {
        return log4;
    }

    public String getLog5() {
        return log5;
    }

    public String getLog6() {
        return log6;
    }

    public String getLog7() {
        return log7;
    }

    public String getLog8() {
        return log8;
    }


}
