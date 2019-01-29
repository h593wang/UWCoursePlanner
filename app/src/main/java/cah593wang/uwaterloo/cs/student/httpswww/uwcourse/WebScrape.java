package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;


import android.os.AsyncTask;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

abstract class WebScrape {

    ArrayList<String> data = new ArrayList<>();
    public void requestRawCount(String dep, int courseCode, int term){

        try {
            URL course = new URL("http://www.adm.uwaterloo.ca/cgi-bin/cgiwrap/infocour/salook.pl?level=under&sess="+term+"&subject="+dep+"&cournum="+courseCode);
            new GetInfoTask().execute(course);
        }
        catch (Exception e) {
        }
    }

    public abstract void onSecCountResult(int result);

    private class GetInfoTask extends AsyncTask<URL, Void, Void> {
        protected Void doInBackground(URL... urls) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(urls[0].openStream()));
                String inputLine;
                int count = 1;
                while ((inputLine = in.readLine()) != null) {
                    data.add(inputLine);
                    if ( count < 10 && inputLine.contains("LEC 00"+ Integer.toString(count))) count++;
                    else if (count < 100 && inputLine.contains("LEC 0" + Integer.toString(count))) count++;
                    else if (inputLine.contains("LEC " + Integer.toString(count))) count++;
                    else if (inputLine.contains("LEC 081")) count++;
                }
                in.close();
                onSecCountResult(count-1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            onSecCountResult(-1);

            return null;
        }
    }

    String getRawDataUW(int lec, String dep,int courseCode,int term, int max) {
            int i = 0;
            for (;i < data.size();i++) {
                if ((data.get(i).contains("LEC 00" + Integer.toString(lec)) || data.get(1).contains("LEC 0" + Integer.toString(lec)))
                        || (lec == max && data.get(i).contains("LEC 081"))) {
                    break;
                }
            }
            String next = data.get(i+1);
            return data.get(i)+data.get(i+1);


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
