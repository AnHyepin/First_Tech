package com.example.demo.controller.sangin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ApplicationDto;
import com.example.demo.dto.CompanyDto;
import com.example.demo.dto.UserToApplicationBookmarkDto;
import com.example.demo.dto.UserToCompanyBookmarkDto;
import com.example.demo.service.sangin.ApplicationService;
import com.example.demo.service.sangin.BookmarkedApplicationService;
import com.example.demo.service.sangin.CompanyApplicationManagementService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/sangin")
public class SanginController {
	@Value("${spring.servlet.multipart.location:./uploads}")
	private String uploadPath;
	@Autowired
	ApplicationService applicationService;
	@Autowired
	BookmarkedApplicationService bookmarkedApplicationService;
	@Autowired
	CompanyApplicationManagementService companyApplicationService;

	// 메인은 상인 템플릿의 sum으로 갑니다
	@RequestMapping("/")
	String main() {
		return "sangin/sum";
	}

	@RequestMapping("/test")
	String test(Model model) {
		System.out.println("공고 페이지로 들어갑니다~~");
		// 전체 공고랑 북마크 된 공고
		List<ApplicationDto> applicationList = applicationService.getApplicationList("user001");
		// 기업 북마크
		List<String> companyList = applicationService.getBookmarkedCompany("user001");
		System.out.println("공고 페이지 전체 리스트 = " + applicationList);
		model.addAttribute("applicationList", applicationList);
		model.addAttribute("companyList", companyList);

		return "sangin/test";
	}

	// 공고 리스트 페이지
	@RequestMapping("/applicationsForm")
	String applicationsForm(Model model) {
		System.out.println("공고 페이지로 들어갑니다~~");
		// 전체 공고랑 북마크 된 공고
		List<ApplicationDto> applicationList = applicationService.getApplicationList("user001");
		// 기업 북마크
		List<String> companyList = applicationService.getBookmarkedCompany("user001");
		System.out.println("공고 페이지 전체 리스트 = " + applicationList);
		model.addAttribute("applicationList", applicationList);
		model.addAttribute("companyList", companyList);

		return "sangin/applicationsForm";
	}

	// 공고 상세 정보 페이지
	@RequestMapping("/applicationDetailForm/{applicationNum}")
	public String applicationDetailForm(@PathVariable("applicationNum") int applicationNum, Model model) {
		System.out.println("디테일 들어오는 것엔 문제가 없습니다");// 확인 완료
		System.out.println("Application Number: " + applicationNum);
		ApplicationDto application = applicationService.getApplication(applicationNum);
		List<String> companyList = applicationService.getBookmarkedCompany("user001");
		model.addAttribute("jobApplication", application);
		model.addAttribute("companyList", companyList);
		return "sangin/applicationDetailForm";
	}

	// 관심 공고 페이지
	@RequestMapping("/bookmarkedApplicationsForm")
	public String bookmarkedApplicationsForm(Model model) {
		// 세션에서 유저 정보를 받아와야합니다 //일단은 여기서 유저 번호를 임의로 지정합니다
		System.out.println("관심 공고 들어오는 것엔 문제가 없습니다");// 확인 완료
		String userId = "user001";
		List<ApplicationDto> applicationList = bookmarkedApplicationService.getBookmarkedApplicationList(userId);
		List<String> companyList = applicationService.getBookmarkedCompany(userId);
		model.addAttribute("applicationList", applicationList);
		model.addAttribute("companyList", companyList);
		return "/sangin/bookmarkedApplicationsForm";
	}

	// 인재풀
	@RequestMapping("/resumePoolForm")
	String resumePoolForm() {
		return "sangin/resumePoolForm";
	}

	// 공고 북마크 추가
	@RequestMapping("/addHeart")
	@ResponseBody
	String addHeart(@RequestBody UserToApplicationBookmarkDto dto) {
		System.out.println("addHeart...");
		System.out.println("userId = " + dto.getUserId());
		System.out.println("applicationNum = " + dto.getApplicationNum());

		applicationService.addHeart(dto);
		return "공고 북마크 추가 성공";
	}

	// 공고 북마크 제거
	@RequestMapping("/removeHeart")
	@ResponseBody
	String removeHeart(@RequestBody UserToApplicationBookmarkDto dto) {
		System.out.println("removeHeart...");
		System.out.println("userId = " + dto.getUserId());
		System.out.println("applicationNum = " + dto.getApplicationNum());
		applicationService.removeHeart(dto);
		return "공고 북마크 제거 성공";
	}

	// 기업 북마크 추가
	@RequestMapping("/addStar")
	@ResponseBody
	String addStar(@RequestBody UserToCompanyBookmarkDto dto) {
		System.out.println("addStar...");
		System.out.println("userId = " + dto.getUserId());
		System.out.println("CompanyId = " + dto.getCompanyId());
		applicationService.addStar(dto);
		return "기업 북마크 추가 성공";
	}

	// 기업 북마크 제거
	@RequestMapping("/removeStar")
	@ResponseBody
	String removeStar(@RequestBody UserToCompanyBookmarkDto dto) {
		System.out.println("removeStar...");
		System.out.println("userId = " + dto.getUserId());
		System.out.println("CompanyId = " + dto.getCompanyId());
		applicationService.removeStar(dto);
		return "기업 북마크 제거 성공";
	}

	// 기업 공고 관리 폼
	@RequestMapping("/companyApplicationManagementForm/{pageNum}")
	String companyApplicationManagementForm(@PathVariable("pageNum") int pageNum, Model model, HttpSession session) {
		CompanyDto company = (CompanyDto) session.getAttribute("company");
		String companyId = company.getCompanyId();
		int limit = 10;
		int startIdx = (pageNum - 1) * 10;
		List<ApplicationDto> companyApplicationList = companyApplicationService.companyApplicationListLimit(companyId,
				startIdx, limit);
		int totalApplication = companyApplicationService.getApplicationCount(companyId); // 전체 이력서 개수
		int totalPages = (int) Math.ceil((double) totalApplication / limit); // 10개 // 10개 나누기 //올림

		model.addAttribute("currentPage", pageNum); // 현재 페이지
		System.out.println("pageNum = " + pageNum);
		model.addAttribute("totalPages", totalPages); // 전체 페이지 수
		System.out.println("totalPages = " + totalPages);
		model.addAttribute("companyApplicationList", companyApplicationList); // 해당하는 공고 보내기
		System.out.println("해당공고 = " + companyApplicationList);
		model.addAttribute("companyId", companyId); // 회사 아이디 보내기
		System.out.println("companyId = " + companyId);
		return "sangin/companyApplicationManagementForm";
	}

	// 기업 공고 작성 폼
	@RequestMapping("/insertApplicationForm/{companyId}")
	String insertApplicationForm(@PathVariable("companyId") String companyId, Model model) {
		model.addAttribute("companyId", companyId);
		return "sangin/insertApplicationForm";
	}

	// 공고 작성 완료 누름 시 db랑 연동되게끔 한 함수
	@RequestMapping("/insertApplications")
	public String insertApplication(@RequestParam("file") MultipartFile paramFile, ApplicationDto dto) {
		MultipartFile file = paramFile;
		String fileName = file.getOriginalFilename();
		File uploadFile = new File(uploadPath + fileName);
		try {
			file.transferTo(uploadFile);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		dto.setFileName(fileName);// 파일 네임도 dto 에 넣어서 데이터 베이스로 보내기
		int result = companyApplicationService.insertApplication(dto);
		return "sangin/sum";
	}

	// 지역 체크 시 지역에 해당하는 공고 리스트 반환
	@RequestMapping("/searchingByArea")
	@ResponseBody
	Map<String, Object> searchingByArea(@RequestBody String value) {
		System.out.println("area value: " + value); // 요청으로 들어온 값 출력
		List<ApplicationDto> listByArea;

		// value 값에 따라 분기 처리
		if (value.equals("all")) {
			listByArea = applicationService.getApplicationList("user001");
		} else {
			listByArea = applicationService.getApplicationByWorkingArea("user001", value);
		}

		List<String> companyList = applicationService.getBookmarkedCompany("user001");
		System.out.println("공고 지역 리스트 listByArea: " + listByArea); // 가져온 데이터 출력

		Map<String, Object> response = new HashMap<>();
		response.put("listByArea", listByArea);
		response.put("companyList", companyList);

		return response;
	}

	// 직무 체크 시 지역에 해당하는 공고 리스트 반환
	@RequestMapping("/searchingByRoleId")
	@ResponseBody
	Map<String, Object> searchingByRoleId(@RequestBody String value) {
	    System.out.println("roleId value: " + value); // 요청으로 들어온 값 출력
	    List<ApplicationDto> listByRoleId;

	    if ("all".equals(value)) {
	        listByRoleId = applicationService.getApplicationList("user001");
	    } else {
	        listByRoleId = applicationService.getApplicationByRoleId("user001", value);
	    }

	    List<String> companyList = applicationService.getBookmarkedCompany("user001");
	    System.out.println("공고 직무 리스트 listByRoleId: " + listByRoleId); // 가져온 데이터 출력

	    Map<String, Object> response = new HashMap<>();
	    response.put("listByRoleId", listByRoleId);
	    response.put("companyList", companyList);

	    return response;
	}

	// 검색어로 검색하기
	@RequestMapping("/searchingByKeyword")
	@ResponseBody
	Map<String, Object> searchingByKeyword(@RequestBody String keyword) {
	    System.out.println("검색어: " + keyword); // 요청으로 들어온 값 출력
	    List<ApplicationDto> listByKeyword;

	    if (keyword == null || keyword.trim().isEmpty()) {
	        listByKeyword = applicationService.getApplicationList("user001"); // 전체 리스트 반환
	    } else {
	        listByKeyword = applicationService.getApplicationByKeyword("user001", keyword);
	    }

	    List<String> companyList = applicationService.getBookmarkedCompany("user001");
	    System.out.println("공고 키워드 리스트 listByKeyword: " + listByKeyword); // 가져온 데이터 출력

	    Map<String, Object> response = new HashMap<>();
	    response.put("listByKeyword", listByKeyword);
	    response.put("companyList", companyList);

	    return response;
	}



}
