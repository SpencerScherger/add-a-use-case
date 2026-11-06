package team.projectpulse.student;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.PatchMapping;
import team.projectpulse.student.converter.StudentDtoToStudentConverter;
import team.projectpulse.student.converter.StudentRegistrationToStudentConverter;
import team.projectpulse.student.converter.StudentToStudentDtoConverter;
import team.projectpulse.student.dto.StudentDto;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    StudentService studentService;
    @Mock
    StudentRegistrationToStudentConverter studentRegistrationToStudentConverter;
    @Mock
    StudentToStudentDtoConverter studentToStudentDtoConverter;
    @Mock
    StudentDtoToStudentConverter studentDtoToStudentConverter;

    @InjectMocks
    StudentController studentController;

    @Test
    void deactivateStudentUsesDedicatedPatchCommand() throws NoSuchMethodException {
        Method method = StudentController.class.getMethod("deactivateStudent", Integer.class);

        assertThat(method.getAnnotation(PatchMapping.class).value())
                .containsExactly("/{studentId}/deactivation");
    }

    @Test
    void deactivateStudentReturnsUpdatedStudent() {
        Student student = new Student();
        StudentDto studentDto = new StudentDto(4, "j.smith@abc.edu", "John", "Smith",
                "j.smith@abc.edu", false, "student", 2, "2023-2024", 1, "Team1");
        given(this.studentService.deactivateStudent(4)).willReturn(student);
        given(this.studentToStudentDtoConverter.convert(student)).willReturn(studentDto);

        Result result = this.studentController.deactivateStudent(4);

        assertThat(result.isFlag()).isTrue();
        assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
        assertThat(result.getMessage()).isEqualTo("Deactivate student successfully");
        assertThat(result.getData()).isSameAs(studentDto);
        verify(this.studentService).deactivateStudent(4);
    }
}
