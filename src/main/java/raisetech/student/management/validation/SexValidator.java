package raisetech.student.management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Getter;

public class SexValidator implements ConstraintValidator<ValidSex, String> {

  // enumの定義をこのクラス内に含めます
  @Getter
  public enum Sex {
    MALE("男性"),FEMALE("女性"),OTHERS("その他");

    private final String japaneseName;

    Sex(String japaneseName){
      this.japaneseName = japaneseName;
    }

  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;  // 入力がnullの場合は許可
    }
    try {
      // 入力値がenumのいずれかと一致するかを確認
      Sex.valueOf(value);
      return true;
    } catch (IllegalArgumentException e) {
      return false;  // enumに該当しない場合はfalseを返す
    }
  }
}
