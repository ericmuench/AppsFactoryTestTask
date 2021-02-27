package de.ericmuench.appsfactorytesttask.util.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

/**
 * This file contains helpful extensions for Android Components to reduce code in Activities,
 * Fragments and other Components.
 */

//region Context Extensions
val Context.connectivityManager : ConnectivityManager?
get() = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
//endregion

//region Activity extensions
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
//endregion


//region Fragment Extensions
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
//endregion

//region Concrete UI Components Extensions
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
//endregion

//region LiveData Extensions
/**
 * This extension function can return a distinct LiveData-Value to avoid false-positive Refresh-
 * Actions. This code was coped from the following tutorial:
 *
 * https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1
 *
 * */
fun <T> LiveData<T>.getDistinct(): LiveData<T> {
    val distinctLiveData = MediatorLiveData<T>()
    distinctLiveData.addSource(this, object : Observer<T> {
        private var initialized = false
        private var lastObj: T? = null
        override fun onChanged(obj: T?) {
            if (!initialized) {
                initialized = true
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            } else if ((obj == null && lastObj != null)
                || obj != lastObj) {
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            }
        }
    })
    return distinctLiveData
}
//endregion
