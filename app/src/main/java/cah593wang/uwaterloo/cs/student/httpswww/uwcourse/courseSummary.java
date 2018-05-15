package cah593wang.uwaterloo.cs.student.httpswww.uwcourse;



public class courseSummary extends course {


    public static void main(String[] args) {

        course cour = new course ("MATH", 235, 1185);
        int secnum = 1;
        int courseMax= getSecCount("MATH", 235, 1185);
        System.out.println(courseMax);
        for (secnum = 1; secnum <= courseMax; secnum++) {
            System.out.println(cour.cour[secnum - 1].classNum);
            System.out.println(cour.cour[secnum - 1].lecNum);
            System.out.println(cour.cour[secnum - 1].campLoc);
            System.out.println(cour.cour[secnum - 1].enrol[0]);
            System.out.println(cour.cour[secnum - 1].enrol[1]);
            System.out.println(cour.cour[secnum - 1].times[0]);
            System.out.println(cour.cour[secnum - 1].times[1]);
            System.out.println(cour.cour[secnum - 1].times[2]);
            System.out.println(cour.cour[secnum - 1].times[3]);
            System.out.println(cour.cour[secnum - 1].times[4]);
            System.out.println(cour.cour[secnum - 1].times[5]);
            System.out.println(cour.cour[secnum - 1].times[6]);
            System.out.println(cour.cour[secnum - 1].room);
            System.out.println(cour.cour[secnum - 1].room2);
            System.out.println(cour.cour[secnum - 1].inst);
            System.out.println(cour.cour[secnum - 1].instQual);
            System.out.println(cour.cour[secnum - 1].wta);
            System.out.println(cour.cour[secnum - 1].lod);
            System.out.println(cour.cour[secnum - 1].hotness);
            System.out.println("");
        }

        /*


        courseSummary course = new courseSummary();
        course.showCourses();
        */
    }

}
