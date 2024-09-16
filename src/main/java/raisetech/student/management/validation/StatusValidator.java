package raisetech.student.management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jdk.jshell.Snippet.Status;
import lombok.Getter;

public class StatusValidator implements ConstraintValidator<ValidStatus, String> {

  @Getter
  public enum Status {
    PRELIMINARY_APPLICATION("仮申込"),FINAL_APPLICATION("本申込"),IN_PROGRESS("受講中"),COURSE_COMPLETED("受講終了");

    private final String courseStatus;

    Status(String courseStatus){
      this.courseStatus = courseStatus;
    }

  }


  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;  // 入力がnullの場合は許可
    }
    try {
      // 入力値がenumのいずれかと一致するかを確認
      StatusValidator.Status.valueOf(value);
      return true;
    } catch (IllegalArgumentException e) {
      return false;  // enumに該当しない場合はfalseを返す
    }
  }


}
