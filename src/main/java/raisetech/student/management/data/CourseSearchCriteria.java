package raisetech.student.management.data;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import jdk.jshell.Snippet.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "受講生コース一覧検索時に指定可能なパラメーター")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchCriteria {

  @Schema(description = "受講しているコース名")
  private String courseName;

  @Schema(description = "コース申込日の範囲検索(起点)")
  private LocalDate startDateFrom;

  @Schema(description = "コース申込日の範囲検索(終点)")
  private LocalDate startDateTo;

  @Schema(description = "コース受講終了日の範囲検索(起点)")
  private LocalDate endDateFrom;

  @Schema(description = "コース受講終了日の範囲検索(終点)")
  private LocalDate endDateTo;

  @Schema(description = "コース申込状況(仮申込、受講中、受講終了、本申込")
  private String status;

}
