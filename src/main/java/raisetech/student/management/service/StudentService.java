package raisetech.student.management.service;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.repository.StudentRepository;

@Service
public class StudentService {

  private StudentRepository repository;

  @Autowired
  public StudentService(StudentRepository repository){
    this.repository = repository;
  }

  public List<Student> searchStudentList(){
    //検索処理
    List<Student> studentList = repository.search();

    //絞り込みをする。年齢が３０代の人のみを抽出する
    List<Student> studentListOf30s = studentList.stream()
        .filter(student -> student.getAge() >= 30 && student.getAge() < 40)
        .toList();

    return studentListOf30s;
  }

  public List<StudentCourse> searchStudentCourseList(){
    //検索処理
    List<StudentCourse> studentCourseList = repository.searchCourse();

    //絞り込みで「Javaコース」のみ抽出する
    List<StudentCourse> studentCourseListOfJava = studentCourseList.stream()
        .filter(studentCourse -> studentCourse.getCourseName().equals("Javaコース"))
        .toList();

    return studentCourseListOfJava;
  }

}
