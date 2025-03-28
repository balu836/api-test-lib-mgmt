package uk.gov.dwp.utils.pdf;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PDFDocField {
    String value() default "";
}
