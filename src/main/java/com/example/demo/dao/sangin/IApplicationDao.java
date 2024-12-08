package com.example.demo.dao.sangin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.ApplicationDto;
import com.example.demo.dto.UserToApplicationBookmarkDto;
import com.example.demo.dto.UserToCompanyBookmarkDto;

@Mapper
public interface IApplicationDao {
	public List<ApplicationDto> getApplicationList1();

	List<ApplicationDto> getApplicationList(@Param("userId") String userId);

	int addHeart(UserToApplicationBookmarkDto dto);

	int removeHeart(UserToApplicationBookmarkDto dto);

	int addStar(UserToCompanyBookmarkDto dto);

	int removeStar(UserToCompanyBookmarkDto dto);

	List<String> getBookmarkedCompany(String userId);

	ApplicationDto getApplication(int applicationNum);

	List<ApplicationDto> getApplicationByWorkingArea(@Param("userId") String userId,@Param("workingArea") String workingArea);

	List<ApplicationDto> getApplicationByRoleId(@Param("userId") String userId,@Param("roleId") String roleId);

	List<ApplicationDto> getApplicationByKeyword(@Param("userId") String userId, @Param("keyword") String keyword);

}
