package raisetech.student.management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jdk.jshell.Snippet.Status;

public class StatusValidator implements ConstraintValidator<ValidStatus, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context){
    if(value == null){
      return true;
    }
    return value == "仮申込" || value == "本申込" || value == "受講中" || value == "受講終了";
  }

}
