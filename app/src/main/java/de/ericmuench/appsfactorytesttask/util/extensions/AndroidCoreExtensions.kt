package de.ericmuench.appsfactorytesttask.util.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

/**
 * This file contains helpful extensions for Android Components to reduce code e.g. in Activities.
 */

//Activity extensions
/**
 * @return whether a Activity runs in portrait-mode
 */
fun Activity.runsInPortrait() : Boolean = runsInOrientation(Configuration.ORIENTATION_PORTRAIT)

/**
 * @return whether a Activity runs in landscape-mode
 */
fun Activity.runsInLandscape() : Boolean = runsInOrientation(Configuration.ORIENTATION_LANDSCAPE)

/**
 * This function checks whether a Activity runs in the given orientation
 * @param orientation The orientation to be checked
 * @return whether the Activity runs in the specified orientation
 */
fun Activity.runsInOrientation(orientation: Int) : Boolean{
    val currentOrientation = this.resources.configuration.orientation
    return currentOrientation == orientation
}

/**
 * This function can switch the View to another Activity by starting a new Intent.
 * @param clazz The Type of Activity to switch to
 * @param config A anonymous extension function to config the Intent that is used to switch the Activity
 * */
fun <T : Any> Activity.switchToActivity(clazz : KClass<T>, config: Intent.() -> Unit = {}){
    val intent = Intent(this,clazz.java)
    config(intent)
    startActivity(intent)
}

/**
 * This function can hide the Keyboard.
 */
fun Activity.hideKeyboard(){
    currentFocus.notNull { view ->
        this.getSystemService(Context.INPUT_METHOD_SERVICE).castedAs<InputMethodManager> {
            it.hideSoftInputFromWindow(view.windowToken,0)
        }
    }
}

//Fragment Extensions
/**
 * This function can hide the Keyboard used by the fragment by calling the hideKeyboard-Function
 * of the underlying Activity.
 */
fun Fragment.hideKeyboard() = activity?.hideKeyboard()


//Concrete UI Components Extensions
/**
 * This extension function makes the small line at the Bottom of the Edit-Text-Field of
 * a SearchView transparent.
 */
fun SearchView.removeSearchPlate() {
    val searchPlate : View = this.findViewById(androidx.appcompat.R.id.search_plate)
    searchPlate.setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent))
}

