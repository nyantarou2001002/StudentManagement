package raisetech.student.management.data;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jdk.jshell.Snippet.Status;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "コースに対する申込情報")
@Getter
@Setter
public class CourseStatus {

  private int id;

  private String courseId;

  @NotNull @Pattern(regexp = "仮申込|本申込|受講中|受講終了", message = "ステータスは '仮申込', '本申込', '受講中', '受講終了' のいずれかでなければなりません。")
  private String status;
}