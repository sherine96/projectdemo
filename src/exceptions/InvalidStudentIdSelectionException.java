package exceptions;

public class InvalidStudentIdSelectionException extends Exception{
    public InvalidStudentIdSelectionException(){
        super("“The input you have provided is invalid, please enter a valid input”");
    }
}
