package com.raaveinm.keymaptest.keybinding

import androidx.compose.ui.input.key.Key
import keymaptest.shared.generated.resources.*
import org.jetbrains.compose.resources.StringResource

///////////////////////////////////////////////
/// Actions
///////////////////////////////////////////////

/**
 * Everything a shortcut can trigger. Purely a list of identifiers + labels: the actions don't know
 * which key they are bound to (that is [KeyMap]'s job), so rebinding never touches this file.
 */
enum class AppAction(
    val title: StringResource,
    val description: StringResource,
) {
    NewFile(Res.string.action_new_file_title, Res.string.action_new_file_desc),
    OpenFile(Res.string.action_open_file_title, Res.string.action_open_file_desc),
    Save(Res.string.action_save_title, Res.string.action_save_desc),
    SaveAll(Res.string.action_save_all_title, Res.string.action_save_all_desc),
    Undo(Res.string.action_undo_title, Res.string.action_undo_desc),
    Redo(Res.string.action_redo_title, Res.string.action_redo_desc),
    Find(Res.string.action_find_title, Res.string.action_find_desc),
    CommandPalette(Res.string.action_command_palette_title, Res.string.action_command_palette_desc),
    ToggleTheme(Res.string.action_toggle_theme_title, Res.string.action_toggle_theme_desc),
    Help(Res.string.action_help_title, Res.string.action_help_desc),
}

///////////////////////////////////////////////
/// Defaults
///////////////////////////////////////////////

/** Factory bindings; "primary" shortcuts adapt to the platform (Cmd on macOS, Ctrl elsewhere). */
fun defaultBindings(isMac: Boolean): Map<AppAction, KeyChord> = mapOf(
    AppAction.NewFile to KeyChord.primary(Key.N, isMac),
    AppAction.OpenFile to KeyChord.primary(Key.O, isMac),
    AppAction.Save to KeyChord.primary(Key.S, isMac),
    AppAction.SaveAll to KeyChord.primary(Key.S, isMac, shift = true),
    AppAction.Undo to KeyChord.primary(Key.Z, isMac),
    AppAction.Redo to KeyChord.primary(Key.Z, isMac, shift = true),
    AppAction.Find to KeyChord.primary(Key.F, isMac),
    AppAction.CommandPalette to KeyChord.primary(Key.P, isMac, shift = true),
    AppAction.ToggleTheme to KeyChord.primary(Key.D, isMac),
    AppAction.Help to KeyChord(Key.F1),
)
