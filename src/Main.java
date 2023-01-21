import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import exceptions.EnrollmentExceedingException;
import Classes.Course;
import Classes.Student;
import exceptions.InvalidCourseIdSelection;
import exceptions.InvalidStudentIdSelectionException;
import exceptions.UnenrollmentInAnyCourseException;
import org.json.*;
import ReadWrite.readwrite;





public class Main extends readwrite{
    public static void main(String[] args) throws IOException,StringIndexOutOfBoundsException {



        String studentdatapath="src/files/studentinfo.text";
        String studentcsvpath="src/files/studentdata.csv";
        String coursedatapath="src/files/coursedata.xml";
        String coursecsvpath="src/files/coursedata.csv";
        String studentcoursesdetails="src/files/Student course details.json";
        String studentrawdata=readfrompath(studentdatapath);
        int studentnumber=studentcount(studentrawdata);
        String[] studentcsv=formatstudenttocsv(studentrawdata,studentnumber);
        writetocsvfile(studentcsv,studentcsvpath);
        String courserawdata=readfrompath(coursedatapath);
        int coursenumber=coursecount(courserawdata);
        String[] coursecsv=formatcoursetocsv(courserawdata,coursenumber);
        writetocsvfile(coursecsv,coursecsvpath);
        Course[] courses=new Course[coursenumber];
        Student[] students=new Student[studentnumber];
        makeStudents(studentcsv,students);
        makeCourses(coursecsv,courses);
        String studentcoursesraw=readfrompath(studentcoursesdetails);
        JSONObject json = new JSONObject(studentcoursesraw);
        int studentid=0;
        int courseid=0;
        Scanner userinput= new Scanner(System.in);
        char choice = 0;
        String k="";




       homepage(students,courses,userinput,json,studentid,courseid,choice,k);

















































    }

    private static void studentdetailspage(JSONObject json, int studentid,Course[] courses,Student[] students) {
        System.out.println("====================================================================================");
        System.out.println("Student Details page");
        System.out.println("====================================================================================");
        System.out.println("Name: "+students[studentid-1].name+"         Grade:"+students[studentid-1].grade+"                Email:"+students[studentid-1].email);
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Enrolled courses.");
        if(json.has(String.valueOf(studentid))){
            JSONArray enrolled=json.getJSONArray(String.valueOf(studentid));
            for (int g=0;g< enrolled.length();g++){
                int m= (int) enrolled.get(g)-1;
                System.out.println(g+1+"- "+courses[m].id+",      "+courses[m].name+",    "+courses[m].instructor+",    "+courses[m].duration+",     "+courses[m].time+",      "+courses[m].location);
            }
            System.out.println("------------------------------------------------------------------------------------");
        }else {
            System.out.println("This student hasn't enrolled in any courses");
            System.out.println("--------------------------------------------------------------------------------");
        }
    }

    private static void listallcourses(Course[] courses) {
        System.out.println("id,     Course Name,         Instructor,        Course duration,        Course time,        Location");
        System.out.println("----------------------------------------------------------------------------------------------------");
        for(int i=0;i<courses.length;i++){
            System.out.println(courses[i].id+",  "+courses[i].name+",  "+courses[i].instructor+",  "+courses[i].duration+",  "+courses[i].time+",  "+courses[i].location);
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    private static void replacementpage(Student[] students, Course[] courses, Scanner userinput, JSONObject json, int studentid, int courseid, char choice, String k) throws IOException,StringIndexOutOfBoundsException {
        if (json.has(String.valueOf(studentid))){
            JSONArray enrolled=json.getJSONArray(String.valueOf(studentid));
            System.out.println("Please enter the course id to be replaced:");
            int replaced= userinput.nextInt();
            if(enrolled.toString().contains("["+replaced+",")||enrolled.toString().contains(","+replaced+"]")||enrolled.toString().contains(","+replaced+",")||enrolled.toString().contains("["+replaced+"]")){

                System.out.println("Available courses");
                System.out.println("====================================================================================================");
                listallcourses(courses);
                System.out.println("Please enter the required course id to replace: ");
                courseid=userinput.nextInt();
                if(courseid==replaced){
                    System.out.println("Failed to replace: Student is already enrolled in that course");
                    repeatunenroll(students,courses,userinput,json,studentid,courseid,choice,k);
                }else if(courseid>=1&courseid<=courses.length){
                    for(int f=0;f<enrolled.length();f++){
                        if(enrolled.getInt(f)==replaced){
                            enrolled.remove(f);
                        }
                    }
                    json.put(String.valueOf(studentid),enrolled);
                    writetojson(json.toString());
                    json.append(String.valueOf(studentid),courseid);
                    writetojson(json.toString());
                    System.out.println("Courses replaced successfully from the "+courses[replaced-1].name+"course to "+courses[courseid-1].name+"course");
                    studentdetailspage(json,studentid,courses,students);
                    repeatunenroll(students,courses,userinput,json,studentid,courseid,choice,k);

                }else{
                    try{throw new InvalidCourseIdSelection();}catch (InvalidCourseIdSelection e){
                        System.out.println(e.getMessage());
                    }
                    repeatunenroll(students,courses,userinput,json,studentid,courseid,choice,k);
                }


            }else{
                try{throw new InvalidCourseIdSelection();}catch (InvalidCourseIdSelection e){
                    System.out.println(e.getMessage());
                }
                repeatunenroll(students,courses,userinput,json,studentid,courseid,choice,k);
            }

        }else{
            System.out.println("Faild to replace as the student hasn't enrolled in any course yet");
            repeatunenroll(students,courses,userinput,json,studentid,courseid,choice,k);
        }


    }

    private static void unenrollmentpage(Student[] students, Course[] courses, Scanner userinput, JSONObject json, int studentid, int courseid, char choice, String k) throws IOException ,StringIndexOutOfBoundsException{
        if (json.has(String.valueOf(studentid))){
            JSONArray enrolled=json.getJSONArray(String.valueOf(studentid));
            if (enrolled.length()==1){
                System.out.println("Student is enrolled in only one course.");
                repeatunenroll(students,courses,userinput,json,studentid,courseid,choice,k);

            }else{
                System.out.println("Please enter course id:");
                courseid=userinput.nextInt();
                for (int i=0;i<enrolled.length();i++){
                    if(enrolled.toString().contains("["+courseid+",")||enrolled.toString().contains(","+courseid+"]")||enrolled.toString().contains(","+courseid+",")||enrolled.toString().contains("["+courseid+"]")){
                        for(int f=0;f<enrolled.length();f++){
                            if(enrolled.getInt(f)==courseid){
                                enrolled.remove(f);
                            }
                        }
                        json.put(String.valueOf(studentid),enrolled);
                        writetojson(json.toString());
                        System.out.println("Unenrolled successfully from the "+courses[courseid-1].name+" course");
                        studentdetailspage(json,studentid,courses,students);
                        repeatunenroll(students,courses,userinput,json,studentid,courseid,choice,k);
                    }else if(courseid<=courses.length&courseid>=1){
                        System.out.println("Faild to unenroll: Student is not enrolled in that course");
                        repeatunenroll(students,courses,userinput,json,studentid,courseid,choice,k);
                    }
                    else{
                        try{throw new InvalidCourseIdSelection();}catch (InvalidCourseIdSelection e){
                            System.out.println(e.getMessage());
                        }
                        repeatunenroll(students,courses,userinput,json,studentid,courseid,choice,k);
                    }
                }
            }

        }else{
            try{throw new UnenrollmentInAnyCourseException();}catch (UnenrollmentInAnyCourseException e){
                System.out.println(e.getMessage());
            }
            repeatunenroll(students,courses,userinput,json,studentid,courseid,choice,k);

        }
    }


    private static void repeatunenroll(Student[] students, Course[] courses, Scanner userinput, JSONObject json, int studentid, int courseid, char choice, String k) throws IOException,StringIndexOutOfBoundsException {
        System.out.println("Please choose from the following:");
        System.out.println("a - Enroll in a course");
        System.out.println("d - Unenrollfrom an existing course");
        System.out.println("r - Replacing an existing course");
        System.out.println("b - Back to the main page");
        System.out.println("please select the required action:");
        k=userinput.next();
        choice=k.charAt(0);
        while(k.length()>1&choice!='a'&choice!='b'&choice!='r'&choice!='d'){
            System.out.println("Invalid choice");
             System.out.println("Please choose from the following:");
             System.out.println("a - Enroll in a course");
             System.out.println("d - Unenrollfrom an existing course");
             System.out.println("r - Replacing an existing course");
             System.out.println("b - Back to the main page");
             System.out.println("please select the required action:");
             k=userinput.next();
            choice=k.charAt(0);
        }
        choicemain(students,courses,userinput,json,studentid,courseid,choice,k);
    }


    private static void enrollmetpage(Student[] students, Course[] courses, Scanner userinput, JSONObject json, int studentid,int courseid, char choice, String k) throws IOException,StringIndexOutOfBoundsException {
        System.out.println("Enrollment page");
        System.out.println("====================================================================================================");
        listallcourses(courses);
        enrollrepeat(students,courses,userinput,json,studentid,courseid,choice,k);
        while(choice!='b'){
            enrollrepeat(students,courses,userinput,json,studentid,courseid,choice,k);
        }


    }

    private static void enrollrepeat(Student[] students, Course[] courses, Scanner userinput, JSONObject json, int studentid, int courseid, char choice, String k) throws IOException,StringIndexOutOfBoundsException {
        System.out.println("Please make one of the following:");
        System.out.println("Enter the course id that you want to enroll the student to");
        System.out.println("Enter b to go back to the home page");
        System.out.print("Please select the required action:");
        k= userinput.next();
        if (k.charAt(0)=='b'){
            homepage(students,courses,userinput,json,studentid,courseid,choice,k);
        }else{
            courseid=Integer.parseInt(k);
            enrollincourse(studentid,courseid,students,courses,json,false,userinput,k,choice);
        }
    }

    private  static  void choicemain(Student[] students,Course[]courses,Scanner userinput,JSONObject json,int studentid,int courseid,char choice,String k) throws IOException ,StringIndexOutOfBoundsException{
       switch(choice){
           case'b':
               homepage(students,courses,userinput,json,studentid,courseid,choice,k);
               break;
           case 'a':
               enrollmetpage(students,courses,userinput,json,studentid,courseid,choice,k);
               break;
           case'd':
               unenrollmentpage(students,courses,userinput,json,studentid,courseid,choice,k);
               break;
           case'r':
               replacementpage(students,courses,userinput,json,studentid,courseid,choice,k);

               break;

       }
    }

    private static void homepage(Student[] students, Course[] courses, Scanner userinput, JSONObject json, int studentid,int courseid,char choice,String k) throws IOException ,StringIndexOutOfBoundsException{
        System.out.println("Welcome to LMS");
        System.out.println("created by {Sherin Ahmed Maged Youssef_20/1/2023}");
        System.out.println("=================================================================================");
        System.out.println("Home page");
        System.out.println("=================================================================================");
        System.out.println("Student list:");
        listingallstudents(students,students.length);
        studentid=entervalidstudentid(studentid,json,students,courses,userinput);
        repeatunenroll(students,courses,userinput,json,studentid,courseid,choice,k);


    }

    private static int entervalidstudentid(int studentid, JSONObject json, Student[] students, Course[] courses,Scanner userinput) throws InputMismatchException{
        System.out.print("Please select the required student:");
        studentid= userinput.nextInt();
        searchforstudentid(json,studentid,students,courses);
        while(validatestudentid(studentid,students)==false){
            try{
                throw new InvalidStudentIdSelectionException();
            }catch (InvalidStudentIdSelectionException e){
                System.out.println(e.getMessage());
            }
            studentid= userinput.nextInt();
            studentid=searchforstudentid(json,studentid,students,courses);
        }
        return studentid;
    }

    private static void enrollincourse(int studentid, int courseid, Student[] students, Course[] courses, JSONObject json,boolean exception,Scanner userinput,String k,char choice) throws IOException,StringIndexOutOfBoundsException {
        if(validatestudentid(studentid,students)&validatecourseid(courseid,courses)){
            if (json.has(String.valueOf(studentid))){
                JSONArray arr=json.getJSONArray(String.valueOf(studentid));
                boolean alreadyenrolled=false;
                if(arr.length()==6){
                    try{
                        throw new EnrollmentExceedingException();
                    }
                    catch (EnrollmentExceedingException e){
                        System.out.println(e.getMessage());
                    }
                    enrollrepeat(students,courses,userinput,json,studentid,courseid,choice,k);

                }else{
                    for(int i=0;i< arr.length();i++){
                        if(arr.getInt(i)==courseid){
                            alreadyenrolled=true;
                        }
                    };
                    if (alreadyenrolled){
                        System.out.println("Student is already enrolled in course");
                        enrollrepeat(students,courses,userinput,json,studentid,courseid,choice,k);
                    }else{
                        json.append(String.valueOf(studentid),courseid);
                        writetojson(json.toString());
                        System.out.println("Student is Enrolled Successfully in the "+courses[courseid-1].name+" course.");
                    }
                }

            }else {
                JSONArray ar=new JSONArray(1);
                ar.put(courseid);
                json.put(String.valueOf(studentid),ar);
                writetojson(json.toString());
                System.out.println("Student is Enrolled Successfully in the "+courses[courseid].name+" course.");
            }
        }else{
            if(exception==false){
                try{throw new InvalidCourseIdSelection();}catch (InvalidCourseIdSelection e){
                    System.out.println(e.getMessage());
                }
                enrollrepeat(students,courses,userinput,json,studentid,courseid,choice,k);

            }

        }
    }

    private static boolean validatecourseid(int i, Course[] courses) {
        boolean idexists=false;
        for(int k=0;k<courses.length;k++){
            if(courses[k].id==i){
                idexists=true;
            }
        }
        return idexists;
    }

    private static boolean validatestudentid(int i, Student[] students) {
        Boolean idexists=false;
        for(int k=0;k<students.length;k++){
            if (students[k].id == i){
                idexists=true;
            }
        };
        return idexists;
    }

    private static int searchforstudentid(JSONObject jsonObject, int id,Student[] students,Course[] courses) {
        if (validatestudentid(id,students)){
            studentdetailspage(jsonObject,id,courses,students);
        }else {
            System.out.println("Invalid Student ID");
        }
        return id;

    }

    private static void listingallstudents(Student[] students,int number) {
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("id  name  Grade  email  address  region  country");
        for(int i=0; i<number;i++){
            System.out.println(students[i].id+"  "+students[i].name+", "+students[i].grade+", "+students[i].email+", "+students[i].address+", "+students[i].region+", "+students[i].country);
        }
        System.out.println("---------------------------------------------------------------------------------");
    }



}