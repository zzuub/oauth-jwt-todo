package com.domain.todo.board.controller;

import com.domain.todo.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//생성자를 자동 생성해 spring 생성자 주입을 처리
//final 키워드가 붙은 필드만 포함하는 생성자를 자동으로 생성.
@RequiredArgsConstructor
//restApi 명확. 한번만 사용해서 반복 없음
//클래스 전체가 api일때 사용
@RestController
//url 요청이 왔을때 어떤 컨트롤러가 호출 되어야 할지 mapping
@RequestMapping("/api")
public class BoardController {
    //불변성 보장, 의존성 누락 즉시 발견 가능
    //private: 클래스 내부에서만 접근 가능 / final: 한번 초기화 후 재할당 불가
    //의존성 주입 : 한 클래스가 다른 클래스나 객체의 사용을 필요로 하는 관계
    private final BoardService boardService;

    @GetMapping("/getTodoList")
    //@RequestParam http 요청 파라미터를 받아서 파라미터 이름으로 바인딩
    public Map<String, Object> getTodoList(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam(required = false) String search,
                                           @RequestParam(required = false) Integer status) {
        //불변 map 생성 메서드
        //고정 파라미터를 간결하고 안전하게 초기화하기 위해
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
        int totalCnt = boardService.getTodoListCnt(param);

        //페이징 정보를 result 맵에 한번에 병합
        //개별 put() 3번 대신 한줄로 작성해 가독성 좋게
        result.putAll(Map.of(
                "currentPage", page,
                "totalCnt", boardService.getTodoListCnt(param),
                "totalPages", (int) Math.ceil((double) totalCnt / pageSize)
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
        //현재 상태를 확인해서 0과1 토글해야 하므로 tode상세를 먼저 조회
        Map<String, Object> todo = boardService.getTodoDetail(todoId);

        //map에서 object -> number->int로 변환
        int currentStatus = ((Number) todo.get("STATUS")).intValue();
        //삼항연산자로 토글 (1완료->0미완료)
        int newStatus = currentStatus == 1 ? 0 : 1;

        Map<String, Object> param = Map.of("todoId", todoId, "status", newStatus);
        return boardService.updateTodoStatus(param);
    }

    @DeleteMapping("/todos/{todoId}")
    public Map<String, Object> deleteTodo(@PathVariable int todoId) {
        return boardService.deleteTodo(todoId);
    }

}
