const STORAGE_KEY = 'tasks';

export function loadTasks() {
  const list = uni.getStorageSync(STORAGE_KEY);
  if (Array.isArray(list)) {
    return list;
  }
  uni.setStorageSync(STORAGE_KEY, []);
  return [];
}

export function saveTasks(tasks) {
  uni.setStorageSync(STORAGE_KEY, tasks || []);
}

export function upsertTask(task) {
  const tasks = loadTasks();
  const idx = tasks.findIndex(item => item.id === task.id);
  if (idx > -1) {
    tasks.splice(idx, 1, task);
  } else {
    tasks.unshift(task);
  }
  saveTasks(tasks);
  return tasks;
}

export function getTaskById(id) {
  const tasks = loadTasks();
  return tasks.find(item => item.id === id);
}

