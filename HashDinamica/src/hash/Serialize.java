package hash;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// https://stackoverflow.com/questions/44291122/how-do-i-pass-a-method-to-an-annotation-using-java-8
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Serialize{
    int numMaxBytes() default -1;
    String metodoNumMaxBytes() default "";
    String metodoLerBytes() default "";
    String metodoObterBytes() default "";
    String metodoToString() default "";
}
