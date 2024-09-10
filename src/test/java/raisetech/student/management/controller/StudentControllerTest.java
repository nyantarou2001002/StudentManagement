package raisetech.student.management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.CourseDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;


@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static StudentCourse createTestCourseDetail(String id){
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setId(id);

    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setCourseId(id);

    return studentCourse;
  }

  @Test
  void 受講生詳細の一覧検索が実行できて空のリストが返ってくること() throws Exception{
    Boolean deleted = false;

    mockMvc.perform(MockMvcRequestBuilders.get("/studentList")
            .param("deleted", String.valueOf(deleted))) // paramをgetの直後に
        .andExpect(status().isOk());

    Mockito.verify(service, Mockito.times(1)).searchStudentList(deleted);
  }


  @Test
  void 受講生コース詳細の一覧検索が実行できて空リストが返ってくること() throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.get("/studentList/courses"))
          .andExpect(status().isOk());

      verify(service, times(1)).searchStudentCourseList();

  }


  @Test
  void 受講生詳細の検索が実行できて空で返ってくること() throws Exception{
    String id = "999";
    mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", id))
        .andExpect(status().isOk());

    verify(service, times(1)).searchStudent(id);
  }

  @Test
  void 受講生コース詳細の検索が実行できて指定したIDに紐づくcourseDetailが返ってくること() throws Exception{
    String id = "999";
    StudentCourse studentCourse = createTestCourseDetail(id);
    when(service.searchStudentCourse(Integer.parseInt(id))).thenReturn(studentCourse);

    mockMvc.perform(MockMvcRequestBuilders.get("/studentList/courses/detail").param("id", id))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.courseStatus").doesNotExist()); // courseStatusがnullの場合

    verify(service, times(1)).searchStudentCourse(Integer.parseInt(id));
  }



  @Test
  void 受講生コース詳細の検索を行った際に入力チェックにかかること() throws Exception {
    String id = "999";
    when(service.searchStudentCourse(Integer.parseInt(id))).thenThrow(
        new OpenApiResourceNotFoundException("受講生ID　「" + id + "」は存在しません。"));

    mockMvc.perform(MockMvcRequestBuilders.get("/studentList/courses/detail").param("id", id))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof OpenApiResourceNotFoundException))
        .andExpect(result -> assertEquals("受講生ID　「" + id + "」は存在しません。",
            result.getResolvedException().getMessage()));

    verify(service, times(1)).searchStudentCourse(Integer.parseInt(id));
  }



  @Test
  void コース申込状況の更新が実行できて＿更新処理が成功しました＿というメッセージが返ってくること() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/studentList/courses/statuses/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                    {"courseId":1, "status":"受講中"}
                    """
            ))
        .andExpect(status().isOk())
        .andExpect(content().string("更新処理が成功しました"));

    verify(service, times(1)).updateCourseStatus(any());
  }



  @Test
  void 受講生詳細の登録が実行できて空で返ってくること() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/registerStudent").contentType(MediaType.APPLICATION_JSON).content(
        """
            {
                    "student": {
                        "name" : "江南浩介",
                        "kanaName" : "エナミ",
                        "nickname" : "コウジ",
                        "email" : "test@example.com",
                        "area" : "奈良",
                        "age" : "36",
                        "sex" : "男性",
                        "remark" : ""
                    },
                    "studentCourseList" : [
                        {
                             "courseName" : "Javaコース"
                        }
                    ]
            }
            """
    ))
        .andExpect(status().isOk());

    verify(service, times(1)).registerStudent(any());
  }



  @Test
  void 受講生詳細の更新が実行できて空で返ってくること()throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.put("/updateStudent").contentType(MediaType.APPLICATION_JSON).content(
        """
            {
                    "student": {
                        "id" : "12",
                        "name" : "江南浩介",
                        "kanaName" : "エナミ",
                        "nickname" : "コウジ",
                        "email" : "test@example.com",
                        "area" : "奈良",
                        "age" : "36",
                        "sex" : "男性",
                        "remark" : ""
                    },
                    "studentCourseList" : [
                        {
                             "id" : "15",
                             "studentId" : "12",
                             "courseName" : "Javaコース",
                             "courseStartAt" : "2024-04-27T10:50:39.833614",
                             "courseEndAt" : "2025-04-27T10:50:39.833614"
                        }
                    ]
            }
            """
    ))
        .andExpect(status().isOk());

    verify(service, times(1)).updateStudent(any());
  }

  @Test
  void 受講生詳細の例外APIが実行できてステータスが400で返ってくること() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/exception"))
        .andExpect(status().isBadRequest()) // ステータスコードが400であることを確認
        .andExpect(content().string("このAPIは現在利用できません。古いURLとなっています。")); // レスポンス内容を確認
  }



  @Test
  void 受講生詳細の受講生で適切な値を入力チェックに異常が発生しないこと(){
    Student student = new Student();
    student.setId("1");
    student.setName("江南康二");
    student.setKanaName("エナミコウジ");
    student.setNickname("エナミ");
    student.setEmail("test@example.com");
    student.setArea("奈良県");
    student.setSex("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(0);

  }

  @Test
  void 受講生詳細の受講生でIDに数字以外を用いた時に入力チェックにかかること(){
    Student student = new Student();
    student.setId("テストです。");
    student.setName("江南康二");
    student.setKanaName("エナミコウジ");
    student.setNickname("エナミ");
    student.setEmail("test@example.com");
    student.setArea("奈良県");
    student.setSex("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message").containsOnly("数字のみを入力するようにしてください");


  }

  @Test
  void コース申込状況の入力チェックができて入力された情報が適切な時に入力チェックがかからないこと() throws Exception{
    CourseStatus courseStatus = new CourseStatus();

    courseStatus.setId(111);
    courseStatus.setCourseId("222");
    courseStatus.setStatus("仮申込");

    Set<ConstraintViolation<CourseStatus>> violations = validator.validate(courseStatus);

    assertEquals(0, violations.size());

  }

  @Test
  void コース申込状況の入力チェックができて入力された値に不適札なものがある場合に入力チェックがかかること() throws  Exception{
    CourseStatus courseStatus = new CourseStatus();

    courseStatus.setId(111);
    courseStatus.setCourseId("222");

    courseStatus.setStatus(null);

    Set<ConstraintViolation<CourseStatus>> violations = validator.validate(courseStatus);

    assertEquals(1,violations.size());


  }




}