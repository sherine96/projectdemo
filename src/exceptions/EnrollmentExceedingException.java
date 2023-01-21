package exceptions;

public class EnrollmentExceedingException extends Exception
{
    public EnrollmentExceedingException(){
        super("Students can’t enroll in more than 6 programs at the same time.");
    }



}
