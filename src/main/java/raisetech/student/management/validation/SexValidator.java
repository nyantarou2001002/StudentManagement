package raisetech.student.management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SexValidator implements ConstraintValidator<ValidSex, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context){
    if(value == null){
      return true;
    }
    return value == "男性" || value == "女性" || value == "その他";

  }
}





