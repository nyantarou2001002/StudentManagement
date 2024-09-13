package raisetech.student.management.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.management.ConstructorParameters;

@Documented
@Constraint(validatedBy = SexValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSex {

  String message() default "無効な入力です。「男性」「女性」「その他」のいずれかを入力してください。";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};



}
