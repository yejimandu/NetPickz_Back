package com.netpickz.api.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netpickz.api.user.request.UserRequest;
import com.netpickz.common.enumType.StateType;
import com.netpickz.core.user.dto.UserDTO;
import com.netpickz.core.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
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

		if("me".equals(userId)) {
			userId = authentication.getName();
		}
		var userDto = userService.getUserInfoByUserId(userId);
		return ResponseEntity.status(HttpStatus.OK).body(userDto.orElse(null));
	}
	
	@Operation(summary = "사용자 생성", description = "요청 정보 기준으로 사용자 정보를 생성합니다.")
	@PostMapping("")
	public ResponseEntity<UserDTO> createUser(
			@org.springframework.web.bind.annotation.RequestBody UserRequest request) {
		var userDto = userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(userDto.orElse(null));
	}
	
	@Operation(summary = "사용자 정보 수정", description = "요청 정보 기준으로 사용자 정보를 수정합니다.")
	@PutMapping("")
	public ResponseEntity<UserDTO> updateUser(
			@org.springframework.web.bind.annotation.RequestBody UserRequest request) {
		// TODO
		var userDto = userService.updateUser(request);
		return ResponseEntity.status(HttpStatus.OK).body(userDto.orElse(null));
	}
	
	@Operation(summary = "사용자 히스토리 정보 조회", description = "사용자 ID 기준으로 히스토리 내역을 조회합니다.")
	@GetMapping("/{userId}/history")
	public ResponseEntity<List<UserDTO>> getHistoryByUserId(
			@PathVariable(name = "userId") String userId) {
		// TODO
		var userDto =  userService.getHistoryByUserId(userId);
		return ResponseEntity.status(HttpStatus.OK).body(userDto.orElse(null));
	}
	
	@Operation(summary = "사용자 상태 변경", description = "사용자 ID 기준으로 사용자 상태를 변경합니다.")
	@Parameter(name = "stateType" , required = true, description = "사용자 상태 타입")
	@GetMapping("/{userId}/state")
	public ResponseEntity<String> updateUserState(
			@PathVariable(name = "userId") String userId,
			@RequestParam(name = "stateType") StateType type) {
		// TODO
		var val =  userService.updateUserState(userId, type);
		return  new ResponseEntity<String>(val == 1 ? "완" : " 놉", HttpStatus.OK);
	}
	
}
