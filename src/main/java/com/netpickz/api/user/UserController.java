package com.netpickz.api.user;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.api.user.request.UserRequest;
import com.netpickz.common.constants.Constants;
import com.netpickz.common.enumType.StateType;
import com.netpickz.core.movie.dto.PageDTO;
import com.netpickz.core.movie.dto.RatingDTO;
import com.netpickz.core.user.dto.UserDTO;
import com.netpickz.core.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

//@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "사용자 관련 기능을 제공하는 컨트롤러")
public class UserController {

	private final UserService userService;

	@Operation(summary = "사용자 정보 조회", description = "사용자 ID 기준으로 사용자 정보를 조회합니다.")
	@GetMapping("/{userId}")
	public ResponseEntity<UserDTO> getUserInfo(
			@PathVariable(name = "userId") String userId, Authentication authentication) {
		if(Constants.ME.equals(userId)) {
			userId = authentication.getName();
		}
		var userDto = userService.getUserInfoByUserId(userId);
		return ResponseEntity.ok(userDto.orElse(null));
	}
	
	@Operation(summary = "사용자 생성", description = "요청 정보 기준으로 사용자 정보를 생성합니다.")
	@PostMapping("")
	public ResponseEntity<UserDTO> createUser(
			@org.springframework.web.bind.annotation.RequestBody UserRequest request) {
		log.info("body={}", request);
		var userDto = userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(userDto.orElse(null));
	}
	
	@Operation(summary = "사용자 정보 수정", description = "요청 정보 기준으로 사용자 정보를 수정합니다.")
	@PatchMapping("/{userId}") 
	public ResponseEntity<UserDTO> updateUser(
			@PathVariable(name = "userId") String userId,
			@org.springframework.web.bind.annotation.RequestBody UserRequest request) {
		request.setUserId(userId);
		var userDto = userService.updateUser(request);
		return ResponseEntity.ok(userDto.orElse(null));
	}
	
//	CompletableFuture<ResponseEntity<PageDTO>>
	@Operation(summary = "사용자 히스토리 정보 조회", description = "사용자 ID 기준으로 히스토리 내역을 조회합니다.")
	@GetMapping("/history")
	public ResponseEntity<PageDTO<RatingDTO>> getHistoryByUserId(
			Authentication authentication, 
			@RequestParam(name = "keyword", required = false) String keyword,
			@ParameterObject
		    @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
		    Pageable pageable) {
		var	userId = authentication.getName();
		var ratingDto =  userService.getHistoryByUserId(userId, keyword, pageable);
		return ResponseEntity.ok(ratingDto);
	}
	
	@Operation(summary = "사용자 상태 변경", description = "사용자 ID 기준으로 사용자 상태를 변경합니다.")
	@Parameter(name = "stateType" , required = true, description = "사용자 상태 타입")
	@PatchMapping("/{userId}/state")
	public ResponseEntity<String> updateUserState(
			@PathVariable(name = "userId") String userId,
			@RequestParam(name = "stateType") StateType type) {
		
		var msg =  userService.updateUserState(userId, type);
		return ResponseEntity.ok(msg);
	}
	
	@Operation(summary = "비밀번호 재설정 링크 검증", description = "비밀번호 재설정 링크를 검증합니다.")
	@GetMapping("/pw/reset/verify")
	public ResponseEntity<String> pwResetVerify(
			Authentication authentication, HttpServletRequest request) {
		var userId = authentication.getName(); 
		var token = request.getHeader("Authorization").substring(7);
		var msg = userService.pwResetVerify(userId , token);
		return ResponseEntity.ok(msg);
	}
	
	@Operation(summary = "이메일 인증 코드 검증", description = "이메일 인증 코드 검증합니다.")
	@PatchMapping("/pw/reset")
	public ResponseEntity<String> pwReset(
			@org.springframework.web.bind.annotation.RequestBody UserRequest request, Authentication authentication) {
		var userId = authentication.getName();
		var msg = userService.passwordChange(userId , request.getNewPassword());
		return ResponseEntity.ok(msg);
	}
	
}
