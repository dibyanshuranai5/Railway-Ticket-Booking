import { useEffect, useMemo, useRef } from "react";
import { Canvas, useFrame } from "@react-three/fiber";
import { gsap } from "gsap";
import * as THREE from "three";
import { StaticTrainFallback } from "./TrainHeroFallback.jsx";

const sleepers = Array.from({ length: 13 }, (_, index) => -3.1 + index * 0.52);

function Wheel({ x, z }) {
  return (
    <group position={[x, -0.49, z]}>
      <mesh rotation={[Math.PI / 2, 0, 0]} castShadow>
        <cylinderGeometry args={[0.26, 0.26, 0.14, 28]} />
        <meshStandardMaterial color="#173d34" roughness={0.42} metalness={0.2} />
      </mesh>
      <mesh position={[0, 0, z > 0 ? 0.08 : -0.08]}>
        <torusGeometry args={[0.16, 0.035, 8, 24]} />
        <meshStandardMaterial color="#9caf9d" metalness={0.6} roughness={0.3} />
      </mesh>
    </group>
  );
}

function TrainModel() {
  const entrance = useRef();
  const train = useRef();
  const reducedMotion = useMemo(
    () =>
      typeof window !== "undefined" &&
      window.matchMedia("(prefers-reduced-motion: reduce)").matches,
    [],
  );

  useEffect(() => {
    if (!entrance.current || reducedMotion) return undefined;

    const timeline = gsap.timeline();
    timeline
      .fromTo(
        entrance.current.scale,
        { x: 0.88, y: 0.88, z: 0.88 },
        { x: 1, y: 1, z: 1, duration: 1.05, ease: "power3.out" },
      )
      .fromTo(
        entrance.current.position,
        { y: -0.22 },
        { y: 0, duration: 1.05, ease: "power3.out" },
        "<",
      );

    return () => timeline.kill();
  }, [reducedMotion]);

  useFrame(({ pointer, clock }, delta) => {
    if (!train.current || reducedMotion) return;

    const ease = Math.min(delta * 2.6, 1);
    train.current.rotation.y += (-0.16 + pointer.x * 0.2 - train.current.rotation.y) * ease;
    train.current.rotation.x += (-pointer.y * 0.09 - train.current.rotation.x) * ease;
    train.current.position.y = Math.sin(clock.elapsedTime * 0.7) * 0.055;
  });

  return (
    <group ref={entrance}>
      <group ref={train}>
        <mesh position={[-0.12, 0.03, 0]} castShadow receiveShadow>
          <boxGeometry args={[3.75, 0.98, 1.38]} />
          <meshStandardMaterial color="#d9e6d4" roughness={0.42} metalness={0.08} />
        </mesh>

        <mesh position={[1.7, 0.04, 0]} scale={[1.08, 0.78, 0.94]} castShadow>
          <sphereGeometry args={[0.76, 32, 22]} />
          <meshStandardMaterial color="#e4ead8" roughness={0.38} metalness={0.1} />
        </mesh>

        <mesh position={[-0.1, 0.55, 0]} castShadow>
          <boxGeometry args={[3.1, 0.09, 1.1]} />
          <meshStandardMaterial color="#eef0df" roughness={0.34} metalness={0.13} />
        </mesh>

        {[-1.35, -0.58, 0.19, 0.96].map((x) => (
          <mesh key={x} position={[x, 0.17, 0.701]}>
            <boxGeometry args={[0.48, 0.32, 0.035]} />
            <meshStandardMaterial
              color="#173e37"
              roughness={0.2}
              metalness={0.34}
              emissive="#102720"
              emissiveIntensity={0.22}
            />
          </mesh>
        ))}

        <mesh position={[2.08, 0.14, 0.56]} rotation={[0, 0, -0.08]}>
          <boxGeometry args={[0.48, 0.31, 0.05]} />
          <meshStandardMaterial color="#163b35" roughness={0.2} metalness={0.3} />
        </mesh>

        <mesh position={[0, -0.2, 0.704]}>
          <boxGeometry args={[3.45, 0.105, 0.045]} />
          <meshStandardMaterial color="#28735b" roughness={0.34} metalness={0.1} />
        </mesh>

        {[[-1.25, 0.5], [1, 0.5], [-1.25, -0.5], [1, -0.5]].map(([x, z]) => (
          <Wheel key={`${x}-${z}`} x={x} z={z} />
        ))}

        {[0.56, -0.56].map((z) => (
          <mesh key={z} position={[2.28, -0.18, z]}>
            <sphereGeometry args={[0.075, 14, 12]} />
            <meshStandardMaterial
              color="#d8ef81"
              emissive="#b9e75f"
              emissiveIntensity={0.8}
              roughness={0.3}
            />
          </mesh>
        ))}
      </group>
    </group>
  );
}

function RailwayScene() {
  return (
    <>
      <ambientLight intensity={1.25} />
      <directionalLight position={[-3, 5, 6]} intensity={2.2} color="#f4f3df" />
      <pointLight position={[3, 1, 3]} intensity={1.5} color="#c6e95d" distance={9} />

      <mesh position={[0, 0.28, -1.4]} rotation={[0.12, -0.15, -0.13]}>
        <torusGeometry args={[3.2, 0.009, 6, 96]} />
        <meshBasicMaterial color="#c6e95d" transparent opacity={0.48} />
      </mesh>
      <mesh position={[0, 0.28, -1.48]} rotation={[0.06, 0.12, 0.1]}>
        <torusGeometry args={[3.55, 0.006, 6, 96]} />
        <meshBasicMaterial color="#e1edc1" transparent opacity={0.24} />
      </mesh>

      <group position={[0, -0.36, 0]}>
        {[-0.4, 0.42].map((z) => (
          <mesh key={z} position={[0, -0.4, z]}>
            <boxGeometry args={[7, 0.055, 0.075]} />
            <meshStandardMaterial color="#a8b89e" metalness={0.5} roughness={0.45} />
          </mesh>
        ))}
        {sleepers.map((x) => (
          <mesh key={x} position={[x, -0.61, 0.01]}>
            <boxGeometry args={[0.075, 0.04, 0.98]} />
            <meshStandardMaterial color="#798d7d" roughness={0.78} />
          </mesh>
        ))}
      </group>

      <TrainModel />
    </>
  );
}

export default function TrainHeroScene() {
  const stage = useRef(null);
  const orbit = useRef(null);

  useEffect(() => {
    const reducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
    if (reducedMotion || !stage.current) return undefined;

    const context = gsap.context(() => {
      gsap.fromTo(
        stage.current,
        { autoAlpha: 0, y: 14 },
        { autoAlpha: 1, y: 0, duration: 0.85, ease: "power2.out" },
      );
      gsap.to(orbit.current, { rotation: 344, duration: 42, repeat: -1, ease: "none" });
    }, stage.current.parentElement);

    return () => context.revert();
  }, []);

  return (
    <div className="hero-art" aria-hidden="true">
      <div className="hero-stamp">BUILT FOR THE WAY INDIA TRAVELS</div>
      <div className="orbit" ref={orbit} />
      <div className="train-3d-stage" ref={stage}>
        <Canvas
          camera={{ position: [0, 0.8, 8], fov: 37, near: 0.1, far: 100 }}
          dpr={[1, 1.5]}
          gl={{ alpha: true, antialias: true, powerPreference: "low-power" }}
          fallback={<StaticTrainFallback />}
        >
          <RailwayScene />
        </Canvas>
      </div>
      <div className="scene-label">A clearer line from here to there</div>
    </div>
  );
}
