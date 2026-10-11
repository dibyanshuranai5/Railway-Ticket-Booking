import { lazy, Suspense, useMemo } from "react";
import { TrainHeroLoading } from "./TrainHeroFallback.jsx";

const TrainHeroScene = lazy(() => import("./TrainHeroScene.jsx"));

function supportsWebGL() {
  if (typeof document === "undefined") return false;

  try {
    const canvas = document.createElement("canvas");
    const context = canvas.getContext("webgl2") || canvas.getContext("webgl");
    if (!context) return false;
    context.getExtension("WEBGL_lose_context")?.loseContext();
    return true;
  } catch {
    return false;
  }
}

export default function TrainHeroLoader() {
  const webglAvailable = useMemo(supportsWebGL, []);

  if (!webglAvailable) return <TrainHeroLoading />;

  return (
    <Suspense fallback={<TrainHeroLoading />}>
      <TrainHeroScene />
    </Suspense>
  );
}
