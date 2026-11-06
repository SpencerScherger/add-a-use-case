package team.projectpulse.student;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer>, JpaSpecificationExecutor<Student> {

    // Find all students in a section
    List<Student> findBySectionSectionId(Integer sectionId);

    List<Student> findByTeamTeamId(Integer teamId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Student s
            set s.username = :username,
                s.firstName = :firstName,
                s.lastName = :lastName,
                s.email = :email
            where s.id = :studentId
            """)
    int updateProfile(@Param("studentId") Integer studentId,
                      @Param("username") String username,
                      @Param("firstName") String firstName,
                      @Param("lastName") String lastName,
                      @Param("email") String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Student s where s.id = :studentId")
    Optional<Student> findEvaluationTargetForUpdate(@Param("studentId") Integer studentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Student s set s.enabled = false where s.id = :studentId and s.enabled = true")
    int deactivateIfEnabled(@Param("studentId") Integer studentId);
}
