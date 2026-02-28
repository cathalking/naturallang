const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "/api/practice";

export interface CreateSessionRequest {
  verbs?: string[];
  forms?: string[];
  tenses?: string[];
  subjects?: string[];
  object?: string;
  maxQuestions?: number;
  pronounMode?: string;
}

export interface CreateSessionResponse {
  sessionId: string;
  maxQuestions: number | null;
  totalPermutations: number;
}

export interface PromptPayload {
  usage: {
    verb: string;
    sentenceForm: string;
    subject: string;
    tense: string;
    object?: string;
  };
  prompt: string;
}

export interface SummaryResult {
  asked: number;
  correct: number;
  incorrect: number;
  skipped: number;
  remaining: number | null;
}

export interface PromptResponse {
  prompt: PromptPayload | null;
  remaining: number | null;
  done: boolean;
  summary: SummaryResult;
}

export interface AnswerRequest {
  response: string;
}

export interface AnswerResponse {
  correct: boolean;
  expected: string;
  prompt: string;
  usage: PromptPayload["usage"];
  asked: number;
  remaining: number | null;
}

const jsonHeaders = { "Content-Type": "application/json" };

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    throw new Error(`API error ${res.status}`);
  }
  return res.json();
}

export function createSession(payload: CreateSessionRequest): Promise<CreateSessionResponse> {
  return fetch(`${BASE_URL}/sessions`, {
    method: "POST",
    headers: jsonHeaders,
    body: JSON.stringify(payload)
  }).then(handleResponse);
}

export function fetchPrompt(sessionId: string): Promise<PromptResponse> {
  return fetch(`${BASE_URL}/sessions/${sessionId}/prompt`).then(handleResponse);
}

export function postAnswer(sessionId: string, answer: AnswerRequest): Promise<AnswerResponse> {
  return fetch(`${BASE_URL}/sessions/${sessionId}/answer`, {
    method: "POST",
    headers: jsonHeaders,
    body: JSON.stringify(answer)
  }).then(handleResponse);
}
