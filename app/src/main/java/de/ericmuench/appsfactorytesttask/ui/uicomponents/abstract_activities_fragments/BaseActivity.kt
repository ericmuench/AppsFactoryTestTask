package de.ericmuench.appsfactorytesttask.ui.uicomponents.abstract_activities_fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import de.ericmuench.appsfactorytesttask.R
import de.ericmuench.appsfactorytesttask.app.AppsFactoryTestTaskApplication
import de.ericmuench.appsfactorytesttask.util.connectivity.InternetConnectivityChecker
import de.ericmuench.appsfactorytesttask.util.extensions.notNull

/**
 * This class defines basic logic for activities as well as own "Events"
 *
 * */
abstract class BaseActivity : AppCompatActivity() {

    //region Fields
    val internetConnectivityChecker : InternetConnectivityChecker
    get(){
        val app = application as AppsFactoryTestTaskApplication
        return app.internetConnectivityChecker
    }
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(internetConnectivityChecker)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(internetConnectivityChecker)
    }

    //region lifecycle functions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                onActionbarBackButtonPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    //endregion

    //region own event functions for subclasses
     protected open fun onActionbarBackButtonPressed() = finish()
    //endregion

    //region Own Utility functions
    /**
     * This function handles an Error by showing Feedback in the UI for the User via an AlertDialog.
     * It can be called manually every time an error occurs
     * @param error A Throwable-Object which usually contains an Exception
     * */
    protected fun handleError(error: Throwable){
        val errorMsg = resources
            .getString(R.string.error_template_try_again)
            .replace("#","\n\n${error.localizedMessage}\n\n")

        AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(errorMsg)
            .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
        error.printStackTrace()
    }

    /**
     * This function can open an Url to a specific Website using an Intent.
     * @param link The URL to open in a Browser
     * */
    protected fun openWebUrl(link : String){
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(link)
        }
        startActivity(intent)
    }
    //endregion
}