import { useEffect, useRef, useState } from "react";
import { Input } from "./components/ui/input";
import { Label } from "./components/ui/label";
import { Button } from "./components/ui/button";
import z from "zod";
import { StopCircleIcon } from "lucide-react";
import { Spinner } from "./components/ui/spinner";

export const App = () => {
  const [token, setToken] = useState<string | null>(null);

  if (!token) return <AuthForm authenticate={(token) => setToken(token)} />;
  return <Authenticated token={token} />;
};

const Authenticated = ({ token }: { token: string }) => {
  const [conversationState, setConversationState] = useState("idle");
  const [fromLanguage, setFromLanguage] = useState("pt");
  const [toLanguage, setToLanguage] = useState("en");

  const ws = useRef<WebSocket | null>(null);
  const audioContext = useRef<AudioContext | null>(null);
  const mediaStream = useRef<MediaStream | null>(null);
  const processor = useRef<ScriptProcessorNode | null>(null);

  const startRecording = async () => {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });

    const context = new AudioContext({ sampleRate: 16000 });
    const source = context.createMediaStreamSource(stream);
    const processorNode = context.createScriptProcessor(4096, 1, 1);

    processorNode.onaudioprocess = (e) => {
      const inputData = e.inputBuffer.getChannelData(0);

      const pcm16 = new Int16Array(inputData.length);
      for (let i = 0; i < inputData.length; i++) {
        const s = Math.max(-1, Math.min(1, inputData[i]));
        pcm16[i] = s < 0 ? s * 0x8000 : s * 0x7fff;
      }

      ws.current?.send(pcm16.buffer);
    };

    source.connect(processorNode);
    processorNode.connect(context.destination);

    audioContext.current = context;
    mediaStream.current = stream;
    processor.current = processorNode;
  };

  const stopRecording = () => {
    if (processor.current) {
      processor.current.disconnect();
      processor.current = null;
    }

    if (audioContext.current) {
      audioContext.current.close();
      audioContext.current = null;
    }

    if (mediaStream.current) {
      mediaStream.current.getTracks().forEach((track) => track.stop());
      mediaStream.current = null;
    }

    ws.current?.send(JSON.stringify({ event: "conversation:translate" }));
  };

  const [conversationTranslationState, setConversationTranslationState] =
    useState<"loading" | "reproducing" | "ready" | null>(null);

  const [audioDeltas, setAudioDeltas] = useState<string[]>([]);

  useEffect(() => {
    const socket = new WebSocket(
      `ws://localhost:8080/ws?session_token=${token}`
    );

    socket.onopen = () => console.log("Connection opened");
    socket.onerror = (err) => console.error(err);
    socket.onclose = () => console.log("Connection closed");

    socket.onmessage = (data) => {
      const parse = z
        .object({ event: z.string(), data: z.any() })
        .safeParse(JSON.parse(data.data));

      if (!parse.success) return;

      console.log("Received message", data);

      switch (parse.data.event) {
        case "conversation:start:success": {
          setConversationState("active");
          startRecording();

          break;
        }
        case "conversation:translate:state": {
          const p = z
            .object({ state: z.enum(["loading", "ready", "reproducing"]) })
            .safeParse(parse.data.data);

          if (p.success) setConversationTranslationState(p.data.state);
          break;
        }
        case "conversation:data:delta": {
          const p = z.object({ audio: z.string() }).safeParse(parse.data.data);
          if (p.success) setAudioDeltas((prev) => [...prev, p.data.audio]);

          break;
        }
        case "conversation:swap:success": {
          setFromLanguage(toLanguage);
          setToLanguage(fromLanguage);
          setConversationState("active");
          startRecording();

          break;
        }
      }
    };

    ws.current = socket;
    return () => socket.close();
  }, [token]);

  useEffect(() => console.log(audioDeltas), [audioDeltas]);

  useEffect(() => {
    if (conversationTranslationState !== "ready" || !audioDeltas.length) return;

    const playAudio = async () => {
      const context = new AudioContext({ sampleRate: 24000 });
      const combinedBase64 = audioDeltas.join("");

      const binaryString = atob(combinedBase64);
      const bytes = new Uint8Array(binaryString.length);

      for (let i = 0; i < binaryString.length; i++)
        bytes[i] = binaryString.charCodeAt(i);

      const pcm16 = new Int16Array(bytes.buffer);
      const float32 = new Float32Array(pcm16.length);

      for (let i = 0; i < pcm16.length; i++)
        float32[i] = pcm16[i] / (pcm16[i] < 0 ? 0x8000 : 0x7fff);

      const audioBuffer = context.createBuffer(1, float32.length, 24000);
      audioBuffer.getChannelData(0).set(float32);

      const source = context.createBufferSource();
      source.buffer = audioBuffer;
      source.connect(context.destination);
      source.start();

      source.onended = () => {
        context.close();
        setAudioDeltas([]);
        setConversationTranslationState(null);

        setConversationState("initializing");
        ws.current?.send(JSON.stringify({ event: "conversation:swap" }));
      };
    };

    playAudio();
  }, [conversationTranslationState, audioDeltas]);

  return (
    <div className="flex justify-center items-center h-svh">
      {conversationState === "idle" && (
        <Button
          onClick={() => {
            ws.current?.send(
              JSON.stringify({
                event: "conversation:start",
                data: { fromLanguage, toLanguage },
              })
            );

            setConversationState("initializing");
          }}
        >
          Start Conversation
        </Button>
      )}
      {conversationState === "initializing" && <Spinner />}
      {conversationState === "active" && (
        <Button onClick={stopRecording}>
          {!conversationTranslationState && <StopCircleIcon />}
          {conversationTranslationState && <Spinner />}
        </Button>
      )}
    </div>
  );
};

const AuthForm = ({
  authenticate,
}: {
  authenticate: (token: string) => void;
}) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleClick = async () => {
    try {
      const res = await fetch("http://localhost:8080/auth/sign", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      const data = await res.json();
      const parse = z.object({ token: z.string() }).safeParse(data);
      if (parse.success) authenticate(parse.data.token);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="h-svh flex justify-center items-center flex-col gap-y-6">
      <h1 className="text-2xl">Sign | Languify</h1>
      <div className="w-72 flex gap-y-6 flex-col">
        <div className="grid gap-y-4">
          <div className="grid gap-y-2">
            <Label>Email</Label>
            <Input value={email} onChange={(ev) => setEmail(ev.target.value)} />
          </div>
          <div className="grid gap-y-2">
            <Label>Password</Label>
            <Input
              value={password}
              onChange={(ev) => setPassword(ev.target.value)}
            />
          </div>
        </div>
        <Button onClick={handleClick}>Continue</Button>
      </div>
    </div>
  );
};
