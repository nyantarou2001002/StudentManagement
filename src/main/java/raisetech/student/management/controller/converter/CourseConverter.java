package raisetech.student.management.controller.converter;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import raisetech.student.management.data.CourseStatus;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.CourseDetail;

@Component
public class CourseConverter {

  public List<CourseDetail> convertCourseDetails(List<StudentCourse> studentCourseList, List<CourseStatus> courseStatusList) {
    List<CourseDetail> courseDetails = new ArrayList<>();

    for (StudentCourse studentCourse : studentCourseList) {
      CourseDetail courseDetail = new CourseDetail();
      courseDetail.setStudentCourse(studentCourse);

      for (CourseStatus courseStatus : courseStatusList) {
        // ここを == から equals()に変更
        if (courseStatus.getCourseId().equals(studentCourse.getId())) {
          courseDetail.setCourseStatus(courseStatus);
        }
      }

      courseDetails.add(courseDetail);
    }

    return courseDetails;
  }



}
