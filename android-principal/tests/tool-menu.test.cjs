const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const { JSDOM } = require('jsdom');
const root = path.resolve(__dirname, '../..');
const dom = new JSDOM(fs.readFileSync(path.join(root, 'index.html'), 'utf8'), {
  url: 'https://appassets.androidplatform.net/index.html', runScripts: 'outside-only'
});
const { window } = dom;
const dialog = window.document.querySelector('#toolIntro');
// JSDOM has no top-layer implementation. Keep its actual DOM events and focus.
dialog.showModal = () => { dialog.open = true; };
dialog.close = () => { dialog.open = false; dialog.dispatchEvent(new window.Event('close')); };
vm.runInContext(fs.readFileSync(path.join(root, 'tool-menu.js'), 'utf8'), dom.getInternalVMContext());
const cards = [...window.document.querySelectorAll('.grid > a.card')];
assert.equal(cards.length, 10);
for (const card of cards) {
  card.focus();
  const click = new window.MouseEvent('click', { bubbles: true, cancelable: true });
  assert.equal(card.dispatchEvent(click), false, 'first tap explains instead of navigating');
  assert.equal(dialog.open, true);
  assert.equal(window.document.querySelector('#introTitle').textContent, card.querySelector('h3').textContent);
  assert.equal(window.document.querySelector('#introContinue').href, card.href);
  const external=new URL(card.href).origin!==window.location.origin;
  assert.equal(window.document.querySelector('#introContinue').target,external?'_blank':'');
  if(external)assert.equal(window.document.querySelector('#introContinue').rel,'noopener noreferrer');
  assert.ok(window.document.querySelectorAll('#introFeatures li').length >= 3);
  assert.ok(window.document.body.classList.contains('intro-open'));
  dialog.querySelector('.intro-cancel').click();
  assert.equal(dialog.open, false);
  assert.equal(window.document.activeElement, card, 'focus returns to selected tool');
}
console.log('PASS: ten tools explain their functions, preserve destinations and restore focus.');
const library = new JSDOM(fs.readFileSync(path.join(root, 'libros.html'), 'utf8'), {runScripts:'outside-only'});
const script = fs.readFileSync(path.join(root, 'libros.js'), 'utf8');
vm.runInContext(script, library.getInternalVMContext());
assert.ok(library.window.document.querySelector('#openLibrary').hidden, 'web fallback does not offer a broken native button');
let opened = 0;
library.window.AndroidPrint = { openLibrary: () => { opened++; } };
vm.runInContext(script, library.getInternalVMContext());
assert.equal(opened, 1);
assert.equal(library.window.document.querySelector('#openLibrary').hidden, false);
library.window.document.querySelector('#openLibrary').click();
assert.equal(opened, 1, 'repeated taps during opening are ignored');
console.log('PASS: library opens through native bridge; website shows its Android requirement.');
