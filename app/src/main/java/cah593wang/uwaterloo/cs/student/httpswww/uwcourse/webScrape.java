package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;

import java.net.*;
import java.io.*;

class webScrape {

    static int getSecCount(String dep, int courseCode, int term){

        try {
            URL course = new URL("http://www.adm.uwaterloo.ca/cgi-bin/cgiwrap/infocour/salook.pl?level=under&sess="+term+"&subject="+dep+"&cournum="+courseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(course.openStream()));

            String inputLine;
            int count = 1;
            while ((inputLine = in.readLine()) != null) {
                if ( count < 10 && inputLine.contains("LEC 00"+ Integer.toString(count))) count++;
                else if (count < 100 && inputLine.contains("LEC 0" + Integer.toString(count))) count++;
                else if (inputLine.contains("LEC " + Integer.toString(count))) count++;
                else if (inputLine.contains("LEC 081")) count++;
            }
            in.close();
            return count - 1;
        }
        catch (Exception e) {
            return -1;
        }
    }

    static String getRawDataUW(int lec, String dep,int courseCode,int term, int max) {
        try {
            URL course = new URL("http://www.adm.uwaterloo.ca/cgi-bin/cgiwrap/infocour/salook.pl?level=under&sess="+term+"&subject="+dep+"&cournum="+courseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(course.openStream()));

            String inputLine = in.readLine();
            while ((!inputLine.contains("LEC 00"+ Integer.toString(lec)) && !inputLine.contains("LEC 0"+ Integer.toString(lec)))
                    && !(lec == max && inputLine.contains("LEC 081"))) {
                inputLine = in.readLine();
            }
            String next = in.readLine();
            in.close();
            return inputLine + next;

        }
        catch (Exception e) {
            return "ERROR";
        }

    }

    static String getRawDataRMP(String inst) {
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
}
