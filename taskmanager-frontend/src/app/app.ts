import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { TaskService } from './services/task';
import { Task, CreateTaskRequest } from './models/task';

type TaskFilter = 'all' | 'completed' | 'pending';
type TaskSort = 'id' | 'title';

@Component({
  selector: 'app-root',
  imports: [FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {

  private taskService = inject(TaskService);

  // Task data
  tasks: Task[] = [];

  // Loading and messages
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';

  // Add task form
  newTaskTitle = '';
  newTaskDescription = '';

  // Edit task form
  editingTaskId: number | null = null;
  editTaskTitle = '';
  editTaskDescription = '';

  // Track individual operations
  updatingTaskId: number | null = null;
  deletingTaskId: number | null = null;

  // Search, filter, sort
  searchText = '';
  selectedFilter: TaskFilter = 'all';
  selectedSort: TaskSort = 'id';

  // Client-side pagination
  currentPage = 1;
  pageSize = 5;

  ngOnInit(): void {
    this.loadTasks();
  }

  // --------------------------------
  // GET ALL TASKS
  // --------------------------------

  loadTasks(): void {
    this.loading = true;
    this.errorMessage = '';

    this.taskService.getTasks().subscribe({
      next: (data: Task[]) => {
        this.tasks = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading tasks:', error);
        this.errorMessage =
          'Unable to load tasks. Check that Spring Boot is running.';
        this.loading = false;
      }
    });
  }

  // --------------------------------
  // CREATE TASK
  // --------------------------------

  addTask(): void {
    const title = this.newTaskTitle.trim();
    const description = this.newTaskDescription.trim();

    if (!title) {
      this.errorMessage = 'Task title is required.';
      return;
    }

    if (title.length > 200 || description.length > 1000) {
      this.errorMessage = 'Task title or description is too long.';
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const newTask: CreateTaskRequest = {
      title: title,
      description: description,
      completed: false
    };

    this.taskService.createTask(newTask).subscribe({
      next: (savedTask: Task) => {
        this.tasks = [...this.tasks, savedTask];

        this.newTaskTitle = '';
        this.newTaskDescription = '';

        this.saving = false;
        this.successMessage = 'Task added successfully!';

        this.selectedFilter = 'all';
        this.searchText = '';
        this.selectedSort = 'id';
        this.currentPage = this.totalPages;
      },
      error: (error) => {
        console.error('Error creating task:', error);
        this.errorMessage = 'Unable to add task. Please try again.';
        this.saving = false;
      }
    });
  }

  // --------------------------------
  // TOGGLE COMPLETED / PENDING
  // --------------------------------

  toggleTask(task: Task): void {
    if (this.updatingTaskId !== null) {
      return;
    }

    this.updatingTaskId = task.id;
    this.errorMessage = '';
    this.successMessage = '';

    const updatedTask: Task = {
      ...task,
      completed: !task.completed
    };

    this.taskService.updateTask(updatedTask).subscribe({
      next: (savedTask: Task) => {
        this.replaceTask(savedTask);
        this.updatingTaskId = null;

        this.successMessage = savedTask.completed
          ? 'Task marked as completed!'
          : 'Task marked as pending!';
      },
      error: (error) => {
        console.error('Error updating task:', error);
        this.errorMessage = 'Unable to update task status.';
        this.updatingTaskId = null;
      }
    });
  }

  // --------------------------------
  // EDIT TASK
  // --------------------------------

  startEdit(task: Task): void {
    this.editingTaskId = task.id;
    this.editTaskTitle = task.title;
    this.editTaskDescription = task.description ?? '';

    this.errorMessage = '';
    this.successMessage = '';
  }

  cancelEdit(): void {
    this.editingTaskId = null;
    this.editTaskTitle = '';
    this.editTaskDescription = '';
  }

  saveEdit(task: Task): void {
    const title = this.editTaskTitle.trim();
    const description = this.editTaskDescription.trim();

    if (!title) {
      this.errorMessage = 'Task title is required.';
      return;
    }

    if (title.length > 200 || description.length > 1000) {
      this.errorMessage = 'Task title or description is too long.';
      return;
    }

    if (this.updatingTaskId !== null) {
      return;
    }

    this.updatingTaskId = task.id;
    this.errorMessage = '';
    this.successMessage = '';

    const updatedTask: Task = {
      ...task,
      title: title,
      description: description
    };

    this.taskService.updateTask(updatedTask).subscribe({
      next: (savedTask: Task) => {
        this.replaceTask(savedTask);
        this.cancelEdit();

        this.updatingTaskId = null;
        this.successMessage = 'Task updated successfully!';
      },
      error: (error) => {
        console.error('Error editing task:', error);
        this.errorMessage = 'Unable to save task changes.';
        this.updatingTaskId = null;
      }
    });
  }

  // --------------------------------
  // DELETE TASK
  // --------------------------------

  deleteTask(task: Task): void {
    if (this.deletingTaskId !== null) {
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete "${task.title}"?`
    );

    if (!confirmed) {
      return;
    }

    this.deletingTaskId = task.id;
    this.errorMessage = '';
    this.successMessage = '';

    this.taskService.deleteTask(task.id).subscribe({
      next: () => {
        this.tasks = this.tasks.filter(
          existingTask => existingTask.id !== task.id
        );

        if (this.editingTaskId === task.id) {
          this.cancelEdit();
        }

        this.deletingTaskId = null;
        this.successMessage = 'Task deleted successfully!';

        if (this.currentPage > this.totalPages) {
          this.currentPage = this.totalPages;
        }
      },
      error: (error) => {
        console.error('Error deleting task:', error);
        this.errorMessage = 'Unable to delete task.';
        this.deletingTaskId = null;
      }
    });
  }

  // --------------------------------
  // REPLACE AN UPDATED TASK
  // --------------------------------

  private replaceTask(updatedTask: Task): void {
    this.tasks = this.tasks.map(task =>
      task.id === updatedTask.id ? updatedTask : task
    );
  }

  // --------------------------------
  // TASK STATISTICS
  // --------------------------------

  get totalTasks(): number {
    return this.tasks.length;
  }

  get completedTasks(): number {
    return this.tasks.filter(task => task.completed).length;
  }

  get pendingTasks(): number {
    return this.tasks.filter(task => !task.completed).length;
  }

  // --------------------------------
  // SEARCH, FILTER, SORT
  // --------------------------------

  get filteredTasks(): Task[] {
    const search = this.searchText.trim().toLowerCase();

    let result = this.tasks.filter(task => {

      const matchesSearch =
        task.title.toLowerCase().includes(search) ||
        (task.description ?? '').toLowerCase().includes(search);

      const matchesFilter =
        this.selectedFilter === 'all' ||
        (this.selectedFilter === 'completed' && task.completed) ||
        (this.selectedFilter === 'pending' && !task.completed);

      return matchesSearch && matchesFilter;
    });

    result = [...result].sort((a, b) => {
      if (this.selectedSort === 'title') {
        return a.title.localeCompare(b.title);
      }

      return a.id - b.id;
    });

    return result;
  }

  // --------------------------------
  // PAGINATION
  // --------------------------------

  get totalPages(): number {
    return Math.max(
      1,
      Math.ceil(this.filteredTasks.length / this.pageSize)
    );
  }

  get paginatedTasks(): Task[] {
    const page = Math.min(this.currentPage, this.totalPages);
    const start = (page - 1) * this.pageSize;

    return this.filteredTasks.slice(
      start,
      start + this.pageSize
    );
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  // Reset pagination after changing search or filter
  resetPage(): void {
    this.currentPage = 1;
  }
}