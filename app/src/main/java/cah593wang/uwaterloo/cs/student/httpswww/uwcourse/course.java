package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

class section {

    public int classNum;
    public int lecNum;
    public String campLoc;
    public int [] enrol = new int [2];
    public int [] times = new int  [7];
    public String room;
    public String room2;
    public String inst;
    public String instQual;
    public String wta;
    public String lod;
    public boolean hotness;
}

public class course  {


    public section [] cour;
    public int count;

    public course() {}

    public course (String info) {
        this.count = getSecCount(info);
        this.cour = new section[count];
        for (int i = 0; i < count; i++){
            cour[i] = new section();
            populateUW(i + 1, info);

            //populateRMP(i, getRawDataRMP( cour[i].inst));
        }
    }

    void populateUW(int sec,  String data) {

        if (sec < 10) {
            if (!(data.charAt(0) == 'L' && data.charAt(1) == 'E' && data.charAt(2) == 'C' && data.charAt(5) == '0' && Character.getNumericValue(data.charAt(6)) == sec)) {
                data = data.substring(1);
            }
        } else {
            if (!(data.charAt(0) == 'L' && data.charAt(1) == 'E' && data.charAt(2) == 'C' && Character.getNumericValue(data.charAt(5)) == sec/10 && Character.getNumericValue(data.charAt(6)) == sec%10)) {
                data = data.substring(1);
            }
        }

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

    /*void populateRMP (int sec, String data) {
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
*/

    int getSecCount(String info){

        int count = 1;
        while (info.contains("LEC 00"+count) || info.contains("LEC 0"+count)) {
            count++;
        }
        return count - 1;
    }



    /*static String getRawDataRMP(String inst) {
        try {
            String [] parts = inst.split(",");
            parts[1] = parts[1].split(" ")[0];
            //System.out.println(parts[1] + " " + parts[0]);
            URL course = new URL("http://www.ratemyprofessors.com/search.jsp?query=" + parts[1] + "+" + parts[0] + "+waterloo");
            BufferedReader in = new BufferedReader(new InputStreamReader(course.openStream()));

            String inputLine = in.readLine();

            while ((!inputLine.contains("<!-- Starts One professor Listing -->"))) {
                inputLine = in.readLine();

            }
            while ((!inputLine.contains("tid"))) {
                inputLine = in.readLine();
            }


            while (!(inputLine.charAt(0) == 't' && inputLine.charAt(1) == 'i' && inputLine.charAt(2) == 'd' && inputLine.charAt(3) == '=')) {
                inputLine = inputLine.substring(1);
            }
            inputLine = inputLine.substring(4);
            int pid = 0;
            while (inputLine.charAt(0) != '"') {
                pid *= 10;
                pid += Character.getNumericValue(inputLine.charAt(0));
                inputLine = inputLine.substring(1);
            }
            in.close();

            URL rmpPage = new URL("http://www.ratemyprofessors.com/ShowRatings.jsp?tid=" + pid);
            BufferedReader reader = new BufferedReader(new InputStreamReader(rmpPage.openStream()));

            inputLine = reader.readLine();

            while ((!inputLine.contains("Overall Quality"))) {
                inputLine = reader.readLine();
            }
            inputLine = reader.readLine();
            String result = inputLine;

            while ((!inputLine.contains("takeAgain"))) {
                inputLine = reader.readLine();
            }
            result += inputLine;

            while ((!inputLine.contains("Level of Difficulty"))) {
                inputLine = reader.readLine();
            }
            while ((!inputLine.contains("."))) {
                inputLine = reader.readLine();
            }
            result += inputLine;

            while ((!inputLine.contains(".png"))) {
                inputLine = reader.readLine();
            }
            result += inputLine;
            reader.close();
            //System.out.println(result);
            return result ;
        }
        catch (Exception e) {
            return "ERROR";
        }

    }
    */
}





