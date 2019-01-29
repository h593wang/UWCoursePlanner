package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;

public class section {

    private int classNum;
    private int lecNum;
    private String campLoc;
    private int enrollMax;
    private int enrollCur;
    private int [] times;
    private String room;
    private String room2;
    private String inst;
    private String instQual;
    private String wta;
    private String lod;

    public section(){}
    public section(int classNum, int lecNum, String campLoc, int enrollMax, int enrollCur, int [] times, String room, String room2, String inst, String instQual, String wta, String lod, boolean hotness) {
        this.classNum = classNum;
        this.lecNum = lecNum;
        this.campLoc = campLoc;
        this.enrollMax = enrollMax;
        this.enrollCur = enrollCur;
        this.times = times;
        this.room = room;
        this.room2 = room2;
        this.inst = inst;
        this.instQual = instQual;
        this.wta = wta;
        this.lod = lod;
        this.hotness = hotness;
    }

    public void setLecNum(int lecNum) {
        this.lecNum = lecNum;
    }

    public void setCampLoc(String campLoc) {
        this.campLoc = campLoc;
    }

    public void setEnrollMax(int enrollMax) {
        this.enrollMax = enrollMax;
    }

    public void setEnrollCur(int enrollCur) {
        this.enrollCur = enrollCur;
    }

    public void setTimes(int[] times) {
        this.times = times;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setRoom2(String room2) {
        this.room2 = room2;
    }

    public void setInst(String inst) {
        this.inst = inst;
    }

    public void setInstQual(String instQual) {
        this.instQual = instQual;
    }

    public void setWta(String wta) {
        this.wta = wta;
    }

    public void setLod(String lod) {
        this.lod = lod;
    }

    public void setHotness(boolean hotness) {
        this.hotness = hotness;
    }

    private boolean hotness;

    public int getLecNum() {
        return lecNum;
    }

    public String getCampLoc() {
        return campLoc;
    }

    public int getEnrollMax() {
        return enrollMax;
    }

    public int getEnrollCur() {
        return enrollCur;
    }

    public int[] getTimes() {
        return times;
    }

    public String getRoom() {
        return room;
    }

    public String getRoom2() {
        return room2;
    }

    public String getInst() {
        return inst;
    }

    public String getInstQual() {
        return instQual;
    }

    public String getWta() {
        return wta;
    }

    public String getLod() {
        return lod;
    }

    public boolean isHotness() {
        return hotness;
    }

    public int getClassNum() {
        return classNum;
    }

    public void setClassNum(int classNum) {
        this.classNum = classNum;
    }
}
