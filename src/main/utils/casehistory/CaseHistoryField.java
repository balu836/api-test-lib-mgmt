package uk.gov.dwp.utils.casehistory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CaseHistoryField {
    String value() default "";
}
