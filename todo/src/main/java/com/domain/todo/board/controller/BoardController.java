package com.domain.todo.board.controller;

import com.domain.todo.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/getTodoList")
    public Map<String, Object> getTodoList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam(required = false) String search, @RequestParam(required = false) Integer status) {
        Map<String, Object> param = new HashMap<>(Map.of(
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

        result.putAll(Map.of(
                "currentPage", page,
                "totalCnt", boardService.getTodoListCnt(param),
                "totalPages", (int) Math.ceil((double) boardService.getTodoListCnt(param) / pageSize)
        ));
        return result;
    }

    @GetMapping("/todos/{todoId}")
    public Map<String, Object> getTodoDetail(@PathVariable int todoId) {
        return boardService.getTodoDetail(todoId);
    }

    @PostMapping("/todos")
    public Map<String, Object> addTodo(@RequestBody Map<String, Object> param) {
        return boardService.addTodo(param);
    }

    @PutMapping("/todos/{todoId}")
    public Map<String, Object> updateTodo(@PathVariable int todoId, @RequestBody Map<String, Object> param) {
        param.put("todoId", todoId);
        return boardService.updateTodo(param);
    }

    @PatchMapping("/todos/{todoId}/status")
    public Map<String, Object> updateTodoStatus(@PathVariable int todoId) {
        Map<String, Object> todo = boardService.getTodoDetail(todoId);

        int currentStatus = todo.get("STATUS") == Boolean.TRUE ? 1 : 0;
        int newStatus = currentStatus == 1 ? 0 : 1;

        Map<String, Object> param = Map.of("todoId", todoId, "status", newStatus);
        return boardService.updateTodoStatus(param);
    }

    @DeleteMapping("/todos/{todoId}")
    public Map<String, Object> deleteTodo(@PathVariable int todoId) {
        return boardService.deleteTodo(todoId);
    }

}
