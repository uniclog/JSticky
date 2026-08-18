import './style.css';
import {Events, Window} from '@wailsio/runtime';
import {App} from '../bindings/JStickyGo';

const root = document.querySelector('#app');
const view = new URLSearchParams(location.search).get('view') || 'note';
let state;
let saveTimer;
let dirty = false;
let pinned = false;
let pointerInside = false;
let appliedOpacity;

const button = (id, icon, title) => `<button id="${id}" class="tool-button" title="${title}" aria-label="${title}"><img src="/icons/${icon}.png" alt=""></button>`;

async function applyTheme() {
  const {settings, windowSettings} = state;
  const style = document.documentElement.style;
  style.setProperty('--theme', settings.appThemeColor);
  style.setProperty('--theme-soft', mixWithWhite(settings.appThemeColor, .72));
  style.setProperty('--ink', settings.textFontColor);
  style.setProperty('--note-font', settings.textFontFamily === 'System' ? 'system-ui' : `'${settings.textFontFamily}'`);
  style.setProperty('--note-size', `${settings.textSize}px`);
  // The settings window stays fully visible, matching the JavaFX version.
  // Opacity and hover restoration apply to the note and screen capture only.
  const supportsOpacity = view !== 'settings';
  const hoverVisible = supportsOpacity && windowSettings.mouseHoverOpacity && pointerInside;
  await setVisualOpacity(supportsOpacity ? (hoverVisible ? 1 : windowSettings.opacity) : 1);
}

async function setVisualOpacity(opacity) {
  if (appliedOpacity === opacity) return;
  appliedOpacity = opacity;
  const windowName = view === 'note' ? 'main' : view;
  try {
    const nativeApplied = await App.SetWindowOpacity(windowName, opacity);
    // CSS remains only as a portable fallback. Windows uses HWND alpha to avoid
    // the grey WebView2 surface visible at very low opacity.
    document.documentElement.style.setProperty('--window-opacity', nativeApplied ? 1 : opacity);
  } catch (error) {
    appliedOpacity = undefined;
    throw error;
  }
}

function bindHoverOpacity(element) {
  element.addEventListener('mouseenter', () => {
    pointerInside = true;
    if (state.windowSettings.mouseHoverOpacity) setVisualOpacity(1);
  });
  element.addEventListener('mouseleave', () => {
    pointerInside = false;
    if (state.windowSettings.mouseHoverOpacity) setVisualOpacity(state.windowSettings.opacity);
  });
}

function mixWithWhite(hex, weight) {
  const n = Number.parseInt(hex.slice(1), 16);
  const channel = value => Math.round(value * weight + 255 * (1 - weight));
  return `rgb(${channel(n >> 16)}, ${channel((n >> 8) & 255)}, ${channel(n & 255)})`;
}

async function save() {
  clearTimeout(saveTimer);
  saveTimer = undefined;
  if (!state || !dirty) return;
  dirty = false;
  try {
    await App.SaveState(state);
  } catch (error) {
    dirty = true;
    throw error;
  }
}

function queueSave() {
  dirty = true;
  clearTimeout(saveTimer);
  saveTimer = setTimeout(save, 1000);
}

function toolbar(left, right, title = '') {
  return `<header class="toolbar"><div class="tool-group">${left}</div><div class="drag-region">${title ? `<span>${title}</span>` : ''}</div><div class="tool-group">${right}</div></header>`;
}

function renderNote() {
  root.innerHTML = `<main class="window note-window">
    ${toolbar(
      button('settings', 'settings', 'Настройки') + button('capture', 'capture', 'Фрагмент экрана'),
      button('pin', 'pin', 'Поверх остальных окон') + button('minimise', 'minimise', 'Свернуть') + button('close', 'close', 'Закрыть')
    )}
    <div class="note-surface"><textarea id="note" aria-label="Текст заметки"></textarea></div>
  </main>`;
  applyTheme();
  bindHoverOpacity(document.querySelector('.note-window'));
  const note = document.querySelector('#note');
  note.value = state.content || '';
  applyNoteSettings(note);
  requestAnimationFrame(() => { note.scrollTop = note.scrollHeight; });
  note.addEventListener('input', () => { state.content = note.value; queueSave(); });
  document.querySelector('#settings').onclick = () => App.OpenSettings();
  document.querySelector('#capture').onclick = () => App.OpenCapture().catch(console.error);
  document.querySelector('#minimise').onclick = () => App.Minimise();
  document.querySelector('#close').onclick = async () => { await save(); App.Quit(); };
  document.querySelector('#pin').onclick = () => {
    pinned = !pinned;
    document.querySelector('#pin').classList.toggle('active', pinned);
    App.SetAlwaysOnTop(pinned);
  };
  Events.On('state-changed', event => {
    state = event.data;
    applyTheme();
    applyNoteSettings(note);
  });
}

function applyNoteSettings(note) {
  note.wrap = state.settings.textWrap ? 'soft' : 'off';
  note.spellcheck = state.settings.spellCheck;
}

function renderSettings() {
  const fonts = ['System', 'Segoe UI', 'Arial', 'Tahoma', 'Times New Roman', 'Georgia', 'Inter'];
  root.innerHTML = `<main class="window settings-window">
    ${toolbar('', button('close', 'close', 'Закрыть'), 'Настройки')}
    <section class="settings-content">
      <label class="field"><span>Прозрачность <output id="opacityValue"></output></span><input id="opacity" type="range" min="0.004" max="1" step="0.001"></label>
      <label class="check"><input id="hoverOpacity" type="checkbox"><span>Полная видимость при наведении</span></label>
      <label class="field"><span>Размер шрифта</span><input id="fontSize" type="number" min="8" max="72"></label>
      <label class="field"><span>Семейство шрифта</span><select id="fontFamily">${fonts.map(font => `<option>${font}</option>`).join('')}</select></label>
      <label class="check"><input id="spellCheck" type="checkbox"><span>Проверка орфографии</span></label>
      <label class="check"><input id="textWrap" type="checkbox"><span>Перенос строк</span></label>
      <div class="colour-fields">
        <label class="colour-field"><span>Цвет заметки</span><input id="themeColor" type="color"></label>
        <label class="colour-field"><span>Цвет текста</span><input id="fontColor" type="color"></label>
      </div>
      <textarea class="sample" readonly>Пример текста заметки…</textarea>
      <footer>JStickyGo · Wails 3<br>2.0.2</footer>
    </section>
  </main>`;
  applyTheme();
  fillSettings();
  bindSettings();
  document.querySelector('#close').onclick = async () => { await save(); Window.Close(); };
}

function fillSettings() {
  const s = state.settings, w = state.windowSettings;
  document.querySelector('#fontSize').value = s.textSize;
  document.querySelector('#fontFamily').value = s.textFontFamily;
  document.querySelector('#spellCheck').checked = s.spellCheck;
  document.querySelector('#textWrap').checked = s.textWrap;
  document.querySelector('#themeColor').value = s.appThemeColor;
  document.querySelector('#fontColor').value = s.textFontColor;
  document.querySelector('#opacity').value = w.opacity;
  document.querySelector('#opacityValue').textContent = formatOpacity(w.opacity);
  document.querySelector('#hoverOpacity').checked = w.mouseHoverOpacity;
}

function bindSettings() {
  const set = (id, object, key, read = element => element.value) => {
    const element = document.querySelector(`#${id}`);
    element.addEventListener('input', () => {
      state[object][key] = read(element);
      applyTheme();
      if (id === 'opacity') document.querySelector('#opacityValue').textContent = formatOpacity(Number(element.value));
      queueSave();
    });
  };
  set('fontSize', 'settings', 'textSize', el => Number(el.value));
  set('fontFamily', 'settings', 'textFontFamily');
  set('spellCheck', 'settings', 'spellCheck', el => el.checked);
  set('textWrap', 'settings', 'textWrap', el => el.checked);
  set('themeColor', 'settings', 'appThemeColor');
  set('fontColor', 'settings', 'textFontColor');
  set('opacity', 'windowSettings', 'opacity', el => Number(el.value));
  set('hoverOpacity', 'windowSettings', 'mouseHoverOpacity', el => el.checked);
}

function formatOpacity(value) {
  const percent = value * 100;
  return `${percent < 1 ? percent.toFixed(1) : Math.round(percent)}%`;
}

async function renderCapture() {
  root.innerHTML = `<main class="window capture-window">
    ${toolbar(button('copy', 'copy', 'Копировать фрагмент'), button('lock', 'capture', 'Заморозить') + button('pin', 'pin', 'Поверх остальных окон') + button('close', 'close', 'Закрыть'))}
    <div class="capture-viewport"><div class="capture-status">Получение изображения…</div></div>
  </main>`;
  applyTheme();
  bindHoverOpacity(document.querySelector('.capture-window'));
  const capture = await App.GetCapture();
  const viewport = document.querySelector('.capture-viewport');
  viewport.innerHTML = `<img id="captureImage" src="${capture.dataURL}" alt="Фрагмент экрана">`;
  let locked = false;
  let onTop = false;
  const updatePosition = async () => {
    if (locked) return;
    const image = document.querySelector('#captureImage');
    if (!image) return;
    const position = await Window.Position();
    image.style.left = `${-(position.x - capture.x) - 2}px`;
    image.style.top = `${-(position.y - capture.y) - 27}px`;
    requestAnimationFrame(updatePosition);
  };
  document.querySelector('#lock').onclick = () => {
    locked = !locked;
    document.querySelector('#lock').classList.toggle('active', locked);
    if (!locked) updatePosition();
  };
  document.querySelector('#pin').onclick = () => {
    onTop = !onTop;
    document.querySelector('#pin').classList.toggle('active', onTop);
    Window.SetAlwaysOnTop(onTop);
  };
  document.querySelector('#copy').onclick = async () => {
    try { await navigator.clipboard.write([new ClipboardItem({'image/png': await captureBlob(capture)})]); }
    catch (error) { console.error('Не удалось скопировать изображение', error); }
  };
  document.querySelector('#close').onclick = () => Window.Close();
  updatePosition();
}

async function captureBlob(capture) {
  const viewport = document.querySelector('.capture-viewport');
  const rect = viewport.getBoundingClientRect();
  const position = await Window.Position();
  const image = new Image();
  image.src = capture.dataURL;
  await image.decode();
  const canvas = document.createElement('canvas');
  canvas.width = Math.round(rect.width); canvas.height = Math.round(rect.height);
  canvas.getContext('2d').drawImage(image,
    position.x + rect.left - capture.x, position.y + rect.top - capture.y,
    canvas.width, canvas.height, 0, 0, canvas.width, canvas.height);
  return new Promise(resolve => canvas.toBlob(resolve, 'image/png'));
}

App.GetState().then(value => {
  state = value;
  if (view === 'settings') renderSettings();
  else if (view === 'capture') renderCapture().catch(console.error);
  else renderNote();
});
