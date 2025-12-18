let currentPage = 1;
let currentSearch = '';
let currentStatus = '';
const PAGE_SIZE = 10;
let searchTimeout;
let editingPage = 1;

document.addEventListener('DOMContentLoaded', function() {
    loadTodoLists();

    document.getElementById('searchInput').addEventListener('input', function() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            currentSearch = this.value.trim();
            loadTodoLists(1);
        }, 300);
    });

    document.getElementById('statusFilter').addEventListener('change', function() {
        currentStatus = this.value;
        loadTodoLists(1);
    });

    document.getElementById('searchInput').addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            if (!this.value.trim()) {
                alert('검색어를 입력하세요! 🔍');
                return;
            }
            currentSearch = this.value.trim();
            loadTodoLists(1);
        }
    });
});

function googleLogin() {
    window.location.href = "/oauth2/authorization/google";
}

function loadTodoLists(page = 1) {
    currentPage = page;

    const params = new URLSearchParams({
        page: page,
        pageSize: PAGE_SIZE
    });
    if (currentSearch) params.append('search', currentSearch);
    if (currentStatus) params.append('status', currentStatus);

    fetch(`/api/getTodoList?${params}`)
        .then(response => {
            if (response.status === 401) {
                if (confirm("로그인이 필요합니다. 구글 로그인 하시겠습니까?")) {
                    window.location.href = "/oauth2/authorization/google";
                }
                throw new Error('UNAUTHORIZED');
            }
            return response.json();
        })
        .then(data => {
            displayTodoLists(data);
        })
        .catch(err => {
            if (err.message === 'UNAUTHORIZED') {
                return;
            }
            document.getElementById('todoLists').innerHTML =
                '<p style="text-align:center;color:white;font-size:18px;padding:40px;">데이터 로드 실패</p>';
        });
}

function displayTodoLists(data) {
    const todos = data.data || [];
    const container = document.getElementById('todoLists');

    if (todos.length === 0) {
        container.innerHTML = '<p style="text-align:center;color:white;font-size:18px;padding:40px;">할 일이 없습니다.</p>';
        return;
    }

    // completed_yn으로 필터링 (0: 진행중, 1: 완료)
    const ongoingTodos = todos.filter(todo => todo.completed_yn == 0);
    const completedTodos = todos.filter(todo => todo.completed_yn == 1)
        .sort((a, b) => new Date(b.registDate) - new Date(a.registDate));
    const sortedTodos = [...ongoingTodos, ...completedTodos];

    const totalCnt = data.totalCnt || 0;
    const totalPages = data.totalPages || 0;

    const searchInfo = currentSearch || currentStatus ?
        `<span style="font-size:0.7em;color:#666;">🔍 ${currentSearch || ''}${currentStatus ? (currentSearch ? ' + ' : '') + (currentStatus == 0 ? '미완료' : '완료') : ''}</span>` : '';

    container.innerHTML = `
        <div class="list-card">
            <div class="list-header">
                <div class="list-title">📝 할 일 목록 
                    <span style="font-size:0.8em;color:#d68910;">
                        (${totalCnt}개) ${searchInfo}
                    </span>
                </div>
                ${(currentSearch || currentStatus) ? `
                <div class="search-reset" onclick="resetSearch()">
                    <i class="fas fa-times"></i> 초기화
                </div>
                ` : ''}
            </div>
            <div class="todo-container">
                <table class="todo-table">
                    <thead>
                        <tr>
                            <th style="width: 45%;">할 일</th>
                            <th style="width: 15%;">작성일</th>
                            <th style="width: 15%;">상태</th>
                            <th style="width: 25%;">작업</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${sortedTodos.map(todo => `
                            <tr class="todo-row ${todo.completed_yn == 1 ? 'completed' : ''}">
                                <td data-label="할 일" class="todo-name">${todo.content || '-'}</td>
                                <td data-label="작성일">${todo.registDate}</td>
                                <td data-label="상태">${todo.completed_yn == 1 ? '✅ 완료' : '⏳ 진행중'}</td>
                                <td data-label="작업" class="todo-actions">
                                    <button class="done-btn" onclick="toggleTodo(${todo.todo_id}, ${todo.completed_yn})">
                                        ${todo.completed_yn == 1 ? '↩️ 취소' : '✅ 완료'}
                                    </button>
                                    <button class="edit-todo" onclick="editTodo(${todo.todo_id}, ${currentPage})">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="delete-todo" onclick="deleteTodo(${todo.todo_id})">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>

                <div class="list-pagination">
                    <button class="page-btn"
                            onclick="loadTodoLists(${Math.max(1, currentPage-1)})"
                            ${currentPage === 1 ? 'disabled' : ''}>
                        <i class="fas fa-chevron-left"></i> 이전
                    </button>
                    <span class="page-info">
                        ${currentPage}/${totalPages}                   
                    </span>
                    <button class="page-btn"
                            onclick="loadTodoLists(${currentPage+1})"
                            ${currentPage >= totalPages ? 'disabled' : ''}>
                        다음 <i class="fas fa-chevron-right"></i>
                    </button>
                </div>
            </div>
        </div>
    `;
}

function resetSearch() {
    currentSearch = '';
    currentStatus = '';
    document.getElementById('searchInput').value = '';
    document.getElementById('statusFilter').value = '';
    loadTodoLists(1);
}

function addTodo() {
    const content = document.getElementById('createContent').value.trim();
    if (!content) { alert('할 일을 입력하세요!'); return; }
    if (content.length > 1000) { alert('내용은 1000자 이내로 입력하세요!'); return; }

    fetch('/api/todos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content, completed_yn: 0 })
    })
        .then(() => {
            loadTodoLists(1);
        })
        .catch(err => alert('추가 실패: ' + err));
}

function updateTodo(todoId, originalPage) {
    const newContent = document.getElementById('editContent').value.trim();
    if (!newContent) { alert('할 일을 입력하세요!'); return; }

    fetch(`/api/todos/${todoId}`)
        .then(response => response.json())
        .then(todo => {
            if (newContent === (todo.content || '').trim()) {
                alert('변경사항이 없습니다!');
                loadTodoLists(originalPage || currentPage);
                return;
            }

            fetch(`/api/todos/${todoId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ content: newContent })
            })
                .then(() => loadTodoLists(originalPage || currentPage))
                .catch(err => alert('수정 실패: ' + err));
        })
        .catch(err => alert('할 일 정보 조회 실패: ' + err));
}

function toggleTodo(todoId, completedYn) {
    fetch(`/api/todos/${todoId}/status`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ completedYn: completedYn})
    })
        .then(() => loadTodoLists(currentPage))
        .catch(err => alert('상태 변경 실패: ' + err));
}

function deleteTodo(todoId) {
    if (confirm('정말 삭제하시겠습니까?')) {
        fetch(`/api/todos/${todoId}`, { method: 'DELETE' })
            .then(() => loadTodoLists(currentPage))
            .catch(err => alert('삭제 실패: ' + err));
    }
}

function showCreateTodo() {
    document.getElementById('todoLists').innerHTML = createTodoModalHTML();
    document.getElementById('createTodoForm').onsubmit = function(e) {
        e.preventDefault();
        addTodo();
    };
}

function editTodo(todoId, page) {
    editingPage = page || currentPage;

    fetch(`/api/todos/${todoId}`)
        .then(response => response.json())
        .then(todo => {
            document.getElementById('todoLists').innerHTML = editTodoModalHTML(todo);

            document.getElementById('editTodoForm').onsubmit = function(e) {
                e.preventDefault();
                updateTodo(todoId, editingPage);
            };
        })
        .catch(err => alert('상세 조회 실패: ' + err));
}

function createTodoModalHTML() {
    return `
        <div class="edit-modal">
            <div class="edit-header">
                <h2><i class="fas fa-plus"></i> 새 할 일 추가</h2>
            </div>
            <form id="createTodoForm" class="edit-form">
                <textarea id="createContent" rows="6" placeholder="할 일을 입력하세요..." 
                          class="edit-textarea" required maxlength="1000"></textarea>
                <div class="edit-buttons">
                    <button type="button" class="edit-btn-cancel" onclick="loadTodoLists(currentPage)">취소</button>
                    <button type="submit" class="edit-btn-save">추가하기</button>
                </div>
            </form>
        </div>
    `;
}

function editTodoModalHTML(todo) {
    return `
        <div class="edit-modal">
            <div class="edit-header">
                <h2><i class="fas fa-edit"></i> 할 일 수정</h2>
            </div>
            <form id="editTodoForm" class="edit-form">
                <input type="hidden" id="editTodoId" value="${todo.todo_id}">
                <textarea id="editContent" rows="6" placeholder="할 일을 입력하세요..." 
                          class="edit-textarea" required maxlength="1000">${todo.content || ''}</textarea>
                <div class="edit-buttons">
                    <button type="button" class="edit-btn-cancel" onclick="loadTodoLists(editingPage)">취소</button>
                    <button type="submit" class="edit-btn-save">수정완료</button>
                </div>
            </form>
        </div>
    `;
}
