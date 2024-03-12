package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {


    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SectionRepository sectionRepository;

   // student gets transcript showing list of all enrollments
   // studentId will be temporary until Login security is implemented
   //example URL  /transcript?studentId=19803
   @GetMapping("/transcripts")
   public List<EnrollmentDTO> getTranscript(@RequestParam("studentId") int studentId) {

       // list course_id, sec_id, title, credit, grade in chronological order
       // user must be a student
       // hint: use enrollment repository method findEnrollmentByStudentIdOrderByTermId
       // irrelevant fields set to null to only return course_id, sec_id, title, credit, and grade

       // Checks if user is a student. TODO: Change will likely be needed when Login security is implemented.
       if (!isStudent(studentId)) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID: " + studentId + " is not a student.");
       }

       List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsByStudentIdOrderByTermId(studentId);
       List<EnrollmentDTO> dto_list = new ArrayList<>();
       for(Enrollment e : enrollments) {
           dto_list.add(new EnrollmentDTO(
                   e.getEnrollmentId(),
                   e.getGrade(),
                   e.getUser().getId(),
                   null,
                   e.getSection().getCourse().getTitle(),
                   null,
                   e.getSection().getCourse().getCourseId(),
                   e.getSection().getSecId(),
                   e.getSection().getSectionNo(),
                   null,
                   null,
                   null,
                   e.getSection().getCourse().getCredits(),
                   0,
                   null));
       }
        return dto_list;
   }

    // student gets a list of their enrollments for the given year, semester
    // user must be student
    // studentId will be temporary until Login security is implemented
   @GetMapping("/enrollments")
   public List<EnrollmentDTO> getSchedule(
           @RequestParam("year") int year,
           @RequestParam("semester") String semester,
           @RequestParam("studentId") int studentId) {

       // Checks if user is a student. TODO: Change will likely be needed when Login security is implemented.
       if (!isStudent(studentId)) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID: " + studentId + " is not a student.");
       }

       List<Enrollment> enrollments = enrollmentRepository.findByYearAndSemesterOrderByCourseId(year, semester, studentId);
       List<EnrollmentDTO> enrollmentDTOList = new ArrayList<>();
       enrollments.forEach(e -> enrollmentDTOList.add(new EnrollmentDTO(
           e.getEnrollmentId(),
           e.getGrade(),
           e.getUser().getId(),
           e.getUser().getName(),
           e.getSection().getCourse().getTitle(),
           e.getUser().getEmail(),
           e.getSection().getCourse().getCourseId(),
           e.getSection().getSecId(),
           e.getSection().getSectionNo(),
           e.getSection().getBuilding(),
           e.getSection().getRoom(),
           e.getSection().getTimes(),
           e.getSection().getCourse().getCredits(),
           e.getSection().getTerm().getYear(),
           e.getSection().getTerm().getSemester()
       )));

       return enrollmentDTOList;
   }


    // student adds enrollment into a section
    // user must be student
    // return EnrollmentDTO with enrollmentId generated by database
    @PostMapping("/enrollments/sections/{sectionNo}")
    public EnrollmentDTO addCourse(
		    @PathVariable int sectionNo,
            @RequestParam("studentId") int studentId ) {

        if (sectionRepository.findById(sectionNo).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This section does not exist.");
        }

        // Checks if user is a student. TODO: Change will likely be needed when Login security is implemented.
        if (!isStudent(studentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID: " + studentId + " is not a student.");
        }

        Section s = sectionRepository.findById(sectionNo).get();
        User user = userRepository.findById(studentId).get();
        Enrollment enrollment = new Enrollment();

        Calendar today = Calendar.getInstance();

        if (today.getTime().before(s.getTerm().getAddDate())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The add date for this section hasn't started yet.");
        } else if (today.getTime().after(s.getTerm().getAddDeadline())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The add deadline for this section has passed.");
        }

        Enrollment alreadyEnrolledCheck = enrollmentRepository.findEnrollmentBySectionNoAndStudentId(sectionNo, studentId);
        if (alreadyEnrolledCheck != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student is already enrolled in section " + sectionNo);
        }

        // Create new enrollment entity + save. Grade == NULL
        enrollment.setSection(s);
        enrollment.setGrade("NULL");
        enrollment.setUser(user);

        enrollment = enrollmentRepository.save(enrollment);

        return new EnrollmentDTO(
            enrollment.getEnrollmentId(),
            enrollment.getGrade(),
            enrollment.getUser().getId(),
            enrollment.getUser().getName(),
            enrollment.getSection().getCourse().getTitle(),
            enrollment.getUser().getEmail(),
            enrollment.getSection().getCourse().getCourseId(),
            enrollment.getSection().getSecId(),
            enrollment.getSection().getSectionNo(),
            enrollment.getSection().getBuilding(),
            enrollment.getSection().getRoom(),
            enrollment.getSection().getTimes(),
            enrollment.getSection().getCourse().getCredits(),
            enrollment.getSection().getTerm().getYear(),
            enrollment.getSection().getTerm().getSemester()
        );

    }

    // student drops a course. User MUST be student.
   @DeleteMapping("/enrollments/{enrollmentId}")
   public void dropCourse(@PathVariable("enrollmentId") int enrollmentId) {

       if (enrollmentRepository.findById(enrollmentId).isEmpty()) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment does not exist.");
       }

       // Checks if user is a student. TODO: Change will likely be needed when Login security is implemented.
       int studentId = enrollmentRepository.findById(enrollmentId).get().getUser().getId();
       if (!isStudent(studentId)) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID: " + studentId + " is not a student.");
       }

       Calendar today = Calendar.getInstance();
       Enrollment currentEnrollment = enrollmentRepository.findById(enrollmentId).get();

       if (today.getTime().after(currentEnrollment.getSection().getTerm().getDropDeadline())) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Current date is beyond the drop deadline.");
       }

       // Drops student from course
       enrollmentRepository.delete(currentEnrollment);

   }

   // Checks if user is a student TODO: Likely will need to be changed once login is implemented
   private boolean isStudent(int userId) {
       if (userRepository.findById(userId).isEmpty()) {
           return false;
       } else return Objects.equals(userRepository.findById(userId).get().getType(), "STUDENT");
   }
}