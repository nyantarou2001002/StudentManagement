package raisetech.student.management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import raisetech.student.management.data.Student;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;


@WebMvcTest(StudentController.class)
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void 受講生詳細の一覧検索が実行できて空のリストが返ってくること() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/studentList"))
        .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(content().json("[]"));

    Mockito.verify(service, Mockito.times(1)).searchStudentList();
  }

  @Test
  void 受講生詳細の検索が実行できて空で返ってくること() throws Exception{
    String id = "999";
    mockMvc.perform(MockMvcRequestBuilders.get("/student/{id}", id))
        .andExpect(status().isOk());

    verify(service, times(1)).searchStudent(id);
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
        .andExpect(status().is4xxClientError())
        .andExpect(content().string("このAPIは現在利用できません。古いURLとなっています。"));
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


}