package raisetech.student.management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SexValidator implements ConstraintValidator<ValidSex, String> {

 // @Override
  //public boolean isValid(String value, ConstraintValidatorContext context){
    //if(value == null){
     // return true;
   // }
  //  return value.equals("男性") || value.equals("女性") || value.equals("その他");
  //}

  // enumの定義をこのクラス内に含めます
  public enum Sex {
    男性, 女性, その他;
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
