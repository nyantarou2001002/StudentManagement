package raisetech.student.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.cglib.core.Local;
import org.xmlunit.diff.Comparison.Detail;
import raisetech.student.management.controller.converter.CourseConverter;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.CourseSearchCriteria;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.data.StudentSearchCriteria;
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

  @InjectMocks
  private StudentService studentService;




  @BeforeEach
  void before(){
    sut = new StudentService(repository, studentConverter,courseConverter);
  }



  @Test
  void 受講生詳細の一覧検索＿条件に応じてフィルタリングが行われること() {
    // モックデータの準備
    List<Student> mockStudents = Arrays.asList(
        new Student("1", "John", "ジョン", "Johnny", "john@example.com", "Tokyo", 25, "男性", "テスト用", false),
        new Student("2", "Jane", "ジェーン", "Janey", "jane@example.com", "Osaka", 30, "女性", "テスト用", false)
    );

    CourseStatus courseStatus1 = new CourseStatus(555, "1", "受講中");
    CourseStatus courseStatus2 = new CourseStatus(666, "2", "受講中");
    List<StudentCourse> mockStudentCourses = Arrays.asList(
        new StudentCourse("1", "1", "Math", LocalDate.of(2023, 1, 10).atStartOfDay(), LocalDate.of(2023, 6, 10).atStartOfDay(), courseStatus1),
        new StudentCourse("2", "2", "English", LocalDate.of(2023, 2, 15).atStartOfDay(), LocalDate.of(2023, 7, 15).atStartOfDay(), courseStatus2)
    );

    // 期待される結果の準備
    StudentDetail studentDetail1 = new StudentDetail(mockStudents.get(0), List.of(mockStudentCourses.get(0)));  // John
    StudentDetail studentDetail2 = new StudentDetail(mockStudents.get(1), List.of(mockStudentCourses.get(1)));  // Jane

    // 検索条件の準備
    StudentSearchCriteria criteria = new StudentSearchCriteria();
    criteria.setName("John");  // John のみ
    criteria.setMinAge(20);    // 25歳以上
    criteria.setMaxAge(30);    // 30歳以下
    criteria.setCourseName("Math"); // Math のみ

    // モックの設定
    when(repository.search()).thenReturn(mockStudents);
    when(repository.searchStudentCourseList()).thenReturn(mockStudentCourses);
    when(studentConverter.convertStudentDetails(mockStudents, mockStudentCourses)).thenReturn(Arrays.asList(studentDetail1, studentDetail2));

    // テスト実行
    List<StudentDetail> result = studentService.searchStudentList(criteria);

    // 期待される結果
    List<StudentDetail> expected = List.of(studentDetail1);

    // 結果の検証
    assertEquals(expected, result);
  }




  @Test
  void 受講生詳細の一覧検索＿検索条件が指定されていない場合に全件取得が行われること() {
    // モックデータの準備（フィルタリングなしのケース）
    List<Student> mockStudents = Arrays.asList(
        new Student("1", "John", "ジョン", "Johnny", "john@example.com", "Tokyo", 25, "男性","テスト用", false),
        new Student("2", "Jane", "ジェーン", "Janey", "jane@example.com", "Osaka", 30, "女性","テスト用", false)
    );

    CourseStatus courseStatus1 = new CourseStatus(555,"1","受講中");
    CourseStatus courseStatus2 = new CourseStatus(666,"2","受講中");
    List<StudentCourse> mockStudentCourses = Arrays.asList(
        new StudentCourse("1","1", "Math", LocalDate.of(2023, 1, 10).atStartOfDay(), LocalDate.of(2023, 6, 10).atStartOfDay(),courseStatus1),
        new StudentCourse("2","2", "English", LocalDate.of(2023, 2, 15).atStartOfDay(), LocalDate.of(2023, 7, 15).atStartOfDay(),courseStatus2)
    );

    List<StudentDetail> mockStudentDetails = Arrays.asList(
        new StudentDetail(mockStudents.get(0), List.of(mockStudentCourses.get(0))),
        new StudentDetail(mockStudents.get(1), List.of(mockStudentCourses.get(1)))
    );

    // モックの設定
    when(repository.search()).thenReturn(mockStudents);
    when(repository.searchStudentCourseList()).thenReturn(mockStudentCourses);
    when(studentConverter.convertStudentDetails(mockStudents, mockStudentCourses)).thenReturn(mockStudentDetails);

    // 検索条件なしの設定
    StudentSearchCriteria criteria = new StudentSearchCriteria();  // 全件取得

    // テスト実行
    List<StudentDetail> result = studentService.searchStudentList(criteria);

    // 検証
    assertThat(result).hasSize(2);

    // メソッド呼び出しの検証
    verify(repository).search();
    verify(repository).searchStudentCourseList();
    verify(studentConverter).convertStudentDetails(mockStudents, mockStudentCourses);
  }



  @Test
  void 受講生コース詳細の一覧検索＿引数に応じて適切に条件検索が行われること() {
    // Arrange
    CourseSearchCriteria criteria = new CourseSearchCriteria();
    criteria.setCourseName("Java");
    criteria.setStartDateFrom(LocalDate.of(2023, 1, 1));
    criteria.setEndDateTo(LocalDate.of(2023, 12, 31));
    criteria.setStatus("仮申込");

    List<StudentCourse> mockStudentCourses = Arrays.asList(
        new StudentCourse("1","1", "Java", LocalDate.of(2023, 5, 1).atStartOfDay(),
            LocalDate.of(2023, 11, 1).atStartOfDay(), new CourseStatus(888,"1", "仮申込")),
        new StudentCourse("2","3", "Python", LocalDate.of(2023, 7, 1).atStartOfDay(),
            LocalDate.of(2023, 9, 1).atStartOfDay(), new CourseStatus(999,"2", "本申込"))
    );

    List<CourseStatus> mockCourseStatuses = Arrays.asList(
        new CourseStatus(888,"1", "仮申込"),
        new CourseStatus(999,"2", "本申込")
    );

    when(repository.searchStudentCourseList()).thenReturn(mockStudentCourses);
    when(repository.searchCourseStatusList()).thenReturn(mockCourseStatuses);

    // Act
    List<StudentCourse> result = studentService.searchStudentCourseList(criteria);

    // Assert
    assertEquals(1, result.size());
    assertEquals("Java", result.get(0).getCourseName());
    assertEquals("仮申込", result.get(0).getCourseStatus().getStatus());

    // Verify repository method calls
    verify(repository, times(1)).searchStudentCourseList();
    verify(repository, times(1)).searchCourseStatusList();
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