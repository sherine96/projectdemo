package exceptions;

public class UnenrollmentInAnyCourseException extends Exception{
    public UnenrollmentInAnyCourseException(){
        super("Student hasn't enrolled in any course yet.");
    }
}
