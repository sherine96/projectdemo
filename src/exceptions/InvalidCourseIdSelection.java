package exceptions;

public class InvalidCourseIdSelection extends Exception{
    public InvalidCourseIdSelection(){
        super("The input you have provided is invalid, please enter a valid input");
    }
}
