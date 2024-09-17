package raisetech.student.management.data;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "コースに対する申込情報")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseStatus {

  private int id;

  private String courseId;

  @NotNull @Pattern(regexp = "仮申込|本申込|受講中|受講終了", message = "ステータスは '仮申込', '本申込', '受講中', '受講終了' のいずれかでなければなりません。")
  private String status;
}
