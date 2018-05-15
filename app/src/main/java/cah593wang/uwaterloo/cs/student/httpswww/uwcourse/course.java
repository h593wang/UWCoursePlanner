package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;

class section {

    int classNum;
    int lecNum;
    String campLoc;
    int [] enrol = new int [2];
    int [] times = new int  [7];
    String room;
    String room2;
    String inst;
    String instQual;
    String wta;
    String lod;
    boolean hotness;
}

public class course extends webScrape {


    section [] cour;

    public course() {}

    public course (String dep, int courseCode, int term) {
        int lecCount = getSecCount(dep, courseCode, term);
        this.cour = new section[lecCount];
        for (int i = 0; i < lecCount; i++){
            cour[i] = new section();
            populateUW(i, getRawDataUW(i + 1, dep, courseCode, term, lecCount));

            populateRMP(i, getRawDataRMP( cour[i].inst));
        }
    }

    void populateUW(int sec,  String data) {
        //getting class id
        data = skipToData(data);
        cour[sec].classNum = 1000 * Character.getNumericValue(data.charAt(0));
        data = data.substring(1);
        cour[sec].classNum += 100 * Character.getNumericValue(data.charAt(0));
        data = data.substring(1);
        cour[sec].classNum += 10 * Character.getNumericValue(data.charAt(0));
        data = data.substring(1);
        cour[sec].classNum += Character.getNumericValue(data.charAt(0));
        data = data.substring(1);
        data = skipSec(data);
        //skipping to lec info
        data = skipSec(data);

        cour[sec].lecNum = sec + 1;

        //skipping to campus location
        data = skipToData(data);

        //saving campus location
        cour[sec].campLoc = String.valueOf(data.charAt(0));
        data = data.substring(1);
        while (data.charAt(0) != '<') {
            cour[sec].campLoc += data.charAt(0);
            data = data.substring(1);
        }

        //skipping over bunch of unneeded bits (admin info)
        data = skipSec(data);
        data = skipSec(data);
        data = skipSec(data);
        data = skipSec(data);

        //skipping to class max size
        data = skipToData(data);

        //saving class size
        while(data.charAt(0)<='9' && data.charAt(0) >= '0'){
            cour[sec].enrol[0] *= 10;
            cour[sec].enrol[0] += Character.getNumericValue(data.charAt(0));
            data = data.substring(1);
        }

        //skipping to class current size
        data = skipToData(data);

        //getting current class size
        while(data.charAt(0)<='9' && data.charAt(0) >= '0'){
            cour[sec].enrol[1] *= 10;
            cour[sec].enrol[1] += Character.getNumericValue(data.charAt(0));
            data = data.substring(1);
        }

        for (int i = 0; i < 7; i++) {
            cour[sec].times[i] = 0;
        }

        //if its an online course
        if (cour[sec].campLoc.contains("ONLINE")) {
            data = data.substring(115);
            data = skipToData(data);
            cour[sec].inst = String.valueOf(data.charAt(0));
            data = data.substring(1);
            while (data.charAt(0) != '<') {
                cour[sec].inst += data.charAt(0);
                data = data.substring(1);
            }
            cour[sec].room = "N/A";
            cour[sec].room2 = "N/A";
            cour[sec].lecNum = 81;
            return;
        }
        //if it isnt online

        //skipping over more unneeded bits (wait list)
        data = data.substring(65);

        //skipping to time
        data = skipToData(data);

        if (data.charAt(0) == 'T') {
            cour[sec].times[0] = cour[sec].times[1] = cour[sec].times[2] = cour[sec].times[3] = cour[sec].times[4] = cour[sec].times[5] = cour[sec].times[6] = 0;
            cour[sec].room = "N/A";
            data = skipToData(data);
        } else {


            //getting time info
            cour[sec].times[0] = 1000 * Character.getNumericValue(data.charAt(0));
            cour[sec].times[0] += 100 * Character.getNumericValue(data.charAt(1));
            cour[sec].times[0] += 10 * Character.getNumericValue(data.charAt(3));
            cour[sec].times[0] += Character.getNumericValue(data.charAt(4));
            data = data.substring(6);
            cour[sec].times[1] = 1000 * Character.getNumericValue(data.charAt(0));
            cour[sec].times[1] += 100 * Character.getNumericValue(data.charAt(1));
            cour[sec].times[1] += 10 * Character.getNumericValue(data.charAt(3));
            cour[sec].times[1] += Character.getNumericValue(data.charAt(4));
            data = data.substring(5);

            if (data.charAt(0) == 'M') {
                cour[sec].times[2] = 1;
                data = data.substring(1);
            }
            if (data.charAt(0) == 'T' && data.charAt(1) != 'h') {
                cour[sec].times[3] = 1;
                data = data.substring(1);
            }
            if (data.charAt(0) == 'W') {
                cour[sec].times[4] = 1;
                data = data.substring(1);
            }
            if (data.charAt(0) == 'T' && data.charAt(1) == 'h') {
                cour[sec].times[5] = 1;
                data = data.substring(2);
            }
            if (data.charAt(0) == 'F') {
                cour[sec].times[6] = 1;
                data = data.substring(1);
            }

            //skipping to campus location
            data = skipToData(data);

            //getting location
            cour[sec].room = String.valueOf(data.charAt(0));
            data = data.substring(1);
            while (data.charAt(0) != '<') {
                cour[sec].room += data.charAt(0);
                data = data.substring(1);
            }
        }

        if (data.charAt(8) != 'R') {
            //skipping to instructor name
            data = skipToData(data);


            //getting inst name
            cour[sec].inst = String.valueOf(data.charAt(0));
            data = data.substring(1);
            while (data.charAt(0) != '<') {
                cour[sec].inst += data.charAt(0);
                data = data.substring(1);
            }
        } else cour[sec].inst = "N/A";

        //getting second location
        if (!(sec < 9 && data.contains("LEC 00" + Integer.toString(sec + 2))) &&
                !(sec <99 && data.contains("LEC 0" + Integer.toString(sec + 2))) && !data.contains("TST")
                && data.contains(":") && !data.contains("Reserve") && !data.contains("TUT")) {
            System.out.println(data);
            while(data.charAt(2) != ':') {
                data = data.substring(1);
            }
            data = data.substring(11);
            if (data.charAt(0) == 'M') {cour[sec].times[2] = 1; data = data.substring(1);}
            if (data.charAt(0) == 'T' && data.charAt(1) != 'h') {cour[sec].times[3] = 1; data = data.substring(1);}
            if (data.charAt(0) == 'W') {cour[sec].times[4] = 1; data = data.substring(1);}
            if (data.charAt(0) == 'T' && data.charAt(1) == 'h') {cour[sec].times[5] = 1; data = data.substring(2);}
            if (data.charAt(0) == 'F') {cour[sec].times[6] = 1; data = data.substring(1);}

            //skipping to campus location
            data = skipToData(data);

            //getting location
            cour[sec].room2 = String.valueOf(data.charAt(0));
            data = data.substring(1);
            while (data.charAt(0) != '<') {
                cour[sec].room2 += data.charAt(0);
                data = data.substring(1);
            }

        } else cour[sec].room2 = "N/A";
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

    void populateRMP (int sec, String data) {
        if (data.equals("ERROR")) {
            cour[sec].instQual = "N/A";
            cour[sec].lod = "N/A";
            cour[sec].wta = "N/A";
            cour[sec].hotness = false;
            return;
        }
        while (data.charAt(1) != '.') {
            data = data.substring(1);
        }
        cour[sec].instQual = data.charAt(0) + "." + data.charAt(2);
        data = data.substring(3);
        if (data.contains("no-rating")) cour[sec].wta = "N/A";
        else {
            while (!(data.charAt(2) == '"' && data.charAt(3) == '>')) {
                data = data.substring(1);
            }
            cour[sec].wta = data.charAt(0) + "" + data.charAt(1) + "%";
        }

        while (data.charAt(1) != '.') {
            data = data.substring(1);
        }
        cour[sec].lod = data.charAt(0) + "." + data.charAt(2);
        if (data.contains("hot")) cour[sec].hotness = true;
        else cour[sec].hotness=false;
    }





}

