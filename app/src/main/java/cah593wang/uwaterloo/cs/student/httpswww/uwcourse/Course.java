package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;


public abstract class Course {

    private String dep;
    private int courseCode;
    private int term;

    public abstract void onCourseReturned();

    private WebScrape webScrape = new WebScrape() {
        @Override
        public void onSecCountResult(int result) {
            if (result <= 0) return;
            count = result;
            initCourse();
        }
    };
    public section [] cour;
    public int count;

    public Course() {}

    public Course(String dep, int courseCode, int term) {
        this.dep = dep;
        this.courseCode = courseCode;
        this.term = term;
        webScrape.requestRawCount(dep, courseCode, term);
    }


    public void initCourse() {
        cour = new section[count];
        for (int i = 0; i < count; i++){
            cour[i] = new section();
            populateUW(i, webScrape.getRawDataUW(i + 1, dep, courseCode, term, count));

            //populateRMP(i, getRawDataRMP( cour[i].inst));

        }
        onCourseReturned();
    }


    void populateUW(int sec,  String data) {
        //getting class id
        data = skipToData(data);
        cour[sec].setClassNum(1000 * Character.getNumericValue(data.charAt(0)));
        data = data.substring(1);
        cour[sec].setClassNum(cour[sec].getClassNum()+ 100 * Character.getNumericValue(data.charAt(0)));
        data = data.substring(1);
        cour[sec].setClassNum(cour[sec].getClassNum()+ 10 * Character.getNumericValue(data.charAt(0)));
        data = data.substring(1);
        cour[sec].setClassNum(cour[sec].getClassNum()+ Character.getNumericValue(data.charAt(0)));
        data = data.substring(1);
        data = skipSec(data);
        //skipping to lec info
        data = skipSec(data);

        cour[sec].setLecNum ( sec + 1);

        //skipping to campus location
        data = skipToData(data);

        //saving campus location
        String campLoc = String.valueOf(data.charAt(0));
        data = data.substring(1);
        while (data.charAt(0) != '<') {
            campLoc += data.charAt(0);
            data = data.substring(1);
        }
        cour[sec].setCampLoc(campLoc);

        //skipping over bunch of unneeded bits (admin info)
        data = skipSec(data);
        data = skipSec(data);
        data = skipSec(data);
        data = skipSec(data);

        //skipping to class max size
        data = skipToData(data);

        //saving class size
        int enrollMax = 0;
        while(data.charAt(0)<='9' && data.charAt(0) >= '0'){
            enrollMax *= 10;
            enrollMax += Character.getNumericValue(data.charAt(0));
            data = data.substring(1);
        }
        cour[sec].setEnrollMax(enrollMax);

        //skipping to class current size
        data = skipToData(data);

        //getting current class size
        enrollMax = 0;
        while(data.charAt(0)<='9' && data.charAt(0) >= '0'){
            enrollMax *= 10;
            enrollMax += Character.getNumericValue(data.charAt(0));
            data = data.substring(1);
        }
        cour[sec].setEnrollCur(enrollMax);

        int [] times = new int[7];
        for (int i = 0; i < 7; i++) {
            times[i] = 0;
        }
        cour[sec].setTimes(times);

        //if its an online Course
        if (cour[sec].getCampLoc().contains("ONLINE")) {
            data = data.substring(115);
            data = skipToData(data);
            String instr = String.valueOf(data.charAt(0));
            data = data.substring(1);
            while (data.charAt(0) != '<') {
                instr += data.charAt(0);
                data = data.substring(1);
            }
            cour[sec].setInst(instr);
            cour[sec].setRoom("N/A");
            cour[sec].setRoom2("N/A");
            cour[sec].setLecNum(81);
            return;
        }
        //if it isnt online

        //skipping over more unneeded bits (wait list)
        data = data.substring(65);

        //skipping to time
        data = skipToData(data);

        if (data.charAt(0) == 'T') {
            cour[sec].setRoom("N/A");
            data = skipToData(data);
        } else {
            //getting time info
            int [] datesData = new int[7];
            datesData[0] = 1000 * Character.getNumericValue(data.charAt(0));
            datesData[0] += 100 * Character.getNumericValue(data.charAt(1));
            datesData[0] += 10 * Character.getNumericValue(data.charAt(3));
            datesData[0] += Character.getNumericValue(data.charAt(4));
            data = data.substring(6);
            datesData[1] = 1000 * Character.getNumericValue(data.charAt(0));
            datesData[1] += 100 * Character.getNumericValue(data.charAt(1));
            datesData[1] += 10 * Character.getNumericValue(data.charAt(3));
            datesData[1] += Character.getNumericValue(data.charAt(4));
            data = data.substring(5);

            if (data.charAt(0) == 'M') {
                datesData[2] = 1;
                data = data.substring(1);
            }
            if (data.charAt(0) == 'T' && data.charAt(1) != 'h') {
                datesData[3] = 1;
                data = data.substring(1);
            }
            if (data.charAt(0) == 'W') {
                datesData[4] = 1;
                data = data.substring(1);
            }
            if (data.charAt(0) == 'T' && data.charAt(1) == 'h') {
                datesData[5] = 1;
                data = data.substring(2);
            }
            if (data.charAt(0) == 'F') {
                datesData[6] = 1;
                data = data.substring(1);
            }
            cour[sec].setTimes(datesData);

            //skipping to campus location
            data = skipToData(data);

            //getting location
            String room = String.valueOf(data.charAt(0));
            data = data.substring(1);
            while (data.charAt(0) != '<') {
                room += data.charAt(0);
                data = data.substring(1);
            }
            cour[sec].setRoom(room);
        }

        if (data.charAt(8) != 'R') {
            //skipping to instructor name
            data = skipToData(data);


            //getting inst name
            String instr = String.valueOf(data.charAt(0));
            data = data.substring(1);
            while (data.charAt(0) != '<') {
                instr += data.charAt(0);
                data = data.substring(1);
            }
            cour[sec].setInst(instr);
        } else cour[sec].setInst("N/A");

        //getting second location
        if (!(sec < 9 && data.contains("LEC 00" + Integer.toString(sec + 2))) &&
                !(sec <99 && data.contains("LEC 0" + Integer.toString(sec + 2))) && !data.contains("TST")
                && data.contains(":") && !data.contains("Reserve") && !data.contains("TUT")) {
            System.out.println(data);
            while(data.charAt(2) != ':') {
                data = data.substring(1);
            }
            data = data.substring(11);
            int [] times2 = cour[sec].getTimes();
            if (data.charAt(0) == 'M') {
                times2[2] = 1;
                data = data.substring(1);
            }
            if (data.charAt(0) == 'T' && data.charAt(1) != 'h') {
                times2[3] = 1;
                data = data.substring(1);
            }
            if (data.charAt(0) == 'W') {
                times2[4] = 1;
                data = data.substring(1);
            }
            if (data.charAt(0) == 'T' && data.charAt(1) == 'h') {

                times2[5] = 1;
                data = data.substring(1);
            }
            if (data.charAt(0) == 'F') {
                times2[6] = 1;
            data = data.substring(1);
            }
            cour[sec].setTimes(times2);
            //skipping to campus location
            data = skipToData(data);

            //getting location
            String room2 = String.valueOf(data.charAt(0));
            data = data.substring(1);
            while (data.charAt(0) != '<') {
                room2 += data.charAt(0);
                data = data.substring(1);
            }
            cour[sec].setRoom2(room2);

        } else cour[sec].setRoom2("N/A");
    }

    String skipSec(String orig) {
        while(orig.charAt(0) != '/') {
            orig = orig.substring(1);
        }
        orig = orig.substring(1);
        return orig;
    }

    String skipToData(String orig) {
        while(!(orig.charAt(0) == '"' && orig.charAt(1) == '>')){
            orig = orig.substring(1);
        }
        orig = orig.substring(2);
        return orig;
    }

    /*
    void populateRMP (int sec, String data) {
        if (data.equals("ERROR")) {
            cour[sec].setinstQual = "N/A";
            cour[sec].setlod = "N/A";
            cour[sec].setwta = "N/A";
            cour[sec].sethotness = false;
            return;
        }
        while (data.charAt(1) != '.') {
            data = data.substring(1);
        }
        cour[sec].setinstQual = data.charAt(0) + "." + data.charAt(2);
        data = data.substring(3);
        if (data.contains("no-rating")) cour[sec].setwta = "N/A";
        else {
            while (!(data.charAt(2) == '"' && data.charAt(3) == '>')) {
                data = data.substring(1);
            }
            cour[sec].setwta = data.charAt(0) + "" + data.charAt(1) + "%";
        }

        while (data.charAt(1) != '.') {
            data = data.substring(1);
        }
        cour[sec].setlod = data.charAt(0) + "." + data.charAt(2);
        if (data.contains("hot")) cour[sec].sethotness = true;
        else cour[sec].sethotness=false;
    }
    */





}

