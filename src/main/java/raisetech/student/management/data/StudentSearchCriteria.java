package raisetech.student.management.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "受講生一覧検索時に指定可能なパラメーター")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSearchCriteria {

  @Schema(description = "受講生の氏名")
  private String name;

  @Schema(description = "受講生のふりがな")
  private String kanaName;

  @Schema(description = "受講生のニックネーム")
  private String nickname;

  @Schema(description = "受講生のメールアドレス")
  @Email(message = "無効な入力です。有効なメールアドレスを入力してください。")
  private String email;

  @Schema(description = "受講生の住所")
  private String area;

  @Schema(description = "受講生検索時の下限年齢")
  private Integer minAge;

  @Schema(description = "受講生検索時の上限年齢")
  private Integer maxAge;

  @Schema(description = "受講生の性別(男性、女性、その他)")
  private String sex;

  @Schema(description = "削除された受講生")
  private Boolean deleted;

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



}
