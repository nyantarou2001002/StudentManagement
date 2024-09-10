package raisetech.student.management.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.student.management.controller.converter.CourseConverter;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.IntegratedDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;


/**
 *受講生情報をトリア塚サービスです。
 * 受講生の検索や登録・更新処理を行います。
 */
@Service
public class StudentService {

  private StudentRepository repository;
  //private StudentConverter converter;
  private StudentConverter studentConverter;
  private CourseConverter courseConverter;


  @Autowired
  public StudentService(StudentRepository repository, StudentConverter studentConverter, CourseConverter courseConverter){
    this.repository = repository;
    this.studentConverter = studentConverter;
    this.courseConverter = courseConverter;
  }



  /**
   * 受講生の一覧検索を行います。
   * 指定されたリクエストパラメーターの値に応じてフィルタリングを行います。
   *
   * @return　受講生一覧(全件)
   */
  public List<StudentDetail> searchStudentList(Boolean deleted){
    List<Student> studentList = repository.search();
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    List<StudentDetail> studentDetails = studentConverter.convertStudentDetails(studentList, studentCourseList);

    //return converter.convertStudentDetails(studentList, studentCourseList);
    return studentDetails.stream()
        .filter(studentDetail ->
            deleted == null || studentDetail.getStudent().isDeleted() == deleted)
        .toList();
  }

  /**
   * 受講生コース詳細一覧検索を行います。
   *
   * @return　コース詳細情報一覧
   */
  //public List<CourseDetail> searchStudentCourseList(){
  public List<StudentCourse> searchStudentCourseList(){
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    List<CourseStatus> courseStatusList = repository.searchCourseStatusList();

    //return courseConverter.convertCourseDetails(studentCourseList, courseStatusList);
    return  studentCourseList;

  }


  /**
   * 受講生検索です
   * IDに紐づく受講生情報を取得した後、その受講生に紐づく受講生コース情報を取得して設定します。
   *
   * @param id　受講生ID
   * @return 受講生詳細
   */
  public StudentDetail searchStudent(String id)  {
    Student student = repository.searchStudent(id);
    List<StudentCourse> studentCourse = repository.searchStudentCourse(student.getId());
    return new StudentDetail(student, studentCourse);

  }

  //public CourseDetail searchStudentCourse(int id) {
  public StudentCourse searchStudentCourse(int id) {
    StudentCourse studentCourse = repository.searchStudentCourse(id);

    if (studentCourse == null) {
      throw new OpenApiResourceNotFoundException("StudentCourse with id " + id + " not found");
    }

    // `studentCourse.getId()` が null の場合も考慮
    String courseId = studentCourse.getId();
    if (courseId == null) {
      throw new OpenApiResourceNotFoundException("Course ID is null for StudentCourse with id " + id);
    }

    CourseStatus courseStatus = repository.searchCourseStatus(Integer.parseInt(courseId));

    // `courseStatus` が null である場合の処理も追加する場合があります
    if (courseStatus == null) {
      throw new OpenApiResourceNotFoundException("CourseStatus not found for course ID " + courseId);
    }

    return studentCourse;
  }







  /**
   * 受講生詳細の登録を行います。
   * 受講生と受講生コース情報を個別に登録し受講生コース情報には受講生情報を紐づける値とコース開始日、コース終了日を設定します。
   * コース申込状況に受講生コースIDの設定を行います。
   *
   * @param studentDetail　受講生詳細
   * @return　登録情報を付与した受講生詳細。
   * @return 登録情報を付与した受講生詳細とコース詳細の統合情報
   */
  @Transactional
  public IntegratedDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();

    // Register student (or update student if necessary)
    repository.updateStudent(student);

    List<CourseDetail> courseDetails = new ArrayList<>();

    studentDetail.getStudentCourseList().forEach(studentCourses -> {
      initStudentsCourse(studentCourses, student.getId());

      // Register or update student course
      repository.updateStudentCourse(studentCourses);

      CourseStatus courseStatus = new CourseStatus();
      courseStatus.setCourseId(studentCourses.getId());
      // Set status as needed
      courseStatus.setStatus("仮登録");
      repository.updateCourseStatus(courseStatus);

      CourseDetail courseDetail = new CourseDetail();
      courseDetail.setStudentCourse(studentCourses);
      courseDetail.setCourseStatus(courseStatus);

      courseDetails.add(courseDetail);
    });

    return new IntegratedDetail(studentDetail, courseDetails);
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
    repository.updateStudent(studentDetail.getStudent());
    studentDetail.getStudentCourseList()
        .forEach(studentCourse -> repository.updateStudentCourse(studentCourse));

  }

  /**
   * コース申込状況の更新を行います。
   * 指定したコースの更新状況に紐づく情報を更新します。
   *
   * @param courseStatus コース申込状況
   */
  @Transactional
  public void updateCourseStatus(CourseStatus courseStatus) throws Exception{
    repository.updateCourseStatus(courseStatus);
  }


}