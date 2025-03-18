package com.cts.myjobportal.controller;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.cts.myjobportal.entity.JobPostActivity;
import com.cts.myjobportal.entity.JobSeekerApply;
import com.cts.myjobportal.entity.JobSeekerProfile;
import com.cts.myjobportal.entity.RecruiterJobsDto;
import com.cts.myjobportal.entity.RecruiterProfile;
import com.cts.myjobportal.entity.Users;
import com.cts.myjobportal.services.JobPostActivityService;
import com.cts.myjobportal.services.JobSeekerApplyService;
import com.cts.myjobportal.services.UsersService;

@Controller
public class JobPostActivityController {

	private final UsersService usersService;
	private final JobPostActivityService jobPostActivityService;
	private final JobSeekerApplyService jobSeekerApplyService;

	@Autowired
	public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService,
			JobSeekerApplyService jobSeekerApplyService) {
		this.usersService = usersService;
		this.jobPostActivityService = jobPostActivityService;
		this.jobSeekerApplyService = jobSeekerApplyService;
	}

	@GetMapping("/dashboard/")
	public String searchJobs(Model model) {
		List<JobPostActivity> jobPost = null;
		jobPost = jobPostActivityService.getAll();

		Object currentUserProfile = usersService.getCurrentUserProfile();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUsername = authentication.getName();
			model.addAttribute("username", currentUsername);
			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
				List<RecruiterJobsDto> recruiterJobs = jobPostActivityService
						.getRecruiterJobs(((RecruiterProfile) currentUserProfile).getUserAccountId());
				model.addAttribute("jobPost", recruiterJobs);
			} else {
				List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService
						.getCandidatesJobs((JobSeekerProfile) currentUserProfile);

				boolean exist;
				for (JobPostActivity jobActivity : jobPost) {
					exist = false;
					for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
						if (Objects.equals(jobActivity.getJobPostId(), jobSeekerApply.getJob().getJobPostId())) {
							jobActivity.setIsActive(true);
							exist = true;
							break;
						}
					}
					if (!exist) {
						jobActivity.setIsActive(false);
					}
					model.addAttribute("jobPost", jobPost);

				}
			}
		}

		model.addAttribute("user", currentUserProfile);

		return "dashboard";
	}

	@GetMapping("/dashboard/add")
	public String addJobs(Model model) {
		model.addAttribute("jobPostActivity", new JobPostActivity());
		model.addAttribute("user", usersService.getCurrentUserProfile());
		return "add-jobs";
	}

	@PostMapping("/dashboard/addNew")
	public String addNew(JobPostActivity jobPostActivity, Model model) {

		Users user = usersService.getCurrentUser();
		if (user != null) {
			jobPostActivity.setPostedById(user);
		}
		jobPostActivity.setPostedDate(new Date());
		model.addAttribute("jobPostActivity", jobPostActivity);
		jobPostActivityService.addNew(jobPostActivity);
		return "redirect:/dashboard/";
	}

	@GetMapping("dashboard/edit/{id}")
	public String editJob(@PathVariable int id, Model model) {

		JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
		model.addAttribute("jobPostActivity", jobPostActivity);
		model.addAttribute("user", usersService.getCurrentUserProfile());
		return "add-jobs";
	}

}
