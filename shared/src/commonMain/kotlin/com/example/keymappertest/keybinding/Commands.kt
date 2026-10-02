package com.example.keymappertest.keybinding

//
// Created by Kirill "Raaveinm" on 9/28/26.
//

/**
 * Overall commands which could be triggered
 *
 * Applicable context defined in
 * @see Context
 *
 */

enum class Commands(val title: String) {
    REFRESH("refresh"),             // Refresh/update action
    ADD_NEW_ELEM("new_elem")        // Element insertion (ex: new server, new game in queue)
}