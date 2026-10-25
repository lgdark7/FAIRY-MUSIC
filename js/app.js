/**
 * FAIRY MUSIC — Official Website JavaScript Engine
 * Cosmic Void, Stellar Dust, Meteor Showers & Interactive Optics.
 * Zero-dependency, 60fps lightweight canvas physics, mobile-optimized.
 */

document.addEventListener('DOMContentLoaded', () => {
  if (window.lucide) {
    window.lucide.createIcons();
  }

  initCosmicCanvas();
  fetchReleaseData();
  setupScreenTabs();
  setupCopyButtons();
  setupEqSliders();
  setupFaqAccordion();
  setupMobileMenu();
  setupCardSpotlight();
  setupBackgroundVideo();
});

/* ==========================================================================
   1. Advanced Cosmic Canvas Engine: Multi-layer Stars, Meteors & Constellations
   ========================================================================== */

function initCosmicCanvas() {
  const canvas = document.getElementById('starfield-canvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  const isMobile = window.innerWidth < 768;
  let width = (canvas.width = window.innerWidth);
  let height = (canvas.height = window.innerHeight);

  // Resize listener with debouncing
  let resizeTimeout;
  window.addEventListener('resize', () => {
    clearTimeout(resizeTimeout);
    resizeTimeout = setTimeout(() => {
      width = canvas.width = window.innerWidth;
      height = canvas.height = window.innerHeight;
      initStars();
    }, 150);
  });

  // Track mouse coordinates for interactive cosmic parallax & constellation effect
  const mouse = { x: -1000, y: -1000, active: false };
  if (!isMobile) {
    window.addEventListener('mousemove', (e) => {
      mouse.x = e.clientX;
      mouse.y = e.clientY;
      mouse.active = true;
    });
    window.addEventListener('mouseleave', () => {
      mouse.active = false;
      mouse.x = -1000;
      mouse.y = -1000;
    });
  }

  // Multi-tier Star Clusters
  let stars = [];
  function initStars() {
    stars = [];
    const count = isMobile
      ? Math.min(Math.floor((width * height) / 18000), 45)
      : Math.min(Math.floor((width * height) / 12000), 95);

    for (let i = 0; i < count; i++) {
      const isLarge = Math.random() > 0.92;
      stars.push({
        x: Math.random() * width,
        y: Math.random() * height,
        originX: 0,
        originY: 0,
        radius: isLarge ? Math.random() * 1.5 + 1.0 : Math.random() * 0.9 + 0.3,
        alpha: Math.random() * 0.5 + 0.15,
        twinkleSpeed: (Math.random() * 0.009 + 0.003) * (Math.random() > 0.5 ? 1 : -1),
        speedY: isLarge ? Math.random() * 0.18 + 0.08 : Math.random() * 0.08 + 0.03,
        speedX: (Math.random() - 0.5) * 0.04,
        isLarge: isLarge
      });
    }
  }
  initStars();

  // Shooting Stars (Meteors) System
  const meteors = [];
  let lastMeteorTime = Date.now();
  let nextMeteorDelay = 2500 + Math.random() * 3500; // Meteor every 2.5 - 6s

  function spawnMeteor() {
    // Spawn meteor from top-right quadrant sweeping down-left or vice versa
    const startX = Math.random() * (width * 0.8) + (width * 0.2);
    const startY = Math.random() * (height * 0.4);
    const length = Math.random() * 80 + 70;
    const speed = Math.random() * 9 + 11;
    const angle = (Math.PI / 4) + (Math.random() * 0.2 - 0.1); // ~45 degrees

    meteors.push({
      x: startX,
      y: startY,
      dx: -Math.cos(angle) * speed,
      dy: Math.sin(angle) * speed,
      length: length,
      alpha: 1.0,
      decay: Math.random() * 0.016 + 0.014,
      size: Math.random() * 1.5 + 1.0
    });
  }

  // Animation Loop
  function render() {
    ctx.clearRect(0, 0, width, height);

    // 1. Render Stars
    for (let i = 0; i < stars.length; i++) {
      const s = stars[i];

      // Twinkle cycle
      s.alpha += s.twinkleSpeed;
      if (s.alpha > 0.65 || s.alpha < 0.1) {
        s.twinkleSpeed = -s.twinkleSpeed;
      }

      // Drift physics
      s.y -= s.speedY;
      s.x += s.speedX;

      if (s.y < 0) {
        s.y = height;
        s.x = Math.random() * width;
      }
      if (s.x < 0) s.x = width;
      if (s.x > width) s.x = 0;

      // Draw star core
      ctx.beginPath();
      ctx.arc(s.x, s.y, s.radius, 0, Math.PI * 2);
      ctx.fillStyle = `rgba(255, 255, 255, ${Math.max(0.08, Math.min(0.75, s.alpha))})`;
      ctx.fill();

      // Subtle glow for larger stellar bodies
      if (s.isLarge && s.alpha > 0.35) {
        ctx.beginPath();
        ctx.arc(s.x, s.y, s.radius * 2.5, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(186, 230, 253, ${s.alpha * 0.15})`;
        ctx.fill();
      }

      // Constellation linkage near cursor (desktop only)
      if (mouse.active) {
        const dist = Math.hypot(mouse.x - s.x, mouse.y - s.y);
        if (dist < 110) {
          ctx.beginPath();
          ctx.moveTo(mouse.x, mouse.y);
          ctx.lineTo(s.x, s.y);
          const linkAlpha = (1 - dist / 110) * 0.22;
          ctx.strokeStyle = `rgba(255, 255, 255, ${linkAlpha})`;
          ctx.lineWidth = 0.75;
          ctx.stroke();
        }
      }
    }

    // 2. Spawn and update Meteors (Shooting Stars)
    const now = Date.now();
    if (now - lastMeteorTime > nextMeteorDelay) {
      spawnMeteor();
      lastMeteorTime = now;
      nextMeteorDelay = 3000 + Math.random() * 4500;
    }

    for (let i = meteors.length - 1; i >= 0; i--) {
      const m = meteors[i];

      m.x += m.dx;
      m.y += m.dy;
      m.alpha -= m.decay;

      if (m.alpha <= 0 || m.x < -100 || m.y > height + 100) {
        meteors.splice(i, 1);
        continue;
      }

      // Render Meteor Tail with linear starlight gradient
      const tailX = m.x - (m.dx / Math.hypot(m.dx, m.dy)) * m.length;
      const tailY = m.y - (m.dy / Math.hypot(m.dx, m.dy)) * m.length;

      const grad = ctx.createLinearGradient(tailX, tailY, m.x, m.y);
      grad.addColorStop(0, 'rgba(255, 255, 255, 0)');
      grad.addColorStop(0.7, `rgba(186, 230, 253, ${m.alpha * 0.35})`);
      grad.addColorStop(1, `rgba(255, 255, 255, ${m.alpha})`);

      ctx.beginPath();
      ctx.moveTo(tailX, tailY);
      ctx.lineTo(m.x, m.y);
      ctx.strokeStyle = grad;
      ctx.lineWidth = m.size;
      ctx.lineCap = 'round';
      ctx.stroke();

      // Meteor Head Flash
      ctx.beginPath();
      ctx.arc(m.x, m.y, m.size * 1.5, 0, Math.PI * 2);
      ctx.fillStyle = `rgba(255, 255, 255, ${m.alpha})`;
      ctx.fill();
    }

    requestAnimationFrame(render);
  }

  render();
}

/* ==========================================================================
   2. Live Release Data Binding
   ========================================================================== */

async function fetchReleaseData() {
  try {
    const res = await fetch('version.json');
    if (!res.ok) return;
    const data = await res.json();

    document.querySelectorAll('.app-version').forEach(el => {
      el.textContent = `v${data.version}`;
    });

    document.querySelectorAll('.app-size').forEach(el => {
      el.textContent = data.size || '71 MB';
    });

    document.querySelectorAll('.app-date').forEach(el => {
      el.textContent = data.releaseDate || '';
    });

    document.querySelectorAll('.app-download-link').forEach(el => {
      el.href = data.apkUrl || 'https://github.com/lgdark7/FAIRY-MUSIC/releases/latest';
    });

    // Populate Changelog
    const changelogList = document.getElementById('changelog-items');
    if (changelogList && data.changelog && data.changelog.length > 0) {
      changelogList.innerHTML = data.changelog.map(section => `
        <div class="mb-4 last:mb-0">
          <h5 class="text-sm font-semibold text-white mb-2 flex items-center gap-2">
            <span class="w-1.5 h-1.5 rounded-full bg-white shadow-[0_0_8px_white]"></span>
            ${section.title}
          </h5>
          <ul class="space-y-1.5 text-xs text-slate-400 pl-4 list-disc marker:text-slate-500">
            ${section.items.map(item => `<li>${item}</li>`).join('')}
          </ul>
        </div>
      `).join('');
    }
  } catch (err) {
    console.debug('Using fallback release info:', err);
  }
}

/* ==========================================================================
   3. High-Contrast Interactive App Screen Previews
   ========================================================================== */

const APP_SCREENS = {
  player: `
    <div class="h-full flex flex-col justify-between p-4 text-left relative z-20">
      <div class="flex items-center justify-between text-xs text-slate-400">
        <span class="text-white font-semibold flex items-center gap-1.5">
          <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse shadow-[0_0_8px_#34d399]"></span> Now Playing
        </span>
        <span class="text-[10px] font-mono bg-white/5 border border-white/10 px-2 py-0.5 rounded text-slate-300">320kbps Opus</span>
      </div>

      <div class="my-auto py-2">
        <div class="w-full aspect-square rounded-2xl overflow-hidden bg-black shadow-2xl border border-white/10 relative group">
          <img src="assets/Fairy-Music.png" alt="Cover" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105">
          <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-transparent to-transparent"></div>
        </div>

        <div class="mt-4 flex items-center justify-between">
          <div class="truncate pr-2">
            <h4 class="text-base font-bold text-white truncate">Starboy</h4>
            <p class="text-xs text-slate-400 mt-0.5">The Weeknd, Daft Punk</p>
          </div>
          <button class="text-slate-300 hover:text-white p-1 transition-transform active:scale-90" aria-label="Favorite">
            <svg class="w-5 h-5 fill-current" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>
          </button>
        </div>
      </div>

      <div class="space-y-3">
        <div>
          <div class="h-1.5 w-full bg-[#181a24] rounded-full overflow-hidden border border-white/5">
            <div class="h-full bg-white w-[42%] shadow-[0_0_10px_white]"></div>
          </div>
          <div class="flex justify-between text-[10px] font-mono text-slate-400 mt-1">
            <span>1:38</span>
            <span>3:50</span>
          </div>
        </div>

        <div class="flex items-center justify-between px-2 pt-1">
          <button class="text-slate-500 hover:text-white transition-colors"><i data-lucide="shuffle" class="w-4 h-4"></i></button>
          <button class="text-slate-300 hover:text-white transition-colors"><i data-lucide="skip-back" class="w-5 h-5"></i></button>
          <button class="w-11 h-11 rounded-full bg-white text-black flex items-center justify-center hover:bg-slate-200 transition-all shadow-[0_0_16px_rgba(255,255,255,0.4)] hover:scale-105 active:scale-95">
            <i data-lucide="pause" class="w-5 h-5 fill-current text-black"></i>
          </button>
          <button class="text-slate-300 hover:text-white transition-colors"><i data-lucide="skip-forward" class="w-5 h-5"></i></button>
          <button class="text-slate-500 hover:text-white transition-colors"><i data-lucide="repeat" class="w-4 h-4"></i></button>
        </div>
      </div>
    </div>
  `,

  lyrics: `
    <div class="h-full flex flex-col justify-between p-4 text-left relative z-20">
      <div class="flex items-center justify-between text-xs text-slate-400 pb-2 border-b border-white/10">
        <span class="font-semibold text-white">Synced Lyrics</span>
        <span class="text-[10px] font-mono text-slate-300 bg-white/5 px-2 py-0.5 rounded border border-white/10">BetterLyrics Engine</span>
      </div>

      <div class="my-auto space-y-3 py-3 text-sm font-medium">
        <p class="text-slate-600 transition-colors">I'm tryna put you in the worst mood, ah</p>
        <p class="text-slate-500 transition-colors">P1 cleaner than your church shoes, ah</p>
        <div class="p-3 rounded-xl bg-white/10 border border-white/20 text-white font-bold text-base shadow-[0_0_20px_rgba(255,255,255,0.1)]">
          <p class="text-white">Milli point two just to hurt you, ah</p>
          <p class="text-[11px] font-normal text-slate-300 mt-1">Word-by-word active timing</p>
        </div>
        <p class="text-slate-400 transition-colors">All red Lamb' just to tease you, ah</p>
        <p class="text-slate-600 transition-colors">None of these toys on lease too, ah</p>
      </div>

      <div class="pt-2 border-t border-white/10 flex items-center justify-between text-[11px] text-slate-400 font-mono">
        <span>Provider: LRCLIB</span>
        <span class="text-emerald-400">Sync: Locked</span>
      </div>
    </div>
  `,

  together: `
    <div class="h-full flex flex-col justify-between p-4 text-left relative z-20">
      <div class="flex items-center justify-between text-xs pb-3 border-b border-white/10">
        <div class="flex items-center gap-1.5">
          <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse shadow-[0_0_8px_#34d399]"></span>
          <span class="font-mono font-bold text-white text-sm">ROOM #82F9</span>
        </div>
        <span class="text-[10px] bg-emerald-950/70 text-emerald-300 border border-emerald-700/50 px-2 py-0.5 rounded font-mono">0 ms drift</span>
      </div>

      <div class="my-auto space-y-2 py-2">
        <div class="text-xs text-slate-400 font-medium">Connected Listeners (3)</div>

        <div class="p-2 rounded-xl bg-white/5 border border-white/10 flex items-center justify-between">
          <div class="flex items-center gap-2">
            <div class="w-7 h-7 rounded-full bg-white text-black flex items-center justify-center text-xs font-bold">B</div>
            <div>
              <div class="text-xs font-semibold text-white">Benny <span class="text-[9px] text-slate-400 font-mono">(Host)</span></div>
              <div class="text-[10px] text-emerald-400 font-mono">0 ms drift</div>
            </div>
          </div>
          <span class="text-[10px] text-slate-300 font-mono">Controlling</span>
        </div>

        <div class="p-2 rounded-xl bg-white/5 border border-white/10 flex items-center justify-between">
          <div class="flex items-center gap-2">
            <div class="w-7 h-7 rounded-full bg-slate-700 flex items-center justify-center text-xs font-bold text-white">K</div>
            <div>
              <div class="text-xs font-semibold text-white">Kiran</div>
              <div class="text-[10px] text-emerald-400 font-mono">+6 ms drift</div>
            </div>
          </div>
          <span class="text-[10px] text-slate-400 font-mono">Listener</span>
        </div>

        <div class="p-2 rounded-xl bg-white/5 border border-white/10 flex items-center justify-between">
          <div class="flex items-center gap-2">
            <div class="w-7 h-7 rounded-full bg-slate-700 flex items-center justify-center text-xs font-bold text-white">S</div>
            <div>
              <div class="text-xs font-semibold text-white">Sam</div>
              <div class="text-[10px] text-emerald-400 font-mono">-2 ms drift</div>
            </div>
          </div>
          <span class="text-[10px] text-slate-400 font-mono">Listener</span>
        </div>
      </div>

      <div class="p-2 rounded-xl bg-white/5 border border-white/10 text-[11px] text-slate-300">
        <span class="font-semibold text-white">Anti-Drift Math:</span> Micro-pitch adjustments keep devices locked in phase.
      </div>
    </div>
  `,

  equalizer: `
    <div class="h-full flex flex-col justify-between p-4 text-left relative z-20">
      <div class="flex items-center justify-between text-xs pb-2 border-b border-white/10">
        <span class="font-semibold text-white">10-Band Equalizer</span>
        <span class="text-[10px] text-emerald-400 font-mono">DSP Active</span>
      </div>

      <div class="my-auto space-y-3 py-2">
        <div class="flex items-center justify-between">
          <span class="text-xs text-slate-300">Bass Boost</span>
          <span class="text-xs font-mono text-white font-semibold">+6 dB</span>
        </div>
        <input type="range" class="slider-clean w-full" min="0" max="10" value="6">

        <div class="flex items-center justify-between pt-1">
          <span class="text-xs text-slate-300">Stereo Widener</span>
          <span class="text-xs font-mono text-white font-semibold">120%</span>
        </div>
        <input type="range" class="slider-clean w-full" min="0" max="10" value="7">

        <div class="flex items-center justify-between pt-1">
          <span class="text-xs text-slate-300">Soundstage</span>
          <span class="text-xs font-mono text-slate-400">Deep Space</span>
        </div>
      </div>

      <div class="grid grid-cols-2 gap-2 text-center text-xs">
        <button class="p-2 rounded-lg bg-white/5 text-slate-300 hover:bg-white/10 border border-white/10 active:scale-95 transition-all">Bypass</button>
        <button class="p-2 rounded-lg bg-white text-black font-semibold hover:bg-slate-200 active:scale-95 transition-all shadow-[0_0_12px_rgba(255,255,255,0.3)]">Save</button>
      </div>
    </div>
  `,

  spotify: `
    <div class="h-full flex flex-col justify-between p-4 text-left relative z-20">
      <div class="flex items-center justify-between text-xs pb-2 border-b border-white/10">
        <span class="font-semibold text-white">Import from Spotify</span>
        <span class="text-[10px] text-emerald-400 font-mono">1-Tap Sync</span>
      </div>

      <div class="my-auto space-y-3 py-2">
        <div class="space-y-1">
          <label class="text-[11px] text-slate-400">Paste Spotify Playlist URL</label>
          <div class="p-2 rounded-xl bg-black border border-white/10 text-xs text-slate-300 font-mono truncate">
            https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M
          </div>
        </div>

        <div class="p-3 rounded-xl bg-white/5 border border-white/10">
          <div class="text-xs font-semibold text-white">Found 50 Songs</div>
          <p class="text-[11px] text-slate-400 mt-0.5">Matched to high-bitrate YouTube Music streams without ads.</p>
        </div>
      </div>

      <button class="w-full py-2.5 rounded-xl bg-white hover:bg-slate-200 text-black font-semibold text-xs flex items-center justify-center gap-1.5 transition-all active:scale-95 shadow-[0_0_15px_rgba(255,255,255,0.35)]">
        <span>Import into Fairy Music</span>
      </button>
    </div>
  `
};

function setupScreenTabs() {
  const container = document.getElementById('mockup-screen-content');
  const buttons = document.querySelectorAll('.screen-tab-btn');
  if (!container || !buttons.length) return;

  function loadScreen(screenKey) {
    container.innerHTML = APP_SCREENS[screenKey] || APP_SCREENS.player;
    if (window.lucide) window.lucide.createIcons();
  }

  buttons.forEach(btn => {
    btn.addEventListener('click', () => {
      buttons.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      const key = btn.getAttribute('data-screen');
      loadScreen(key);
    });
  });

  // Default initial load
  loadScreen('player');
}

/* ==========================================================================
   5. Interactive Spotlight Glow on Obsidian Cards
   ========================================================================== */

function setupCardSpotlight() {
  const cards = document.querySelectorAll('.card-obsidian');
  if (!cards.length || window.innerWidth < 768) return;

  cards.forEach(card => {
    card.addEventListener('mousemove', (e) => {
      const rect = card.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;
      card.style.setProperty('--mouse-x', `${x}px`);
      card.style.setProperty('--mouse-y', `${y}px`);
    });
  });
}

/* ==========================================================================
   6. Copy Buttons with Clean Toast Notification
   ========================================================================== */

function setupCopyButtons() {
  const toast = document.getElementById('toast');
  const toastText = document.getElementById('toast-msg');

  function showToast(msg) {
    if (!toast) return;
    if (toastText) toastText.textContent = msg;
    toast.classList.add('show');
    setTimeout(() => {
      toast.classList.remove('show');
    }, 2400);
  }

  document.querySelectorAll('[data-copy]').forEach(btn => {
    btn.addEventListener('click', () => {
      const textToCopy = btn.getAttribute('data-copy');
      navigator.clipboard.writeText(textToCopy).then(() => {
        showToast('Copied to clipboard!');
        // Micro feedback on button itself
        const origText = btn.innerHTML;
        btn.classList.add('bg-white/20');
        setTimeout(() => btn.classList.remove('bg-white/20'), 300);
      }).catch(() => {
        showToast('Unable to copy');
      });
    });
  });
}

/* ==========================================================================
   8. Interactive Equalizer Playground Sliders
   ========================================================================== */

function setupEqSliders() {
  const sliders = document.querySelectorAll('.demo-eq-slider');
  sliders.forEach(slider => {
    const valDisplay = slider.parentElement?.querySelector('.eq-val');
    slider.addEventListener('input', () => {
      const v = parseInt(slider.value, 10);
      if (valDisplay) {
        valDisplay.textContent = v > 0 ? `+${v} dB` : `${v} dB`;
      }
    });
  });
}

/* ==========================================================================
   9. FAQ Accordion
   ========================================================================== */

function setupFaqAccordion() {
  const items = document.querySelectorAll('.faq-item');
  items.forEach(item => {
    const trigger = item.querySelector('.faq-trigger');
    const content = item.querySelector('.faq-content');
    const icon = item.querySelector('.faq-icon');

    if (!trigger || !content) return;

    trigger.addEventListener('click', () => {
      const isOpen = !content.classList.contains('hidden');

      items.forEach(other => {
        other.querySelector('.faq-content')?.classList.add('hidden');
        other.querySelector('.faq-icon')?.classList.remove('rotate-180');
      });

      if (!isOpen) {
        content.classList.remove('hidden');
        icon?.classList.add('rotate-180');
      }
    });
  });
}

/* ==========================================================================
   10. Mobile Navigation Menu with Backdrop Blur
   ========================================================================== */

function setupMobileMenu() {
  const btn = document.getElementById('mobile-menu-btn');
  const menu = document.getElementById('mobile-menu');
  if (!btn || !menu) return;

  btn.addEventListener('click', (e) => {
    e.stopPropagation();
    menu.classList.toggle('hidden');
  });

  // Close when tapping any navigation item
  menu.querySelectorAll('a').forEach(link => {
    link.addEventListener('click', () => {
      menu.classList.add('hidden');
    });
  });

  // Close when tapping outside
  document.addEventListener('click', (e) => {
    if (!menu.contains(e.target) && !btn.contains(e.target)) {
      menu.classList.add('hidden');
    }
  });
}

/* ==========================================================================
   9. Ambient Video Background & Audio Controls
   ========================================================================== */

function setupBackgroundVideo() {
  const video = document.getElementById('bg-video');
  const toggleBtn = document.getElementById('bg-audio-toggle');
  if (!video || !toggleBtn) return;

  // Guarantee seamless background video autoplay
  video.muted = true;
  const playPromise = video.play();
  if (playPromise !== undefined) {
    playPromise.catch(() => {
      document.body.addEventListener('click', () => {
        if (video.paused) video.play();
      }, { once: true });
    });
  }

  // Toggle ambient audio on user click
  toggleBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    if (video.muted) {
      video.muted = false;
      video.volume = 0.6;
      if (video.paused) video.play();
      toggleBtn.innerHTML = `
        <i data-lucide="volume-2" class="w-3.5 h-3.5 text-cyan-400"></i>
        <span>Sound Active</span>
      `;
    } else {
      video.muted = true;
      toggleBtn.innerHTML = `
        <i data-lucide="volume-x" class="w-3.5 h-3.5 text-slate-300"></i>
        <span>Sound Muted</span>
      `;
    }
    if (window.lucide) window.lucide.createIcons();
  });
}
