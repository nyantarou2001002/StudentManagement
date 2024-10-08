
package raisetech.student.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.student.management.data.CourseSearchCriteria;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.data.StudentSearchCriteria;
import raisetech.student.management.domain.IntegratedDetail;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;

/**
 *受講生の検索や登録、更新などを行うREST APIとして実行されるControllerです。
 */

@Validated
@RestController
public class StudentController {

  private StudentService service;


  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }


  /**
   * 受講生詳細一覧検索です。 リクエストパラメーターを指定すると絞り込み検索ができます。
   *
   * @param criteria 検索条件
   * @return 受講生詳細一覧
   */
  @Operation(summary = "一覧検索", description = "受講生の一覧を検索します。")
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList(@Valid @ModelAttribute StudentSearchCriteria criteria){
    return service.searchStudentList(criteria);
  }

  /**
   * 受講生コースの一覧検索です。 コースの申込状況を確認できます。
   *
   * @return 受講生コース詳細一覧
   */
  @Operation(summary = "受講生コース一覧検索", description = "条件に合致する受講生コースの一覧を検索します。")
  @GetMapping("/studentList/courses")
  //public List<CourseDetail> getStudentCoursesList() {
  public List<StudentCourse> getStudentCoursesList(@Valid @ModelAttribute CourseSearchCriteria criteria){
    return service.searchStudentCourseList(criteria);
  }


  /**
   * 受講生詳細検索です IDに紐づく任意の受講生の情報を取得します
   *
   * @return 受講生
   * @oaram id 受講生ID
   */
  @Operation(summary = "受講生詳細検索", description = "IDに紐づく任意の受講生の情報を取得します。")
  @GetMapping("/student/{id}")
  public StudentDetail getStudent(
      @PathVariable @NotBlank @Pattern(regexp = "^\\d+$") String id) {
    return service.searchStudent(id);
  }

  /**
   * 受講生コース詳細検索です。IDに紐づく任意の受講生の情報を取得します。
   *
   * @param id 受講生コースID
   * @return 受講生コース詳細情報(申込状況)
   */
  @Operation(summary = "受講生コース詳細検索", description = "IDに紐づく任意の受講生コースの情報を取得します。")
  @GetMapping("/studentList/courses/detail")
  //public CourseDetail getStudentCourses(
  public StudentCourse getStudentCourses(
      @Parameter(description = "受講生コースのID") @RequestParam @NotNull int id){
    return service.searchStudentCourse(id);
  }


  /**
   * 受講生詳細の登録を行います。
   * コースの申込状況は「仮登録」として自動登録されます。
   *
   * @param studentDetail　受講生詳細
   * @return　実行結果
   */
  @Operation(summary = "受講生登録", description = "受講生を登録します。")
  @PostMapping("/registerStudent")
  public ResponseEntity<IntegratedDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail){

    //studentCourseListのnullチェック
    if(studentDetail.getStudentCourseList() == null){
      //空のリストをセット
      studentDetail.setStudentCourseList(new ArrayList<>());
    }

    IntegratedDetail newStudent = service.registerStudent(studentDetail);
    return ResponseEntity.ok(newStudent);
  }

  /**
   * 受講生詳細の更新を行います。
   * キャンセルフラグの更新もここで行います。(論理削除)
   * @param studentDetail 受講生詳細
   * @return　実行結果
   */
  @Operation(summary = "受講生更新", description = "受講生詳細の更新を行います。またキャンセルフラグの更新もここで行います。")
  @PutMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(
      @RequestBody @Valid StudentDetail studentDetail){
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました。");
  }

  /**
   * 受講生コースの申込状況の更新を行います。
   *
   * @param courseStatus　受講生コースの申込状況
   * @return　実行結果
   */
  @Operation(summary = "受講生コース申込状況更新", description = "受講生コースの申込状況の更新を行います。")
  @PutMapping("/studentList/courses/statuses/update")
  public ResponseEntity<String> updateStudentCourse(@RequestBody @Valid CourseStatus courseStatus) {
    try {
      service.updateCourseStatus(courseStatus);
      return ResponseEntity.ok("更新処理が成功しました");
    } catch (Exception e) {
      // エラーログを出力する場合
      e.printStackTrace();
      // エラーメッセージを含むレスポンスを返す
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("更新処理に失敗しました: " + e.getMessage());
    }
  }

  @GetMapping("/exception")
  public ResponseEntity<String> throwException() {
    return ResponseEntity.badRequest()
        .body("このAPIは現在利用できません。古いURLとなっています。");
  }



}
