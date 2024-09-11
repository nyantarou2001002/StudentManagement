package raisetech.student.management.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  private static Student createStudent(){
    Student student = new Student();
    student.setName("江南康二");
    student.setKanaName("エナミコウジ");
    student.setNickname("エナミ");
    student.setEmail("test@example.com");
    student.setArea("奈良県");
    student.setAge(36);
    student.setSex("男性");
    student.setRemark("");
    student.setDeleted(false);
    return student;

  }

  private static StudentCourse createStudentCourse(Student student){
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(student.getId());
    studentCourse.setCourseName(student.getId());
    studentCourse.setCourseName("Javaコース");
    studentCourse.setCourseStartAt(LocalDateTime.of(2024,9,10,14,00));
    studentCourse.setCourseEndAt(LocalDateTime.of(2025,9,10,14,00));
    return studentCourse;
  }

  @Test
  void 受講生の全件検索が行えること(){
    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void 指定した受講生IDに紐づいて受講生の検索が行えること(){
    int id = 4;
    Student actual = sut.searchStudent(String.valueOf(id));
    assertEquals("佐藤涼子", actual.getName());
  }

  @Test
  void 受講生コース情報の全件検索が行えること(){
    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertEquals(10, actual.size());
  }

  @Test
  void 指定したIDに基づく受講生コース情報の検索が行えること(){
    int id = 3;
    StudentCourse actual = sut.searchStudentCourse(id);
    assertEquals("デザインコース", actual.getCourseName());
  }

  @Test
  void 受講生IDに紐づく受講生コース情報の検索が行えること(){
    int studentId = 4;
    List<StudentCourse> actual = sut.searchStudentCourse(String.valueOf(studentId));
    assertEquals(1,actual.size());
    assertEquals("Web制作コース", actual.get(0).getCourseName());
  }

  @Test
  void コース申込状況の全件検索が行えること(){
    List<CourseStatus> actual = sut.searchCourseStatusList();
    assertEquals(8,actual.size());
  }

  @Test
  void 指定した受講生コースIDに紐づくコース申込状況の検索が行えること(){
    String courseId = "5";
    CourseStatus actual = sut.searchCourseStatus(Integer.parseInt(courseId));
    assertEquals("仮申込", actual.getStatus());


  }


  @Test
  void 受講生の登録が行えること(){
    Student student = new Student();
    student.setName("江南康二");
    student.setKanaName("エナミコウジ");
    student.setNickname("エナミ");
    student.setEmail("test@example.com");
    student.setArea("奈良県");
    student.setAge(36);
    student.setSex("男性");
    student.setRemark("");
    student.setDeleted(false);

    sut.registerStudent(student);

    List<Student> actual = sut.search();

    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生コースの登録が行えること(){
    Student student = new Student();
    student.setName("江南康二");
    student.setKanaName("エナミコウジ");
    student.setNickname("エナミ");
    student.setEmail("test@example.com");
    student.setArea("奈良県");
    student.setAge(36);
    student.setSex("男性");
    student.setRemark("");
    student.setDeleted(false);

    sut.registerStudent(student);

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(student.getId());
    studentCourse.setCourseName("Javaコース");
    studentCourse.setCourseStartAt(LocalDateTime.of(2023,8,1,12,0,0));
    studentCourse.setCourseEndAt(LocalDateTime.of(2023,11,1,18,0,0));

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertEquals(11, actual.size());

  }

  @Test
  void コース申込状況の登録が行えること(){
    Student student = createStudent();
    sut.registerStudent(student);

    StudentCourse studentCourse = createStudentCourse(student);
    sut.registerStudentCourse(studentCourse);

    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setCourseId(studentCourse.getId());
    courseStatus.setStatus("仮申込");
    sut.registerCourseStatus(courseStatus);

    List<CourseStatus> actual = sut.searchCourseStatusList();
    assertEquals(9,actual.size());


  }



  @Test
  void 受講生の更新が行えること(){
    int id = 3;
    Student student = sut.searchStudent(String.valueOf(id));
    student.setName("田中花子2");
    student.setKanaName("タナカハナコ2");
    student.setNickname("ハナ2");
    student.setEmail("hana2@example.com");
    student.setArea("北海道2");
    student.setAge(44);
    student.setSex("その他");
    student.setRemark("受講生更新テスト");
    student.setDeleted(true);

    sut.updateStudent(student);

    Student actual = sut.searchStudent(String.valueOf(id));
    assertEquals("田中花子2", actual.getName());
    assertEquals("タナカハナコ2", actual.getKanaName());
    assertEquals("ハナ2", actual.getNickname());
    assertEquals("hana2@example.com", actual.getEmail());
    assertEquals("北海道2", actual.getArea());
    assertEquals(44, actual.getAge());
    assertEquals("その他", actual.getSex());
    assertEquals("受講生更新テスト", actual.getRemark());
    assertTrue(actual.isDeleted());

  }

  @Test
  void 受講生コース情報のコース名の更新が行えること(){
    int studentId = 5;
    List<StudentCourse> studentCourses = sut.searchStudentCourse(String.valueOf(studentId));
    studentCourses.get(0).setCourseName("AWSコース2");

    sut.updateStudentCourse(studentCourses.get(0));

    List<StudentCourse> actual = sut.searchStudentCourse(String.valueOf(studentId));
    assertEquals("AWSコース2", actual.get(0).getCourseName());

  }

  @Test
  void コース申込状況の更新が行えること(){
    String courseId = "1";
    CourseStatus courseStatus = sut.searchCourseStatus(Integer.parseInt(courseId));
    courseStatus.setStatus("本申込");

    sut.updateCourseStatus(courseStatus);

    CourseStatus actual = sut.searchCourseStatus(Integer.parseInt(courseId));
    assertEquals("本申込", actual.getStatus());

  }








}