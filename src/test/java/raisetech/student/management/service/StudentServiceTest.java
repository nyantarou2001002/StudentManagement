package raisetech.student.management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.web.client.ResourceAccessException;
import raisetech.student.management.controller.converter.CourseConverter;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.IntegratedDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter studentConverter;

  @Mock
  private CourseConverter courseConverter;

  private StudentService sut;


  @BeforeEach
  void before() {
    sut = new StudentService(repository, studentConverter,courseConverter);
  }

  private static List<StudentDetail> createTestStudentDetails(){
    Student activeStudent = new Student();
    activeStudent.setDeleted(false);
    StudentCourse activeStudentCourse1 = new StudentCourse();
    StudentCourse activeStudentCourse2 = new StudentCourse();
    List<StudentCourse> activeStudentCourses = new ArrayList<>(
        List.of(activeStudentCourse1, activeStudentCourse2));
    StudentDetail activeStudentDetail = new StudentDetail(activeStudent,activeStudentCourses);

    Student deletedStudent = new Student();
    deletedStudent.setDeleted(true);
    StudentCourse deletedStudentCourse1 = new StudentCourse();
    StudentCourse deletedStudentCourse2 = new StudentCourse();
    List<StudentCourse> deletedStudentCourses = new ArrayList<>(
        List.of(deletedStudentCourse1,deletedStudentCourse2));
    StudentDetail deletedStudentDetail = new StudentDetail(deletedStudent,deletedStudentCourses);

    return new ArrayList<>(List.of(activeStudentDetail,deletedStudentDetail));
  }





  @Test
  void 受講生詳細の一覧検索＿deletedがnullの場合にリポジトリとコンバーターの処理が適切に呼び出せていること(){
    Boolean deleted = null;

    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<StudentDetail> studentDetails = createTestStudentDetails();

    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(studentConverter.convertStudentDetails(studentList, studentCourseList)).thenReturn(studentDetails);

    List<StudentDetail> actualStudentDetails = sut.searchStudentList(deleted);


    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(studentConverter, times(1)).convertStudentDetails(studentList, studentCourseList);

    assertEquals(studentDetails, actualStudentDetails);
    assertEquals(2,actualStudentDetails.size());

  }

  @Test
  void 受講生詳細の一覧検索＿deletedがfalseの場合にリポジトリとコンバーターの処理が適切に呼び出せていること(){
    Boolean deleted = false;

    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<StudentDetail> studentDetails = createTestStudentDetails();

    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(studentConverter.convertStudentDetails(studentList, studentCourseList)).thenReturn(studentDetails);

    List<StudentDetail> actualStudentDetails = sut.searchStudentList(deleted);


    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(studentConverter, times(1)).convertStudentDetails(studentList, studentCourseList);

    assertFalse(studentDetails.get(0).getStudent().isDeleted());
    assertEquals(1,actualStudentDetails.size());

  }

  @Test
  void 受講生詳細の一覧検索＿deletedがtrueの場合にリポジトリとコンバーターの処理が適切に呼び出せていること(){
    Boolean deleted = true;

    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<StudentDetail> studentDetails = createTestStudentDetails();

    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(studentConverter.convertStudentDetails(studentList, studentCourseList)).thenReturn(studentDetails);

    List<StudentDetail> actualStudentDetails = sut.searchStudentList(deleted);


    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(studentConverter, times(1)).convertStudentDetails(studentList, studentCourseList);

    assertTrue(actualStudentDetails.getFirst().getStudent().isDeleted());
    assertEquals(1,actualStudentDetails.size());

  }


  @Test
  void 受講生詳細の検索＿リポジトリの処理が適切に呼び出せていること() {
    String id = "999";
    Student student = new Student();
    student.setId(id);
    when(repository.searchStudent(id)).thenReturn(student);
    when(repository.searchStudentCourse(id)).thenReturn(new ArrayList<>());

    StudentDetail expected = new StudentDetail(student, new ArrayList<>());

    StudentDetail actual = sut.searchStudent(id);

    verify(repository, times(1)).searchStudent(id);
    verify(repository, times(1)).searchStudentCourse(id);
    Assertions.assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
  }

  @Test
  void 受講生コース情報の検索＿受講生コースIDに紐づくコース申込状況と受講生情報が返ってくること() throws Exception {
    int id = 555;
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(String.valueOf(id));

    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setCourseId(studentCourse.getId());

    // モック設定: searchStudentCourse メソッドが StudentCourse を返す
    when(repository.searchStudentCourse(id)).thenReturn(studentCourse);
    when(repository.searchCourseStatus(id)).thenReturn(courseStatus);

    // sut.searchStudentCourse メソッドが StudentCourse を返すと仮定し、CourseDetail に変換する
    StudentCourse studentCourseResult = sut.searchStudentCourse(id);

    // CourseDetail オブジェクトを作成し、結果をセット
    CourseDetail result = new CourseDetail();
    result.setStudentCourse(studentCourseResult);
    result.setCourseStatus(courseStatus);

    // 検証
    verify(repository, times(1)).searchStudentCourse(id);
    verify(repository, times(1)).searchCourseStatus(id);
    assertEquals(studentCourse, result.getStudentCourse());
    assertEquals(courseStatus, result.getCourseStatus());
    assertEquals(result.getStudentCourse().getId(), result.getCourseStatus().getCourseId());
  }


  @Test
  void 受講生コース詳細の検索＿存在しない受講生コースIDを入力した場合に例外がスローされること() throws Exception {
    int id = 777; // 型をintに変更
    // スタブ設定をint型に変更
    doReturn(null).when(repository).searchStudentCourse(id);

    // 例外がスローされることを確認
    assertThrows(OpenApiResourceNotFoundException.class, () -> sut.searchStudentCourse(id));

    // 検証
    verify(repository, times(1)).searchStudentCourse(id);
    verify(repository, never()).searchCourseStatus(anyInt());
  }


  @Test
  void 受講生詳細の登録＿リポジトリの処理が適切に呼び出せている上に受講生コース情報の初期情報が登録されてコースの申込状況がインスタンス化されること() {
    String studentId = "0";
    String courseId1 = "1";
    String courseId2 = "2";

    Student student = new Student();
    student.setId(studentId);

    List<StudentCourse> studentCourseList = new ArrayList<>();
    StudentCourse studentCourse1 = new StudentCourse();
    StudentCourse studentCourse2 = new StudentCourse();
    studentCourse1.setId(courseId1);
    studentCourse2.setId(courseId2);
    studentCourseList.add(studentCourse1);
    studentCourseList.add(studentCourse2);

    StudentDetail studentDetail = new StudentDetail(student, studentCourseList);

    doNothing().when(repository).updateStudent(any(Student.class));
    doNothing().when(repository).updateStudentCourse(any(StudentCourse.class));
    doNothing().when(repository).updateCourseStatus(any(CourseStatus.class));

    LocalDateTime testStartTime = LocalDateTime.now();

    IntegratedDetail result = sut.registerStudent(studentDetail);

    verify(repository).updateStudent(student);
    verify(repository, times(2)).updateStudentCourse(any(StudentCourse.class));
    verify(repository, times(2)).updateCourseStatus(any(CourseStatus.class));

    assertEquals(student, result.getStudentDetail().getStudent());
    assertEquals(2, result.getStudentDetail().getStudentCourseList().size());
    assertEquals(2, result.getCourseDetails().size());

    result.getStudentDetail().getStudentCourseList().forEach(studentCourse -> {
      assertEquals(studentId, studentCourse.getStudentId());
      assertTrue(studentCourse.getCourseStartAt().isAfter(testStartTime) || studentCourse.getCourseStartAt().isEqual(testStartTime));
      assertTrue(studentCourse.getCourseEndAt().isAfter(studentCourse.getCourseStartAt()));
      assertEquals(1, ChronoUnit.YEARS.between(studentCourse.getCourseStartAt(), studentCourse.getCourseEndAt()));
    });

    result.getCourseDetails().forEach(courseDetail -> {
      assertEquals(courseDetail.getStudentCourse().getId(), courseDetail.getCourseStatus().getCourseId());
    });
  }





  @Test
  void 受講生詳細の登録＿初期化処理が行われること(){
    String id = "999";
    Student student = new Student();
    student.setId(id);
    StudentCourse studentCourse = new StudentCourse();

    sut.initStudentsCourse(studentCourse, student.getId());

    assertEquals(id, studentCourse.getStudentId());
    assertEquals(LocalDateTime.now().getHour(), studentCourse.getCourseStartAt().getHour());
    assertEquals(LocalDateTime.now().plusYears(1).getYear(), studentCourse.getCourseEndAt().getYear());

  }




  @Test
  void 受講生詳細の更新＿リポジトリの処理が適切に呼び出せていること(){
    Student student = new Student();
    StudentCourse studentCourse = new StudentCourse();
    List<StudentCourse> studentCourseList = List.of(studentCourse);
    StudentDetail studentDetail = new StudentDetail(student, studentCourseList);

    sut.registerStudent(studentDetail);

    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(1)).updateStudentCourse(studentCourse);
  }

  @Test
  void コース申込状況の更新＿リポジトリの処理を適切に呼び出せていること() throws Exception {
    CourseStatus courseStatus = new CourseStatus();

    doNothing().when(repository).updateCourseStatus(any(CourseStatus.class));

    sut.updateCourseStatus(courseStatus);

    verify(repository, times(1)).updateCourseStatus(courseStatus);
  }





}