package com.domain.todo.board.controller;

import com.domain.todo.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/getTodoList")
    public Map<String, Object> getTodoList(@AuthenticationPrincipal OAuth2User principal,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam(required = false) String search,
                                           @RequestParam(required = false) Integer status) {
        String userId = principal.getAttribute("sub").toString();

        Map<String, Object> param = new HashMap<>(Map.of(
                "userId", userId,
                "offset", (page - 1) * pageSize,
                "pageSize", pageSize
        ));

        if (search != null && !search.trim().isEmpty()) {
            param.put("search", search.trim());
        }
        if (status != null) {
            param.put("status", status);
        }

        Map<String, Object> result = boardService.getTodoList(param);
        int totalCnt = boardService.getTodoListCnt(param);

        result.putAll(Map.of(
                "currentPage", page,
                "totalCnt", boardService.getTodoListCnt(param),
                "totalPages", (int) Math.ceil((double) totalCnt / pageSize)
        ));
        return result;
    }

    @GetMapping("/todos/{todoId}")
    public Map<String, Object> getTodoDetail(@AuthenticationPrincipal OAuth2User principal,
                                             @PathVariable int todoId) {
        String userId = principal.getAttribute("sub").toString();
        return boardService.getTodoDetail(todoId, userId);
    }

    @PostMapping("/todos")
    public Map<String, Object> addTodo(@AuthenticationPrincipal OAuth2User principal,
                                       @RequestBody Map<String, Object> param) {
        String userId = principal.getAttribute("sub").toString();
        param.put("userId", userId);
        return boardService.addTodo(param);
    }

    @PutMapping("/todos/{todoId}")
    public Map<String, Object> updateTodo(@AuthenticationPrincipal OAuth2User principal,
                                          @PathVariable int todoId,
                                          @RequestBody Map<String, Object> param) {
        String userId = principal.getAttribute("sub").toString();

        param.put("todoId", todoId);
        param.put("userId", userId);
        return boardService.updateTodo(param);
    }

    @PatchMapping("/todos/{todoId}/status")
    public Map<String, Object> updateTodoStatus(@AuthenticationPrincipal OAuth2User principal,
                                                @PathVariable int todoId) {
        String userId = principal.getAttribute("sub").toString();

        Map<String, Object> todo = boardService.getTodoDetail(todoId, userId);

        int currentStatus = ((Number) todo.get("completed_yn")).intValue();
        int newStatus = currentStatus == 1 ? 0 : 1;

        Map<String, Object> param = Map.of(
                "userId", userId,
                "todoId", todoId,
                "completed_yn", newStatus
        );
        return boardService.updateTodoStatus(param);
    }

    @DeleteMapping("/todos/{todoId}")
    public Map<String, Object> deleteTodo(@AuthenticationPrincipal OAuth2User principal,
                                          @PathVariable int todoId) {
        String userId = principal.getAttribute("sub").toString();
        return boardService.deleteTodo(todoId, userId);
    }

}
