<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import {
  BookOpen,
  LayoutDashboard,
  Library,
  Users,
  Settings,
  Search,
  Plus,
  Pencil,
  Trash2,
  ChevronLeft,
  ChevronRight,
  Menu,
  X,
  Sparkles,
} from "lucide-vue-next";

const books = ref([]),
  query = ref(""),
  category = ref("全部分类"),
  showForm = ref(false),
  editing = ref(null),
  mobileOpen = ref(false),
  showRecords = ref(false),
  borrowRecords = ref([]),
  selectedRecord = ref(null),
  showReturnConfirm = ref(false),
  returnError = ref(""),
  showDeleteAccount = ref(false),
  deleteError = ref(""),
  showDeleteBook = ref(false),
  selectedDeleteBook = ref(null);
const page = ref(1),
  pageSize = 10,
  total = ref(0),
  libraryTotal = ref(0),
  pages = ref(1);
const activeMetric = ref("all");
// 登录状态仅保存在当前页面内，刷新或重新打开页面后必须重新登录。
const loginUser = ref(null);
const loginForm = ref({ username: "admin", password: "" }),
  loginError = ref("");
const registerMode = ref(false),
  registerForm = ref({ username: "", password: "", confirmPassword: "" }),
  showLogoutConfirm = ref(false),
  showBorrowConfirm = ref(false),
  selectedBook = ref(null),
  borrowSuccess = ref(false),
  borrowError = ref("");
const displayUsername = computed(() => loginUser.value?.username || "管理员");
const isAdmin = computed(
  () =>
    loginUser.value?.role === "ADMIN" || loginUser.value?.username === "admin",
);
const avatarText = computed(() => {
  const name = displayUsername.value.trim();
  return name.length <= 2 ? name : name.slice(0, 2).toUpperCase();
});
const now = ref(new Date());
const weekdays = [
  "星期日",
  "星期一",
  "星期二",
  "星期三",
  "星期四",
  "星期五",
  "星期六",
];
const currentDate = computed(() => {
  const date = now.value;
  return `${weekdays[date.getDay()]}，${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
});
function formatDateTime(value) {
  return value ? String(value).replace("T", " ").slice(0, 16) : "—";
}
let clockTimer;
const form = ref({
  title: "",
  author: "",
  isbn: "",
  category: "文学",
  publisher: "",
  year: 2024,
  stock: 1,
  description: "",
});
const categories = ["全部分类", "历史文学", "科幻小说", "推理小说", "言情小说"];
const filteredBooks = computed(() =>
  books.value.filter((b) => {
    const matchBase =
      (category.value === "全部分类" || b.category === category.value) &&
      [b.title, b.author].some((v) =>
        (v || "").toLowerCase().includes(query.value.toLowerCase()),
      );
    const matchMetric =
      activeMetric.value === "all" ||
      (activeMetric.value === "available" && b.stock > 0) ||
      (activeMetric.value === "borrowed" && b.stock <= 0) ||
      (activeMetric.value === "new" &&
        b.createTime &&
        new Date(b.createTime).getMonth() === new Date().getMonth() &&
        new Date(b.createTime).getFullYear() === new Date().getFullYear());
    return matchBase && matchMetric;
  }),
);
const stats = computed(() => ({ total: libraryTotal.value }));
async function load() {
  const scrollY = window.scrollY;
  try {
    const categoryId =
      category.value === "全部分类" ? "" : categories.indexOf(category.value);
    const params = new URLSearchParams({ page: page.value, size: pageSize });
    if (query.value.trim()) params.set("keyword", query.value.trim());
    if (categoryId) params.set("categoryId", categoryId);
    const r = await fetch(`/api/books?${params}`);
    if (r.ok) {
      const data = await r.json();
      total.value = data.total || 0;
      pages.value = data.pages || 1;
      books.value = (data.list || []).map((b) => ({
        ...b,
        title: b.bookName,
        category:
          ["", "历史文学", "科幻小说", "推理小说", "言情小说"][b.categoryId] ||
          "未分类",
        totalStock: b.stock,
        isbn: "",
        publisher: "",
        year: "",
        description: "",
      }));
      await nextTick();
      window.scrollTo({ top: scrollY, behavior: "auto" });
    }
  } catch {
    books.value = [];
  }
}
async function refreshLibraryTotal() {
  try {
    const r = await fetch("/api/books?page=1&size=1");
    if (r.ok) libraryTotal.value = (await r.json()).total || 0;
  } catch {}
}
function changePage(next) {
  if (next < 1 || next > pages.value) return;
  page.value = next;
  load();
}
watch([query, category], () => {
  page.value = 1;
  load();
});
watch(activeMetric, () => load());
watch(books, refreshLibraryTotal);
function reset() {
  form.value = {
    title: "",
    author: "",
    isbn: "",
    category: "文学",
    publisher: "",
    year: 2024,
    stock: 1,
    description: "",
  };
  editing.value = null;
}
function openEdit(b) {
  editing.value = b.id;
  form.value = { ...b };
  showForm.value = true;
}
async function save() {
  const method = editing.value ? "PUT" : "POST";
  const url = editing.value ? `/api/books/${editing.value}` : "/api/books";
  const payload = {
    bookName: form.value.title,
    author: form.value.author,
    price: Number(form.value.price || 0),
    categoryId: categories.indexOf(form.value.category),
    stock: Number(form.value.stock || 0),
  };
  try {
    const r = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    if (r.ok) {
      await load();
      showForm.value = false;
      reset();
    }
  } catch {
    const item = {
      ...form.value,
      id: editing.value || Date.now(),
      totalStock: form.value.stock,
    };
    books.value = editing.value
      ? books.value.map((b) => (b.id === editing.value ? item : b))
      : [item, ...books.value];
    showForm.value = false;
    reset();
  }
}
function requestDeleteBook(book) {
  selectedDeleteBook.value = book;
  showDeleteBook.value = true;
}
async function confirmDeleteBook() {
  const book = selectedDeleteBook.value;
  if (!book) return;
  try {
    const r = await fetch(`/api/books/${book.id}`, { method: "DELETE" });
    if (r.ok) {
      showDeleteBook.value = false;
      selectedDeleteBook.value = null;
      await load();
    }
  } catch {
    books.value = books.value.filter((b) => b.id !== book.id);
    showDeleteBook.value = false;
    selectedDeleteBook.value = null;
  }
}
function borrowBook(bookToBorrow) {
  const book = bookToBorrow || books.value.find((b) => b.stock > 0);
  if (!book) {
    alert("当前没有可借阅图书");
    return;
  }
  selectedBook.value = book;
  borrowSuccess.value = false;
  borrowError.value = "";
  showBorrowConfirm.value = true;
}
async function confirmBorrow() {
  const book = selectedBook.value;
  const r = await fetch("/api/borrow-records", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ bookId: book.id, readerId: loginUser.value.id }),
  });
  if (r.ok) {
    borrowSuccess.value = true;
    borrowError.value = "";
    await load();
  } else {
    const data = await r.json().catch(() => ({}));
    borrowError.value = data.message || "借阅失败，请稍后重试";
  }
}
async function loadRecords() {
  const r = await fetch("/api/borrow-records");
  if (r.ok) {
    const data = await r.json();
    const readerId = loginUser.value.id;
    borrowRecords.value = data
      .filter(
        (x) =>
          isAdmin.value ||
          String(x.readerId ?? x.READER_ID ?? x.reader_id) === String(readerId),
      )
      .map((x) => {
        const b = books.value.find(
          (book) =>
            String(book.id) === String(x.bookId ?? x.BOOK_ID ?? x.book_id),
        );
        return {
          ...x,
          bookName: b?.title || "未知图书",
          stock: b?.stock ?? "-",
          borrowerUsername:
            x.borrowerUsername ??
            x.borrower_username ??
            x.BORROWERUSERNAME ??
            "未知用户",
          borrowTime: x.borrowTime ?? x.borrow_time ?? x.BORROW_TIME,
          dueTime: x.dueTime ?? x.due_time ?? x.DUE_TIME,
          status: x.status ?? x.STATUS,
          id: x.id ?? x.ID,
        };
      });
  }
}
function openRecords() {
  showRecords.value = true;
  loadRecords();
}
function returnBook(record) {
  selectedRecord.value = record;
  returnError.value = "";
  showReturnConfirm.value = true;
}
async function confirmReturn() {
  const record = selectedRecord.value;
  const id = record.id ?? record.ID;
  const r = await fetch(`/api/borrow-records/${id}/return`, { method: "POST" });
  if (r.ok) {
    showReturnConfirm.value = false;
    await load();
    await loadRecords();
  } else {
    returnError.value =
      (await r.json().catch(() => ({}))).message || "归还失败，请稍后重试";
  }
}
async function login() {
  loginError.value = "";
  const r = await fetch("/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(loginForm.value),
  });
  if (!r.ok) {
    loginError.value = "用户名或密码错误";
    return;
  }
  loginUser.value = await r.json();
  load();
}
function logout() {
  showLogoutConfirm.value = true;
}
function requestDeleteAccount() {
  deleteError.value = "";
  showDeleteAccount.value = true;
}
async function confirmDeleteAccount() {
  const id = loginUser.value?.id;
  const r = await fetch(`/api/auth/users/${id}`, { method: "DELETE" });
  if (r.ok) {
    showDeleteAccount.value = false;
    localStorage.removeItem("libraryUser");
    loginUser.value = null;
    loginForm.value = { username: "", password: "" };
  } else {
    deleteError.value =
      (await r.json().catch(() => ({}))).message || "注销失败，请稍后重试";
  }
}
function confirmLogout() {
  showLogoutConfirm.value = false;
  localStorage.removeItem("libraryUser");
  loginUser.value = null;
  loginForm.value = { username: "", password: "" };
  loginError.value = "";
}
async function register() {
  loginError.value = "";
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    loginError.value = "两次输入的密码不一致";
    return;
  }
  const r = await fetch("/api/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(registerForm.value),
  });
  if (!r.ok) {
    const d = await r.json().catch(() => ({}));
    loginError.value =
      r.status === 409
        ? "用户名已存在，请更换用户名"
        : d.message || d.error || "注册失败，请检查注册信息";
    return;
  }
  registerMode.value = false;
  loginForm.value.username = registerForm.value.username;
  registerForm.value = { username: "", password: "", confirmPassword: "" };
  loginError.value = "注册成功，请登录";
}
onMounted(() => {
  const input = document.querySelector(".search input");
  if (input) input.placeholder = "搜索书名或作者...";
  document.querySelectorAll(".stat").forEach((card, index) =>
    card.addEventListener("click", () => {
      activeMetric.value = ["all", "available", "borrowed", "new"][index];
      page.value = 1;
    }),
  );
  if (loginUser.value) load();
});
onMounted(() => {
  clockTimer = window.setInterval(() => {
    now.value = new Date();
  }, 60000);
});
onUnmounted(() => window.clearInterval(clockTimer));
</script>

<style>
table th:nth-child(5),
table td:nth-child(5),
.form-grid label:nth-child(3),
.form-grid label:nth-child(4),
.form-grid label:nth-child(6),
.modal > label:last-of-type,
.book-info small,
.nav-label.second,
aside nav:nth-of-type(2) {
  display: none;
}
.stat {
  cursor: pointer;
}
.stats .stat:nth-child(n + 2) {
  display: none;
}
.login-page {
  min-height: 100vh;
  background: #f6f8fb;
  display: grid;
  place-items: center;
  padding: 24px;
}
.login-card {
  width: min(420px, 100%);
  background: #fff;
  border: 1px solid #edf0f4;
  border-radius: 16px;
  padding: 38px;
  box-shadow: 0 18px 50px #11243b18;
}
.login-card .brand-mark {
  margin: 0 auto 18px;
}
.login-card h1 {
  text-align: center;
  margin: 0;
}
.login-card p {
  text-align: center;
  color: #8996a5;
  margin: 10px 0 28px;
}
.login-card label {
  display: block;
  color: #5a6b7d;
  font-size: 12px;
  margin: 0 0 16px;
}
.login-card input {
  display: block;
  width: 100%;
  margin-top: 7px;
  border: 1px solid #e0e6ec;
  border-radius: 6px;
  padding: 11px;
  font: inherit;
  outline-color: #e59b63;
}
.login-card .primary {
  width: 100%;
  justify-content: center;
  margin-top: 8px;
}
.login-error {
  color: #d47767;
  font-size: 12px;
  margin: -4px 0 12px;
}
.login-page ~ .app-shell {
  display: none;
}
.login-page:has(+ .register-overlay) .login-card {
  display: none;
}
.register-overlay {
  position: fixed;
  inset: 0;
  z-index: 10;
}
.sidebar nav:first-of-type a:first-child {
  display: none;
}
</style>

<template>
  <div v-if="!loginUser" class="login-page">
    <form class="login-card" @submit.prevent="login">
      <div class="brand-mark"><BookOpen :size="28" /></div>
      <h1>书库管理系统</h1>
      <p>登录后管理图书与借阅记录</p>
      <label
        >用户名<input
          v-model="loginForm.username"
          required
          autocomplete="username" /></label
      ><label
        >密码<input
          v-model="loginForm.password"
          type="password"
          required
          autocomplete="current-password"
      /></label>
      <div v-if="loginError" class="login-error">{{ loginError }}</div>
      <button class="primary" type="submit">登录</button
      ><button
        class="secondary auth-switch"
        type="button"
        @click="
          registerMode = true;
          loginError = '';
        "
      >
        注册新账号
      </button>
    </form>
  </div>
  <div v-if="!loginUser && registerMode" class="login-page register-overlay">
    <form class="login-card" @submit.prevent="register">
      <div class="brand-mark"><BookOpen :size="28" /></div>
      <h1>注册账号</h1>
      <p>创建你的图书管理账号</p>
      <label
        >用户名<input
          v-model="registerForm.username"
          minlength="3"
          required /></label
      ><label
        >密码<input
          v-model="registerForm.password"
          type="password"
          minlength="6"
          required /></label
      ><label
        >确认密码<input
          v-model="registerForm.confirmPassword"
          type="password"
          required
      /></label>
      <div v-if="loginError" class="login-error">{{ loginError }}</div>
      <button class="primary" type="submit">注册</button
      ><button
        class="secondary auth-switch"
        type="button"
        @click="
          registerMode = false;
          loginError = '';
        "
      >
        返回登录
      </button>
    </form>
  </div>
  <div v-else class="app-shell">
    <aside class="sidebar" :class="{ open: mobileOpen }">
      <div class="brand">
        <div class="brand-mark"><BookOpen :size="21" /></div>
        <span>书库 <em>LIBRARY</em></span
        ><button class="close" @click="mobileOpen = false"><X /></button>
      </div>
      <div class="nav-label">工作台</div>
      <nav>
        <a :class="{ active: !showRecords }" @click="showRecords = false"
          ><LayoutDashboard /><span>概览</span></a
        ><a :class="{ active: !showRecords }" @click="showRecords = false"
          ><Library /><span>图书管理</span></a
        ><a :class="{ active: showRecords }" @click="openRecords"
          ><BookOpen /><span>借阅记录</span></a
        >
      </nav>
      <div class="nav-label second">系统</div>
      <nav>
        <a><Settings /><span>系统设置</span></a>
      </nav>
      <div class="sidebar-foot">
        <Sparkles :size="18" />
        <div><strong>保持井然有序</strong><small>管理每一本好书</small></div>
      </div>
    </aside>
    <main class="main" :class="{ 'records-mode': showRecords }">
      <header>
        <button class="menu" @click="mobileOpen = true"><Menu /></button>
        <div>
          <p class="eyebrow">{{ currentDate }}</p>
          <h1>早上好，{{ displayUsername }} <span>👋</span></h1>
        </div>
        <div class="user-actions">
          <span v-if="isAdmin" class="admin-mode">管理员模式</span>
          <div class="avatar">{{ avatarText }}</div>
          <button class="logout-button" type="button" @click="logout">
            退出登录</button
          ><button
            class="delete-account-button"
            type="button"
            @click="requestDeleteAccount"
          >
            注销账号
          </button>
        </div>
      </header>
      <section class="hero">
        <div>
          <div class="hero-kicker">LIBRARY OVERVIEW</div>
          <h2>让知识触手可及</h2>
          <p>管理馆藏，连接每一位读者。</p>
        </div>
        <div class="hero-art">
          <span>READ</span><BookOpen :size="74" stroke-width="1.3" />
        </div>
      </section>
      <div class="stats">
        <div class="stat">
          <span>馆藏图书</span><strong>{{ stats.total || 0 }}</strong
          ><small>本书籍</small><i class="blue"><BookOpen /></i>
        </div>
        <div class="stat">
          <span>可借阅</span><strong>{{ stats.available || 0 }}</strong
          ><small>本可借</small><i class="green">✓</i>
        </div>
        <div class="stat">
          <span>借阅中</span><strong>{{ stats.borrowed || 0 }}</strong
          ><small>本书籍</small><i class="orange">↗</i>
        </div>
        <div class="stat">
          <span>本月新增</span><strong>12</strong><small>本书籍</small
          ><i class="purple">＋</i>
        </div>
      </div>
      <section class="content-card">
        <div class="card-head">
          <div>
            <h3>图书目录</h3>
            <p>管理和查看所有馆藏图书</p>
          </div>
          <button
            v-if="isAdmin"
            class="primary"
            @click="
              reset();
              showForm = true;
            "
          >
            <Plus :size="18" /> 添加图书
          </button>
        </div>
        <div class="toolbar">
          <div class="search">
            <Search :size="18" /><input
              v-model="query"
              placeholder="搜索书名、作者或 ISBN..."
            />
          </div>
          <select v-model="category">
            <option v-for="c in categories" :key="c">{{ c }}</option>
          </select>
        </div>
        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th>图书信息</th>
                <th>作者</th>
                <th>分类</th>
                <th>馆藏状态</th>
                <th>ISBN</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="b in filteredBooks" :key="b.id">
                <td>
                  <div class="book-info">
                    <div class="cover">{{ (b.title || "书")[0] }}</div>
                    <div>
                      <strong>{{ b.title }}</strong
                      ><small
                        >{{ b.publisher || "未填写出版社" }} ·
                        {{ b.year }}</small
                      >
                    </div>
                  </div>
                </td>
                <td>{{ b.author }}</td>
                <td>
                  <span class="tag">{{ b.category }}</span>
                </td>
                <td>
                  <span :class="['status', b.stock > 0 ? 'available' : 'empty']"
                    ><i />{{ b.stock > 0 ? "可借阅" : "已借出" }} ·
                    {{ b.stock }}本</span
                  >
                </td>
                <td class="isbn">{{ b.isbn }}</td>
                <td>
                  <button
                    v-if="!isAdmin"
                    class="icon-btn borrow-icon"
                    type="button"
                    title="借阅此书"
                    aria-label="借阅此书"
                    @click="borrowBook(b)"
                  >
                    <BookOpen :size="16" /></button
                  ><button v-if="isAdmin" class="icon-btn" @click="openEdit(b)">
                    <Pencil :size="16" /></button
                  ><button
                    v-if="isAdmin"
                    class="icon-btn danger"
                    @click="requestDeleteBook(b)"
                  >
                    <Trash2 :size="16" />
                  </button>
                </td>
              </tr>
              <tr v-if="!filteredBooks.length">
                <td colspan="6" class="empty-row">
                  暂无图书数据，点击“添加图书”开始建立馆藏。
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="pagination">
          <span
            >共 {{ total }} 条记录，本页显示 {{ filteredBooks.length }} 条</span
          >
          <div>
            <button :disabled="page === 1" @click="changePage(page - 1)">
              <ChevronLeft :size="16" /></button
            ><button
              v-for="p in pages"
              :key="p"
              :class="{ 'page-active': p === page }"
              @click="changePage(p)"
            >
              {{ p }}</button
            ><button :disabled="page === pages" @click="changePage(page + 1)">
              <ChevronRight :size="16" />
            </button>
          </div>
        </div>
      </section>
    </main>
    <div v-if="showRecords" class="records-overlay">
      <section class="content-card records-page">
        <div class="card-head">
          <div>
            <h3>借阅记录</h3>
            <p>{{ displayUsername }} 的借阅记录</p>
          </div>
          <button class="secondary" type="button" @click="showRecords = false">
            返回概览
          </button>
        </div>
        <div class="records-list">
          <div
            v-for="record in borrowRecords"
            :key="record.id ?? record.ID"
            class="record-item"
          >
            <div>
              <strong>{{ record.bookName }}</strong>
              <p v-if="isAdmin" class="record-borrower">
                借阅者：{{ record.borrowerUsername }}
              </p>
              <p>
                库存：{{ record.stock }} 本　·　借阅时间：{{
                  formatDateTime(record.borrowTime)
                }}
              </p>
              <p>归还期限：{{ formatDateTime(record.dueTime) }}</p>
            </div>
            <span
              v-if="isAdmin"
              :class="[
                'record-status',
                record.status === 'RETURNED'
                  ? 'record-returned'
                  : 'record-borrowed',
              ]"
              >{{ record.status === "RETURNED" ? "已归还" : "未归还" }}</span
            ><template v-else
              ><span v-if="record.status === 'RETURNED'" class="record-returned"
                >已归还</span
              ><button
                v-else
                class="secondary"
                type="button"
                @click="returnBook(record)"
              >
                归还
              </button></template
            >
          </div>
          <div v-if="!borrowRecords.length" class="empty-row">暂无借阅记录</div>
        </div>
      </section>
    </div>
    <div
      v-if="showReturnConfirm"
      class="confirm-backdrop"
      @click.self="showReturnConfirm = false"
    >
      <div class="confirm-card return-card">
        <div class="confirm-icon">↩</div>
        <h3>确认归还</h3>
        <p v-if="returnError" class="borrow-error">{{ returnError }}</p>
        <div class="borrow-details">
          <p>
            书本名称：<strong>{{ selectedRecord?.bookName }}</strong>
          </p>
          <p>
            借阅时间：<strong>{{
              formatDateTime(selectedRecord?.borrowTime)
            }}</strong>
          </p>
          <p>
            归还期限：<strong>{{
              formatDateTime(selectedRecord?.dueTime)
            }}</strong>
          </p>
        </div>
        <div class="modal-actions">
          <button
            class="secondary"
            type="button"
            @click="showReturnConfirm = false"
          >
            取消</button
          ><button class="primary" type="button" @click="confirmReturn">
            确认归还
          </button>
        </div>
      </div>
    </div>
    <div
      v-if="showDeleteAccount"
      class="confirm-backdrop"
      @click.self="showDeleteAccount = false"
    >
      <div class="confirm-card">
        <div class="confirm-icon">!</div>
        <h3>注销账号</h3>
        <p v-if="deleteError" class="borrow-error">{{ deleteError }}</p>
        <p v-else>
          确认注销账号“{{
            displayUsername
          }}”？注销后账号数据将被删除，无法恢复。
        </p>
        <div class="modal-actions">
          <button
            class="secondary"
            type="button"
            @click="showDeleteAccount = false"
          >
            取消</button
          ><button
            v-if="!deleteError"
            class="primary"
            type="button"
            @click="confirmDeleteAccount"
          >
            确认注销</button
          ><button
            v-else
            class="primary"
            type="button"
            @click="showDeleteAccount = false"
          >
            知道了
          </button>
        </div>
      </div>
    </div>
    <div
      v-if="showDeleteBook"
      class="confirm-backdrop"
      @click.self="showDeleteBook = false"
    >
      <div class="confirm-card">
        <div class="confirm-icon danger-icon">!</div>
        <h3>确认删除图书？</h3>
        <p>
          确定要删除《{{ selectedDeleteBook?.title }}》吗？删除后将无法恢复。
        </p>
        <div class="modal-actions">
          <button
            class="secondary"
            type="button"
            @click="showDeleteBook = false"
          >
            取消</button
          ><button
            class="danger-primary"
            type="button"
            @click="confirmDeleteBook"
          >
            确认删除
          </button>
        </div>
      </div>
    </div>
    <div
      v-if="showLogoutConfirm"
      class="confirm-backdrop"
      @click.self="showLogoutConfirm = false"
    >
      <div class="confirm-card">
        <div class="confirm-icon">↪</div>
        <h3>确认退出登录？</h3>
        <p>退出后需要重新登录才能访问图书管理页面。</p>
        <div class="modal-actions">
          <button
            class="secondary"
            type="button"
            @click="showLogoutConfirm = false"
          >
            取消</button
          ><button class="primary" type="button" @click="confirmLogout">
            确认退出
          </button>
        </div>
      </div>
    </div>
    <div
      v-if="showBorrowConfirm"
      class="confirm-backdrop"
      @click.self="showBorrowConfirm = false"
    >
      <form class="confirm-card borrow-card" @submit.prevent="confirmBorrow">
        <div class="confirm-icon"><BookOpen :size="22" /></div>
        <p v-if="borrowError" class="borrow-error">{{ borrowError }}</p>
        <template v-if="!borrowSuccess"
          ><h3>确认借阅</h3>
          <div class="borrow-details">
            <p>
              借阅人：<strong>{{ displayUsername }}</strong>
            </p>
            <p>
              书本名称：<strong>{{ selectedBook?.title }}</strong>
            </p>
            <p>
              当前库存：<strong>{{ selectedBook?.stock }} 本</strong>
            </p>
          </div>
          <div class="modal-actions">
            <button
              class="secondary"
              type="button"
              @click="showBorrowConfirm = false"
            >
              取消</button
            ><button class="primary" type="submit">确认借阅</button>
          </div></template
        ><template v-else
          ><h3>借阅成功</h3>
          <p class="borrow-success">
            《{{ selectedBook?.title }}》已成功借阅，借阅期限为 30 天。
          </p>
          <div class="modal-actions">
            <button
              class="primary"
              type="button"
              @click="showBorrowConfirm = false"
            >
              完成
            </button>
          </div></template
        >
      </form>
    </div>
    <div v-if="showForm" class="modal-backdrop" @click.self="showForm = false">
      <form class="modal" @submit.prevent="save">
        <div class="modal-head">
          <div>
            <h3>{{ editing ? "编辑图书" : "添加图书" }}</h3>
            <p>填写图书的基本信息</p>
          </div>
          <button type="button" class="close dark" @click="showForm = false">
            <X />
          </button>
        </div>
        <label
          >书名<input
            v-model="form.title"
            required
            placeholder="例如：百年孤独"
        /></label>
        <div class="form-grid">
          <label
            >作者<input
              v-model="form.author"
              required
              placeholder="作者姓名" /></label
          ><label
            >分类<select v-model="form.category">
              <option v-for="c in categories.slice(1)" :key="c">{{ c }}</option>
            </select></label
          ><label
            >ISBN<input v-model="form.isbn" placeholder="978-7-xxx" /></label
          ><label
            >出版年份<input v-model.number="form.year" type="number" /></label
          ><label
            >库存数量<input
              v-model.number="form.stock"
              type="number"
              min="0" /></label
          ><label
            >出版社<input v-model="form.publisher" placeholder="出版社名称"
          /></label>
        </div>
        <label
          >简介<textarea
            v-model="form.description"
            rows="3"
            placeholder="简单描述这本书..."
          />
        </label>
        <div class="modal-actions">
          <button type="button" class="secondary" @click="showForm = false">
            取消</button
          ><button class="primary">保存图书</button>
        </div>
      </form>
    </div>
  </div>
</template>
