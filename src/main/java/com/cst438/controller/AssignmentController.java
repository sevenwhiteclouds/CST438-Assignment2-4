package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.AssignmentDTO;
import com.cst438.dto.AssignmentStudentDTO;
import com.cst438.dto.GradeDTO;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AssignmentController {
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    GradeRepository gradeRepository;

    // instructor lists assignments for a section.  Assignments ordered by due date.
    // logged in user must be the instructor for the section
    @GetMapping("/sections/{secNo}/assignments")
    public List<AssignmentDTO> getAssignments(@PathVariable("secNo") int secNo) {
        Section sec = sectionRepository.findById(secNo).orElse(null);

        if (sec == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "section not found");
        }

        List<Assignment> assignments = assignmentRepository.findBySectionNoOrderByDueDate(secNo);
        List<AssignmentDTO> assignmentsDTO = new ArrayList<>();

        assignments.forEach(ass -> assignmentsDTO.add(new AssignmentDTO(
            ass.getAssignmentId(),
            ass.getTitle(),
            ass.getDueDate().toString(),
            sec.getCourse().getCourseId(),
            sec.getSecId(),
            sec.getSectionNo()
        )));

        return assignmentsDTO;
    }

    // add assignment
    // user must be instructor of the section
    // return AssignmentDTO with assignmentID generated by database
    @PostMapping("/assignments")
    public AssignmentDTO createAssignment(@RequestBody AssignmentDTO dto) {
        Date dueDate = null;
        try {
            dueDate = Date.valueOf(dto.dueDate());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Section sec = sectionRepository.findById(dto.secNo()).orElse(null);

        if (sec == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (sec.getSecId() != dto.secId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (dto.title().length() > 45 || dto.title().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (dueDate.toLocalDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (!sec.getCourse().getCourseId().equals(dto.courseId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Assignment assignment = new Assignment();
        assignment.setTitle(dto.title());
        assignment.setDueDate(dueDate);
        assignment.setSection(sec);

        assignment = assignmentRepository.save(assignment);

        return new AssignmentDTO(
            assignment.getAssignmentId(),
            assignment.getTitle(),
            assignment.getDueDate().toString(),
            assignment.getSection().getCourse().getCourseId(),
            assignment.getSection().getSecId(),
            assignment.getSection().getSectionNo()
        );
    }

    // update assignment for a section.  Only title and dueDate may be changed.
    // user must be instructor of the section
    // return updated AssignmentDTO
    @PutMapping("/assignments")
    public AssignmentDTO updateAssignment(@RequestBody AssignmentDTO dto) {
        Date dueDate = null;
        try {
            dueDate = Date.valueOf(dto.dueDate());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Assignment assignment = assignmentRepository.findById(dto.id()).orElse(null);

        if (assignment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (dto.title().length() > 45 || dto.title().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (dueDate.toLocalDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (!assignment.getSection().getCourse().getCourseId().equals(dto.courseId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (assignment.getSection().getSecId() != dto.secId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (assignment.getSection().getSectionNo() != dto.secNo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        assignment.setTitle(dto.title());
        assignment.setDueDate(dueDate);
        assignment = assignmentRepository.save(assignment);

        return new AssignmentDTO(
            assignment.getAssignmentId(),
            assignment.getTitle(),
            assignment.getDueDate().toString(),
            assignment.getSection().getCourse().getCourseId(),
            assignment.getSection().getSecId(),
            assignment.getSection().getSectionNo()
        );
    }

    // delete assignment for a section
    // logged in user must be instructor of the section
    @DeleteMapping("/assignments/{assignmentId}")
    public void deleteAssignment(@PathVariable("assignmentId") int assignmentId) {

        // TODO for later - add logged in user check

        Assignment a = assignmentRepository.findById(assignmentId).orElse(null);
        // do nothing if assignment does not exist
        if (a!=null) {
            assignmentRepository.delete(a);
        }
        else {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "assignment not found ");
        }
    }

    // instructor gets grades for assignment ordered by student name
    // user must be instructor for the section
    @GetMapping("/assignments/{assignmentId}/grades")
    public List<GradeDTO> getAssignmentGrades(@PathVariable("assignmentId") int assignmentId) {

        // TODO for later - add logged in user check

        // get the list of enrollments for the section related to this assignment.
		// hint: use te enrollment repository method findEnrollmentsBySectionOrderByStudentName.
        // for each enrollment, get the grade related to the assignment and enrollment
		//   hint: use the gradeRepository findByEnrollmentIdAndAssignmentId method.
        //   if the grade does not exist, create a grade entity and set the score to NULL
        //   and then save the new entity

        Assignment a = assignmentRepository.findById(assignmentId).orElse(null);
        if (a != null) {
            int assignmentSectionId = a.getSection().getSectionNo();
            List<Enrollment> enrollments = enrollmentRepository.findEnrollmentsBySectionNoOrderByStudentName(assignmentSectionId);

            List<GradeDTO> grade_list = new ArrayList<>();

            for (Enrollment e : enrollments) {
                Grade grade = gradeRepository.findByEnrollmentIdAndAssignmentId(e.getEnrollmentId(), assignmentId);
                if (grade != null) {
                    grade_list.add(new GradeDTO(grade.getGradeId(), e.getUser().getName(), e.getUser().getEmail(), a.getTitle(),
                            a.getSection().getCourse().getCourseId(), assignmentSectionId, grade.getScore()));
                } else {
                    Grade g = new Grade();
                    g.setScore(null);
                    g.setAssignment(assignmentRepository.findById(assignmentId).orElse(null));
                    g.setEnrollment(e);
                    grade_list.add(new GradeDTO(g.getGradeId(), e.getUser().getName(), e.getUser().getEmail(), a.getTitle(),
                            a.getSection().getCourse().getCourseId(), assignmentSectionId, g.getScore()));
                }
            }
            return grade_list;
        }
        else {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "assignment not found ");
        }
    }

    // instructor uploads grades for assignment
    // user must be instructor for the section
    @PutMapping("/grades")
    public void updateGrades(@RequestBody List<GradeDTO> dlist) {

        // TODO for later - add logged in user check

        // for each grade in the GradeDTO list, retrieve the grade entity
        // update the score and save the entity

        for (int i = 0; i < dlist.size(); i++) {
            Grade g = gradeRepository.findById(dlist.get(i).gradeId()).orElse(null);
            if (g == null) {
                throw new ResponseStatusException( HttpStatus.NOT_FOUND, "grade not found ");
            }
            else {
                g.setScore(dlist.get(i).score());
                gradeRepository.save(g);
            }
        }
    }

    // student lists their assignments/grades for an enrollment ordered by due date
    // student must be enrolled in the section
    @GetMapping("/assignments")
    public List<AssignmentStudentDTO> getStudentAssignments(
            @RequestParam("studentId") int studentId,
            @RequestParam("year") int year,
            @RequestParam("semester") String semester) {

        // TODO for later - add logged in user check

        // return a list of assignments and (if they exist) the assignment grade
        //  for all sections that the student is enrolled for the given year and semester
		//  hint: use the assignment repository method findByStudentIdAndYearAndSemesterOrderByDueDate

        List<Assignment> assignments =
                assignmentRepository.findByStudentIdAndYearAndSemesterOrderByDueDate(studentId, year, semester);

        List<Enrollment> enrollments =
                enrollmentRepository.findByYearAndSemesterOrderByCourseId(year, semester, studentId);

        if (assignments.isEmpty() || enrollments.isEmpty()) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "not found ");
        }

        List<AssignmentStudentDTO> assignment_grade_list = new ArrayList<>();
        for (Assignment a : assignments) {
            if (gradeRepository.findById(a.getAssignmentId()).isPresent()){
                Integer grade = gradeRepository.findById(a.getAssignmentId()).get().getScore();
                assignment_grade_list.add(new AssignmentStudentDTO(a.getAssignmentId(), a.getTitle(), a.getDueDate(),
                    a.getSection().getCourse().getCourseId(), a.getSection().getSecId(), grade));
            }
        }

        return assignment_grade_list;
    }
}