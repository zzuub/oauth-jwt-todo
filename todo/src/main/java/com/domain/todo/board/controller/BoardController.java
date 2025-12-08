package com.domain.todo.board.controller;

import com.domain.todo.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//todo
/*
getTodoList()       // 목록   완료
getTodoListCnt()    // 개수   완료
getTodoDetail()     // 상세   완료
insertTodo()        // 등록   완료
updateTodo()        // 수정   완료
deleteTodo()        // 삭제   완료
⭐ updateTodoStatus()  // 완료/미완료 토글 (자주 씀!)
viewCnt() // 조회수
test
*/

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/getTodoList")
    /*public Map<String, Object> getTodoList(@RequestParam Map<String,Object> param) {
        return boardService.getTodoList(param);
    }*/
    public Map<String, Object> getTodoList( @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> param = new HashMap<>();
        param.put("offset", (page - 1) * pageSize);
        param.put("pageSize", pageSize);
        return boardService.getTodoList(param);
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

    @DeleteMapping("/todos/{todoId}")
    public Map<String, Object> deleteTodo(@PathVariable int todoId) {
        return boardService.deleteTodo(todoId);
    }


}
