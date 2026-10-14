export interface Task {
  id: number;
  title: string;
  description: string | null;
  completed: boolean;
}

export interface CreateTaskRequest {
  title: string;
  description: string;
  completed: boolean;
}