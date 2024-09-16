package raisetech.student.management.repository;



import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

/**
 * 受講生テーブルと受講生コース情報テーブルと紐づくRepositoryです。
 */
@Mapper
public interface StudentRepository {

  /**
   * 受講生の全件検索を行います。
   *
   * @return　受講生一覧(全件)
   */

  List<Student> search();

  /**
   * 受講生の検索を行います。
   *
   * @param id　受講生ID
   * @return　受講生
   */
  Student searchStudent(String id);

  /**
   * 受講生のコース情報の全件検索を行います。
   *
   * @return　受講生コース情報(全件)
   */
  List<StudentCourse> searchStudentCourseList();

  /**
   *
   * @param id 受講生コースID
   * @return 受講生コースIDに紐づく受講生コース情報
   */
  StudentCourse searchStudentCourse(int id);

  @Select("SELECT * FROM student_courses WHERE student_id = #{studentId}")
  List<StudentCourse> searchStudentCourseListForStudent(String studentid);

  /**
   * 受講生IDに紐づく受講生コース情報を検索します。
   *
   * @param studentId 受講生ID
   * @return 受講生IDに紐づく受講生コース情報
   */
  List<StudentCourse> searchStudentCourse(String studentId);

  /**
   * 受講生のコース申込状況の全件検索を行います。
   *
   * @return コース申込状況(全件)
   */
  List<CourseStatus> searchCourseStatusList();

  /**
   * 受講生コースIDに紐づく受講生コース申込状況を検索します。
   *
   * @param courseId 受講生コースID
   * @return 受講生コースIDに紐づく受講生コース申込状況
   */
  CourseStatus searchCourseStatus(int courseId);

  /**
   * 受講生を新規登録します。
   * IDに関しては自動採番を行う。
   *
   * @param student　受講生
   */
  void registerStudent(Student student);


  /**
   * 受講生コース情報を新規登録します。IDに関しては自動採番を行う。
   *
   * @param studentCourse　受講生コース情報
   */
  void registerStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生コースの申し込み状況を新規登録します。IDに関しては自動採番を行う。
   *
   * @param courseStatus 新規の受講生コースの申込状況
   */
  void registerCourseStatus(CourseStatus courseStatus);


  /**
   * 受講生を更新します。
   *
   * @param student　受講生
   */
  void updateStudent(Student student);

  /**
   * 受講生コース情報のコース名を更新します。
   *
   *
   * @param studentCourse　受講生コース情報
   */
  void updateStudentCourse(StudentCourse studentCourse);

  /**
   * 受講生コースの申込状況を更新します。
   *
   * @param courseStatus 受講生コースの申込状況の更新情報
   */
  void updateCourseStatus(CourseStatus courseStatus);

}
