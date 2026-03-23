export interface MasSession {
  id: string;
  goal: string;
  status: 'INIT' | 'PLANNING' | 'EXECUTING' | 'COMPLETED' | 'FAILED';
  createTime: string;
  updateTime: string;
}

export interface MasTaskHistory {
  id: number;
  sessionId: string;
  taskId: string;
  name: string;
  role: string;
  prompt: string;
  result: string;
  status: 'SUCCESS' | 'FAILED' | 'INTERRUPTED';
  isInternal: boolean;
  executionTime: number;
  createTime: string;
}

export interface MasMessage {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  createTime: string;
}

export interface AgentWorkCard {
  role: string;
  agentName: string;
  status: 'idle' | 'working' | 'done';
  currentTask?: MasTaskHistory;
  recentResults: MasTaskHistory[];
  totalTasks: number;
  completedTasks: number;
}

export interface FlowNode {
  state: string;
  label: string;
  status: 'completed' | 'active' | 'pending' | 'failed';
  decision?: string;
  timestamp?: string;
}
