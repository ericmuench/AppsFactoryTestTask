package de.ericmuench.appsfactorytesttask.util.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
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
 * This function can switch the View to another Activity by starting a new Intent. It is an
 * alternative to the other switchToActivity-Function using a class parameter.
 * @param config A anonymous extension function to config the Intent that is used to switch the Activity
 * */
inline fun <reified T : Activity> Activity.switchToActivity(config: Intent.() -> Unit = {}){
    val intent = Intent(this,T::class.java)
    config(intent)
    startActivity(intent)
}

/**
 * This function can switch the View to another Activity requesting for a result. It also uses an
 * Intent for switching the view.
 *
 * @param requestCode The Request-Code for an Activity-Change with Result
 * @param optionsBundle An Options-Bundle that can be used normally when using startActivityForResult
 * @param config A anonymous extension function to config the Intent that is used to switch the Activity
 *
 * */
inline fun <reified T : Activity> Activity.switchToActivityForResult(
        requestCode: Int,
        optionsBundle: Bundle? = null,
        config: Intent.() -> Unit = {}
){
    val intent = Intent(this,T::class.java)
    config(intent)
    startActivityForResult(intent,requestCode,optionsBundle)
}

/**
 * This function can be used to finish an Activity with a certain result data and a result Code.
 *
 * @param resultCode Whether the result is ok or cancelled
 * @param modifyIntent An extension function to config the Intent and put Result-Data in it
 */
fun Activity.finishWithResultData(resultCode: Int,modifyIntent : Intent.()->Unit){
    val resultIntent = Intent()
    modifyIntent.invoke(resultIntent)
    setResult(resultCode, resultIntent)
    finish()
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


/**
 * This function can switch the View to another Activity by starting a new Intent. It is an
 * alternative to the other switchToActivity-Function using a class parameter.
 * @param config A anonymous extension function to config the Intent that is used to switch the Activity
 * */
inline fun <reified T : Activity> Fragment.switchToActivity(config: Intent.() -> Unit = {}){
    val cntxt = context
    if(cntxt != null){
        val intent = Intent(cntxt,T::class.java)
        config(intent)
        startActivity(intent)
    }
}

/**
 * This function can switch the View to another Activity requesting for a result. It also uses an
 * Intent for switching the view.
 *
 * @param requestCode The Request-Code for an Activity-Change with Result
 * @param optionsBundle An Options-Bundle that can be used normally when using startActivityForResult
 * @param config A anonymous extension function to config the Intent that is used to switch the Activity
 *
 * */
inline fun <reified T : Activity> Fragment.switchToActivityForResult(
        requestCode: Int,
        optionsBundle: Bundle? = null,
        config: Intent.() -> Unit = {}
){
    val cntxt = context
    if(cntxt != null){
        val intent = Intent(cntxt,T::class.java)
        config(intent)
        startActivityForResult(intent,requestCode,optionsBundle)
    }

}

//Concrete UI Components Extensions
/**
 * This extension function makes the small line at the Bottom of the Edit-Text-Field of
 * a SearchView transparent.
 */
fun SearchView.removeSearchPlate() {
    val searchPlate : View = this.findViewById(androidx.appcompat.R.id.search_plate)
    searchPlate.setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent))
}

/**
 * This function calculates the last possible index for an element in the datasource of an
 * adapter of a RecyclerView.
 * */
fun <VH : RecyclerView.ViewHolder> RecyclerView.Adapter<VH>.lastItemIndex() : Int = itemCount - 1