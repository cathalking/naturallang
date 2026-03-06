import React, { useEffect, useRef, useState } from "react";
import {
  createSession,
  fetchPrompt,
  postAnswer,
  postSkip,
  quitSession,
  PromptPayload,
  PromptResponse,
  AnswerResponse,
  SummaryResult
} from "./api/practice";

type Feedback = {
  type: "success" | "error" | "info";
  text: string;
  detailQuestion?: string;
  detailAnswer?: string;
};

type FeedbackMotion = "success" | "error" | null;

const DEFAULT_SESSION = {
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
  const [sessionDone, setSessionDone] = useState(false);
  const [feedbackMotion, setFeedbackMotion] = useState<FeedbackMotion>(null);
  const [feedbackMotionKey, setFeedbackMotionKey] = useState(0);
  const feedbackMotionTimeoutRef = useRef<number | null>(null);

  useEffect(() => {
    return () => {
      if (feedbackMotionTimeoutRef.current !== null) {
        window.clearTimeout(feedbackMotionTimeoutRef.current);
      }
    };
  }, []);

  const triggerFeedbackMotion = (type: Exclude<FeedbackMotion, null>) => {
    if (feedbackMotionTimeoutRef.current !== null) {
      window.clearTimeout(feedbackMotionTimeoutRef.current);
    }
    setFeedbackMotion(type);
    setFeedbackMotionKey((current) => current + 1);
    feedbackMotionTimeoutRef.current = window.setTimeout(() => {
      setFeedbackMotion(null);
      feedbackMotionTimeoutRef.current = null;
    }, 520);
  };

  const resetState = () => {
    setSessionId(null);
    setPrompt(null);
    setSummary(null);
    setRemaining(null);
    setAnswer("");
    setFeedback(null);
    setSessionDone(false);
    setFeedbackMotion(null);
  };

  const startPractice = async () => {
    setLoading(true);
    resetState();
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

  const loadPrompt = async (id: string, options?: { preserveFeedback?: boolean }) => {
    try {
      const response: PromptResponse = await fetchPrompt(id);
      setSummary(response.summary);
      setRemaining(response.remaining);
      if (response.done) {
        setSessionDone(true);
        setPrompt(null);
        if (!options?.preserveFeedback) {
          setFeedback({ type: "info", text: "Session complete." });
        }
        await quitSession(id).catch(() => {});
        return;
      }
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
      if (result.correct) {
        triggerFeedbackMotion("success");
      } else {
        triggerFeedbackMotion("error");
      }
      setFeedback(
        result.correct
          ? {
              type: "success",
              text: "Correct!",
              detailQuestion: result.prompt,
              detailAnswer: result.expected
            }
          : {
              type: "error",
              text: "Incorrect",
              detailQuestion: result.prompt,
              detailAnswer: result.expected
            }
      );
      setAnswer("");
      await loadPrompt(sessionId);
    } catch {
      setFeedback({ type: "error", text: "Submission failed." });
    } finally {
      setLoading(false);
    }
  };

  const skipPrompt = async () => {
    if (!sessionId || !prompt) return;
    setLoading(true);
    try {
      const skippedPrompt = prompt.prompt;
      const updatedSummary = await postSkip(sessionId);
      setSummary(updatedSummary);
      setRemaining(updatedSummary.remaining);
      setAnswer("");
      setFeedback({ type: "info", text: `Skipped: ${skippedPrompt}` });
      await loadPrompt(sessionId, { preserveFeedback: true });
    } catch {
      setFeedback({ type: "error", text: "Skip failed." });
    } finally {
      setLoading(false);
    }
  };

  const handleAnswerKeyDown = async (
    event: React.KeyboardEvent<HTMLTextAreaElement>
  ) => {
    if (event.key !== "Enter" || event.shiftKey) {
      return;
    }
    event.preventDefault();
    if (loading || !prompt) {
      return;
    }
    await submitAnswer();
  };

  return (
    <div className="app-shell">
      <div className="session-banner">
        {sessionId ? `Session ${sessionId.slice(0, 8)}` : "Start a practice session"}
      </div>
      <div className={`card${feedbackMotion ? ` card-${feedbackMotion}` : ""}`}>
        {!sessionId ? (
          <button className="btn primary" onClick={startPractice} disabled={loading}>
            {loading ? "Starting..." : "Start practice"}
          </button>
        ) : sessionDone ? (
          <div className="session-done">
            <div className="session-done-content">
              <p className="session-done-title">Practice complete!</p>
              <p className="session-done-subtext">
                Nice work translating {summary?.asked ?? 0} prompts. Ready for another round?
              </p>
            </div>
            <button className="btn primary" onClick={startPractice} disabled={loading}>
              {loading ? "Starting..." : "Start another session"}
            </button>
          </div>
        ) : (
          <>
            <div className="prompt-text">{prompt?.prompt ?? "Loading..."}</div>
            <textarea
              className="input-area"
              placeholder="Type the Irish translation"
              value={answer}
              onChange={(event) => setAnswer(event.target.value)}
              onKeyDown={handleAnswerKeyDown}
              rows={3}
              disabled={!prompt || loading}
            />
            <div className="controls">
              <button className="btn primary" onClick={submitAnswer} disabled={!prompt || loading}>
                Submit
              </button>
              <button
                className="btn secondary"
                onClick={skipPrompt}
                disabled={!prompt || loading}
              >
                Skip
              </button>
            </div>
          </>
        )}
        {summary && !sessionDone && (
          <div className="scoreboard">
            <span>
              ✅ {summary.correct ?? 0} · ❌ {summary.incorrect ?? 0} · ⚪ {summary.skipped ?? 0}
            </span>
            <span>{remaining !== null ? `Left: ${remaining}` : "∞"} questions</span>
          </div>
        )}
        {feedback && (
          <div
            key={feedbackMotionKey}
            className={`feedback ${feedback.type}${feedbackMotion ? ` feedback-${feedbackMotion}` : ""}`}
          >
            <span className="feedback-badge">{feedback.text}</span>
            {(feedback.detailQuestion || feedback.detailAnswer) && (
              <span className="feedback-detail">
                {feedback.detailQuestion && (
                  <span className="feedback-detail-question">{feedback.detailQuestion}</span>
                )}
                {feedback.detailAnswer && (
                  <span className="feedback-detail-answer">{feedback.detailAnswer}</span>
                )}
              </span>
            )}
          </div>
        )}
        {summary && sessionDone && (
          <div className="summary">
            <span>
              <span className="summary-icon">🕒</span>Asked: {summary.asked}
            </span>
            <span>
              <span className="summary-icon">✅</span>Correct: {summary.correct}
            </span>
            <span>
              <span className="summary-icon">❌</span>Incorrect: {summary.incorrect}
            </span>
            <span>
              <span className="summary-icon">⚪</span>Skipped: {summary.skipped}
            </span>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
