package raisetech.student.management.service;


import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.controller.conveter.StudentConverter;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;


/**
 *受講生情報をトリア塚サービスです。
 * 受講生の検索や登録・更新処理を行います。
 */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter converter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter){
    this.repository = repository;
    this.converter = converter;
  }



  /**
   * 受講生の一覧検索を行います。
   * 全件検索を行うので、条件指定は行いません。
   * 
   * @return　受講生一覧(全件)
   */
  public List<StudentDetail> searchStudentList(){
    List<Student> studentList = repository.search();
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    return converter.convertStudentDetails(studentList, studentCourseList);
  }


  /**
   * 受講生検索です
   * IDに紐づく受講生情報を取得した後、その受講生に紐づく受講生コース情報を取得して設定します。
   * 
   * @param id　受講生ID
   * @return 受講生詳細
   */
  public StudentDetail searchStudent(String id){
    Student student = repository.searchStudent(id);
    List<StudentCourse> studentCourse = repository.searchStudentCourse(student.getId());
    return new StudentDetail(student, studentCourse);

  }


  /**
   * 受講生詳細の登録を行います。
   * 受講生と受講生コース情報を個別に登録し受講生コース情報には受講生情報を紐づける値とコース開始日、コース終了日を設定します。
   * 
   * @param studentDetail　受講生詳細
   * @return　登録情報を付与した受講生詳細。
   */
  @Transactional
  public StudentDetail  registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();


    repository.registerStudent(student);
    studentDetail.getStudentCourseList().forEach(studentCourses -> {
      initStudentsCourse(studentCourses, student.getId());
      repository.registerStudentCourse(studentCourses);
    });
    return studentDetail;
  }

  /**
   * 受講生コース情報を登録する際の初期情報を設定する。
   *
   * @param studentCourses　受講生コース情報
   * @param id　受講生
   */
  void initStudentsCourse(StudentCourse studentCourses, String id) {
    LocalDateTime now = LocalDateTime.now();

    studentCourses.setStudentId(id);
    studentCourses.setCourseStartAt(now);
    studentCourses.setCourseEndAt(now.plusYears(1));
  }

  /**
   * 受講生詳細の更新を行います。
   * 受講生の情報と受講生コース情報をそれぞれ更新します。
   * @param studentDetail　受講生詳細
   */
  @Transactional
  public void  updateStudent(StudentDetail studentDetail){
    repository.updaterStudent(studentDetail.getStudent());
    studentDetail.getStudentCourseList()
        .forEach(studentCourse -> repository.updateStudentCourse(studentCourse));




  }


}
