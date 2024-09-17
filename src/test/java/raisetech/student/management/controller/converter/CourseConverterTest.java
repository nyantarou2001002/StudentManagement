package raisetech.student.management.controller.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.CourseDetail;

@ExtendWith(MockitoExtension.class)
class CourseConverterTest {

  private CourseConverter sut;

  @BeforeEach
  void before(){
    sut = new CourseConverter();
  }

  @Test
  void 受講生コース一覧及びコースの申込状況一覧から受講生コースIDが一致する情報を受講生コース詳細情報一覧にできていること(){
    List<StudentCourse> studentCourseList = new ArrayList<>();
    IntStream.rangeClosed(1, 2).forEach(i -> {
      StudentCourse studentCourse = new StudentCourse();
      studentCourse.setId(String.valueOf(i));
      studentCourseList.add(studentCourse);
    });

    List<CourseStatus> courseStatusList = new ArrayList<>();
    IntStream.rangeClosed(1, 2).forEach(i -> {
      CourseStatus courseStatus = new CourseStatus();
      courseStatus.setCourseId(String.valueOf(i));
      courseStatusList.add(courseStatus);
    });

    List<CourseDetail> actualCourseDetails = sut.convertCourseDetails(studentCourseList, courseStatusList);

    assertEquals(2, actualCourseDetails.size());

    for(CourseDetail detail : actualCourseDetails){
      assertEquals(detail.getStudentCourse().getId(), detail.getCourseStatus().getCourseId());

    }

  }

}
