//package uk.gov.dwp.utils.field;
//
//import uk.gov.dwp.datatypes.EventDate;
//import uk.gov.dwp.datatypes.FieldType;
//import uk.gov.dwp.datatypes.Period;
//
//import java.lang.annotation.Repeatable;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//
//@Repeatable(EvaluateFields.class)
//@Retention(RetentionPolicy.RUNTIME)
//public @interface EvaluateField {
//
//    FieldType ofType();
//
//    Period ofPeriod() default Period.DEFAULT;
//
//    EventDate withValue() default EventDate.DEFAULT;
//
//}
