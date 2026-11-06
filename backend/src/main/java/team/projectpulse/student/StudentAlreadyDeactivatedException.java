package team.projectpulse.student;

public class StudentAlreadyDeactivatedException extends RuntimeException {

    public StudentAlreadyDeactivatedException() {
        super("Student is already deactivated.");
    }
}
