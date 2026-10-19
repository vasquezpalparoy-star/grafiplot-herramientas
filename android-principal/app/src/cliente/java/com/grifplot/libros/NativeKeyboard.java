package com.grifplot.libros;

import android.content.Context;
import android.text.InputType;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/** Uses the keyboard chosen by the user in Android settings. */
final class NativeKeyboard {
 static void bind(MainActivity activity, EditText field, Runnable accept, boolean numeric) {
  field.setShowSoftInputOnFocus(true);
  field.setSelectAllOnFocus(false);
  field.addTextChangedListener(new TextWatcher() {
   public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
   public void onTextChanged(CharSequence s, int start, int before, int count) { activity.touch(); }
   public void afterTextChanged(Editable text) {}
  });
  if (numeric && (field.getInputType() & InputType.TYPE_MASK_CLASS) != InputType.TYPE_CLASS_NUMBER)
   field.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
  field.setImeOptions(accept == null ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_SEARCH);
  field.setOnEditorActionListener((view, actionId, event) -> {
   if (actionId != EditorInfo.IME_ACTION_SEARCH && actionId != EditorInfo.IME_ACTION_DONE) return false;
   activity.touch();
   InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
   manager.hideSoftInputFromWindow(field.getWindowToken(), 0);
   if (accept != null) accept.run();
   return true;
  });
 }
 static void openOnEntry(MainActivity activity, EditText field, Runnable accept) {
  final int generation = activity.generation;
  field.postDelayed(() -> {
   if (generation != activity.generation || activity.isFinishing() || activity.isDestroyed()
       || !field.isAttachedToWindow() || !field.isShown()) return;
   field.requestFocus();
   InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
   manager.showSoftInput(field, InputMethodManager.SHOW_IMPLICIT);
   activity.touch();
  }, 200);
 }
}
