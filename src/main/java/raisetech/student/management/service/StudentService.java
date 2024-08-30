package raisetech.student.management.service;


import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourses;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository){
    this.repository = repository;
  }

  public List<Student> searchStudentList(){
    return repository.search();
  }

  public StudentDetail searchStudent(String id){
    Student student = repository.searchStudent(id);
    List<StudentCourses> studentCourses = repository.searchStudentsCourses(student.getId());
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourses(studentCourses);
    return studentDetail;

  }


  public List<StudentCourses> searchStudentCourseList(){
    return repository.searchStudentsCoursesList();
  }

  @Transactional
  public void  registerStudent(StudentDetail studentDetail) {
    repository.registerStudent((studentDetail.getStudent()));
    //  TODO:コース情報登録も行う
    for (StudentCourses studentCourses : studentDetail.getStudentCourses()) {
      studentCourses.setStudentId(studentDetail.getStudent().getId());
      studentCourses.setCourseStartAt(LocalDateTime.now());
      studentCourses.setCourseEndAt(LocalDateTime.now().plusYears(1));
      repository.registerStudentsCourses(studentCourses);
    }
  }
  @Transactional
  public void  updateStudent(StudentDetail studentDetail){
    repository.updaterStudent(studentDetail.getStudent());
    for(StudentCourses studentCourses : studentDetail.getStudentCourses()){
      studentCourses.setStudentId(studentDetail.getStudent().getId());
      repository.updateStudentsCourses(studentCourses);
  }




  }


}
