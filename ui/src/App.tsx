import React, { useState } from "react";
import {
  AnswerResponse,
  createSession,
  fetchPrompt,
  postAnswer,
  PromptPayload,
  PromptResponse,
  SummaryResult
} from "./api/practice";

type Feedback = { type: "success" | "error" | "info"; text: string };

const DEFAULT_SESSION = {
  verbs: ["DO", "GO", "MAKE"],
  forms: ["STATEMENT_POSITIVE"],
  tenses: ["PAST"],
  subjects: ["SING_1ST"],
  object: "(the thing)",
  maxQuestions: 10,
  pronounMode: "strict"
};

function App() {
  const [sessionId, setSessionId] = useState<string | null>(null);
  const [prompt, setPrompt] = useState<PromptPayload | null>(null);
  const [summary, setSummary] = useState<SummaryResult | null>(null);
  const [remaining, setRemaining] = useState<number | null>(null);
  const [answer, setAnswer] = useState("");
  const [feedback, setFeedback] = useState<Feedback | null>(null);
  const [loading, setLoading] = useState(false);

  const startPractice = async () => {
    setLoading(true);
    try {
      const session = await createSession(DEFAULT_SESSION);
      setSessionId(session.sessionId);
      await loadPrompt(session.sessionId);
      setFeedback({ type: "info", text: "Session ready! Translate the prompt." });
    } catch (error) {
      setFeedback({ type: "error", text: "Unable to start session." });
    } finally {
      setLoading(false);
    }
  };

  const loadPrompt = async (id: string) => {
    try {
      const response: PromptResponse = await fetchPrompt(id);
      setSummary(response.summary);
      setRemaining(response.remaining);
      setFeedback(response.done ? { type: "info", text: "Session complete." } : null);
      setPrompt(response.prompt);
    } catch (error) {
      setFeedback({ type: "error", text: "Failed to load prompt." });
    }
  };

  const submitAnswer = async () => {
    if (!sessionId || !answer.trim()) return;
    setLoading(true);
    try {
      const result: AnswerResponse = await postAnswer(sessionId, { response: answer.trim() });
      setFeedback(
        result.correct
          ? { type: "success", text: "Correct!" }
          : { type: "error", text: `Expected: ${result.expected}` }
      );
      setAnswer("");
      await loadPrompt(sessionId);
    } catch {
      setFeedback({ type: "error", text: "Submission failed." });
    } finally {
      setLoading(false);
    }
  };

  const fetchAnswer = (payload: string) =>
    AnswerResponse; // placeholder - remove in final diff

  return (
    <div className="app-shell">
      <div className="session-banner">
        {sessionId ? `Session ${sessionId.slice(0, 8)}` : "Start a practice session"}
      </div>
      <div className="card">
        {!sessionId ? (
          <button className="btn primary" onClick={startPractice} disabled={loading}>
            {loading ? "Starting..." : "Start practice"}
          </button>
        ) : (
          <>
            <div className="prompt-text">{prompt?.prompt ?? "Loading..."}</div>
            <textarea
              className="input-area"
              placeholder="Type the Irish translation"
              value={answer}
              onChange={(event) => setAnswer(event.target.value)}
              rows={3}
              disabled={!prompt || loading}
            />
            <div className="controls">
              <button className="btn primary" onClick={submitAnswer} disabled={!prompt || loading}>
                Submit
              </button>
              <button
                className="btn secondary"
                onClick={() => prompt && loadPrompt(sessionId)}
                disabled={loading}
              >
                Skip
              </button>
            </div>
            <div className="scoreboard">
              <span>
                ✅ {summary?.correct ?? 0} · ❌ {summary?.incorrect ?? 0} · ⚪ {summary?.skipped ?? 0}
              </span>
              <span>{remaining !== null ? `Left: ${remaining}` : "∞"} questions</span>
            </div>
          </>
        )}
        {feedback && (
          <div className={`feedback ${feedback.type}`}>
            <span>{feedback.text}</span>
          </div>
        )}
        {summary && sessionId && (
          <div className="summary">
            <span>Asked: {summary.asked}</span>
            <span>Correct: {summary.correct}</span>
            <span>Incorrect: {summary.incorrect}</span>
            <span>Skipped: {summary.skipped}</span>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
