export function StaticTrainFallback() {
  return (
    <div className="train-scene">
      <div className="train-body">
        <div className="train-glint" />
        <div className="car-window" />
        <div className="train-nose"><div className="train-window" /></div>
        <div className="car-band" />
        <div className="car-brand">RAILY EXPRESS</div>
        <div className="wheel" />
      </div>
      <div className="track-line" />
    </div>
  );
}

export function TrainHeroLoading() {
  return (
    <div className="hero-art" aria-hidden="true">
      <div className="hero-stamp">BUILT FOR THE WAY INDIA TRAVELS</div>
      <div className="orbit" />
      <StaticTrainFallback />
      <div className="scene-label">A clearer line from here to there</div>
    </div>
  );
}
